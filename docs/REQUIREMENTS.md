# Levantamento de Requisitos - AutoCare Hub

## 1. Contexto do problema

Uma oficina mecanica precisa controlar clientes, veiculos, serviços solicitados, pecas, estoque, orçamentos, aprovação e entrega. Quando esse processo fica em papel, planilhas ou mensagens soltas, a oficina perde historico e fica dificil saber em que etapa cada atendimento esta.

O AutoCare Hub organiza esse fluxo em um backend monolitico: a oficina cadastra clientes, veiculos, serviços, pecas e Ordens de Serviço, gera o orçamento, registra a aprovação, acompanha os status e consulta indicadores.

## 2. Personas e papeis

| Persona | Necessidade no dominio | Representação no MVP |
|---|---|---|
| Cliente da oficina | Acompanhar a OS e aprovar orçamento. | `role=CUSTOMER`, vinculado a um `customerId`. |
| Atendente | Cadastrar cliente, veiculo e abrir OS. | Usuário administrativo ou funcionario (`ADMIN`/`EMPLOYEE`). |
| Mecanico/equipe tecnica | Diagnosticar, executar e finalizar serviços. | Usuário funcionario, representado nos fluxos de status. |
| Administrador da oficina | Gerenciar cadastros, usuários, OS, estoque e indicadores. | `role=ADMIN`. |
| Responsavel pelo estoque | Controlar entrada, saida, reserva e baixa de pecas. | `ADMIN` ou `EMPLOYEE` com acesso aos endpoints de pecas. |

Os papeis de negocio aparecem na documentação para explicar a rotina da oficina. No codigo, a autorização usa `UserRole`, `profileType`, `employeeSubRole` e regras de acesso por cliente quando necessario.

## 3. Jornada da solução

1. O atendente identifica o cliente por CPF/CNPJ.
2. O sistema localiza ou permite cadastrar o cliente.
3. O atendente cadastra ou seleciona o veiculo.
4. O atendente cria a Ordem de Serviço.
5. O atendente adiciona serviços solicitados.
6. O atendente adiciona pecas ou insumos, quando necessario.
7. O sistema calcula o orçamento com base nos serviços e pecas.
8. O cliente aprova o orçamento.
9. A oficina atualiza o status da OS ate finalização e entrega.
10. O cliente consulta o acompanhamento da OS pela API.
11. O administrador acompanha listagens e tempo medio de execução.

O MVP nao implementa pagamento online, envio real de e-mail, SMS, WhatsApp, integração com fornecedores, agenda externa, ERP, mensageria ou deploy cloud produtivo.

## 4. Requisitos funcionais

| ID | Requisito funcional | Descrição | Criterio de aceite | Implementação/Evidencia | Status |
|---|---|---|---|---|---|
| RF-001 | Cadastrar cliente | Permitir cadastro de cliente da oficina. | `POST /api/v1/customers` cria cliente com dados validos. | `CustomersController`, `CreateCustomerUseCase`, `AdministrativeCrudIntegrationTest`. | Atendido |
| RF-002 | Identificar cliente por CPF/CNPJ | Localizar cliente por documento no fluxo da OS. | Criação de OS aceita `customerDocument` e vincula o cliente correto. | `CreateServiceOrderUseCase`, `CustomerRepository`, `docs/openapi/openapi.yaml`. | Atendido |
| RF-003 | Validar CPF/CNPJ | Validar e normalizar documento do cliente. | Documento invalido e recusado; CPF/CNPJ valido e aceito. | `Document`, `CustomerTest`, `SensitiveDataValidationIntegrationTest`. | Atendido |
| RF-004 | Cadastrar veiculo | Permitir cadastro de veiculo com placa, marca, modelo e ano. | `POST /api/v1/vehicles` cria veiculo vinculado ao cliente. | `VehiclesController`, `CreateVehicleUseCase`, `AdministrativeCrudIntegrationTest`. | Atendido |
| RF-005 | Validar placa de veiculo | Validar placa antiga brasileira e Mercosul. | Placa invalida e recusada no dominio/API. | `Plate`, `VehicleTest`, `SensitiveDataValidationIntegrationTest`. | Atendido |
| RF-006 | Vincular veiculo a cliente | Garantir que o veiculo pertence ao cliente da OS. | Criação de OS bloqueia veiculo de outro cliente. | `CreateServiceOrderUseCase`, `VehicleRepository`, testes de use case. | Atendido |
| RF-007 | Cadastrar serviços | Gerenciar catalogo de serviços da oficina. | `/api/v1/workshop-services` permite CRUD de serviços. | `WorkshopServicesController`, `WorkshopService`, `AdministrativeCrudIntegrationTest`. | Atendido |
| RF-008 | Cadastrar pecas e insumos | Gerenciar catalogo de pecas/insumos. | `/api/v1/parts` permite CRUD de pecas. | `PartsController`, `Part`, `AdministrativeCrudIntegrationTest`. | Atendido |
| RF-009 | Controlar estoque | Registrar entrada, saida, reserva e baixa de pecas. | Movimentações alteram estoque sem permitir saldo negativo. | `Part`, `RegisterPartStockMovementUseCase`, `PartStockFlowIntegrationTest`. | Atendido |
| RF-010 | Criar Ordem de Serviço | Criar OS para cliente e veiculo. | `POST /api/v1/service-orders` cria OS com status inicial. | `ServiceOrdersController`, `CreateServiceOrderUseCase`, `ServiceOrderFlowIntegrationTest`. | Atendido |
| RF-011 | Incluir serviços solicitados na OS | Adicionar serviços na criação ou depois da OS. | `POST /api/v1/service-orders/{id}/services` inclui item valido. | `AddServiceToServiceOrderUseCase`, `ServiceOrder`. | Atendido |
| RF-012 | Incluir pecas/insumos na OS | Adicionar pecas na criação ou depois da OS. | `POST /api/v1/service-orders/{id}/parts` respeita estoque disponivel. | `AddPartToServiceOrderUseCase`, `Part`, `ServiceOrderFlowIntegrationTest`. | Atendido |
| RF-013 | Gerar orçamento automáticamente | Calcular orçamento a partir dos serviços e pecas da OS. | `/budget/generate` calcula total de serviços, pecas e total geral. | `GenerateServiceOrderBudgetUseCase`, `ServiceOrder.generateBudget`, testes de orçamento. | Atendido |
| RF-014 | Disponibilizar orçamento para aprovação | Colocar OS em etapa de aprovação apos gerar orçamento. | Geração de orçamento altera status para `AGUARDANDO_APROVACAO`. | `ServiceOrderStatus`, `GenerateServiceOrderBudgetUseCase`. | Atendido |
| RF-015 | Aprovar orçamento | Registrar aceite do orçamento pelo cliente ou usuário autorizado. | `/budget/approve` registra `approvedAt` e avanca o fluxo. | `ApproveServiceOrderBudgetUseCase`, `SecurityAuthorizationIntegrationTest`. | Atendido |
| RF-016 | Controlar status da OS | Controlar etapas recebida, diagnostico, aprovação, execução, finalização e entrega. | `/status` aceita transições validas e bloqueia invalidas. | `ServiceOrder`, `ServiceOrderStatus`, `UpdateServiceOrderStatusUseCaseTest`. | Atendido |
| RF-017 | Permitir consulta da OS pelo cliente via API | Cliente deve acompanhar a propria OS. | `GET /api/v1/service-orders/tracking` retorna acompanhamento autorizado. | `TrackServiceOrderUseCase`, `SecurityAuthorizationIntegrationTest`. | Atendido |
| RF-018 | Listar Ordens de Serviço | Permitir consulta administrativa das OS. | `GET /api/v1/service-orders` retorna lista filtravel. | `ListServiceOrdersUseCase`, `docs/openapi/openapi.yaml`. | Atendido |
| RF-019 | Detalhar Ordem de Serviço | Permitir consulta de uma OS especifica. | `GET /api/v1/service-orders/{id}` retorna detalhe da OS. | `FindServiceOrderUseCase`, `ServiceOrdersController`. | Atendido |
| RF-020 | Monitorar tempo medio de execução | Calcular media de execução das OS concluidas. | `GET /api/v1/service-orders/metrics/average-execution-time` retorna metrica. | `GetAverageServiceOrderExecutionTimeUseCase`, `ServiceOrderFlowIntegrationTest`. | Atendido |
| RF-021 | Autenticar usuários administrativos | Login deve emitir token JWT para APIs protegidas. | `POST /api/v1/auth/login` retorna Bearer token valido. | `AuthController`, `LoginUseCase`, `JwtServiceTest`. | Atendido |

## 5. Requisitos nao funcionais

| ID | Requisito nao funcional | Categoria | Descrição | Criterio de aceite | Evidencia | Status |
|---|---|---|---|---|---|---|
| RNF-001 | Backend monolitico | Arquitetura | Backend entregue como uma unica aplicação Spring Boot. | Existe uma aplicação principal `AutoCareHubApiApplication`. | `pom.xml`, `Dockerfile`, `src/main/java`. | Atendido |
| RNF-002 | Arquitetura em camadas | Arquitetura | Separar interface, aplicação, dominio e infraestrutura. | Pacotes `interfaces`, `application`, `domain` e `infrastructure` existem. | Estrutura de pacotes e `docs/ARCHITECTURE.md`. | Atendido |
| RNF-003 | API RESTful | Arquitetura | Expor recursos por endpoints REST versionados. | Rotas `/api/v1/**` documentadas no OpenAPI. | `docs/openapi/openapi.yaml`. | Atendido |
| RNF-004 | Documentação Swagger/OpenAPI | Documentação | Disponibilizar contrato e UI de teste local. | Swagger abre em `/swagger-ui.html` e contrato existe no repositorio. | `SwaggerUiController`, `SecurityAuthorizationIntegrationTest`. | Atendido |
| RNF-005 | Banco PostgreSQL | Persistencia | Usar PostgreSQL como banco principal do MVP. | Compose sobe `autocarehub-postgres` e backend usa JDBC PostgreSQL. | `docker-compose.yml`, `application.yml`. | Atendido |
| RNF-006 | Justificativa do banco | Persistencia | Explicar a escolha por banco relacional. | Arquitetura documenta relações entre cliente, veiculo, OS, itens e estoque. | `docs/ARCHITECTURE.md`. | Atendido |
| RNF-007 | Dockerfile para build | Execução local | Permitir build da API em container. | `docker build` consegue montar imagem da API. | `Dockerfile`. | Atendido |
| RNF-008 | Docker Compose completo | Execução local | Subir banco, backend e frontend demonstrativo. | `docker compose up -d --build` sobe os serviços definidos. | `docker-compose.yml`. | Atendido |
| RNF-009 | Execução local simples | Operação | Projeto deve ter comandos claros para rodar localmente. | README documenta `.env`, Docker Compose, Swagger e testes. | `README.md`, `.env.example`. | Atendido |
| RNF-010 | JWT nas APIs administrativas | Seguranca | APIs internas exigem Bearer Token e papel adequado. | Endpoint administrativo sem token retorna 401/403. | `SecurityConfig`, `SecurityAuthorizationIntegrationTest`. | Atendido |
| RNF-011 | Validação de dados sensiveis | Seguranca | CPF/CNPJ e placa devem ser validados. | Entradas invalidas sao recusadas. | `Document`, `Plate`, `SensitiveDataValidationIntegrationTest`. | Atendido |
| RNF-012 | Testes unitarios e integração | Testabilidade | Cobrir dominio, use cases, REST, seguranca e fluxos criticos. | `mvn test` executa a suite automatizada. | `src/test/java`, `docs/TESTING.md`. | Atendido |
| RNF-013 | Cobertura minima de 80% | Qualidade | Manter cobertura acima do minimo exigido. | `mvn verify` passa no gate JaCoCo. | `pom.xml`, `target/site/jacoco/index.html`. | Atendido |
| RNF-014 | Relatorio de vulnerabilidades | Seguranca | Consolidar scans e riscos reais. | Relatorio lista ferramentas executadas, achados e risco residual. | `docs/SECURITY_REPORT.md`. | Atendido |
| RNF-015 | `.env.example` sem secrets reais | Seguranca | Exemplo de ambiente nao pode conter credenciais reais. | Arquivo usa placeholders para senha e JWT. | `.env.example`, `.gitignore`. | Atendido |
| RNF-016 | Migrations com Flyway | Persistencia | Schema deve ser versionado. | Existe migration baseline e Flyway roda nos testes/execução. | `src/main/resources/db/migration`, logs do `mvn verify`. | Atendido |
| RNF-017 | Senhas com BCrypt | Seguranca | Senhas de usuários devem usar hash seguro. | Backend configura `BCryptPasswordEncoder`. | `SecurityConfig`. | Atendido |
| RNF-018 | Tratamento padronizado de erros | Operação | API deve responder erros sem stacktrace ao usuário. | Exceptions sao tratadas por handler global. | `RestExceptionHandler`, OpenAPI. | Atendido |

## 6. Matriz de rastreabilidade

| Requisito | Codigo/Componente relacionado | Endpoint/Contrato | Teste/Evidencia | Documento relacionado |
|---|---|---|---|---|
| RF-001 | `CustomersController`, `CreateCustomerUseCase` | `POST /api/v1/customers` | `AdministrativeCrudIntegrationTest` | OpenAPI, README |
| RF-002 | `CreateServiceOrderUseCase`, `CustomerRepository` | `POST /api/v1/service-orders` | `CreateServiceOrderUseCaseTest` | DDD, Event Storming |
| RF-003 | `Document` | Clientes e criação de OS | `CustomerTest`, `SensitiveDataValidationIntegrationTest` | DDD, Security Report |
| RF-004 | `VehiclesController`, `CreateVehicleUseCase` | `POST /api/v1/vehicles` | `AdministrativeCrudIntegrationTest` | OpenAPI |
| RF-005 | `Plate` | Veiculos e tracking | `VehicleTest`, `SensitiveDataValidationIntegrationTest` | DDD |
| RF-006 | `CreateServiceOrderUseCase`, `VehicleRepository` | `POST /api/v1/service-orders` | `CreateServiceOrderUseCaseTest` | DDD |
| RF-007 | `WorkshopServicesController` | `/api/v1/workshop-services` | `AdministrativeCrudIntegrationTest` | OpenAPI |
| RF-008 | `PartsController` | `/api/v1/parts` | `AdministrativeCrudIntegrationTest` | OpenAPI |
| RF-009 | `Part`, use cases de estoque | `/stock-movement`, `/reserve`, `/commit-reservation` | `PartTest`, `PartStockFlowIntegrationTest` | DDD, Architecture |
| RF-010 | `ServiceOrdersController`, `CreateServiceOrderUseCase` | `POST /api/v1/service-orders` | `ServiceOrderFlowIntegrationTest` | Event Storming, Domain Storytelling |
| RF-011 | `AddServiceToServiceOrderUseCase` | `POST /api/v1/service-orders/{id}/services` | `ServiceOrderFlowIntegrationTest` | OpenAPI |
| RF-012 | `AddPartToServiceOrderUseCase` | `POST /api/v1/service-orders/{id}/parts` | `ServiceOrderFlowIntegrationTest` | OpenAPI |
| RF-013 | `GenerateServiceOrderBudgetUseCase`, `ServiceOrder` | `POST /api/v1/service-orders/{id}/budget/generate` | `GenerateServiceOrderBudgetUseCaseTest` | DDD, Event Storming |
| RF-014 | `ServiceOrderStatus.AGUARDANDO_APROVACAO` | Budget generate | `ServiceOrderFlowIntegrationTest` | DDD |
| RF-015 | `ApproveServiceOrderBudgetUseCase` | `POST /api/v1/service-orders/{id}/budget/approve` | `ApproveServiceOrderBudgetUseCaseTest` | Domain Storytelling |
| RF-016 | `UpdateServiceOrderStatusUseCase`, `ServiceOrder` | `PATCH /api/v1/service-orders/{id}/status` | `UpdateServiceOrderStatusUseCaseTest` | DDD |
| RF-017 | `TrackServiceOrderUseCase`, `AuthorizationService` | `GET /api/v1/service-orders/tracking` | `TrackServiceOrderUseCaseTest`, `SecurityAuthorizationIntegrationTest` | OpenAPI |
| RF-018 | `ListServiceOrdersUseCase` | `GET /api/v1/service-orders` | `ListServiceOrdersUseCaseTest` | README |
| RF-019 | `FindServiceOrderUseCase` | `GET /api/v1/service-orders/{id}` | `SecurityAuthorizationIntegrationTest` | OpenAPI |
| RF-020 | `GetAverageServiceOrderExecutionTimeUseCase` | `GET /api/v1/service-orders/metrics/average-execution-time` | `ServiceOrderFlowIntegrationTest` | README |
| RF-021 | `AuthController`, `LoginUseCase`, `JwtService` | `POST /api/v1/auth/login` | `JwtServiceTest`, `SecurityAuthorizationIntegrationTest` | Security Report |
| RNF-001 | `AutoCareHubApiApplication` | Aplicação Spring Boot | `AutoCareHubApiApplicationTests` | Architecture |
| RNF-002 | Pacotes em camadas | Nao se aplica | Estrutura `src/main/java/br/com/autocarehub` | Architecture |
| RNF-003 | Controllers REST e OpenAPI | `/api/v1/**` | MockMvc e OpenAPI | README |
| RNF-004 | Springdoc/Swagger | `/swagger-ui.html`, `/openapi.yaml` | `SecurityAuthorizationIntegrationTest` | README |
| RNF-005 | JPA/PostgreSQL | `DB_URL` | Docker Compose e `application.yml` | Architecture |
| RNF-006 | Modelo relacional | Tabelas Flyway | Migration baseline | Architecture |
| RNF-007 | Dockerfile | Build da API | Dockerfile revisado | README |
| RNF-008 | Docker Compose | `docker-compose.yml` | `docker compose config --quiet` | README |
| RNF-009 | README e `.env.example` | Comandos locais | Validação manual/documental | README |
| RNF-010 | `SecurityConfig`, JWT | APIs protegidas | `SecurityAuthorizationIntegrationTest` | Security Report |
| RNF-011 | `Document`, `Plate` | Entradas sensiveis | Testes de validação | DDD |
| RNF-012 | Testes automatizados | `src/test/java` | `mvn test` | Testing |
| RNF-013 | JaCoCo | `mvn verify` | `target/site/jacoco` | Testing, Static Analysis |
| RNF-014 | Scans de seguranca | Nao se aplica | `docs/SECURITY_REPORT.md` | Security Report |
| RNF-015 | `.env.example`, `.gitignore` | Variaveis locais | Arquivos revisados | Security Report |
| RNF-016 | Flyway | `src/main/resources/db/migration` | Logs de migration nos testes | Architecture |
| RNF-017 | `SecurityConfig` | Login/usuários | `BCryptPasswordEncoder` | Security Report |
| RNF-018 | `RestExceptionHandler` | Erros da API | Responses padronizados | OpenAPI |

## 7. Conclusao

Os requisitos funcionais e nao funcionais do MVP estao cobertos por codigo, contrato OpenAPI, testes ou documentação. Nao foram incluidos requisitos de pagamento, WhatsApp, SMS, e-mail real, fornecedores, app mobile, mensageria ou cloud porque essas integrações nao fazem parte do codigo do AutoCare Hub.
