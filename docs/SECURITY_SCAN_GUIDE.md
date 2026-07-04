# Guia de scans de seguranca

Este guia lista os comandos usados para revalidar a seguranca do AutoCare Hub na Fase 2. Registre resultados reais em
[SECURITY_REPORT.md](SECURITY_REPORT.md) apenas depois de executar as ferramentas.

## Pre-requisitos

- Java 21 e Maven.
- Node.js/npm para o frontend.
- Docker, se for executar scans de imagem.
- Gitleaks, Trivy ou ferramenta equivalente, quando disponivel localmente.

## Backend

```bash
mvn dependency-check:check
```

Relatorios esperados:

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

Com Trivy, quando disponivel:

```bash
docker build -t autocarehub-api:local .
docker build -t autocarehub-web:local ./frontend
trivy image autocarehub-api:local
trivy image autocarehub-web:local
```

## Secrets

Com Gitleaks, quando disponivel:

```bash
gitleaks detect --source . --report-format json --report-path security-reports/secrets/gitleaks.json
```

## API dinamica

Com OWASP ZAP, quando disponivel e com a API local rodando:

```bash
docker run --rm -t ghcr.io/zaproxy/zaproxy:stable zap-api-scan.py \
  -t http://host.docker.internal:8080/v3/api-docs \
  -f openapi
```

## Pontos obrigatorios de revisao

- JWT configurado por variavel de ambiente.
- `.env` real fora do versionamento.
- CORS sem wildcard em ambiente real.
- Secrets em `k8s/02-secret.yaml` apenas como placeholders.
- Sem senhas reais em workflows.
- Validacoes de CPF/CNPJ, placa e payloads mantidas.
- Autorizacao por perfil e escopo de cliente/empresa preservada.

