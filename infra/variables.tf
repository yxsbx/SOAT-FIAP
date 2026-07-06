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

  validation {
    condition     = can(regex("^[a-z0-9]([-a-z0-9]*[a-z0-9])?$", var.namespace))
    error_message = "O namespace deve seguir o padrão DNS label do Kubernetes."
  }
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

variable "postgres_service_name" {
  description = "Nome do Service Kubernetes usado pelo backend para acessar o PostgreSQL."
  type        = string
  default     = "autocarehub-postgres"
}

variable "postgres_pvc_name" {
  description = "Nome do PVC usado pelo PostgreSQL demonstrativo."
  type        = string
  default     = "autocarehub-postgres-data"
}

variable "postgres_storage_size" {
  description = "Tamanho solicitado para o volume persistente do PostgreSQL."
  type        = string
  default     = "1Gi"

  validation {
    condition     = can(regex("^[1-9][0-9]*(Mi|Gi|Ti)$", var.postgres_storage_size))
    error_message = "O tamanho do storage deve usar unidades Mi, Gi ou Ti, por exemplo 1Gi."
  }
}

variable "postgres_storage_class_name" {
  description = "StorageClass do PVC. Use null para deixar o cluster escolher a classe padrão."
  type        = string
  default     = null
}

variable "postgres_data_directory" {
  description = "Diretorio de dados usado pelo container PostgreSQL."
  type        = string
  default     = "/var/lib/postgresql/data/pgdata"
}

variable "postgres_password" {
  description = "Senha do PostgreSQL. Informe via TF_VAR_postgres_password ou terraform.tfvars local."
  type        = string
  sensitive   = true

  validation {
    condition     = length(var.postgres_password) >= 12
    error_message = "A senha do PostgreSQL deve ter pelo menos 12 caracteres."
  }
}

variable "jwt_secret" {
  description = "Segredo JWT com pelo menos 32 bytes. Informe via TF_VAR_jwt_secret ou terraform.tfvars local."
  type        = string
  sensitive   = true

  validation {
    condition     = length(var.jwt_secret) >= 32
    error_message = "O segredo JWT deve ter pelo menos 32 caracteres."
  }
}

variable "jwt_expiration_minutes" {
  description = "Tempo de expiração do JWT em minutos."
  type        = number
  default     = 60

  validation {
    condition     = var.jwt_expiration_minutes > 0
    error_message = "O tempo de expiração do JWT deve ser maior que zero."
  }
}

variable "backend_port" {
  description = "Porta HTTP interna do backend."
  type        = number
  default     = 8080

  validation {
    condition     = var.backend_port > 0 && var.backend_port <= 65535
    error_message = "A porta do backend deve estar entre 1 e 65535."
  }
}

variable "spring_profile" {
  description = "Perfil Spring usado no cluster academico."
  type        = string
  default     = "local"
}

variable "cors_allowed_origins" {
  description = "Origens liberadas no CORS."
  type        = string
  default     = "http://localhost:5173,http://127.0.0.1:5173,http://localhost:8080"
}

variable "springdoc_api_docs_enabled" {
  description = "Habilita o endpoint OpenAPI no ambiente academico."
  type        = bool
  default     = true
}

variable "java_tool_options" {
  description = "Opções de JVM para o backend."
  type        = string
  default     = "-Xms256m -Xmx512m"
}
