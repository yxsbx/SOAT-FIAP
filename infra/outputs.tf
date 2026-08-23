output "namespace" {
  description = "Namespace criado para o AutoCare Hub."
  value       = kubernetes_namespace.autocarehub.metadata[0].name
}

output "kind_cluster_name" {
  description = "Cluster kind criado pelo Terraform quando create_kind_cluster=true; null quando o cluster ja existia."
  value       = var.create_kind_cluster ? var.kind_cluster_name : null
}

output "config_map_name" {
  description = "ConfigMap criado pelo Terraform."
  value       = kubernetes_config_map.autocarehub.metadata[0].name
}

output "secret_name" {
  description = "Secret criada pelo Terraform."
  value       = kubernetes_secret.autocarehub.metadata[0].name
}

output "postgres_pvc_name" {
  description = "PVC criado para persistencia do PostgreSQL demonstrativo."
  value       = kubernetes_persistent_volume_claim.postgres_data.metadata[0].name
}

output "postgres_jdbc_url" {
  description = "URL JDBC configurada para o backend acessar o PostgreSQL no cluster."
  value       = kubernetes_config_map.autocarehub.data.DB_URL
}

output "kubectl_apply_workloads_command" {
  description = "Comando para aplicar os workloads Kubernetes apos o provisionamento base."
  value       = "kubectl apply -f ../k8s/04-postgres-deployment.yaml -f ../k8s/05-postgres-service.yaml -f ../k8s/06-backend-deployment.yaml -f ../k8s/07-backend-service.yaml -f ../k8s/08-backend-hpa.yaml -f ../k8s/09-frontend-deployment.yaml -f ../k8s/10-frontend-service.yaml -f ../k8s/11-frontend-hpa.yaml"
}
