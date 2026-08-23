# Kubernetes - AutoCare Hub

Manifestos da Fase 2 para deploy local ou academico do AutoCare Hub em Kubernetes.

## Recursos

| Status | Recurso |
| --- | --- |
| ATENDIDO | Namespace `autocarehub`. |
| ATENDIDO | ConfigMap com variaveis nao sensiveis. |
| ATENDIDO | Secret de exemplo com placeholders, sem secrets reais. |
| ATENDIDO | PostgreSQL demonstrativo no cluster com PVC, Deployment e Service. |
| ATENDIDO | Backend com Deployment, Service, probes, requests/limits e HPA por CPU/memoria. |
| ATENDIDO | Frontend demonstrativo com Deployment, Service, probes, requests/limits e HPA por CPU/memoria. |
| VALIDAR MANUALMENTE | Metrics Server precisa existir no cluster para o HPA calcular metricas. |
| VALIDAR MANUALMENTE | Imagens precisam estar publicadas no registry ou carregadas no cluster local. |

## Aplicação

Antes de aplicar, substitua os placeholders em `secret.example.yaml` por valores reais seguros no ambiente de execucao.
Nao versione secrets reais.

```powershell
kubectl apply -f k8s/
kubectl get pods -n autocarehub
kubectl get svc -n autocarehub
kubectl get hpa -n autocarehub
```

Para acesso local:

```powershell
kubectl port-forward -n autocarehub svc/backend 8080:8080
kubectl port-forward -n autocarehub svc/autocarehub-web 5173:8080
```

Para validar os manifestos sem criar recursos:

```powershell
kubectl apply --dry-run=client -f k8s/
```

O PostgreSQL aqui e demonstrativo para avaliação academica. Em producao, avaliar banco gerenciado, backup, replicação e
politicas de armazenamento.
