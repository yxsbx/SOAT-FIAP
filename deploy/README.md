# Deploy - AutoCare Hub

Esta pasta concentra os arquivos de publicação da aplicação. A intenção é separar execução/publicação de código de aplicação.

## Organização

```text
deploy/
|-- docker/       # Docker Compose local e exemplos de variaveis
|-- kubernetes/   # Manifests Kubernetes para backend, frontend e PostgreSQL demonstrativo
`-- pipelines/    # Notas sobre os workflows de CI/CD
```

## Relação com outras pastas

- `backend/`: código e build da API.
- `frontend/`: código e build da aplicação web.
- `infra/terraform/`: provisionamento de infraestrutura base.
- `deploy/`: publicação dos componentes em Docker/Kubernetes.
