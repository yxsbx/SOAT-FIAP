# Infraestrutura - AutoCare Hub

Estrutura Terraform da Fase 2 para provisionamento academico/local de recursos base no Kubernetes.

## Decisao de ambiente

Este projeto nao assume uma cloud especifica. Para evitar inventar AWS, Azure ou GCP sem evidencia no repositorio, a
infraestrutura foi modelada para um cluster Kubernetes local ou academico ja existente, como `kind`, `minikube` ou um
cluster disponibilizado para demonstracao.

O Terraform cria:

- namespace `autocarehub`;
- ConfigMap com variaveis nao sensiveis;
- Secret com valores sensiveis recebidos por variaveis.

Os workloads da aplicacao ficam em [../k8s](../k8s) e podem ser aplicados depois do provisionamento base.

## Variaveis sensiveis

Nao versione `terraform.tfvars` com valores reais. Use variaveis de ambiente:

```bash
export TF_VAR_postgres_password="substituir-localmente"
export TF_VAR_jwt_secret="segredo-com-pelo-menos-32-bytes"
```

No PowerShell:

```powershell
$env:TF_VAR_postgres_password = "substituir-localmente"
$env:TF_VAR_jwt_secret = "segredo-com-pelo-menos-32-bytes"
```

## Comandos

```bash
terraform init
terraform fmt
terraform validate
terraform plan
terraform apply
terraform destroy
```

Depois de aplicar a infraestrutura base:

```bash
kubectl apply -f ../k8s/
kubectl get pods -n autocarehub
kubectl get svc -n autocarehub
kubectl get hpa -n autocarehub
```

## Limitacoes

- Nao cria cluster Kubernetes.
- Nao cria banco gerenciado em cloud.
- Usa o contexto Kubernetes configurado localmente.
- Secrets reais devem ser fornecidos apenas por ambiente seguro ou pela plataforma de CI/CD.

