# Pipelines - AutoCare Hub

Os workflows executáveis ficam em `.github/workflows/`. Esta pasta documenta a parte de publicação para manter os arquivos de deploy agrupados.

## Workflows

- `quality.yml`: valida backend, frontend, Docker Compose e build das imagens.
- `deploy.yml`: valida backend, frontend, Kubernetes, Terraform, gera imagens e aplica manifests quando os secrets de deploy estão configurados.
- `qodana_code_quality.yml`: executa análise Qodana.

## Caminhos usados pela pipeline

- Backend: `backend/`
- Frontend: `frontend/`
- Docker Compose: `deploy/docker/docker-compose.yml`
- Kubernetes: `deploy/kubernetes/`
- Terraform: `infra/terraform/`
