# Infraestrutura - AutoCare Hub

Estrutura Terraform da Fase 2 para provisionamento academico/local de Kubernetes e recursos base da aplicação.

## Decisão de ambiente

Este projeto não assume uma cloud especifica. Para evitar inventar AWS, Azure ou GCP sem evidencia no repositorio, a
infraestrutura foi modelada para um cluster Kubernetes local ou academico. O modo padrão usa um cluster ja existente,
como `kind`, `minikube` ou um cluster disponibilizado para demonstração; opcionalmente, o Terraform cria um cluster local
com `kind`.

O Terraform pode criar:

- cluster Kubernetes local com `kind`, quando `create_kind_cluster=true`;
- namespace `autocarehub`;
- ConfigMap com variáveis não sensíveis;
- Secret com valores sensíveis recebidos por variáveis;
- PVC do PostgreSQL demonstrativo.

Os workloads da aplicação ficam em [../k8s](../k8s) e podem ser aplicados depois do provisionamento base. Esta estrutura
não assume cloud especifica.

## Arquivos

| Arquivo | Função |
| ------- | ------ |
| `main.tf` | Criação opcional de cluster `kind`, provider Kubernetes e recursos base: Namespace, ConfigMap, Secret e PVC. |
| `variables.tf` | Variáveis parametrizáveis do ambiente local/acadêmico. |
| `outputs.tf` | Outputs uteis para conferencia e proximo passo de deploy. |
| `terraform.tfvars.example` | Exemplo com placeholders seguros. |

## Variáveis sensíveis

Não versione `terraform.tfvars` com valores reais. Use variáveis de ambiente:

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

As variáveis possuem validações básicas para evitar namespace inválido, porta fora da faixa permitida, tamanho de
storage sem unidade reconhecida, senha fraca do PostgreSQL e segredo JWT menor que 32 caracteres.

## Comandos

```bash
terraform init
terraform fmt
terraform validate
terraform plan
terraform apply
terraform destroy
```

Para provisionar tambem um cluster local com `kind`, instale Docker Desktop e `kind`, depois execute:

```bash
terraform apply -var="create_kind_cluster=true"
```

Use esse modo quando o cluster ainda não existir. Se o cluster `kind` ja existir, use o modo padrão com
`create_kind_cluster=false` e aponte `kubeconfig_path`/`kubeconfig_context` para ele.

Depois de aplicar a infraestrutura base, aplique apenas os workloads que não são gerenciados pelo Terraform:

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

Se preferir não usar Terraform, use diretamente o fluxo Kubernetes completo documentado em [../k8s/README.md](../k8s/README.md):

```bash
kubectl apply -f ../k8s/
```

Não misture os dois modos aplicando `../k8s/02-secret.yaml` depois do Terraform, pois esse arquivo contem placeholders.

## Limitações

- O modo `kind` cria cluster local; não cria cluster gerenciado em cloud.
- Não cria banco gerenciado em cloud.
- Provisiona apenas a infraestrutura base do banco demonstrativo no cluster: PVC e variáveis de conexao.
- Usa o contexto Kubernetes configurado localmente.
- Secrets reais devem ser fornecidos apenas por ambiente seguro ou pela plataforma de CI/CD.
- O HPA dos workloads depende de Metrics Server instalado no cluster.
