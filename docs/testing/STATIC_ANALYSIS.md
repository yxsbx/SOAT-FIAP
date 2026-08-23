# Análise Estática de Código - AutoCare Hub

## 1. Objetivo

Este documento registra a validação de qualidade, manutenibilidade, cobertura e análise estática aplicada ao AutoCare
Hub.

Ele complementa o `docs/security/SECURITY_REPORT.md`: o relatório de segurança consolida vulnerabilidades e riscos residuais,
enquanto este documento mostra como o código foi analisado para reduzir bugs, inconsistências, problemas de manutenção,
falhas de formatação e riscos técnicos antes da entrega.

## 2. Escopo analisado

A análise considerou:

- backend Spring Boot em `backend/src/main/java`;
- testes automatizados em `backend/src/test/java`;
- contrato OpenAPI em `docs/api/openapi/openapi.yaml`;
- configuração Maven em `backend/pom.xml`;
- frontend demonstrativo em `frontend/`;
- configuração Docker e Docker Compose;
- relatórios locais em `backend/target/`;
- evidências revisadas em `security-reports/`.

Não foi encontrada configuração de SonarQube ou SonarCloud no projeto. Por isso, a análise estática final foi baseada
nas ferramentas configuradas ou executadas com evidência local: Spotless, JaCoCo, OWASP Dependency-Check, ESLint, npm
audit, Semgrep e Gitleaks.

Checkstyle, PMD, SpotBugs, FindSecBugs, SonarQube e SonarCloud podem ser usados em ciclos futuros, mas não fazem parte
da evidência final desta entrega.

## 3. Ferramentas utilizadas

| Ferramenta                    | Onde está no projeto                            | Finalidade                                                                                                      |
|-------------------------------|-------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| Spotless 2.44.4               | `backend/pom.xml`                                       | Padronização Java com Palantir Java Format, remoção de imports não usados e consistência de whitespace.         |
| JaCoCo 0.8.12                 | `backend/pom.xml`                                       | Cobertura de testes e gate mínimo automatizado.                                                                 |
| OWASP Dependency-Check 12.1.1 | `backend/pom.xml`                                       | Análise de CVEs nas dependências Maven.                                                                         |
| OpenAPI Generator 7.22.0      | `backend/pom.xml`                                       | Geração de contratos Java a partir do `openapi.yaml`, reduzindo divergência entre documentação e implementação. |
| ESLint                        | `frontend/package.json`                         | Análise estática do frontend demonstrativo.                                                                     |
| npm audit 9.6.6               | npm local                                       | Análise de vulnerabilidades nas dependências do frontend.                                                       |
| Semgrep 1.166.0               | `security-reports/static-analysis/semgrep.json` | Análise estática de segurança em arquivos Java, JavaScript, JSON e Dockerfile.                                  |
| Gitleaks                      | `security-reports/secrets/gitleaks.json`        | Verificação de secrets no histórico Git.                                                                        |

## 4. Comandos executados

### Backend

```powershell
cd backend
mvn spotless:check
mvn clean verify
mvn dependency-check:check -DautoUpdate=false
```

### Frontend

```powershell
cd frontend
npm run lint
npm run build
npm audit --json
cd ..
```

### Docker Compose

```powershell
docker compose --env-file deploy/docker/.env -f deploy/docker/docker-compose.yml config --quiet
```

A validação com `docker compose --env-file deploy/docker/.env -f deploy/docker/docker-compose.yml config --quiet` é
não destrutiva e verifica se o arquivo Compose é válido. O comando
`docker compose --env-file deploy/docker/.env -f deploy/docker/docker-compose.yml down -v` não é necessário para a análise estática, porque remove volumes locais. Ele deve ser usado
apenas na demonstração de execução limpa do projeto ou em ambiente descartável.

## 5. Resultados por ferramenta

| Ferramenta             | Resultado                                                                              |
|------------------------|----------------------------------------------------------------------------------------|
| Spotless               | `mvn spotless:check` aprovado, sem arquivos exigindo alteração.                        |
| Maven/JUnit            | `mvn test` aprovado com 167 testes, 0 falhas, 0 erros e 0 ignorados.                   |
| JaCoCo                 | Gate aprovado no `mvn clean verify`.                                                   |
| OWASP Dependency-Check | Evidência versionada em `security-reports/`; reexecução local atual não foi usada como gate desta revisão documental. |
| ESLint frontend        | Aprovado com `--max-warnings=0`.                                                       |
| Vite build             | Build de produção aprovado.                                                            |
| npm audit              | Relatório JSON final com 0 vulnerabilidades.                                           |
| Semgrep                | Evidência versionada em `security-reports/static-analysis/semgrep.json`.               |
| Gitleaks               | Evidência versionada em `security-reports/secrets/gitleaks.json`.                      |
| Docker Compose config  | Arquivo Compose válido na validação não destrutiva.                                    |

## 6. Correções e ajustes aplicados

Nesta revisão, a cobertura de branches foi reforçada com testes adicionais de aplicação e integração REST. O gate de
90% foi mantido no `backend/pom.xml`; a correção foi feita cobrindo cenários reais que já existiam no código.

Também foi removida do `backend/pom.xml` a configuração de Testcontainers e o override de `commons-compress` em escopo de teste,
porque a suíte real usa H2 em memória e MockMvc, sem containers de teste. Essa remoção reduziu dependências sem uso no
fluxo real de validação.

A documentação foi ajustada para separar melhor qualidade de código e vulnerabilidades:

- criação deste documento;
- criação de `docs/testing/TESTING.md`;
- inclusão deste documento no índice do `README.md`;
- ajuste do guia de scans para indicar onde consolidar qualidade, cobertura, análise estática e vulnerabilidades;
- alinhamento com `docs/security/SECURITY_REPORT.md`, que permanece como documento oficial de vulnerabilidades e riscos
  residuais.

## 7. Qualidade e manutenibilidade

A revisão técnica confirmou que:

- controllers REST delegam para use cases e não acessam repositories diretamente;
- regras centrais de OS, orçamento e estoque ficam no domínio e nos casos de uso;
- value objects concentram validações de CPF/CNPJ, placa e dinheiro;
- o contrato OpenAPI é usado para gerar interfaces e reduzir divergência de payloads;
- Spotless remove imports não usados e mantém formatação consistente;
- os testes cobrem domínio, aplicação, segurança, integração REST, validações sensíveis, estoque e fluxo completo de OS.

O único alerta observado durante os testes foi emitido pelo Hibernate Validator em código gerado dentro de
`backend/target/generated-sources/openapi`, indicando uso de `@Valid` em listas geradas. Como o código é gerado a partir do
contrato OpenAPI e a build passa, esse alerta foi registrado como observação técnica, não como falha da entrega.

## 8. Cobertura de testes

O projeto exige internamente cobertura mínima de 90% em instruções, linhas e branches pelo JaCoCo, acima dos 80% mínimos
esperados para a entrega. Na última validação documentada, `mvn clean verify` passou e o gate JaCoCo foi aprovado.

Os testes incluem:

- entidades e value objects de domínio;
- regras de estoque em `Part`;
- transições de status e orçamento em `ServiceOrder`;
- criação, geração e aprovação de orçamento;
- acompanhamento de OS;
- autenticação e autorização JWT;
- validação de CPF/CNPJ e placa;
- fluxo administrativo de CRUD;
- gestão de usuários, empresas e escopo administrativo;
- fluxo de estoque;
- fluxo completo de Ordem de Serviço.

## 9. Evidências

Relatórios gerados ou usados como evidência da entrega:

```text
backend/target/site/jacoco/index.html
backend/target/site/jacoco/jacoco.csv
security-reports/backend-dependencies/dependency-check-report.html
security-reports/backend-dependencies/dependency-check-report.json
security-reports/backend-dependencies/dependency-check-report.sarif
security-reports/frontend-dependencies/npm-audit-report.json
security-reports/static-analysis/semgrep.json
security-reports/secrets/gitleaks.json
security-reports/docker/docker-scout-cves.txt
security-reports/docker/docker-scout-frontend-cves.txt
security-reports/dast/zap-api-report.html
security-reports/dast/zap-api-report.json
```

Os arquivos em `security-reports/` ficam versionados como evidência revisada da entrega. A pasta `backend/target/` permanece
fora do versionamento e é usada como saída local dos comandos Maven.

O resumo de vulnerabilidades, CVEs corrigidas e riscos residuais aceitos fica em:

```text
docs/security/SECURITY_REPORT.md
```

## 10. Pontos fora da evidência final

As ferramentas abaixo não foram usadas como evidência final da entrega:

| Ferramenta           | Motivo                                                                                    |
|----------------------|-------------------------------------------------------------------------------------------|
| SonarQube/SonarCloud | Não há configuração no projeto e não houve execução evidenciada.                          |
| Checkstyle           | Não está configurado no projeto.                                                          |
| PMD                  | Não está configurado no projeto.                                                          |
| SpotBugs/FindSecBugs | Não está configurado no projeto.                                                          |
| Trivy                | Não foi usado como evidência final porque a entrega consolidou Docker Scout para imagens. |
| TruffleHog           | Não foi usado como evidência final porque a entrega consolidou Gitleaks para secrets.     |

Essas ferramentas podem ser adicionadas em ciclos futuros, mas não devem ser citadas como resultado executado nesta
entrega.

## 11. Pendências identificadas

Não há pendência obrigatória aberta para a entrega dentro do escopo desta análise estática.

Os pontos abaixo ficam apenas como melhoria futura:

- configurar SonarQube ou SonarCloud em pipeline CI/CD;
- adicionar Checkstyle, PMD ou SpotBugs caso o projeto evolua e precise de regras mais rígidas de qualidade;
- automatizar Semgrep, Gitleaks, Docker Scout e npm audit em pipeline;
- revisar o alerta do Hibernate Validator se o OpenAPI Generator passar a gerar código diferente em versões futuras.

Esses itens não bloqueiam o Tech Challenge, porque os gates configurados e as evidências locais já cobrem formatação,
testes, cobertura, dependências, análise estática de segurança e secrets.

## 12. Conclusão

O projeto possui evidência suficiente de análise estática e qualidade para a entrega: formatação automatizada, cobertura
com gate, testes unitários e de integração, análise de dependências backend, análise de dependências frontend, análise
estática de segurança e verificação de secrets.

Não há achado relevante aberto nesta revisão. SonarQube/SonarCloud não foi executado e não deve ser citado como
evidência final, apenas como ferramenta complementar possível para ciclos futuros.
