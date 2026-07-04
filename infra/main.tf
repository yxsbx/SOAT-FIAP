terraform {
  required_version = ">= 1.6.0"

  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.36"
    }
  }
}

provider "kubernetes" {
  config_path    = var.kubeconfig_path
  config_context = var.kubeconfig_context
}

resource "kubernetes_namespace" "autocarehub" {
  metadata {
    name = var.namespace
  }
}

resource "kubernetes_config_map" "autocarehub" {
  metadata {
    name      = "autocarehub-config"
    namespace = kubernetes_namespace.autocarehub.metadata[0].name
  }

  data = {
    POSTGRES_DB              = var.postgres_db
    POSTGRES_USER            = var.postgres_user
    DB_URL                   = "jdbc:postgresql://autocarehub-postgres:5432/${var.postgres_db}"
    DB_USERNAME              = var.postgres_user
    JWT_EXPIRATION_MINUTES   = tostring(var.jwt_expiration_minutes)
    SERVER_PORT              = "8080"
    SPRING_PROFILES_ACTIVE   = var.spring_profile
    APP_CORS_ALLOWED_ORIGINS = var.cors_allowed_origins
    SPRINGDOC_API_DOCS_ENABLED = "true"
    JAVA_OPTS                = var.java_opts
  }
}

resource "kubernetes_secret" "autocarehub" {
  metadata {
    name      = "autocarehub-secret"
    namespace = kubernetes_namespace.autocarehub.metadata[0].name
  }

  data = {
    POSTGRES_PASSWORD = var.postgres_password
    DB_PASSWORD       = var.postgres_password
    JWT_SECRET        = var.jwt_secret
  }

  type = "Opaque"
}

