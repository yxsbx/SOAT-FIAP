# Validação pelo Roteiro da Faculdade

**Projeto:** AutoCare Hub  
**Responsável:** Yasmin Barcelos Pires - RM370897  
**Data:** 20/06/2026

## Matriz de validação

| Risco de perda de nota | Estado atual | Evidência | Ação restante |
| --- | --- | --- | --- |
| DDD superficial | Atendido | `docs/DDD_DOCUMENTATION.md` descreve subdomínios, bounded contexts, entidades, value objects, agregado, repositórios, serviços e linguagem ubíqua. | Mostrar apenas decisões centrais no vídeo. |
| Event Storming fraco | Atendido | `docs/EVENT_STORMING.md` contém atores, comandos, eventos, políticas, exceções e diagramas Mermaid. | Mostrar o fluxo da OS e a máquina de estados. |
| README sem execução clara | Atendido | `README.md` documenta `.env`, comando Docker único, URLs, testes, Swagger, usuários e scans. | Nenhuma ação técnica. |
| Docker não sobe de primeira | Atendido | `docker compose up -d --build` validado com PostgreSQL, API e frontend. | Demonstrar o comando e `docker compose ps`. |
| Swagger incompleto | Atendido | OpenAPI versionado e `/v3/api-docs` validado com HTTP 200. | Demonstrar login, Authorize e endpoints principais. |
| Testes sem fluxo principal | Atendido | Testes de integração cobrem OS, orçamento, estoque, segurança, CRUD administrativo e persistência. | Nenhuma ação técnica. |
| Cobertura abaixo de 80% | Atendido globalmente | 93,93% de instruções, 94,83% de linhas e 76,09% de branches; gates de 90/90/70; 109 testes. | Explicar apenas os excludes técnicos legítimos. |
| JWT parcial | Atendido | Login JWT, filtro Bearer, expiração, BCrypt e autorização por papel/propriedade. | Mostrar login e acesso autorizado no vídeo. |
| Cliente não consulta OS | Atendido | Endpoint por cliente e tracking protegido, com teste de autorização. | Demonstrar com `cliente@autocarehub.com`. |
| Orçamento não automático | Atendido | A criação aceita `generateBudget`; também existe geração manual controlada por status. | Demonstrar um dos fluxos e explicar o outro. |
| Status não acompanha ações | Atendido | Geração muda para `WAITING_APPROVAL`; aprovação registra aceite; execução e finalização têm transições explícitas. | Não afirmar que aprovação inicia execução automaticamente. |
| Relatório genérico | Atendido | Dependency-Check, npm audit, Docker Scout, Gitleaks e Semgrep têm evidências reais. | Mencionar a CVE média aceita no frontend. |
| PDF sem links/evidências | Atendido após regeneração | Documento de entrega contém links GitHub apontando para `main` e rotas locais. | Abrir os links do PDF final antes do envio. |
| Vídeo só de tela | Preparado | `docs/VIDEO_SCRIPT.md` reserva tempo para arquitetura, DDD, Event Storming, segurança, testes e vulnerabilidades. | Ensaiar e gravar em até 15 minutos. |

## Riscos residuais

### Prioridade máxima

1. Gravar o vídeo seguindo o roteiro.
2. Executar o checklist rápido imediatamente antes da gravação.
3. Manter as evidências do commit final junto da entrega.
4. Confirmar que o link enviado à faculdade aponta para a branch `main`.

### Prioridade média

1. Explicar que a cobertura é global e que apenas código gerado/estrutural está excluído.
2. Mostrar aprovação e início da execução como ações separadas.
3. Demonstrar a consulta da OS pelo cliente.
4. Informar com transparência a CVE média da imagem frontend sem correção disponível.

## Evidências principais

```text
docs/DDD_DOCUMENTATION.md
docs/EVENT_STORMING.md
docs/SECURITY_REPORT.md
docs/FINAL_VALIDATION_REPORT.md
docs/VIDEO_SCRIPT.md
output/pdf/AutoCare_Hub_Tech_Challenge_Entrega_Final.pdf
target/site/jacoco/index.html
target/dependency-check/dependency-check-report.html
security-reports/frontend-dependencies/npm-audit-report.json
security-reports/docker/docker-scout-cves.txt
security-reports/docker/docker-scout-frontend-cves.txt
security-reports/secrets/gitleaks.json
security-reports/static-analysis/semgrep.json
```
