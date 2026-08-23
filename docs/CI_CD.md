# CI/CD - AutoCare Hub

Este documento descreve a pipeline de deploy da Fase 2 em GitHub Actions.

Workflow principal:

- `.github/workflows/phase2-ci-cd.yml`

## Quando executa

- Automaticamente em `push` para `main`.
- Manualmente pela aba GitHub Actions, usando `workflow_dispatch`.

## Etapas

1. Checkout do repositorio.
2. Setup de Java 21, Node.js 22 e Terraform.
3. Build, testes e cobertura do backend com `mvn -B verify`.
4. Instalação do frontend com `npm ci`.
5. Lint e build do frontend com `npm run lint` e `npm run build`.
6. Validação estrutural dos manifests Kubernetes em `k8s/*.yaml`.
7. Validação do Terraform com `terraform fmt -check`, `terraform init -backend=false` e `terraform validate`.
8. Build das imagens Docker do backend e frontend.
9. Push das imagens para GitHub Container Registry em `main` ou execução manual.
10. Configuração do kubeconfig a partir de secret da plataforma.
11. Aplicação de namespace, ConfigMap e PVC do banco.
12. Criação/atualização do Secret Kubernetes a partir de GitHub Actions Secrets.
13. Aplicação do PostgreSQL, backend, frontend, Services e HPAs.
14. Atualização dos Deployments para as imagens geradas no commit da pipeline.
15. Verificação de rollout e listagem de Deployments, Pods, Services e HPAs.

## Secrets necessários

Configure os valores em `Settings > Secrets and variables > Actions > Repository secrets`.

| Secret | Obrigatorio para deploy real | Uso |
| ------ | ---------------------------- | --- |
| `KUBE_CONFIG` | Sim | Conteudo do kubeconfig em base64 para acessar o cluster Kubernetes. |
| `POSTGRES_PASSWORD` | Sim | Senha aplicada no Secret Kubernetes para PostgreSQL e backend. |
| `JWT_SECRET` | Sim | Segredo JWT usado pela API. Deve ter pelo menos 32 bytes. |
| `EXTERNAL_SERVICE_TOKEN` | Sim | Token compartilhado exigido pelos webhooks externos simulados da API. |

O push para GHCR usa `GITHUB_TOKEN`, secret automatico do GitHub Actions. Nao configure nem exponha tokens pessoais para esse fluxo sem necessidade.

## Variaveis fixas da pipeline

| Variavel | Valor |
| -------- | ----- |
| `REGISTRY` | `ghcr.io` |
| `BACKEND_IMAGE` | `ghcr.io/${{ github.repository_owner }}/autocarehub-api` |
| `FRONTEND_IMAGE` | `ghcr.io/${{ github.repository_owner }}/autocarehub-web` |
| `IMAGE_TAG` | SHA do commit executado pela pipeline |

## Deploy do banco

A pipeline aplica:

- `k8s/postgres-deployment.yaml`
- `k8s/postgres-service.yaml`

As migrations SQL ficam em `backend/src/main/resources/db/migration/` e sao executadas pelo Flyway durante o startup do backend. O deploy do banco nesta entrega e academico/demonstrativo; ambiente produtivo deve avaliar banco gerenciado, backup e replicação.

## Comportamento sem secrets

Se `KUBE_CONFIG`, `POSTGRES_PASSWORD`, `JWT_SECRET` ou `EXTERNAL_SERVICE_TOKEN` nao estiverem configurados, a pipeline continua executando build, testes, validacoes e build das imagens. O deploy real no cluster e pulado com mensagem explicita no log.

Esse comportamento evita expor credenciais e permite demonstrar a automação mesmo sem cluster disponivel.

## Como executar

Execucao automatica:

```bash
git push origin main
```

Execucao manual:

1. Abrir a aba Actions no GitHub.
2. Selecionar o workflow `Phase 2 CI/CD`.
3. Clicar em `Run workflow`.
4. Acompanhar os jobs `build-test`, `docker-build` e `deploy-kubernetes`.

## Como demonstrar no video

Mostre:

- O arquivo `.github/workflows/phase2-ci-cd.yml`.
- Os jobs de build/teste/imagem e deploy Kubernetes.
- A tela de GitHub Actions com a execucao disponivel.
- A lista de secrets exigidos, sem revelar valores.
- O log de deploy real se os secrets e cluster estiverem configurados.
- A mensagem de deploy pulado se o ambiente nao tiver secrets.

Nao afirme que houve deploy real se o job apenas executou as etapas de build/validação ou pulou o Kubernetes por falta de secrets.
