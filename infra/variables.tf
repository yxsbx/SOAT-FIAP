variable "kubeconfig_path" {
  description = "Caminho do kubeconfig usado pelo Terraform."
  type        = string
  default     = "~/.kube/config"
}

variable "kubeconfig_context" {
  description = "Contexto Kubernetes usado pelo Terraform. Use null para o contexto atual."
  type        = string
  default     = null
}

variable "namespace" {
  description = "Namespace Kubernetes do AutoCare Hub."
  type        = string
  default     = "autocarehub"
}

variable "postgres_db" {
  description = "Nome do banco PostgreSQL."
  type        = string
  default     = "autocarehub"
}

variable "postgres_user" {
  description = "Usuario do PostgreSQL."
  type        = string
  default     = "autocarehub"
}

variable "postgres_password" {
  description = "Senha do PostgreSQL. Informe via TF_VAR_postgres_password ou terraform.tfvars local."
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "Segredo JWT com pelo menos 32 bytes. Informe via TF_VAR_jwt_secret ou terraform.tfvars local."
  type        = string
  sensitive   = true
}

variable "jwt_expiration_minutes" {
  description = "Tempo de expiracao do JWT em minutos."
  type        = number
  default     = 60
}

variable "spring_profile" {
  description = "Perfil Spring usado no cluster academico."
  type        = string
  default     = "local"
}

variable "cors_allowed_origins" {
  description = "Origens liberadas no CORS."
  type        = string
  default     = "http://localhost:5173,http://127.0.0.1:5173,http://autocarehub-web"
}

variable "java_opts" {
  description = "Opcoes de JVM para o backend."
  type        = string
  default     = "-Xms256m -Xmx512m"
}

