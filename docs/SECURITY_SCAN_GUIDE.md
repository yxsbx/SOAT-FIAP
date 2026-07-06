# Guia de scans de segurança

Este guia lista os comandos usados para revalidar a segurança do AutoCare Hub na Fase 2. Registre resultados reais em
[SECURITY_REPORT.md](SECURITY_REPORT.md) apenas depois de executar as ferramentas.

## Pre-requisitos

- Java 21 e Maven.
- Node.js/npm para o frontend.
- Docker, se for executar scans de imagem.
- Gitleaks, Trivy ou ferramenta equivalente, quando disponível localmente.

## Backend

```bash
mvn dependency-check:check
```

Relatórios esperados:

```text
target/dependency-check/dependency-check-report.html
target/dependency-check/dependency-check-report.json
```

## Frontend

```bash
cd frontend
npm audit --audit-level=high
cd ..
```

## Imagens Docker

Com Trivy, quando disponível:

```bash
docker build -t autocarehub-api:local .
docker build -t autocarehub-web:local ./frontend
trivy image autocarehub-api:local
trivy image autocarehub-web:local
```

## Secrets

Com Gitleaks, quando disponível:

```bash
gitleaks detect --source . --report-format json --report-path security-reports/secrets/gitleaks.json
```

## API dinâmica

Com OWASP ZAP, quando disponível e com a API local rodando:

```bash
docker run --rm -t ghcr.io/zaproxy/zaproxy:stable zap-api-scan.py \
  -t http://host.docker.internal:8080/v3/api-docs \
  -f openapi
```

## Pontos obrigatorios de revisão

- JWT configurado por variável de ambiente.
- `.env` real fora do versionamento.
- CORS sem wildcard em ambiente real.
- Secrets em `k8s/02-secret.yaml` apenas como placeholders.
- Sem senhas reais em workflows.
- Validações de CPF/CNPJ, placa e payloads mantidas.
- Autorização por perfil e escopo de cliente/empresa preservada.

