# CI/CD - AutoCare Hub

Este documento descreve a pipeline de deploy da Fase 2 em GitHub Actions.

Workflow principal:

- `.github/workflows/phase2-ci-cd.yml`

## Quando executa

- Automaticamente em `push` para `main`.
- Manualmente pela aba GitHub Actions, usando `workflow_dispatch`.

## Etapas

1. Checkout do repositório.
2. Setup de Java 21, Node.js 22 e Terraform.
3. Build, testes e cobertura do backend com `mvn -B verify`.
4. Instalação do frontend com `npm ci`.
5. Lint e build do frontend com `npm run lint` e `npm run build`.
6. Validação do Terraform com `terraform fmt -check`, `terraform init -backend=false` e `terraform validate`.
7. Build das imagens Docker do backend e frontend.
8. Criação de cluster Kubernetes local e temporário com `kind` no runner do GitHub Actions.
9. Build das imagens locais `autocarehub-api:local` e `autocarehub-web:local`.
10. Carga das imagens no cluster `kind`.
11. Provisionamento da base Kubernetes e do banco com Terraform: Namespace, ConfigMap, Secret, PVC, Deployment e Service do PostgreSQL.
12. Aplicação do backend, frontend, Services e HPAs.
13. Verificação de rollout e listagem de Pods, Services e HPAs.

## Secrets

A pipeline da Fase 2 não exige secrets de nuvem, registry ou cluster remoto. O deploy automatizado ocorre em um cluster
`kind` efêmero criado dentro do próprio runner do GitHub Actions.

## Variáveis fixas da pipeline

| Variável | Valor |
| -------- | ----- |
| `TF_VAR_postgres_password` | Valor seguro de CI usado apenas no cluster temporário. |
| `TF_VAR_jwt_secret` | Segredo JWT de CI usado apenas no cluster temporário. |
| `TF_VAR_external_service_token` | Token externo de CI usado apenas no cluster temporário. |

## Deploy do banco

A pipeline aplica:

- Deployment e Service do PostgreSQL por Terraform.

As migrations SQL ficam em `backend/src/main/resources/db/migration/` e são executadas pelo Flyway durante o startup do backend. O deploy do banco nesta entrega é acadêmico/demonstrativo; ambiente produtivo deve avaliar banco gerenciado, backup e replicação.

## Publicação externa

Não há publicação em nuvem, GHCR ou cluster externo nesta entrega. O requisito de deploy Kubernetes é demonstrado no
CI/CD com Kubernetes local em `kind`, sem expor credenciais pessoais ou institucionais.

## Como executar

Execução automática:

```bash
git push origin main
```

Execução manual:

1. Abrir a aba Actions no GitHub.
2. Selecionar o workflow `Phase 2 CI/CD`.
3. Clicar em `Run workflow`.
4. Acompanhar os jobs `build-test`, `docker-build` e `local-kubernetes-deploy`.

## Como demonstrar no vídeo

Mostre:

- O arquivo `.github/workflows/phase2-ci-cd.yml`.
- Os jobs de build/teste/imagem e deploy Kubernetes local.
- A tela de GitHub Actions com a execução disponível.
- O log do job `local-kubernetes-deploy`, mostrando criação do cluster `kind`, `terraform apply`, `kubectl apply` e rollout.

Não afirme que houve publicação em nuvem. O deploy demonstrado é local/efêmero dentro do GitHub Actions.
