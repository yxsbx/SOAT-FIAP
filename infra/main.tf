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

    labels = {
      app        = "autocarehub"
      managed-by = "terraform"
    }
  }
}

resource "kubernetes_config_map" "autocarehub" {
  metadata {
    name      = "autocarehub-config"
    namespace = kubernetes_namespace.autocarehub.metadata[0].name

    labels = {
      app        = "autocarehub"
      managed-by = "terraform"
    }
  }

  data = {
    POSTGRES_DB                 = var.postgres_db
    POSTGRES_USER               = var.postgres_user
    PGDATA                      = var.postgres_data_directory
    DB_URL                      = "jdbc:postgresql://${var.postgres_service_name}:5432/${var.postgres_db}"
    DB_USERNAME                 = var.postgres_user
    JWT_EXPIRATION_MINUTES      = tostring(var.jwt_expiration_minutes)
    SERVER_PORT                 = tostring(var.backend_port)
    SPRING_PROFILES_ACTIVE      = var.spring_profile
    APP_CORS_ALLOWED_ORIGINS    = var.cors_allowed_origins
    SPRINGDOC_API_DOCS_ENABLED  = tostring(var.springdoc_api_docs_enabled)
    JAVA_TOOL_OPTIONS           = var.java_tool_options
  }
}

resource "kubernetes_secret" "autocarehub" {
  metadata {
    name      = "autocarehub-secret"
    namespace = kubernetes_namespace.autocarehub.metadata[0].name

    labels = {
      app        = "autocarehub"
      managed-by = "terraform"
    }
  }

  data = {
    POSTGRES_PASSWORD = var.postgres_password
    DB_PASSWORD       = var.postgres_password
    JWT_SECRET        = var.jwt_secret
  }

  type = "Opaque"
}

resource "kubernetes_persistent_volume_claim" "postgres_data" {
  metadata {
    name      = var.postgres_pvc_name
    namespace = kubernetes_namespace.autocarehub.metadata[0].name

    labels = {
      app        = "autocarehub"
      component  = "database"
      managed-by = "terraform"
    }
  }

  spec {
    access_modes = ["ReadWriteOnce"]

    resources {
      requests = {
        storage = var.postgres_storage_size
      }
    }

    storage_class_name = var.postgres_storage_class_name
  }
}
