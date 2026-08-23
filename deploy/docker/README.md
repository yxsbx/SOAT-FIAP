# Docker - AutoCare Hub

Execução local integrada com PostgreSQL, backend e frontend.

## Arquivos

```text
deploy/docker/
|-- .env                  # Ambiente local ignorado pelo Git
|-- .env.example          # Template seguro para ambiente local
|-- docker-compose.yml
```

## Comandos

Execute a partir da raiz do repositório:

```powershell
.\scripts\start-local.ps1 -Rebuild -Reset
```

Com Docker Compose diretamente:

```powershell
docker compose --env-file deploy/docker/.env -f deploy/docker/docker-compose.yml config --quiet
docker compose --env-file deploy/docker/.env -f deploy/docker/docker-compose.yml up -d --build
docker compose --env-file deploy/docker/.env -f deploy/docker/docker-compose.yml logs -f
docker compose --env-file deploy/docker/.env -f deploy/docker/docker-compose.yml down
```

Para usar variáveis locais, crie um arquivo `.env` em `deploy/docker/` com base em `deploy/docker/.env.example`.
