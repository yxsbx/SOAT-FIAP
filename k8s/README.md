# Kubernetes - AutoCare Hub

Manifestos demonstraveis da Fase 2 para executar o AutoCare Hub em um cluster Kubernetes local ou academico.

## Recursos

- Namespace `autocarehub`.
- ConfigMap com configuracoes nao sensiveis.
- Secret com placeholders seguros.
- PostgreSQL com PVC, Deployment e Service.
- Backend com Deployment, Service e HPA por CPU/memoria.
- Frontend com Deployment, Service e HPA por CPU.

## Arquivos

| Arquivo | Funcao |
| ------- | ------ |
| `00-namespace.yaml` | Namespace `autocarehub`. |
| `01-configmap.yaml` | Variaveis nao sensiveis da API, PostgreSQL e CORS. |
| `02-secret.yaml` | Placeholders de senha do banco e segredo JWT. |
| `03-postgres-pvc.yaml` | Volume persistente do PostgreSQL. |
| `04-postgres-deployment.yaml` | Pod do PostgreSQL com probes e limites. |
| `05-postgres-service.yaml` | DNS interno `autocarehub-postgres`. |
| `06-backend-deployment.yaml` | Pods da API Spring Boot. |
| `07-backend-service.yaml` | DNS interno `backend`, usado pelo proxy Nginx do frontend. |
| `08-backend-hpa.yaml` | Escala automatica da API por CPU e memoria. |
| `09-frontend-deployment.yaml` | Pods do frontend Nginx/Vue. |
| `10-frontend-service.yaml` | Service do frontend demonstrativo. |
| `11-frontend-hpa.yaml` | Escala automatica do frontend por CPU. |

## Antes de aplicar

Substitua os placeholders em `02-secret.yaml` por valores seguros do ambiente. Nao versione secrets reais.

As imagens padrao apontam para GHCR:

```text
ghcr.io/yxsbx/autocarehub-api:latest
ghcr.io/yxsbx/autocarehub-web:latest
```

Se usar imagens locais em `kind` ou `minikube`, ajuste as imagens em:

```text
k8s/06-backend-deployment.yaml
k8s/09-frontend-deployment.yaml
```

## Comandos

```bash
kubectl apply -f k8s/
kubectl get pods -n autocarehub
kubectl get svc -n autocarehub
kubectl get hpa -n autocarehub
kubectl logs -n autocarehub deploy/autocarehub-api
kubectl delete -f k8s/
```

Para demonstrar acesso local:

```bash
kubectl port-forward -n autocarehub svc/backend 8080:8080
kubectl port-forward -n autocarehub svc/autocarehub-web 5173:8080
```

## Validacao sem aplicar no cluster

```bash
kubectl apply --dry-run=client -f k8s/
```

## Limitacoes

- O Secret usa placeholders e deve ser substituido fora do repositorio antes de um deploy real.
- O PostgreSQL em Kubernetes e apenas demonstrativo; ambientes produtivos devem avaliar backup, replicacao e banco gerenciado.
- O HPA depende de Metrics Server instalado no cluster.
- Para clusters locais, as imagens precisam existir no registry ou ser carregadas no runtime do cluster.
