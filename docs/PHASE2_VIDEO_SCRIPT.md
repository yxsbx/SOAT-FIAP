# Roteiro do video - Fase 2

Tempo alvo: ate 15 minutos.

| Tempo | O que falar | O que mostrar | Requisito comprovado |
| ----- | ----------- | ------------- | -------------------- |
| 0:00-1:00 | Problema da oficina e evolucao da Fase 1 para Fase 2. | `README.md` e resumo do projeto. | Contexto e documentacao. |
| 1:00-2:30 | Arquitetura monolitica modular com Clean Architecture/Hexagonal. | `docs/PHASE2_ARCHITECTURE.md`, pacotes `domain`, `application`, `infrastructure`, `interfaces`. | Arquitetura e Clean Code. |
| 2:30-4:00 | Execucao local com Docker. | `docker compose down -v`, `docker compose up -d --build`, `docker compose ps`. | Docker e Compose. |
| 4:00-6:00 | Consumo das APIs obrigatorias. | Swagger e chamadas para OS, status, decisao externa e listagem. | APIs Fase 2 e OpenAPI. |
| 6:00-7:30 | Testes e cobertura. | `mvn clean verify`, `target/site/jacoco/index.html`. | Testes automatizados e JaCoCo. |
| 7:30-9:30 | Kubernetes. | `kubectl apply -f k8s/`, `kubectl get pods`, `kubectl get svc`, `kubectl get hpa`. | Deploy e escalabilidade. |
| 9:30-11:00 | Terraform. | `infra/README.md`, `terraform init`, `terraform plan`. | IaC. |
| 11:00-12:30 | CI/CD. | `.github/workflows/quality.yml` e `.github/workflows/deploy.yml`. | Pipeline automatizada. |
| 12:30-14:00 | Frontend demonstrativo. | `http://localhost:5173`. | Produto visual e consumo da API. |
| 14:00-15:00 | Fechamento e evidencias. | `docs/DELIVERY_DOCUMENT.md`, PDF final e links. | Entrega final. |

## Endpoints para demonstrar

- `POST /api/v1/auth/login`
- `POST /api/v1/service-orders`
- `GET /api/v1/service-orders/{serviceOrderId}`
- `POST /api/v1/service-orders/{serviceOrderId}/budget/decision`
- `POST /api/v1/service-orders/{serviceOrderId}/status/external`
- `GET /api/v1/service-orders`

## Checklist antes de gravar

- `mvn clean verify` passando.
- `npm run lint` e `npm run build` passando no frontend.
- `docker compose up -d --build` funcionando.
- Swagger acessivel em `http://localhost:8080/swagger-ui.html`.
- Manifests em `k8s/` aplicaveis no cluster escolhido.
- Variaveis sensiveis configuradas fora do repositorio.
- Link do video e PDF final preenchidos na entrega.

