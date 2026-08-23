# Relatório de infraestrutura e CI/CD - Fase 2

## Arquivos criados/alterados

| Status   | Item                                                                                                                           |
|----------|--------------------------------------------------------------------------------------------------------------------------------|
| ATENDIDO | `Dockerfile`, `docker-compose.yml`, `.env.example` e `pom.xml` na raiz para comandos diretos da entrega.                       |
| ATENDIDO | `k8s/` com manifestos Kubernetes e README.                                                                                     |
| ATENDIDO | `infra/` com `main.tf`, `variables.tf`, `outputs.tf`, `versions.tf`, `terraform.tfvars.example` e README.                      |
| ATENDIDO | `infra/.terraform.lock.hcl` gerado por `terraform init` para fixar providers.                                                  |
| ATENDIDO | `.github/workflows/phase2-ci-cd.yml`.                                                                                          |
| ATENDIDO | `docs/PHASE2_DELIVERY_DOCUMENT.md`.                                                                                            |
| ATENDIDO | `docs/PHASE2_INFRA_CICD_REPORT.md`.                                                                                            |
| ATENDIDO | Estruturas duplicadas antigas removidas: `deploy/`, `infra/terraform/`, `backend/Dockerfile` e `.github/workflows/deploy.yml`. |
| ATENDIDO | PDF antigo de entrega removido; o PDF final deve ser gerado fora do repositório após inserir o link real do vídeo.             |

## Docker

| Status   | Item                                                                                                        |
|----------|-------------------------------------------------------------------------------------------------------------|
| ATENDIDO | Backend roda em container com build multi-stage Java 21.                                                    |
| ATENDIDO | Frontend demonstrativo roda em container Nginx unprivileged.                                                |
| ATENDIDO | PostgreSQL sobe no ambiente local via Docker Compose.                                                       |
| ATENDIDO | Variáveis sensíveis vêm de `.env`; `.env.example` usa placeholders.                                         |
| ATENDIDO | `.env` e variantes reais estão no `.gitignore`.                                                             |
| ATENDIDO | `docker compose up -d --build` executado fora do sandbox; backend, frontend e PostgreSQL ficaram `healthy`. |
| ATENDIDO | Docker Compose consolidado em `docker-compose.yml` na raiz; a pasta antiga `deploy/docker/` foi removida.   |

## Kubernetes

| Status              | Item                                                                                        |
|---------------------|---------------------------------------------------------------------------------------------|
| ATENDIDO            | Namespace, ConfigMap, Secret de exemplo, PostgreSQL, backend e frontend declarados.         |
| ATENDIDO            | Services para backend, frontend e PostgreSQL.                                               |
| ATENDIDO            | HPA do backend e frontend com CPU e memória.                                                |
| ATENDIDO            | Requests e limits em backend, frontend e PostgreSQL.                                        |
| ATENDIDO            | Probes HTTP do backend em endpoints reais do Actuator.                                      |
| ATENDIDO            | Manifests consolidados somente em `k8s/`; a pasta antiga `deploy/kubernetes/` foi removida. |
| VALIDAR MANUALMENTE | HPA depende de Metrics Server instalado.                                                    |
| ATENDIDO            | Imagens usam tags locais e são carregadas no cluster `kind` durante o CI/CD.                |

## Terraform

| Status   | Item                                                                                           |
|----------|------------------------------------------------------------------------------------------------|
| ATENDIDO | Terraform local cria cluster `kind` opcional.                                                  |
| ATENDIDO | Terraform cria Namespace, ConfigMap, Secret e PVC.                                             |
| ATENDIDO | Banco provisionado como PostgreSQL demonstrativo dentro do cluster local via Terraform.        |
| ATENDIDO | Variáveis sensíveis exigidas por `TF_VAR_*` ou `terraform.tfvars` local ignorado pelo Git.     |
| ATENDIDO | `terraform init` executado fora do sandbox e providers baixados com sucesso.                   |
| ATENDIDO | Terraform consolidado diretamente em `infra/`; a pasta antiga `infra/terraform/` foi removida. |

## CI/CD

| Status              | Item                                                                                                                    |
|---------------------|-------------------------------------------------------------------------------------------------------------------------|
| ATENDIDO            | Workflow da Fase 2 faz checkout, setup Java 21, cache Maven, testes e `mvn verify`.                                     |
| ATENDIDO            | Workflow faz setup Node, `npm ci`, lint, build e `npm audit`.                                                           |
| ATENDIDO            | Workflow válida Docker Compose e constrói imagens backend/frontend.                                                     |
| ATENDIDO            | Deploy Kubernetes cria cluster `kind` temporário, aplica Terraform e aplica manifestos no CI/CD.                        |
| ATENDIDO            | Workflow duplicado antigo `.github/workflows/deploy.yml` removido; o workflow principal da Fase 2 e `phase2-ci-cd.yml`. |

## README

| Status           | Item                                                                                    |
|------------------|-----------------------------------------------------------------------------------------|
| ATENDIDO         | README documenta Fase 2, Docker, Kubernetes, Terraform, CI/CD, APIs, vídeo e PDF final. |
| BLOQUEIA ENTREGA | Link do vídeo deve ser preenchido antes da entrega final no portal.                     |
| ATENDIDO         | Acesso do `soat-architecture` foi confirmado no GitHub.                                 |

## Comandos executados

| Status              | Comando                                                      | Resultado                                                                                                                                                              |
|---------------------|--------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ATENDIDO            | `mvn spotless:check`                                         | Passou na raiz após adicionar `pom.xml` agregador com plugin Spotless.                                                                                                 |
| ATENDIDO            | `mvn test`                                                   | Passou na raiz: 172 testes, 0 falhas, 0 erros e 0 ignorados.                                                                                                           |
| ATENDIDO            | `cd backend; mvn test`                                       | Passou: 172 testes, 0 falhas, 0 erros e 0 ignorados.                                                                                                                   |
| ATENDIDO            | `mvn clean verify`                                           | Passou com 172 testes, 0 falhas, 0 erros, 0 ignorados; gate JaCoCo aprovado.                                                                                           |
| ATENDIDO            | `cd frontend; npm ci`                                        | Passou: 141 pacotes instalados/auditados, 0 vulnerabilidades.                                                                                                          |
| ATENDIDO            | `cd frontend; npm run lint`                                  | Passou com ESLint e `--max-warnings=0`.                                                                                                                                |
| ATENDIDO            | `cd frontend; npm run build`                                 | Falhou no sandbox com `spawn EPERM`; passou fora do sandbox com Vite, gerando `dist/`.                                                                                 |
| ATENDIDO            | `cd frontend; npm audit --json`                              | Passou: 0 vulnerabilidades totais.                                                                                                                                     |
| ATENDIDO            | `docker compose config --quiet`                              | Passou com variáveis temporárias de exemplo.                                                                                                                           |
| ATENDIDO            | `Copy-Item .env.example .env; docker compose config --quiet` | Passou após consolidar Docker Compose na raiz e criar `.env` local a partir do exemplo.                                                                                |
| ATENDIDO            | `docker compose down`                                        | Passou fora do sandbox.                                                                                                                                                |
| ATENDIDO            | `docker compose down --remove-orphans`                       | Passou fora do sandbox.                                                                                                                                                |
| ATENDIDO            | `docker compose down -v`                                     | Passou fora do sandbox e removeu o volume local do Compose.                                                                                                            |
| ATENDIDO            | `docker compose up -d --build`                               | Construiu imagens e subiu containers; primeira tentativa encontrou conflito com containers antigos, resolvido antes da consolidação final para Docker Compose na raiz. |
| ATENDIDO            | `docker compose ps`                                          | PostgreSQL, `app` e frontend ficaram `Up` e `healthy`; portas 5432, 8080 e 5173 expostas.                                                                              |
| ATENDIDO            | `docker compose logs --tail=100 app`                         | Backend iniciou com Spring Boot, conectou ao PostgreSQL 16.13 e aplicou 1 migration Flyway com sucesso.                                                                |
| VALIDAR MANUALMENTE | `kubectl apply --dry-run=client -f k8s/`                     | Falhou porque o cluster/kubeconfig local pediu credenciais; não foi possível validar contra API Kubernetes local.                                                      |
| VALIDAR MANUALMENTE | `kubectl apply --dry-run=client --validate=false -f k8s/`    | Também falhou por credenciais do cluster ao reconhecer recursos.                                                                                                       |
| VALIDAR MANUALMENTE | Validador estrutural Ruby dos YAMLs                          | Não executado localmente porque `ruby` não está instalado no PATH; a checagem existe no workflow `phase2-ci-cd.yml`.                                                   |
| ATENDIDO            | `cd infra; terraform fmt -check`                             | Passou.                                                                                                                                                                |
| ATENDIDO            | `cd infra; terraform init`                                   | Falhou no sandbox por proxy `127.0.0.1:9`; passou fora do sandbox e baixou `hashicorp/kubernetes v2.38.0` e `hashicorp/null v3.3.1`.                                   |
| ATENDIDO            | `cd infra; terraform validate`                               | Passou: configuração válida.                                                                                                                                           |
| ATENDIDO            | Consolidação de Terraform                                    | Estrutura duplicada antiga removida; a entrega versionada fica em `infra/`.                                                                                            |

## Comandos não executados por limitação do ambiente

| Status              | Item                                                                                            |
|---------------------|-------------------------------------------------------------------------------------------------|
| ATENDIDO            | Deploy Kubernetes automatizado demonstrado em cluster local/efêmero no GitHub Actions.          |
| VALIDAR MANUALMENTE | Validador estrutural Ruby de manifests não executado localmente porque Ruby não está instalado. |

## Pendências obrigatórias

| Status              | Item                                                                           |
|---------------------|--------------------------------------------------------------------------------|
| BLOQUEIA ENTREGA    | Inserir link real do vídeo demonstrativo.                                      |
| ATENDIDO            | Acesso do usuário `soat-architecture` ao repositório privado confirmado.       |
| VALIDAR MANUALMENTE | Regenerar PDF final no portal/documento final após inserir link real do vídeo. |

## Melhorias futuras

| Status          | Item                                                                         |
|-----------------|------------------------------------------------------------------------------|
| MELHORIA FUTURA | Avaliar banco gerenciado, backup e replicação para produção.                 |
| MELHORIA FUTURA | Automatizar scans adicionais de imagem e secrets em pipeline obrigatória.    |
| MELHORIA FUTURA | Adicionar ambiente cloud somente se houver exigência futura e credenciais institucionais. |

## Conclusão

| Status  | Item                                                                                                                                                                                   |
|---------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ATENDIDO | A infraestrutura local, Kubernetes, Terraform e CI/CD estão implementados para avaliação acadêmica; a entrega final ainda depende do link real do vídeo e da regeneração do PDF final. |
