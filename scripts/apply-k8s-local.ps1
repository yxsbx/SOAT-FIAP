param(
    [string]$Namespace = "autocarehub",
    [string]$EnvFile = ".env",
    [switch]$SkipSecret,
    [switch]$Wait
)

$ErrorActionPreference = "Stop"

function Read-EnvFile {
    param([string]$Path)

    $values = @{}
    if (-not (Test-Path $Path)) {
        return $values
    }

    Get-Content $Path | ForEach-Object {
        $line = $_.Trim()
        if ($line -eq "" -or $line.StartsWith("#") -or -not $line.Contains("=")) {
            return
        }

        $parts = $line.Split("=", 2)
        $values[$parts[0].Trim()] = $parts[1].Trim().Trim('"').Trim("'")
    }

    return $values
}

function Get-ConfigValue {
    param(
        [hashtable]$Values,
        [string]$Name,
        [string]$Fallback
    )

    if ($Values.ContainsKey($Name) -and -not [string]::IsNullOrWhiteSpace($Values[$Name])) {
        return $Values[$Name]
    }

    $environmentValue = [Environment]::GetEnvironmentVariable($Name)
    if (-not [string]::IsNullOrWhiteSpace($environmentValue)) {
        return $environmentValue
    }

    return $Fallback
}

function Invoke-Kubectl {
    param([string[]]$Arguments)

    & kubectl @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "kubectl falhou com codigo $LASTEXITCODE."
    }
}

$repoRoot = Split-Path -Parent $PSScriptRoot
$k8sDir = Join-Path $repoRoot "k8s"
$envPath = Join-Path $repoRoot $EnvFile

if (-not (Get-Command kubectl -ErrorAction SilentlyContinue)) {
    throw "kubectl nao encontrado no PATH. Instale/configure o kubectl antes de aplicar os manifests."
}

$context = (& kubectl config current-context 2>$null)
if ($LASTEXITCODE -ne 0 -or [string]::IsNullOrWhiteSpace($context)) {
    throw "Nenhum contexto Kubernetes ativo. No Docker Desktop, habilite Kubernetes e rode: kubectl config use-context docker-desktop"
}

Write-Host "Contexto Kubernetes atual: $context"

& kubectl auth can-i get pods --namespace $Namespace *> $null
if ($LASTEXITCODE -ne 0) {
    throw "O contexto '$context' nao esta autenticado ou nao tem permissao. Verifique o cluster com: kubectl config get-contexts"
}

$envValues = Read-EnvFile $envPath
$postgresPassword = Get-ConfigValue $envValues "POSTGRES_PASSWORD" "autocarehub-local-postgres"
$dbPassword = Get-ConfigValue $envValues "DB_PASSWORD" $postgresPassword
$jwtSecret = Get-ConfigValue $envValues "JWT_SECRET" "autocarehub-local-jwt-secret-at-least-32-bytes"
$externalToken = Get-ConfigValue $envValues "EXTERNAL_SERVICE_TOKEN" "autocarehub-local-external-token"

Write-Host "Aplicando namespace e configuracoes base..."
Invoke-Kubectl @("apply", "-f", (Join-Path $k8sDir "namespace.yaml"))
Invoke-Kubectl @("apply", "-f", (Join-Path $k8sDir "configmap.yaml"))

if (-not $SkipSecret) {
    Write-Host "Criando/atualizando Secret real a partir de .env/variaveis locais..."
    $secretYaml = & kubectl -n $Namespace create secret generic autocarehub-secret `
        --from-literal=POSTGRES_PASSWORD=$postgresPassword `
        --from-literal=DB_PASSWORD=$dbPassword `
        --from-literal=JWT_SECRET=$jwtSecret `
        --from-literal=EXTERNAL_SERVICE_TOKEN=$externalToken `
        --dry-run=client -o yaml

    if ($LASTEXITCODE -ne 0) {
        throw "Nao foi possivel gerar o Secret local."
    }

    $secretYaml | & kubectl apply -f -
    if ($LASTEXITCODE -ne 0) {
        throw "Nao foi possivel aplicar o Secret local."
    }
}

$manifestFiles = @(
    "postgres-service.yaml",
    "postgres-deployment.yaml",
    "backend-service.yaml",
    "backend-deployment.yaml",
    "backend-hpa.yaml",
    "frontend-service.yaml",
    "frontend-deployment.yaml",
    "frontend-hpa.yaml"
)

foreach ($file in $manifestFiles) {
    Invoke-Kubectl @("apply", "-f", (Join-Path $k8sDir $file))
}

if ($Wait) {
    Write-Host "Aguardando rollouts..."
    Invoke-Kubectl @("rollout", "status", "-n", $Namespace, "deploy/autocarehub-postgres", "--timeout=180s")
    Invoke-Kubectl @("rollout", "status", "-n", $Namespace, "deploy/autocarehub-api", "--timeout=180s")
    Invoke-Kubectl @("rollout", "status", "-n", $Namespace, "deploy/autocarehub-web", "--timeout=180s")
}

Write-Host ""
Write-Host "Recursos Kubernetes:"
Invoke-Kubectl @("get", "pods,svc,hpa", "-n", $Namespace)
Write-Host ""
Write-Host "Para acessar localmente:"
Write-Host "kubectl port-forward -n $Namespace svc/backend 8080:8080"
Write-Host "kubectl port-forward -n $Namespace svc/autocarehub-web 5173:8080"
