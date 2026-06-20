# Validação pelo Roteiro da Faculdade

**Projeto:** AutoCare Hub  
**Responsável:** Yasmin Barcelos Pires - RM370897  
**Data:** 20/06/2026

## Matriz de validação

| Risco de perda de nota | Estado atual | Evidência | Ação restante |
| --- | --- | --- | --- |
| DDD superficial | Atendido | `docs/DDD_DOCUMENTATION.md` documenta linguagem ubíqua, subdomínios, bounded contexts, entidades, value objects, agregados, repositórios, serviços, políticas, eventos, comandos e diagramas. | Apresentar apenas os pontos centrais no vídeo. |
| Event Storming fraco | Atendido | `docs/EVENT_STORMING.md` contém 3 fluxos, atores, comandos, eventos, agregados, políticas, exceções, decisões e 4 diagramas Mermaid. | Mostrar ao menos um fluxo e o diagrama de estados no vídeo. |
| README sem execução clara | Atendido | `README.md` documenta `.env`, Docker Compose, Maven, frontend, testes, Swagger e scans. | Nenhuma ação técnica. |
| Docker não sobe de primeira | Atendido para o escopo principal | `docker compose up -d --build` validado com API e PostgreSQL. Runtime distroless, non-root e banco healthy. | O frontend demonstrativo ainda sobe separadamente com `npm run dev`; deixar isso claro na gravação. |
| Swagger incompleto | Atendido | OpenAPI versionado com 34 paths e 51 operações; `/v3/api-docs` retornou 200. | Demonstrar login, Authorize e endpoints principais. |
| Testes sem fluxo principal | Atendido | `ServiceOrderFlowIntegrationTest` cobre cliente, veículo, serviço, peça, OS, orçamento, aprovação, status e métrica. | Nenhuma ação técnica. |
| Cobertura abaixo de 80% | Atendido no núcleo medido | 95,36% de instruções e 97,35% de linhas; 108 testes; `mvn verify` aprovado. | Explicar que REST e infraestrutura estão excluídos. Cobertura global não deve ser apresentada como 95%. |
| JWT parcial | Atendido | Login JWT, filtro Bearer, expiração, BCrypt, rotas por papel e autorização por propriedade de cliente/OS. | Mostrar login administrativo e acesso de cliente no vídeo. |
| Cliente não consulta OS | Atendido | Teste autoriza cliente apenas nas próprias OS; endpoint `GET /api/v1/customers/{customerId}/service-orders` e tracking protegido. | Demonstrar com usuário `cliente@autocarehub.com`. |
| Orçamento não automático | Atendido | `generateBudget` tem default `true`; criação da OS pode gerar orçamento automaticamente. | Demonstrar uma criação com orçamento automático ou explicar a opção manual. |
| Status não acompanha ações | Atendido | Geração muda para `WAITING_APPROVAL`; aprovação registra aceite; início muda para `IN_PROGRESS`; finalização e entrega têm transições controladas. | Não afirmar que aprovação inicia execução automaticamente. |
| Relatório genérico | Atendido | Dependency-Check, npm audit, Docker Scout, Gitleaks e Semgrep possuem resultados e arquivos de evidência reais. | Manter relatórios junto da entrega. |
| PDF sem links/evidências | Parcial | PDF A4 com 9 páginas, link clicável do repositório e caminhos das evidências. | Os caminhos internos não são hyperlinks; o repositório precisa estar acessível ao avaliador. |
| Vídeo só de tela | Preparado, ainda não comprovado | `docs/VIDEO_SCRIPT.md` reserva tempo para arquitetura, DDD, Event Storming, segurança, testes e vulnerabilidades. | Ensaiar e gravar seguindo o roteiro, não apenas navegar pela interface. |

## Riscos residuais antes da entrega

### Prioridade máxima

1. Conceder acesso de leitura ao repositório privado para `soatarchitecture`.
2. Informar o username de Discord no documento final.
3. Ensaiar e gravar o vídeo em até 15 minutos.
4. Fazer commit e push de todas as correções e evidências finais.

### Prioridade média

1. No vídeo, explicar que a cobertura de 95% é do núcleo de negócio medido.
2. Mostrar que aprovação e início da execução são ações separadas.
3. Demonstrar o cliente consultando somente as próprias Ordens de Serviço.
4. Deixar claro que Docker Compose sobe backend e banco; o frontend demonstrativo é iniciado separadamente.

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
security-reports/secrets/gitleaks.json
security-reports/static-analysis/semgrep.json
```
