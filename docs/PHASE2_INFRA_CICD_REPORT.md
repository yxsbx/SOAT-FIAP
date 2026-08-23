# Relatorio De Infraestrutura E CI/CD - Fase 2

## Arquivos Criados/Alterados

| Status | Item |
| --- | --- |
| ATENDIDO | `Dockerfile`, `docker-compose.yml`, `.env.example` e `pom.xml` na raiz para comandos diretos da entrega. |
| ATENDIDO | `k8s/` com manifestos Kubernetes e README. |
| ATENDIDO | `infra/` com `main.tf`, `variables.tf`, `outputs.tf`, `versions.tf`, `terraform.tfvars.example` e README. |
| ATENDIDO | `infra/.terraform.lock.hcl` gerado por `terraform init` para fixar providers. |
| ATENDIDO | `.github/workflows/phase2-ci-cd.yml`. |
| ATENDIDO | `docs/PHASE2_DELIVERY_DOCUMENT.md`. |
| ATENDIDO | `docs/PHASE2_INFRA_CICD_REPORT.md`. |
| ATENDIDO | Estruturas duplicadas antigas removidas: `deploy/`, `infra/terraform/`, `backend/Dockerfile` e `.github/workflows/deploy.yml`. |
| ATENDIDO | PDF antigo de entrega removido; o PDF final deve ser gerado fora do repositório apos inserir o link real do video. |

## Docker

| Status | Item |
| --- | --- |
| ATENDIDO | Backend roda em container com build multi-stage Java 21. |
| ATENDIDO | Frontend demonstrativo roda em container Nginx unprivileged. |
| ATENDIDO | PostgreSQL sobe no ambiente local via Docker Compose. |
| ATENDIDO | Variaveis sensiveis vêm de `.env`; `.env.example` usa placeholders. |
| ATENDIDO | `.env` e variantes reais estao no `.gitignore`. |
| ATENDIDO | `docker compose up -d --build` executado fora do sandbox; backend, frontend e PostgreSQL ficaram `healthy`. |
| ATENDIDO | Docker Compose consolidado em `docker-compose.yml` na raiz; a pasta antiga `deploy/docker/` foi removida. |

## Kubernetes

| Status | Item |
| --- | --- |
| ATENDIDO | Namespace, ConfigMap, Secret de exemplo, PostgreSQL, backend e frontend declarados. |
| ATENDIDO | Services para backend, frontend e PostgreSQL. |
| ATENDIDO | HPA do backend e frontend com CPU e memoria. |
| ATENDIDO | Requests e limits em backend, frontend e PostgreSQL. |
| ATENDIDO | Probes HTTP do backend em endpoints reais do Actuator. |
| ATENDIDO | Manifests consolidados somente em `k8s/`; a pasta antiga `deploy/kubernetes/` foi removida. |
| VALIDAR MANUALMENTE | HPA depende de Metrics Server instalado. |
| VALIDAR MANUALMENTE | Imagens precisam estar disponiveis no registry ou carregadas no cluster local. |

## Terraform

| Status | Item |
| --- | --- |
| ATENDIDO | Terraform local cria cluster `kind` opcional. |
| ATENDIDO | Terraform cria Namespace, ConfigMap, Secret e PVC. |
| PARCIAL | Banco provisionado como PostgreSQL dentro do cluster local, nao como banco gerenciado. |
| ATENDIDO | Variaveis sensiveis exigidas por `TF_VAR_*` ou `terraform.tfvars` local ignorado pelo Git. |
| ATENDIDO | `terraform init` executado fora do sandbox e providers baixados com sucesso. |
| ATENDIDO | Terraform consolidado diretamente em `infra/`; a pasta antiga `infra/terraform/` foi removida. |

## CI/CD

| Status | Item |
| --- | --- |
| ATENDIDO | Workflow da Fase 2 faz checkout, setup Java 21, cache Maven, testes e `mvn verify`. |
| ATENDIDO | Workflow faz setup Node, `npm ci`, lint, build e `npm audit`. |
| ATENDIDO | Workflow valida Docker Compose e constrói imagens backend/frontend. |
| ATENDIDO | Deploy Kubernetes aplica manifestos e cria Secret real a partir de GitHub Actions Secrets. |
| ATENDIDO | Workflow duplicado antigo `.github/workflows/deploy.yml` removido; o workflow principal da Fase 2 e `phase2-ci-cd.yml`. |
| VALIDAR MANUALMENTE | Deploy real depende de `KUBE_CONFIG`, `POSTGRES_PASSWORD`, `JWT_SECRET` e `EXTERNAL_SERVICE_TOKEN`. |

## README

| Status | Item |
| --- | --- |
| ATENDIDO | README documenta Fase 2, Docker, Kubernetes, Terraform, CI/CD, APIs, video e PDF final. |
| BLOQUEIA ENTREGA | Link do video deve ser preenchido antes da entrega final no portal. |
| VALIDAR MANUALMENTE | Acesso do `soat-architecture` deve ser confirmado no GitHub. |

## Comandos Executados

| Status | Comando | Resultado |
| --- | --- | --- |
| ATENDIDO | `mvn spotless:check` | Passou na raiz apos adicionar `pom.xml` agregador com plugin Spotless. |
| ATENDIDO | `mvn test` | Passou na raiz: 172 testes, 0 falhas, 0 erros e 0 ignorados. |
| ATENDIDO | `cd backend; mvn test` | Passou: 172 testes, 0 falhas, 0 erros e 0 ignorados. |
| PARCIAL | `mvn clean verify` | Falhou no ambiente local Windows durante leitura de fontes gerados em `backend/target/generated-sources/openapi` apos `clean`; a tentativa foi contaminada por execucoes Maven concorrentes e depois persistiu como erro local de leitura. Reexecutar em terminal limpo/CI antes da entrega final. |
| ATENDIDO | `cd frontend; npm ci` | Passou: 141 pacotes instalados/auditados, 0 vulnerabilidades. |
| ATENDIDO | `cd frontend; npm run lint` | Passou com ESLint e `--max-warnings=0`. |
| ATENDIDO | `cd frontend; npm run build` | Falhou no sandbox com `spawn EPERM`; passou fora do sandbox com Vite, gerando `dist/`. |
| ATENDIDO | `cd frontend; npm audit --json` | Passou: 0 vulnerabilidades totais. |
| ATENDIDO | `docker compose config --quiet` | Passou com variaveis temporarias de exemplo. |
| ATENDIDO | `Copy-Item .env.example .env; docker compose config --quiet` | Passou apos consolidar Docker Compose na raiz e criar `.env` local a partir do exemplo. |
| ATENDIDO | `docker compose down` | Passou fora do sandbox. |
| ATENDIDO | `docker compose down --remove-orphans` | Passou fora do sandbox. |
| ATENDIDO | `docker compose down -v` | Passou fora do sandbox e removeu o volume local do Compose. |
| ATENDIDO | `docker compose up -d --build` | Construiu imagens e subiu containers; primeira tentativa encontrou conflito com containers antigos, resolvido antes da consolidação final para Docker Compose na raiz. |
| ATENDIDO | `docker compose ps` | PostgreSQL, `app` e frontend ficaram `Up` e `healthy`; portas 5432, 8080 e 5173 expostas. |
| ATENDIDO | `docker compose logs --tail=100 app` | Backend iniciou com Spring Boot, conectou ao PostgreSQL 16.13 e aplicou 1 migration Flyway com sucesso. |
| VALIDAR MANUALMENTE | `kubectl apply --dry-run=client -f k8s/` | Falhou porque o cluster/kubeconfig local pediu credenciais; nao foi possivel validar contra API Kubernetes local. |
| VALIDAR MANUALMENTE | `kubectl apply --dry-run=client --validate=false -f k8s/` | Tambem falhou por credenciais do cluster ao reconhecer recursos. |
| VALIDAR MANUALMENTE | Validador estrutural Ruby dos YAMLs | Nao executado localmente porque `ruby` nao esta instalado no PATH; a checagem existe no workflow `phase2-ci-cd.yml`. |
| ATENDIDO | `cd infra; terraform fmt -check` | Passou. |
| ATENDIDO | `cd infra; terraform init` | Falhou no sandbox por proxy `127.0.0.1:9`; passou fora do sandbox e baixou `hashicorp/kubernetes v2.38.0` e `hashicorp/null v3.3.1`. |
| ATENDIDO | `cd infra; terraform validate` | Passou: configuração valida. |
| ATENDIDO | Consolidação de Terraform | Estrutura duplicada antiga removida; a entrega versionada fica em `infra/`. |

## Comandos Nao Executados Por Limitação Do Ambiente

| Status | Item |
| --- | --- |
| VALIDAR MANUALMENTE | Deploy Kubernetes real nao executado porque o kubeconfig local exige credenciais. |
| VALIDAR MANUALMENTE | Validador estrutural Ruby de manifests nao executado localmente porque Ruby nao esta instalado. |
| PARCIAL | `mvn clean verify` deve ser reexecutado em terminal limpo ou CI; `mvn test` passou, mas o gate JaCoCo desta rodada nao foi confirmado por causa do erro local de leitura em `target/generated-sources/openapi`. |

## Pendencias Obrigatorias

| Status | Item |
| --- | --- |
| BLOQUEIA ENTREGA | Inserir link real do video demonstrativo. |
| PARCIAL | Reexecutar `mvn clean verify` em ambiente limpo/CI para registrar gate JaCoCo final desta rodada. |
| VALIDAR MANUALMENTE | Confirmar acesso do usuario `soat-architecture` ao repositorio privado. |
| VALIDAR MANUALMENTE | Regenerar PDF final no portal/documento final apos inserir link real do video. |

## Melhorias Futuras

| Status | Item |
| --- | --- |
| MELHORIA FUTURA | Avaliar banco gerenciado, backup e replicação para producao. |
| MELHORIA FUTURA | Automatizar scans adicionais de imagem e secrets em pipeline obrigatoria. |
| MELHORIA FUTURA | Adicionar ambiente cloud quando houver decisao e credenciais institucionais. |

## Conclusao

| Status | Item |
| --- | --- |
| PARCIAL | A infraestrutura local, Kubernetes, Terraform e CI/CD estao implementados para avaliação academica; a entrega final ainda depende do link real do video e da confirmação manual de acesso ao repositorio. |
