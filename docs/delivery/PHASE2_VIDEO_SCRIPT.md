# Roteiro do video - Fase 2

Tempo alvo: ate 15 minutos.

Objetivo do video: demonstrar a aplicacao em execucao, CI/CD, consumo das APIs e escalabilidade automatica, sem publicar
nada em nuvem. O deploy Kubernetes e demonstrado localmente e no GitHub Actions com cluster `kind` temporario.

## Checklist antes de gravar

1. Confirmar que Docker Desktop esta aberto.
2. Confirmar que o repositorio esta atualizado e com as alteracoes commitadas/pushadas.
3. Confirmar que o workflow `Phase 2 CI/CD` tem uma execucao recente no GitHub Actions.
4. Confirmar que o video sera publicado no YouTube ou Vimeo como publico ou não listado.
5. Deixar abertas as abas:
   - `README.md`;
   - `docs/architecture/PHASE2_ARCHITECTURE.md`;
   - `http://localhost:5173`;
   - `http://localhost:8080/swagger-ui.html`;
   - GitHub Actions do repositorio.

## Comandos de preparacao

Rode antes de comecar a gravar, para evitar gastar tempo com downloads:

```powershell
cd C:\Users\ybarc\git\SOAT-FIAP
docker --version
docker compose version
mvn -version
node --version
npm --version
terraform -version
kubectl version --client
```

Validar testes e builds:

```powershell
cd C:\Users\ybarc\git\SOAT-FIAP\backend
mvn clean verify

cd C:\Users\ybarc\git\SOAT-FIAP\frontend
npm ci
npm run lint
npm run build
```

Subir a aplicacao local:

```powershell
cd C:\Users\ybarc\git\SOAT-FIAP
.\scripts\start-local.ps1 -Rebuild -Reset
docker compose ps
```

Validar Terraform:

```powershell
cd C:\Users\ybarc\git\SOAT-FIAP\infra
terraform fmt -check
terraform init -backend=false
terraform validate
cd ..
```

## Sequencia do video

| Tempo       | Tema                 | O que mostrar                                                                  | Requisito comprovado                           |
|-------------|----------------------|--------------------------------------------------------------------------------|------------------------------------------------|
| 0:00-1:00   | Contexto e objetivos | `README.md`, descricao da solucao e objetivos da Fase 2.                       | README atualizado.                             |
| 1:00-2:20   | Arquitetura          | Diagrama no README e `docs/architecture/PHASE2_ARCHITECTURE.md`.               | Componentes, infraestrutura e fluxo de deploy. |
| 2:20-3:30   | Execucao local       | `docker compose ps`, frontend e Swagger abertos.                               | Dockerfile e docker-compose.                   |
| 3:30-5:50   | APIs obrigatorias    | Swagger/Postman: login, criar OS, consultar status, listagem, webhook externo. | Consumo das APIs.                              |
| 5:50-7:00   | Testes               | `mvn clean verify`, quantidade de testes e JaCoCo.                             | Testes automatizados.                          |
| 7:00-9:00   | Kubernetes           | `k8s/`, Deployments, Services, ConfigMap, Secret e HPA CPU/memoria.            | Manifestos Kubernetes.                         |
| 9:00-10:20  | Terraform            | `infra/`, `terraform validate`, recursos criados.                              | IaC para cluster local e banco.                |
| 10:20-12:20 | CI/CD                | `.github/workflows/phase2-ci-cd.yml` e GitHub Actions.                         | Build, testes, Docker, deploy K8s e banco.     |
| 12:20-14:10 | Escalabilidade       | HPA e simulacao de carga ou multiplas OS.                                      | Escalabilidade automatica.                     |
| 14:10-15:00 | Fechamento           | Checklist do README, link do repo, lembrar PDF e link do video.                | Entrega final.                                 |

## Fala sugerida por bloco

### 1. Contexto

Falar:

> Este projeto evolui a aplicacao da Fase 1 para uma solucao com melhor organizacao de codigo, testes, Docker,
> Kubernetes, Terraform e CI/CD. A entrega e academica e demonstrada localmente, sem publicacao em cloud.

Mostrar:

- `README.md`;
- secao `Objetivos da Fase 2`;
- checklist final da entrega.

### 2. Arquitetura

Mostrar:

- diagrama Mermaid do README;
- [docs/architecture/PHASE2_ARCHITECTURE.md](../architecture/PHASE2_ARCHITECTURE.md);
- pacotes principais do backend:
  - `domain`;
  - `application`;
  - `infrastructure`;
  - `interfaces`.

Falar:

> A aplicacao segue uma organizacao hexagonal: dominio e casos de uso ficam separados dos adaptadores de REST,
> seguranca e persistencia. A infraestrutura da Fase 2 usa Docker local, Kubernetes, Terraform e pipeline CI/CD.

### 3. Execucao local

Comandos para mostrar:

```powershell
cd C:\Users\ybarc\git\SOAT-FIAP
docker compose ps
```

Abrir:

- Frontend: `http://localhost:5173`;
- Swagger: `http://localhost:8080/swagger-ui.html`;
- Healthcheck: `http://localhost:8080/actuator/health`.

Falar:

> O Docker Compose sobe PostgreSQL, backend e frontend. O backend aguarda o banco saudavel, executa migrations Flyway e
> expoe healthcheck.

### 4. APIs obrigatorias

Preferencia: demonstrar pelo Swagger para economizar tempo.

Endpoints essenciais:

```text
POST /api/v1/auth/login
POST /api/v1/service-orders
GET  /api/v1/service-orders/{serviceOrderId}
GET  /api/v1/service-orders
POST /api/v1/service-orders/{serviceOrderId}/budget/external-approval
POST /api/v1/service-orders/{serviceOrderId}/budget/external-rejection
POST /api/v1/service-orders/{serviceOrderId}/status/external
```

Pontos para falar:

- Abertura de OS retorna identificador unico.
- Consulta de status retorna a etapa atual.
- Listagem operacional ordena por `IN_PROGRESS`, `WAITING_APPROVAL`, `IN_DIAGNOSIS` e `RECEIVED`.
- Finalizadas e entregues não aparecem na fila operacional.
- Atualizacao externa simula ferramenta como email usando `X-External-Service-Token`.

Se quiser usar comandos, depois de obter o token:

```powershell
$TOKEN = "COLE_O_TOKEN_AQUI"
$EXTERNAL_SERVICE_TOKEN = "replace-with-local-external-service-token"
```

Criar uma OS já com orçamento gerado para demonstrar aprovação externa:

```powershell
$createdOrder = Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8080/api/v1/service-orders" `
  -Headers @{ Authorization = "Bearer $TOKEN" } `
  -ContentType "application/json" `
  -Body '{
    "customerDocument": "12345678909",
    "vehicleId": "20000000-0000-0000-0000-000000000001",
    "diagnosticNotes": "OS para demonstrar aprovação externa por email.",
    "services": [
      { "serviceId": "30000000-0000-0000-0000-000000000004", "quantity": 1 }
    ],
    "generateBudget": true
  }'

$SERVICE_ORDER_ID = $createdOrder.id
```

Consultar listagem:

```powershell
curl.exe "http://localhost:8080/api/v1/service-orders?page=0&size=10" `
  -H "Authorization: Bearer $TOKEN"
```

Simular aprovação externa do cliente por email:

```powershell
curl.exe -X POST "http://localhost:8080/api/v1/service-orders/$SERVICE_ORDER_ID/budget/external-approval" `
  -H "X-External-Service-Token: $EXTERNAL_SERVICE_TOKEN" `
  -H "Content-Type: application/json" `
  -d '{ "source": "email", "reason": "Cliente aprovou o orçamento pelo link enviado." }'
```

Atualizar status por ferramenta externa:

```powershell
curl.exe -X POST "http://localhost:8080/api/v1/service-orders/$SERVICE_ORDER_ID/status/external" `
  -H "X-External-Service-Token: $EXTERNAL_SERVICE_TOKEN" `
  -H "Content-Type: application/json" `
  -d '{ "status": "FINISHED", "source": "email", "message": "Atualizacao externa simulada." }'
```

### 5. Testes automatizados

Mostrar:

```powershell
cd C:\Users\ybarc\git\SOAT-FIAP\backend
mvn clean verify
```

Falar:

> A suite rapida roda pelo Surefire, e o verify tambem executa os testes de integracao pelo Failsafe. O projeto tem
> 146 testes rapidos, 26 testes de integracao e gate JaCoCo ativo para cobertura.

Abrir, se der tempo:

```text
backend/target/site/jacoco/index.html
docs/testing/TESTING.md
```

### 6. Kubernetes

Mostrar arquivos:

```text
k8s/backend-deployment.yaml
k8s/backend-service.yaml
k8s/backend-hpa.yaml
k8s/frontend-deployment.yaml
k8s/frontend-service.yaml
k8s/frontend-hpa.yaml
k8s/configmap.yaml
k8s/secret.example.yaml
```

Comandos para cluster local ja existente:

```powershell
cd C:\Users\ybarc\git\SOAT-FIAP
kubectl config get-contexts
kubectl config use-context docker-desktop
.\scripts\apply-k8s-local.ps1 -Wait
kubectl get pods -n autocarehub
kubectl get svc -n autocarehub
kubectl get hpa -n autocarehub
```

Se aparecer `the server has asked for the client to provide credentials`, explique que o `kubectl` esta apontando para
um cluster sem login ou credencial expirada. A correcao e selecionar um contexto valido, por exemplo
`docker-desktop`, `kind-autocarehub` ou `minikube`, antes de aplicar.

Se estiver usando Terraform para criar base e banco, aplique somente workloads da aplicacao depois:

```powershell
kubectl apply -f k8s/backend-deployment.yaml `
  -f k8s/backend-service.yaml `
  -f k8s/backend-hpa.yaml `
  -f k8s/frontend-deployment.yaml `
  -f k8s/frontend-service.yaml `
  -f k8s/frontend-hpa.yaml
```

Falar:

> Os HPAs usam `autoscaling/v2` e escalam por CPU e memoria. O Metrics Server precisa estar instalado para calcular as
> metricas em tempo real.

### 7. Terraform

Mostrar arquivos:

```text
infra/main.tf
infra/variables.tf
infra/outputs.tf
infra/README.md
```

Comandos:

```powershell
cd C:\Users\ybarc\git\SOAT-FIAP\infra
$env:TF_VAR_postgres_password = "senha-local-segura"
$env:TF_VAR_jwt_secret = "segredo-local-com-pelo-menos-32-bytes"
$env:TF_VAR_external_service_token = "token-local-dos-webhooks"
terraform init
terraform fmt -check
terraform validate
terraform plan
```

Se for demonstrar aplicando localmente com `kind`:

```powershell
terraform apply -auto-approve -var="create_kind_cluster=true"
```

Falar:

> O Terraform provisiona o cluster local `kind` opcional, namespace, ConfigMap, Secret, PVC e o PostgreSQL
> demonstrativo com Deployment e Service. Os workloads da aplicacao ficam em YAML no diretorio `k8s`.

### 8. CI/CD

Mostrar:

- [.github/workflows/phase2-ci-cd.yml](../../.github/workflows/phase2-ci-cd.yml);
- [docs/CI_CD.md](../CI_CD.md);
- tela do GitHub Actions.

Falar:

> A pipeline executa build e testes do backend, lint/build/audit do frontend, valida Docker Compose, cria imagens Docker,
> cria um cluster `kind` temporario, carrega as imagens no cluster, executa Terraform para base e banco, aplica os
> manifests da aplicacao e valida rollout.

No GitHub Actions, abrir o job `local-kubernetes-deploy` e mostrar logs de:

```text
Create local Kubernetes cluster
Load images into local cluster
Provision Kubernetes base with Terraform
Apply Kubernetes workloads
Validate rollout
```

### 9. Escalabilidade automatica

Opcao A, mais simples para o video: mostrar HPA e explicar a metrica:

```powershell
kubectl get hpa -n autocarehub
kubectl describe hpa autocarehub-api -n autocarehub
kubectl describe hpa autocarehub-web -n autocarehub
```

Opcao B, se o Metrics Server estiver funcionando, simular carga:

```powershell
kubectl run autocarehub-load --rm -i --tty --image=busybox:1.36 --restart=Never -n autocarehub -- `
  /bin/sh -c "while true; do wget -q -O- http://backend:8080/actuator/health >/dev/null; done"
```

Em outro terminal:

```powershell
kubectl get hpa -n autocarehub --watch
kubectl get deploy -n autocarehub
```

Opcao C, se preferir simular pelo requisito de multiplas OS:

```powershell
.\scripts\load-test-service-orders.ps1 -Requests 100 -Concurrency 10 -GenerateBudget
```

Falar:

> A escalabilidade automatica esta declarada nos HPAs de backend e frontend por CPU e memoria. Em ambiente com Metrics
> Server, o Kubernetes ajusta replicas entre minimo e maximo conforme consumo.

### 10. Fechamento

Mostrar:

- checklist do README;
- [docs/PHASE2_DELIVERY_DOCUMENT.md](../PHASE2_DELIVERY_DOCUMENT.md);
- link do video ainda a preencher antes da entrega;
- PDF final que sera enviado no Portal do Aluno.

Falar:

> A entrega final inclui o link do repositorio compartilhado com `soat-architecture`, desenho da arquitetura e link do
> video publicado no YouTube ou Vimeo.

## Comandos rapidos para copiar durante a gravacao

```powershell
cd C:\Users\ybarc\git\SOAT-FIAP
.\scripts\start-local.ps1 -Rebuild -Reset
docker compose ps
```

```powershell
cd C:\Users\ybarc\git\SOAT-FIAP\backend
mvn clean verify
```

```powershell
cd C:\Users\ybarc\git\SOAT-FIAP\infra
terraform fmt -check
terraform init -backend=false
terraform validate
```

```powershell
cd C:\Users\ybarc\git\SOAT-FIAP
kubectl get pods -n autocarehub
kubectl get svc -n autocarehub
kubectl get hpa -n autocarehub
```

```powershell
cd C:\Users\ybarc\git\SOAT-FIAP
.\scripts\load-test-service-orders.ps1 -Requests 100 -Concurrency 10 -GenerateBudget
```

## Plano B se algo demorar

- Se `mvn clean verify` demorar, mostre o comando e o resultado anterior documentado no README: `mvn clean test`
  passou com 146 testes e `mvn clean verify` soma mais 26 testes de integracao pelo Failsafe.
- Se Kubernetes local não estiver pronto, mostre a execucao do GitHub Actions no job `local-kubernetes-deploy`.
- Se o HPA não mostrar aumento em tempo real, mostre os arquivos `backend-hpa.yaml` e `frontend-hpa.yaml`, mais
  `kubectl describe hpa`, explicando que a escala depende do Metrics Server e de carga suficiente.
- Se o Swagger estiver lento, use a collection Postman versionada em
  [docs/api/postman/autocarehub-phase2.postman_collection.json](../api/postman/autocarehub-phase2.postman_collection.json).
