[CmdletBinding()]
param(
    [switch]$Rebuild,
    [switch]$Reset,
    [switch]$FollowLogs
)

$ErrorActionPreference = "Stop"

$root = Resolve-Path (Join-Path $PSScriptRoot "..")
$composeFile = Join-Path $root "docker-compose.yml"
$envFile = Join-Path $root ".env"
$envExample = Join-Path $root ".env.example"

function Invoke-Compose {
    param([string[]]$Arguments)

    docker compose @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "docker compose falhou com código $LASTEXITCODE."
    }
}

if (-not (Test-Path $envFile)) {
    if (-not (Test-Path $envExample)) {
        throw "Arquivo de exemplo não encontrado: $envExample"
    }

    Copy-Item $envExample $envFile
    Write-Host "Arquivo local criado em .env. Revise as variáveis se quiser trocar portas ou senhas."
}

if ($Reset) {
    Write-Host "Reset solicitado: containers, rede e volume local do PostgreSQL serão recriados."
    Invoke-Compose @("--env-file", $envFile, "-f", $composeFile, "down", "--volumes", "--remove-orphans")
}

$composeArgs = @(
    "--env-file", $envFile,
    "-f", $composeFile,
    "up",
    "-d"
)

if ($Rebuild) {
    $composeArgs += "--build"
}

Invoke-Compose $composeArgs
Invoke-Compose @("--env-file", $envFile, "-f", $composeFile, "ps")

Write-Host ""
Write-Host "AutoCare Hub iniciado:"
Write-Host "- Frontend: http://localhost:5173"
Write-Host "- API: http://localhost:8080"
Write-Host "- Swagger: http://localhost:8080/swagger-ui.html"

if ($FollowLogs) {
    Invoke-Compose @("--env-file", $envFile, "-f", $composeFile, "logs", "-f")
}
