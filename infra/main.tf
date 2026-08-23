resource "null_resource" "kind_cluster" {
  count = var.create_kind_cluster ? 1 : 0

  triggers = {
    cluster_name = var.kind_cluster_name
    kubeconfig   = pathexpand(var.kubeconfig_path)
  }

  provisioner "local-exec" {
    command = "kind create cluster --name ${var.kind_cluster_name} --kubeconfig ${pathexpand(var.kubeconfig_path)}"
  }

  provisioner "local-exec" {
    when    = destroy
    command = "kind delete cluster --name ${self.triggers.cluster_name}"
  }
}

provider "kubernetes" {
  config_path    = var.kubeconfig_path
  config_context = var.kubeconfig_context
}

resource "kubernetes_namespace" "autocarehub" {
  depends_on = [null_resource.kind_cluster]

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
    POSTGRES_DB                               = var.postgres_db
    POSTGRES_USER                             = var.postgres_user
    PGDATA                                    = var.postgres_data_directory
    DB_URL                                    = "jdbc:postgresql://${var.postgres_service_name}:5432/${var.postgres_db}"
    DB_USERNAME                               = var.postgres_user
    JWT_EXPIRATION_MINUTES                    = tostring(var.jwt_expiration_minutes)
    SERVER_PORT                               = tostring(var.backend_port)
    SPRING_PROFILES_ACTIVE                    = var.spring_profile
    APP_CORS_ALLOWED_ORIGINS                  = var.cors_allowed_origins
    SPRINGDOC_API_DOCS_ENABLED                = tostring(var.springdoc_api_docs_enabled)
    MANAGEMENT_ENDPOINT_HEALTH_PROBES_ENABLED = "true"
    JAVA_TOOL_OPTIONS                         = var.java_tool_options
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
    POSTGRES_PASSWORD      = var.postgres_password
    DB_PASSWORD            = var.postgres_password
    JWT_SECRET             = var.jwt_secret
    EXTERNAL_SERVICE_TOKEN = var.external_service_token
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

resource "kubernetes_deployment" "postgres" {
  metadata {
    name      = "autocarehub-postgres"
    namespace = kubernetes_namespace.autocarehub.metadata[0].name

    labels = {
      app        = "autocarehub-postgres"
      component  = "database"
      managed-by = "terraform"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "autocarehub-postgres"
      }
    }

    template {
      metadata {
        labels = {
          app = "autocarehub-postgres"
        }
      }

      spec {
        container {
          name              = "postgres"
          image             = "postgres:16"
          image_pull_policy = "IfNotPresent"

          port {
            name           = "postgres"
            container_port = 5432
          }

          env_from {
            config_map_ref {
              name = kubernetes_config_map.autocarehub.metadata[0].name
            }
          }

          env_from {
            secret_ref {
              name = kubernetes_secret.autocarehub.metadata[0].name
            }
          }

          volume_mount {
            name       = "data"
            mount_path = "/var/lib/postgresql/data"
          }

          readiness_probe {
            exec {
              command = ["sh", "-c", "pg_isready -U \"$POSTGRES_USER\" -d \"$POSTGRES_DB\""]
            }

            initial_delay_seconds = 10
            period_seconds        = 10
          }

          liveness_probe {
            exec {
              command = ["sh", "-c", "pg_isready -U \"$POSTGRES_USER\" -d \"$POSTGRES_DB\""]
            }

            initial_delay_seconds = 30
            period_seconds        = 20
          }

          resources {
            requests = {
              cpu    = "100m"
              memory = "256Mi"
            }

            limits = {
              cpu    = "500m"
              memory = "512Mi"
            }
          }
        }

        volume {
          name = "data"

          persistent_volume_claim {
            claim_name = kubernetes_persistent_volume_claim.postgres_data.metadata[0].name
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "postgres" {
  metadata {
    name      = var.postgres_service_name
    namespace = kubernetes_namespace.autocarehub.metadata[0].name

    labels = {
      app        = "autocarehub-postgres"
      component  = "database"
      managed-by = "terraform"
    }
  }

  spec {
    selector = {
      app = "autocarehub-postgres"
    }

    port {
      name        = "postgres"
      port        = 5432
      target_port = "postgres"
    }

    type = "ClusterIP"
  }
}
