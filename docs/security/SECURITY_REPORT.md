# Relatório de segurança - AutoCare Hub Fase 2

## Objetivo

Registrar a revisão de segurança do AutoCare Hub para a Fase 2 sem inventar resultados de scan. Este documento separa:

- controles confirmados por revisão de código/configuração;
- comandos reexecutados nesta rodada;
- evidências versionadas já existentes em `security-reports/`;
- pendências de ferramenta ou ambiente.

## Escopo revisado

| Item                               | Resultado da revisão                                                                                                                                |
|------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| JWT                                | Implementado com JJWT, segredo obrigatório via `JWT_SECRET`/`security.jwt.secret`, mínimo de 32 bytes é expiração configurável.                     |
| CORS                               | Configuravel por `APP_CORS_ALLOWED_ORIGINS`; `SecurityConfig` rejeita origem `*` e `null`.                                                          |
| Secrets no repositório             | Não foi identificado secret real em `.env.example`, manifestos ou workflows; arquivos reais continuam fora do versionamento.                        |
| `.env` ignorado                    | `.gitignore` ignora `.env` e `.env.*`, mantendo `!.env.example`.                                                                                    |
| `.env.example` seguro              | Usa placeholders para senha do banco é segredo JWT.                                                                                                 |
| Kubernetes Secrets                 | `k8s/secret.example.yaml` contem placeholders; `phase2-ci-cd.yml` cria o Secret real a partir de GitHub Actions Secrets.                            |
| Variáveis sensíveis fora do código | Banco, JWT e Terraform sensível dependem de variáveis de ambiente, secrets do CI/CD ou `TF_VAR_*`.                                                  |
| CPF/CNPJ                           | `Document` normaliza e valida dígitos verificadores de CPF/CNPJ.                                                                                    |
| Placa                              | `Plate` aceita placas antigas e Mercosul, normalizando para alfanumérico maiúsculo.                                                                 |
| Payloads                           | OpenAPI gerado aplica `@Valid`, Bean Validation, limites de tamanho/paginação é padroes para documento/placa; Jackson rejeita campos desconhecidos. |
| Autorização por perfil             | `SecurityConfig` diferencia `ADMIN`, `EMPLOYEE` e `CUSTOMER`; testes de segurança existem.                                                          |
| Escopo empresa/cliente             | `AuthorizationService` restringe acesso a cliente/OS pelo `customerId`; política de usuários restringe gestão por empresa.                          |
| Pipeline                           | Workflows usam placeholders de CI ou GitHub Actions Secrets; não ha `echo` de valores sensíveis.                                                    |
| `.gitignore`                       | Atualizado para cobrir arquivos locais de ambiente, chaves, certificados, kubeconfig é estado Terraform.                                            |

## Correcoes aplicadas nesta revisão

| Risco                                                                                                                  | Correcao                                                                                                   |
|------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------|
| Arquivos locais sensíveis fora do padrão `.env` poderiam ser adicionados por engano.                                   | `.gitignore` passou a ignorar `.envrc`, chaves/certificados locais é kubeconfig local.                     |
| Relatório anterior misturava scans antigos com linguagem de resultado final atual.                                     | Este relatório foi reescrito para separar evidências versionadas, comandos reexecutados é pendências.      |
| Guia de scans não orientava claramente o caso de lock do Dependency-Check nem execução via Docker para Gitleaks/Trivy. | `SECURITY_SCAN_GUIDE.md` foi atualizado com comandos, pré-requisitos é política de registro de evidências. |

Não houve mudanca de regra de segurança no código de negocio nesta rodada; portanto, não foram criados testes novos.

## Comandos executados nesta rodada

| Comando                                                       | Resultado real                                                                                       |
|---------------------------------------------------------------|------------------------------------------------------------------------------------------------------|
| `npm audit --audit-level=high` em `frontend/`                 | Sucesso: `found 0 vulnerabilities`.                                                                  |
| `npm audit --json` em `frontend/` após as correções da Fase 2 | Sucesso: resultado final com 0 vulnerabilidades.                                                     |
| `gitleaks version`                                            | Falhou: `gitleaks` não está instalado no PATH local.                                                 |
| `trivy --version`                                             | Falhou: `trivy` não está instalado no PATH local.                                                    |
| `docker run --rm zricethezav/gitleaks:latest version`         | Falhou: Docker daemon não estava em execução.                                                        |
| `docker run --rm aquasec/trivy:latest --version`              | Falhou: Docker daemon não estava em execução.                                                        |
| `mvn dependency-check:check -DautoUpdate=false`               | Bloqueado por `odc.update.lock`; processo encerrado após confirmar que era a tentativa desta rodada. |

## Evidências versionadas existentes

As evidências abaixo existem no repositório e foram lidas nesta revisão. Elas não substituem reexecução atual quando o
ambiente permitir, mas servem como histórico versionado da entrega.

| Evidencia                                                            | Resultado lido                                                                         |
|----------------------------------------------------------------------|----------------------------------------------------------------------------------------|
| `security-reports/backend-dependencies/dependency-check-report.json` | 103 dependências; 0 dependências com vulnerabilidades no JSON versionado.              |
| `security-reports/frontend-dependencies/npm-audit-report.json`       | `metadata.vulnerabilities.total = 0`.                                                  |
| `security-reports/secrets/gitleaks.json`                             | 0 entradas.                                                                            |
| `security-reports/docker/docker-scout-cves.txt`                      | Backend com 0 criticas, 0 altas é 1 media (`CVE-2026-54515`) no relatório versionado.  |
| `security-reports/docker/docker-scout-frontend-cves.txt`             | Frontend com 0 criticas, 0 altas é 1 media (`CVE-2025-60876`) no relatório versionado. |

## Riscos restantes

| Risco                                                                                            | Status                                                                                                                            |
|--------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| Dependency-Check não reexecutado nesta rodada por lock local do OWASP no cache Maven.            | Pendente: reexecutar quando o lock terminar ou em ambiente limpo de CI.                                                           |
| Gitleaks não reexecutado nesta rodada por ausência da ferramenta é Docker daemon parado.         | Pendente: instalar Gitleaks ou iniciar Docker é executar via container.                                                           |
| Trivy não reexecutado nesta rodada por ausência da ferramenta é Docker daemon parado.            | Pendente: instalar Trivy ou iniciar Docker é executar via container.                                                              |
| Imagens Docker versionadas ainda possuem 1 CVE media cada em relatórios Docker Scout anteriores. | Residual: reválidar com Trivy/Docker Scout quando Docker estiver ativo é atualizar imagem/dependencia se houver fix.              |
| Swagger/OpenAPI habilitado por padrão para ambiente local.                                       | Aceito para avaliação acadêmica; em produção usar `SPRINGDOC_API_DOCS_ENABLED=false` e restringir exposicao.                      |
| JWT no frontend demonstrativo fica em `localStorage`.                                            | Aceito no MVP acadêmico; mitigado por autorização server-side é ausência de `v-html`/`innerHTML`/`eval` identificado no frontend. |

## Checklist final

| Controle                                                                  | Status                                                      |
|---------------------------------------------------------------------------|-------------------------------------------------------------|
| JWT com segredo externo é mínimo de 32 bytes                              | OK                                                          |
| CORS sem wildcard em produção/configuração                                | OK                                                          |
| Secrets reais fora do repositório                                         | OK na revisão local; Gitleaks atual pendente por ferramenta |
| `.env` ignorado                                                           | OK                                                          |
| `.env.example` seguro                                                     | OK                                                          |
| Kubernetes Secrets com placeholders no repo                               | OK                                                          |
| Variáveis sensíveis fora do código                                        | OK                                                          |
| CPF/CNPJ válidado                                                         | OK                                                          |
| Placa válidada                                                            | OK                                                          |
| Payloads válidados                                                        | OK                                                          |
| Autorização por perfil                                                    | OK                                                          |
| Acesso restrito por empresa/cliente                                       | OK                                                          |
| Dependências frontend sem vulnerabilidades altas/criticas na rodada atual | OK                                                          |
| Dependências backend reválidadas na rodada atual                          | Pendente por lock do Dependency-Check                       |
| Pipeline sem exposicao de segredos                                        | OK                                                          |
| `.gitignore` revisado                                                     | OK                                                          |

## Pendências

1. Reexecutar `mvn dependency-check:check` quando o lock `odc.update.lock` não estiver ativo.
2. Iniciar Docker Desktop ou instalar Gitleaks/Trivy localmente para reexecutar:
   - `gitleaks detect --source . --report-format json --report-path security-reports/secrets/gitleaks.json`;
   - `trivy image autocarehub-api:local`;
   - `trivy image autocarehub-web:local`.
3. Reavaliar as CVEs medias dos relatórios Docker Scout versionados após novo scan de imagem.
