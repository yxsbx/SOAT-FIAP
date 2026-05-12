# Documento de Entrega Academica

## Identificacao

Nome do aluno:

```text
[NOME_DO_ALUNO]
```

Dados academicos:

| Nome | RM | Discord |
| --- | --- | --- |
| [NOME_DO_ALUNO] | [RM_DO_ALUNO] | [DISCORD_DO_ALUNO] |

## Links

Documentacao DDD:

```text
[LINK_DA_DOCUMENTACAO_DDD]
```

Repositorio privado:

```text
[LINK_DO_REPOSITORIO_PRIVADO]
```

Video de apresentacao:

```text
[LINK_DO_VIDEO]
```

Relatorio de vulnerabilidades:

```text
[LINK_OU_CAMINHO_DO_RELATORIO_DE_VULNERABILIDADES]
```

## Resumo do Projeto

O AutoCare Hub API e um MVP academico para gerenciamento de uma oficina mecanica. A aplicacao oferece uma API REST para cadastro e consulta de clientes, veiculos, servicos, pecas, estoque e ordens de servico.

O fluxo principal do MVP permite registrar uma ordem de servico, associar servicos e pecas, gerar um orcamento, aprovar o orcamento e acompanhar a evolucao do status ate a entrega.

O projeto tambem demonstra praticas de arquitetura em camadas, DDD, contrato OpenAPI First, autenticacao com JWT, persistencia relacional, migrations com Flyway, testes automatizados, Docker e analise de vulnerabilidades.

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
- JWT
- Docker e Docker Compose
- JUnit 5
- Mockito
- H2 para testes automatizados
- OWASP Dependency-Check

## Arquitetura

O projeto foi implementado como um monolito em camadas:

- `domain`: regras de negocio, entidades e objetos de valor.
- `application`: casos de uso, comandos, entradas, saidas e portas.
- `infrastructure`: persistencia JPA, configuracoes, seguranca, repositories e adapters.
- `interfaces`: controllers REST manuais e mappers para DTOs gerados pelo OpenAPI.
- `docs`: documentacao OpenAPI, DDD, seguranca e materiais de entrega.

A separacao de camadas evita que controllers acessem diretamente repositories ou exponham entidades JPA. Os controllers recebem DTOs da interface REST, convertem para comandos ou entradas da application layer, chamam os casos de uso e retornam DTOs de resposta.

## Documentacao DDD

A documentacao DDD esta organizada em:

- `docs/ddd/01-product-context.md`
- `docs/ddd/02-ubiquitous-language.md`
- `docs/ddd/03-event-storming.md`
- `docs/ddd/04-bounded-contexts.md`
- `docs/ddd/05-aggregates-and-business-rules.md`
- `docs/ddd/06-context-map.md`
- `docs/ddd/07-future-evolution.md`

Ela descreve o contexto do produto, linguagem ubiqua, event storming, bounded contexts, agregados, regras de negocio, mapa de contexto e evolucoes futuras.

## Relatorio de Vulnerabilidades

Ferramenta utilizada:

```text
OWASP Dependency-Check
```

Comando para executar:

```bash
mvn dependency-check:check
```

Caminho da documentacao:

```text
docs/security/vulnerability-analysis.md
```

Caminho dos relatorios gerados:

```text
target/dependency-check
```

Resumo da analise:

```text
[PREENCHER_APOS_EXECUTAR_O_SCAN]
```

Vulnerabilidades encontradas:

```text
[PREENCHER_APOS_EXECUTAR_O_SCAN]
```

Plano de correcao:

```text
[PREENCHER_APOS_EXECUTAR_O_SCAN]
```

## Instrucoes de Execucao

### Pre-requisitos

- Java 21
- Maven 3.9+
- Docker e Docker Compose

### Executar localmente com PostgreSQL via Docker

Subir o banco:

```bash
docker compose up -d postgres
```

Executar a aplicacao:

```bash
mvn spring-boot:run
```

A API fica disponivel em:

```text
http://localhost:8080
```

### Executar com Docker Compose

```bash
docker compose up --build
```

Para parar:

```bash
docker compose down
```

### Acessar Swagger

```text
http://localhost:8080/swagger-ui.html
```

### Executar testes

```bash
mvn test
```

### Executar analise de vulnerabilidades

```bash
mvn dependency-check:check
```

### Credenciais locais de desenvolvimento

```text
Usuario: admin@autocarehub.com
Senha: autocare123
Perfil: ADMIN
```
