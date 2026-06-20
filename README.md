# AutoCare Hub

AutoCare Hub e um MVP academico desenvolvido para o Tech Challenge FIAP. A entrega principal e um backend monolitico em Java/Spring Boot para gestao de uma oficina mecanica, com API REST, arquitetura em camadas, conceitos de DDD, autenticacao JWT, documentacao OpenAPI/Swagger, migrations com Flyway, Docker, testes automatizados e relatorio de vulnerabilidades.

O repositorio tambem possui um frontend Vue/Vite em `frontend/` para demonstracao da experiencia web, mas o foco do roteiro oficial e o backend.

## Contexto do Desafio

O Tech Challenge solicita um MVP backend para uma oficina mecanica, capaz de organizar o ciclo de atendimento de clientes e veículos:

- cadastro de clientes;
- cadastro de veículos;
- cadastro de servicos;
- cadastro de pecas e insumos;
- controle de estoque;
- criacao de Ordem de Servico;
- geracao e aprovacao de orcamento;
- acompanhamento da Ordem de Servico pelo cliente via API;
- seguranca nas APIs administrativas;
- documentacao, testes, Docker e relatorio de vulnerabilidades.

## Problema Resolvido

Oficinas mecanicas costumam lidar com informacoes espalhadas sobre clientes, veículos, servicos, pecas, orcamentos e andamento das Ordens de Servico. Isso dificulta rastreabilidade, controle de estoque, comunicacao com o cliente e gestao do atendimento.

O AutoCare Hub centraliza esses dados em uma API REST, permitindo que a oficina registre o atendimento desde a identificacao do cliente ate a entrega do veículo.

## Objetivo do MVP

Entregar um backend monolitico funcional, testavel e documentado para:

- administrar clientes, veículos, servicos, pecas e insumos;
- criar Ordens de Servico completas;
- gerar orcamentos automaticamente a partir de servicos e pecas;
- permitir aprovacao do orcamento;
- controlar status da OS;
- consultar o andamento da OS pelo cliente;
- proteger APIs administrativas com JWT;
- validar dados sensiveis como CPF/CNPJ e placa.

## Funcionalidades Implementadas

- Autenticacao com JWT.
- CRUD administrativo de clientes.
- CRUD administrativo de veículos.
- CRUD administrativo de servicos da oficina.
- CRUD administrativo de pecas e insumos.
- Controle de estoque com entrada, saida, reserva, liberacao e baixa.
- Criacao de Ordem de Servico com cliente identificado por CPF/CNPJ.
- Cadastro ou vinculacao de veículo na criacao da OS.
- Inclusao de servicos solicitados.
- Inclusao de pecas e insumos.
- Geracao automatica de orcamento.
- Aprovacao de orcamento.
- Controle de status da OS.
- Consulta de OS pelo cliente via API.
- Tempo medio de execucao de Ordens de Servico finalizadas.
- Cadastro e consulta de interessados em parceria.
- Gestao basica de usuarios, permissoes e preferencias.
- Swagger/OpenAPI.
- Dockerfile e `docker-compose.yml`.
- Testes unitarios e de integracao.
- Documentacao DDD e Event Storming.
- Relatorio de vulnerabilidades.

## Tecnologias Utilizadas

Backend:

- Java 21
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL 16
- Flyway
- Maven
- JWT com JJWT
- Springdoc Swagger UI
- OpenAPI Generator
- JaCoCo
- OWASP Dependency-Check
- JUnit 5
- Mockito
- H2 em testes
- Testcontainers

Frontend demonstrativo:

- Vue 3
- Vite 8
- Pinia
- Vue Router
- Lucide Vue
- ESLint

Infraestrutura:

- Docker
- Docker Compose

## Justificativa do Banco de Dados

O banco escolhido para o ambiente principal e PostgreSQL.

A escolha e adequada ao dominio porque o MVP trabalha com dados relacionais e consistentes:

- um cliente pode ter varios veículos;
- uma OS pertence a um cliente e a um veículo;
- uma OS possui servicos e pecas;
- pecas possuem quantidade em estoque;
- movimentacoes de estoque precisam manter integridade;
- usuarios podem estar associados a perfis, empresas e permissoes.

PostgreSQL oferece transacoes, integridade referencial, indices, suporte amplo no ecossistema Spring e boa aderencia a cenarios reais de sistemas administrativos.

Nos testes automatizados, o projeto usa H2 e/ou Testcontainers conforme o tipo de teste, mantendo execucao local reproduzivel.

## Arquitetura do Projeto

O backend e um monolito em camadas. A separacao principal e:

```text
src/main/java/br/com/autocarehub
|-- domain
|   |-- enums
|   |-- exception
|   |-- model
|   |-- policy
|   |-- service
|   `-- valueobject
|-- application
|   `-- usecase
|-- infrastructure
|   |-- config
|   |-- persistence
|   `-- security
`-- interfaces
    `-- rest
        |-- controller
        |-- exception
        `-- mapper
```

Responsabilidades:

- `domain`: regras de negocio, entidades, value objects, enums, excecoes e politicas.
- `application`: use cases que coordenam os fluxos da aplicacao.
- `infrastructure`: persistencia JPA, configuracoes, seguranca e adaptadores.
- `interfaces`: controllers REST, tratamento de excecoes REST e mapeamento de DTOs.

Controllers nao acessam repositories diretamente. Eles chamam use cases, que aplicam regras e interagem com portas/adaptadores.

## Estrutura de Pastas

```text
.
|-- docs
|   |-- DDD_DOCUMENTATION.md
|   |-- EVENT_STORMING.md
|   |-- SECURITY_REPORT.md
|   |-- SECURITY_SCAN_GUIDE.md
|   |-- DELIVERY_DOCUMENT.md
|   `-- openapi/openapi.yaml
|-- frontend
|   |-- package.json
|   |-- package-lock.json
|   `-- src
|-- security-reports
|   `-- frontend-dependencies/npm-audit-report.json
|-- src
|   |-- main
|   |   |-- java/br/com/autocarehub
|   |   `-- resources
|   |       |-- application.yml
|   |       `-- db/migration/V1__create_autocarehub_baseline.sql
|   `-- test
|-- Dockerfile
|-- docker-compose.yml
|-- pom.xml
|-- .env.example
`-- README.md
```

## Aplicacao de DDD

O projeto aplica DDD de forma pragmatica dentro de um monolito em camadas.

Principais elementos:

- Entidades de dominio: `Customer`, `Vehicle`, `ServiceOrder`, `WorkshopService`, `Part`, `Budget`, `BudgetItem`, `StockMovement`, `User`.
- Value Objects: `Document`, `Plate`, `Money`, `Address`.
- Enums de dominio: `ServiceOrderStatus`, `StockMovementType`, `DocumentType`, `UserRole`.
- Excecoes de dominio: `DomainException`, `InvalidServiceOrderStatusTransitionException`.
- Politicas de dominio: `PlatformFeePolicy`.
- Use cases de aplicacao: criacao de OS, geracao de orcamento, aprovacao de orcamento, controle de estoque, CRUDs administrativos e autenticacao.

A Ordem de Servico e o agregado central do fluxo de atendimento. Ela conecta cliente, veículo, servicos, pecas, orcamento, status e historico de andamento.

Documentacao complementar:

```text
docs/DDD_DOCUMENTATION.md
docs/EVENT_STORMING.md
```

## Linguagem Ubiqua Resumida

| Termo | Significado |
| --- | --- |
| Cliente | Pessoa fisica ou juridica atendida pela oficina, identificada por CPF ou CNPJ. |
| Veiculo | Bem do cliente atendido pela oficina, identificado por placa, marca, modelo e ano. |
| Ordem de Servico | Registro principal do atendimento da oficina. |
| Servico | Atividade executada pela oficina, com descricao e preco. |
| Peca/Insumo | Item fisico usado no atendimento e controlado em estoque. |
| Estoque | Quantidade disponivel, reservada e minima de pecas/insumos. |
| Orcamento | Composicao financeira da OS, calculada por servicos e pecas. |
| Aprovacao | Confirmacao do cliente para executar o orcamento. |
| Status da OS | Etapa atual da Ordem de Servico. |

Status da OS:

```text
RECEBIDA
EM_DIAGNOSTICO
AGUARDANDO_APROVACAO
EM_EXECUCAO
FINALIZADA
ENTREGUE
```

## Fluxos Principais

### Criacao da OS

1. A API recebe CPF/CNPJ do cliente.
2. Se o cliente existir, ele e vinculado a OS.
3. Se o cliente nao existir, ele pode ser cadastrado.
4. O veículo e identificado por placa.
5. Se o veículo nao existir, ele pode ser cadastrado e vinculado ao cliente.
6. Servicos solicitados sao incluidos.
7. Pecas e insumos podem ser incluidos.
8. A OS e criada com status inicial adequado.

Endpoint principal:

```text
POST /api/v1/service-orders
```

### Geracao de Orcamento

1. A OS possui servicos e, opcionalmente, pecas.
2. O sistema calcula total de servicos.
3. O sistema calcula total de pecas.
4. O sistema calcula total geral.
5. A OS passa para `AGUARDANDO_APROVACAO` quando aplicavel.

Endpoint:

```text
POST /api/v1/service-orders/{serviceOrderId}/budget/generate
```

### Aprovacao do Orcamento

1. O cliente ou usuario autorizado aprova o orcamento.
2. A OS e liberada para execucao.
3. Reservas de pecas podem ser convertidas em baixa de estoque quando aplicavel.
4. O status passa para uma etapa coerente com a execucao.

Endpoint:

```text
POST /api/v1/service-orders/{serviceOrderId}/budget/approve
```

### Acompanhamento da OS

O cliente pode acompanhar o andamento da Ordem de Servico via API, consultando dados basicos da OS, veículo, status, servicos, pecas, orcamento e historico disponivel.

Endpoint:

```text
GET /api/v1/service-orders/tracking
```

### Gestao de Estoque

1. Pecas e insumos sao cadastrados com preco, quantidade e estoque minimo.
2. Entradas aumentam a quantidade disponivel.
3. Saidas reduzem a quantidade disponivel.
4. Reservas bloqueiam quantidade para orcamento.
5. Liberacao devolve quantidade reservada.
6. Baixa definitiva reduz estoque quando a peca e usada ou vendida.
7. O dominio impede estoque negativo.

Endpoints principais:

```text
GET /api/v1/parts
POST /api/v1/parts
PATCH /api/v1/parts/{partId}/stock
PATCH /api/v1/parts/{partId}/stock-movement
PATCH /api/v1/parts/{partId}/reserve
PATCH /api/v1/parts/{partId}/release-reservation
PATCH /api/v1/parts/{partId}/commit-reservation
```

## Seguranca

### JWT

- Login por `POST /api/v1/auth/login`.
- Token JWT assinado com segredo vindo de variavel de ambiente.
- Expiracao configuravel por `JWT_EXPIRATION_MINUTES`.
- APIs administrativas exigem token Bearer.

### Validacoes

- CPF real.
- CNPJ real.
- Placa brasileira antiga e Mercosul.
- E-mail.
- Tamanhos maximos em campos textuais.
- Precos e quantidades nao negativos.
- Rejeicao de campos desconhecidos via Jackson.

### Tratamento de Erros

- Handler REST global.
- Erros padronizados.
- Excecoes de dominio e aplicacao convertidas para respostas HTTP adequadas.
- Sem retorno intencional de stacktrace ao usuario.

### Protecao de Endpoints

- Spring Security centraliza autorizacao por rota.
- Endpoints administrativos exigem roles/perfis adequados.
- CORS configuravel e sem wildcard.
- Swagger pode ser desabilitado por variavel de ambiente.

## Como Executar Localmente

Pre-requisitos:

- Java 21
- Maven 3.9+
- Docker e Docker Compose para subir PostgreSQL

Crie o arquivo `.env` a partir do exemplo:

```powershell
Copy-Item .env.example .env
```

Edite o `.env` e informe pelo menos:

```text
POSTGRES_PASSWORD=[PREENCHER - senha local do PostgreSQL]
JWT_SECRET=[PREENCHER - segredo local com pelo menos 32 bytes]
```

Suba somente o banco:

```powershell
docker compose up -d postgres
```

Execute a API:

```powershell
mvn spring-boot:run
```

URL padrao:

```text
http://localhost:8080
```

## Como Executar com Docker

Pre-requisitos:

- Docker
- Docker Compose

Crie e edite o `.env`:

```powershell
Copy-Item .env.example .env
```

Suba API e banco:

```powershell
docker compose up --build
```

Em background:

```powershell
docker compose up -d --build
```

Parar:

```powershell
docker compose down
```

Remover volume do banco local:

```powershell
docker compose down -v
```

Servicos:

```text
API: http://localhost:8080
PostgreSQL: localhost:5432
Swagger: http://localhost:8080/swagger-ui.html
```

## Frontend Demonstrativo

Pre-requisitos:

- Node.js compativel com Vite 8: `^20.19.0 || >=22.12.0`
- npm

Executar:

```powershell
cd frontend
npm install
npm run dev
```

URL padrao:

```text
http://localhost:5173
```

Build:

```powershell
cd frontend
npm run build
```

## Como Rodar Testes

Backend:

```powershell
mvn test
```

Validacao completa Maven:

```powershell
mvn verify
```

Frontend:

```powershell
cd frontend
npm run build
```

## Como Verificar Cobertura

Gerar relatorio JaCoCo:

```powershell
mvn test
```

Abrir:

```text
target/site/jacoco/index.html
```

Cobertura validada em 20/06/2026 no núcleo de negócio medido pelo JaCoCo:

| Metrica | Cobertura |
| --- | ---: |
| Instrucoes | 95,36% |
| Branches | 78,45% |
| Linhas | 97,35% |
| Metodos | 94,31% |
| Classes | 100,00% |

Resultado: 108 testes automatizados, 0 falhas, 0 erros e `mvn verify` concluído com sucesso.

Observacao: o JaCoCo exclui classes geradas pelo OpenAPI, a camada REST, a infraestrutura e records auxiliares de comando/consulta/saida. Os percentuais representam o núcleo de negócio medido, não a cobertura global de todas as classes. A cobertura de controllers e adapters ainda pode ser ampliada.

## Como Acessar Swagger

Com a API em execucao:

```text
http://localhost:8080/swagger-ui.html
```

Contrato OpenAPI versionado:

```text
docs/openapi/openapi.yaml
```

Fluxo de autenticacao no Swagger:

1. Execute `POST /api/v1/auth/login`.
2. Copie o token retornado.
3. Clique em `Authorize`.
4. Informe:

```text
Bearer <token>
```

No MVP academico, Swagger fica habilitado para facilitar avaliacao. Para desabilitar:

```text
SPRINGDOC_API_DOCS_ENABLED=false
SPRINGDOC_SWAGGER_UI_ENABLED=false
```

## Usuarios de Demonstracao

Os usuarios demo sao carregados por `src/main/resources/db/migration/V1__create_autocarehub_baseline.sql`.

A senha acadêmica dos usuários seed é exclusiva do ambiente local e não deve ser reutilizada em produção:

```text
autocare123
```

| Usuario | Perfil |
| --- | --- |
| `admin@autocarehub.com` | Admin tecnico inicial |
| `master@autocarehub.com` | Admin Master da plataforma |
| `oficina.admin@autocarehub.com` | Admin de oficina |
| `loja.admin@autocarehub.com` | Admin de loja de pecas |
| `oficina.funcionario@autocarehub.com` | Funcionario de oficina |
| `loja.funcionario@autocarehub.com` | Funcionario de loja de pecas |
| `cliente@autocarehub.com` | Cliente final demo |

Exemplo de login:

```powershell
curl -X POST http://localhost:8080/api/v1/auth/login `
  -H "Content-Type: application/json" `
  -d "{\"username\":\"admin@autocarehub.com\",\"password\":\"autocare123\"}"
```

## Endpoints Principais

Autenticacao:

```text
POST /api/v1/auth/login
```

Clientes:

```text
GET    /api/v1/customers
POST   /api/v1/customers
GET    /api/v1/customers/{customerId}
PUT    /api/v1/customers/{customerId}
DELETE /api/v1/customers/{customerId}
GET    /api/v1/customers/{customerId}/vehicles
GET    /api/v1/customers/{customerId}/service-orders
```

Veiculos:

```text
GET    /api/v1/vehicles
POST   /api/v1/vehicles
GET    /api/v1/vehicles/{vehicleId}
PUT    /api/v1/vehicles/{vehicleId}
DELETE /api/v1/vehicles/{vehicleId}
```

Servicos:

```text
GET    /api/v1/workshop-services
POST   /api/v1/workshop-services
GET    /api/v1/workshop-services/{serviceId}
PUT    /api/v1/workshop-services/{serviceId}
DELETE /api/v1/workshop-services/{serviceId}
```

Pecas e estoque:

```text
GET    /api/v1/parts
POST   /api/v1/parts
GET    /api/v1/parts/{partId}
PUT    /api/v1/parts/{partId}
DELETE /api/v1/parts/{partId}
PATCH  /api/v1/parts/{partId}/stock
PATCH  /api/v1/parts/{partId}/stock-movement
PATCH  /api/v1/parts/{partId}/reservation
PATCH  /api/v1/parts/{partId}/reserve
PATCH  /api/v1/parts/{partId}/release-reservation
PATCH  /api/v1/parts/{partId}/commit-reservation
```

Ordens de Servico:

```text
GET   /api/v1/service-orders
POST  /api/v1/service-orders
GET   /api/v1/service-orders/{serviceOrderId}
POST  /api/v1/service-orders/{serviceOrderId}/services
POST  /api/v1/service-orders/{serviceOrderId}/parts
POST  /api/v1/service-orders/{serviceOrderId}/budget/generate
POST  /api/v1/service-orders/{serviceOrderId}/budget/approve
PATCH /api/v1/service-orders/{serviceOrderId}/status
GET   /api/v1/service-orders/tracking
GET   /api/v1/service-orders/metrics/average-execution-time
```

Usuarios:

```text
GET   /api/v1/users/me
PUT   /api/v1/users/me
PATCH /api/v1/users/me/password
GET   /api/v1/users/me/preferences/home
PUT   /api/v1/users/me/preferences/home
GET   /api/v1/users
POST  /api/v1/users
GET   /api/v1/users/partners
PUT   /api/v1/users/{userId}
PATCH /api/v1/users/{userId}/password
```

Interessados em parceria:

```text
POST /api/v1/demo-leads
GET  /api/v1/demo-leads
```

## Como Executar o Scan de Vulnerabilidades

Backend:

```powershell
mvn dependency-check:check
```

Relatorios gerados:

```text
target/dependency-check/
```

Frontend:

```powershell
cd frontend
npm audit --json
```

Guia completo:

```text
docs/SECURITY_SCAN_GUIDE.md
```

## Relatorio de Vulnerabilidades

Relatorio oficial atualizado:

```text
docs/SECURITY_REPORT.md
```

Evidencias atuais:

```text
target/dependency-check/dependency-check-report.html
target/dependency-check/dependency-check-report.json
security-reports/frontend-dependencies/npm-audit-report.json
```

Resultado atual documentado:

- Dependency-Check backend: 0 vulnerabilidades.
- npm audit frontend: 0 vulnerabilidades.
- Docker Scout: 0 vulnerabilidades na imagem final distroless.
- Gitleaks: 0 leaks em 35 commits.
- Semgrep: 0 achados e 0 erros em 190 arquivos.

## Decisoes Tecnicas

- Monolito em camadas para reduzir complexidade operacional no MVP.
- DDD aplicado no dominio e na camada de aplicacao sem criar microsservicos.
- PostgreSQL como banco principal por consistencia relacional.
- Flyway para versionamento de schema.
- OpenAPI versionado para contrato REST e Swagger.
- JWT stateless para proteger APIs administrativas.
- Docker Compose para ambiente local reproduzivel.
- Senhas com BCrypt.
- Segredos por variaveis de ambiente.
- Migrations consolidadas para facilitar recriacao limpa do banco academico.
- Frontend mantido como demonstracao complementar, sem ser o foco da entrega backend.

## Limitacoes Conhecidas

- Nao ha pagamento online.
- Nao ha envio real de e-mail, SMS ou WhatsApp.
- Historico de status da OS e simplificado para o MVP.
- Controle de multiplas oficinas/lojas existe de forma simplificada no mesmo monolito.
- Swagger fica publico no ambiente local academico.
- Um teste dinamico dedicado de segurança permanece como melhoria futura.
- Cobertura global ainda pode ser ampliada para se aproximar da meta desejada de 95%.

## Melhorias Futuras

- Ampliar cobertura automatizada de controllers, adapters e fluxos negativos.
- Criar auditoria de acoes sensiveis.
- Melhorar historico detalhado de status da OS.
- Integrar notificacoes reais para cliente.
- Restringir Swagger por ambiente/perfil em producao.
- Evoluir multiempresa/multitenancy.
- Adicionar pipeline CI com testes, cobertura e scans de seguranca.
- Reexecutar Docker Scout, Gitleaks e Semgrep em cada ciclo de entrega.
- Evoluir metricas operacionais da oficina.
