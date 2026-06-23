# Relatorio de Vulnerabilidades - AutoCare Hub

## 1. Titulo

Relatorio de Vulnerabilidades do projeto AutoCare Hub - Tech Challenge FIAP.

## 2. Objetivo da analise

Registrar a analise de seguranca realizada no projeto AutoCare Hub com base nos resultados reais do OWASP Dependency-Check. O documento consolida vulnerabilidades encontradas, correcoes aplicadas, evidencias, riscos ainda nao analisados e recomendacoes futuras.

Este relatorio nao inventa resultados de ferramentas nao executadas. Quando nao houve evidencia de scan, o item permanece marcado como "Nao executado".

## 3. Escopo analisado

| Item | Status | Observação |
| --- | --- | --- |
| Backend Spring Boot | Analisado | Dependencias Maven analisadas pelo OWASP Dependency-Check. |
| Dependencias backend | Analisado e corrigido | Relatorios em `target/dependency-check/dependency-check-report.html` e `target/dependency-check/dependency-check-report.json`. |
| Frontend Vue/Vite | Analisado | Dependencias npm analisadas por `npm audit` e codigo incluído no Semgrep. |
| Dependencias frontend | Analisado e corrigido | Relatorio em `security-reports/frontend-dependencies/npm-audit-report.json`. |
| Dockerfiles | Analisados e corrigidos | Docker Scout executado nas imagens finais de backend e frontend. |
| docker-compose.yml | Revisado em execução | Containers validados como non-root, read-only e com `no-new-privileges`. |
| Secrets | Analisado | Gitleaks executado sobre 36 commits, sem leaks encontrados. |
| OpenAPI/Swagger | Analisado indiretamente | Vulnerabilidades anteriores em Swagger UI foram corrigidas por atualização de versao. |
| Cobertura de testes | Analisado | Relatorio JaCoCo em `target/site/jacoco/index.html` e `target/site/jacoco/jacoco.csv`. |

Item fora do escopo desta versao:

- teste dinamico de seguranca dedicado contra a API em execução.

## 4. Data da analise

| Campo | Valor |
| --- | --- |
| Data de consolidação | 20/06/2026 |
| Horario do relatorio Dependency-Check | 17:07:53 UTC |
| Responsavel | Yasmin Barcelos Pires - RM370897 |
| Branch final de entrega | `main` |
| Commit técnico validado | `dbed819` |

## 5. Ferramentas utilizadas

| Ferramenta | Versao | Finalidade | Comando executado | Saida/relatorio |
| --- | --- | --- | --- | --- |
| OWASP Dependency-Check Maven Plugin | 12.1.1 | Dependencias backend | `mvn dependency-check:check -DautoUpdate=false` | `target/dependency-check/dependency-check-report.html` e `target/dependency-check/dependency-check-report.json` |
| JaCoCo | 0.8.12 | Cobertura de testes | `mvn test` | `target/site/jacoco/index.html` e `target/site/jacoco/jacoco.csv` |
| npm audit | 9.6.6 | Dependencias frontend | `npm audit --json` | `security-reports/frontend-dependencies/npm-audit-report.json` |
| Docker Scout | 1.20.4 | Imagens Docker | `docker scout cves <imagem> --only-severity critical,high,medium` | `security-reports/docker/docker-scout-cves.txt` e `security-reports/docker/docker-scout-frontend-cves.txt` |
| Semgrep | 1.166.0 | Analise estatica Java/JavaScript/Dockerfile | Regras `p/java`, `p/javascript` e `p/security-audit` | `security-reports/static-analysis/semgrep.json` |
| Gitleaks | Imagem Docker latest em 20/06/2026 | Secrets no historico Git | `gitleaks detect` | `security-reports/secrets/gitleaks.json` |

## 6. Ambiente analisado

| Campo | Valor |
| --- | --- |
| Sistema operacional | Windows, execução local em PowerShell |
| Java | Java 21 |
| Maven | Maven local do projeto |
| Banco usado no scan | Nao aplicavel ao Dependency-Check |
| Perfil Spring | Nao aplicavel ao Dependency-Check |
| Observacoes | O relatorio JSON informa `engineVersion` 12.1.1 e data `2026-06-20T17:07:53.263950500Z`. |

## 7. Resumo executivo

O primeiro scan de dependencias backend apontou vulnerabilidades em bibliotecas centrais do runtime, incluindo Spring Boot, Spring Framework, Spring Security, Tomcat, PostgreSQL JDBC, Log4j API, Commons Compress, Commons Lang e Swagger UI. O scan inicial do frontend apontou vulnerabilidades altas transitivas em Vite/esbuild. Em 20/06/2026, um novo `npm audit` identificou uma vulnerabilidade moderada transitiva em `js-yaml`, corrigida com atualização do lockfile.

O primeiro Docker Scout do backend encontrou 1 vulnerabilidade critica, 2 altas e 9 medias em
`/usr/bin/pebble`, presente na imagem base Ubuntu. O runtime foi migrado para
`gcr.io/distroless/java21-debian12:nonroot`, eliminando os achados. No frontend, a imagem Nginx
1.27/Alpine inicialmente apresentou 75 CVEs. A troca para `mainline-alpine-slim`, fixada por digest,
reduziu o resultado para 0 criticas, 0 altas e 1 media em BusyBox, sem versao corrigida indicada pelo
scanner em 20/06/2026.

As dependencias foram atualizadas e o OWASP Dependency-Check foi executado novamente. No scan final, o resultado foi:

| Indicador | Resultado |
| --- | ---: |
| Dependencias analisadas | 126 |
| Artefatos vulneraveis | 0 |
| Ocorrencias de vulnerabilidades | 0 |
| Achados unicos | 0 |
| Excecoes de analise | 0 |
| Status do build do Dependency-Check | Sucesso |

Os scans finais não apresentam vulnerabilidades críticas ou altas, nem leaks ou achados estáticos.
Permanece 1 CVE média aceita temporariamente na imagem frontend.

## 8. Resultado geral dos scans

| Categoria | Critical | High | Medium | Low | Unknown | Status geral |
| --- | ---: | ---: | ---: | ---: | ---: | --- |
| Dependencias backend | 0 | 0 | 0 | 0 | 0 | Corrigido |
| Dependencias frontend | 0 | 0 | 0 | 0 | 0 | Corrigido |
| Imagem backend | 0 | 0 | 0 | 0 | 0 | Corrigido |
| Imagem frontend | 0 | 0 | 1 | 0 | 0 | Risco médio aceito temporariamente |
| Analise estatica | 0 | 0 | 0 | 0 | 0 | Semgrep sem achados |
| Secrets | 0 | 0 | 0 | 0 | 0 | Gitleaks sem leaks |

## 9. Cobertura de testes

O relatório JaCoCo mede domínio, aplicação, controllers REST, segurança, mappers e adapters de
persistência. São excluídos apenas o bootstrap da aplicação, classes geradas automaticamente pelo
OpenAPI e records estruturais de comando, consulta e saída sem lógica própria. O gate executado por
`mvn verify` exige no mínimo 90% de instruções, linhas e branches.

| Metrica | Coberto | Nao coberto | Cobertura |
| --- | ---: | ---: | ---: |
| Instrucoes | 9.752 | 397 | 96,09% |
| Branches | 457 | 49 | 90,32% |
| Linhas | 2.440 | 75 | 97,02% |
| Metodos | 634 | 36 | 94,63% |

Resultado validado com 145 testes automatizados e `mvn verify` concluindo com sucesso.

## 10. Tabela de vulnerabilidades encontradas

| ID | Ferramenta | Severidade | Arquivo/Pacote afetado | Descrição | Impacto | Correção aplicada | Status | Evidencia |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| VULN-001 | OWASP Dependency-Check | Alta/Media | `log4j-api-2.24.3.jar` | CVEs reportados no scan inicial. | Risco em biblioteca de logging/transitiva. | Atualização do BOM Spring Boot para 4.1.0, que passou a resolver Log4j API 2.25.4. | Corrigido | Scan final sem achados em `target/dependency-check/dependency-check-report.json`. |
| VULN-002 | OWASP Dependency-Check | Alta | `postgresql-42.7.10.jar` | CVE reportado no scan inicial. | Risco associado ao driver JDBC PostgreSQL. | Atualização por BOM para PostgreSQL JDBC 42.7.11. | Corrigido | Scan final sem achados. |
| VULN-003 | OWASP Dependency-Check | Critica/Alta/Media | `spring-boot-3.5.13.jar` e starters | CVEs reportados no scan inicial para Spring Boot. | Risco no framework base da API. | Atualização do parent para Spring Boot 4.1.0. | Corrigido | Scan final sem achados. |
| VULN-004 | OWASP Dependency-Check | Alta/Media/Baixa | `spring-core-6.2.17.jar` e `spring-web-6.2.17.jar` | CVEs reportados no scan inicial para Spring Framework. | Risco transversal na infraestrutura web e core. | Atualização transitiva para Spring Framework 7.0.8 via Spring Boot 4.1.0. | Corrigido | Scan final sem achados. |
| VULN-005 | OWASP Dependency-Check | Alta/Media/Baixa | `spring-security-core-6.5.9.jar` e `spring-security-web-6.5.9.jar` | CVEs reportados no scan inicial para Spring Security. | Risco em autenticação e autorização. | Atualização transitiva para Spring Security 7.1.0 via Spring Boot 4.1.0. | Corrigido | Scan final sem achados. |
| VULN-006 | OWASP Dependency-Check | Critica/Alta/Media/Baixa | `tomcat-embed-core-10.1.53.jar` | CVEs reportados no scan inicial para Tomcat embutido. | Risco no servidor HTTP embutido. | Atualização transitiva para Tomcat 11 via Spring Boot 4.1.0. | Corrigido | Scan final sem achados. |
| VULN-007 | OWASP Dependency-Check | Media | `commons-compress-1.24.0.jar` | CVE-2024-25710 e CVE-2024-26308 no scan intermediario. | Risco em processamento de arquivos compactados, em escopo de teste/transitivo. | Override para Commons Compress 1.28.0. | Corrigido | Scan final sem achados. |
| VULN-008 | OWASP Dependency-Check | Media | `commons-lang3-3.17.0.jar` | CVE reportado no scan inicial. | Risco moderado em biblioteca utilitaria transitiva. | Atualização transitiva para Commons Lang 3.20.0. | Corrigido | Scan final sem achados. |
| VULN-009 | OWASP Dependency-Check | Media/Desconhecida | `swagger-ui-5.32.2.jar` | CVEs/achados DOMPurify no bundle JavaScript do Swagger UI. | Risco na interface de documentação se exposta fora do ambiente academico. | Atualização direta para Swagger UI 5.32.6. | Corrigido | Scan final sem achados. |
| VULN-010 | npm audit | Alta | `vite`, `esbuild`, `@vitejs/plugin-vue` | `npm audit` apontou 3 vulnerabilidades altas, com origem em `esbuild` e efeito transitivo em Vite/plugin Vue. | Risco de supply chain/RCE em ambiente de build conforme advisory do pacote. | Atualização para `vite` 8.0.16 e `@vitejs/plugin-vue` 6.0.7; lockfile regenerado. | Corrigido | `security-reports/frontend-dependencies/npm-audit-report.json` com 0 vulnerabilidades. |
| VULN-011 | npm audit | Media | `js-yaml-4.1.1` transitivo do ESLint | GHSA-h67p-54hq-rp68, com risco de DoS por complexidade quadratica. | Impacto no ferramental de desenvolvimento/lint. | Execução de `npm audit fix`, atualização transitiva e regeneração do lockfile. | Corrigido | `npm audit --json` de 20/06/2026 com 0 vulnerabilidades. |
| VULN-012 | Docker Scout | Critica/Alta/Media | `/usr/bin/pebble` na imagem backend anterior | 12 CVEs em pacote não utilizado pelo runtime Java. | Superfície de ataque desnecessária. | Migração para distroless Java 21 non-root. | Corrigido | `security-reports/docker/docker-scout-cves.txt` sem vulnerabilidades. |
| VULN-013 | Docker Scout | Critica/Alta/Media | Nginx 1.27 sobre Alpine 3.21 | 75 CVEs na imagem frontend inicial. | Vulnerabilidades do sistema operacional do container web. | Migração para `mainline-alpine-slim` fixada por digest. | Corrigido | Scan final sem críticas ou altas. |
| RISK-001 | Docker Scout | Media | BusyBox 1.37.0-r30 | CVE-2025-60876. | Risco residual na imagem base frontend. | Container non-root, read-only e sem novos privilégios; monitoramento da base. | Aceito temporariamente | Scanner informa `Fixed version: not fixed`. |

## 11. Analise por ferramenta

### 11.1 OWASP Dependency-Check - backend

| Campo | Valor |
| --- | --- |
| Comando | `mvn dependency-check:check -DautoUpdate=false` |
| Versao da engine | 12.1.1 |
| Relatorios | `target/dependency-check/dependency-check-report.html` e `target/dependency-check/dependency-check-report.json` |
| Resultado inicial | Falhava por vulnerabilidades com CVSS maior ou igual a 7,0. |
| Resultado final | Build com sucesso, zero vulnerabilidades reportadas. |
| Dependencias analisadas no scan final | 126 |
| Artefatos vulneraveis no scan final | 0 |
| Ocorrencias no scan final | 0 |
| Excecoes de analise no scan final | 0 |

Observação tecnica: o analisador Sonatype OSS Index estava retornando 401 sem credenciais. Para manter o scan local reproduzivel, o plugin foi configurado com `ossindexAnalyzerEnabled=false`. O scan continua usando NVD, Known Exploited Vulnerabilities e RetireJS.

### 11.2 npm audit - frontend

| Campo | Valor |
| --- | --- |
| Comando | `npm audit --json` |
| Relatorio | `security-reports/frontend-dependencies/npm-audit-report.json` |
| Resultado inicial | 3 vulnerabilidades altas em `vite`, `esbuild` e `@vitejs/plugin-vue`; nova ocorrencia moderada em `js-yaml` identificada em 20/06/2026. |
| Resultado final | 0 vulnerabilidades. |
| Ação tomada | Atualização de Vite/plugin Vue, correção transitiva de `js-yaml` e regeneração do `package-lock.json`. |

### 11.3 Docker image scan

| Campo | Valor |
| --- | --- |
| Ferramenta | Docker Scout 1.20.4 |
| Imagens analisadas | `soat-fiap-app:latest` e `soat-fiap-frontend:latest` |
| Relatorios | `security-reports/docker/docker-scout-cves.txt` e `security-reports/docker/docker-scout-frontend-cves.txt` |
| Backend final | 0 criticas, 0 altas, 0 medias e 0 baixas. |
| Frontend inicial | 4 criticas, 26 altas e 45 medias. |
| Frontend final | 0 criticas, 0 altas e 1 media sem correção disponível. |
| Ação tomada | Backend distroless; frontend Nginx unprivileged slim fixado por digest. |

### 11.4 Analise estatica de codigo

| Campo | Valor |
| --- | --- |
| Ferramenta | Semgrep 1.166.0 |
| Relatorio | `security-reports/static-analysis/semgrep.json` |
| Escopo | 200 arquivos, 187 regras, Java, JavaScript, JSON, Dockerfile e regras multilinguagem. |
| Resumo | 0 achados e 0 erros. |

### 11.5 Scan de secrets

| Campo | Valor |
| --- | --- |
| Ferramenta | Gitleaks |
| Relatorio | `security-reports/secrets/gitleaks.json` |
| Escopo | 36 commits e aproximadamente 3,24 MB analisados. |
| Resumo | 0 leaks encontrados. |

## 12. Vulnerabilidades corrigidas

| ID | Severidade | Correção aplicada | Arquivo/commit | Evidencia |
| --- | --- | --- | --- | --- |
| VULN-001 a VULN-009 | Critica/Alta/Media/Baixa | Atualização de dependencias Maven e novo scan limpo. | `pom.xml` | `target/dependency-check/dependency-check-report.json` com 0 vulnerabilidades. |
| VULN-010 | Alta | Atualização de dependencias frontend e novo audit limpo. | `frontend/package.json`, `frontend/package-lock.json` | `security-reports/frontend-dependencies/npm-audit-report.json` com 0 vulnerabilidades. |
| VULN-011 | Media | Atualização transitiva de `js-yaml` via `npm audit fix`. | `frontend/package-lock.json` | Audit de 20/06/2026 com 0 vulnerabilidades. |
| VULN-012 | Critica/Alta/Media | Substituição da imagem runtime Ubuntu/Temurin por distroless Java 21 non-root. | `Dockerfile`, `docker-compose.yml` | Docker Scout final com 0 vulnerabilidades. |
| VULN-013 | Critica/Alta/Media | Substituição da imagem Nginx/Alpine antiga por variante unprivileged slim atual. | `frontend/Dockerfile` | Scan final sem vulnerabilidades críticas ou altas. |

## 13. Vulnerabilidades aceitas como risco

| ID | Severidade | Justificativa | Mitigação existente | Responsavel pela aceitação | Revisar em |
| --- | --- | --- | --- | --- | --- |
| RISK-001 | Media | BusyBox não possui versão corrigida indicada pelo scanner na base atual. | Imagem slim, usuário não privilegiado, filesystem read-only e `no-new-privileges`. | Yasmin Barcelos Pires | Próxima atualização da imagem base |

## 14. Vulnerabilidades pendentes

| ID | Severidade | Motivo da pendencia | Plano de correção | Prioridade | Prazo |
| --- | --- | --- | --- | --- | --- |
| RISK-001 | Media | Não existe versão corrigida indicada pelo Docker Scout. | Atualizar a imagem fixada quando uma base corrigida estiver disponível e repetir o scan. | Média | Próximo ciclo |

Não há pendências críticas ou altas. Um DAST dedicado permanece como melhoria futura.

## 15. Boas praticas de seguranca implementadas

| Controle | Status | Evidencia/observação |
| --- | --- | --- |
| JWT assinado com segredo vindo de variavel de ambiente | Implementado | Configuração `security.jwt.secret`/`JWT_SECRET`; aplicação falha se o segredo nao for informado. |
| Expiração de token configuravel | Implementado | `security.jwt.expiration-minutes`/`JWT_EXPIRATION_MINUTES`. |
| Senhas com BCrypt | Implementado | `PasswordEncoder` com `BCryptPasswordEncoder`. |
| APIs administrativas protegidas por autenticação | Implementado | Configuração Spring Security/JWT. |
| DTOs explicitos para evitar mass assignment | Implementado | Requests/responses separados das entidades JPA. |
| Jackson rejeitando campos desconhecidos | Implementado | `fail-on-unknown-properties: true` e `ObjectMapper` de compatibilidade. |
| Validação real de CPF/CNPJ | Implementado | Value object de documento no dominio. |
| Validação real de placa | Implementado | Value object de placa no dominio. |
| Tratamento global de excecoes | Implementado | Responses padronizados sem stacktrace intencional. |
| CORS sem wildcard | Implementado | Configuração rejeita origem `*` e `null`. |
| Swagger publico apenas para MVP academico | Documentado/configuravel | Pode ser desabilitado por `SPRINGDOC_API_DOCS_ENABLED` e `SPRINGDOC_SWAGGER_UI_ENABLED`. |
| `.env.example` sem secrets reais | Implementado/documentado | Variaveis sensiveis devem ser preenchidas localmente. |
| Dependencias backend sem CVEs no scan final | Corrigido | `mvn dependency-check:check` final com sucesso. |
| Dependencias frontend sem vulnerabilidades no audit final | Corrigido | `npm audit --json` final com 0 vulnerabilidades. |
| Runtime distroless non-root | Implementado | Imagem final sem shell ou gerenciador de pacotes, executada como `nonroot`. |
| Container read-only e sem novos privilegios | Implementado | `read_only: true` e `no-new-privileges:true` no Compose. |
| Imagem backend sem vulnerabilidades no Scout final | Corrigido | Relatorio final com 0 vulnerabilidades. |
| Imagem frontend sem críticas ou altas | Validado | 1 CVE média aceita temporariamente. |
| Historico Git sem secrets detectados | Validado | Gitleaks analisou 36 commits e encontrou 0 leaks. |
| Analise estatica sem achados | Validado | Semgrep executou 187 regras em 200 arquivos com 0 achados. |

## 16. Evidencias

| Evidencia | Caminho | Descrição |
| --- | --- | --- |
| Relatorio Dependency-Check HTML | `target/dependency-check/dependency-check-report.html` | Relatorio navegavel com dependencias, CVEs, severidades e referencias. |
| Relatorio Dependency-Check JSON | `target/dependency-check/dependency-check-report.json` | Evidencia estruturada usada para consolidar este relatorio. |
| Relatorio npm audit JSON | `security-reports/frontend-dependencies/npm-audit-report.json` | Evidencia estruturada do frontend com 0 vulnerabilidades. |
| Relatorio Docker Scout | `security-reports/docker/docker-scout-cves.txt` | Evidencia do scan final da imagem com 0 vulnerabilidades. |
| Relatorio Docker Scout frontend | `security-reports/docker/docker-scout-frontend-cves.txt` | Evidencia do scan final com 1 CVE média e nenhuma crítica/alta. |
| Relatorio Gitleaks | `security-reports/secrets/gitleaks.json` | Evidencia estruturada com 0 leaks. |
| Relatorio Semgrep | `security-reports/static-analysis/semgrep.json` | Evidencia estruturada com 0 achados e 0 erros. |
| Relatorio JaCoCo HTML | `target/site/jacoco/index.html` | Relatorio navegavel de cobertura atual. |
| Relatorio JaCoCo CSV | `target/site/jacoco/jacoco.csv` | Evidencia estruturada usada para consolidar cobertura. |

## 17. Prints ou caminhos dos arquivos de relatorio

```text
target/dependency-check/dependency-check-report.html
target/dependency-check/dependency-check-report.json
security-reports/frontend-dependencies/npm-audit-report.json
security-reports/docker/docker-scout-cves.txt
security-reports/docker/docker-scout-frontend-cves.txt
security-reports/secrets/gitleaks.json
security-reports/static-analysis/semgrep.json
target/site/jacoco/index.html
target/site/jacoco/jacoco.csv
```

## 18. Conclusao

O projeto AutoCare Hub corrigiu as vulnerabilidades de dependências backend, frontend e imagens
identificadas nos scans. Dependency-Check, npm audit e a imagem backend ficaram sem vulnerabilidades.
A imagem frontend ficou sem críticas ou altas e mantém 1 CVE média sem correção disponível.

Considerando o escopo analisado por Dependency-Check, npm audit, Docker Scout, Gitleaks e Semgrep, o sistema esta apto para entrega academica. Um teste dinamico dedicado contra a API permanece como evolução futura.

## 19. Recomendacoes futuras

1. Manter o Spring Boot BOM atualizado e evitar overrides manuais sem necessidade.
2. Reexecutar `mvn dependency-check:check` antes da entrega final e em cada ciclo de manutenção.
3. Manter o frontend atualizado e reexecutar `npm audit` antes da entrega final.
4. Reexecutar Docker Scout a cada atualização da imagem base.
5. Reexecutar Gitleaks a cada ciclo de entrega.
6. Reexecutar Semgrep e avaliar integração futura com SonarQube.
7. Configurar CI para bloquear vulnerabilidades criticas e altas sem aceite formal.
8. Restringir Swagger em ambientes produtivos.
9. Manter a cobertura global acima dos gates de 90% para instruções, linhas e branches.

## Checklist de seguranca

| Item | Status | Evidencia | Observação |
| --- | --- | --- | --- |
| JWT | OK | Configuração por variavel de ambiente | Validar segredo real apenas fora do repositorio. |
| Senhas | OK | BCrypt | Nao retornar senha/hash em DTOs. |
| Secrets | OK | Gitleaks com 0 leaks em 36 commits | Reexecutar antes de cada entrega. |
| Logs | OK no escopo estatico | Semgrep sem achados | Complementar futuramente com DAST. |
| Validação de entrada | OK | CPF/CNPJ e placa em value objects | Manter testes cobrindo documentos e placas invalidos. |
| Tratamento de erros | OK | Handler global esperado | Confirmar sem stacktrace em ambiente produtivo. |
| CORS | OK | Configuração sem wildcard | Revisar origens permitidas por ambiente. |
| Swagger | OK no MVP | Versao atualizada e flags para desabilitar | Restringir em produção. |
| Docker | OK com risco médio documentado | Backend sem vulnerabilidades; frontend sem críticas/altas e com 1 média aceita | Reexecutar a cada atualização da imagem. |
| Banco de dados | Parcial | Credenciais por `.env` | Sem scan especifico de banco. |
| Dependencias backend | OK | Dependency-Check final | Zero vulnerabilidades reportadas. |
| Dependencias frontend | OK | `npm audit` final | Zero vulnerabilidades reportadas. |
| Dados sensiveis | Parcial | Mascaramento e DTOs | Validar responses de detalhe e logs. |
| Frontend | OK no escopo analisado | `npm audit` e Semgrep sem achados | Manter dependencias atualizadas. |
| Backend | OK no escopo analisado | Dependency-Check e Semgrep sem achados | Complementar futuramente com DAST. |
