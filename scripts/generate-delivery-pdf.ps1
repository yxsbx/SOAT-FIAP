$ErrorActionPreference = "Stop"

$output = Join-Path $PSScriptRoot "..\docs\DELIVERY_DOCUMENT.pdf"

$pages = @(
    @(
        "AutoCare Hub - Tech Challenge FIAP - Fase 2",
        "Documento final de entrega",
        "",
        "Projeto: AutoCare Hub",
        "Participante: Yasmin Barcelos Pires - RM370897 - Discord yxsbx",
        "Repositorio privado: https://github.com/yxsbx/SOAT-FIAP",
        "Branch de entrega: main",
        "Acesso ao avaliador: usuário informado pela FIAP com leitura concedida.",
        "",
        "Resumo da solução",
        "Backend monolitico para gestão de oficina mecanica, com clientes, veiculos,",
        "ordens de servico, servicos, pecas, estoque, orcamento, aprovação e status da OS.",
        "A Fase 2 evolui a solução com arquitetura em camadas, automação, infraestrutura",
        "como código, Kubernetes, CI/CD, testes e documentação de entrega.",
        "",
        "Evoluções implementadas na Fase 2",
        "- Decisão externa de orcamento: POST /api/v1/service-orders/{id}/budget/decision",
        "- Atualização externa de status: POST /api/v1/service-orders/{id}/status/external",
        "- Listagem operacional de OS ordenada por prioridade/status e data.",
        "- Ocultação de OS finalizadas e entregues na fila operacional.",
        "- Manifests Kubernetes em k8s/.",
        "- Estrutura Terraform em infra/.",
        "- Pipeline GitHub Actions em .github/workflows/deploy.yml.",
        "- Collection Postman em docs/postman/autocarehub-phase2.postman_collection.json."
    ),
    @(
        "Documentação principal",
        "",
        "- README.md: entrada principal do projeto e fluxo rapido da API.",
        "- docs/openapi/openapi.yaml: contrato OpenAPI.",
        "- docs/DDD_DOCUMENTATION.md: documentação DDD.",
        "- docs/EVENT_STORMING.md: Event Storming.",
        "- docs/PHASE2_ARCHITECTURE.md: desenho textual e Mermaid da arquitetura.",
        "- docs/TESTING.md: estrategia e evidencias de testes.",
        "- docs/SECURITY_REPORT.md: relatorio de vulnerabilidades.",
        "- docs/SECURITY_SCAN_GUIDE.md: guia de execução dos scans.",
        "- docs/PHASE2_VIDEO_SCRIPT.md: roteiro do video demonstrativo.",
        "",
        "Recursos escolhidos",
        "- Java, Spring Boot e Maven para o backend.",
        "- PostgreSQL e Flyway para persistencia e migrações.",
        "- Docker e Docker Compose para execução local.",
        "- Kubernetes com Deployments, Services, ConfigMaps, Secrets e HPA.",
        "- Terraform com provider Kubernetes para provisionamento academico/local.",
        "- GitHub Actions para build, testes, imagens e deploy Kubernetes.",
        "- Vue/Vite no frontend demonstrativo.",
        "",
        "Seguranca e scans",
        "- JWT e autorização por perfil.",
        "- Validação de CPF/CNPJ e placa.",
        "- Secrets fora do código e uso de placeholders seguros.",
        "- Relatorios em docs/SECURITY_REPORT.md e docs/SECURITY_SCAN_GUIDE.md."
    ),
    @(
        "Evidencias de validação",
        "",
        "- mvn clean verify: 160 testes, 0 falhas, 0 erros, JaCoCo aprovado.",
        "- mvn -q spotless:check: aprovado.",
        "- docker compose config --quiet: aprovado.",
        "- npm run lint: aprovado.",
        "- npm run build: aprovado.",
        "- git diff --check: aprovado.",
        "- kubectl version --client: cliente disponível.",
        "- terraform version: não executado neste ambiente porque Terraform não esta instalado.",
        "",
        "Links finais",
        "- Desenho da arquitetura: docs/PHASE2_ARCHITECTURE.md",
        "- Collection Postman: docs/postman/autocarehub-phase2.postman_collection.json",
        "- PDF final: docs/DELIVERY_DOCUMENT.pdf",
        "- Video demonstrativo: [INSERIR LINK DO VIDEO DA FASE 2]",
        "",
        "Pendencias antes do envio no portal",
        "- Gravar e publicar o video demonstrativo de ate 15 minutos.",
        "- Substituir o placeholder do link do video em README.md e docs/DELIVERY_DOCUMENT.md.",
        "- Configurar secrets reais da plataforma de CI/CD se o deploy real for demonstrado.",
        "- Validar Terraform em maquina com terraform instalado.",
        "",
        "Conclusão",
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
        $font = if ($line -match "^(AutoCare|Documento|Resumo|Evoluções|Documentação|Recursos|Segurança|Evidencias|Links|Pendências|Conclusão)") {
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
