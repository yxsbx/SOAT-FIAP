# Relatório de Validação Final

**Projeto:** AutoCare Hub  
**Responsável:** Yasmin Barcelos Pires - RM370897  
**Data:** 20/06/2026  
**Branch validada:** `dev`

## Resultado geral

| Área | Resultado |
| --- | --- |
| Backend | Aprovado |
| Testes Maven | 108 testes, 0 falhas, 0 erros, 0 ignorados |
| Cobertura JaCoCo | Regras mínimas de 95% para instruções e linhas atendidas |
| Frontend build | Aprovado |
| Frontend lint | 0 erros; 990 avisos de formatação preexistentes |
| npm audit | 0 vulnerabilidades |
| OWASP Dependency-Check | 126 dependências, 0 vulneráveis, 0 exceções |
| Docker Compose | API e PostgreSQL ativos |
| Swagger/OpenAPI | `/v3/api-docs` 200 e `/swagger-ui.html` acessível por redirecionamento |
| Docker Scout | 0 vulnerabilidades na imagem final |
| Gitleaks | 0 leaks em 35 commits |
| Semgrep | 0 achados e 0 erros em 190 arquivos |
| Login administrativo | Aprovado |
| Fluxo principal da OS | Aprovado |
| Frontend | Carregamento e login administrativo aprovados, sem erros de console |

## Comandos validados

```powershell
mvn verify
cd frontend
npm audit --json
npm run build
docker compose up -d --build
```

## Fluxo funcional validado

Em ambiente Docker local, foram validados:

1. Login com usuário administrativo seed.
2. Criação de cliente com CPF válido.
3. Criação de veículo com placa válida.
4. Criação de serviço.
5. Criação de peça.
6. Reserva e liberação de estoque.
7. Registro de entrada de estoque.
8. Criação de Ordem de Serviço sem orçamento automático.
9. Geração de orçamento.
10. Aprovação do orçamento e confirmação das peças reservadas.
11. Transição explícita da OS para `IN_PROGRESS`.
12. Consulta de tracking com status `EM_EXECUCAO`.

## Segurança da imagem

A imagem inicial baseada em `eclipse-temurin:21-jre` apresentou CVEs em um utilitário não utilizado pela aplicação. O runtime foi migrado para:

```text
gcr.io/distroless/java21-debian12:nonroot
```

Resultado final:

- usuário `nonroot`;
- filesystem read-only;
- `no-new-privileges`;
- Docker Scout com 0 vulnerabilidades.

## Evidências

```text
target/site/jacoco/index.html
target/site/jacoco/jacoco.csv
target/dependency-check/dependency-check-report.html
target/dependency-check/dependency-check-report.json
security-reports/frontend-dependencies/npm-audit-report.json
security-reports/docker/docker-scout-cves.txt
security-reports/secrets/gitleaks.json
security-reports/static-analysis/semgrep.json
```

## Pendências externas

- Informar o username de Discord de Yasmin no documento final.
- Conceder acesso ao repositório privado para `soatarchitecture`.
- O PDF final foi gerado e revisado visualmente; apenas o link externo do repositório ficou clicável.
- Gravar o vídeo seguindo `docs/VIDEO_SCRIPT.md`.
