# Terraform - AutoCare Hub

Infraestrutura como codigo da Fase 2 para provisionamento local ou academico de Kubernetes e recursos base do banco.

## Recursos Criados

| Status   | Recurso                                                                                                       |
|----------|---------------------------------------------------------------------------------------------------------------|
| ATENDIDO | Cluster Kubernetes local com `kind`, opcional, quando `create_kind_cluster=true`.                             |
| ATENDIDO | Namespace `autocarehub`.                                                                                      |
| ATENDIDO | ConfigMap `autocarehub-config` com variaveis não sensiveis.                                                   |
| ATENDIDO | Secret `autocarehub-secret` com senha do banco, JWT secret e token externo recebidos por variaveis sensiveis. |
| ATENDIDO | PVC `autocarehub-postgres-data` para o PostgreSQL demonstrativo executado no cluster.                         |
| ATENDIDO | Deployment e Service do PostgreSQL demonstrativo no Kubernetes local.                                         |

Os Deployments, Services e HPAs da aplicação ficam em `../k8s/` e sao aplicados depois do Terraform. O banco de dados
demonstrativo fica no Terraform para atender o requisito de IaC do banco sem contratar cloud paga.

## Pre-Requisitos

- Terraform 1.6 ou superior.
- `kubectl` configurado para um cluster local ou academico.
- Opcionalmente Docker Desktop e `kind`, se o Terraform for criar o cluster local.
- Metrics Server no cluster para funcionamento do HPA.

## Variaveis Sensiveis

Nao versione `terraform.tfvars` real. Use variaveis de ambiente:

```powershell
$env:TF_VAR_postgres_password = "substituir-localmente"
$env:TF_VAR_jwt_secret = "segredo-com-pelo-menos-32-bytes"
$env:TF_VAR_external_service_token = "token-local-dos-webhooks"
```

Ou copie o exemplo e substitua localmente:

```powershell
Copy-Item infra/terraform.tfvars.example infra/terraform.tfvars
```

O arquivo `terraform.tfvars` fica no `.gitignore`.

## Execucao

A partir da raiz:

```powershell
cd infra
terraform init
terraform fmt
terraform validate
terraform plan
terraform apply
terraform destroy
cd ..
```

Para criar tambem um cluster local com `kind`:

```powershell
cd infra
terraform apply -var="create_kind_cluster=true"
cd ..
```

## Relação Com Kubernetes

O Terraform prepara a base compartilhada e o banco PostgreSQL demonstrativo: namespace, configuracoes, secrets, PVC,
Deployment e Service do banco. Em seguida, aplique os workloads da aplicação:

```powershell
kubectl apply -f k8s/backend-deployment.yaml
kubectl apply -f k8s/backend-service.yaml
kubectl apply -f k8s/backend-hpa.yaml
kubectl apply -f k8s/frontend-deployment.yaml
kubectl apply -f k8s/frontend-service.yaml
kubectl apply -f k8s/frontend-hpa.yaml
```

Se preferir não usar Terraform, aplique o pacote completo:

```powershell
kubectl apply -f k8s/
kubectl get pods -n autocarehub
kubectl get svc -n autocarehub
kubectl get hpa -n autocarehub
```

Nesse modo completo, `k8s/secret.example.yaml` cria apenas placeholders. Substitua por secrets reais no ambiente antes
de considerar o deploy como real.

## Banco De Dados

O banco da Fase 2 e o PostgreSQL demonstrativo no Kubernetes local. O Terraform cria o PVC, as variaveis sensiveis, o
Deployment e o Service do banco. As migrations Flyway rodam no startup do backend.

O PVC usa `wait_until_bound = false` para evitar timeout em clusters locais, como `kind`, quando a StorageClass trabalha
com binding tardio (`WaitForFirstConsumer`). Nesses casos, o volume so fica `Bound` depois que o Pod do PostgreSQL e
agendado. A validacao real acontece no rollout do Deployment do PostgreSQL.

## Limitacoes

| Status              | Item                                                                            |
|---------------------|---------------------------------------------------------------------------------|
| VALIDAR MANUALMENTE | O cluster local precisa estar ativo e acessivel pelo kubeconfig.                |
| VALIDAR MANUALMENTE | O HPA depende de Metrics Server.                                                |
| MELHORIA FUTURA     | Para producao, avaliar banco gerenciado, backup, replicação e registry privado. |
