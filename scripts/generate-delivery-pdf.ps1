[CmdletBinding()]
param(
    [string]$VideoUrl = "[PREENCHER APOS PUBLICAR NO YOUTUBE OU VIMEO]",
    [string]$EvaluatorAccess = "Confirmar acesso do usuario soat-architecture antes do envio"
)

$ErrorActionPreference = "Stop"

$output = Join-Path $PSScriptRoot "..\docs\delivery\DELIVERY_DOCUMENT.pdf"

$pages = @(
    @(
        "AutoCare Hub - Tech Challenge FIAP - Fase 2",
        "Documento final de entrega",
        "",
        "Projeto: AutoCare Hub",
        "Participante: Yasmin Barcelos Pires - RM370897 - Discord yxsbx",
        "Repositorio privado: https://github.com/yxsbx/SOAT-FIAP",
        "Branch de entrega: main",
        "Acesso ao avaliador: $EvaluatorAccess.",
        "",
        "Resumo da solucao",
        "Backend monolitico para gestao de oficina mecanica, com clientes, veiculos,",
        "ordens de servico, servicos, pecas, estoque, orcamento, aprovacao e status da OS.",
        "A Fase 2 evolui a solucao com arquitetura em camadas, automacao, infraestrutura",
        "como codigo, Kubernetes, CI/CD, testes e documentacao de entrega.",
        "",
        "Evolucoes implementadas na Fase 2",
        "- Decisao externa de orcamento: POST /api/v1/service-orders/{id}/budget/decision",
        "- Atualizacao externa de status: POST /api/v1/service-orders/{id}/status/external",
        "- Listagem operacional de OS ordenada por prioridade/status e data.",
        "- Ocultacao de OS finalizadas e entregues na fila operacional.",
        "- Manifests Kubernetes em deploy/kubernetes/.",
        "- Estrutura Terraform em infra/terraform/.",
        "- Pipeline GitHub Actions em .github/workflows/deploy.yml.",
        "- Collection Postman em docs/api/postman/autocarehub-phase2.postman_collection.json."
    ),
    @(
        "Documentacao principal",
        "",
        "- README.md: entrada principal do projeto e fluxo rapido da API.",
        "- docs/api/openapi/openapi.yaml: contrato OpenAPI.",
        "- docs/domain/DDD_DOCUMENTATION.md: documentacao DDD.",
        "- docs/domain/EVENT_STORMING.md: Event Storming.",
        "- docs/architecture/PHASE2_ARCHITECTURE.md: desenho textual e Mermaid da arquitetura.",
        "- docs/testing/TESTING.md: estrategia e evidencias de testes.",
        "- docs/security/SECURITY_REPORT.md: relatorio de vulnerabilidades.",
        "- docs/security/SECURITY_SCAN_GUIDE.md: guia de execucao dos scans.",
        "- docs/delivery/PHASE2_VIDEO_SCRIPT.md: roteiro do video demonstrativo.",
        "",
        "Recursos escolhidos",
        "- Java, Spring Boot e Maven para o backend.",
        "- PostgreSQL e Flyway para persistencia e migracoes.",
        "- Docker e Docker Compose para execucao local.",
        "- Kubernetes com Deployments, Services, ConfigMaps, Secrets e HPA.",
        "- Terraform com criacao opcional de cluster kind e provider Kubernetes.",
        "- GitHub Actions para build, testes, imagens e deploy Kubernetes.",
        "- Vue/Vite no frontend demonstrativo.",
        "",
        "Seguranca e scans",
        "- JWT e autorizacao por perfil.",
        "- Validacao de CPF/CNPJ e placa.",
        "- Secrets fora do codigo e uso de placeholders seguros.",
        "- Relatorios em docs/security/SECURITY_REPORT.md e docs/security/SECURITY_SCAN_GUIDE.md."
    ),
    @(
        "Evidencias de validacao",
        "",
        "- mvn clean verify: 160 testes, 0 falhas, 0 erros, JaCoCo aprovado.",
        "- mvn -q spotless:check: aprovado.",
        "- docker compose config --quiet: aprovado.",
        "- npm run lint: aprovado.",
        "- npm run build: aprovado.",
        "- git diff --check: aprovado.",
        "- kubectl version --client: cliente disponivel.",
        "- terraform version: nao executado neste ambiente porque Terraform nao esta instalado.",
        "",
        "Links finais",
        "- Desenho da arquitetura: docs/architecture/PHASE2_ARCHITECTURE.md",
        "- Collection Postman: docs/api/postman/autocarehub-phase2.postman_collection.json",
        "- PDF final: docs/delivery/DELIVERY_DOCUMENT.pdf",
        "- Video demonstrativo: $VideoUrl",
        "",
        "Pendencias antes do envio no portal",
        "- Gravar e publicar o video demonstrativo de ate 15 minutos, se ainda nao publicado.",
        "- Substituir o placeholder do link do video em README.md e docs/delivery/DELIVERY_DOCUMENT.md.",
        "- Confirmar acesso do usuario soat-architecture ao repositorio privado.",
        "- Configurar secrets reais da plataforma de CI/CD se o deploy real for demonstrado.",
        "- Validar Terraform em maquina com terraform instalado.",
        "",
        "Conclusao",
        "O AutoCare Hub esta documentado e preparado para a entrega academica da Fase 2,",
        "com os artefatos exigidos versionados e os dados externos mantidos como placeholders claros."
    )
)

function Escape-Pdf([string] $value) {
    $normalized = $value.Normalize([Text.NormalizationForm]::FormD)
    $builder = [Text.StringBuilder]::new()

    foreach ($character in $normalized.ToCharArray()) {
        $category = [Globalization.CharUnicodeInfo]::GetUnicodeCategory($character)
        if ($category -ne [Globalization.UnicodeCategory]::NonSpacingMark) {
            [void] $builder.Append($character)
        }
    }

    $ascii = $builder.ToString() -replace "[^\x20-\x7E]", "?"
    return ($ascii -replace "\\", "\\" -replace "\(", "\(" -replace "\)", "\)")
}

$objects = [System.Collections.Generic.List[string]]::new()
$objects.Add("<< /Type /Catalog /Pages 2 0 R >>")

$pageReferences = for ($i = 0; $i -lt $pages.Count; $i++) {
    "$(3 + ($i * 2)) 0 R"
}

$objects.Add("<< /Type /Pages /Kids [ $($pageReferences -join " ") ] /Count $($pages.Count) >>")

for ($i = 0; $i -lt $pages.Count; $i++) {
    $pageObject = 3 + ($i * 2)
    $contentObject = $pageObject + 1
    $objects.Add("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Resources << /Font << /F1 << /Type /Font /Subtype /Type1 /BaseFont /Helvetica >> /F2 << /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >> >> >> /Contents $contentObject 0 R >>")

    $content = "BT`n/F2 14 Tf`n50 750 Td`n"
    $firstLine = $true

    foreach ($line in $pages[$i]) {
        if (-not $firstLine) {
            $content += "0 -16 Td`n"
        }

        $firstLine = $false
        $font = if ($line -match "^(AutoCare|Documento|Resumo|Evolucoes|Documentacao|Recursos|Seguranca|Evidencias|Links|Pendencias|Conclusao)") {
            "/F2 11 Tf"
        } else {
            "/F1 9 Tf"
        }
        $content += "$font`n($(Escape-Pdf $line)) Tj`n"
    }

    $content += "ET`n"
    $length = [Text.Encoding]::ASCII.GetByteCount($content)
    $objects.Add("<< /Length $length >>`nstream`n$content`nendstream")
}

$stream = [IO.MemoryStream]::new()
$encoding = [Text.Encoding]::ASCII

function Write-PdfText([string] $value) {
    $bytes = $encoding.GetBytes($value)
    $stream.Write($bytes, 0, $bytes.Length)
}

Write-PdfText "%PDF-1.4`n%AutoCare Hub`n"
$offsets = @()

for ($i = 0; $i -lt $objects.Count; $i++) {
    $offsets += $stream.Position
    Write-PdfText "$($i + 1) 0 obj`n$($objects[$i])`nendobj`n"
}

$xref = $stream.Position
Write-PdfText "xref`n0 $($objects.Count + 1)`n0000000000 65535 f `n"

foreach ($offset in $offsets) {
    Write-PdfText ("{0:0000000000} 00000 n `n" -f $offset)
}

Write-PdfText "trailer`n<< /Size $($objects.Count + 1) /Root 1 0 R >>`nstartxref`n$xref`n%%EOF`n"

[IO.File]::WriteAllBytes((Resolve-Path (Split-Path $output)).Path + "\DELIVERY_DOCUMENT.pdf", $stream.ToArray())
Get-Item $output
