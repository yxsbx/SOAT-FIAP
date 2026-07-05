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
- PVC do PostgreSQL demonstrativo.

Os workloads da aplicacao ficam em [../k8s](../k8s) e podem ser aplicados depois do provisionamento base. Esta estrutura
nao cria cluster Kubernetes e nao assume cloud especifica.

## Arquivos

| Arquivo | Funcao |
| ------- | ------ |
| `main.tf` | Provider Kubernetes e recursos base: Namespace, ConfigMap, Secret e PVC. |
| `variables.tf` | Variaveis parametrizaveis do ambiente local/acadêmico. |
| `outputs.tf` | Outputs uteis para conferencia e proximo passo de deploy. |
| `terraform.tfvars.example` | Exemplo com placeholders seguros. |

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

Opcionalmente, copie o exemplo e substitua os placeholders localmente:

```bash
cp terraform.tfvars.example terraform.tfvars
```

O arquivo `terraform.tfvars` real deve ficar fora do versionamento.

## Comandos

```bash
terraform init
terraform fmt
terraform validate
terraform plan
terraform apply
terraform destroy
```

Depois de aplicar a infraestrutura base, aplique apenas os workloads que nao sao gerenciados pelo Terraform:

```bash
kubectl apply -f ../k8s/04-postgres-deployment.yaml \
  -f ../k8s/05-postgres-service.yaml \
  -f ../k8s/06-backend-deployment.yaml \
  -f ../k8s/07-backend-service.yaml \
  -f ../k8s/08-backend-hpa.yaml \
  -f ../k8s/09-frontend-deployment.yaml \
  -f ../k8s/10-frontend-service.yaml \
  -f ../k8s/11-frontend-hpa.yaml

kubectl get pods -n autocarehub
kubectl get svc -n autocarehub
kubectl get hpa -n autocarehub
```

Se preferir nao usar Terraform, use diretamente o fluxo Kubernetes completo documentado em [../k8s/README.md](../k8s/README.md):

```bash
kubectl apply -f ../k8s/
```

Nao misture os dois modos aplicando `../k8s/02-secret.yaml` depois do Terraform, pois esse arquivo contem placeholders.

## Limitacoes

- Nao cria cluster Kubernetes.
- Nao cria banco gerenciado em cloud.
- Provisiona apenas a infraestrutura base do banco demonstrativo no cluster: PVC e variaveis de conexao.
- Usa o contexto Kubernetes configurado localmente.
- Secrets reais devem ser fornecidos apenas por ambiente seguro ou pela plataforma de CI/CD.
- O HPA dos workloads depende de Metrics Server instalado no cluster.
