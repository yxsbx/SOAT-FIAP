# Relatório de Vulnerabilidades - AutoCare Hub

## 1. Título

Relatório de Vulnerabilidades do projeto AutoCare Hub - Tech Challenge FIAP.

## 2. Objetivo da análise

Registrar a análise de segurança realizada no projeto AutoCare Hub com base nos resultados reais do OWASP
Dependency-Check. O documento consolida vulnerabilidades encontradas, correções aplicadas, evidências, riscos ainda não
analisados e recomendações futuras.

Este relatório não inventa resultados de ferramentas não executadas. Quando não houve evidência de scan, o item
permanece marcado como "Não executado".

## 3. Escopo analisado

- **Item:**  Backend Spring Boot
  - **Status:**  Analisado
  - **Observação:**  Dependências Maven analisadas pelo OWASP Dependency-Check.

- **Item:**  Dependências backend
  - **Status:**  Analisado e corrigido
  - **Observação:**  Relatórios em `target/dependency-check/dependency-check-report.html` e
    `target/dependency-check/dependency-check-report.json`.

- **Item:**  Frontend Vue/Vite
  - **Status:**  Analisado
  - **Observação:**  Dependências npm analisadas por `npm audit` e código incluído no Semgrep.

- **Item:**  Dependências frontend
  - **Status:**  Analisado e corrigido
  - **Observação:**  Relatório em `security-reports/frontend-dependencies/npm-audit-report.json`.

- **Item:**  Dockerfiles
  - **Status:**  Analisados e corrigidos
  - **Observação:**  Docker Scout executado nas imagens finais de backend e frontend.

- **Item:**  docker-compose.yml
  - **Status:**  Revisado em execução
  - **Observação:**  Containers validados como non-root, read-only e com `no-new-privileges`.

- **Item:**  Secrets
  - **Status:**  Analisado
  - **Observação:**  Gitleaks executado sobre 36 commits, sem leaks encontrados.

- **Item:**  OpenAPI/Swagger
  - **Status:**  Analisado indiretamente
  - **Observação:**  Vulnerabilidades anteriores em Swagger UI foram corrigidas por atualização de versão.

- **Item:**  Cobertura de testes
  - **Status:**  Analisado
  - **Observação:**  Relatório JaCoCo em `target/site/jacoco/index.html` e `target/site/jacoco/jacoco.csv`.

Item fora do escopo desta versão:

- teste dinâmico de segurança dedicado contra a API em execução.

## 4. Data da análise

| Campo                                 | Valor                            |
| ------------------------------------- | -------------------------------- |
| Data de consolidação                  | 20/06/2026                       |
| Horário do relatório Dependency-Check | 17:07:53 UTC                     |
| Responsável                           | Yasmin Barcelos Pires - RM370897 |
| Branch final de entrega               | `main`                           |
| Commit técnico validado               | `dbed819`                        |

## 5. Ferramentas utilizadas

- **Ferramenta:**  OWASP Dependency-Check Maven Plugin
  - **Versão:**  12.1.1
  - **Finalidade:**  Dependências backend
  - **Comando executado:**  `mvn dependency-check:check -DautoUpdate=false`
  - **Saída/relatório:**  `target/dependency-check/dependency-check-report.html` e
    `target/dependency-check/dependency-check-report.json`

- **Ferramenta:**  JaCoCo
  - **Versão:**  0.8.12
  - **Finalidade:**  Cobertura de testes
  - **Comando executado:**  `mvn test`
  - **Saída/relatório:**  `target/site/jacoco/index.html` e `target/site/jacoco/jacoco.csv`

- **Ferramenta:**  npm audit
  - **Versão:**  9.6.6
  - **Finalidade:**  Dependências frontend
  - **Comando executado:**  `npm audit --json`
  - **Saída/relatório:**  `security-reports/frontend-dependencies/npm-audit-report.json`

- **Ferramenta:**  Docker Scout
  - **Versão:**  1.20.4
  - **Finalidade:**  Imagens Docker
  - **Comando executado:**  `docker scout cves <imagem> --only-severity critical,high,medium`
  - **Saída/relatório:**  `security-reports/docker/docker-scout-cves.txt` e
    `security-reports/docker/docker-scout-frontend-cves.txt`

- **Ferramenta:**  Semgrep
  - **Versão:**  1.166.0
  - **Finalidade:**  Análise estática Java/JavaScript/Dockerfile
  - **Comando executado:**  Regras `p/java`, `p/javascript` e `p/security-audit`
  - **Saída/relatório:**  `security-reports/static-analysis/semgrep.json`

- **Ferramenta:**  Gitleaks
  - **Versão:**  Imagem Docker latest em 20/06/2026
  - **Finalidade:**  Secrets no histórico Git
  - **Comando executado:**  `gitleaks detect`
  - **Saída/relatório:**  `security-reports/secrets/gitleaks.json`

## 6. Ambiente analisado

| Campo               | Valor                                                                                    |
| ------------------- | ---------------------------------------------------------------------------------------- |
| Sistema operacional | Windows, execução local em PowerShell                                                    |
| Java                | Java 21                                                                                  |
| Maven               | Maven local do projeto                                                                   |
| Banco usado no scan | Não aplicável ao Dependency-Check                                                        |
| Perfil Spring       | Não aplicável ao Dependency-Check                                                        |
| Observações         | O relatório JSON informa `engineVersion` 12.1.1 e data `2026-06-20T17:07:53.263950500Z`. |

## 7. Resumo executivo

O primeiro scan de dependências backend apontou vulnerabilidades em bibliotecas centrais do runtime, incluindo Spring
Boot, Spring Framework, Spring Security, Tomcat, PostgreSQL JDBC, Log4j API, Commons Compress, Commons Lang e Swagger
UI. O scan inicial do frontend apontou vulnerabilidades altas transitivas em Vite/esbuild. Em 20/06/2026, um novo
`npm audit` identificou uma vulnerabilidade moderada transitiva em `js-yaml`, corrigida com atualização do lockfile.

O primeiro Docker Scout do backend encontrou 1 vulnerabilidade crítica, 2 altas e 9 médias em `/usr/bin/pebble`,
presente na imagem base Ubuntu. O runtime foi migrado para `gcr.io/distroless/java21-debian12:nonroot`, eliminando os
achados. No frontend, a imagem Nginx 1.27/Alpine inicialmente apresentou 75 CVEs. A troca para `mainline-alpine-slim`,
fixada por digest, reduziu o resultado para 0 críticas, 0 altas e 1 média em BusyBox, sem versão corrigida indicada pelo
scanner em 20/06/2026.

As dependências foram atualizadas e o OWASP Dependency-Check foi executado novamente. No scan final, o resultado foi:

| Indicador                           | Resultado |
| ----------------------------------- | --------: |
| Dependências analisadas             |       126 |
| Artefatos vulneráveis               |         0 |
| Ocorrências de vulnerabilidades     |         0 |
| Achados únicos                      |         0 |
| Exceções de análise                 |         0 |
| Status do build do Dependency-Check |   Sucesso |

Os scans finais não apresentam vulnerabilidades críticas ou altas, nem leaks ou achados estáticos. Permanece 1 CVE média
aceita temporariamente na imagem frontend.

## 8. Resultado geral dos scans

| Categoria             | Critical | High | Medium | Low | Unknown | Status geral                       |
| --------------------- | -------: | ---: | -----: | --: | ------: | ---------------------------------- |
| Dependências backend  |        0 |    0 |      0 |   0 |       0 | Corrigido                          |
| Dependências frontend |        0 |    0 |      0 |   0 |       0 | Corrigido                          |
| Imagem backend        |        0 |    0 |      0 |   0 |       0 | Corrigido                          |
| Imagem frontend       |        0 |    0 |      1 |   0 |       0 | Risco médio aceito temporariamente |
| Análise estática      |        0 |    0 |      0 |   0 |       0 | Semgrep sem achados                |
| Secrets               |        0 |    0 |      0 |   0 |       0 | Gitleaks sem leaks                 |

## 9. Cobertura de testes

O relatório JaCoCo mede domínio, aplicação, controllers REST, segurança, mappers e adapters de persistência. São
excluídos apenas o bootstrap da aplicação, classes geradas automaticamente pelo OpenAPI e records estruturais de
comando, consulta e saída sem lógica própria. O gate executado por `mvn verify` exige no mínimo 90% de instruções,
linhas e branches.

| Métrica    | Coberto | Não coberto | Cobertura |
| ---------- | ------: | ----------: | --------: |
| Instruções |   9.752 |         397 |    96,09% |
| Branches   |     457 |          49 |    90,32% |
| Linhas     |   2.440 |          75 |    97,02% |
| Métodos    |     634 |          36 |    94,63% |

Resultado validado com 145 testes automatizados e `mvn verify` concluindo com sucesso.

## 10. Tabela de vulnerabilidades encontradas

- **ID:**  VULN-001
  - **Ferramenta:**  OWASP Dependency-Check
  - **Severidade:**  Alta/Média
  - **Arquivo/Pacote afetado:**  `log4j-api-2.24.3.jar`
  - **Descrição:**  CVEs reportados no scan inicial.
  - **Impacto:**  Risco em biblioteca de logging/transitiva.
  - **Correção aplicada:**  Atualização do BOM Spring Boot para 4.1.0, que passou a resolver Log4j API 2.25.4.
  - **Status:**  Corrigido
  - **Evidência:**  Scan final sem achados em `target/dependency-check/dependency-check-report.json`.

- **ID:**  VULN-002
  - **Ferramenta:**  OWASP Dependency-Check
  - **Severidade:**  Alta
  - **Arquivo/Pacote afetado:**  `postgresql-42.7.10.jar`
  - **Descrição:**  CVE reportado no scan inicial.
  - **Impacto:**  Risco associado ao driver JDBC PostgreSQL.
  - **Correção aplicada:**  Atualização por BOM para PostgreSQL JDBC 42.7.11.
  - **Status:**  Corrigido
  - **Evidência:**  Scan final sem achados.

- **ID:**  VULN-003
  - **Ferramenta:**  OWASP Dependency-Check
  - **Severidade:**  Crítica/Alta/Média
  - **Arquivo/Pacote afetado:**  `spring-boot-3.5.13.jar` e starters
  - **Descrição:**  CVEs reportados no scan inicial para Spring Boot.
  - **Impacto:**  Risco no framework base da API.
  - **Correção aplicada:**  Atualização do parent para Spring Boot 4.1.0.
  - **Status:**  Corrigido
  - **Evidência:**  Scan final sem achados.

- **ID:**  VULN-004
  - **Ferramenta:**  OWASP Dependency-Check
  - **Severidade:**  Alta/Média/Baixa
  - **Arquivo/Pacote afetado:**  `spring-core-6.2.17.jar` e `spring-web-6.2.17.jar`
  - **Descrição:**  CVEs reportados no scan inicial para Spring Framework.
  - **Impacto:**  Risco transversal na infraestrutura web e core.
  - **Correção aplicada:**  Atualização transitiva para Spring Framework 7.0.8 via Spring Boot 4.1.0.
  - **Status:**  Corrigido
  - **Evidência:**  Scan final sem achados.

- **ID:**  VULN-005
  - **Ferramenta:**  OWASP Dependency-Check
  - **Severidade:**  Alta/Média/Baixa
  - **Arquivo/Pacote afetado:**  `spring-security-core-6.5.9.jar` e `spring-security-web-6.5.9.jar`
  - **Descrição:**  CVEs reportados no scan inicial para Spring Security.
  - **Impacto:**  Risco em autenticação e autorização.
  - **Correção aplicada:**  Atualização transitiva para Spring Security 7.1.0 via Spring Boot 4.1.0.
  - **Status:**  Corrigido
  - **Evidência:**  Scan final sem achados.

- **ID:**  VULN-006
  - **Ferramenta:**  OWASP Dependency-Check
  - **Severidade:**  Crítica/Alta/Média/Baixa
  - **Arquivo/Pacote afetado:**  `tomcat-embed-core-10.1.53.jar`
  - **Descrição:**  CVEs reportados no scan inicial para Tomcat embutido.
  - **Impacto:**  Risco no servidor HTTP embutido.
  - **Correção aplicada:**  Atualização transitiva para Tomcat 11 via Spring Boot 4.1.0.
  - **Status:**  Corrigido
  - **Evidência:**  Scan final sem achados.

- **ID:**  VULN-007
  - **Ferramenta:**  OWASP Dependency-Check
  - **Severidade:**  Média
  - **Arquivo/Pacote afetado:**  `commons-compress-1.24.0.jar`
  - **Descrição:**  CVE-2024-25710 e CVE-2024-26308 no scan intermediário.
  - **Impacto:**  Risco em processamento de arquivos compactados, em escopo de teste/transitivo.
  - **Correção aplicada:**  Override para Commons Compress 1.28.0.
  - **Status:**  Corrigido
  - **Evidência:**  Scan final sem achados.

- **ID:**  VULN-008
  - **Ferramenta:**  OWASP Dependency-Check
  - **Severidade:**  Média
  - **Arquivo/Pacote afetado:**  `commons-lang3-3.17.0.jar`
  - **Descrição:**  CVE reportado no scan inicial.
  - **Impacto:**  Risco moderado em biblioteca utilitária transitiva.
  - **Correção aplicada:**  Atualização transitiva para Commons Lang 3.20.0.
  - **Status:**  Corrigido
  - **Evidência:**  Scan final sem achados.

- **ID:**  VULN-009
  - **Ferramenta:**  OWASP Dependency-Check
  - **Severidade:**  Média/Desconhecida
  - **Arquivo/Pacote afetado:**  `swagger-ui-5.32.2.jar`
  - **Descrição:**  CVEs/achados DOMPurify no bundle JavaScript do Swagger UI.
  - **Impacto:**  Risco na interface de documentação se exposta fora do ambiente acadêmico.
  - **Correção aplicada:**  Atualização direta para Swagger UI 5.32.6.
  - **Status:**  Corrigido
  - **Evidência:**  Scan final sem achados.

- **ID:**  VULN-010
  - **Ferramenta:**  npm audit
  - **Severidade:**  Alta
  - **Arquivo/Pacote afetado:**  `vite`, `esbuild`, `@vitejs/plugin-vue`
  - **Descrição:**  `npm audit` apontou 3 vulnerabilidades altas, com origem em `esbuild` e efeito transitivo em
    Vite/plugin Vue.
  - **Impacto:**  Risco de supply chain/RCE em ambiente de build conforme advisory do pacote.
  - **Correção aplicada:**  Atualização para `vite` 8.0.16 e `@vitejs/plugin-vue` 6.0.7; lockfile regenerado.
  - **Status:**  Corrigido
  - **Evidência:**  `security-reports/frontend-dependencies/npm-audit-report.json` com 0 vulnerabilidades.

- **ID:**  VULN-011
  - **Ferramenta:**  npm audit
  - **Severidade:**  Média
  - **Arquivo/Pacote afetado:**  `js-yaml-4.1.1` transitivo do ESLint
  - **Descrição:**  GHSA-h67p-54hq-rp68, com risco de DoS por complexidade quadrática.
  - **Impacto:**  Impacto no ferramental de desenvolvimento/lint.
  - **Correção aplicada:**  Execução de `npm audit fix`, atualização transitiva e regeneração do lockfile.
  - **Status:**  Corrigido
  - **Evidência:**  `npm audit --json` de 20/06/2026 com 0 vulnerabilidades.

- **ID:**  VULN-012
  - **Ferramenta:**  Docker Scout
  - **Severidade:**  Crítica/Alta/Média
  - **Arquivo/Pacote afetado:**  `/usr/bin/pebble` na imagem backend anterior
  - **Descrição:**  12 CVEs em pacote não utilizado pelo runtime Java.
  - **Impacto:**  Superfície de ataque desnecessária.
  - **Correção aplicada:**  Migração para distroless Java 21 non-root.
  - **Status:**  Corrigido
  - **Evidência:**  `security-reports/docker/docker-scout-cves.txt` sem vulnerabilidades.

- **ID:**  VULN-013
  - **Ferramenta:**  Docker Scout
  - **Severidade:**  Crítica/Alta/Média
  - **Arquivo/Pacote afetado:**  Nginx 1.27 sobre Alpine 3.21
  - **Descrição:**  75 CVEs na imagem frontend inicial.
  - **Impacto:**  Vulnerabilidades do sistema operacional do container web.
  - **Correção aplicada:**  Migração para `mainline-alpine-slim` fixada por digest.
  - **Status:**  Corrigido
  - **Evidência:**  Scan final sem críticas ou altas.

- **ID:**  RISK-001
  - **Ferramenta:**  Docker Scout
  - **Severidade:**  Média
  - **Arquivo/Pacote afetado:**  BusyBox 1.37.0-r30
  - **Descrição:**  CVE-2025-60876.
  - **Impacto:**  Risco residual na imagem base frontend.
  - **Correção aplicada:**  Container non-root, read-only e sem novos privilégios; monitoramento da base.
  - **Status:**  Aceito temporariamente
  - **Evidência:**  Scanner informa `Fixed version: not fixed`.

## 11. Análise por ferramenta

### 11.1 OWASP Dependency-Check - backend

- **Campo:**  Comando
  - **Valor:**  `mvn dependency-check:check -DautoUpdate=false`

- **Campo:**  Versão da engine
  - **Valor:**  12.1.1

- **Campo:**  Relatórios
  - **Valor:**  `target/dependency-check/dependency-check-report.html` e
    `target/dependency-check/dependency-check-report.json`

- **Campo:**  Resultado inicial
  - **Valor:**  Falhava por vulnerabilidades com CVSS maior ou igual a 7,0.

- **Campo:**  Resultado final
  - **Valor:**  Build com sucesso, zero vulnerabilidades reportadas.

- **Campo:**  Dependências analisadas no scan final
  - **Valor:**  126

- **Campo:**  Artefatos vulneráveis no scan final
  - **Valor:**  0

- **Campo:**  Ocorrências no scan final
  - **Valor:**  0

- **Campo:**  Exceções de análise no scan final
  - **Valor:**  0

Observação técnica: o analisador Sonatype OSS Index estava retornando 401 sem credenciais. Para manter o scan local
reproduzível, o plugin foi configurado com `ossindexAnalyzerEnabled=false`. O scan continua usando NVD, Known Exploited
Vulnerabilities e RetireJS.

### 11.2 npm audit - frontend

- **Campo:**  Comando
  - **Valor:**  `npm audit --json`

- **Campo:**  Relatório
  - **Valor:**  `security-reports/frontend-dependencies/npm-audit-report.json`

- **Campo:**  Resultado inicial
  - **Valor:**  3 vulnerabilidades altas em `vite`, `esbuild` e `@vitejs/plugin-vue`; nova ocorrência moderada em
    `js-yaml` identificada em 20/06/2026.

- **Campo:**  Resultado final
  - **Valor:**  0 vulnerabilidades.

- **Campo:**  Ação tomada
  - **Valor:**  Atualização de Vite/plugin Vue, correção transitiva de `js-yaml` e regeneração do `package-lock.json`.

### 11.3 Docker image scan

- **Campo:**  Ferramenta
  - **Valor:**  Docker Scout 1.20.4

- **Campo:**  Imagens analisadas
  - **Valor:**  `soat-fiap-app:latest` e `soat-fiap-frontend:latest`

- **Campo:**  Relatórios
  - **Valor:**  `security-reports/docker/docker-scout-cves.txt` e
    `security-reports/docker/docker-scout-frontend-cves.txt`

- **Campo:**  Backend final
  - **Valor:**  0 críticas, 0 altas, 0 médias e 0 baixas.

- **Campo:**  Frontend inicial
  - **Valor:**  4 críticas, 26 altas e 45 médias.

- **Campo:**  Frontend final
  - **Valor:**  0 críticas, 0 altas e 1 média sem correção disponível.

- **Campo:**  Ação tomada
  - **Valor:**  Backend distroless; frontend Nginx unprivileged slim fixado por digest.

### 11.4 Análise estática de código

| Campo      | Valor                                                                                 |
| ---------- | ------------------------------------------------------------------------------------- |
| Ferramenta | Semgrep 1.166.0                                                                       |
| Relatório  | `security-reports/static-analysis/semgrep.json`                                       |
| Escopo     | 200 arquivos, 187 regras, Java, JavaScript, JSON, Dockerfile e regras multilinguagem. |
| Resumo     | 0 achados e 0 erros.                                                                  |

### 11.5 Scan de secrets

| Campo      | Valor                                            |
| ---------- | ------------------------------------------------ |
| Ferramenta | Gitleaks                                         |
| Relatório  | `security-reports/secrets/gitleaks.json`         |
| Escopo     | 36 commits e aproximadamente 3,24 MB analisados. |
| Resumo     | 0 leaks encontrados.                             |

## 12. Vulnerabilidades corrigidas

- **ID:**  VULN-001 a VULN-009
  - **Severidade:**  Crítica/Alta/Média/Baixa
  - **Correção aplicada:**  Atualização de dependências Maven e novo scan limpo.
  - **Arquivo/commit:**  `pom.xml`
  - **Evidência:**  `target/dependency-check/dependency-check-report.json` com 0 vulnerabilidades.

- **ID:**  VULN-010
  - **Severidade:**  Alta
  - **Correção aplicada:**  Atualização de dependências frontend e novo audit limpo.
  - **Arquivo/commit:**  `frontend/package.json`, `frontend/package-lock.json`
  - **Evidência:**  `security-reports/frontend-dependencies/npm-audit-report.json` com 0 vulnerabilidades.

- **ID:**  VULN-011
  - **Severidade:**  Média
  - **Correção aplicada:**  Atualização transitiva de `js-yaml` via `npm audit fix`.
  - **Arquivo/commit:**  `frontend/package-lock.json`
  - **Evidência:**  Audit de 20/06/2026 com 0 vulnerabilidades.

- **ID:**  VULN-012
  - **Severidade:**  Crítica/Alta/Média
  - **Correção aplicada:**  Substituição da imagem runtime Ubuntu/Temurin por distroless Java 21 non-root.
  - **Arquivo/commit:**  `Dockerfile`, `docker-compose.yml`
  - **Evidência:**  Docker Scout final com 0 vulnerabilidades.

- **ID:**  VULN-013
  - **Severidade:**  Crítica/Alta/Média
  - **Correção aplicada:**  Substituição da imagem Nginx/Alpine antiga por variante unprivileged slim atual.
  - **Arquivo/commit:**  `frontend/Dockerfile`
  - **Evidência:**  Scan final sem vulnerabilidades críticas ou altas.

## 13. Vulnerabilidades aceitas como risco

- **ID:**  RISK-001
  - **Severidade:**  Média
  - **Justificativa:**  BusyBox não possui versão corrigida indicada pelo scanner na base atual.
  - **Mitigação existente:**  Imagem slim, usuário não privilegiado, filesystem read-only e `no-new-privileges`.
  - **Responsável pela aceitação:**  Yasmin Barcelos Pires
  - **Revisar em:**  Próxima atualização da imagem base

## 14. Vulnerabilidades pendentes

- **ID:**  RISK-001
  - **Severidade:**  Média
  - **Motivo da pendência:**  Não existe versão corrigida indicada pelo Docker Scout.
  - **Plano de correção:**  Atualizar a imagem fixada quando uma base corrigida estiver disponível e repetir o scan.
  - **Prioridade:**  Média
  - **Prazo:**  Próximo ciclo

Não há pendências críticas ou altas. Um DAST dedicado permanece como melhoria futura.

## 15. Boas práticas de segurança implementadas

- **Controle:**  JWT assinado com segredo vindo de variável de ambiente
  - **Status:**  Implementado
  - **Evidência/observação:**  Configuração `security.jwt.secret`/`JWT_SECRET`; aplicação falha se o segredo não for
    informado.

- **Controle:**  Expiração de token configurável
  - **Status:**  Implementado
  - **Evidência/observação:**  `security.jwt.expiration-minutes`/`JWT_EXPIRATION_MINUTES`.

- **Controle:**  Senhas com BCrypt
  - **Status:**  Implementado
  - **Evidência/observação:**  `PasswordEncoder` com `BCryptPasswordEncoder`.

- **Controle:**  APIs administrativas protegidas por autenticação
  - **Status:**  Implementado
  - **Evidência/observação:**  Configuração Spring Security/JWT.

- **Controle:**  DTOs explícitos para evitar mass assignment
  - **Status:**  Implementado
  - **Evidência/observação:**  Requests/responses separados das entidades JPA.

- **Controle:**  Jackson rejeitando campos desconhecidos
  - **Status:**  Implementado
  - **Evidência/observação:**  `fail-on-unknown-properties: true` e `ObjectMapper` de compatibilidade.

- **Controle:**  Validação real de CPF/CNPJ
  - **Status:**  Implementado
  - **Evidência/observação:**  Value object de documento no domínio.

- **Controle:**  Validação real de placa
  - **Status:**  Implementado
  - **Evidência/observação:**  Value object de placa no domínio.

- **Controle:**  Tratamento global de exceções
  - **Status:**  Implementado
  - **Evidência/observação:**  Responses padronizados sem stacktrace intencional.

- **Controle:**  CORS sem wildcard
  - **Status:**  Implementado
  - **Evidência/observação:**  Configuração rejeita origem `*` e `null`.

- **Controle:**  Swagger público apenas para MVP acadêmico
  - **Status:**  Documentado/configurável
  - **Evidência/observação:**  Pode ser desabilitado por `SPRINGDOC_API_DOCS_ENABLED` e `SPRINGDOC_SWAGGER_UI_ENABLED`.

- **Controle:**  `.env.example` sem secrets reais
  - **Status:**  Implementado/documentado
  - **Evidência/observação:**  Variáveis sensíveis devem ser preenchidas localmente.

- **Controle:**  Dependências backend sem CVEs no scan final
  - **Status:**  Corrigido
  - **Evidência/observação:**  `mvn dependency-check:check` final com sucesso.

- **Controle:**  Dependências frontend sem vulnerabilidades no audit final
  - **Status:**  Corrigido
  - **Evidência/observação:**  `npm audit --json` final com 0 vulnerabilidades.

- **Controle:**  Runtime distroless non-root
  - **Status:**  Implementado
  - **Evidência/observação:**  Imagem final sem shell ou gerenciador de pacotes, executada como `nonroot`.

- **Controle:**  Container read-only e sem novos privilégios
  - **Status:**  Implementado
  - **Evidência/observação:**  `read_only: true` e `no-new-privileges:true` no Compose.

- **Controle:**  Imagem backend sem vulnerabilidades no Scout final
  - **Status:**  Corrigido
  - **Evidência/observação:**  Relatório final com 0 vulnerabilidades.

- **Controle:**  Imagem frontend sem críticas ou altas
  - **Status:**  Validado
  - **Evidência/observação:**  1 CVE média aceita temporariamente.

- **Controle:**  Histórico Git sem secrets detectados
  - **Status:**  Validado
  - **Evidência/observação:**  Gitleaks analisou 36 commits e encontrou 0 leaks.

- **Controle:**  Análise estática sem achados
  - **Status:**  Validado
  - **Evidência/observação:**  Semgrep executou 187 regras em 200 arquivos com 0 achados.

## 16. Evidências

- **Evidência:**  Relatório Dependency-Check HTML
  - **Caminho:**  `target/dependency-check/dependency-check-report.html`
  - **Descrição:**  Relatório navegável com dependências, CVEs, severidades e referências.

- **Evidência:**  Relatório Dependency-Check JSON
  - **Caminho:**  `target/dependency-check/dependency-check-report.json`
  - **Descrição:**  Evidência estruturada usada para consolidar este relatório.

- **Evidência:**  Relatório npm audit JSON
  - **Caminho:**  `security-reports/frontend-dependencies/npm-audit-report.json`
  - **Descrição:**  Evidência estruturada do frontend com 0 vulnerabilidades.

- **Evidência:**  Relatório Docker Scout
  - **Caminho:**  `security-reports/docker/docker-scout-cves.txt`
  - **Descrição:**  Evidência do scan final da imagem com 0 vulnerabilidades.

- **Evidência:**  Relatório Docker Scout frontend
  - **Caminho:**  `security-reports/docker/docker-scout-frontend-cves.txt`
  - **Descrição:**  Evidência do scan final com 1 CVE média e nenhuma crítica/alta.

- **Evidência:**  Relatório Gitleaks
  - **Caminho:**  `security-reports/secrets/gitleaks.json`
  - **Descrição:**  Evidência estruturada com 0 leaks.

- **Evidência:**  Relatório Semgrep
  - **Caminho:**  `security-reports/static-analysis/semgrep.json`
  - **Descrição:**  Evidência estruturada com 0 achados e 0 erros.

- **Evidência:**  Relatório JaCoCo HTML
  - **Caminho:**  `target/site/jacoco/index.html`
  - **Descrição:**  Relatório navegável de cobertura atual.

- **Evidência:**  Relatório JaCoCo CSV
  - **Caminho:**  `target/site/jacoco/jacoco.csv`
  - **Descrição:**  Evidência estruturada usada para consolidar cobertura.

## 17. Prints ou caminhos dos arquivos de relatório

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

## 18. Conclusão

O projeto AutoCare Hub corrigiu as vulnerabilidades de dependências backend, frontend e imagens identificadas nos scans.
Dependency-Check, npm audit e a imagem backend ficaram sem vulnerabilidades. A imagem frontend ficou sem críticas ou
altas e mantém 1 CVE média sem correção disponível.

Considerando o escopo analisado por Dependency-Check, npm audit, Docker Scout, Gitleaks e Semgrep, o sistema está apto
para entrega acadêmica. Um teste dinâmico dedicado contra a API permanece como evolução futura.

## 19. Recomendações futuras

1. Manter o Spring Boot BOM atualizado e evitar overrides manuais sem necessidade.
2. Reexecutar `mvn dependency-check:check` antes da entrega final e em cada ciclo de manutenção.
3. Manter o frontend atualizado e reexecutar `npm audit` antes da entrega final.
4. Reexecutar Docker Scout a cada atualização da imagem base.
5. Reexecutar Gitleaks a cada ciclo de entrega.
6. Reexecutar Semgrep e avaliar integração futura com SonarQube.
7. Configurar CI para bloquear vulnerabilidades críticas e altas sem aceite formal.
8. Restringir Swagger em ambientes produtivos.
9. Manter a cobertura global acima dos gates de 90% para instruções, linhas e branches.

## Checklist de segurança

- **Item:**  JWT
  - **Status:**  OK
  - **Evidência:**  Configuração por variável de ambiente
  - **Observação:**  Validar segredo real apenas fora do repositório.

- **Item:**  Senhas
  - **Status:**  OK
  - **Evidência:**  BCrypt
  - **Observação:**  Não retornar senha/hash em DTOs.

- **Item:**  Secrets
  - **Status:**  OK
  - **Evidência:**  Gitleaks com 0 leaks em 36 commits
  - **Observação:**  Reexecutar antes de cada entrega.

- **Item:**  Logs
  - **Status:**  OK no escopo estático
  - **Evidência:**  Semgrep sem achados
  - **Observação:**  Complementar futuramente com DAST.

- **Item:**  Validação de entrada
  - **Status:**  OK
  - **Evidência:**  CPF/CNPJ e placa em value objects
  - **Observação:**  Manter testes cobrindo documentos e placas inválidos.

- **Item:**  Tratamento de erros
  - **Status:**  OK
  - **Evidência:**  Handler global esperado
  - **Observação:**  Confirmar ausência de stacktrace em ambiente produtivo.

- **Item:**  CORS
  - **Status:**  OK
  - **Evidência:**  Configuração sem wildcard
  - **Observação:**  Revisar origens permitidas por ambiente.

- **Item:**  Swagger
  - **Status:**  OK no MVP
  - **Evidência:**  Versão atualizada e flags para desabilitar
  - **Observação:**  Restringir em produção.

- **Item:**  Docker
  - **Status:**  OK com risco médio documentado
  - **Evidência:**  Backend sem vulnerabilidades; frontend sem críticas/altas e com 1 média aceita
  - **Observação:**  Reexecutar a cada atualização da imagem.

- **Item:**  Banco de dados
  - **Status:**  Parcial
  - **Evidência:**  Credenciais por `.env`
  - **Observação:**  Sem scan específico de banco.

- **Item:**  Dependências backend
  - **Status:**  OK
  - **Evidência:**  Dependency-Check final
  - **Observação:**  Zero vulnerabilidades reportadas.

- **Item:**  Dependências frontend
  - **Status:**  OK
  - **Evidência:**  `npm audit` final
  - **Observação:**  Zero vulnerabilidades reportadas.

- **Item:**  Dados sensíveis
  - **Status:**  Parcial
  - **Evidência:**  Mascaramento e DTOs
  - **Observação:**  Validar responses de detalhe e logs.

- **Item:**  Frontend
  - **Status:**  OK no escopo analisado
  - **Evidência:**  `npm audit` e Semgrep sem achados
  - **Observação:**  Manter dependências atualizadas.

- **Item:**  Backend
  - **Status:**  OK no escopo analisado
  - **Evidência:**  Dependency-Check e Semgrep sem achados
  - **Observação:**  Complementar futuramente com DAST.
