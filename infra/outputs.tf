output "namespace" {
  description = "Namespace criado para o AutoCare Hub."
  value       = kubernetes_namespace.autocarehub.metadata[0].name
}

output "config_map_name" {
  description = "ConfigMap criado pelo Terraform."
  value       = kubernetes_config_map.autocarehub.metadata[0].name
}

output "secret_name" {
  description = "Secret criada pelo Terraform."
  value       = kubernetes_secret.autocarehub.metadata[0].name
}

