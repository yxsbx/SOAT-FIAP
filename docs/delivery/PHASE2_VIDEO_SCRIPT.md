# Roteiro do video - Fase 2

Tempo alvo: ate 15 minutos.

| Tempo       | O que falar                                                      | O que mostrar                                                                                                                                                                                                                   | Requisito comprovado             |
|-------------|------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------|
| 0:00-1:00   | Problema da oficina e evolução da Fase 1 para Fase 2.            | `README.md` e resumo do projeto.                                                                                                                                                                                                | Contexto e documentação.         |
| 1:00-2:30   | Arquitetura monolitica modular com Clean Architecture/Hexagonal. | `docs/architecture/PHASE2_ARCHITECTURE.md`, pacotes `domain`, `application`, `infrastructure`, `interfaces`.                                                                                                                                 | Arquitetura e Clean Code.        |
| 2:30-4:00   | Execução local com Docker.                                       | `.\scripts\start-local.ps1 -Rebuild -Reset`, `docker compose --env-file deploy/docker/.env -f deploy/docker/docker-compose.yml ps`.                                                                                             | Docker e Compose.                |
| 4:00-6:00   | Consumo das APIs obrigatorias.                                   | Swagger e chamadas para OS, status, decisão externa e listagem.                                                                                                                                                                 | APIs Fase 2 e OpenAPI.           |
| 6:00-7:30   | Testes e cobertura.                                              | `mvn clean verify`, `backend/target/site/jacoco/index.html`.                                                                                                                                                                            | Testes automatizados e JaCoCo.   |
| 7:30-9:30   | Kubernetes.                                                      | `kubectl apply --dry-run=client -f deploy/kubernetes/`, `kubectl apply -f deploy/kubernetes/`, `kubectl get pods -n autocarehub`, `kubectl get svc -n autocarehub`, `kubectl get hpa -n autocarehub`, `kubectl logs -n autocarehub deploy/autocarehub-api`. | Deploy e escalabilidade.         |
| 9:30-11:00  | Terraform.                                                       | `infra/README.md`, `terraform init`, `terraform fmt`, `terraform validate`, `terraform plan`, `terraform apply`, `terraform destroy`.                                                                                           | IaC.                             |
| 11:00-12:30 | CI/CD.                                                           | `.github/workflows/quality.yml`, `.github/workflows/deploy.yml`, `docs/CI_CD.md`, tela do GitHub Actions e secrets necessários sem exibir valores.                                                                               | Pipeline automatizada.           |
| 12:30-14:00 | Frontend demonstrativo.                                          | `http://localhost:5173`.                                                                                                                                                                                                        | Produto visual e consumo da API. |
| 14:00-15:00 | Fechamento e evidencias.                                         | `docs/delivery/DELIVERY_DOCUMENT.md`, PDF final e links.                                                                                                                                                                                 | Entrega final.                   |

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
- `.\scripts\start-local.ps1 -Rebuild -Reset` funcionando.
- Swagger acessível em `http://localhost:8080/swagger-ui.html`.
- Manifests em `deploy/kubernetes/` aplicáveis no cluster escolhido.
- Metrics Server instalado no cluster para demonstrar HPA.
- Imagens `ghcr.io/yxsbx/autocarehub-api:latest` e `ghcr.io/yxsbx/autocarehub-web:latest` publicadas ou carregadas no cluster local.
- Secrets `KUBE_CONFIG`, `POSTGRES_PASSWORD` e `JWT_SECRET` configurados no GitHub Actions para deploy real.
- Se os secrets nao estiverem configurados, explicar no video que o deploy Kubernetes foi pulado/simulado por limitacao de ambiente.
- Link do video e PDF final preenchidos na entrega.

## Demonstração Kubernetes

1. Abrir `deploy/kubernetes/README.md` e mostrar a separação dos manifests.

2. Validar sem aplicar:

```bash
kubectl apply --dry-run=client -f deploy/kubernetes/
```

3. Aplicar no cluster:

```bash
kubectl apply -f deploy/kubernetes/
kubectl get pods -n autocarehub
kubectl get svc -n autocarehub
kubectl get hpa -n autocarehub
kubectl logs -n autocarehub deploy/autocarehub-api
```

4. Demonstrar HPA com Metrics Server instalado:

```bash
kubectl get hpa -n autocarehub
kubectl run autocarehub-load --rm -i --tty --image=busybox:1.36 --restart=Never -n autocarehub -- \
  /bin/sh -c "while true; do wget -q -O- http://backend:8080/actuator/health >/dev/null; done"
kubectl get hpa -n autocarehub --watch
kubectl get deploy -n autocarehub
```

5. Demonstrar acesso local:
```bash
kubectl port-forward -n autocarehub svc/backend 8080:8080
kubectl port-forward -n autocarehub svc/autocarehub-web 5173:8080
```

6. Encerrar a demonstração:
```bash
kubectl delete -f deploy/kubernetes/
```

## Demonstração CI/CD

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
   - push para GHCR em `main` ou execução manual;
   - criação do Secret Kubernetes a partir de GitHub Actions Secrets;
   - aplicação do PVC, PostgreSQL, backend, frontend, Services e HPAs;
   - atualização dos Deployments para a imagem do SHA da execução;
   - verificação de rollout.

3. Abrir `docs/CI_CD.md` e mostrar a tabela de secrets e a regra de deploy pulado quando algum secret estiver ausente.

4. Mostrar no GitHub Actions a execução disponível. Não afirmar que o deploy real executou se os secrets não estiverem configurados.

5. Explicar os secrets sem exibir valores:

```text
KUBE_CONFIG
POSTGRES_PASSWORD
JWT_SECRET
```
