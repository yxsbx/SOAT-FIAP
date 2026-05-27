# Documento Final de Entrega - Tech Challenge

## 1. Nome do Grupo

```text
[PREENCHER - NOME_DO_GRUPO]
```

## 2. Participantes

| Nome        | RM          | Papel no projeto |
|-------------|-------------|------------------|
| [PREENCHER] | [PREENCHER] | [PREENCHER]      |

## 3. Usernames no Discord

| Participante | Discord     |
|--------------|-------------|
| [PREENCHER]  | [PREENCHER] |

## 4. Nome do Projeto

AutoCare Hub

## 5. Descricao Resumida

O AutoCare Hub e um MVP backend monolitico para gestao de uma oficina mecanica. A API centraliza cadastro de clientes,
veiculos, servicos, pecas e insumos, controle de estoque, criacao de ordens de servico, geracao e aprovacao de
orçamentos e acompanhamento da OS pelo cliente.

## 6. Link da Documentacao DDD

```text
[PREENCHER - link para README/PDF/Miro da documentacao DDD]
```

Arquivo base no repositorio:

```text
docs/ddd/README.md
```

## 7. Link do Repositorio Privado

```text
[PREENCHER - URL_DO_REPOSITORIO_PRIVADO]
```

## 8. Link ou Instrucao de Acesso ao Swagger

Com a aplicacao em execucao local:

```text
http://localhost:8080/swagger-ui.html
```

Contrato OpenAPI:

```text
docs/openapi/openapi.yaml
```

## 9. Como Executar o Projeto

### Com Docker Compose

```bash
cp .env.example .env
docker compose up --build
```

Antes de executar, editar `.env` e definir `POSTGRES_PASSWORD` e `JWT_SECRET` para valores locais.

### Sem Docker para a aplicacao

```bash
docker compose up -d postgres
mvn spring-boot:run
```

Pre-requisitos:

- Java 21
- Maven 3.9+
- Docker e Docker Compose para o PostgreSQL local ou testes com Testcontainers

## 10. Como Rodar os Testes

```bash
mvn test
```

Para validar tambem cobertura:

```bash
mvn verify
```

Relatorio de cobertura:

```text
target/site/jacoco/index.html
```

## 11. Como Rodar o Scan de Vulnerabilidade

```bash
mvn dependency-check:check
```

Relatorios gerados:

```text
target/dependency-check
```

Modelo para registrar a analise:

```text
SECURITY_REPORT.md
```

## 12. Resumo das Funcionalidades Entregues

- Autenticacao JWT para APIs administrativas.
- CRUD de clientes com validacao de CPF/CNPJ.
- CRUD de veiculos com validacao de placa.
- CRUD de servicos da oficina.
- CRUD de pecas e insumos.
- Controle de estoque com entrada, saida, reserva, liberacao e baixa.
- Criacao de Ordem de Servico com cliente, veiculo, servicos e pecas.
- Geracao automatica de orçamento.
- Aprovacao de orçamento pelo cliente.
- Acompanhamento da OS pelo cliente via API.
- Monitoramento de tempo medio de execucao dos servicos.
- Swagger/OpenAPI.
- Testes unitarios e de integracao.
- Dockerfile e docker-compose.yml.

## 13. Resumo da Arquitetura

O projeto segue um monolito em camadas:

- `domain`: entidades, value objects, enums, regras de negocio e excecoes.
- `application`: use cases, comandos, portas e orquestracao dos fluxos.
- `infrastructure`: persistencia JPA, repositories, seguranca e configuracoes.
- `interfaces`: controllers REST, mappers e DTOs gerados pelo OpenAPI.

Essa organizacao evita regra de negocio em controllers e preserva o dominio independente da camada HTTP.

## 14. Resumo da Aplicacao de DDD

Foram aplicados conceitos de DDD na modelagem dos principais elementos do dominio:

- Entidades: Cliente, Veiculo, Ordem de Servico, Servico, Peca/Insumo, Orçamento e Movimentacao de Estoque.
- Value Objects: Documento, Placa, Dinheiro e Endereco.
- Agregados: Ordem de Servico, Peca/Insumo e Cliente/Veiculo.
- Politicas de dominio: transicao de status da OS, calculo de orçamento e regras de estoque.
- Linguagem ubiqua documentada em `docs/ddd/README.md`.

## 15. Resumo do Event Storming

O Event Storming documenta dois fluxos principais:

- Criacao e acompanhamento da Ordem de Servico.
- Gestao de pecas e insumos.

Arquivo:

```text
docs/ddd/event-storming-auto-care-hub.md
```

## 16. Relatorio de Vulnerabilidades

Arquivo:

```text
SECURITY_REPORT.md
```

Status:

```text
[PREENCHER apos executar o scan real]
```

## 17. Decisoes Tecnicas

- Monolito em camadas por simplicidade operacional no MVP.
- PostgreSQL por consistencia relacional e suporte transacional.
- Flyway para versionamento de schema e massa de demonstracao.
- OpenAPI First para manter contrato REST documentado.
- JWT stateless para proteger APIs administrativas.
- Testcontainers para testes de integracao com banco real.
- Docker Compose para ambiente local reprodutivel.

## 18. Limitacoes Conhecidas

- Envio de orçamento ao cliente e representado por API, sem mensageria real.
- Historico de status e simplificado para o MVP.
- Swagger fica publico no perfil local academico para facilitar avaliacao.
- O scan de vulnerabilidades precisa ser executado no ambiente final e registrado no `SECURITY_REPORT.md`.

## 19. Melhorias Futuras

- Auditoria detalhada de alteracoes por usuario.
- Outbox/eventos de dominio persistidos.
- Integracao real com e-mail, SMS ou WhatsApp.
- Multi-tenant completo por oficina.
- Permissoes mais granulares por acao.
- Pipeline CI com testes, cobertura e scan de vulnerabilidades.
