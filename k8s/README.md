# Kubernetes - AutoCare Hub

Manifestos demonstraveis da Fase 2 para executar o AutoCare Hub em um cluster Kubernetes local ou academico.

## Recursos

- Namespace `autocarehub`.
- ConfigMap com configuracoes nao sensiveis.
- Secret com placeholders seguros.
- PostgreSQL com PVC, Deployment e Service.
- Backend com Deployment, Service e HPA por CPU/memoria.
- Frontend com Deployment, Service e HPA por CPU.

## Antes de aplicar

Substitua os placeholders em `02-secret.yaml` por valores seguros do ambiente. Nao versione secrets reais.

Se usar imagens locais em `kind` ou `minikube`, ajuste as imagens em:

```text
k8s/04-backend.yaml
k8s/05-frontend.yaml
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
kubectl port-forward -n autocarehub svc/autocarehub-api 8080:8080
kubectl port-forward -n autocarehub svc/autocarehub-web 5173:8080
```

