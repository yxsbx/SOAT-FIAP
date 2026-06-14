# AutoCare Hub

MVP academico do Tech Challenge FIAP para gestao de oficina mecanica. O projeto principal e uma API REST em backend monolitico, com arquitetura em camadas, DDD, JWT, OpenAPI/Swagger, Flyway, Docker, testes automatizados e relatorio de vulnerabilidades.

Existe um frontend Vue 3 no repositorio para demonstracao, mas a entrega principal e o backend.

## Visao Geral

O AutoCare Hub cobre o ciclo basico de atendimento de uma oficina:

- cadastro de clientes com validacao de CPF/CNPJ;
- cadastro de veiculos com validacao de placa brasileira antiga e Mercosul;
- cadastro de servicos da oficina;
- cadastro de pecas e insumos;
- controle de estoque com entrada, saida, reserva, liberacao e baixa;
- criacao de Ordem de Servico com cliente, veiculo, servicos e pecas;
- geracao e aprovacao de orcamento;
- acompanhamento da OS pelo cliente;
- administracao protegida por JWT.

## Documentacao

| Documento | Finalidade |
| --- | --- |
| `docs/DDD_DOCUMENTATION.md` | DDD, linguagem ubiqua, entidades, value objects, agregados, bounded contexts e regras. |
| `docs/EVENT_STORMING.md` | Event Storming dos fluxos de OS e estoque. |
| `docs/SECURITY_SCAN_GUIDE.md` | Como executar scans de seguranca. |
| `docs/SECURITY_REPORT.md` | Resultado real dos scans executados e analise dos achados. |
| `docs/DELIVERY_DOCUMENT.md` | Documento final para gerar PDF de entrega academica. |
| `docs/openapi/openapi.yaml` | Contrato OpenAPI versionado. |
| `frontend/README.md` | Execucao do frontend demonstrativo. |

## Stack

- Java 21
- Spring Boot 4.1
- Maven
- PostgreSQL 16
- Flyway
- Spring Security + JWT
- OpenAPI Generator + Springdoc Swagger UI
- JaCoCo
- Testcontainers
- OWASP Dependency-Check
- Docker e Docker Compose
- Vue 3 + Vite no frontend demonstrativo

## Arquitetura

O backend continua sendo um monolito em camadas:

```text
src/main/java/br/com/autocarehub
├── domain
│   ├── model
│   ├── valueobject
│   ├── enums
│   ├── exception
│   ├── service
│   └── policy
├── application
│   ├── usecase
│   ├── service
│   ├── dto
│   └── port
│       ├── in
│       └── out
├── infrastructure
│   ├── persistence
│   ├── security
│   └── config
└── interfaces
    └── rest
```

Regras de negocio ficam no dominio. Use cases coordenam fluxos na camada de aplicacao. Persistencia, seguranca e configuracao ficam em infraestrutura. Controllers REST ficam em interfaces e nao acessam repositories diretamente.

## Banco de Dados

O banco principal e PostgreSQL. As migrations Flyway ficam em:

```text
src/main/resources/db/migration
```

O `docker-compose.yml` sobe PostgreSQL e API. Para testes de integracao, o projeto usa Testcontainers quando aplicavel.

## Execucao Local com Docker

Pre-requisitos:

- Docker
- Docker Compose

Crie o arquivo local de variaveis:

```bash
cp .env.example .env
```

Preencha pelo menos:

```text
POSTGRES_PASSWORD=[PREENCHER - SENHA_LOCAL_DO_POSTGRES]
JWT_SECRET=[PREENCHER - SEGREDO_LOCAL_COM_PELO_MENOS_32_BYTES]
```

Subir ambiente completo:

```bash
docker compose up --build
```

Subir em background:

```bash
docker compose up -d --build
```

Parar:

```bash
docker compose down
```

Remover volumes locais:

```bash
docker compose down -v
```

URLs locais:

```text
API: http://localhost:8080
Swagger: http://localhost:8080/swagger-ui.html
PostgreSQL: localhost:5432
```

## Execucao Local sem Docker para a API

Pre-requisitos:

- Java 21
- Maven 3.9+
- PostgreSQL 16 local ou via Compose

Subir somente o banco pelo Compose:

```bash
docker compose up -d postgres
```

Executar a API:

```bash
mvn spring-boot:run
```

Variaveis principais:

```text
DB_URL=jdbc:postgresql://localhost:5432/autocarehub
DB_USERNAME=autocarehub
DB_PASSWORD=replace-with-local-postgres-password
JWT_SECRET=replace-with-local-jwt-secret-at-least-32-bytes
JWT_EXPIRATION_MINUTES=60
APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,http://127.0.0.1:5173
```

## Testes e Cobertura

Executar testes:

```bash
mvn test
```

Executar testes, gerar relatorio e validar cobertura:

```bash
mvn verify
```

Relatorio JaCoCo:

```text
target/site/jacoco/index.html
```

A regra atual exige cobertura minima de 95% para instrucoes e linhas no escopo de negocio medido pelo JaCoCo (`domain` e `application`). A ultima validacao local registrou 108 testes, 95,36% de instrucoes e 97,35% de linhas cobertas.

Essa meta atende e supera o criterio academico de cobertura minima de 80%.

## Swagger/OpenAPI

Swagger UI local:

```text
http://localhost:8080/swagger-ui.html
```

Contrato versionado:

```text
docs/openapi/openapi.yaml
```

Para autenticar no Swagger:

1. Execute `POST /api/v1/auth/login`.
2. Copie o token retornado.
3. Clique em `Authorize`.
4. Informe `Bearer <token>`.

No MVP academico, Swagger e OpenAPI ficam publicos para facilitar avaliacao local. Em ambiente produtivo, desative com:

```text
SPRINGDOC_API_DOCS_ENABLED=false
SPRINGDOC_SWAGGER_UI_ENABLED=false
```

## Usuarios Demo

Os usuarios demo sao carregados pela migration inicial. A senha nao deve ser escrita no repositorio; use o valor local configurado para demonstracao.

```text
admin@autocarehub.com              ADMIN tecnico inicial
master@autocarehub.com             Admin master da plataforma
oficina.admin@autocarehub.com      Admin de oficina
loja.admin@autocarehub.com         Admin de loja de pecas
oficina.funcionario@autocarehub.com Funcionario de oficina
loja.funcionario@autocarehub.com   Funcionario de loja de pecas
cliente@autocarehub.com            Cliente demo
```

Exemplo de login:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin@autocarehub.com\",\"password\":\"$SENHA_DEMO_LOCAL\"}"
```

## Endpoints Principais

Autenticacao:

- `POST /api/v1/auth/login`

Clientes:

- `GET /api/v1/customers`
- `POST /api/v1/customers`
- `GET /api/v1/customers/{customerId}`
- `PUT /api/v1/customers/{customerId}`
- `DELETE /api/v1/customers/{customerId}`
- `GET /api/v1/customers/{customerId}/vehicles`
- `GET /api/v1/customers/{customerId}/service-orders`

Veiculos:

- `GET /api/v1/vehicles`
- `POST /api/v1/vehicles`
- `GET /api/v1/vehicles/{vehicleId}`
- `PUT /api/v1/vehicles/{vehicleId}`
- `DELETE /api/v1/vehicles/{vehicleId}`

Servicos:

- `GET /api/v1/workshop-services`
- `POST /api/v1/workshop-services`
- `GET /api/v1/workshop-services/{serviceId}`
- `PUT /api/v1/workshop-services/{serviceId}`
- `DELETE /api/v1/workshop-services/{serviceId}`

Pecas e estoque:

- `GET /api/v1/parts`
- `POST /api/v1/parts`
- `GET /api/v1/parts/{partId}`
- `PUT /api/v1/parts/{partId}`
- `DELETE /api/v1/parts/{partId}`
- `PATCH /api/v1/parts/{partId}/stock`
- `POST /api/v1/parts/{partId}/stock-movement`
- `PATCH /api/v1/parts/{partId}/reservation`
- `POST /api/v1/parts/{partId}/reserve`
- `POST /api/v1/parts/{partId}/release-reservation`
- `POST /api/v1/parts/{partId}/commit-reservation`

Ordens de Servico:

- `GET /api/v1/service-orders`
- `POST /api/v1/service-orders`
- `GET /api/v1/service-orders/{serviceOrderId}`
- `POST /api/v1/service-orders/{serviceOrderId}/services`
- `POST /api/v1/service-orders/{serviceOrderId}/parts`
- `POST /api/v1/service-orders/{serviceOrderId}/budget/generate`
- `POST /api/v1/service-orders/{serviceOrderId}/budget/approve`
- `PATCH /api/v1/service-orders/{serviceOrderId}/status`
- `GET /api/v1/service-orders/tracking`
- `GET /api/v1/service-orders/metrics/average-execution-time`

Usuarios:

- `GET /api/v1/users/me`
- `PUT /api/v1/users/me`
- `PATCH /api/v1/users/me/password`
- `GET /api/v1/users/me/preferences/home`
- `PUT /api/v1/users/me/preferences/home`
- `GET /api/v1/users`
- `POST /api/v1/users`
- `GET /api/v1/users/partners`
- `PUT /api/v1/users/{userId}`
- `PATCH /api/v1/users/{userId}/password`

Demo leads:

- `GET /api/v1/demo-leads`
- `POST /api/v1/demo-leads`

## Seguranca

- JWT assinado com segredo vindo de variavel de ambiente.
- APIs administrativas protegidas por Spring Security.
- Senhas persistidas com BCrypt.
- CPF/CNPJ e placa validados por value objects.
- DTOs separados das entidades de persistencia.
- CORS configuravel por ambiente.
- Swagger desabilitavel por variavel de ambiente.

Guia de scans:

```text
docs/SECURITY_SCAN_GUIDE.md
```

Relatorio de vulnerabilidades:

```text
docs/SECURITY_REPORT.md
```

Executar Dependency-Check:

```bash
mvn dependency-check:check
```

## Frontend Demonstrativo

O frontend fica em `frontend/`.

```bash
cd frontend
npm install
npm run dev
```

URL padrao do Vite:

```text
http://localhost:5173
```

## Decisoes Tecnicas

- Monolito em camadas para manter simplicidade operacional no MVP academico.
- DDD aplicado no dominio e nos use cases, sem separar em microsservicos.
- PostgreSQL para consistencia relacional entre clientes, veiculos, ordens, pecas e estoque.
- Flyway para versionamento de schema.
- OpenAPI First para manter contrato REST e Swagger alinhados.
- JWT stateless para proteger APIs administrativas.
- Docker Compose para ambiente local reproduzivel.

## Limitacoes Conhecidas

- Nao ha pagamento online.
- Nao ha envio real de e-mail, SMS ou WhatsApp.
- O historico de status da OS e simplificado para o escopo do MVP.
- O controle de parceiros e lojas permanece no mesmo monolito.
- O Swagger fica publico no ambiente local academico.
