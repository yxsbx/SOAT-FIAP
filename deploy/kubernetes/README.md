# Kubernetes - AutoCare Hub

Manifestos demonstráveis da Fase 2 para executar o AutoCare Hub em um cluster Kubernetes local ou academico.

## Recursos

- Namespace `autocarehub`.
- ConfigMap com configurações não sensíveis.
- Secret com placeholders seguros.
- PostgreSQL com PVC, Deployment e Service.
- Backend com Deployment, Service e HPA por CPU/memoria.
- Frontend com Deployment, Service e HPA por CPU/memoria.

## Arquivos

| Arquivo                       | Função                                                     |
|-------------------------------|------------------------------------------------------------|
| `00-namespace.yaml`           | Namespace `autocarehub`.                                   |
| `01-configmap.yaml`           | Variáveis não sensíveis da API, PostgreSQL e CORS.         |
| `02-secret.yaml`              | Placeholders de senha do banco e segredo JWT.              |
| `03-postgres-pvc.yaml`        | Volume persistente do PostgreSQL.                          |
| `04-postgres-deployment.yaml` | Pod do PostgreSQL com probes e limites.                    |
| `05-postgres-service.yaml`    | DNS interno `autocarehub-postgres`.                        |
| `06-backend-deployment.yaml`  | Pods da API Spring Boot.                                   |
| `07-backend-service.yaml`     | DNS interno `backend`, usado pelo proxy Nginx do frontend. |
| `08-backend-hpa.yaml`         | Escala automatica da API por CPU e memoria.                |
| `09-frontend-deployment.yaml` | Pods do frontend Nginx/Vue.                                |
| `10-frontend-service.yaml`    | Service do frontend demonstrativo.                         |
| `11-frontend-hpa.yaml`        | Escala automatica do frontend por CPU e memoria.           |

## Antes de aplicar

Substitua os placeholders em `02-secret.yaml` por valores seguros do ambiente. Não versione secrets reais.

As imagens padrão apontam para GHCR:

```text
ghcr.io/yxsbx/autocarehub-api:latest
ghcr.io/yxsbx/autocarehub-web:latest
```

Se usar imagens locais em `kind` ou `minikube`, ajuste as imagens em:

```text
deploy/kubernetes/06-backend-deployment.yaml
deploy/kubernetes/09-frontend-deployment.yaml
```

## Comandos

```bash
kubectl apply -f deploy/kubernetes/
kubectl get pods -n autocarehub
kubectl get svc -n autocarehub
kubectl get hpa -n autocarehub
kubectl logs -n autocarehub deploy/autocarehub-api
kubectl delete -f deploy/kubernetes/
```

Para demonstrar acesso local:

```bash
kubectl port-forward -n autocarehub svc/backend 8080:8080
kubectl port-forward -n autocarehub svc/autocarehub-web 5173:8080
```

## Validação sem aplicar no cluster

```bash
kubectl apply --dry-run=client -f deploy/kubernetes/
```

## Limitações

- O Secret usa placeholders e deve ser substituido fora do repositorio antes de um deploy real.
- O PostgreSQL em Kubernetes e apenas demonstrativo; ambientes produtivos devem avaliar backup, replicação e banco gerenciado.
- O HPA depende de Metrics Server instalado no cluster.
- Para clusters locais, as imagens precisam existir no registry ou ser carregadas no runtime do cluster.
