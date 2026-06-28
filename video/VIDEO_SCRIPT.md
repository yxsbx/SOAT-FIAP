# Roteiro do Vídeo de Apresentação - Tech Challenge

Tempo máximo: 15 minutos  
Projeto: AutoCare Hub  
Apresentadora: Yasmin Barcelos Pires - RM370897  
Formato: gravação de tela com narração objetiva, alternando entre documentação, código, Swagger e evidências já geradas.

## 1. Roteiro ajustado por tempo

### 0:00 - 0:35 | Abertura e documento final

**O que falar**

- "Meu nome é Yasmin Barcelos Pires, RM370897, e esta é uma entrega individual do Tech Challenge FIAP."
- "O projeto é o AutoCare Hub, um backend para gestão de oficina mecânica, com frontend demonstrativo apenas para apoiar a apresentação."

**O que mostrar**

- Abrir `docs/DELIVERY_DOCUMENT.md`.
- Mostrar nome do projeto, participante, RM, Discord, link do repositório e link da documentação.

**Arquivos**

```text
docs/DELIVERY_DOCUMENT.md
README.md
```

**Requisito comprovado**

- Documento final de entrega/PDF.
- Links do repositório, documentação e participante.
- README explicativo.

### 0:35 - 1:20 | Problema e objetivo

**O que falar**

- "Oficinas lidam com clientes, veículos, serviços, peças, estoque, orçamento e status da OS em controles separados."
- "Isso gera perda de histórico, dificuldade de acompanhar status, falhas de orçamento/autorização, controle ruim de peças e dependência de planilhas ou anotações."
- "O objetivo do MVP é centralizar esse ciclo em uma API REST: cadastro, criação da OS, orçamento, aprovação, estoque e acompanhamento pelo cliente."
- "O frontend demonstrativo existe para transformar a API em uma experiência visual, mostrando para a banca como oficinas, clientes e administradores usariam o produto na prática."

**O que mostrar**

- `README.md`, início do documento e seção `Escopo do MVP`.
- `docs/DELIVERY_DOCUMENT.md`, seções `Descrição resumida do projeto` e `Objetivo do MVP`.

**Requisito comprovado**

- Problema e objetivo do projeto.
- Escopo do MVP.

### 1:20 - 2:20 | Arquitetura backend

**O que falar**

- "O backend é um monolito em camadas."
- "A camada `domain` concentra regras de negócio; `application` coordena use cases; `infrastructure` contém persistência, segurança e configurações; `interfaces` expõe controllers REST e mappers."
- "Controllers não acessam repositórios diretamente; eles chamam use cases."

**O que mostrar**

- Árvore `src/main/java/br/com/autocarehub`.
- Abrir rapidamente um controller, um use case e uma entidade.

**Arquivos**

```text
src/main/java/br/com/autocarehub/domain/model/ServiceOrder.java
src/main/java/br/com/autocarehub/application/usecase/serviceorder/CreateServiceOrderUseCase.java
src/main/java/br/com/autocarehub/interfaces/rest/controller/ServiceOrdersController.java
src/main/java/br/com/autocarehub/infrastructure/persistence/repository/ServiceOrderRepositoryAdapter.java
```

**Requisito comprovado**

- Backend monolítico.
- Arquitetura em camadas.

### 2:20 - 3:35 | DDD e linguagem ubíqua

**O que falar**

- "A Ordem de Serviço é o agregado central."
- "O domínio modela clientes, veículos, serviços, peças, orçamento, estoque e status da OS."
- "CPF/CNPJ e placa são value objects com validação de domínio."
- "Os status internos aparecem em português no domínio, e a API expõe códigos externos como `WAITING_APPROVAL` e `IN_PROGRESS`."

**O que mostrar**

- `docs/DDD_DOCUMENTATION.md`: linguagem ubíqua, entidades, value objects, agregados e diagramas.
- Abrir classes principais.

**Arquivos**

```text
docs/DDD_DOCUMENTATION.md
src/main/java/br/com/autocarehub/domain/model/ServiceOrder.java
src/main/java/br/com/autocarehub/domain/model/Part.java
src/main/java/br/com/autocarehub/domain/valueobject/Document.java
src/main/java/br/com/autocarehub/domain/valueobject/Plate.java
src/main/java/br/com/autocarehub/domain/enums/ServiceOrderStatus.java
```

**Requisito comprovado**

- Aplicação de DDD.
- Linguagem Ubíqua.
- Diagramas de DDD.
- Validação de CPF/CNPJ e placa.

### 3:35 - 4:25 | Event Storming

**O que falar**

- "O Event Storming documenta comandos, eventos, agregados, políticas, exceções e pontos de decisão."
- "Os fluxos cobertos são criação/acompanhamento da OS e gestão de peças e insumos."
- "No MVP, eventos são modelagem documentada, não event store implementado."

**O que mostrar**

- `docs/EVENT_STORMING.md`: fluxos, comandos/eventos e diagramas Mermaid.

**Arquivos**

```text
docs/EVENT_STORMING.md
```

**Requisito comprovado**

- Event Storming completo dos fluxos exigidos.
- Criação e acompanhamento da OS.
- Gestão de peças e insumos.

### 4:25 - 5:10 | Swagger/OpenAPI

**O que falar**

- "A API está documentada no Swagger e o contrato OpenAPI está versionado no repositório."
- "Para testar endpoints protegidos, primeiro faço login e uso o token no botão Authorize."

**O que mostrar**

- Abrir `http://localhost:8080/swagger-ui.html`.
- Abrir `docs/openapi/openapi.yaml`.

**Endpoints**

```text
POST /api/v1/auth/login
```

**Arquivos**

```text
docs/openapi/openapi.yaml
src/main/java/br/com/autocarehub/interfaces/rest/controller/AuthController.java
```

**Requisito comprovado**

- Swagger/OpenAPI.
- JWT para APIs administrativas.

### 5:10 - 6:15 | CRUDs obrigatórios

**O que falar**

- "Os CRUDs administrativos cobrem clientes, veículos, serviços, peças e insumos."
- "Clientes e veículos sustentam a criação da OS; serviços e peças alimentam orçamento e estoque."

**O que mostrar**

- No Swagger, mostrar os grupos `customers`, `vehicles`, `workshop-services` e `parts`.
- Se o tempo permitir, executar uma listagem e uma criação preparada.

**Endpoints**

```text
GET  /api/v1/customers
POST /api/v1/customers
GET  /api/v1/vehicles
POST /api/v1/vehicles
GET  /api/v1/workshop-services
GET  /api/v1/parts
POST /api/v1/parts
```

**Arquivos**

```text
src/main/java/br/com/autocarehub/interfaces/rest/controller/CustomersController.java
src/main/java/br/com/autocarehub/interfaces/rest/controller/VehiclesController.java
src/main/java/br/com/autocarehub/interfaces/rest/controller/WorkshopServicesController.java
src/main/java/br/com/autocarehub/interfaces/rest/controller/PartsController.java
src/main/resources/db/migration/V1__create_autocarehub_baseline.sql
```

**Requisito comprovado**

- CRUD de clientes.
- CRUD de veículos.
- CRUD de serviços.
- CRUD de peças e insumos.
- Cadastro de veículo com placa, marca, modelo e ano.

### 6:15 - 7:30 | Criação da Ordem de Serviço

**O que falar**

- "A criação da OS identifica o cliente por CPF/CNPJ."
- "O fluxo pode cadastrar ou vincular veículo por placa, marca, modelo e ano."
- "A OS exige serviços solicitados e pode incluir peças/insumos."
- "A opção `generateBudget` permite gerar orçamento no próprio fluxo de criação."

**O que mostrar**

- Swagger em `POST /api/v1/service-orders`.
- `CreateServiceOrderUseCase.java`.
- Migrations Flyway com dados seed, se usar dados prontos.

**Endpoint**

```text
POST /api/v1/service-orders
```

**Arquivos**

```text
src/main/java/br/com/autocarehub/application/usecase/serviceorder/CreateServiceOrderUseCase.java
src/main/java/br/com/autocarehub/domain/model/ServiceOrder.java
src/main/resources/db/migration/V1__create_autocarehub_baseline.sql
```

**Requisito comprovado**

- Criação da OS.
- Identificação por CPF/CNPJ.
- Inclusão de serviços solicitados.
- Inclusão de peças e insumos.
- Geração automática de orçamento.

### 7:30 - 8:45 | Orçamento, aprovação, status e acompanhamento

**O que falar**

- "O orçamento soma serviços e peças."
- "Ao gerar orçamento, o status vai para aguardando aprovação."
- "Ao aprovar, o aceite é registrado e reservas de peças são confirmadas."
- "A execução não é iniciada automaticamente; ela exige transição administrativa para `IN_PROGRESS`, sem mentir sobre o fluxo."
- "O cliente acompanha a OS pela API de tracking."

**O que mostrar**

- Swagger nos endpoints de orçamento, status, tracking e média de execução.
- `GenerateServiceOrderBudgetUseCase`, `ApproveServiceOrderBudgetUseCase`, `UpdateServiceOrderStatusUseCase` e `TrackServiceOrderUseCase`.

**Endpoints**

```text
POST  /api/v1/service-orders/{serviceOrderId}/budget/generate
POST  /api/v1/service-orders/{serviceOrderId}/budget/approve
PATCH /api/v1/service-orders/{serviceOrderId}/status
GET   /api/v1/service-orders/tracking
GET   /api/v1/service-orders/metrics/average-execution-time
```

**Arquivos**

```text
src/main/java/br/com/autocarehub/application/usecase/serviceorder/GenerateServiceOrderBudgetUseCase.java
src/main/java/br/com/autocarehub/application/usecase/serviceorder/ApproveServiceOrderBudgetUseCase.java
src/main/java/br/com/autocarehub/application/usecase/serviceorder/UpdateServiceOrderStatusUseCase.java
src/main/java/br/com/autocarehub/application/usecase/serviceorder/TrackServiceOrderUseCase.java
src/main/java/br/com/autocarehub/application/usecase/serviceorder/GetAverageServiceOrderExecutionTimeUseCase.java
```

**Requisito comprovado**

- Geração de orçamento.
- Aprovação de orçamento pelo cliente.
- Status da OS.
- Acompanhamento da OS pelo cliente via API.
- Monitoramento do tempo médio de execução.

### 8:45 - 9:35 | Controle de estoque

**O que falar**

- "O estoque controla quantidade total, reservada e disponível."
- "Entradas, saídas, reservas, liberações e baixas impedem estoque negativo."
- "Quando o orçamento é aprovado, as reservas das peças podem ser confirmadas."

**O que mostrar**

- Swagger em endpoints de estoque.
- `Part.java` e use cases de estoque.

**Endpoints**

```text
PATCH /api/v1/parts/{partId}/stock-movement
PATCH /api/v1/parts/{partId}/reserve
PATCH /api/v1/parts/{partId}/release-reservation
PATCH /api/v1/parts/{partId}/commit-reservation
```

**Arquivos**

```text
src/main/java/br/com/autocarehub/domain/model/Part.java
src/main/java/br/com/autocarehub/application/usecase/part/RegisterPartStockMovementUseCase.java
src/main/java/br/com/autocarehub/application/usecase/part/ReservePartStockUseCase.java
src/main/java/br/com/autocarehub/application/usecase/part/CommitPartReservationUseCase.java
```

**Requisito comprovado**

- Controle de estoque.
- Gestão de peças e insumos.

### 9:35 - 10:25 | Segurança e validações

**O que falar**

- "As APIs administrativas exigem JWT."
- "Senhas usam BCrypt e o segredo JWT vem de variável de ambiente."
- "CPF/CNPJ e placa são validados por value objects e também cobertos por testes."

**O que mostrar**

- Login no Swagger e botão `Authorize`.
- `SecurityConfig.java`, `Document.java`, `Plate.java`.
- Teste de validação sensível.

**Endpoint**

```text
POST /api/v1/auth/login
```

**Arquivos**

```text
src/main/java/br/com/autocarehub/infrastructure/security/SecurityConfig.java
src/main/java/br/com/autocarehub/domain/valueobject/Document.java
src/main/java/br/com/autocarehub/domain/valueobject/Plate.java
src/test/java/br/com/autocarehub/interfaces/rest/SensitiveDataValidationIntegrationTest.java
```

**Requisito comprovado**

- JWT para APIs administrativas.
- Validação de CPF/CNPJ.
- Validação de placa.

### 10:25 - 11:25 | Docker e execução local

**O que falar**

- "O projeto sobe com Docker Compose: PostgreSQL, API e frontend."
- "Variáveis sensíveis ficam no `.env`; o repositório mantém apenas `.env.example`."
- "O Dockerfile da API usa runtime sem privilégios e o Compose configura segurança do container."
- "Para provar o projeto do zero, eu paro containers antigos, removo o volume local do banco, subo novamente e mostro o Flyway criando a base."

**O que mostrar**

- `Dockerfile`, `docker-compose.yml`, `frontend/Dockerfile`.
- Terminal com os containers subindo ou já ativos.
- Logs da API mostrando inicialização sem erro e migrations Flyway aplicadas.

**Comandos**

```powershell
cp .env.example .env
docker compose down
docker compose down --remove-orphans
docker compose down -v
docker compose up -d --build
docker compose ps
docker compose logs -f app
```

**Arquivos**

```text
Dockerfile
docker-compose.yml
frontend/Dockerfile
.env.example
```

**Requisito comprovado**

- Dockerfile.
- `docker-compose.yml`.
- Execução local reproduzível.
- Aplicação iniciando sem erro.
- Migrations Flyway executando no startup.

### 11:25 - 12:25 | Testes e cobertura

**O que falar**

- "Há testes unitários e de integração cobrindo domínio, use cases, autorização, REST, estoque e fluxo da OS."
- "A cobertura consolidada é superior ao mínimo exigido de 80%: 96,36% de instruções, 97,28% de linhas e 90,28% de branches."
- "O gate interno exige 90%."

**O que mostrar**

- Pasta `src/test/java`.
- `pom.xml` com JaCoCo.
- Relatório JaCoCo em `target/site/jacoco/index.html`.

**Comandos**

```powershell
mvn clean test
mvn clean verify
```

**Arquivos**

```text
pom.xml
src/test/java/br/com/autocarehub/interfaces/rest/ServiceOrderFlowIntegrationTest.java
src/test/java/br/com/autocarehub/interfaces/rest/PartStockFlowIntegrationTest.java
src/test/java/br/com/autocarehub/interfaces/rest/AdministrativeCrudIntegrationTest.java
target/site/jacoco/index.html
```

**Requisito comprovado**

- Testes unitários e de integração.
- Cobertura mínima de 80%.

### 12:25 - 13:20 | Relatório de vulnerabilidades

**O que falar**

- "O relatório registra scans reais, vulnerabilidades encontradas, correções e risco residual."
- "Dependency-Check, npm audit, Docker Scout, Gitleaks e Semgrep foram documentados."
- "A imagem frontend mantém 1 CVE média sem correção disponível; isso está assumido como risco temporário, não escondido."

**O que mostrar**

- `docs/SECURITY_REPORT.md`: resumo executivo, resultados por ferramenta, vulnerabilidades corrigidas e risco aceito.
- Relatório OWASP Dependency-Check, se gerado localmente.

**Comandos**

```powershell
mvn dependency-check:check
```

**Arquivos**

```text
docs/SECURITY_REPORT.md
docs/SECURITY_SCAN_GUIDE.md
target/dependency-check/dependency-check-report.html
target/dependency-check/dependency-check-report.json
```

**Requisito comprovado**

- Relatório de vulnerabilidades com scan real.
- Correções e riscos documentados.

### 13:20 - 14:30 | Demonstração prática pelo Swagger

**O que falar**

- "Agora mostro o fluxo mínimo executável: login, consulta de cadastros, OS, orçamento, aprovação, status, tracking, estoque e métrica."
- "Para economizar tempo, posso usar dados seed da migration."

**Sequência de teste**

1. `POST /api/v1/auth/login`
2. `GET /api/v1/customers`
3. `POST /api/v1/customers`, se usar payload pronto; caso contrário, mostrar listagem seed.
4. `GET /api/v1/vehicles`
5. `POST /api/v1/vehicles`, se usar payload pronto; caso contrário, mostrar listagem seed.
6. `GET /api/v1/workshop-services`
7. `GET /api/v1/parts`
8. `PATCH /api/v1/parts/{partId}/stock-movement`
9. `POST /api/v1/service-orders`
10. `POST /api/v1/service-orders/{serviceOrderId}/budget/generate`
11. `POST /api/v1/service-orders/{serviceOrderId}/budget/approve`
12. `PATCH /api/v1/service-orders/{serviceOrderId}/status`
13. `GET /api/v1/service-orders/tracking`
14. `GET /api/v1/service-orders/metrics/average-execution-time`

**O que mostrar acontecendo**

- Login retornando JWT.
- Chamada administrativa funcionando com Bearer token.
- Cliente e veículo sendo criados ou listados a partir dos dados seed.
- Serviços e peças sendo listados.
- Estoque recebendo movimentação.
- OS sendo criada.
- Orçamento sendo gerado e depois aprovado.
- Status mudando para `IN_PROGRESS` em chamada administrativa explícita.
- Cliente consultando acompanhamento da OS.
- Métrica de tempo médio retornando resposta.

**Requisito comprovado**

- Funcionamento prático do MVP.
- CRUDs, OS, orçamento, aprovação, acompanhamento, estoque, JWT e métricas.

### 14:30 - 15:00 | Fechamento

**O que falar**

- "O AutoCare Hub entrega o ciclo principal de atendimento de oficina em um backend monolítico documentado, testado, dockerizado e analisado por scans."
- "As limitações estão documentadas: sem pagamento online, sem notificações reais, sem event store e com DAST dedicado como melhoria futura."
- "A entrega final, DDD, Event Storming, Swagger, relatório de segurança e roteiro estão versionados no repositório."

**O que mostrar**

- Voltar para `docs/DELIVERY_DOCUMENT.md`, seção de conclusão.

**Requisito comprovado**

- Síntese da entrega.
- Transparência sobre escopo e limitações.

## 2. Checklist rápido para gravação

- [ ] `.env` criado a partir de `.env.example`, com `POSTGRES_PASSWORD` e `JWT_SECRET`.
- [ ] `docker compose up -d --build` executado antes de gravar.
- [ ] `docker compose ps` mostrando PostgreSQL, API e frontend ativos.
- [ ] `docker compose logs app` conferido com aplicação iniciada sem erro e migrations Flyway executadas.
- [ ] Swagger aberto em `http://localhost:8080/swagger-ui.html`.
- [ ] Frontend aberto em `http://localhost:5173`, se for mostrar a interface.
- [ ] Usuário seed separado; senha local: `autocare123`.
- [ ] Token JWT obtido e configurado no `Authorize` do Swagger.
- [ ] IDs prontos para cliente, veículo, serviço, peça e OS.
- [ ] Relatório JaCoCo aberto em `target/site/jacoco/index.html`.
- [ ] Relatório Dependency-Check aberto em `target/dependency-check/dependency-check-report.html`.
- [ ] Abas abertas: README, entrega, DDD, Event Storming, segurança, OpenAPI e Swagger.
- [ ] Não executar comandos demorados durante a gravação; mencionar ou mostrar resultado já gerado.

## 3. Comandos para rodar e demonstrar

```powershell
cp .env.example .env
docker compose down
docker compose down --remove-orphans
docker compose down -v
docker compose up -d --build
docker compose ps
docker compose logs -f app
mvn clean test
mvn clean verify
mvn dependency-check:check
```

Abrir no navegador:

```text
http://localhost:8080/swagger-ui.html
target/site/jacoco/index.html
target/dependency-check/dependency-check-report.html
```

Observação: o serviço da API no `docker-compose.yml` se chama `app`. Portanto, use `docker compose logs -f app`; `backend`
não é um nome de serviço válido neste projeto.

Opcional, com cuidado: `docker volume prune` remove volumes Docker não usados por outros projetos também. Mencione apenas
se for necessário limpar volumes órfãos.

## 4. Endpoints que serão testados

```text
POST  /api/v1/auth/login
GET   /api/v1/customers
POST  /api/v1/customers
GET   /api/v1/vehicles
POST  /api/v1/vehicles
GET   /api/v1/workshop-services
GET   /api/v1/parts
PATCH /api/v1/parts/{partId}/stock-movement
POST  /api/v1/service-orders
POST  /api/v1/service-orders/{serviceOrderId}/budget/generate
POST  /api/v1/service-orders/{serviceOrderId}/budget/approve
PATCH /api/v1/service-orders/{serviceOrderId}/status
GET   /api/v1/service-orders/tracking
GET   /api/v1/service-orders/metrics/average-execution-time
```

## 5. Arquivos que serão mostrados

```text
README.md
docs/DELIVERY_DOCUMENT.md
docs/DDD_DOCUMENTATION.md
docs/EVENT_STORMING.md
docs/SECURITY_REPORT.md
docs/SECURITY_SCAN_GUIDE.md
docs/openapi/openapi.yaml
Dockerfile
docker-compose.yml
frontend/Dockerfile
pom.xml
src/main/resources/db/migration/V1__create_autocarehub_baseline.sql
src/main/java/br/com/autocarehub/domain/model/ServiceOrder.java
src/main/java/br/com/autocarehub/domain/model/Part.java
src/main/java/br/com/autocarehub/domain/valueobject/Document.java
src/main/java/br/com/autocarehub/domain/valueobject/Plate.java
src/main/java/br/com/autocarehub/interfaces/rest/controller/ServiceOrdersController.java
src/main/java/br/com/autocarehub/application/usecase/serviceorder/CreateServiceOrderUseCase.java
src/main/java/br/com/autocarehub/application/usecase/serviceorder/GenerateServiceOrderBudgetUseCase.java
src/main/java/br/com/autocarehub/application/usecase/serviceorder/ApproveServiceOrderBudgetUseCase.java
src/main/java/br/com/autocarehub/application/usecase/serviceorder/TrackServiceOrderUseCase.java
src/test/java/br/com/autocarehub/interfaces/rest/ServiceOrderFlowIntegrationTest.java
src/test/java/br/com/autocarehub/interfaces/rest/PartStockFlowIntegrationTest.java
target/site/jacoco/index.html
target/dependency-check/dependency-check-report.html
```

## 6. Critérios do Tech Challenge cobertos

- Problema e objetivo do projeto.
- Backend monolítico.
- Arquitetura em camadas.
- DDD, Linguagem Ubíqua e diagramas.
- Event Storming dos fluxos obrigatórios.
- Swagger/OpenAPI.
- Dockerfile e `docker-compose.yml`.
- README explicativo.
- CRUDs obrigatórios.
- Criação da OS.
- Geração e aprovação de orçamento.
- Acompanhamento da OS pelo cliente via API.
- Controle de estoque.
- JWT para APIs administrativas.
- Validação de CPF/CNPJ e placa.
- Testes unitários e de integração.
- Cobertura mínima de 80%.
- Relatório de vulnerabilidades com scan real.
- Documento final de entrega/PDF.
- Links do repositório, documentação e participante.

## 7. Riscos ou pendências antes de gravar

- Regenerar o PDF final se `docs/DELIVERY_DOCUMENT.md` tiver sido alterado após a última geração.
- Confirmar que o relatório JaCoCo e o Dependency-Check existem localmente antes de abrir no vídeo.
- Separar payloads e IDs antes da gravação para não gastar tempo criando dados ao vivo.
- Explicar corretamente que a aprovação do orçamento não inicia execução automaticamente; a transição para `IN_PROGRESS` é administrativa.
- Informar com transparência que o DAST dedicado fica como melhoria futura.
- Informar que a imagem frontend tem 1 CVE média sem correção disponível, registrada como risco aceito temporariamente.
