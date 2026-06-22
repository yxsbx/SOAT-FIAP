[CmdletBinding()]
param(
    [string]$BaseUrl = "http://localhost:5173",
    [string]$Username = "admin@autocarehub.com",
    [string]$Password = "autocare123",
    [int]$TimeoutSeconds = 120
)

$ErrorActionPreference = "Stop"

function Wait-ForEndpoint {
    param([string]$Uri)

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    do {
        try {
            $response = Invoke-WebRequest $Uri -UseBasicParsing
            if ($response.StatusCode -eq 200) {
                return $response
            }
        }
        catch {
            Start-Sleep -Seconds 3
        }
    } while ((Get-Date) -lt $deadline)

    throw "Endpoint did not become ready: $Uri"
}

$frontend = Wait-ForEndpoint "$BaseUrl/"
$openApi = Wait-ForEndpoint "$BaseUrl/v3/api-docs"
$login =
    Invoke-RestMethod "$BaseUrl/api/v1/auth/login" `
        -Method Post `
        -ContentType "application/json" `
        -Body (@{ username = $Username; password = $Password } | ConvertTo-Json)

if (-not $login.accessToken) {
    throw "Login did not return an access token"
}

$headers = @{ Authorization = "Bearer $($login.accessToken)" }
$customers = Invoke-RestMethod "$BaseUrl/api/v1/customers" -Headers $headers
$services = Invoke-RestMethod "$BaseUrl/api/v1/workshop-services" -Headers $headers
$parts = Invoke-RestMethod "$BaseUrl/api/v1/parts" -Headers $headers

[pscustomobject]@{
    FrontendStatus = $frontend.StatusCode
    OpenApiStatus = $openApi.StatusCode
    Login = "OK"
    Customers = $customers.items.Count
    Services = $services.items.Count
    Parts = $parts.items.Count
} | Format-List
