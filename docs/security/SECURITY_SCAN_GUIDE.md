# Guia de scans de segurança

Este guia lista os comandos de segurança usados para revalidar o AutoCare Hub na Fase 2. Registre em
[SECURITY_REPORT.md](SECURITY_REPORT.md) apenas resultados realmente executados ou evidências já versionadas no
repositório, sempre deixando claro qual é o caso.

## Pré-requisitos

- Java 21 e Maven.
- Node.js 22/npm para o frontend.
- Docker em execução, se forem executados scans por imagem ou ferramentas em container.
- Gitleaks e Trivy instalados localmente, ou Docker ativo para executá-los via imagem.

## Backend

```bash
mvn dependency-check:check
```

Quando a base local do OWASP Dependency-Check já estiver atualizada e for necessário evitar acesso à internet:

```bash
mvn dependency-check:check -DautoUpdate=false
```

Saídas esperadas:

```text
backend/target/dependency-check/dependency-check-report.html
backend/target/dependency-check/dependency-check-report.json
```

Se o comando ficar bloqueado em `odc.update.lock`, aguarde outro processo Dependency-Check terminar. Não remova o lock
do cache Maven sem confirmar que não existe processo Maven/Java atualizando a base.

## Frontend

```bash
cd frontend
npm audit --audit-level=high
cd ..
```

Para gerar evidência JSON:

```bash
cd frontend
npm audit --json > ../security-reports/frontend-dependencies/npm-audit-report.json
cd ..
```

## Imagens Docker

Com Trivy instalado localmente:

```bash
docker build -t autocarehub-api:local .
docker build -t autocarehub-web:local ./frontend
trivy image autocarehub-api:local
trivy image autocarehub-web:local
```

Com Docker ativo e Trivy via container:

```bash
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy:latest image autocarehub-api:local
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy:latest image autocarehub-web:local
```

## Secrets

Com Gitleaks instalado localmente:

```bash
gitleaks detect --source . --report-format json --report-path security-reports/secrets/gitleaks.json
```

Com Docker ativo e Gitleaks via container:

```bash
docker run --rm -v "$PWD:/repo" zricethezav/gitleaks:latest detect --source /repo \
  --report-format json --report-path /repo/security-reports/secrets/gitleaks.json
```

## API dinâmica

Com OWASP ZAP, quando disponível e com a API local rodando:

```bash
docker run --rm -t ghcr.io/zaproxy/zaproxy:stable zap-api-scan.py \
  -t http://host.docker.internal:8080/v3/api-docs \
  -f openapi
```

## Revisão manual obrigatória

- JWT deve exigir segredo por variável de ambiente e tamanho mínimo.
- CORS não deve aceitar `*` ou `null`; origens devem vir de configuração por ambiente.
- `.env`, arquivos de chave, kubeconfig local e `terraform.tfvars` reais devem ficar fora do versionamento.
- `.env.example`, `frontend/.env.example`, `infra/terraform.tfvars.example` e `k8s/secret.example.yaml` devem usar apenas placeholders.
- Kubernetes Secrets reais devem ser criados por CI/CD, Terraform ou operador do ambiente, nunca versionados.
- GitHub Actions não deve imprimir valores de secrets.
- Validações de CPF/CNPJ, placa e payloads devem continuar cobertas por domínio, OpenAPI/Bean Validation e testes.
- Autorização por perfil e escopo de cliente/empresa deve continuar coberta por testes de segurança.
- O relatório final deve separar resultados reexecutados na rodada atual de evidências versionadas anteriormente.