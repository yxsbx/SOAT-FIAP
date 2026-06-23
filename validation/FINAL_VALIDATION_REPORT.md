# Relatório de Validação Final

**Projeto:** AutoCare Hub  
**Responsável:** Yasmin Barcelos Pires - RM370897  
**Data:** 20/06/2026  
**Branch final de entrega:** `main`
**Commit técnico validado:** `dbed819`

## Resultado geral

| Área | Resultado |
| --- | --- |
| Backend | Aprovado |
| Testes Maven | 145 testes, 0 falhas, 0 erros, 0 ignorados |
| Cobertura JaCoCo global | 96,09% de instruções, 97,02% de linhas e 90,32% de branches |
| Gate de cobertura | Mínimo de 90% para instruções, linhas e branches atendido |
| Frontend build | Aprovado |
| Frontend lint | 0 erros e 0 warnings |
| Spotless Java | Aprovado |
| CI | Backend, frontend e build Docker configurados para a `main` |
| npm audit | 0 vulnerabilidades |
| OWASP Dependency-Check | 126 dependências, 0 vulneráveis, 0 exceções |
| Docker Compose | PostgreSQL, API e frontend ativos com um comando |
| Swagger/OpenAPI | `/v3/api-docs` 200 e Swagger acessível |
| Docker Scout backend | 0 vulnerabilidades |
| Docker Scout frontend | 0 críticas, 0 altas e 1 média sem correção disponível |
| Gitleaks | 0 leaks em 36 commits |
| Semgrep | 0 achados e 0 erros em 200 arquivos com 187 regras |
| Login administrativo | Aprovado por `localhost` e pelo IP local |
| Fluxo principal da OS | Aprovado |
| Repositório privado | Acesso Read concedido a `soatarchitecture` |

## Comandos validados

```powershell
mvn verify
cd frontend
npm run lint
npm run build
npm audit --audit-level=low
cd ..
docker compose up -d --build
```

URLs validadas:

```text
Frontend: http://localhost:5173
API: http://localhost:8080
Swagger: http://localhost:8080/swagger-ui.html
Frontend pela rede local: http://192.168.18.5:5173
```

## Fluxos validados

1. Login com usuário administrativo seed.
2. CRUD administrativo de cliente, veículo, serviço e peça pela API.
3. Cadastro e listagem de lead de demonstração.
4. Gestão de usuário e preferência de dashboard.
5. Reserva, liberação e entrada de estoque.
6. Criação de Ordem de Serviço.
7. Geração e aprovação de orçamento.
8. Transição explícita para `IN_PROGRESS`.
9. Consulta de tracking pelo cliente.
10. Carregamento do frontend sem erros de console ou overflow horizontal.

## Cobertura

O JaCoCo mede domínio, aplicação, REST, segurança, mappers e persistência. Excludes:

- bootstrap `AutoCareHubApiApplication`;
- classes geradas automaticamente pelo OpenAPI;
- records estruturais `$Command`, `$Query` e `$Output` sem lógica própria.

| Métrica | Coberto | Não coberto | Cobertura |
| --- | ---: | ---: | ---: |
| Instruções | 9.752 | 397 | 96,09% |
| Branches | 457 | 49 | 90,32% |
| Linhas | 2.440 | 75 | 97,02% |
| Métodos | 634 | 36 | 94,63% |

## Segurança das imagens

Backend:

- runtime `gcr.io/distroless/java21-debian12:nonroot`;
- 0 vulnerabilidades no Docker Scout;
- filesystem read-only e `no-new-privileges`.

Frontend:

- runtime Nginx unprivileged `mainline-alpine-slim`, fixado por digest;
- redução de 75 CVEs da imagem anterior para 1 CVE média;
- CVE restante em BusyBox sem versão corrigida disponível no scan de 20/06/2026;
- usuário UID 101, filesystem read-only e `no-new-privileges`.

## Evidências

```text
target/site/jacoco/index.html
target/site/jacoco/jacoco.csv
target/dependency-check/dependency-check-report.html
target/dependency-check/dependency-check-report.json
security-reports/frontend-dependencies/npm-audit-report.json
security-reports/docker/docker-scout-cves.txt
security-reports/docker/docker-scout-frontend-cves.txt
security-reports/secrets/gitleaks.json
security-reports/static-analysis/semgrep.json
```

## Pendências de entrega

- Gravar o vídeo seguindo `video/VIDEO_SCRIPT.md`.
