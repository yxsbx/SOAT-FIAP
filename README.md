# AutoCare Hub

MVP backend monolitico para gestao de uma oficina mecanica, desenvolvido para o Tech Challenge FIAP. A API cobre
clientes, veiculos, servicos, pecas e insumos, estoque, ordens de servico, orçamentos, aprovacao pelo cliente,
acompanhamento da OS e APIs administrativas protegidas por JWT.

## Problema

Oficinas pequenas e medias costumam controlar atendimento, orçamentos, estoque e andamento de servicos em ferramentas
separadas ou planilhas. Isso dificulta rastreabilidade da ordem de servico, controle de pecas usadas, comunicacao com o
cliente e gestao do faturamento operacional.

## Objetivo

Entregar um MVP backend RESTful que centraliza o fluxo essencial de uma oficina:

- cadastrar clientes e veiculos;
- cadastrar servicos, pecas e insumos;
- criar ordens de servico;
- gerar orçamentos a partir de servicos e pecas;
- permitir aprovacao do orçamento;
- acompanhar o status da OS;
- controlar estoque com entradas, saidas, reservas e baixas;
- proteger APIs administrativas com JWT;
- documentar contrato via Swagger/OpenAPI;
- executar localmente com Docker Compose.

## Escopo do MVP

O MVP foca no backend monolitico. Existe um frontend Vue 3 no repositorio para demonstracao, mas a entrega principal do
Tech Challenge e a API.

Incluido:

- Auth/Login com JWT.
- CRUD de clientes.
- CRUD de veiculos.
- CRUD de servicos da oficina.
- CRUD de pecas e insumos.
- Controle de estoque.
- Criacao e acompanhamento de ordens de servico.
- Geracao e aprovacao de orçamento.
- Consulta de OS pelo cliente.
- Validacao de CPF/CNPJ e placa.
- Swagger/OpenAPI.
- Testes unitarios e de integracao.
- Dockerfile e docker-compose.
- Documentacao DDD e Event Storming.

Fora do escopo do MVP:

- Pagamento online.
- Envio real de e-mail, SMS ou WhatsApp.
- Integracao com catalogos externos de pecas.
- Multi-tenant completo por oficina.

## Tecnologias

- Java 21
- Spring Boot 3.5
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL 16
- Flyway
- Maven
- OpenAPI Generator
- Springdoc Swagger UI
- JJWT
- Docker e Docker Compose
- JUnit 5
- Testcontainers
- JaCoCo
- OWASP Dependency-Check

## Banco de Dados

O PostgreSQL foi escolhido por ser relacional, robusto e adequado ao dominio. O sistema possui relacoes fortes entre
clientes, veiculos, ordens de servico, servicos, pecas e movimentacoes de estoque. Tambem ha necessidade de consistencia
transacional em operacoes como geracao de orçamento, reserva de pecas e baixa de estoque.

PostgreSQL tambem se integra bem com Flyway, JPA, Testcontainers e Docker Compose.

## Arquitetura

O backend usa monolito em camadas com conceitos de DDD:

```text
src/main/java/br/com/autocarehub
├── domain
│   ├── model          Entidades, agregados e modelos do dominio
│   ├── valueobject    Objetos de valor como Document, Plate, Money e Address
│   ├── enums          Enumeracoes do dominio
│   ├── exception      Excecoes de dominio
│   ├── service        Validacoes/servicos de dominio
│   └── policy         Politicas de negocio
├── application
│   ├── usecase        Casos de uso por contexto funcional
│   ├── service        Reservado para servicos de aplicacao
│   ├── dto            Reservado para DTOs de aplicacao
│   └── port
│       ├── in         Reservado para portas de entrada
│       └── out        Portas de saida, incluindo repositorios
├── infrastructure
│   ├── persistence
│   │   ├── entity     Entidades JPA
│   │   ├── repository Repositorios Spring Data e adapters das portas
│   │   └── mapper     Mappers JPA/dominio
│   ├── security       JWT, autorizacao e configuracao de seguranca
│   └── config         Beans e compatibilidade de infraestrutura
└── interfaces
    └── rest
        ├── controller Controllers REST
        ├── request    Reservado para requests manuais
        ├── response   Reservado para responses manuais
        ├── mapper     Mappers REST/dominio
        └── exception  Tratamento padronizado de erros REST
```

Outras pastas importantes:

```text
docs/openapi/openapi.yaml              Contrato REST OpenAPI
docs/ddd/DDD_DOCUMENTATION.md          Documentacao DDD consolidada
docs/ddd/EVENT_STORMING.md             Event Storming consolidado
docs/security/SECURITY_SCAN_GUIDE.md   Guia de scans de seguranca
docs/security/SECURITY_REPORT.md       Relatorio de seguranca
docs/delivery/DELIVERY_DOCUMENT.md     Documento de entrega FIAP
src/main/resources/db/migration        Migrations Flyway
src/test/java                          Testes unitarios e integracao
frontend/src/pages                     Paginas do frontend Vue 3 demonstrativo
frontend/src/styles/main.css           Estilos globais do frontend
```

## DDD Aplicado

Entidades principais:

- `Customer`: cliente da oficina.
- `Vehicle`: veiculo vinculado ao cliente.
- `WorkshopService`: servico oferecido pela oficina.
- `Part`: peca ou insumo em estoque.
- `ServiceOrder`: ordem de servico.
- `Budget` e `BudgetItem`: composicao financeira do orçamento.
- `StockMovement`: movimentacao de estoque.

Value Objects:

- `Document`: CPF/CNPJ normalizado e validado.
- `Plate`: placa brasileira antiga ou Mercosul normalizada.
- `Money`: valores monetarios nao negativos.
- `Address`: endereco normalizado.

Agregados:

- `ServiceOrder` agrega servicos e pecas solicitadas.
- `Part` controla estoque disponivel, reservado e minimo.
- `Customer` e `Vehicle` mantem identidade e vinculo do atendimento.

Servicos e politicas de dominio:

- `PlatformFeePolicy`: calcula taxa por tiers de faturamento.
- Regras internas de `ServiceOrder`: transicoes de status e invariantes.
- Regras internas de `Part`: impedir estoque negativo, reservar, liberar e baixar.

## Linguagem Ubiqua

- Cliente: pessoa ou empresa atendida.
- Veiculo: bem do cliente atendido pela oficina.
- Ordem de Servico: registro operacional do atendimento.
- Diagnostico: etapa inicial de verificacao.
- Orçamento: calculo de servicos e pecas a aprovar.
- Aprovacao: aceite do cliente para execucao.
- Execucao: realizacao do servico.
- Finalizacao: servico concluido.
- Entrega: veiculo entregue ao cliente.
- Peca/Insumo: item de estoque usado ou vendido.
- Reserva: bloqueio temporario de peca para orçamento.
- Baixa: reducao definitiva do estoque.

## Fluxos Principais

### Criacao da OS

1. Identificar cliente por CPF/CNPJ.
2. Cadastrar cliente se nao existir.
3. Vincular ou cadastrar veiculo por placa.
4. Incluir servicos solicitados.
5. Incluir pecas e insumos, se houver.
6. Criar OS com status inicial `RECEBIDA` ou gerar orçamento automaticamente.

### Geracao de Orçamento

1. Somar servicos solicitados.
2. Somar pecas e insumos.
3. Calcular total geral.
4. Reservar pecas no estoque quando aplicavel.
5. Alterar OS para `AGUARDANDO_APROVACAO`.

### Aprovacao do Orçamento

1. Cliente ou usuario autorizado aprova o orçamento.
2. OS registra data de aprovacao.
3. Pecas reservadas sao baixadas do estoque.
4. OS fica liberada para `EM_EXECUCAO`.

### Acompanhamento da OS

O cliente pode consultar o progresso por endpoint protegido/autorizado, recebendo status, veiculo, servicos, pecas,
orçamento e historico basico.

Status:

- `RECEBIDA`
- `EM_DIAGNOSTICO`
- `AGUARDANDO_APROVACAO`
- `EM_EXECUCAO`
- `FINALIZADA`
- `ENTREGUE`

### Gestao de Estoque

- Entrada aumenta quantidade.
- Saida reduz quantidade.
- Reserva bloqueia peca para orçamento.
- Liberacao remove bloqueio.
- Confirmacao baixa estoque.
- Estoque negativo e baixa acima do disponivel sao bloqueados.

## Seguranca

- Login via `POST /api/v1/auth/login`.
- JWT assinado com segredo configurado por variavel de ambiente.
- APIs administrativas exigem Bearer Token.
- Roles e permissoes restringem acessos.
- Senhas usam BCrypt.
- CPF/CNPJ e placa sao normalizados antes de persistir.
- Requests usam DTOs explicitos para reduzir risco de mass assignment.
- Campos de entrada possuem limites de tamanho nos fluxos administrativos.
- Jackson rejeita campos desconhecidos.
- Respostas de erro sao padronizadas.
- CORS e exposicao do Swagger sao configuraveis por variaveis de ambiente.
- Dados sensiveis nao devem ser logados.
- `APP_CORS_ALLOWED_ORIGINS` deve listar origens explicitas. O backend rejeita `*` e `null`.
- Em listagens, documentos de clientes podem ser mascarados; consulte o detalhe individual apenas quando houver necessidade operacional.

## Execucao Local com Docker

Pre-requisitos:

- Docker
- Docker Compose

Crie um `.env` local a partir do exemplo:

```bash
cp .env.example .env
```

Edite o `.env` antes de subir o ambiente. O `docker-compose.yml` exige `POSTGRES_PASSWORD` e `JWT_SECRET` definidos
localmente e nao possui fallback com segredo fixo. Nao versionar `.env`. O `JWT_SECRET` deve ter pelo menos 32 bytes e
nao deve ser reutilizado entre ambientes.

Suba API e banco:

```bash
docker compose up --build
```

Em background:

```bash
docker compose up -d --build
```

Parar:

```bash
docker compose down
```

Parar e remover volume do banco:

```bash
docker compose down -v
```

URLs:

```text
API: http://localhost:8080
Swagger: http://localhost:8080/swagger-ui.html
PostgreSQL: localhost:5432
```

## Execucao Local sem Docker

Pre-requisitos:

- Java 21
- Maven 3.9+
- PostgreSQL 16 local ou via compose

Subir somente o banco:

```bash
docker compose up -d postgres
```

Executar a API:

```bash
mvn spring-boot:run
```

Variaveis usadas pela aplicacao:

```text
DB_URL=jdbc:postgresql://localhost:5432/autocarehub
DB_USERNAME=autocarehub
DB_PASSWORD=replace-with-local-postgres-password
JWT_SECRET=replace-with-local-jwt-secret-at-least-32-bytes
JWT_EXPIRATION_MINUTES=60
SERVER_PORT=8080
APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,http://127.0.0.1:5173
SPRINGDOC_API_DOCS_ENABLED=true
SPRINGDOC_SWAGGER_UI_ENABLED=true
```

Nao use `APP_CORS_ALLOWED_ORIGINS=*`. A aplicacao rejeita wildcard para evitar CORS permissivo em ambientes de scan,
homologacao ou producao.

## Testes e Cobertura

Rodar testes:

```bash
mvn test
```

Rodar testes e checagem de cobertura:

```bash
mvn verify
```

Relatorio JaCoCo:

```text
target/site/jacoco/index.html
```

O projeto possui regra JaCoCo para cobertura minima de 80% no pacote de dominio. Os testes de integracao usam
Testcontainers com PostgreSQL, portanto o Docker precisa estar ativo.

## Swagger/OpenAPI

Swagger UI local:

```text
http://localhost:8080/swagger-ui.html
```

Contrato:

```text
docs/openapi/openapi.yaml
```

Para autenticar no Swagger:

1. Execute `POST /api/v1/auth/login`.
2. Copie o campo `accessToken`.
3. Clique em `Authorize`.
4. Informe `Bearer <accessToken>`.

No MVP academico, Swagger e OpenAPI ficam publicos para facilitar avaliacao. Em producao, desative por ambiente com:

```text
SPRINGDOC_API_DOCS_ENABLED=false
SPRINGDOC_SWAGGER_UI_ENABLED=false
```

Tambem recomenda-se restringir `/swagger-ui/**`, `/swagger-ui.html`, `/v3/api-docs/**` e `/openapi.yaml` por rede ou
perfil administrativo quando houver ambiente produtivo. Nunca publique Swagger produtivo com credenciais reais nos
exemplos.

## Usuarios de Teste

Senha padrao dos seeds academicos locais:

```text
autocare123
```

Essa senha existe apenas para demonstracao do MVP. Nao reutilize em producao e redefina as senhas apos carregar dados
reais. No frontend, o preenchimento automatico da senha demo deve ser configurado via `VITE_DEMO_PASSWORD` no `.env`
local, nao no codigo versionado.

Contas:

```text
admin@autocarehub.com              ADMIN tecnico inicial
master@autocarehub.com             Admin Master
oficina.admin@autocarehub.com      Admin de oficina
loja.admin@autocarehub.com         Admin de loja de pecas
oficina.funcionario@autocarehub.com Funcionario de oficina
loja.funcionario@autocarehub.com   Funcionario de loja de pecas
cliente@autocarehub.com            Cliente final
```

Login via curl:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin@autocarehub.com\",\"password\":\"$SENHA_DEMO_LOCAL\"}"
```

## Endpoints Principais

Auth:

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
- `PATCH /api/v1/parts/{partId}/stock-movement`
- `PATCH /api/v1/parts/{partId}/reservation`
- `PATCH /api/v1/parts/{partId}/reserve`
- `PATCH /api/v1/parts/{partId}/release-reservation`
- `PATCH /api/v1/parts/{partId}/commit-reservation`

Ordens de servico e orçamentos:

- `GET /api/v1/service-orders`
- `POST /api/v1/service-orders`
- `GET /api/v1/service-orders/{serviceOrderId}`
- `POST /api/v1/service-orders/{serviceOrderId}/services`
- `POST /api/v1/service-orders/{serviceOrderId}/parts`
- `POST /api/v1/service-orders/{serviceOrderId}/budget/generate`
- `POST /api/v1/service-orders/{serviceOrderId}/budget/approve`
- `PATCH /api/v1/service-orders/{serviceOrderId}/status`
- `GET /api/v1/service-orders/tracking`
- `GET /api/v1/customers/{customerId}/service-orders`
- `GET /api/v1/service-orders/metrics/average-execution-time`

Usuarios e interessados:

- `GET /api/v1/users/me`
- `PUT /api/v1/users/me`
- `PATCH /api/v1/users/me/password`
- `GET /api/v1/users/me/preferences/home`
- `PUT /api/v1/users/me/preferences/home`
- `GET /api/v1/users`
- `POST /api/v1/users`
- `PUT /api/v1/users/{userId}`
- `PATCH /api/v1/users/{userId}/password`
- `POST /api/v1/demo-leads`
- `GET /api/v1/demo-leads`

## Scan de Vulnerabilidades

Executar:

```bash
mvn dependency-check:check
```

Relatorios:

```text
target/dependency-check
```

Documento de apoio:

```text
docs/security/SECURITY_REPORT.md
```

O arquivo `docs/security/SECURITY_REPORT.md` registra a execucao dos scans. Novos achados devem ser atualizados apenas
com evidencia gerada pela ferramenta.

## Decisoes Tecnicas

- Monolito em camadas para reduzir complexidade operacional no MVP.
- DDD no dominio para concentrar regras e linguagem de negocio.
- OpenAPI First para alinhar contrato, DTOs e Swagger.
- PostgreSQL para consistencia relacional e transacional.
- Flyway para versionar schema e seeds.
- Testcontainers para validar integracao com banco real.
- JWT stateless para proteger APIs administrativas.
- Docker Compose para ambiente local reproduzivel.

## Limitacoes Conhecidas

- O envio de orçamento ao cliente e representado pela API, sem integracao real de mensageria.
- O historico de status da OS e derivado de timestamps simples.
- O controle de parceiros e lojas foi mantido no mesmo monolito para fins demonstrativos.
- O Swagger fica publico no perfil local academico.
- A massa demo e fixa e voltada para avaliacao.

## Melhorias Futuras

- Auditoria detalhada de alteracoes por usuario.
- Eventos de dominio persistidos em outbox.
- Integracao real com e-mail/WhatsApp.
- Multi-tenant por oficina/parceiro.
- Controle fiscal e pagamentos.
- Observabilidade com metricas, tracing e dashboards.
- Politicas de acesso mais granulares por permissao.
