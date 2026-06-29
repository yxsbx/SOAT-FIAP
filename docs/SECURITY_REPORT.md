# Relatório de Vulnerabilidades - AutoCare Hub

## 1. Título

Relatório de Vulnerabilidades do projeto AutoCare Hub - Tech Challenge FIAP.

## 2. Objetivo da análise

Este relatório registra a análise de segurança realizada no projeto AutoCare Hub a partir dos scans executados para a entrega do MVP. O documento consolida as vulnerabilidades encontradas, as correções aplicadas, as evidências geradas e os riscos residuais aceitos temporariamente.

A análise considera apenas ferramentas realmente executadas no projeto: OWASP Dependency-Check, npm audit, Docker Scout, OWASP ZAP, Semgrep, Gitleaks e JaCoCo. Os resultados abaixo foram consolidados a partir dos relatórios gerados localmente. O detalhamento de qualidade, cobertura e análise estática fica em `docs/STATIC_ANALYSIS.md`.

## 3. Escopo analisado

| Item                     | Status                     | Evidência/observação                                                                                                                                                       |
|--------------------------|----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Backend Spring Boot      | Analisado                  | Dependências Maven analisadas pelo OWASP Dependency-Check.                                                                                                                 |
| Dependências backend     | Analisadas e corrigidas    | Relatórios versionados em `security-reports/backend-dependencies/dependency-check-report.html` e `security-reports/backend-dependencies/dependency-check-report.json`.     |
| Frontend Vue/Vite        | Analisado                  | Dependências npm analisadas por `npm audit`; código incluído no Semgrep.                                                                                                   |
| Dependências frontend    | Analisadas e corrigidas    | Relatório em `security-reports/frontend-dependencies/npm-audit-report.json`.                                                                                               |
| Dockerfile backend       | Analisado e corrigido      | Docker Scout executado na imagem final da API.                                                                                                                             |
| Dockerfile frontend      | Analisado e corrigido      | Docker Scout executado na imagem final do frontend.                                                                                                                        |
| `docker-compose.yml`     | Revisado na execução local | Containers configurados com usuário não privilegiado, filesystem read-only e `no-new-privileges`, conforme aplicável.                                                      |
| API local                | Analisada                  | OWASP ZAP API scan executado contra `/v3/api-docs`, com 0 falhas e 2 avisos revisados.                                                                                     |
| Secrets no histórico Git | Analisado                  | Gitleaks executado sobre todos os commits, sem leaks encontrados.                                                                                                          |
| OpenAPI/Swagger          | Analisado e atualizado     | Vulnerabilidades anteriores no Swagger UI foram tratadas por atualização de versão.                                                                                        |
| Cobertura de testes      | Analisada                  | Relatórios JaCoCo em `target/site/jacoco/index.html` e `target/site/jacoco/jacoco.csv`. A cobertura é usada como evidência de qualidade, não como scan de vulnerabilidade. |
| Análise estática         | Analisada                  | Semgrep executado em arquivos Java, JavaScript, JSON, Dockerfile e regras multilinguagem.                                                                                  |

A análise dinâmica com OWASP ZAP foi usada como evidência complementar contra a API local. Os gates principais da entrega continuam sendo Dependency-Check, npm audit, Docker Scout, Gitleaks, Semgrep, JaCoCo e a validação dos testes automatizados.

## 3.1 Segurança desde o desenho da solução

O AutoCare Hub trata dados pessoais e operacionais de uma oficina, como CPF/CNPJ de clientes, telefone, e-mail, dados de veículos, credenciais de usuários administrativos, tokens JWT, estoque, orçamentos e Ordens de Serviço. Por isso, os controles de segurança foram considerados nos fluxos principais do MVP, e não apenas no scan final.

Controles aplicados no projeto:

- validação de CPF/CNPJ no value object `Document`, com cálculo dos dígitos verificadores;
- validação de placas antigas e Mercosul no value object `Plate`;
- autenticação JWT para APIs administrativas e para operações de cliente autenticado;
- autorização por papel (`ADMIN`, `EMPLOYEE`, `CUSTOMER`) e por vínculo de cliente em endpoints de OS;
- senha com BCrypt;
- segredo JWT obrigatório por variável de ambiente;
- `.env.example` com placeholders, sem secrets reais;
- `.env` e `target/` fora do versionamento;
- `security-reports/` versionado como evidência revisada da entrega;
- DTOs explícitos para reduzir risco de mass assignment;
- Spring Data/JPA e `JdbcTemplate` com parâmetros bindados, sem SQL montado por concatenação;
- tratamento global de erros sem retorno de stack trace ao usuário;
- CORS sem wildcard e com origens configuráveis;
- headers de segurança configurados no Spring Security;
- runtime backend distroless/non-root;
- Docker Compose com `read_only` e `no-new-privileges`, conforme aplicável;
- scans de dependências, secrets, imagens Docker e análise estática.

Ficaram fora do escopo do MVP: auditoria produtiva, WAF, rotação automatizada de secrets e observabilidade de segurança em produção. O frontend demonstrativo usa `localStorage` para o JWT, decisão aceita para o escopo acadêmico local. Como mitigação, o projeto evita renderização HTML insegura no frontend e mantém validações e controles de autorização no backend.

## 3.2 Avaliação de ameaças

| Ameaça                                 | Onde poderia ocorrer                                       | Controle aplicado                                                                                                 | Evidência                                                                                                  |
|----------------------------------------|------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------|
| Acesso administrativo sem autenticação | CRUDs de clientes, veículos, serviços, peças, estoque e OS | Spring Security exige JWT e papel administrativo/funcionário                                                      | `SecurityConfig` e `SecurityAuthorizationIntegrationTest`                                                  |
| Token ausente ou inválido              | Qualquer endpoint protegido                                | Filtro JWT limpa o contexto e retorna 401 padronizado                                                             | `JwtAuthenticationFilter` e `SecurityConfig`                                                               |
| Cliente acessando OS de outro cliente  | Detalhe, listagem, tracking e aprovação de orçamento       | `AuthorizationService` valida `customerId` vinculado ao usuário                                                   | `ServiceOrdersController` e testes de autorização                                                          |
| CPF/CNPJ inválido                      | Cadastro e atualização de cliente                          | `Document.from(...)` normaliza e valida dígitos                                                                   | `Document`, `CustomerTest`, `SensitiveDataValidationIntegrationTest`                                       |
| Placa inválida                         | Cadastro e atualização de veículo                          | `Plate.from(...)` aceita formatos brasileiro antigo e Mercosul                                                    | `Plate`, `VehicleTest`, `SensitiveDataValidationIntegrationTest`                                           |
| SQL Injection                          | Filtros, consultas por ID/documento/placa e persistência   | Repositories Spring Data/JPA, JPQL estática e `JdbcTemplate` com placeholders                                     | `ServiceOrderJpaRepository`, `StockMovementRepositoryAdapter`                                              |
| Command Injection                      | Backend de negócio                                         | O backend não executa comandos do sistema operacional no fluxo do MVP                                             | Revisão por busca de `Runtime.getRuntime` e `ProcessBuilder`                                               |
| XSS em campos textuais                 | Campos renderizados no frontend demonstrativo              | Vue escapa interpolação por padrão; não há uso identificado de `v-html`, `innerHTML`, `eval` ou APIs equivalentes | Busca estática no diretório `frontend/`                                                                    |
| Exposição de secrets                   | Código, `.env`, relatórios e histórico Git                 | `.gitignore`, `.env.example` sem secrets e Gitleaks sem leaks                                                     | `.gitignore`, `.env.example`, `security-reports/secrets/gitleaks.json`                                     |
| Dependência vulnerável                 | Maven/npm                                                  | Dependency-Check e npm audit com resultado final limpo                                                            | `security-reports/backend-dependencies/*` e `security-reports/frontend-dependencies/npm-audit-report.json` |
| Imagem Docker vulnerável               | Runtime da API e frontend                                  | Imagens non-root; riscos médios residuais revisados e aceitos                                                     | Relatórios Docker Scout                                                                                    |
| Erro interno expondo detalhe técnico   | Responses de erro da API                                   | `RestExceptionHandler` padroniza resposta e não retorna stack trace                                               | `RestExceptionHandler`                                                                                     |
| Alteração indevida de status da OS     | Endpoint de status                                         | Endpoint exige `ADMIN` ou `EMPLOYEE`; domínio valida transições                                                   | `SecurityConfig`, `ServiceOrder`, testes de fluxo                                                          |
| Uso indevido de estoque                | Movimentação/reserva/baixa de peças                        | Endpoints de estoque exigem `ADMIN` ou `EMPLOYEE`; domínio valida quantidade e saldo                              | `SecurityConfig`, `Part`, `PartStockFlowIntegrationTest`                                                   |

## 4. Data da análise

| Campo                                 | Valor                            |
|---------------------------------------|----------------------------------|
| Data de consolidação                  | 29/06/2026                       |
| Horário do relatório Dependency-Check | 2026-06-29T20:51:40Z             |
| Responsável                           | Yasmin Barcelos Pires - RM370897 |
| Branch final de entrega               | `main`                           |

## 5. Ferramentas utilizadas

| Ferramenta                          | Versão                                         | Finalidade                                  | Comando/forma de execução                                                    | Saída                                                                                                                                       |
|-------------------------------------|------------------------------------------------|---------------------------------------------|------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|
| OWASP Dependency-Check Maven Plugin | 12.1.1                                         | Dependências backend                        | `mvn dependency-check:check -DautoUpdate=false`                              | `security-reports/backend-dependencies/dependency-check-report.html` e `security-reports/backend-dependencies/dependency-check-report.json` |
| JaCoCo                              | 0.8.12                                         | Cobertura de testes                         | `mvn verify`                                                                 | `target/site/jacoco/index.html` e `target/site/jacoco/jacoco.csv`                                                                           |
| npm audit                           | 9.6.6                                          | Dependências frontend                       | `npm audit --json`                                                           | `security-reports/frontend-dependencies/npm-audit-report.json`                                                                              |
| Docker Scout                        | 1.20.4                                         | Imagens Docker                              | `docker scout cves <imagem> --only-severity critical,high,medium`            | `security-reports/docker/docker-scout-cves.txt` e `security-reports/docker/docker-scout-frontend-cves.txt`                                  |
| OWASP ZAP                           | Imagem Docker `ghcr.io/zaproxy/zaproxy:stable` | DAST complementar da API                    | `zap-api-scan.py -t http://host.docker.internal:8080/v3/api-docs -f openapi` | `security-reports/dast/zap-api-report.html` e `security-reports/dast/zap-api-report.json`                                                   |
| Semgrep                             | 1.166.0                                        | Análise estática Java/JavaScript/Dockerfile | Regras `p/java`, `p/javascript` e `p/security-audit`                         | `security-reports/static-analysis/semgrep.json`                                                                                             |
| Gitleaks                            | Imagem Docker `latest` em 20/06/2026           | Secrets no histórico Git                    | `gitleaks detect`                                                            | `security-reports/secrets/gitleaks.json`                                                                                                    |

## 6. Ambiente analisado

| Campo                                       | Valor                                                                                                        |
|---------------------------------------------|--------------------------------------------------------------------------------------------------------------|
| Sistema operacional                         | Windows, execução local em PowerShell                                                                        |
| Java                                        | Java 21                                                                                                      |
| Maven                                       | Maven local do projeto                                                                                       |
| Banco usado no scan de dependências         | Não aplicável ao Dependency-Check                                                                            |
| Perfil Spring usado no scan de dependências | Não aplicável ao Dependency-Check                                                                            |
| Observação                                  | O relatório JSON do Dependency-Check informa `engineVersion` 12.1.1 e data `2026-06-29T20:51:40.997196900Z`. |

## 7. Resumo executivo

O primeiro scan de dependências backend apontou vulnerabilidades em bibliotecas centrais do runtime, incluindo Spring Boot, Spring Framework, Spring Security, Tomcat, PostgreSQL JDBC, Log4j API, Commons Compress, Commons Lang e Swagger UI. As dependências foram atualizadas e o OWASP Dependency-Check foi executado novamente.

O frontend também passou por revisão. O scan inicial do `npm audit` apontou vulnerabilidades altas transitivas em Vite/esbuild. Em 20/06/2026, uma nova execução identificou uma vulnerabilidade moderada transitiva em `js-yaml`, corrigida com atualização do lockfile.

As imagens Docker também foram revisadas. No backend, o Docker Scout encontrou vulnerabilidades associadas ao pacote `/usr/bin/pebble`, presente na imagem base anterior. O runtime foi migrado para `gcr.io/distroless/java21-debian12:nonroot`, eliminando os achados críticos e altos. Na última execução, o backend ficou com 0 críticas, 0 altas e 1 média em `jackson-databind 2.21.4`, transitiva de `jjwt-jackson`; o Scout indica correção em `2.21.5`, mas a disponibilidade dessa versão foi testada com `mvn dependency:get` e o artefato ainda não estava publicado no Maven Central na validação. No frontend, a imagem Nginx/Alpine inicial apresentou 75 CVEs. A troca para uma imagem Nginx unprivileged slim, fixada por digest, reduziu o resultado para 0 críticas, 0 altas e 1 média em BusyBox, sem versão corrigida indicada pelo scanner na data da análise.

Resultado final do OWASP Dependency-Check:

| Indicador                           | Resultado |
|-------------------------------------|----------:|
| Dependências analisadas             |       103 |
| Artefatos vulneráveis               |         0 |
| Ocorrências de vulnerabilidades     |         0 |
| Achados únicos                      |         0 |
| Exceções de análise                 |         0 |
| Status do build do Dependency-Check |   Sucesso |

No estado final da entrega, não há vulnerabilidades críticas ou altas abertas nos scans consolidados. Os riscos residuais documentados são duas CVEs médias em imagens Docker: uma no backend por dependência transitiva sem versão corrigida disponível no Maven Central no momento da validação, e uma no frontend por BusyBox sem versão corrigida indicada pelo Docker Scout.

## 8. Resultado geral dos scans

| Categoria             | Critical | High | Medium | Low |  Unknown | Status geral                       |
|-----------------------|---------:|-----:|-------:|----:|---------:|------------------------------------|
| Dependências backend  |        0 |    0 |      0 |   0 |        0 | Corrigido                          |
| Dependências frontend |        0 |    0 |      0 |   0 |        0 | Corrigido                          |
| Imagem backend        |        0 |    0 |      1 |   0 |        0 | Risco médio aceito temporariamente |
| Imagem frontend       |        0 |    0 |      1 |   0 |        0 | Risco médio aceito temporariamente |
| Análise estática      |        0 |    0 |      0 |   0 |        0 | Semgrep sem achados                |
| Secrets               |        0 |    0 |      0 |   0 |        0 | Gitleaks sem leaks                 |
| OWASP ZAP API scan    |        0 |    0 |      0 |   0 | 2 avisos | 0 falhas; avisos revisados         |

## 9. Cobertura de testes

O JaCoCo mede domínio, aplicação, controllers REST, segurança, mappers e adapters de persistência. Foram excluídos apenas o bootstrap da aplicação, classes geradas automaticamente pelo OpenAPI e records estruturais de comando, consulta e saída sem lógica própria.

O gate executado por `mvn verify` exige no mínimo 90% de instruções, linhas e branches.

Resultado de qualidade revalidado em 29/06/2026:

| Métrica    | Coberto | Não coberto | Cobertura |
|------------|--------:|------------:|----------:|
| Instruções |   9.701 |         372 |    96,31% |
| Branches   |     447 |          49 |    90,12% |
| Linhas     |   2.333 |          66 |    97,25% |
| Métodos    |     641 |          32 |    95,25% |

Resultado validado com 143 testes automatizados e `mvn verify` concluindo com sucesso.

## 10. Vulnerabilidades encontradas e tratadas

### VULN-001 - Log4j API

| Campo             | Valor                                                                                           |
|-------------------|-------------------------------------------------------------------------------------------------|
| Ferramenta        | OWASP Dependency-Check                                                                          |
| Severidade        | Alta/Média                                                                                      |
| Pacote afetado    | `log4j-api-2.24.3.jar`                                                                          |
| Descrição         | CVEs reportadas no scan inicial.                                                                |
| Impacto           | Risco em biblioteca de logging/transitiva.                                                      |
| Correção aplicada | Atualização do BOM Spring Boot, passando a resolver Log4j API 2.25.4.                           |
| Status            | Corrigido                                                                                       |
| Evidência         | Scan final sem achados em `security-reports/backend-dependencies/dependency-check-report.json`. |

### VULN-002 - PostgreSQL JDBC

| Campo             | Valor                                             |
|-------------------|---------------------------------------------------|
| Ferramenta        | OWASP Dependency-Check                            |
| Severidade        | Alta                                              |
| Pacote afetado    | `postgresql-42.7.10.jar`                          |
| Descrição         | CVE reportada no scan inicial.                    |
| Impacto           | Risco associado ao driver JDBC PostgreSQL.        |
| Correção aplicada | Atualização por BOM para PostgreSQL JDBC 42.7.11. |
| Status            | Corrigido                                         |
| Evidência         | Scan final sem achados.                           |

### VULN-003 - Spring Boot

| Campo             | Valor                                             |
|-------------------|---------------------------------------------------|
| Ferramenta        | OWASP Dependency-Check                            |
| Severidade        | Crítica/Alta/Média                                |
| Pacote afetado    | `spring-boot-3.5.13.jar` e starters               |
| Descrição         | CVEs reportadas no scan inicial para Spring Boot. |
| Impacto           | Risco no framework base da API.                   |
| Correção aplicada | Atualização do parent/BOM do Spring Boot.         |
| Status            | Corrigido                                         |
| Evidência         | Scan final sem achados.                           |

### VULN-004 - Spring Framework

| Campo             | Valor                                                               |
|-------------------|---------------------------------------------------------------------|
| Ferramenta        | OWASP Dependency-Check                                              |
| Severidade        | Alta/Média/Baixa                                                    |
| Pacote afetado    | `spring-core-6.2.17.jar` e `spring-web-6.2.17.jar`                  |
| Descrição         | CVEs reportadas no scan inicial para Spring Framework.              |
| Impacto           | Risco transversal na infraestrutura web e core.                     |
| Correção aplicada | Atualização transitiva do Spring Framework pelo BOM do Spring Boot. |
| Status            | Corrigido                                                           |
| Evidência         | Scan final sem achados.                                             |

### VULN-005 - Spring Security

| Campo             | Valor                                                              |
|-------------------|--------------------------------------------------------------------|
| Ferramenta        | OWASP Dependency-Check                                             |
| Severidade        | Alta/Média/Baixa                                                   |
| Pacote afetado    | `spring-security-core-6.5.9.jar` e `spring-security-web-6.5.9.jar` |
| Descrição         | CVEs reportadas no scan inicial para Spring Security.              |
| Impacto           | Risco em autenticação e autorização.                               |
| Correção aplicada | Atualização transitiva do Spring Security pelo BOM do Spring Boot. |
| Status            | Corrigido                                                          |
| Evidência         | Scan final sem achados.                                            |

### VULN-006 - Tomcat embutido

| Campo             | Valor                                                     |
|-------------------|-----------------------------------------------------------|
| Ferramenta        | OWASP Dependency-Check                                    |
| Severidade        | Crítica/Alta/Média/Baixa                                  |
| Pacote afetado    | `tomcat-embed-core-10.1.53.jar`                           |
| Descrição         | CVEs reportadas no scan inicial para Tomcat embutido.     |
| Impacto           | Risco no servidor HTTP embutido.                          |
| Correção aplicada | Atualização transitiva do Tomcat pelo BOM do Spring Boot. |
| Status            | Corrigido                                                 |
| Evidência         | Scan final sem achados.                                   |

### VULN-007 - Commons Compress

| Campo             | Valor                                                                          |
|-------------------|--------------------------------------------------------------------------------|
| Ferramenta        | OWASP Dependency-Check                                                         |
| Severidade        | Média                                                                          |
| Pacote afetado    | `commons-compress-1.24.0.jar`                                                  |
| Descrição         | CVE-2024-25710 e CVE-2024-26308 no scan intermediário.                         |
| Impacto           | Risco em processamento de arquivos compactados, em escopo de teste/transitivo. |
| Correção aplicada | Override para Commons Compress 1.28.0.                                         |
| Status            | Corrigido                                                                      |
| Evidência         | Scan final sem achados.                                                        |

### VULN-008 - Commons Lang

| Campo             | Valor                                               |
|-------------------|-----------------------------------------------------|
| Ferramenta        | OWASP Dependency-Check                              |
| Severidade        | Média                                               |
| Pacote afetado    | `commons-lang3-3.17.0.jar`                          |
| Descrição         | CVE reportada no scan inicial.                      |
| Impacto           | Risco moderado em biblioteca utilitária transitiva. |
| Correção aplicada | Atualização transitiva para Commons Lang 3.20.0.    |
| Status            | Corrigido                                           |
| Evidência         | Scan final sem achados.                             |

### VULN-009 - Swagger UI

| Campo             | Valor                                                                                     |
|-------------------|-------------------------------------------------------------------------------------------|
| Ferramenta        | OWASP Dependency-Check                                                                    |
| Severidade        | Média/Desconhecida                                                                        |
| Pacote afetado    | `swagger-ui-5.32.2.jar`                                                                   |
| Descrição         | CVEs/achados DOMPurify no bundle JavaScript do Swagger UI.                                |
| Impacto           | Risco na interface de documentação, principalmente se exposta fora do ambiente acadêmico. |
| Correção aplicada | Atualização direta para Swagger UI 5.32.6.                                                |
| Status            | Corrigido                                                                                 |
| Evidência         | Scan final sem achados.                                                                   |

### VULN-010 - Vite/esbuild/plugin Vue

| Campo             | Valor                                                                                                        |
|-------------------|--------------------------------------------------------------------------------------------------------------|
| Ferramenta        | npm audit                                                                                                    |
| Severidade        | Alta                                                                                                         |
| Pacotes afetados  | `vite`, `esbuild`, `@vitejs/plugin-vue`                                                                      |
| Descrição         | `npm audit` apontou 3 vulnerabilidades altas com origem em `esbuild` e efeito transitivo em Vite/plugin Vue. |
| Impacto           | Risco de supply chain/RCE em ambiente de build, conforme advisory dos pacotes.                               |
| Correção aplicada | Atualização para `vite` 8.0.16 e `@vitejs/plugin-vue` 6.0.7; lockfile regenerado.                            |
| Status            | Corrigido                                                                                                    |
| Evidência         | `security-reports/frontend-dependencies/npm-audit-report.json` com 0 vulnerabilidades.                       |

### VULN-011 - js-yaml transitivo

| Campo             | Valor                                                                          |
|-------------------|--------------------------------------------------------------------------------|
| Ferramenta        | npm audit                                                                      |
| Severidade        | Média                                                                          |
| Pacote afetado    | `js-yaml-4.1.1` transitivo do ESLint                                           |
| Descrição         | GHSA-h67p-54hq-rp68, com risco de DoS por complexidade quadrática.             |
| Impacto           | Impacto no ferramental de desenvolvimento/lint.                                |
| Correção aplicada | Execução de `npm audit fix`, atualização transitiva e regeneração do lockfile. |
| Status            | Corrigido                                                                      |
| Evidência         | `security-reports/frontend-dependencies/npm-audit-report.json` com 0 vulnerabilidades. |

### VULN-012 - Imagem backend anterior

| Campo             | Valor                                                                                                                             |
|-------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| Ferramenta        | Docker Scout                                                                                                                      |
| Severidade        | Crítica/Alta/Média                                                                                                                |
| Pacote afetado    | `/usr/bin/pebble` na imagem backend anterior                                                                                      |
| Descrição         | 12 CVEs em pacote não utilizado pelo runtime Java.                                                                                |
| Impacto           | Superfície de ataque desnecessária na imagem de execução.                                                                         |
| Correção aplicada | Migração para distroless Java 21 non-root.                                                                                        |
| Status            | Corrigido                                                                                                                         |
| Evidência         | Scan final sem vulnerabilidades críticas ou altas; permanece apenas o risco médio `RISK-002`, tratado como risco residual aceito. |

### VULN-013 - Imagem frontend anterior

| Campo             | Valor                                                                                                                             |
|-------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| Ferramenta        | Docker Scout                                                                                                                      |
| Severidade        | Crítica/Alta/Média                                                                                                                |
| Pacote afetado    | Nginx 1.27 sobre Alpine 3.21                                                                                                      |
| Descrição         | 75 CVEs na imagem frontend inicial.                                                                                               |
| Impacto           | Vulnerabilidades do sistema operacional do container web.                                                                         |
| Correção aplicada | Migração para imagem Nginx unprivileged slim fixada por digest.                                                                   |
| Status            | Corrigido                                                                                                                         |
| Evidência         | Scan final sem vulnerabilidades críticas ou altas; permanece apenas o risco médio `RISK-001`, tratado como risco residual aceito. |

### RISK-001 - BusyBox na imagem frontend

| Campo              | Valor                                                             |
|--------------------|-------------------------------------------------------------------|
| Ferramenta         | Docker Scout                                                      |
| Severidade         | Média                                                             |
| Pacote afetado     | BusyBox 1.37.0-r30                                                |
| Descrição          | CVE-2025-60876.                                                   |
| Impacto            | Risco residual na imagem base frontend.                           |
| Mitigação aplicada | Container non-root, filesystem read-only e sem novos privilégios. |
| Status             | Aceito temporariamente                                            |
| Evidência          | Scanner informa `Fixed version: not fixed`.                       |

### RISK-002 - Jackson Databind transitivo na imagem backend

| Campo              | Valor                                                                                                                                 |
|--------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| Ferramenta         | Docker Scout                                                                                                                          |
| Severidade         | Média                                                                                                                                 |
| Pacote afetado     | `jackson-databind 2.21.4`, transitivo de `jjwt-jackson`                                                                               |
| Descrição          | CVE-2026-54515. O Docker Scout indica correção em `2.21.5`, mas `mvn dependency:get` confirmou que essa versão ainda não estava publicada no Maven Central na validação. |
| Impacto            | Risco residual em biblioteca empacotada na imagem backend.                                                                            |
| Mitigação aplicada | Imagem distroless non-root, Dependency-Check Maven limpo e dependência usada no fluxo interno de JWT.                                 |
| Status             | Aceito temporariamente                                                                                                                |
| Evidência          | `security-reports/docker/docker-scout-cves.txt`.                                                                                      |

## 11. Análise por ferramenta

### 11.1 OWASP Dependency-Check - backend

| Campo                                 | Valor                                                                                                                                       |
|---------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|
| Comando                               | `mvn dependency-check:check -DautoUpdate=false`                                                                                             |
| Versão da engine                      | 12.1.1                                                                                                                                      |
| Relatórios                            | `security-reports/backend-dependencies/dependency-check-report.html` e `security-reports/backend-dependencies/dependency-check-report.json` |
| Resultado inicial                     | Falhava por vulnerabilidades com CVSS maior ou igual a 7,0.                                                                                 |
| Resultado final                       | Build com sucesso, zero vulnerabilidades reportadas.                                                                                        |
| Dependências analisadas no scan final | 103                                                                                                                                         |
| Artefatos vulneráveis no scan final   | 0                                                                                                                                           |
| Ocorrências no scan final             | 0                                                                                                                                           |
| Exceções de análise no scan final     | 0                                                                                                                                           |

Observação técnica: o analisador Sonatype OSS Index retornou 401 sem credenciais. Para manter o scan local reproduzível, o plugin foi configurado com `ossindexAnalyzerEnabled=false`. O scan continuou usando NVD, Known Exploited Vulnerabilities e RetireJS.

### 11.2 npm audit - frontend

| Campo             | Valor                                                                                                                                   |
|-------------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| Comando           | `npm audit --json`                                                                                                                      |
| Relatório         | `security-reports/frontend-dependencies/npm-audit-report.json`                                                                          |
| Resultado inicial | 3 vulnerabilidades altas em `vite`, `esbuild` e `@vitejs/plugin-vue`; nova ocorrência moderada em `js-yaml` identificada em 20/06/2026. |
| Resultado final   | 0 vulnerabilidades.                                                                                                                     |
| Ação tomada       | Atualização de Vite/plugin Vue, correção transitiva de `js-yaml` e regeneração do `package-lock.json`.                                  |

### 11.3 Docker Scout - imagens

| Campo              | Valor                                                                                                                                  |
|--------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| Ferramenta         | Docker Scout 1.20.4                                                                                                                    |
| Imagens analisadas | `soat-fiap-app:latest` e `soat-fiap-frontend:latest`                                                                                   |
| Relatórios         | `security-reports/docker/docker-scout-cves.txt` e `security-reports/docker/docker-scout-frontend-cves.txt`                             |
| Backend final      | 0 críticas, 0 altas e 1 média em `jackson-databind 2.21.4`; versão corrigida indicada pelo Scout ainda não publicada no Maven Central. |
| Frontend inicial   | 4 críticas, 26 altas e 45 médias.                                                                                                      |
| Frontend final     | 0 críticas, 0 altas e 1 média sem correção disponível.                                                                                 |
| Ação tomada        | Backend distroless; frontend Nginx unprivileged slim fixado por digest.                                                                |

### 11.4 Semgrep - análise estática

| Campo      | Valor                                                                                 |
|------------|---------------------------------------------------------------------------------------|
| Ferramenta | Semgrep 1.166.0                                                                       |
| Relatório  | `security-reports/static-analysis/semgrep.json`                                       |
| Escopo     | 200 arquivos, 187 regras, Java, JavaScript, JSON, Dockerfile e regras multilinguagem. |
| Resultado  | 0 achados e 0 erros.                                                                  |

### 11.5 Gitleaks - secrets

| Campo      | Valor                                            |
|------------|--------------------------------------------------|
| Ferramenta | Gitleaks                                         |
| Relatório  | `security-reports/secrets/gitleaks.json`         |
| Escopo     | 36 commits e aproximadamente 3,24 MB analisados. |
| Resultado  | 0 leaks encontrados.                             |

### 11.6 OWASP ZAP - API scan complementar

| Campo      | Valor                                                                                     |
|------------|-------------------------------------------------------------------------------------------|
| Ferramenta | OWASP ZAP via imagem `ghcr.io/zaproxy/zaproxy:stable`                                     |
| Comando    | `zap-api-scan.py -t http://host.docker.internal:8080/v3/api-docs -f openapi`              |
| Relatórios | `security-reports/dast/zap-api-report.html` e `security-reports/dast/zap-api-report.json` |
| Escopo     | API local importada a partir do OpenAPI, com 49 URLs importadas e 133 URLs avaliadas.     |
| Resultado  | 0 falhas, 117 regras PASS e 2 avisos.                                                     |

Avisos revisados:

| Alerta                                                   | Severidade no ZAP | Interpretação no MVP                                                                                   |
|----------------------------------------------------------|-------------------|--------------------------------------------------------------------------------------------------------|
| `Timestamp Disclosure - Unix`                            | WARN              | Detectado em respostas 401 de endpoints protegidos. Não expôs dado de negócio nem segredo.             |
| `Cross-Origin-Resource-Policy Header Missing or Invalid` | WARN              | Detectado no `/v3/api-docs`. Aceito no ambiente local acadêmico com Swagger habilitado para avaliação. |

## 12. Vulnerabilidades corrigidas

| ID                  | Origem                          | Correção aplicada                                                              | Arquivos afetados                                     | Evidência                                                                                    |
|---------------------|---------------------------------|--------------------------------------------------------------------------------|-------------------------------------------------------|----------------------------------------------------------------------------------------------|
| VULN-001 a VULN-009 | Dependências Maven              | Atualização de dependências e novo scan limpo.                                 | `pom.xml`                                             | `security-reports/backend-dependencies/dependency-check-report.json` com 0 vulnerabilidades. |
| VULN-010            | Dependências frontend           | Atualização de Vite/plugin Vue e novo audit limpo.                             | `frontend/package.json`, `frontend/package-lock.json` | `security-reports/frontend-dependencies/npm-audit-report.json` com 0 vulnerabilidades.       |
| VULN-011            | Dependência transitiva frontend | Atualização transitiva de `js-yaml` via `npm audit fix`.                       | `frontend/package-lock.json`                          | `security-reports/frontend-dependencies/npm-audit-report.json` com 0 vulnerabilidades.        |
| VULN-012            | Imagem backend                  | Substituição da imagem runtime Ubuntu/Temurin por distroless Java 21 non-root. | `Dockerfile`, `docker-compose.yml`                    | Scan final sem críticas/altas e com 1 risco médio aceito.                                    |
| VULN-013            | Imagem frontend                 | Substituição da imagem Nginx/Alpine antiga por variante unprivileged slim.     | `frontend/Dockerfile`                                 | Scan final sem críticas/altas e com 1 risco médio aceito.                                    |

## 13. Risco residual aceito

| ID       | Severidade | Justificativa                                                                                                         | Mitigação existente                                                                                        | Responsável pela aceitação | Revisão                                                                           |
|----------|------------|-----------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------|----------------------------|-----------------------------------------------------------------------------------|
| RISK-001 | Média      | BusyBox não possui versão corrigida indicada pelo Docker Scout na base analisada.                                     | Imagem slim, usuário não privilegiado, filesystem read-only e `no-new-privileges`.                         | Yasmin Barcelos Pires      | Próxima atualização da imagem base.                                               |
| RISK-002 | Média      | Docker Scout indicou `jackson-databind 2.21.5`, mas `mvn dependency:get` confirmou que essa versão não estava disponível no Maven Central em 29/06/2026. | Backend distroless/non-root; Dependency-Check Maven limpo; dependência usada de forma transitiva pelo JWT. | Yasmin Barcelos Pires      | Reavaliar quando `jackson-databind 2.21.5` ou versão corrigida estiver publicada. |

Não há vulnerabilidades críticas ou altas abertas nos scans finais. Os riscos residuais registrados são médios e foram aceitos temporariamente por ausência de correção aplicável no momento da análise.

## 14. Boas práticas de segurança implementadas

| Controle                                                  | Status                   | Evidência/observação                                                                                                |
|-----------------------------------------------------------|--------------------------|---------------------------------------------------------------------------------------------------------------------|
| JWT assinado com segredo vindo de variável de ambiente    | Implementado             | Configuração `security.jwt.secret`/`JWT_SECRET`; aplicação falha se o segredo não for informado.                    |
| Expiração de token configurável                           | Implementado             | `security.jwt.expiration-minutes`/`JWT_EXPIRATION_MINUTES`.                                                         |
| Senhas com BCrypt                                         | Implementado             | `PasswordEncoder` com `BCryptPasswordEncoder`.                                                                      |
| APIs administrativas protegidas por autenticação          | Implementado             | Configuração Spring Security/JWT.                                                                                   |
| DTOs explícitos para evitar mass assignment               | Implementado             | Requests/responses separados das entidades JPA.                                                                     |
| Jackson rejeitando campos desconhecidos                   | Implementado             | `fail-on-unknown-properties: true` e `ObjectMapper` de compatibilidade.                                             |
| Validação real de CPF/CNPJ                                | Implementado             | Value object de documento no domínio.                                                                               |
| Validação real de placa                                   | Implementado             | Value object de placa no domínio.                                                                                   |
| Tratamento global de exceções                             | Implementado             | Responses padronizados sem stack trace ao usuário.                                                                  |
| Controle de acesso por vínculo de cliente                 | Implementado             | Cliente autenticado só acessa OS e listagens do próprio `customerId`.                                               |
| Prevenção contra SQL Injection                            | Implementado             | Repositories JPA/JPQL estática e `JdbcTemplate` com placeholders.                                                   |
| Prevenção contra Command Injection                        | Redução de superfície    | Backend não executa comandos do sistema operacional no fluxo do MVP.                                                |
| Prevenção contra XSS no frontend demonstrativo            | Validado                 | Sem uso identificado de `v-html`, `innerHTML`, `eval` ou APIs equivalentes; Vue escapa interpolação por padrão.     |
| CORS sem wildcard                                         | Implementado             | Configuração rejeita origem `*` e `null`.                                                                           |
| Swagger configurável para avaliação local                 | Implementado/documentado | Contrato OpenAPI desabilitável por `SPRINGDOC_API_DOCS_ENABLED`; Swagger UI local servida para avaliação acadêmica. |
| `.env.example` sem secrets reais                          | Implementado/documentado | Variáveis sensíveis devem ser preenchidas localmente.                                                               |
| Dependências backend sem CVEs no scan final               | Corrigido                | `mvn dependency-check:check` final com sucesso.                                                                     |
| Dependências frontend sem vulnerabilidades no audit final | Corrigido                | `npm audit --json` final com 0 vulnerabilidades.                                                                    |
| Runtime backend distroless non-root                       | Implementado             | Imagem final sem shell ou gerenciador de pacotes, executada como `nonroot`.                                         |
| Container read-only e sem novos privilégios               | Implementado             | `read_only: true` e `no-new-privileges:true` no Compose, conforme aplicável.                                        |
| Imagem backend sem críticas ou altas                      | Validado                 | 1 CVE média transitiva aceita temporariamente.                                                                      |
| Imagem frontend sem críticas ou altas                     | Validado                 | 1 CVE média aceita temporariamente.                                                                                 |
| Histórico Git sem secrets detectados                      | Validado                 | Gitleaks analisou 36 commits e encontrou 0 leaks.                                                                   |
| Análise estática sem achados                              | Validado                 | Semgrep executou 187 regras em 200 arquivos com 0 achados.                                                          |
| API scan com OWASP ZAP                                    | Validado                 | 0 falhas e 2 avisos revisados.                                                                                      |

## 14.1 Análise baseada no OWASP Top 10

| Categoria OWASP                            | Risco no AutoCare Hub                                                       | Controle aplicado                                                                  | Evidência                                                                        |
|--------------------------------------------|-----------------------------------------------------------------------------|------------------------------------------------------------------------------------|----------------------------------------------------------------------------------|
| Broken Access Control                      | Cliente acessar dados de outro cliente ou executar ação administrativa      | JWT, papéis e autorização por `customerId` nos fluxos de OS/cliente                | `SecurityConfig`, `AuthorizationService`, `SecurityAuthorizationIntegrationTest` |
| Cryptographic Failures                     | Senha ou token exposto/armazenado de forma fraca                            | BCrypt para senha e JWT assinado com segredo externo ao repositório                | `SecurityConfig`, `JwtService`, `.env.example`                                   |
| Injection                                  | SQL Injection em filtros, IDs, documento, placa ou movimentações de estoque | Spring Data/JPA, JPQL estática e `JdbcTemplate` parametrizado                      | Repositories e busca estática sem SQL concatenado                                |
| Insecure Design                            | Segurança tratada apenas depois do desenvolvimento                          | Validações no domínio, papéis, avaliação de ameaças e gates de segurança           | Este relatório, testes de segurança e domínio                                    |
| Security Misconfiguration                  | CORS amplo, Swagger produtivo exposto ou container privilegiado             | CORS sem wildcard, Swagger configurável, Docker non-root/read-only                 | `SecurityConfig`, `application.yml`, Dockerfile e Compose                        |
| Vulnerable and Outdated Components         | Bibliotecas Maven/npm ou imagens com CVEs                                   | Dependency-Check, npm audit e Docker Scout                                         | Relatórios em `target/` e `security-reports/`                                    |
| Identification and Authentication Failures | Token inválido aceito ou segredo fraco                                      | Filtro JWT, segredo obrigatório com tamanho mínimo e expiração configurável        | `JwtAuthenticationFilter`, `JwtService`                                          |
| Software and Data Integrity Failures       | Dependência transitiva vulnerável ou lockfile desatualizado                 | Atualização de BOM/lockfile e scans reproduzíveis                                  | `pom.xml`, `package-lock.json`, relatórios de scan                               |
| Security Logging and Monitoring Failures   | Exposição de dados sensíveis em erro/log                                    | Handler global sem stack trace na resposta; Semgrep/Gitleaks sem achados           | `RestExceptionHandler`, Semgrep, Gitleaks                                        |
| Server-Side Request Forgery                | Backend chamar URLs externas controladas pelo usuário                       | Não há fluxo de negócio com requisição server-side para URL informada pelo usuário | Código backend revisado                                                          |

## 14.2 Análise de bibliotecas e licenças

A análise principal de bibliotecas foi feita por OWASP Dependency-Check no backend e `npm audit` no frontend. O projeto também foi revisado para remover dependências de teste que não eram usadas pela suíte real, reduzindo a superfície de dependências e a quantidade de artefatos analisados no scan final.

| Grupo                 | Ferramenta/evidência               | Resultado                                                                                             |
|-----------------------|------------------------------------|-------------------------------------------------------------------------------------------------------|
| Dependências Maven    | OWASP Dependency-Check 12.1.1      | 103 dependências analisadas, 0 vulnerabilidades.                                                      |
| Dependências npm      | npm audit 9.6.6                    | 172 dependências no relatório JSON, 0 vulnerabilidades.                                               |
| Bibliotecas removidas | Revisão manual do `pom.xml`        | Testcontainers e override de `commons-compress` de teste removidos por não serem usados.              |
| Licenças              | Revisão no escopo acadêmico do MVP | Não foi identificado uso intencional de biblioteca com licença incompatível para a entrega acadêmica. |

Não há plugin de relatório automático de licenças configurado no Maven. A análise formal de licenças ficou como verificação complementar, fora do critério principal do Tech Challenge.

## 15. Evidências

Os caminhos abaixo indicam as evidências geradas durante a validação. Nesta entrega, `security-reports/` fica versionado com evidências revisadas; `target/` continua sendo saída local gerada pelos comandos Maven.

| Evidência                       | Caminho                                                              | Descrição                                                              |
|---------------------------------|----------------------------------------------------------------------|------------------------------------------------------------------------|
| Relatório Dependency-Check HTML | `security-reports/backend-dependencies/dependency-check-report.html` | Relatório navegável com dependências, CVEs, severidades e referências. |
| Relatório Dependency-Check JSON | `security-reports/backend-dependencies/dependency-check-report.json` | Evidência estruturada usada para consolidar este relatório.            |
| Relatório npm audit JSON        | `security-reports/frontend-dependencies/npm-audit-report.json`       | Evidência estruturada do frontend com 0 vulnerabilidades.              |
| Relatório Docker Scout backend  | `security-reports/docker/docker-scout-cves.txt`                      | Evidência do scan final da imagem backend com 1 CVE média residual.    |
| Relatório Docker Scout frontend | `security-reports/docker/docker-scout-frontend-cves.txt`             | Evidência do scan final com 1 CVE média e nenhuma crítica/alta.        |
| Relatório OWASP ZAP HTML        | `security-reports/dast/zap-api-report.html`                          | Relatório DAST complementar com 0 falhas e 2 avisos.                   |
| Relatório OWASP ZAP JSON        | `security-reports/dast/zap-api-report.json`                          | Evidência estruturada do scan API baseado no OpenAPI local.            |
| Relatório Gitleaks              | `security-reports/secrets/gitleaks.json`                             | Evidência estruturada com 0 leaks.                                     |
| Relatório Semgrep               | `security-reports/static-analysis/semgrep.json`                      | Evidência estruturada com 0 achados e 0 erros.                         |
| Relatório JaCoCo HTML           | `target/site/jacoco/index.html`                                      | Relatório navegável de cobertura atual.                                |
| Relatório JaCoCo CSV            | `target/site/jacoco/jacoco.csv`                                      | Evidência estruturada usada para consolidar cobertura.                 |

Lista consolidada de caminhos:

```text
security-reports/backend-dependencies/dependency-check-report.html
security-reports/backend-dependencies/dependency-check-report.json
security-reports/frontend-dependencies/npm-audit-report.json
security-reports/docker/docker-scout-cves.txt
security-reports/docker/docker-scout-frontend-cves.txt
security-reports/dast/zap-api-report.html
security-reports/dast/zap-api-report.json
security-reports/secrets/gitleaks.json
security-reports/static-analysis/semgrep.json
target/site/jacoco/index.html
target/site/jacoco/jacoco.csv
```

## 16. Checklist de segurança

| Item                  | Status                       | Evidência                                                 | Observação                                                           |
|-----------------------|------------------------------|-----------------------------------------------------------|----------------------------------------------------------------------|
| JWT                   | OK                           | Configuração por variável de ambiente                     | Segredo real deve existir apenas fora do repositório.                |
| Senhas                | OK                           | BCrypt                                                    | Senha/hash não devem ser retornados em DTOs.                         |
| Secrets               | OK                           | Gitleaks com 0 leaks em 36 commits                        | Reexecutar antes de novas entregas.                                  |
| Logs                  | OK no escopo estático        | Semgrep sem achados                                       | Avaliação dinâmica pode ser feita em ciclo complementar.             |
| Validação de entrada  | OK                           | CPF/CNPJ e placa em value objects                         | Manter testes cobrindo documentos e placas inválidos.                |
| Tratamento de erros   | OK                           | Handler global esperado                                   | Stack trace não deve ser exposto ao usuário.                         |
| CORS                  | OK                           | Configuração sem wildcard                                 | Revisar origens permitidas por ambiente.                             |
| Swagger               | OK no MVP                    | Versão atualizada e flags para desabilitar                | Restringir em ambientes produtivos.                                  |
| Docker                | OK com riscos médios aceitos | Backend e frontend sem críticas/altas; 2 médias aceitas   | Reexecutar a cada atualização da imagem.                             |
| Banco de dados        | OK no escopo do MVP          | Credenciais por `.env`                                    | Scan específico do banco não foi critério desta análise.             |
| Dependências backend  | OK                           | Dependency-Check final                                    | Zero vulnerabilidades reportadas.                                    |
| Dependências frontend | OK                           | `npm audit` final                                         | Zero vulnerabilidades reportadas.                                    |
| Dados sensíveis       | OK no escopo revisado        | DTOs e validações de entrada                              | Responses e logs devem continuar sem dados sensíveis desnecessários. |
| Frontend              | OK no escopo analisado       | `npm audit` e Semgrep sem achados                         | Manter dependências atualizadas.                                     |
| Backend               | OK no escopo analisado       | Dependency-Check, Semgrep e ZAP sem falhas críticas/altas | ZAP trouxe apenas avisos revisados.                                  |

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
10. Reexecutar OWASP ZAP quando houver mudança relevante no contrato OpenAPI.
11. Reavaliar `RISK-001` e `RISK-002` quando novas versões das imagens ou dependências ficarem disponíveis.

## 18. Conclusão

O projeto AutoCare Hub corrigiu as vulnerabilidades identificadas nas dependências backend, dependências frontend e imagens Docker analisadas. O Dependency-Check e o npm audit ficaram sem vulnerabilidades no resultado final.

A imagem backend ficou sem vulnerabilidades críticas ou altas e mantém 1 CVE média em `jackson-databind`, aceita temporariamente porque a versão corrigida indicada pelo Docker Scout ainda não estava disponível no Maven Central na validação. A imagem frontend também ficou sem vulnerabilidades críticas ou altas e mantém 1 CVE média em BusyBox, aceita temporariamente porque o Docker Scout não indicou versão corrigida para a base analisada. Esses riscos foram registrados, mitigados com configuração de container mais restritiva e direcionados para revisão na próxima atualização de dependências ou imagens.

Considerando o escopo analisado por Dependency-Check, npm audit, Docker Scout, OWASP ZAP, Gitleaks, Semgrep e JaCoCo, o sistema está apto para a entrega do Tech Challenge.
