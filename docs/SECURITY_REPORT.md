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
| Dockerfile | Analisado e corrigido | Docker Scout executado sobre a imagem final. |
| docker-compose.yml | Revisado em execução | Container validado como non-root, read-only e com `no-new-privileges`. |
| Secrets | Analisado | Gitleaks executado sobre 35 commits, sem leaks encontrados. |
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
| Branch/estado analisado | `dev`, working tree validada em 20/06/2026 |

## 5. Ferramentas utilizadas

| Ferramenta | Versao | Finalidade | Comando executado | Saida/relatorio |
| --- | --- | --- | --- | --- |
| OWASP Dependency-Check Maven Plugin | 12.1.1 | Dependencias backend | `mvn dependency-check:check -DautoUpdate=false` | `target/dependency-check/dependency-check-report.html` e `target/dependency-check/dependency-check-report.json` |
| JaCoCo | 0.8.12 | Cobertura de testes | `mvn test` | `target/site/jacoco/index.html` e `target/site/jacoco/jacoco.csv` |
| npm audit | 9.6.6 | Dependencias frontend | `npm audit --json` | `security-reports/frontend-dependencies/npm-audit-report.json` |
| Docker Scout | 1.20.4 | Imagem Docker | `docker scout cves soat-fiap-app:latest --only-severity critical,high,medium` | `security-reports/docker/docker-scout-cves.txt` |
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

O primeiro Docker Scout executado em 20/06/2026 encontrou 1 vulnerabilidade critica, 2 altas e 9 medias em `/usr/bin/pebble`, presente na imagem base Ubuntu do runtime. A imagem final foi migrada para `gcr.io/distroless/java21-debian12:nonroot`, removendo o pacote desnecessario e reduzindo a superficie de ataque.

As dependencias foram atualizadas e o OWASP Dependency-Check foi executado novamente. No scan final, o resultado foi:

| Indicador | Resultado |
| --- | ---: |
| Dependencias analisadas | 126 |
| Artefatos vulneraveis | 0 |
| Ocorrencias de vulnerabilidades | 0 |
| Achados unicos | 0 |
| Excecoes de analise | 0 |
| Status do build do Dependency-Check | Sucesso |

Com base nos scans finais de dependencias backend, frontend, imagem Docker, secrets e analise estatica, nao ha vulnerabilidades ou leaks abertos reportados pelas ferramentas executadas.

## 8. Resultado geral dos scans

| Categoria | Critical | High | Medium | Low | Unknown | Status geral |
| --- | ---: | ---: | ---: | ---: | ---: | --- |
| Dependencias backend | 0 | 0 | 0 | 0 | 0 | Corrigido |
| Dependencias frontend | 0 | 0 | 0 | 0 | 0 | Corrigido |
| Imagem Docker | 0 | 0 | 0 | 0 | 0 | Corrigido |
| Analise estatica | 0 | 0 | 0 | 0 | 0 | Semgrep sem achados |
| Secrets | 0 | 0 | 0 | 0 | 0 | Gitleaks sem leaks |

## 9. Cobertura de testes

O relatorio JaCoCo atual ignora classes geradas pelo OpenAPI e componentes fora do escopo de negocio na
configuração do Maven. A regra academica de 95% e validada por `mvn verify` sobre o bundle medido de
`domain` e `application`, com minimo de 95% para instrucoes e linhas.

Tambem sao excluidas da metrica a camada REST, a infraestrutura e records auxiliares de
comando/consulta/saida. Assim, a cobertura apresentada representa o nucleo de negocio medido, e nao a
cobertura global de todas as classes do repositorio.

| Metrica | Coberto | Nao coberto | Cobertura |
| --- | ---: | ---: | ---: |
| Instrucoes | 5.082 | 247 | 95,36% |
| Branches | 284 | 78 | 78,45% |
| Linhas | 1.250 | 34 | 97,35% |
| Metodos | 348 | 21 | 94,31% |
| Classes | 81 | 0 | 100,00% |

Resultado validado com 108 testes automatizados e `mvn verify` concluindo com sucesso.

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
| VULN-012 | Docker Scout | Critica/Alta/Media | `/usr/bin/pebble` na imagem `eclipse-temurin:21-jre` | 12 CVEs em pacotes Go embutidos no utilitario não utilizado pelo runtime Java. | Aumento desnecessario da superficie de ataque da imagem. | Migração do runtime para `gcr.io/distroless/java21-debian12:nonroot`. | Corrigido | Docker Scout final com 0 vulnerabilidades em `security-reports/docker/docker-scout-cves.txt`. |

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
| Imagem analisada | `soat-fiap-app:latest` |
| Relatorio | `security-reports/docker/docker-scout-cves.txt` |
| Resultado inicial | 1 critica, 2 altas e 9 medias em `/usr/bin/pebble` da imagem base. |
| Resultado final | 0 criticas, 0 altas, 0 medias e 0 baixas. |
| Ação tomada | Migração para runtime distroless Java 21 non-root. |

### 11.4 Analise estatica de codigo

| Campo | Valor |
| --- | --- |
| Ferramenta | Semgrep 1.166.0 |
| Relatorio | `security-reports/static-analysis/semgrep.json` |
| Escopo | 190 arquivos, 178 regras, Java, JavaScript, JSON, Dockerfile e regras multilinguagem. |
| Resumo | 0 achados e 0 erros. |

### 11.5 Scan de secrets

| Campo | Valor |
| --- | --- |
| Ferramenta | Gitleaks |
| Relatorio | `security-reports/secrets/gitleaks.json` |
| Escopo | 35 commits e aproximadamente 3,16 MB analisados. |
| Resumo | 0 leaks encontrados. |

## 12. Vulnerabilidades corrigidas

| ID | Severidade | Correção aplicada | Arquivo/commit | Evidencia |
| --- | --- | --- | --- | --- |
| VULN-001 a VULN-009 | Critica/Alta/Media/Baixa | Atualização de dependencias Maven e novo scan limpo. | `pom.xml` | `target/dependency-check/dependency-check-report.json` com 0 vulnerabilidades. |
| VULN-010 | Alta | Atualização de dependencias frontend e novo audit limpo. | `frontend/package.json`, `frontend/package-lock.json` | `security-reports/frontend-dependencies/npm-audit-report.json` com 0 vulnerabilidades. |
| VULN-011 | Media | Atualização transitiva de `js-yaml` via `npm audit fix`. | `frontend/package-lock.json` | Audit de 20/06/2026 com 0 vulnerabilidades. |
| VULN-012 | Critica/Alta/Media | Substituição da imagem runtime Ubuntu/Temurin por distroless Java 21 non-root. | `Dockerfile`, `docker-compose.yml` | Docker Scout final com 0 vulnerabilidades. |

## 13. Vulnerabilidades aceitas como risco

Nenhuma vulnerabilidade foi aceita formalmente como risco nesta analise. O scan final de dependencias backend nao manteve CVEs abertos.

| ID | Severidade | Justificativa | Mitigação existente | Responsavel pela aceitação | Revisar em |
| --- | --- | --- | --- | --- | --- |
| N/A | N/A | Nenhum risco aceito formalmente. | N/A | N/A | N/A |

## 14. Vulnerabilidades pendentes

Nao ha vulnerabilidades pendentes no escopo do OWASP Dependency-Check backend, `npm audit` frontend ou Docker Scout apos os scans finais.

| ID | Severidade | Motivo da pendencia | Plano de correção | Prioridade | Prazo |
| --- | --- | --- | --- | --- | --- |
| N/A | N/A | Nao ha pendencias de dependencias backend/frontend nos scans finais. | N/A | N/A | N/A |

Nao ha pendencias nos scans de dependencias, imagem Docker, secrets e analise estatica executados. Um DAST dedicado permanece como melhoria futura, fora do escopo atual.

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
| Imagem Docker sem vulnerabilidades no Scout final | Corrigido | Relatorio final com 0 vulnerabilidades. |
| Historico Git sem secrets detectados | Validado | Gitleaks analisou 35 commits e encontrou 0 leaks. |
| Analise estatica sem achados | Validado | Semgrep executou 178 regras em 190 arquivos com 0 achados. |

## 16. Evidencias

| Evidencia | Caminho | Descrição |
| --- | --- | --- |
| Relatorio Dependency-Check HTML | `target/dependency-check/dependency-check-report.html` | Relatorio navegavel com dependencias, CVEs, severidades e referencias. |
| Relatorio Dependency-Check JSON | `target/dependency-check/dependency-check-report.json` | Evidencia estruturada usada para consolidar este relatorio. |
| Relatorio npm audit JSON | `security-reports/frontend-dependencies/npm-audit-report.json` | Evidencia estruturada do frontend com 0 vulnerabilidades. |
| Relatorio Docker Scout | `security-reports/docker/docker-scout-cves.txt` | Evidencia do scan final da imagem com 0 vulnerabilidades. |
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
security-reports/secrets/gitleaks.json
security-reports/static-analysis/semgrep.json
target/site/jacoco/index.html
target/site/jacoco/jacoco.csv
```

## 18. Conclusao

O projeto AutoCare Hub corrigiu as vulnerabilidades de dependencias backend, frontend e imagem Docker identificadas nos scans. O scan final do OWASP Dependency-Check nao reportou vulnerabilidades abertas nas dependencias Maven analisadas. O `npm audit` e o Docker Scout finais tambem retornaram 0 vulnerabilidades.

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
9. Manter a cobertura automatizada acima da meta academica de 95% em cada alteração relevante.

## Checklist de seguranca

| Item | Status | Evidencia | Observação |
| --- | --- | --- | --- |
| JWT | OK | Configuração por variavel de ambiente | Validar segredo real apenas fora do repositorio. |
| Senhas | OK | BCrypt | Nao retornar senha/hash em DTOs. |
| Secrets | OK | Gitleaks com 0 leaks em 35 commits | Reexecutar antes de cada entrega. |
| Logs | OK no escopo estatico | Semgrep sem achados | Complementar futuramente com DAST. |
| Validação de entrada | OK | CPF/CNPJ e placa em value objects | Manter testes cobrindo documentos e placas invalidos. |
| Tratamento de erros | OK | Handler global esperado | Confirmar sem stacktrace em ambiente produtivo. |
| CORS | OK | Configuração sem wildcard | Revisar origens permitidas por ambiente. |
| Swagger | OK no MVP | Versao atualizada e flags para desabilitar | Restringir em produção. |
| Docker | OK | Docker Scout final com 0 vulnerabilidades; runtime distroless non-root | Reexecutar a cada atualização da imagem. |
| Banco de dados | Parcial | Credenciais por `.env` | Sem scan especifico de banco. |
| Dependencias backend | OK | Dependency-Check final | Zero vulnerabilidades reportadas. |
| Dependencias frontend | OK | `npm audit` final | Zero vulnerabilidades reportadas. |
| Dados sensiveis | Parcial | Mascaramento e DTOs | Validar responses de detalhe e logs. |
| Frontend | OK no escopo analisado | `npm audit` e Semgrep sem achados | Manter dependencias atualizadas. |
| Backend | OK no escopo analisado | Dependency-Check e Semgrep sem achados | Complementar futuramente com DAST. |
