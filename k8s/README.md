# Kubernetes - AutoCare Hub

Manifestos da Fase 2 para deploy local ou academico do AutoCare Hub em Kubernetes.

## Recursos

| Status              | Recurso                                                                                        |
|---------------------|------------------------------------------------------------------------------------------------|
| ATENDIDO            | Namespace `autocarehub`.                                                                       |
| ATENDIDO            | ConfigMap com variaveis não sensiveis.                                                         |
| ATENDIDO            | Secret de exemplo com placeholders, sem secrets reais.                                         |
| ATENDIDO            | PostgreSQL demonstrativo no cluster com PVC, Deployment e Service.                             |
| ATENDIDO            | Backend com Deployment, Service, probes, requests/limits e HPA por CPU/memoria.                |
| ATENDIDO            | Frontend demonstrativo com Deployment, Service, probes, requests/limits e HPA por CPU/memoria. |
| VALIDAR MANUALMENTE | Metrics Server precisa existir no cluster para o HPA calcular metricas.                        |
| ATENDIDO            | Imagens usam tags locais e devem ser carregadas no runtime do cluster local.                   |

## Aplicação

Antes de aplicar, confirme que o `kubectl` esta autenticado em um cluster local, como Docker Desktop, kind ou minikube.
O erro abaixo indica problema de contexto/credencial do Kubernetes, nao erro de YAML:

```text
the server has asked for the client to provide credentials
```

No Docker Desktop, habilite Kubernetes nas configuracoes e selecione o contexto:

```powershell
kubectl config get-contexts
kubectl config use-context docker-desktop
kubectl get nodes
```

O caminho recomendado para deploy local e usar o script abaixo. Ele constroi e tagueia as imagens locais esperadas pelos
manifests (`autocarehub-api:local` e `autocarehub-web:local`), aplica os manifests em ordem, cria o Secret real a partir
do `.env` local ou variaveis de ambiente e nao aplica `secret.example.yaml` com placeholders:

```powershell
.\scripts\apply-k8s-local.ps1 -Wait
kubectl get pods -n autocarehub
kubectl get svc -n autocarehub
kubectl get hpa -n autocarehub
```

Se quiser aplicar manualmente, substitua os placeholders em `secret.example.yaml` por valores reais seguros no ambiente
de execucao, ou crie o Secret com `kubectl create secret generic`. Nao versione secrets reais.

Para acesso local:

```powershell
kubectl port-forward -n autocarehub svc/backend 8080:8080
kubectl port-forward -n autocarehub svc/autocarehub-web 5173:8080
```

Para validar os manifestos sem criar recursos:

```powershell
kubectl apply --dry-run=client -f k8s/namespace.yaml
kubectl apply --dry-run=client -f k8s/configmap.yaml
kubectl apply --dry-run=client -f k8s/postgres-service.yaml
kubectl apply --dry-run=client -f k8s/postgres-deployment.yaml
kubectl apply --dry-run=client -f k8s/backend-service.yaml
kubectl apply --dry-run=client -f k8s/backend-deployment.yaml
kubectl apply --dry-run=client -f k8s/backend-hpa.yaml
kubectl apply --dry-run=client -f k8s/frontend-service.yaml
kubectl apply --dry-run=client -f k8s/frontend-deployment.yaml
kubectl apply --dry-run=client -f k8s/frontend-hpa.yaml
```

O PostgreSQL aqui e demonstrativo para avaliação academica. Em producao, avaliar banco gerenciado, backup, replicação e
politicas de armazenamento.
