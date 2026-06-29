# Análise Estática de Código - AutoCare Hub

## 1. Objetivo

Este documento registra a validação de qualidade, manutenibilidade, cobertura e análise estática aplicada ao AutoCare Hub. Ele complementa o `docs/SECURITY_REPORT.md`: o relatório de segurança consolida vulnerabilidades, enquanto este documento mostra como o código foi analisado para reduzir bugs, inconsistências, dependências vulneráveis e problemas de manutenção.

## 2. Escopo analisado

A análise considerou:

- backend Spring Boot em `src/main/java`;
- testes automatizados em `src/test/java`;
- contrato OpenAPI em `docs/openapi/openapi.yaml`, usado para geração de interfaces;
- configuração Maven em `pom.xml`;
- frontend demonstrativo em `frontend/`;
- relatórios locais em `target/` e `security-reports/`.

Não foi encontrada configuração de SonarQube ou SonarCloud no projeto. Por isso, a análise estática final foi baseada nas ferramentas realmente configuradas ou com evidência local: Spotless, JaCoCo, OWASP Dependency-Check, ESLint, npm audit, Semgrep e Gitleaks.

## 3. Ferramentas utilizadas

| Ferramenta | Onde está no projeto | Finalidade |
| --- | --- | --- |
| Spotless 2.44.4 | `pom.xml` | Padronização Java com Palantir Java Format, remoção de imports não usados e whitespace consistente. |
| JaCoCo 0.8.12 | `pom.xml` | Cobertura de testes e gate mínimo automatizado. |
| OWASP Dependency-Check 12.1.1 | `pom.xml` | Análise de CVEs nas dependências Maven. |
| OpenAPI Generator 7.22.0 | `pom.xml` | Geração de contratos Java a partir do `openapi.yaml`, reduzindo divergência entre documentação e implementação. |
| ESLint | `frontend/package.json` | Análise estática do frontend demonstrativo. |
| npm audit 9.6.6 | npm local | Análise de vulnerabilidades das dependências do frontend. |
| Semgrep 1.166.0 | Evidência em `security-reports/static-analysis/semgrep.json` | Análise estática de segurança em arquivos Java, JavaScript, JSON e Dockerfile. |
| Gitleaks | Evidência em `security-reports/secrets/gitleaks.json` | Verificação de secrets no histórico Git. |

Checkstyle, PMD, SpotBugs, FindSecBugs, SonarQube e SonarCloud não estão configurados no projeto. Eles podem ser usados como complemento local, mas não fazem parte da evidência final desta validação.

## 4. Comandos executados

Validação backend:

```powershell
mvn spotless:check
mvn clean verify
mvn dependency-check:check -DautoUpdate=false
```

Validação frontend:

```powershell
cd frontend
npm run lint
npm run build
npm audit --json
```

Conferência Docker não destrutiva:

```powershell
docker compose config --quiet
```

O comando `docker compose down -v` não foi executado nesta revisão porque remove volumes locais. Para uma validação limpa antes da entrega, ele pode ser rodado manualmente em ambiente descartável.

## 5. Resultados por ferramenta

| Ferramenta | Resultado |
| --- | --- |
| Spotless | Build aprovado. 173 arquivos Java verificados; 0 exigiram alteração. |
| Maven/JUnit | `mvn clean verify` aprovado com 143 testes, 0 falhas, 0 erros e 0 ignorados. |
| JaCoCo | Gate aprovado. Cobertura: 96,31% instruções, 97,25% linhas e 90,12% branches. |
| OWASP Dependency-Check | Build aprovado. 103 dependências analisadas, 0 vulnerabilidades no relatório final. |
| ESLint frontend | Aprovado com `--max-warnings=0`. |
| Vite build | Build de produção aprovado. |
| npm audit | 172 dependências no relatório JSON, 0 vulnerabilidades. |
| Semgrep | Evidência local com 200 arquivos analisados, 0 achados e 0 erros. |
| Gitleaks | Evidência local com 0 leaks. |
| Docker Compose config | Arquivo Compose válido em validação não destrutiva. |

## 6. Correções aplicadas

Nesta revisão não foi necessário alterar código backend nem frontend. Os gates configurados passaram sem exigir correção de formatação, teste ou cobertura. Também foi removida do `pom.xml` a configuração de Testcontainers e `commons-compress` em escopo de teste, porque a suíte real usa H2 em memória e MockMvc, sem containers de teste.

A documentação foi ajustada para deixar a análise estática explícita e separada do relatório de vulnerabilidades:

- criação deste documento;
- criação de `docs/TESTING.md`;
- inclusão do documento no índice do `README.md`;
- ajuste do guia de scans para indicar onde consolidar qualidade e vulnerabilidades.

## 7. Qualidade e manutenibilidade

A revisão técnica confirmou que:

- controllers REST delegam para use cases e não acessam repositories diretamente;
- regras centrais de OS, orçamento e estoque ficam no domínio e nos casos de uso;
- value objects concentram validações de CPF/CNPJ, placa e dinheiro;
- o contrato OpenAPI é usado para gerar interfaces e reduzir divergência de payloads;
- Spotless remove imports não usados e mantém formatação consistente;
- os testes cobrem domínio, aplicação, segurança, integração REST, validações sensíveis, estoque e fluxo completo de OS.

O único alerta observado durante os testes foi emitido por Hibernate Validator em código gerado dentro de `target/generated-sources/openapi`, indicando uso de `@Valid` em listas geradas. Como o código é gerado a partir do contrato OpenAPI e a build passa, isso não foi tratado como falha de entrega.

## 8. Cobertura de testes

O projeto exige internamente cobertura mínima de 90% em instruções, linhas e branches pelo JaCoCo, acima dos 80% mínimos esperados para a entrega.

| Métrica | Resultado atual | Gate do projeto |
| --- | --- | --- |
| Instruções | 96,31% | 90% |
| Linhas | 97,25% | 90% |
| Branches | 90,12% | 90% |

Os testes incluem:

- entidades e value objects de domínio;
- regras de estoque em `Part`;
- transições e orçamento em `ServiceOrder`;
- criação, geração e aprovação de orçamento;
- acompanhamento de OS;
- autenticação e autorização JWT;
- validação de CPF/CNPJ e placa;
- fluxo administrativo de CRUD;
- fluxo de estoque;
- fluxo completo de Ordem de Serviço.

## 9. Evidências

Relatórios gerados localmente:

```text
target/site/jacoco/index.html
target/site/jacoco/jacoco.csv
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

Os arquivos em `security-reports/` ficam versionados como evidência revisada da entrega. `target/` permanece fora do
versionamento e é usado como saída local dos comandos Maven. O resumo de vulnerabilidades fica em
`docs/SECURITY_REPORT.md`.

## 10. Conclusão

O projeto possui evidência suficiente de análise estática para a entrega: formatação automatizada, cobertura com gate, testes unitários e de integração, análise de dependências backend, análise de dependências frontend, análise estática de segurança e verificação de secrets.

Não há finding relevante aberto nesta revisão. SonarQube/SonarCloud não foi executado e não deve ser citado como evidência final, apenas como ferramenta complementar possível para ciclos futuros.
