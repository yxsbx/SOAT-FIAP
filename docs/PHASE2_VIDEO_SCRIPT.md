# Roteiro do video - Fase 2

Tempo alvo: ate 15 minutos.

| Tempo | O que falar | O que mostrar | Requisito comprovado |
| ----- | ----------- | ------------- | -------------------- |
| 0:00-1:00 | Problema da oficina e evolucao da Fase 1 para Fase 2. | `README.md` e resumo do projeto. | Contexto e documentacao. |
| 1:00-2:30 | Arquitetura monolitica modular com Clean Architecture/Hexagonal. | `docs/PHASE2_ARCHITECTURE.md`, pacotes `domain`, `application`, `infrastructure`, `interfaces`. | Arquitetura e Clean Code. |
| 2:30-4:00 | Execucao local com Docker. | `docker compose down -v`, `docker compose up -d --build`, `docker compose ps`. | Docker e Compose. |
| 4:00-6:00 | Consumo das APIs obrigatorias. | Swagger e chamadas para OS, status, decisao externa e listagem. | APIs Fase 2 e OpenAPI. |
| 6:00-7:30 | Testes e cobertura. | `mvn clean verify`, `target/site/jacoco/index.html`. | Testes automatizados e JaCoCo. |
| 7:30-9:30 | Kubernetes. | `kubectl apply --dry-run=client -f k8s/`, `kubectl apply -f k8s/`, `kubectl get pods -n autocarehub`, `kubectl get svc -n autocarehub`, `kubectl get hpa -n autocarehub`, `kubectl logs -n autocarehub deploy/autocarehub-api`. | Deploy e escalabilidade. |
| 9:30-11:00 | Terraform. | `infra/README.md`, `terraform init`, `terraform fmt`, `terraform validate`, `terraform plan`, `terraform apply`, `terraform destroy`. | IaC. |
| 11:00-12:30 | CI/CD. | `.github/workflows/quality.yml`, `.github/workflows/deploy.yml`, tela do GitHub Actions e secrets necessários sem exibir valores. | Pipeline automatizada. |
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
- Metrics Server instalado no cluster para demonstrar HPA.
- Imagens `ghcr.io/yxsbx/autocarehub-api:latest` e `ghcr.io/yxsbx/autocarehub-web:latest` publicadas ou carregadas no cluster local.
- Secrets `KUBE_CONFIG`, `POSTGRES_PASSWORD` e `JWT_SECRET` configurados no GitHub Actions para deploy real.
- Link do video e PDF final preenchidos na entrega.

## Demonstracao Kubernetes

1. Abrir `k8s/README.md` e mostrar a separacao dos manifests.
2. Validar sem aplicar:

```bash
kubectl apply --dry-run=client -f k8s/
```

3. Aplicar no cluster:

```bash
kubectl apply -f k8s/
kubectl get pods -n autocarehub
kubectl get svc -n autocarehub
kubectl get hpa -n autocarehub
kubectl logs -n autocarehub deploy/autocarehub-api
```

4. Demonstrar acesso local:

```bash
kubectl port-forward -n autocarehub svc/backend 8080:8080
kubectl port-forward -n autocarehub svc/autocarehub-web 5173:8080
```

5. Encerrar a demonstracao:

```bash
kubectl delete -f k8s/
```

## Demonstracao CI/CD

1. Abrir `.github/workflows/quality.yml` e mostrar:
   - Spotless;
   - `mvn verify`;
   - lint/build/audit do frontend;
   - validação do Docker Compose;
   - build das imagens.

2. Abrir `.github/workflows/deploy.yml` e mostrar:
   - `mvn -B verify`;
   - `npm ci`, lint e build;
   - validação estrutural dos YAMLs Kubernetes;
   - `terraform fmt -check`, `terraform init -backend=false` e `terraform validate`;
   - build das imagens Docker;
   - push para GHCR em `main`;
   - criação do Secret Kubernetes a partir de GitHub Actions Secrets;
   - aplicação do PVC, PostgreSQL, backend, frontend, Services e HPAs;
   - verificação de rollout.

3. Mostrar no GitHub Actions a execução disponível. Não afirmar que o deploy real executou se os secrets não estiverem configurados.

4. Explicar os secrets sem exibir valores:

```text
KUBE_CONFIG
POSTGRES_PASSWORD
JWT_SECRET
```
