# Relatório de Vulnerabilidades - AutoCare Hub

## 1. Título

Relatório de Vulnerabilidades do projeto AutoCare Hub - Tech Challenge FIAP.

## 2. Objetivo da análise

Este relatório registra a análise de segurança realizada no projeto AutoCare Hub a partir dos scans executados na entrega do MVP. O documento consolida as vulnerabilidades encontradas, as correções aplicadas, as evidências geradas e o risco residual aceito.

A análise considera os resultados das ferramentas realmente executadas no projeto: OWASP Dependency-Check, npm audit, Docker Scout, Semgrep, Gitleaks e JaCoCo. Os resultados abaixo não foram estimados manualmente; eles foram consolidados a partir dos relatórios gerados localmente.

## 3. Escopo analisado

| Item | Status | Evidência/observação                                                                                                           |
| --- | --- |--------------------------------------------------------------------------------------------------------------------------------|
| Backend Spring Boot | Analisado | Dependências Maven analisadas pelo OWASP Dependency-Check.                                                                     |
| Dependências backend | Analisadas e corrigidas | Relatórios em `target/dependency-check/dependency-check-report.html` e `target/dependency-check/dependency-check-report.json`. |
| Frontend Vue/Vite | Analisado | Dependências npm analisadas por `npm audit`; código incluído no Semgrep.                                                       |
| Dependências frontend | Analisadas e corrigidas | Relatório em `security-reports/frontend-dependencies/npm-audit-report.json`.                                                   |
| Dockerfile backend | Analisado e corrigido | Docker Scout executado na imagem final da API.                                                                                 |
| Dockerfile frontend | Analisado e corrigido | Docker Scout executado na imagem final do frontend.                                                                            |
| `docker-compose.yml` | Revisado na execução local | Containers configurados com usuário não privilegiado, filesystem read-only e `no-new-privileges`, conforme aplicável.          |
| Secrets no histórico Git | Analisado | Gitleaks executado sobre todos os commits, sem leaks encontrados.                                                              |
| OpenAPI/Swagger | Analisado no escopo de dependências | Vulnerabilidades anteriores no Swagger UI foram tratadas por atualização de versão.                                            |
| Cobertura de testes | Analisada | Relatórios JaCoCo em `target/site/jacoco/index.html` e `target/site/jacoco/jacoco.csv`.                                        |
| Análise estática | Analisada | Semgrep executado em arquivos Java, JavaScript, JSON, Dockerfile e regras multilinguagem.                                      |

A análise dinâmica dedicada contra a API em execução não fez parte do critério principal desta entrega. O relatório, portanto, concentra a evidência nos scans de dependências, imagens, secrets, análise estática e cobertura automatizada.

## 4. Data da análise

| Campo | Valor |
| --- | --- |
| Data de consolidação | 28/06/2026 |
| Horário do relatório Dependency-Check | 18:13:06 UTC |
| Responsável | Yasmin Barcelos Pires - RM370897 |
| Branch final de entrega | `main` |

## 5. Ferramentas utilizadas

| Ferramenta | Versão | Finalidade | Comando/forma de execução | Saída |
| --- | --- | --- | --- | --- |
| OWASP Dependency-Check Maven Plugin | 12.1.1 | Dependências backend | `mvn dependency-check:check -DautoUpdate=false` | `target/dependency-check/dependency-check-report.html` e `target/dependency-check/dependency-check-report.json` |
| JaCoCo | 0.8.12 | Cobertura de testes | `mvn verify` | `target/site/jacoco/index.html` e `target/site/jacoco/jacoco.csv` |
| npm audit | 9.6.6 | Dependências frontend | `npm audit --json` | `security-reports/frontend-dependencies/npm-audit-report.json` |
| Docker Scout | 1.20.4 | Imagens Docker | `docker scout cves <imagem> --only-severity critical,high,medium` | `security-reports/docker/docker-scout-cves.txt` e `security-reports/docker/docker-scout-frontend-cves.txt` |
| Semgrep | 1.166.0 | Análise estática Java/JavaScript/Dockerfile | Regras `p/java`, `p/javascript` e `p/security-audit` | `security-reports/static-analysis/semgrep.json` |
| Gitleaks | Imagem Docker `latest` em 20/06/2026 | Secrets no histórico Git | `gitleaks detect` | `security-reports/secrets/gitleaks.json` |

## 6. Ambiente analisado

| Campo | Valor |
| --- | --- |
| Sistema operacional | Windows, execução local em PowerShell |
| Java | Java 21 |
| Maven | Maven local do projeto |
| Banco usado no scan de dependências | Não aplicável ao Dependency-Check |
| Perfil Spring usado no scan de dependências | Não aplicável ao Dependency-Check |
| Observação | O relatório JSON do Dependency-Check informa `engineVersion` 12.1.1 e data `2026-06-28T18:13:06.803721400Z`. |

## 7. Resumo executivo

O primeiro scan de dependências backend apontou vulnerabilidades em bibliotecas centrais do runtime, incluindo Spring Boot, Spring Framework, Spring Security, Tomcat, PostgreSQL JDBC, Log4j API, Commons Compress, Commons Lang e Swagger UI. As dependências foram atualizadas e o OWASP Dependency-Check foi executado novamente.

O frontend também passou por revisão. O scan inicial do `npm audit` apontou vulnerabilidades altas transitivas em Vite/esbuild. Em 20/06/2026, uma nova execução identificou uma vulnerabilidade moderada transitiva em `js-yaml`, corrigida com atualização do lockfile.

As imagens Docker também foram revisadas. No backend, o Docker Scout encontrou vulnerabilidades associadas ao pacote `/usr/bin/pebble`, presente na imagem base anterior. O runtime foi migrado para `gcr.io/distroless/java21-debian12:nonroot`, eliminando esses achados. No frontend, a imagem Nginx/Alpine inicial apresentou 75 CVEs. A troca para uma imagem Nginx unprivileged slim, fixada por digest, reduziu o resultado para 0 críticas, 0 altas e 1 média em BusyBox, sem versão corrigida indicada pelo scanner na data da análise.

Resultado final do OWASP Dependency-Check:

| Indicador | Resultado |
| --- | ---: |
| Dependências analisadas | 127 |
| Artefatos vulneráveis | 0 |
| Ocorrências de vulnerabilidades | 0 |
| Achados únicos | 0 |
| Exceções de análise | 0 |
| Status do build do Dependency-Check | Sucesso |

No estado final da entrega, não há vulnerabilidades críticas ou altas abertas nos scans consolidados. O único risco residual documentado é uma CVE média na imagem frontend, aceita temporariamente por ausência de versão corrigida indicada pelo Docker Scout.

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

O JaCoCo mede domínio, aplicação, controllers REST, segurança, mappers e adapters de persistência. Foram excluídos apenas o bootstrap da aplicação, classes geradas automaticamente pelo OpenAPI e records estruturais de comando, consulta e saída sem lógica própria.

O gate executado por `mvn verify` exige no mínimo 90% de instruções, linhas e branches.

Resultado de qualidade revalidado em 28/06/2026:

| Métrica | Coberto | Não coberto | Cobertura |
| --- | ---: | ---: | ---: |
| Instruções | 9.701 | 372 | 96,31% |
| Branches | 447 | 49 | 90,12% |
| Linhas | 2.333 | 66 | 97,25% |
| Métodos | 641 | 32 | 95,25% |

Resultado validado com 143 testes automatizados e `mvn verify` concluindo com sucesso.

## 10. Vulnerabilidades encontradas e tratadas

### VULN-001 - Log4j API

| Campo | Valor |
| --- | --- |
| Ferramenta | OWASP Dependency-Check |
| Severidade | Alta/Média |
| Pacote afetado | `log4j-api-2.24.3.jar` |
| Descrição | CVEs reportados no scan inicial. |
| Impacto | Risco em biblioteca de logging/transitiva. |
| Correção aplicada | Atualização do BOM Spring Boot, passando a resolver Log4j API 2.25.4. |
| Status | Corrigido |
| Evidência | Scan final sem achados em `target/dependency-check/dependency-check-report.json`. |

### VULN-002 - PostgreSQL JDBC

| Campo | Valor |
| --- | --- |
| Ferramenta | OWASP Dependency-Check |
| Severidade | Alta |
| Pacote afetado | `postgresql-42.7.10.jar` |
| Descrição | CVE reportado no scan inicial. |
| Impacto | Risco associado ao driver JDBC PostgreSQL. |
| Correção aplicada | Atualização por BOM para PostgreSQL JDBC 42.7.11. |
| Status | Corrigido |
| Evidência | Scan final sem achados. |

### VULN-003 - Spring Boot

| Campo | Valor |
| --- | --- |
| Ferramenta | OWASP Dependency-Check |
| Severidade | Crítica/Alta/Média |
| Pacote afetado | `spring-boot-3.5.13.jar` e starters |
| Descrição | CVEs reportados no scan inicial para Spring Boot. |
| Impacto | Risco no framework base da API. |
| Correção aplicada | Atualização do parent/BOM do Spring Boot. |
| Status | Corrigido |
| Evidência | Scan final sem achados. |

### VULN-004 - Spring Framework

| Campo | Valor |
| --- | --- |
| Ferramenta | OWASP Dependency-Check |
| Severidade | Alta/Média/Baixa |
| Pacote afetado | `spring-core-6.2.17.jar` e `spring-web-6.2.17.jar` |
| Descrição | CVEs reportados no scan inicial para Spring Framework. |
| Impacto | Risco transversal na infraestrutura web e core. |
| Correção aplicada | Atualização transitiva do Spring Framework pelo BOM do Spring Boot. |
| Status | Corrigido |
| Evidência | Scan final sem achados. |

### VULN-005 - Spring Security

| Campo | Valor |
| --- | --- |
| Ferramenta | OWASP Dependency-Check |
| Severidade | Alta/Média/Baixa |
| Pacote afetado | `spring-security-core-6.5.9.jar` e `spring-security-web-6.5.9.jar` |
| Descrição | CVEs reportados no scan inicial para Spring Security. |
| Impacto | Risco em autenticação e autorização. |
| Correção aplicada | Atualização transitiva do Spring Security pelo BOM do Spring Boot. |
| Status | Corrigido |
| Evidência | Scan final sem achados. |

### VULN-006 - Tomcat embutido

| Campo | Valor |
| --- | --- |
| Ferramenta | OWASP Dependency-Check |
| Severidade | Crítica/Alta/Média/Baixa |
| Pacote afetado | `tomcat-embed-core-10.1.53.jar` |
| Descrição | CVEs reportados no scan inicial para Tomcat embutido. |
| Impacto | Risco no servidor HTTP embutido. |
| Correção aplicada | Atualização transitiva do Tomcat pelo BOM do Spring Boot. |
| Status | Corrigido |
| Evidência | Scan final sem achados. |

### VULN-007 - Commons Compress

| Campo | Valor |
| --- | --- |
| Ferramenta | OWASP Dependency-Check |
| Severidade | Média |
| Pacote afetado | `commons-compress-1.24.0.jar` |
| Descrição | CVE-2024-25710 e CVE-2024-26308 no scan intermediário. |
| Impacto | Risco em processamento de arquivos compactados, em escopo de teste/transitivo. |
| Correção aplicada | Override para Commons Compress 1.28.0. |
| Status | Corrigido |
| Evidência | Scan final sem achados. |

### VULN-008 - Commons Lang

| Campo | Valor |
| --- | --- |
| Ferramenta | OWASP Dependency-Check |
| Severidade | Média |
| Pacote afetado | `commons-lang3-3.17.0.jar` |
| Descrição | CVE reportado no scan inicial. |
| Impacto | Risco moderado em biblioteca utilitária transitiva. |
| Correção aplicada | Atualização transitiva para Commons Lang 3.20.0. |
| Status | Corrigido |
| Evidência | Scan final sem achados. |

### VULN-009 - Swagger UI

| Campo | Valor |
| --- | --- |
| Ferramenta | OWASP Dependency-Check |
| Severidade | Média/Desconhecida |
| Pacote afetado | `swagger-ui-5.32.2.jar` |
| Descrição | CVEs/achados DOMPurify no bundle JavaScript do Swagger UI. |
| Impacto | Risco na interface de documentação, principalmente se exposta fora do ambiente acadêmico. |
| Correção aplicada | Atualização direta para Swagger UI 5.32.6. |
| Status | Corrigido |
| Evidência | Scan final sem achados. |

### VULN-010 - Vite/esbuild/plugin Vue

| Campo | Valor |
| --- | --- |
| Ferramenta | npm audit |
| Severidade | Alta |
| Pacotes afetados | `vite`, `esbuild`, `@vitejs/plugin-vue` |
| Descrição | `npm audit` apontou 3 vulnerabilidades altas com origem em `esbuild` e efeito transitivo em Vite/plugin Vue. |
| Impacto | Risco de supply chain/RCE em ambiente de build, conforme advisory dos pacotes. |
| Correção aplicada | Atualização para `vite` 8.0.16 e `@vitejs/plugin-vue` 6.0.7; lockfile regenerado. |
| Status | Corrigido |
| Evidência | `security-reports/frontend-dependencies/npm-audit-report.json` com 0 vulnerabilidades. |

### VULN-011 - js-yaml transitivo

| Campo | Valor |
| --- | --- |
| Ferramenta | npm audit |
| Severidade | Média |
| Pacote afetado | `js-yaml-4.1.1` transitivo do ESLint |
| Descrição | GHSA-h67p-54hq-rp68, com risco de DoS por complexidade quadrática. |
| Impacto | Impacto no ferramental de desenvolvimento/lint. |
| Correção aplicada | Execução de `npm audit fix`, atualização transitiva e regeneração do lockfile. |
| Status | Corrigido |
| Evidência | `npm audit --json` de 20/06/2026 com 0 vulnerabilidades. |

### VULN-012 - Imagem backend anterior

| Campo | Valor |
| --- | --- |
| Ferramenta | Docker Scout |
| Severidade | Crítica/Alta/Média |
| Pacote afetado | `/usr/bin/pebble` na imagem backend anterior |
| Descrição | 12 CVEs em pacote não utilizado pelo runtime Java. |
| Impacto | Superfície de ataque desnecessária na imagem de execução. |
| Correção aplicada | Migração para distroless Java 21 non-root. |
| Status | Corrigido |
| Evidência | `security-reports/docker/docker-scout-cves.txt` sem vulnerabilidades. |

### VULN-013 - Imagem frontend anterior

| Campo | Valor |
| --- | --- |
| Ferramenta | Docker Scout |
| Severidade | Crítica/Alta/Média |
| Pacote afetado | Nginx 1.27 sobre Alpine 3.21 |
| Descrição | 75 CVEs na imagem frontend inicial. |
| Impacto | Vulnerabilidades do sistema operacional do container web. |
| Correção aplicada | Migração para imagem Nginx unprivileged slim fixada por digest. |
| Status | Corrigido |
| Evidência | Scan final sem vulnerabilidades críticas ou altas. |

### RISK-001 - BusyBox na imagem frontend

| Campo | Valor |
| --- | --- |
| Ferramenta | Docker Scout |
| Severidade | Média |
| Pacote afetado | BusyBox 1.37.0-r30 |
| Descrição | CVE-2025-60876. |
| Impacto | Risco residual na imagem base frontend. |
| Mitigação aplicada | Container non-root, filesystem read-only e sem novos privilégios. |
| Status | Aceito temporariamente |
| Evidência | Scanner informa `Fixed version: not fixed`. |

## 11. Análise por ferramenta

### 11.1 OWASP Dependency-Check - backend

| Campo | Valor |
| --- | --- |
| Comando | `mvn dependency-check:check -DautoUpdate=false` |
| Versão da engine | 12.1.1 |
| Relatórios | `target/dependency-check/dependency-check-report.html` e `target/dependency-check/dependency-check-report.json` |
| Resultado inicial | Falhava por vulnerabilidades com CVSS maior ou igual a 7,0. |
| Resultado final | Build com sucesso, zero vulnerabilidades reportadas. |
| Dependências analisadas no scan final | 127 |
| Artefatos vulneráveis no scan final | 0 |
| Ocorrências no scan final | 0 |
| Exceções de análise no scan final | 0 |

Observação técnica: o analisador Sonatype OSS Index retornou 401 sem credenciais. Para manter o scan local reproduzível, o plugin foi configurado com `ossindexAnalyzerEnabled=false`. O scan continuou usando NVD, Known Exploited Vulnerabilities e RetireJS.

### 11.2 npm audit - frontend

| Campo | Valor |
| --- | --- |
| Comando | `npm audit --json` |
| Relatório | `security-reports/frontend-dependencies/npm-audit-report.json` |
| Resultado inicial | 3 vulnerabilidades altas em `vite`, `esbuild` e `@vitejs/plugin-vue`; nova ocorrência moderada em `js-yaml` identificada em 20/06/2026. |
| Resultado final | 0 vulnerabilidades. |
| Ação tomada | Atualização de Vite/plugin Vue, correção transitiva de `js-yaml` e regeneração do `package-lock.json`. |

### 11.3 Docker Scout - imagens

| Campo | Valor |
| --- | --- |
| Ferramenta | Docker Scout 1.20.4 |
| Imagens analisadas | `soat-fiap-app:latest` e `soat-fiap-frontend:latest` |
| Relatórios | `security-reports/docker/docker-scout-cves.txt` e `security-reports/docker/docker-scout-frontend-cves.txt` |
| Backend final | 0 críticas, 0 altas, 0 médias e 0 baixas. |
| Frontend inicial | 4 críticas, 26 altas e 45 médias. |
| Frontend final | 0 críticas, 0 altas e 1 média sem correção disponível. |
| Ação tomada | Backend distroless; frontend Nginx unprivileged slim fixado por digest. |

### 11.4 Semgrep - análise estática

| Campo | Valor |
| --- | --- |
| Ferramenta | Semgrep 1.166.0 |
| Relatório | `security-reports/static-analysis/semgrep.json` |
| Escopo | 200 arquivos, 187 regras, Java, JavaScript, JSON, Dockerfile e regras multilinguagem. |
| Resultado | 0 achados e 0 erros. |

### 11.5 Gitleaks - secrets

| Campo | Valor |
| --- | --- |
| Ferramenta | Gitleaks |
| Relatório | `security-reports/secrets/gitleaks.json` |
| Escopo | 36 commits e aproximadamente 3,24 MB analisados. |
| Resultado | 0 leaks encontrados. |

## 12. Vulnerabilidades corrigidas

| ID | Origem | Correção aplicada | Arquivos afetados | Evidência |
| --- | --- | --- | --- | --- |
| VULN-001 a VULN-009 | Dependências Maven | Atualização de dependências e novo scan limpo. | `pom.xml` | `target/dependency-check/dependency-check-report.json` com 0 vulnerabilidades. |
| VULN-010 | Dependências frontend | Atualização de Vite/plugin Vue e novo audit limpo. | `frontend/package.json`, `frontend/package-lock.json` | `security-reports/frontend-dependencies/npm-audit-report.json` com 0 vulnerabilidades. |
| VULN-011 | Dependência transitiva frontend | Atualização transitiva de `js-yaml` via `npm audit fix`. | `frontend/package-lock.json` | Audit de 20/06/2026 com 0 vulnerabilidades. |
| VULN-012 | Imagem backend | Substituição da imagem runtime Ubuntu/Temurin por distroless Java 21 non-root. | `Dockerfile`, `docker-compose.yml` | Docker Scout final com 0 vulnerabilidades. |
| VULN-013 | Imagem frontend | Substituição da imagem Nginx/Alpine antiga por variante unprivileged slim. | `frontend/Dockerfile` | Scan final sem vulnerabilidades críticas ou altas. |

## 13. Risco residual aceito

| ID | Severidade | Justificativa | Mitigação existente | Responsável pela aceitação | Revisão |
| --- | --- | --- | --- | --- | --- |
| RISK-001 | Média | BusyBox não possui versão corrigida indicada pelo Docker Scout na base analisada. | Imagem slim, usuário não privilegiado, filesystem read-only e `no-new-privileges`. | Yasmin Barcelos Pires | Próxima atualização da imagem base. |

Não há vulnerabilidades críticas ou altas abertas nos scans finais. O risco residual registrado é médio e foi aceito temporariamente porque não havia versão corrigida indicada pela ferramenta na data da análise.

## 14. Boas práticas de segurança implementadas

| Controle | Status | Evidência/observação |
| --- | --- | --- |
| JWT assinado com segredo vindo de variável de ambiente | Implementado | Configuração `security.jwt.secret`/`JWT_SECRET`; aplicação falha se o segredo não for informado. |
| Expiração de token configurável | Implementado | `security.jwt.expiration-minutes`/`JWT_EXPIRATION_MINUTES`. |
| Senhas com BCrypt | Implementado | `PasswordEncoder` com `BCryptPasswordEncoder`. |
| APIs administrativas protegidas por autenticação | Implementado | Configuração Spring Security/JWT. |
| DTOs explícitos para evitar mass assignment | Implementado | Requests/responses separados das entidades JPA. |
| Jackson rejeitando campos desconhecidos | Implementado | `fail-on-unknown-properties: true` e `ObjectMapper` de compatibilidade. |
| Validação real de CPF/CNPJ | Implementado | Value object de documento no domínio. |
| Validação real de placa | Implementado | Value object de placa no domínio. |
| Tratamento global de exceções | Implementado | Responses padronizados sem stacktrace intencional. |
| CORS sem wildcard | Implementado | Configuração rejeita origem `*` e `null`. |
| Swagger configurável para avaliação local | Implementado/documentado | Contrato OpenAPI desabilitável por `SPRINGDOC_API_DOCS_ENABLED`; Swagger UI local servida para avaliação acadêmica. |
| `.env.example` sem secrets reais | Implementado/documentado | Variáveis sensíveis devem ser preenchidas localmente. |
| Dependências backend sem CVEs no scan final | Corrigido | `mvn dependency-check:check` final com sucesso. |
| Dependências frontend sem vulnerabilidades no audit final | Corrigido | `npm audit --json` final com 0 vulnerabilidades. |
| Runtime backend distroless non-root | Implementado | Imagem final sem shell ou gerenciador de pacotes, executada como `nonroot`. |
| Container read-only e sem novos privilégios | Implementado | `read_only: true` e `no-new-privileges:true` no Compose, conforme aplicável. |
| Imagem backend sem vulnerabilidades no Scout final | Corrigido | Relatório final com 0 vulnerabilidades. |
| Imagem frontend sem críticas ou altas | Validado | 1 CVE média aceita temporariamente. |
| Histórico Git sem secrets detectados | Validado | Gitleaks analisou 36 commits e encontrou 0 leaks. |
| Análise estática sem achados | Validado | Semgrep executou 187 regras em 200 arquivos com 0 achados. |

## 15. Evidências

Os caminhos abaixo indicam as evidências geradas localmente durante a validação. Os arquivos brutos ficam fora do versionamento por padrão; este relatório consolida os resultados relevantes para a entrega.

| Evidência | Caminho | Descrição |
| --- | --- | --- |
| Relatório Dependency-Check HTML | `target/dependency-check/dependency-check-report.html` | Relatório navegável com dependências, CVEs, severidades e referências. |
| Relatório Dependency-Check JSON | `target/dependency-check/dependency-check-report.json` | Evidência estruturada usada para consolidar este relatório. |
| Relatório npm audit JSON | `security-reports/frontend-dependencies/npm-audit-report.json` | Evidência estruturada do frontend com 0 vulnerabilidades. |
| Relatório Docker Scout backend | `security-reports/docker/docker-scout-cves.txt` | Evidência do scan final da imagem backend com 0 vulnerabilidades. |
| Relatório Docker Scout frontend | `security-reports/docker/docker-scout-frontend-cves.txt` | Evidência do scan final com 1 CVE média e nenhuma crítica/alta. |
| Relatório Gitleaks | `security-reports/secrets/gitleaks.json` | Evidência estruturada com 0 leaks. |
| Relatório Semgrep | `security-reports/static-analysis/semgrep.json` | Evidência estruturada com 0 achados e 0 erros. |
| Relatório JaCoCo HTML | `target/site/jacoco/index.html` | Relatório navegável de cobertura atual. |
| Relatório JaCoCo CSV | `target/site/jacoco/jacoco.csv` | Evidência estruturada usada para consolidar cobertura. |

Lista consolidada de caminhos:

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

## 16. Checklist de segurança

| Item | Status | Evidência | Observação |
| --- | --- | --- | --- |
| JWT | OK | Configuração por variável de ambiente | Segredo real deve existir apenas fora do repositório. |
| Senhas | OK | BCrypt | Senha/hash não devem ser retornados em DTOs. |
| Secrets | OK | Gitleaks com 0 leaks em 36 commits | Reexecutar antes de novas entregas. |
| Logs | OK no escopo estático | Semgrep sem achados | Avaliação dinâmica pode ser feita em ciclo complementar. |
| Validação de entrada | OK | CPF/CNPJ e placa em value objects | Manter testes cobrindo documentos e placas inválidos. |
| Tratamento de erros | OK | Handler global esperado | Stacktrace não deve ser exposto ao usuário. |
| CORS | OK | Configuração sem wildcard | Revisar origens permitidas por ambiente. |
| Swagger | OK no MVP | Versão atualizada e flags para desabilitar | Restringir em ambientes produtivos. |
| Docker | OK com risco médio aceito | Backend sem vulnerabilidades; frontend sem críticas/altas e com 1 média aceita | Reexecutar a cada atualização da imagem. |
| Banco de dados | OK no escopo do MVP | Credenciais por `.env` | Scan específico do banco não foi critério desta análise. |
| Dependências backend | OK | Dependency-Check final | Zero vulnerabilidades reportadas. |
| Dependências frontend | OK | `npm audit` final | Zero vulnerabilidades reportadas. |
| Dados sensíveis | OK no escopo revisado | DTOs e validações de entrada | Responses e logs devem continuar sem dados sensíveis desnecessários. |
| Frontend | OK no escopo analisado | `npm audit` e Semgrep sem achados | Manter dependências atualizadas. |
| Backend | OK no escopo analisado | Dependency-Check e Semgrep sem achados | Análise dinâmica pode complementar a segurança em ciclos futuros. |

## 17. Recomendações de manutenção

1. Manter o Spring Boot BOM atualizado e evitar overrides manuais sem necessidade.
2. Reexecutar `mvn dependency-check:check` antes de novas entregas ou ciclos de manutenção.
3. Manter o frontend atualizado e reexecutar `npm audit` após alterações de dependências.
4. Reexecutar Docker Scout a cada atualização das imagens base.
5. Reexecutar Gitleaks antes de publicar ou entregar novas versões.
6. Reexecutar Semgrep após mudanças relevantes de código.
7. Configurar CI para bloquear vulnerabilidades críticas e altas sem aceite formal.
8. Restringir Swagger em ambientes produtivos.
9. Manter a cobertura global acima dos gates de 90% para instruções, linhas e branches.
10. DAST dedicado fora do critério obrigatório desta entrega.

## 18. Conclusão

O projeto AutoCare Hub corrigiu as vulnerabilidades identificadas nas dependências backend, dependências frontend e imagens Docker analisadas. O Dependency-Check, o npm audit e o Docker Scout da imagem backend ficaram sem vulnerabilidades no resultado final.

A imagem frontend ficou sem vulnerabilidades críticas ou altas e mantém 1 CVE média em BusyBox, aceita temporariamente porque o Docker Scout não indicou versão corrigida para a base analisada. Esse risco foi registrado, mitigado com configuração de container mais restritiva e direcionado para revisão na próxima atualização da imagem base.

Considerando o escopo analisado por Dependency-Check, npm audit, Docker Scout, Gitleaks, Semgrep e JaCoCo, o sistema está apto para a entrega acadêmica do Tech Challenge.
