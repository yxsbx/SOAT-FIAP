# Relatorio de seguranca - AutoCare Hub Fase 2

## Objetivo

Registrar a revisao de seguranca do AutoCare Hub para a Fase 2 sem inventar resultados de scan. Este documento separa:

- controles confirmados por revisao de codigo/configuracao;
- comandos reexecutados nesta rodada;
- evidencias versionadas ja existentes em `security-reports/`;
- pendencias de ferramenta ou ambiente.

## Escopo revisado

| Item | Resultado da revisao |
| --- | --- |
| JWT | Implementado com JJWT, segredo obrigatorio via `JWT_SECRET`/`security.jwt.secret`, minimo de 32 bytes e expiracao configuravel. |
| CORS | Configuravel por `APP_CORS_ALLOWED_ORIGINS`; `SecurityConfig` rejeita origem `*` e `null`. |
| Secrets no repositorio | Nao foi identificado secret real em `.env.example`, manifestos ou workflows; arquivos reais continuam fora do versionamento. |
| `.env` ignorado | `.gitignore` ignora `.env` e `.env.*`, mantendo `!.env.example`. |
| `.env.example` seguro | Usa placeholders para senha do banco e segredo JWT. |
| Kubernetes Secrets | `k8s/02-secret.yaml` contem placeholders; `deploy.yml` cria o Secret real a partir de GitHub Actions Secrets. |
| Variaveis sensiveis fora do codigo | Banco, JWT e Terraform sensivel dependem de variaveis de ambiente, secrets do CI/CD ou `TF_VAR_*`. |
| CPF/CNPJ | `Document` normaliza e valida digitos verificadores de CPF/CNPJ. |
| Placa | `Plate` aceita placas antigas e Mercosul, normalizando para alfanumerico maiusculo. |
| Payloads | OpenAPI gerado aplica `@Valid`, Bean Validation, limites de tamanho/paginacao e padroes para documento/placa; Jackson rejeita campos desconhecidos. |
| Autorizacao por perfil | `SecurityConfig` diferencia `ADMIN`, `EMPLOYEE` e `CUSTOMER`; testes de seguranca existem. |
| Escopo empresa/cliente | `AuthorizationService` restringe acesso a cliente/OS pelo `customerId`; politica de usuarios restringe gestao por empresa. |
| Pipeline | Workflows usam placeholders de CI ou GitHub Actions Secrets; nao ha `echo` de valores sensiveis. |
| `.gitignore` | Atualizado para cobrir arquivos locais de ambiente, chaves, certificados, kubeconfig e estado Terraform. |

## Correcoes aplicadas nesta revisao

| Risco | Correcao |
| --- | --- |
| Arquivos locais sensiveis fora do padrao `.env` poderiam ser adicionados por engano. | `.gitignore` passou a ignorar `.envrc`, chaves/certificados locais e kubeconfig local. |
| Relatorio anterior misturava scans antigos com linguagem de resultado final atual. | Este relatorio foi reescrito para separar evidencias versionadas, comandos reexecutados e pendencias. |
| Guia de scans nao orientava claramente o caso de lock do Dependency-Check nem execucao via Docker para Gitleaks/Trivy. | `SECURITY_SCAN_GUIDE.md` foi atualizado com comandos, pre-requisitos e politica de registro de evidencias. |

Nao houve mudanca de regra de seguranca no codigo de negocio nesta rodada; portanto, nao foram criados testes novos.

## Comandos executados nesta rodada

| Comando | Resultado real |
| --- | --- |
| `npm audit --audit-level=high` em `frontend/` | Sucesso: `found 0 vulnerabilities`. |
| `gitleaks version` | Falhou: `gitleaks` nao esta instalado no PATH local. |
| `trivy --version` | Falhou: `trivy` nao esta instalado no PATH local. |
| `docker run --rm zricethezav/gitleaks:latest version` | Falhou: Docker daemon nao estava em execucao. |
| `docker run --rm aquasec/trivy:latest --version` | Falhou: Docker daemon nao estava em execucao. |
| `mvn dependency-check:check -DautoUpdate=false` | Bloqueado por `odc.update.lock`; processo encerrado apos confirmar que era a tentativa desta rodada. |

## Evidencias versionadas existentes

As evidencias abaixo existem no repositorio e foram lidas nesta revisao. Elas nao substituem reexecucao atual quando o
ambiente permitir, mas servem como historico versionado da entrega.

| Evidencia | Resultado lido |
| --- | --- |
| `security-reports/backend-dependencies/dependency-check-report.json` | 103 dependencias; 0 dependencias com vulnerabilidades no JSON versionado. |
| `security-reports/frontend-dependencies/npm-audit-report.json` | `metadata.vulnerabilities.total = 0`. |
| `security-reports/secrets/gitleaks.json` | 0 entradas. |
| `security-reports/docker/docker-scout-cves.txt` | Backend com 0 criticas, 0 altas e 1 media (`CVE-2026-54515`) no relatorio versionado. |
| `security-reports/docker/docker-scout-frontend-cves.txt` | Frontend com 0 criticas, 0 altas e 1 media (`CVE-2025-60876`) no relatorio versionado. |

## Riscos restantes

| Risco | Status |
| --- | --- |
| Dependency-Check nao reexecutado nesta rodada por lock local do OWASP no cache Maven. | Pendente: reexecutar quando o lock terminar ou em ambiente limpo de CI. |
| Gitleaks nao reexecutado nesta rodada por ausencia da ferramenta e Docker daemon parado. | Pendente: instalar Gitleaks ou iniciar Docker e executar via container. |
| Trivy nao reexecutado nesta rodada por ausencia da ferramenta e Docker daemon parado. | Pendente: instalar Trivy ou iniciar Docker e executar via container. |
| Imagens Docker versionadas ainda possuem 1 CVE media cada em relatorios Docker Scout anteriores. | Residual: revalidar com Trivy/Docker Scout quando Docker estiver ativo e atualizar imagem/dependencia se houver fix. |
| Swagger/OpenAPI habilitado por padrao para ambiente local. | Aceito para avaliacao academica; em producao usar `SPRINGDOC_API_DOCS_ENABLED=false` e restringir exposicao. |
| JWT no frontend demonstrativo fica em `localStorage`. | Aceito no MVP academico; mitigado por autorizacao server-side e ausencia de `v-html`/`innerHTML`/`eval` identificado no frontend. |

## Checklist final

| Controle | Status |
| --- | --- |
| JWT com segredo externo e minimo de 32 bytes | OK |
| CORS sem wildcard em producao/configuracao | OK |
| Secrets reais fora do repositorio | OK na revisao local; Gitleaks atual pendente por ferramenta |
| `.env` ignorado | OK |
| `.env.example` seguro | OK |
| Kubernetes Secrets com placeholders no repo | OK |
| Variaveis sensiveis fora do codigo | OK |
| CPF/CNPJ validado | OK |
| Placa validada | OK |
| Payloads validados | OK |
| Autorizacao por perfil | OK |
| Acesso restrito por empresa/cliente | OK |
| Dependencias frontend sem vulnerabilidades altas/criticas na rodada atual | OK |
| Dependencias backend revalidadas na rodada atual | Pendente por lock do Dependency-Check |
| Pipeline sem exposicao de segredos | OK |
| `.gitignore` revisado | OK |

## Pendencias

1. Reexecutar `mvn dependency-check:check` quando o lock `odc.update.lock` nao estiver ativo.
2. Iniciar Docker Desktop ou instalar Gitleaks/Trivy localmente para reexecutar:
   - `gitleaks detect --source . --report-format json --report-path security-reports/secrets/gitleaks.json`;
   - `trivy image autocarehub-api:local`;
   - `trivy image autocarehub-web:local`.
3. Reavaliar as CVEs medias dos relatorios Docker Scout versionados apos novo scan de imagem.
