param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$Username = "admin@autocarehub.com",
    [string]$Password = "autocare123",
    [int]$Requests = 100,
    [int]$Concurrency = 10,
    [switch]$GenerateBudget
)

$ErrorActionPreference = "Stop"

if ($Requests -lt 1) {
    throw "Requests deve ser maior que zero."
}

if ($Concurrency -lt 1) {
    throw "Concurrency deve ser maior que zero."
}

function Invoke-JsonRequest {
    param(
        [string]$Method,
        [string]$Uri,
        [hashtable]$Headers,
        [object]$Body
    )

    $parameters = @{
        Method      = $Method
        Uri         = $Uri
        ContentType = "application/json"
        Headers     = $Headers
    }

    if ($null -ne $Body) {
        $parameters.Body = ($Body | ConvertTo-Json -Depth 10)
    }

    Invoke-RestMethod @parameters
}

Write-Host "Autenticando em $BaseUrl..."
$login = Invoke-JsonRequest `
    -Method "POST" `
    -Uri "$BaseUrl/api/v1/auth/login" `
    -Headers @{} `
    -Body @{ username = $Username; password = $Password }

$token = $login.accessToken
if ([string]::IsNullOrWhiteSpace($token)) {
    throw "Login nao retornou accessToken."
}

$headers = @{ Authorization = "Bearer $token" }
$startedAt = Get-Date
$allResults = New-Object System.Collections.Generic.List[object]

Write-Host "Disparando $Requests requisicoes com concorrencia $Concurrency..."

for ($offset = 0; $offset -lt $Requests; $offset += $Concurrency) {
    $batchSize = [Math]::Min($Concurrency, $Requests - $offset)
    $jobs = @()

    for ($index = 0; $index -lt $batchSize; $index++) {
        $requestNumber = $offset + $index + 1
        $jobs += Start-Job -ScriptBlock {
            param($BaseUrl, $Token, $RequestNumber, $GenerateBudget)

            $payload = @{
                customerDocument = "12345678909"
                vehicleId        = "20000000-0000-0000-0000-000000000001"
                diagnosticNotes  = "Carga automatizada Tech Challenge - OS $RequestNumber"
                services         = @(
                    @{
                        serviceId = "30000000-0000-0000-0000-000000000004"
                        quantity  = 1
                    }
                )
                generateBudget   = [bool]$GenerateBudget
            }

            $body = $payload | ConvertTo-Json -Depth 10
            $headers = @{ Authorization = "Bearer $Token" }
            $watch = [System.Diagnostics.Stopwatch]::StartNew()

            try {
                $response = Invoke-RestMethod `
                    -Method Post `
                    -Uri "$BaseUrl/api/v1/service-orders" `
                    -ContentType "application/json" `
                    -Headers $headers `
                    -Body $body

                $watch.Stop()
                [pscustomobject]@{
                    requestNumber = $RequestNumber
                    ok            = $true
                    status        = 201
                    elapsedMs     = $watch.ElapsedMilliseconds
                    id            = $response.id
                    orderStatus   = $response.status
                    error         = $null
                }
            } catch {
                $watch.Stop()
                $statusCode = $null
                if ($_.Exception.Response -and $_.Exception.Response.StatusCode) {
                    $statusCode = [int]$_.Exception.Response.StatusCode
                }

                [pscustomobject]@{
                    requestNumber = $RequestNumber
                    ok            = $false
                    status        = $statusCode
                    elapsedMs     = $watch.ElapsedMilliseconds
                    id            = $null
                    orderStatus   = $null
                    error         = $_.Exception.Message
                }
            }
        } -ArgumentList $BaseUrl, $token, $requestNumber, $GenerateBudget.IsPresent
    }

    Wait-Job $jobs | Out-Null
    $batchResults = Receive-Job $jobs
    Remove-Job $jobs

    foreach ($result in $batchResults) {
        $allResults.Add($result)
    }

    $done = [Math]::Min($offset + $batchSize, $Requests)
    $ok = ($allResults | Where-Object { $_.ok }).Count
    $failed = $allResults.Count - $ok
    Write-Host "Progresso: $done/$Requests | sucesso: $ok | falha: $failed"
}

$finishedAt = Get-Date
$successes = $allResults | Where-Object { $_.ok }
$failures = $allResults | Where-Object { -not $_.ok }
$elapsedSeconds = [Math]::Max(1, ($finishedAt - $startedAt).TotalSeconds)
$averageMs = if ($successes.Count -gt 0) {
    [Math]::Round(($successes | Measure-Object -Property elapsedMs -Average).Average, 2)
} else {
    0
}

Write-Host ""
Write-Host "Resumo da carga"
Write-Host "Requisicoes: $Requests"
Write-Host "Concorrencia: $Concurrency"
Write-Host "Sucessos: $($successes.Count)"
Write-Host "Falhas: $($failures.Count)"
Write-Host "Tempo total: $([Math]::Round(($finishedAt - $startedAt).TotalSeconds, 2))s"
Write-Host "Vazao aproximada: $([Math]::Round($Requests / $elapsedSeconds, 2)) req/s"
Write-Host "Tempo medio de sucesso: ${averageMs}ms"

if ($failures.Count -gt 0) {
    Write-Host ""
    Write-Host "Primeiras falhas:"
    $failures | Select-Object -First 5 requestNumber, status, error | Format-Table -AutoSize
}

Write-Host ""
Write-Host "Consultando fila operacional apos carga..."
$queue = Invoke-RestMethod -Method Get -Uri "$BaseUrl/api/v1/service-orders?page=0&size=10" -Headers $headers
$queue.items | Select-Object id, status, createdAt | Format-Table -AutoSize
