# Refinamento Técnico - AutoCare Hub

## 1. Objetivo do refinamento

Este documento mostra como o problema de negócio da oficina foi transformado em solução técnica no AutoCare Hub.

O foco do refinamento foi pegar a jornada principal do Tech Challenge, principalmente a Ordem de Serviço, e decidir como ela seria implementada em um backend monolítico com camadas claras, domínio protegido, API REST, banco relacional, autenticação JWT, Docker, testes e documentação.

## 2. Jornada técnica refinada

A jornada abaixo reflete o fluxo implementado no backend:

1. O atendente ou usuário administrativo informa CPF/CNPJ do cliente.
2. O backend valida o documento com o value object `Document`.
3. O sistema localiza o cliente pelo documento ou cadastra um novo cliente durante o fluxo de criação da OS.
4. O atendente informa ou seleciona o veículo.
5. O backend valida a placa com o value object `Plate`.
6. O backend garante que o veículo pertence ao cliente usado na OS.
7. O atendente cria a Ordem de Serviço.
8. O sistema registra serviços solicitados.
9. O sistema registra peças ou insumos necessários, quando houver.
10. O backend valida disponibilidade de estoque pelo agregado `Part`.
11. O sistema gera orçamento com serviços e peças.
12. O backend reserva peças quando o orçamento é gerado.
13. O cliente aprova o orçamento pelo fluxo disponível na API.
14. O backend aprova a OS, confirma a baixa das peças reservadas e libera execução.
15. A oficina inicia diagnóstico ou execução conforme a transição solicitada.
16. A oficina finaliza a OS.
17. A oficina registra a entrega do veículo.
18. O cliente consulta o acompanhamento da OS pela API.
19. O administrador consulta listagens, detalhes e tempo médio de execução.

O MVP não implementa WhatsApp, e-mail, SMS, pagamento online, agenda externa, fornecedores externos ou ERP. O fluxo principal fica dentro do backend, banco PostgreSQL e frontend demonstrativo.

## 3. Requisitos de negócio transformados em decisões técnicas

| Requisito de negócio | Decisão técnica | Justificativa | Evidência no projeto |
| --- | --- | --- | --- |
| Gerenciar clientes | CRUD REST e entidade `Customer` | Clientes são a base para veículos e OS. | `CustomersController`, `Customer`, `CustomerRepository`. |
| Identificar cliente por CPF/CNPJ | Value object `Document` | Evita validar documento em vários pontos diferentes. | `domain/valueobject/Document.java`. |
| Gerenciar veículos | CRUD REST e entidade `Vehicle` | Veículo precisa estar vinculado ao cliente da OS. | `VehiclesController`, `Vehicle`, `CreateServiceOrderUseCase`. |
| Validar placa | Value object `Plate` | Centraliza validação de placa antiga e Mercosul. | `domain/valueobject/Plate.java`. |
| Gerenciar serviços | CRUD REST e entidade `WorkshopService` | Serviços entram no orçamento da OS. | `WorkshopServicesController`, `WorkshopService`. |
| Gerenciar peças e insumos | CRUD REST e agregado `Part` | Peças impactam estoque, reserva, baixa e orçamento. | `PartsController`, `Part`, use cases de estoque. |
| Criar Ordem de Serviço | Use case + agregado `ServiceOrder` | A OS concentra status, itens e orçamento. | `CreateServiceOrderUseCase`, `ServiceOrder`. |
| Gerar orçamento | Regra em `ServiceOrder` e orquestração em use case | O total depende dos itens da OS e deve ficar no domínio. | `ServiceOrder.generateBudget`, `GenerateServiceOrderBudgetUseCase`. |
| Aprovar orçamento | Regra em `ServiceOrder` e baixa de estoque no use case | Aprovação muda status e confirma uso das peças. | `ApproveServiceOrderBudgetUseCase`. |
| Acompanhar OS | Endpoint de tracking | Cliente acompanha sem acessar APIs administrativas. | `TrackServiceOrderUseCase`, `/api/v1/service-orders/tracking`. |
| Monitorar tempo médio | Use case de métrica | Indicador usa OS finalizadas/entregues com início e fim de execução. | `GetAverageServiceOrderExecutionTimeUseCase`. |
| Proteger APIs administrativas | JWT e Spring Security | Operações internas exigem autenticação. | `SecurityConfig`, `JwtAuthenticationFilter`, `JwtService`. |

### Requisitos técnicos da solução

| Requisito técnico | Decisão adotada | Justificativa | Onde aparece |
| --- | --- | --- | --- |
| Backend monolítico | Uma aplicação Spring Boot | O MVP valida fluxo de oficina sem complexidade distribuída. | `AutoCareHubApiApplication`, `Dockerfile`. |
| Arquitetura em camadas | `interfaces`, `application`, `domain`, `infrastructure` | Separa entrada, orquestração, regra e tecnologia. | `src/main/java/br/com/autocarehub`. |
| API REST | Controllers versionados em `/api/v1` | Facilita consumo pelo frontend, Swagger e avaliação. | `interfaces/rest/controller`, `docs/openapi/openapi.yaml`. |
| Swagger/OpenAPI | Contrato versionado e Swagger UI | Documenta endpoints e gera interfaces. | `docs/openapi/openapi.yaml`, `/swagger-ui.html`. |
| PostgreSQL | Banco relacional principal | O domínio tem relações claras entre cliente, veículo, OS e estoque. | `docker-compose.yml`, `application.yml`. |
| Dockerfile | Imagem backend com runtime distroless nonroot | Reduz superfície de runtime e padroniza execução. | `Dockerfile`. |
| Docker Compose | Backend, banco e frontend local | Facilita demonstração e validação local. | `docker-compose.yml`. |
| JWT | Bearer Token para APIs protegidas | Atende segurança administrativa sem depender de provedor externo. | `SecurityConfig`, `JwtService`. |
| Validação sensível | `Document` e `Plate` | Regras ficam centralizadas no domínio. | `domain/valueobject`. |
| Testes automatizados | JUnit, Mockito, MockMvc e H2 | Valida domínio, use cases, REST e segurança. | `src/test/java`, `docs/TESTING.md`. |
| Cobertura mínima | JaCoCo no `mvn verify` | Garante cobertura acima da exigência de 80%. | `pom.xml`, `target/site/jacoco`. |
| Vulnerabilidades | Dependency-Check, npm audit, Semgrep, Gitleaks, Docker Scout e OWASP ZAP | Registra riscos encontrados, tratados e aceitos. | `docs/SECURITY_REPORT.md`. |

## 4. Dúvidas técnicas e decisões tomadas

| Dúvida técnica | Decisão | Motivo |
| --- | --- | --- |
| Como representar CPF/CNPJ? | Criar `Document` como value object. | Documento é regra sensível e aparece em cliente, tracking e criação de OS. |
| Como validar placa? | Criar `Plate` como value object. | Placa precisa ser validada de forma igual em veículo e acompanhamento. |
| Como controlar status da OS? | Métodos de transição em `ServiceOrder`. | Evita que controller ou use case alterem status livremente. |
| Quando gerar orçamento? | Na criação da OS, se solicitado, ou no endpoint de geração. | O MVP precisa permitir geração automática e também geração explícita. |
| Quando reservar estoque? | Na geração de orçamento. | A reserva evita prometer peça sem disponibilidade antes da aprovação. |
| Quando baixar estoque? | Na aprovação do orçamento. | A baixa representa o compromisso real de execução. |
| Como impedir estoque negativo? | Regras em `Part`. | O agregado protege disponibilidade, reserva e baixa. |
| Como separar API administrativa da consulta do cliente? | JWT para rotas administrativas e endpoint específico de tracking. | Cliente acompanha OS sem permissão administrativa. |
| Como calcular tempo médio? | Use case que calcula duração entre início e fim de execução. | A regra depende de várias OS e fica melhor como caso de uso de consulta. |
| Como documentar a API? | OpenAPI versionado em `docs/openapi/openapi.yaml`. | O contrato também gera interfaces REST no build. |
| Como rodar localmente com banco? | Docker Compose com backend, PostgreSQL e frontend. | Facilita avaliação local sem instalar banco manualmente. |
| Como proteger senha? | BCrypt via `PasswordEncoder`. | Senhas não ficam em texto puro. |

## 5. Spikes e validações técnicas

As validações foram práticas e compatíveis com o tamanho do MVP. Elas não representam POCs separadas em outro repositório; são verificações feitas no próprio projeto.

| Validação técnica | Objetivo | Resultado | Evidência |
| --- | --- | --- | --- |
| Execução local com Docker Compose | Confirmar que backend, PostgreSQL e frontend sobem juntos. | Configuração válida. | `docker-compose.yml`, `Dockerfile`, `frontend/Dockerfile`. |
| Conexão com PostgreSQL | Garantir persistência relacional para o MVP. | Backend usa JDBC/JPA com variáveis de ambiente. | `application.yml`, `docker-compose.yml`. |
| Migrations Flyway | Garantir schema reproduzível. | Migration baseline aplicada em testes e execução local. | `src/main/resources/db/migration`. |
| Swagger/OpenAPI | Confirmar documentação e teste manual da API. | Swagger exposto em `/swagger-ui.html`. | `docs/openapi/openapi.yaml`, `SecurityConfig`. |
| Autenticação JWT | Confirmar login e proteção das rotas administrativas. | Login retorna token e filtros validam Bearer Token. | `LoginUseCase`, `JwtAuthenticationFilter`. |
| Fluxo completo da OS | Validar criação, orçamento, aprovação, execução, finalização, entrega e tracking. | Fluxo coberto por teste de API. | `ServiceOrderFlowIntegrationTest`. |
| Cálculo de orçamento | Validar soma de serviços e peças. | Regra testada em domínio e use cases. | `ServiceOrderTest`, `GenerateServiceOrderBudgetUseCaseTest`. |
| Controle de estoque | Validar reserva, baixa e bloqueio de saldo inválido. | Regras concentradas em `Part`. | `PartTest`, `PartStockFlowIntegrationTest`. |
| Cobertura JaCoCo | Garantir cobertura acima do mínimo. | `mvn verify` passa no gate de 90%. | `target/site/jacoco`. |
| Scans de segurança | Consolidar vulnerabilidades e riscos aceitos. | Resultado documentado separadamente. | `docs/SECURITY_REPORT.md`. |

## 6. Arquitetura da solução

O AutoCare Hub usa um backend monolítico em camadas:

- `interfaces`: controllers REST, mappers REST, exceptions HTTP e adaptação do contrato OpenAPI.
- `application`: use cases, comandos, consultas e portas de repositório.
- `domain`: entidades, agregados, value objects, enums e exceções de domínio.
- `infrastructure`: JPA, adapters de persistência, segurança JWT, configuração e integração com bibliotecas.

Essa separação permite que o domínio da oficina fique protegido de detalhes como HTTP, JPA, Docker e Swagger.

A arquitetura C4 está documentada em `docs/ARCHITECTURE.md`.

## 7. Tecnologias e ferramentas utilizadas

| Tecnologia/ferramenta | Uso no AutoCare Hub |
| --- | --- |
| Java 21 | Linguagem do backend. |
| Spring Boot 4.1.0 | Base da aplicação monolítica. |
| Spring Web MVC | Exposição da API REST. |
| Spring Security | Proteção das rotas e integração do filtro JWT. |
| JJWT | Emissão e validação de tokens JWT. |
| BCrypt | Hash de senhas. |
| Spring Data JPA/Hibernate | Persistência relacional. |
| PostgreSQL 16 | Banco principal em execução local. |
| H2 | Banco em memória para testes automatizados. |
| Flyway | Versionamento do schema. |
| OpenAPI Generator | Geração de interfaces REST a partir do contrato. |
| Springdoc/Swagger UI | Documentação navegável da API. |
| Docker e Docker Compose | Execução local reproduzível. |
| JUnit 5, Mockito e MockMvc | Testes unitários, de aplicação e integração REST. |
| JaCoCo | Cobertura e gate de qualidade. |
| OWASP Dependency-Check | Scan de dependências backend. |
| Vue/Vite | Frontend demonstrativo. |

## 8. Integrações e dependências

Integrações reais do MVP:

- PostgreSQL, usado pelo backend via JPA/JDBC.
- Frontend demonstrativo Vue/Vite, que consome a API REST.
- Swagger UI, servido pela própria aplicação para teste manual da API.
- Docker Compose, usado para orquestrar backend, banco e frontend localmente.

O MVP não depende de integrações externas reais. Não há pagamento online, WhatsApp, e-mail, SMS, fornecedor externo, ERP, API Gateway, Kafka, mensageria ou cloud produtiva no código.

## 9. Estratégia de implementação

A implementação foi organizada para começar pelo fluxo central da oficina:

1. Modelar clientes, veículos, serviços, peças e OS no domínio.
2. Criar value objects para dados sensíveis e regras repetidas.
3. Implementar use cases para cada operação da API.
4. Criar adapters JPA para persistência.
5. Expor controllers REST a partir do contrato OpenAPI.
6. Proteger operações administrativas com JWT.
7. Cobrir regras críticas com testes unitários e fluxos REST com integração.
8. Empacotar e rodar localmente com Docker Compose.
9. Consolidar arquitetura, DDD, testes, segurança e entrega nos documentos.

Essa estratégia mantém o MVP simples, mas rastreável: cada requisito obrigatório tem um ponto técnico claro no código ou na documentação.

## 10. Riscos técnicos tratados

| Risco | Tratamento aplicado |
| --- | --- |
| Ambiente difícil de reproduzir | Docker Compose e `.env.example`. |
| API difícil de entender | Swagger/OpenAPI versionado. |
| Dados inválidos | `Document`, `Plate` e validações de request. |
| Acesso indevido | JWT nas APIs administrativas e regras no `SecurityConfig`. |
| Senhas expostas | BCrypt em criação e troca de senha. |
| Estoque inconsistente | Reserva, baixa e disponibilidade protegidas em `Part`. |
| Status inconsistente da OS | Transições controladas em `ServiceOrder`. |
| Orçamento incorreto | Cálculo centralizado em `ServiceOrder` e testes de domínio/use case. |
| Banco inconsistente | Migration Flyway versionada. |
| Regressão em fluxo crítico | Testes unitários e integração REST. |
| Dependências vulneráveis | Scans e relatório de vulnerabilidades. |

## 11. Itens fora do escopo do MVP

Os itens abaixo não são pendências da entrega; ficaram fora do escopo para manter o MVP focado no backend da oficina:

- microserviços;
- API Gateway;
- mensageria;
- Kafka ou filas;
- pagamento online;
- integração com WhatsApp, e-mail ou SMS;
- integração com fornecedores;
- integração com ERP;
- app mobile real;
- autenticação OAuth2 externa;
- deploy cloud produtivo;
- observabilidade avançada com tracing distribuído.

## 12. Conclusão

O refinamento técnico está aderente ao escopo da FIAP porque mostra como a jornada da oficina virou uma solução implementada em backend monolítico, com camadas claras, domínio protegido, persistência relacional, autenticação, contrato OpenAPI, execução local e validação automatizada.

Não foi identificada necessidade de refatoração grande no backend nesta revisão. A principal melhoria foi documentar melhor as decisões e a rastreabilidade entre negócio, código e arquitetura.
