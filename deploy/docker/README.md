# Docker - AutoCare Hub

Execução local integrada com PostgreSQL, backend e frontend.

## Arquivos

```text
deploy/docker/
|-- docker-compose.yml
`-- env/
    `-- .env.example
```

## Comandos

Execute a partir da raiz do repositório:

```bash
docker compose -f deploy/docker/docker-compose.yml config --quiet
docker compose -f deploy/docker/docker-compose.yml up -d --build
docker compose -f deploy/docker/docker-compose.yml logs -f
docker compose -f deploy/docker/docker-compose.yml down
```

Para usar variáveis locais, crie um arquivo `.env` em `deploy/docker/` com base em `deploy/docker/env/.env.example`.
