# AutoCare Hub API

API REST para gerenciamento de uma oficina mecânica. O sistema cobre cadastro de clientes, veículos, serviços de
oficina, peças, estoque, ordens de servico, orçamento, aprovação e acompanhamento de status.

## Objetivo Academico

Este projeto foi desenvolvido como MVP academico para demonstrar a construção de uma API backend usando arquitetura em
camadas, DDD, contrato OpenAPI First, persistencia relacional, migrations versionadas, autenticação JWT e
conteinerização com Docker.

## Stack Utilizada

- Java 21
- Spring Boot 3
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- Maven
- OpenAPI Generator
- Springdoc Swagger UI
- JWT com JJWT
- Docker e Docker Compose
- JUnit e Spring Boot Test
- Testcontainers com PostgreSQL para testes automatizados

## Arquitetura

O projeto segue um monolito em camadas:

- `domain`: entidades e regras centrais do negocio.
- `application`: use cases, comandos, consultas e portas de repositorio.
- `infrastructure`: configuracoes, seguranca, persistencia JPA, repositories Spring Data e adapters.
- `interfaces`: controllers REST manuais e mappers para DTOs gerados pelo OpenAPI.
- `docs/openapi`: contrato OpenAPI que define os endpoints e DTOs publicos.

Essa organização separa regra de negócio, orquestração de casos de uso, detalhes tecnicos e interface HTTP.

## Como Rodar Localmente

Pre-requisitos:

- Java 21
- Maven 3.9+
- Docker, caso queira subir PostgreSQL local via compose

Suba apenas o banco:

```bash
docker compose up -d postgres
```

Execute a aplicação localmente:

```bash
mvn spring-boot:run
```

A API ficara disponivel em:

```text
http://localhost:8080
```

Por padrao, a aplicacao usa:

```text
DB_URL=jdbc:postgresql://localhost:5432/autocarehub
DB_USERNAME=autocarehub
DB_PASSWORD=autocarehub
JWT_SECRET=change-me-change-me-change-me-change-me
JWT_EXPIRATION_MINUTES=60
```

## Como Rodar com Docker

Suba a aplicacao e o PostgreSQL:

```bash
docker compose up --build
```

Para rodar em background:

```bash
docker compose up -d --build
```

Para parar:

```bash
docker compose down
```

Para remover tambem o volume do banco:

```bash
docker compose down -v
```

## Swagger

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

Contrato OpenAPI:

```text
docs/openapi/openapi.yaml
```

## Frontend Vue 3

O projeto tambem possui um frontend em Vue 3 na pasta `frontend`.

Para rodar a API e o frontend localmente:

```bash
docker compose up -d postgres
mvn spring-boot:run
```

Em outro terminal:

```bash
cd frontend
npm install
npm run dev
```

O frontend ficara disponivel em:

```text
http://localhost:5173
```

Scripts uteis do frontend:

```bash
npm run backend:db
npm run backend:api
npm run backend:test
npm run backend:security
npm run build
```

## Testes

Execute:

```bash
mvn test
```

Os testes usam Testcontainers com PostgreSQL 16 e Flyway habilitado. Isso valida as migrations no mesmo tipo de banco
usado pela aplicacao local e pelo Docker Compose.

## Analise de Vulnerabilidades

O projeto usa OWASP Dependency-Check para analisar vulnerabilidades conhecidas nas dependencias Maven.

Execute:

```bash
mvn dependency-check:check
```

Os relatorios sao gerados em:

```text
target/dependency-check
```

A documentacao da analise fica em:

```text
docs/security/vulnerability-analysis.md
```

## Migrations

As migrations ficam em:

```text
src/main/resources/db/migration
```

Ao iniciar a aplicacao, o Flyway executa automaticamente as migrations pendentes.

Para aplicar migrations localmente, suba o PostgreSQL e inicie a aplicacao:

```bash
docker compose up -d postgres
mvn spring-boot:run
```

Com Docker Compose completo, as migrations tambem rodam no startup do container da aplicacao:

```bash
docker compose up --build
```

## Usuario Admin Local

Seed inicial criado por Flyway:

```text
Usuario: admin@autocarehub.com
Senha: autocare123
Perfil: ADMIN
```

Login:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin@autocarehub.com\",\"password\":\"autocare123\"}"
```

Use o campo `accessToken` retornado como Bearer token nas demais chamadas.

## Dados Demonstrativos

O Flyway tambem carrega uma massa de dados demonstrativa para uso no frontend:

```text
Admin Master: master@autocarehub.com / autocare123
Admin de oficina: oficina.admin@autocarehub.com / autocare123
Admin de loja de peças: loja.admin@autocarehub.com / autocare123
Funcionario de oficina: oficina.funcionario@autocarehub.com / autocare123
Funcionario de loja de peças: loja.funcionario@autocarehub.com / autocare123
Cliente: cliente@autocarehub.com / autocare123
```

A base demo inclui clientes, clientes com mais de um veiculo, frota empresarial, peças em estoque,
peças abaixo do minimo, serviços da oficina e ordens de servico com historico de uso em diferentes status.

## Principais Endpoints

Autenticacao:

- `POST /api/v1/auth/login`

Clientes:

- `GET /api/v1/customers`
- `POST /api/v1/customers`
- `GET /api/v1/customers/{customerId}`
- `PUT /api/v1/customers/{customerId}`
- `DELETE /api/v1/customers/{customerId}`

Veiculos:

- `GET /api/v1/vehicles`
- `POST /api/v1/vehicles`
- `GET /api/v1/vehicles/{vehicleId}`
- `PUT /api/v1/vehicles/{vehicleId}`
- `DELETE /api/v1/vehicles/{vehicleId}`
- `GET /api/v1/customers/{customerId}/vehicles`

serviços da oficina:

- `GET /api/v1/workshop-services`
- `POST /api/v1/workshop-services`
- `GET /api/v1/workshop-services/{serviceId}`
- `PUT /api/v1/workshop-services/{serviceId}`
- `DELETE /api/v1/workshop-services/{serviceId}`

Peças:

- `GET /api/v1/parts`
- `POST /api/v1/parts`
- `GET /api/v1/parts/{partId}`
- `PUT /api/v1/parts/{partId}`
- `DELETE /api/v1/parts/{partId}`
- `PATCH /api/v1/parts/{partId}/stock`
- `PATCH /api/v1/parts/{partId}/stock-movement`
- `PATCH /api/v1/parts/{partId}/reservation`
- `PATCH /api/v1/parts/{partId}/reserve`
- `PATCH /api/v1/parts/{partId}/release-reservation`
- `PATCH /api/v1/parts/{partId}/commit-reservation`

Ordens de servico:

- `GET /api/v1/service-orders`
- `POST /api/v1/service-orders`
- `GET /api/v1/service-orders/{serviceOrderId}`
- `POST /api/v1/service-orders/{serviceOrderId}/services`
- `POST /api/v1/service-orders/{serviceOrderId}/parts`
- `POST /api/v1/service-orders/{serviceOrderId}/budget/generate`
- `POST /api/v1/service-orders/{serviceOrderId}/budget/approve`
- `PATCH /api/v1/service-orders/{serviceOrderId}/status`
- `GET /api/v1/customers/{customerId}/service-orders`

## Regra de Estoque e Orcamento

Peças podem ser cadastradas, atualizadas, movimentadas por entrada/saida/venda e reservadas para orcamentos. A reserva
reduz a quantidade disponivel, mas nao reduz o estoque total. Quando o orcamento e aprovado ou a reserva e confirmada,
a quantidade reservada e baixada definitivamente do estoque. Se a reserva for liberada, a quantidade volta a ficar
disponivel.

## OpenAPI First

O contrato da API e definido antes da implementacao em `docs/openapi/openapi.yaml`. A partir desse contrato, o OpenAPI
Generator cria interfaces Java e DTOs em `target/generated-sources/openapi`.

Os controllers manuais implementam as interfaces geradas e convertem os DTOs gerados para commands e queries da camada
de application. Isso reduz divergencia entre documentacao e implementacao, facilita validacao do contrato e torna o
Swagger uma representacao direta da API publica.

## DDD

O projeto usa conceitos de Domain-Driven Design para isolar o conhecimento de negocio no dominio. Entidades
como `Customer`, `Vehicle`, `Part`, `WorkshopService` e `ServiceOrder` concentram regras e invariantes. A camada de
application coordena casos de uso e depende de portas de repositorio, sem conhecer detalhes de JPA ou HTTP.

Essa abordagem mantem controllers, DTOs gerados, repositories Spring Data e entidades JPA fora do nucleo de negocio.

## Justificativa do PostgreSQL

PostgreSQL foi escolhido por ser um banco relacional robusto, amplamente usado em ambientes produtivos e adequado para o
dominio da aplicacao. O sistema possui relacionamentos claros entre clientes, veiculos, ordens de servico, serviços e
peças, alem de necessidade de consistencia transacional para operacoes como composicao de orcamento e baixa de estoque.

O uso de PostgreSQL tambem combina bem com Flyway, JPA e execucao via Docker Compose.

## Justificativa do Monolito em Camadas

Para o escopo academico e para um MVP, um monolito em camadas oferece menor complexidade operacional, deploy simples e
boa separacao interna de responsabilidades. A aplicacao ainda mantem fronteiras claras entre dominio, casos de uso,
infraestrutura e interface REST, permitindo evolucao futura sem introduzir a complexidade de microsservicos antes de
haver necessidade real.

Essa escolha favorece clareza arquitetural, testabilidade e entrega incremental.
