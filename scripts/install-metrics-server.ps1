param(
    [string]$ManifestUrl = "https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml",
    [switch]$SkipLocalTlsPatch
)

$ErrorActionPreference = "Stop"

function Invoke-Kubectl {
    param([string[]]$Arguments)

    & kubectl @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "kubectl falhou com codigo $LASTEXITCODE."
    }
}

if (-not (Get-Command kubectl -ErrorAction SilentlyContinue)) {
    throw "kubectl nao encontrado no PATH. Instale/configure o kubectl antes de instalar o Metrics Server."
}

$context = (& kubectl config current-context 2>$null)
if ($LASTEXITCODE -ne 0 -or [string]::IsNullOrWhiteSpace($context)) {
    throw "Nenhum contexto Kubernetes ativo. Verifique com: kubectl config get-contexts"
}

Write-Host "Contexto Kubernetes atual: $context"
Write-Host "Instalando Metrics Server..."
Invoke-Kubectl @("apply", "-f", $ManifestUrl)

if (-not $SkipLocalTlsPatch) {
    Write-Host "Aplicando patch para cluster local..."
    $patch = @'
{
  "spec": {
    "template": {
      "spec": {
        "containers": [
          {
            "name": "metrics-server",
            "args": [
              "--cert-dir=/tmp",
              "--secure-port=10250",
              "--kubelet-preferred-address-types=InternalIP,ExternalIP,Hostname",
              "--kubelet-use-node-status-port",
              "--metric-resolution=15s",
              "--kubelet-insecure-tls"
            ]
          }
        ]
      }
    }
  }
}
'@
    Invoke-Kubectl @("patch", "deployment", "metrics-server", "-n", "kube-system", "--type=merge", "-p", $patch)
}

Write-Host "Aguardando Metrics Server..."
Invoke-Kubectl @("rollout", "status", "deployment/metrics-server", "-n", "kube-system", "--timeout=180s")

Write-Host ""
Write-Host "Validacao:"
Invoke-Kubectl @("top", "nodes")
Write-Host ""
Write-Host "Depois de alguns minutos, rode:"
Write-Host "kubectl get hpa -n autocarehub"
