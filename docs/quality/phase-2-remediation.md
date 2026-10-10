# Correções da avaliação da Fase 2

## Critérios e evidências

| Ponto avaliado | Comportamento implementado | Evidência |
| --- | --- | --- |
| Aprovação e recusa idempotentes | Repetições da decisão do orçamento vigente preservam a decisão e não repetem consumo/liberação de estoque. A recusa é persistida em `rejected_at`. | `BudgetConsistencyIntegrationTest`, `ApproveServiceOrderBudgetUseCaseTest`, `ServiceOrderFlowIntegrationTest` |
| Concorrência e falhas parciais | Transação no limite do caso de uso, bloqueio de escrita da OS e das peças e ordem consistente de bloqueio. Falha no estoque reverte toda a decisão. | Testes simultâneos de aprovação/recusa, de duas OS com a mesma peça e de rollback na segunda peça. |
| Decisões incompatíveis | Um orçamento aprovado não pode ser recusado. Após recusa, é necessário gerar um novo orçamento antes de aprovar. Nova geração abre um novo ciclo de decisão. | Testes de domínio e integração. |
| Exclusão lógica | `FINISHED` e `DELIVERED` saem da fila operacional, inclusive quando informados explicitamente no filtro. Os registros permanecem no banco e nas consultas de histórico/detalhe. | Teste de integração de fila, paginação e preservação do histórico. |
| Prioridade e antiguidade | Execução, aprovação, diagnóstico e recebida; dentro da prioridade, criação mais antiga primeiro e ID como desempate para paginação estável. | Consulta JPA e testes de fila. |
| Clean Code e erros | `BudgetStock` centraliza reserva, consumo e liberação; conflitos de concorrência retornam HTTP 409 com orientação de repetição, sem detalhes internos do banco. | `RestExceptionHandlerTest` e testes dos fluxos críticos. |
| HPA | CPU e memória com targets e requests validados automaticamente; Metrics Server provisionado no EKS por Terraform e no kind pela pipeline. | `scripts/validate-hpa.cjs`, `terraform/metrics-server.tf` no infra-k8s e pipeline da Fase 2. |

## Migração

A API executa `V2__persist_budget_rejection.sql` pelo Flyway no próximo início. A migração é aditiva e mantém a V1 intacta. A cópia em `infra-db/docs/migration` é referência; não execute a mesma migração manualmente.

Recusas anteriores à migração não podem ser inferidas com segurança apenas pelo status de diagnóstico. O registro persistido de idempotência passa a existir para as novas recusas. A API atual identifica o orçamento vigente pela OS; não existe identificador de evento externo para deduplicar notificações antigas que cheguem depois da emissão de um novo orçamento.

## Validação antes da entrega

Na raiz da API independente:

```sh
mvn spotless:check verify
```

No monorepositório:

```sh
mvn -f backend/pom.xml spotless:check verify
```

O `verify` executa testes unitários e de integração, migrações Flyway em H2 no modo PostgreSQL e exige pelo menos 90% de instruções, linhas e branches via JaCoCo. Relatórios: `target/surefire-reports`, `target/failsafe-reports` e `target/site/jacoco/index.html` (prefixo `backend/` no monorepositório).

Os testes locais não substituem a validação do HPA em um cluster em execução. Depois do deploy:

```sh
kubectl wait apiservice/v1beta1.metrics.k8s.io --for=condition=Available --timeout=180s
kubectl wait hpa/autocarehub-api hpa/autocarehub-web -n autocarehub --for=condition=ScalingActive --timeout=300s
kubectl top pods -n autocarehub
kubectl describe hpa -n autocarehub
```

Confirme `ScalingActive=True`, métricas atuais sem `<unknown>` e a variação de réplicas sob carga. A pipeline de kind passa a exigir métricas disponíveis; o teste de carga no ambiente de entrega continua sendo uma verificação operacional.

Referência do componente de métricas: [Kubernetes Metrics Server](https://kubernetes-sigs.github.io/metrics-server/).

## Resultado da validação local — 07/09/2026

| Repositório | Testes | Falhas/erros | Instruções | Linhas | Branches |
| --- | ---: | ---: | ---: | ---: | ---: |
| SOAT-FIAP / backend | 187 | 0 | 97,07% | 97,76% | 90,88% |
| SOAT-FIAP-autocare-api | 190 | 0 | 97,04% | 97,71% | 91,01% |

Build e gate JaCoCo aprovados com Maven verify. A API independente inclui testes
adicionais dos claims de autenticação externa da Fase 3 (cliente, validade, papel,
identificador ausente/inválido e preservação de autenticação existente).
Os quatro HPAs passaram no validador de contrato, os workflows alterados foram
validados como YAML e o infra-k8s passou em terraform fmt -check e terraform validate.
Nenhum deploy ou teste de carga foi executado no cluster nesta validação local.
