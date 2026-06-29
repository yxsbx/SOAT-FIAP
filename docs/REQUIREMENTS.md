# Levantamento de Requisitos - AutoCare Hub

## 1. Contexto do problema

Uma oficina mecânica precisa controlar clientes, veículos, serviços solicitados, peças, estoque, orçamentos, aprovação e entrega. Quando esse processo fica em papel, planilhas ou mensagens soltas, a oficina perde histórico e fica difícil saber em que etapa cada atendimento está.

O AutoCare Hub organiza esse fluxo em um backend monolítico. A oficina cadastra clientes, veículos, serviços, peças e Ordens de Serviço, gera o orçamento, registra a aprovação, acompanha os status e consulta indicadores básicos da operação.

## 2. Personas e papéis

| Persona                  | Necessidade no domínio                                    | Representação no MVP                                        |
|--------------------------|-----------------------------------------------------------|-------------------------------------------------------------|
| Cliente da oficina       | Acompanhar a OS e aprovar orçamento.                      | `role=CUSTOMER`, vinculado a um `customerId`.               |
| Atendente                | Cadastrar cliente, veículo e abrir OS.                    | Usuário administrativo ou funcionário (`ADMIN`/`EMPLOYEE`). |
| Mecânico/equipe técnica  | Diagnosticar, executar e finalizar serviços.              | Usuário funcionário, representado nos fluxos de status.     |
| Administrador da oficina | Gerenciar cadastros, usuários, OS, estoque e indicadores. | `role=ADMIN`.                                               |
| Responsável pelo estoque | Controlar entrada, saída, reserva e baixa de peças.       | `ADMIN` ou `EMPLOYEE` com acesso aos endpoints de peças.    |

Os papéis de negócio aparecem na documentação para explicar a rotina da oficina. No código, a autorização usa `UserRole`, `profileType`, `employeeSubRole` e regras de acesso por cliente quando necessário.

## 3. Jornada da solução

1. O atendente identifica o cliente por CPF/CNPJ.
2. O sistema localiza o cliente ou permite seu cadastro.
3. O atendente cadastra ou seleciona o veículo.
4. O atendente cria a Ordem de Serviço.
5. O atendente adiciona os serviços solicitados.
6. O atendente adiciona peças ou insumos, quando necessário.
7. O sistema calcula o orçamento com base nos serviços e peças.
8. O cliente aprova o orçamento ou a oficina registra a aprovação recebida.
9. A oficina atualiza o status da OS até a finalização e entrega.
10. O cliente consulta o acompanhamento da OS pela API.
11. O administrador acompanha listagens e tempo médio de execução.

O MVP não implementa pagamento online, envio real de e-mail, SMS, WhatsApp, integração com fornecedores, agenda externa, ERP, mensageria ou deploy cloud produtivo. Esses itens ficaram fora do escopo desta fase e não são pendências da entrega.

## 4. Requisitos funcionais

| ID     | Requisito funcional                          | Descrição                                                                           | Critério de aceite                                                             | Implementação/Evidência                                                                    | Status   |
|--------|----------------------------------------------|-------------------------------------------------------------------------------------|--------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------|----------|
| RF-001 | Cadastrar cliente                            | Permitir o cadastro de clientes da oficina.                                         | `POST /api/v1/customers` cria cliente com dados válidos.                       | `CustomersController`, `CreateCustomerUseCase`, `AdministrativeCrudIntegrationTest`.       | Atendido |
| RF-002 | Identificar cliente por CPF/CNPJ             | Localizar cliente por documento no fluxo da OS.                                     | A criação de OS aceita `customerDocument` e vincula o cliente correto.         | `CreateServiceOrderUseCase`, `CustomerRepository`, `docs/openapi/openapi.yaml`.            | Atendido |
| RF-003 | Validar CPF/CNPJ                             | Validar e normalizar o documento do cliente.                                        | Documento inválido é recusado; CPF/CNPJ válido é aceito.                       | `Document`, `CustomerTest`, `SensitiveDataValidationIntegrationTest`.                      | Atendido |
| RF-004 | Cadastrar veículo                            | Permitir o cadastro de veículo com placa, marca, modelo e ano.                      | `POST /api/v1/vehicles` cria veículo vinculado ao cliente.                     | `VehiclesController`, `CreateVehicleUseCase`, `AdministrativeCrudIntegrationTest`.         | Atendido |
| RF-005 | Validar placa de veículo                     | Validar placa brasileira antiga e padrão Mercosul.                                  | Placa inválida é recusada no domínio/API.                                      | `Plate`, `VehicleTest`, `SensitiveDataValidationIntegrationTest`.                          | Atendido |
| RF-006 | Vincular veículo a cliente                   | Garantir que o veículo usado na OS pertence ao cliente correto.                     | A criação de OS bloqueia veículo de outro cliente.                             | `CreateServiceOrderUseCase`, `VehicleRepository`, testes de use case.                      | Atendido |
| RF-007 | Cadastrar serviços                           | Gerenciar o catálogo de serviços da oficina.                                        | `/api/v1/workshop-services` permite CRUD de serviços.                          | `WorkshopServicesController`, `WorkshopService`, `AdministrativeCrudIntegrationTest`.      | Atendido |
| RF-008 | Cadastrar peças e insumos                    | Gerenciar o catálogo de peças e insumos.                                            | `/api/v1/parts` permite CRUD de peças.                                         | `PartsController`, `Part`, `AdministrativeCrudIntegrationTest`.                            | Atendido |
| RF-009 | Controlar estoque                            | Registrar entrada, saída, reserva e baixa de peças.                                 | Movimentações alteram o estoque sem permitir saldo negativo.                   | `Part`, `RegisterPartStockMovementUseCase`, `PartStockFlowIntegrationTest`.                | Atendido |
| RF-010 | Criar Ordem de Serviço                       | Criar OS para cliente e veículo.                                                    | `POST /api/v1/service-orders` cria OS com status inicial.                      | `ServiceOrdersController`, `CreateServiceOrderUseCase`, `ServiceOrderFlowIntegrationTest`. | Atendido |
| RF-011 | Incluir serviços solicitados na OS           | Adicionar serviços na criação ou depois da OS.                                      | `POST /api/v1/service-orders/{serviceOrderId}/services` inclui item válido.    | `AddServiceToServiceOrderUseCase`, `ServiceOrder`.                                         | Atendido |
| RF-012 | Incluir peças/insumos na OS                  | Adicionar peças na criação ou depois da OS.                                         | `POST /api/v1/service-orders/{serviceOrderId}/parts` respeita o estoque disponível. | `AddPartToServiceOrderUseCase`, `Part`, `ServiceOrderFlowIntegrationTest`.                 | Atendido |
| RF-013 | Gerar orçamento automaticamente              | Calcular orçamento a partir dos serviços e peças da OS.                             | `/budget/generate` calcula total de serviços, peças e total geral.             | `GenerateServiceOrderBudgetUseCase`, `ServiceOrder.generateBudget`, testes de orçamento.   | Atendido |
| RF-014 | Disponibilizar orçamento para aprovação      | Colocar a OS em etapa de aprovação após gerar orçamento.                            | Geração de orçamento altera status para `AGUARDANDO_APROVACAO`.                | `ServiceOrderStatus`, `GenerateServiceOrderBudgetUseCase`.                                 | Atendido |
| RF-015 | Aprovar orçamento                            | Registrar aceite do orçamento pelo cliente ou por usuário autorizado.               | `/budget/approve` registra `approvedAt` e avança o fluxo.                      | `ApproveServiceOrderBudgetUseCase`, `SecurityAuthorizationIntegrationTest`.                | Atendido |
| RF-016 | Controlar status da OS                       | Controlar etapas recebida, diagnóstico, aprovação, execução, finalização e entrega. | `/status` aceita transições válidas e bloqueia inválidas.                      | `ServiceOrder`, `ServiceOrderStatus`, `UpdateServiceOrderStatusUseCaseTest`.               | Atendido |
| RF-017 | Permitir consulta da OS pelo cliente via API | Permitir que o cliente acompanhe a própria OS.                                      | `GET /api/v1/service-orders/tracking` retorna acompanhamento autorizado.       | `TrackServiceOrderUseCase`, `SecurityAuthorizationIntegrationTest`.                        | Atendido |
| RF-018 | Listar Ordens de Serviço                     | Permitir consulta administrativa das OS.                                            | `GET /api/v1/service-orders` retorna lista filtrável.                          | `ListServiceOrdersUseCase`, `docs/openapi/openapi.yaml`.                                   | Atendido |
| RF-019 | Detalhar Ordem de Serviço                    | Permitir consulta de uma OS específica.                                             | `GET /api/v1/service-orders/{serviceOrderId}` retorna detalhe da OS.           | `FindServiceOrderUseCase`, `ServiceOrdersController`.                                      | Atendido |
| RF-020 | Monitorar tempo médio de execução            | Calcular média de execução das OS concluídas.                                       | `GET /api/v1/service-orders/metrics/average-execution-time` retorna a métrica. | `GetAverageServiceOrderExecutionTimeUseCase`, `ServiceOrderFlowIntegrationTest`.           | Atendido |
| RF-021 | Autenticar usuários administrativos          | Login deve emitir token JWT para APIs protegidas.                                   | `POST /api/v1/auth/login` retorna Bearer token válido.                         | `AuthController`, `LoginUseCase`, `JwtServiceTest`.                                        | Atendido |

## 5. Requisitos não funcionais

| ID      | Requisito não funcional          | Categoria      | Descrição                                                     | Critério de aceite                                                          | Evidência                                                      | Status   |
|---------|----------------------------------|----------------|---------------------------------------------------------------|-----------------------------------------------------------------------------|----------------------------------------------------------------|----------|
| RNF-001 | Backend monolítico               | Arquitetura    | Backend entregue como uma única aplicação Spring Boot.        | Existe uma aplicação principal `AutoCareHubApiApplication`.                 | `pom.xml`, `Dockerfile`, `src/main/java`.                      | Atendido |
| RNF-002 | Arquitetura em camadas           | Arquitetura    | Separar interface, aplicação, domínio e infraestrutura.       | Pacotes `interfaces`, `application`, `domain` e `infrastructure` existem.   | Estrutura de pacotes e `docs/ARCHITECTURE.md`.                 | Atendido |
| RNF-003 | API RESTful                      | Arquitetura    | Expor recursos por endpoints REST versionados.                | Rotas `/api/v1/**` documentadas no OpenAPI.                                 | `docs/openapi/openapi.yaml`.                                   | Atendido |
| RNF-004 | Documentação Swagger/OpenAPI     | Documentação   | Disponibilizar contrato da API e UI para teste local.         | Swagger abre em `/swagger-ui.html` e contrato existe no repositório.        | `SwaggerUiController`, `SecurityAuthorizationIntegrationTest`. | Atendido |
| RNF-005 | Banco PostgreSQL                 | Persistência   | Usar PostgreSQL como banco principal do MVP.                  | Compose sobe `autocarehub-postgres` e backend usa JDBC PostgreSQL.          | `docker-compose.yml`, `application.yml`.                       | Atendido |
| RNF-006 | Justificativa do banco           | Persistência   | Explicar a escolha por banco relacional.                      | Arquitetura documenta relações entre cliente, veículo, OS, itens e estoque. | `docs/ARCHITECTURE.md`.                                        | Atendido |
| RNF-007 | Dockerfile para build            | Execução local | Permitir build da API em container.                           | `docker build` consegue montar a imagem da API.                             | `Dockerfile`.                                                  | Atendido |
| RNF-008 | Docker Compose completo          | Execução local | Subir banco, backend e frontend demonstrativo.                | `docker compose up -d --build` sobe os serviços definidos.                  | `docker-compose.yml`.                                          | Atendido |
| RNF-009 | Execução local simples           | Operação       | Projeto deve ter comandos claros para rodar localmente.       | README documenta `.env`, Docker Compose, Swagger e testes.                  | `README.md`, `.env.example`.                                   | Atendido |
| RNF-010 | JWT nas APIs administrativas     | Segurança      | APIs internas exigem Bearer Token e papel adequado.           | Endpoint administrativo sem token retorna 401/403.                          | `SecurityConfig`, `SecurityAuthorizationIntegrationTest`.      | Atendido |
| RNF-011 | Validação de dados sensíveis     | Segurança      | CPF/CNPJ e placa devem ser validados.                         | Entradas inválidas são recusadas.                                           | `Document`, `Plate`, `SensitiveDataValidationIntegrationTest`. | Atendido |
| RNF-012 | Testes unitários e de integração | Testabilidade  | Cobrir domínio, use cases, REST, segurança e fluxos críticos. | `mvn test` executa a suíte automatizada.                                    | `src/test/java`, `docs/TESTING.md`.                            | Atendido |
| RNF-013 | Cobertura mínima de 80%          | Qualidade      | Manter cobertura acima do mínimo exigido.                     | `mvn verify` passa no gate JaCoCo.                                          | `pom.xml`, `target/site/jacoco/index.html`.                    | Atendido |
| RNF-014 | Relatório de vulnerabilidades    | Segurança      | Consolidar scans executados e riscos reais.                   | Relatório lista ferramentas executadas, achados e riscos residuais aceitos. | `docs/SECURITY_REPORT.md`.                                     | Atendido |
| RNF-015 | `.env.example` sem secrets reais | Segurança      | Exemplo de ambiente não pode conter credenciais reais.        | Arquivo usa placeholders para senha e JWT.                                  | `.env.example`, `.gitignore`.                                  | Atendido |
| RNF-016 | Migrations com Flyway            | Persistência   | Schema deve ser versionado.                                   | Existe migration baseline e Flyway roda nos testes/execução.                | `src/main/resources/db/migration`, logs do `mvn verify`.       | Atendido |
| RNF-017 | Senhas com BCrypt                | Segurança      | Senhas de usuários devem usar hash seguro.                    | Backend configura `BCryptPasswordEncoder`.                                  | `SecurityConfig`.                                              | Atendido |
| RNF-018 | Tratamento padronizado de erros  | Operação       | API deve responder erros sem stack trace ao usuário.          | Exceptions são tratadas por handler global.                                 | `RestExceptionHandler`, OpenAPI.                               | Atendido |

## 6. Matriz de rastreabilidade

| Requisito | Código/Componente relacionado                          | Endpoint/Contrato                                           | Teste/Evidência                                                        | Documento relacionado               |
|-----------|--------------------------------------------------------|-------------------------------------------------------------|------------------------------------------------------------------------|-------------------------------------|
| RF-001    | `CustomersController`, `CreateCustomerUseCase`         | `POST /api/v1/customers`                                    | `AdministrativeCrudIntegrationTest`                                    | OpenAPI, README                     |
| RF-002    | `CreateServiceOrderUseCase`, `CustomerRepository`      | `POST /api/v1/service-orders`                               | `CreateServiceOrderUseCaseTest`                                        | DDD, Event Storming                 |
| RF-003    | `Document`                                             | Clientes e criação de OS                                    | `CustomerTest`, `SensitiveDataValidationIntegrationTest`               | DDD, Security Report                |
| RF-004    | `VehiclesController`, `CreateVehicleUseCase`           | `POST /api/v1/vehicles`                                     | `AdministrativeCrudIntegrationTest`                                    | OpenAPI                             |
| RF-005    | `Plate`                                                | Veículos e tracking                                         | `VehicleTest`, `SensitiveDataValidationIntegrationTest`                | DDD                                 |
| RF-006    | `CreateServiceOrderUseCase`, `VehicleRepository`       | `POST /api/v1/service-orders`                               | `CreateServiceOrderUseCaseTest`                                        | DDD                                 |
| RF-007    | `WorkshopServicesController`                           | `/api/v1/workshop-services`                                 | `AdministrativeCrudIntegrationTest`                                    | OpenAPI                             |
| RF-008    | `PartsController`                                      | `/api/v1/parts`                                             | `AdministrativeCrudIntegrationTest`                                    | OpenAPI                             |
| RF-009    | `Part`, use cases de estoque                           | `/stock-movement`, `/reserve`, `/commit-reservation`        | `PartTest`, `PartStockFlowIntegrationTest`                             | DDD, Architecture                   |
| RF-010    | `ServiceOrdersController`, `CreateServiceOrderUseCase` | `POST /api/v1/service-orders`                               | `ServiceOrderFlowIntegrationTest`                                      | Event Storming, Domain Storytelling |
| RF-011    | `AddServiceToServiceOrderUseCase`                      | `POST /api/v1/service-orders/{serviceOrderId}/services`     | `ServiceOrderFlowIntegrationTest`                                      | OpenAPI                             |
| RF-012    | `AddPartToServiceOrderUseCase`                         | `POST /api/v1/service-orders/{serviceOrderId}/parts`        | `ServiceOrderFlowIntegrationTest`                                      | OpenAPI                             |
| RF-013    | `GenerateServiceOrderBudgetUseCase`, `ServiceOrder`    | `POST /api/v1/service-orders/{serviceOrderId}/budget/generate` | `GenerateServiceOrderBudgetUseCaseTest`                             | DDD, Event Storming                 |
| RF-014    | `ServiceOrderStatus.AGUARDANDO_APROVACAO`              | Budget generate                                             | `ServiceOrderFlowIntegrationTest`                                      | DDD                                 |
| RF-015    | `ApproveServiceOrderBudgetUseCase`                     | `POST /api/v1/service-orders/{serviceOrderId}/budget/approve` | `ApproveServiceOrderBudgetUseCaseTest`                               | Domain Storytelling                 |
| RF-016    | `UpdateServiceOrderStatusUseCase`, `ServiceOrder`      | `PATCH /api/v1/service-orders/{serviceOrderId}/status`      | `UpdateServiceOrderStatusUseCaseTest`                                  | DDD                                 |
| RF-017    | `TrackServiceOrderUseCase`, `AuthorizationService`     | `GET /api/v1/service-orders/tracking`                       | `TrackServiceOrderUseCaseTest`, `SecurityAuthorizationIntegrationTest` | OpenAPI                             |
| RF-018    | `ListServiceOrdersUseCase`                             | `GET /api/v1/service-orders`                                | `ListServiceOrdersUseCaseTest`                                         | README                              |
| RF-019    | `FindServiceOrderUseCase`                              | `GET /api/v1/service-orders/{serviceOrderId}`               | `SecurityAuthorizationIntegrationTest`                                 | OpenAPI                             |
| RF-020    | `GetAverageServiceOrderExecutionTimeUseCase`           | `GET /api/v1/service-orders/metrics/average-execution-time` | `ServiceOrderFlowIntegrationTest`                                      | README                              |
| RF-021    | `AuthController`, `LoginUseCase`, `JwtService`         | `POST /api/v1/auth/login`                                   | `JwtServiceTest`, `SecurityAuthorizationIntegrationTest`               | Security Report                     |
| RNF-001   | `AutoCareHubApiApplication`                            | Aplicação Spring Boot                                       | `AutoCareHubApiApplicationTests`                                       | Architecture                        |
| RNF-002   | Pacotes em camadas                                     | Não se aplica                                               | Estrutura `src/main/java/br/com/autocarehub`                           | Architecture                        |
| RNF-003   | Controllers REST e OpenAPI                             | `/api/v1/**`                                                | MockMvc e OpenAPI                                                      | README                              |
| RNF-004   | Springdoc/Swagger                                      | `/swagger-ui.html`, `/openapi.yaml`                         | `SecurityAuthorizationIntegrationTest`                                 | README                              |
| RNF-005   | JPA/PostgreSQL                                         | `DB_URL`                                                    | Docker Compose e `application.yml`                                     | Architecture                        |
| RNF-006   | Modelo relacional                                      | Tabelas Flyway                                              | Migration baseline                                                     | Architecture                        |
| RNF-007   | Dockerfile                                             | Build da API                                                | Dockerfile revisado                                                    | README                              |
| RNF-008   | Docker Compose                                         | `docker-compose.yml`                                        | `docker compose config --quiet`                                        | README                              |
| RNF-009   | README e `.env.example`                                | Comandos locais                                             | Validação documental                                                   | README                              |
| RNF-010   | `SecurityConfig`, JWT                                  | APIs protegidas                                             | `SecurityAuthorizationIntegrationTest`                                 | Security Report                     |
| RNF-011   | `Document`, `Plate`                                    | Entradas sensíveis                                          | Testes de validação                                                    | DDD                                 |
| RNF-012   | Testes automatizados                                   | `src/test/java`                                             | `mvn test`                                                             | Testing                             |
| RNF-013   | JaCoCo                                                 | `mvn verify`                                                | `target/site/jacoco`                                                   | Testing, Static Analysis            |
| RNF-014   | Scans de segurança                                     | Não se aplica                                               | `docs/SECURITY_REPORT.md`                                              | Security Report                     |
| RNF-015   | `.env.example`, `.gitignore`                           | Variáveis locais                                            | Arquivos revisados                                                     | Security Report                     |
| RNF-016   | Flyway                                                 | `src/main/resources/db/migration`                           | Logs de migration nos testes                                           | Architecture                        |
| RNF-017   | `SecurityConfig`                                       | Login/usuários                                              | `BCryptPasswordEncoder`                                                | Security Report                     |
| RNF-018   | `RestExceptionHandler`                                 | Erros da API                                                | Responses padronizados                                                 | OpenAPI                             |

## 7. Conclusão

Os requisitos funcionais e não funcionais do MVP estão cobertos por código, contrato OpenAPI, testes ou documentação. A matriz de rastreabilidade mostra como cada requisito se conecta a endpoints, componentes, evidências e documentos do projeto.

Não foram incluídos requisitos de pagamento, WhatsApp, SMS, e-mail real, fornecedores, aplicativo mobile, mensageria ou cloud produtiva porque essas integrações não fazem parte do escopo obrigatório do AutoCare Hub nesta fase.
