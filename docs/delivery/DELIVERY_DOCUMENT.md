# Documento Final de Entrega - Tech Challenge FIAP

## 1. Nome do Grupo

```text
[PREENCHER - NOME_DO_GRUPO]
```

## 2. Participantes

| Nome completo | RM | Papel no projeto |
|---|---|---|
| [PREENCHER - NOME_DO_PARTICIPANTE_1] | [PREENCHER - RM] | [PREENCHER] |
| [PREENCHER - NOME_DO_PARTICIPANTE_2] | [PREENCHER - RM] | [PREENCHER] |
| [PREENCHER - NOME_DO_PARTICIPANTE_3] | [PREENCHER - RM] | [PREENCHER] |

## 3. Usernames no Discord

| Participante | Username no Discord |
|---|---|
| [PREENCHER - NOME_DO_PARTICIPANTE_1] | [PREENCHER - DISCORD] |
| [PREENCHER - NOME_DO_PARTICIPANTE_2] | [PREENCHER - DISCORD] |
| [PREENCHER - NOME_DO_PARTICIPANTE_3] | [PREENCHER - DISCORD] |

## 4. Nome do Projeto

AutoCare Hub

## 5. Descricao Resumida

O AutoCare Hub e um MVP de back-end monolitico para gestao de uma oficina mecanica, desenvolvido para o Tech Challenge FIAP. A API centraliza o cadastro de clientes, veiculos, servicos, pecas e insumos, controle de estoque, criacao de ordens de servico, geracao e aprovacao de orcamentos e acompanhamento da OS pelo cliente.

O objetivo principal e oferecer uma base RESTful segura, documentada e executavel localmente, aplicando arquitetura em camadas, conceitos de Domain-Driven Design, autenticacao JWT, validacoes de dados sensiveis, testes automatizados, Docker e documentacao de entrega.

## 6. Link da Documentacao DDD

Link externo, se publicado em Miro, PDF ou plataforma equivalente:

```text
[PREENCHER - LINK_DA_DOCUMENTACAO_DDD]
```

Arquivos base no repositorio:

```text
docs/ddd/DDD_DOCUMENTATION.md
docs/ddd/EVENT_STORMING.md
```

## 7. Link do Repositorio Privado

```text
[PREENCHER - LINK_DO_REPOSITORIO_PRIVADO]
```

## 8. Link ou Instrucao de Acesso ao Swagger

Com a aplicacao em execucao local:

```text
http://localhost:8080/swagger-ui.html
```

Contrato OpenAPI versionado no repositorio:

```text
docs/openapi/openapi.yaml
```

Observacao: para o MVP academico, o Swagger pode ficar acessivel localmente para facilitar a avaliacao. Para ambiente produtivo, recomenda-se restringir ou desabilitar a exposicao usando as variaveis:

```text
SPRINGDOC_API_DOCS_ENABLED=false
SPRINGDOC_SWAGGER_UI_ENABLED=false
```

## 9. Como Executar o Projeto

### Pre-requisitos

- Java 21.
- Maven 3.9 ou superior.
- Docker e Docker Compose.
- Git.

### Configurar variaveis locais

Copiar o arquivo de exemplo:

```bash
cp .env.example .env
```

Editar o arquivo `.env` e preencher, no minimo:

```text
POSTGRES_PASSWORD=[PREENCHER - SENHA_LOCAL_DO_POSTGRES]
JWT_SECRET=[PREENCHER - SEGREDO_LOCAL_COM_PELO_MENOS_32_BYTES]
```

### Executar com Docker Compose

```bash
docker compose up --build
```

A API ficara disponivel em:

```text
http://localhost:8080
```

### Executar aplicacao local com PostgreSQL via Docker

Subir apenas o banco:

```bash
docker compose up -d postgres
```

Executar a aplicacao:

```bash
mvn spring-boot:run
```

## 10. Como Rodar os Testes

Executar os testes:

```bash
mvn test
```

Executar testes com verificacao de cobertura:

```bash
mvn verify
```

Relatorio de cobertura JaCoCo:

```text
target/site/jacoco/index.html
```

Observacao: os testes de integracao usam Testcontainers com PostgreSQL. Portanto, o Docker precisa estar instalado, iniciado e acessivel no ambiente onde os testes forem executados.

## 11. Como Rodar o Scan de Vulnerabilidade

Executar o OWASP Dependency-Check:

```bash
mvn dependency-check:check
```

Relatorios gerados:

```text
target/dependency-check
```

Modelo de registro da analise:

```text
SECURITY_REPORT.md
```

## 12. Resumo das Funcionalidades Entregues

- Autenticacao JWT para APIs administrativas.
- Protecao de endpoints por perfil e roles.
- CRUD de clientes com validacao real de CPF/CNPJ.
- CRUD de veiculos com validacao de placa brasileira antiga e Mercosul.
- CRUD de servicos da oficina.
- CRUD de pecas e insumos.
- Controle de estoque com entrada, saida, reserva, liberacao e baixa.
- Criacao de Ordem de Servico com cliente, veiculo, servicos e pecas.
- Inclusao de servicos solicitados na OS.
- Inclusao de pecas e insumos na OS.
- Geracao automatica de orcamento com total de servicos, pecas e total geral.
- Aprovacao de orcamento pelo cliente ou usuario autorizado.
- Transicoes de status da OS controladas por regra de dominio.
- Consulta e acompanhamento da OS pelo cliente via API.
- Monitoramento de tempo medio de execucao dos servicos.
- Swagger/OpenAPI.
- Testes unitarios e de integracao.
- Dockerfile e `docker-compose.yml`.
- Documentacao DDD e Event Storming.
- Modelo de relatorio de vulnerabilidades.

## 13. Resumo da Arquitetura

O projeto foi implementado como um back-end monolitico em camadas:

```text
src/main/java/br/com/autocarehub
├── domain
│   ├── entidades, value objects, enums, regras de negocio e excecoes
├── application
│   ├── use cases, comandos e portas de repositorio
├── infrastructure
│   ├── persistencia JPA, repositories, adapters, seguranca e configuracoes
└── interfaces
    ├── controllers REST, mappers e integracao com DTOs gerados pelo OpenAPI
```

Principios adotados:

- Controllers sem regra de negocio.
- Controllers nao acessam repositories diretamente.
- Entidades JPA nao sao expostas como resposta da API.
- Casos de uso coordenam os fluxos da aplicacao.
- Regras centrais ficam no dominio.
- Persistencia fica isolada na camada de infraestrutura.
- Contrato REST versionado em OpenAPI.

## 14. Resumo da Aplicacao de DDD

O projeto aplica DDD de forma compativel com um monolito em camadas.

Entidades principais:

- `Customer`: cliente atendido pela oficina.
- `Vehicle`: veiculo vinculado ao cliente.
- `WorkshopService`: servico oferecido pela oficina.
- `Part`: peca ou insumo controlado em estoque.
- `ServiceOrder`: ordem de servico.
- `Budget` e `BudgetItem`: composicao financeira do orcamento.
- `StockMovement`: movimentacao de estoque.

Value Objects:

- `Document`: CPF/CNPJ normalizado e validado.
- `Plate`: placa normalizada e validada.
- `Money`: representacao de valores monetarios.
- `Address`: dados de endereco.

Agregados e regras:

- `ServiceOrder` centraliza servicos, pecas, orcamento e transicoes de status.
- `Part` centraliza regras de estoque disponivel, reservado, minimo e baixa.
- `Customer` e `Vehicle` preservam identidade e vinculo do atendimento.

Linguagem ubiqua documentada:

```text
docs/ddd/02-ubiquitous-language.md
```

## 15. Resumo do Event Storming

O Event Storming foi documentado para os fluxos principais exigidos:

1. Criacao e acompanhamento da Ordem de Servico.
2. Gestao de pecas e insumos.

Eventos contemplados:

- Cliente identificado.
- Veiculo cadastrado.
- Ordem de Servico criada.
- Servico solicitado incluido.
- Peca incluida na ordem.
- Orcamento gerado.
- Orcamento aprovado.
- Ordem liberada para execucao.
- Diagnostico iniciado.
- Execucao iniciada.
- Servico finalizado.
- Veiculo entregue.
- Estoque atualizado.
- Peca reservada.
- Peca baixada do estoque.
- Estoque insuficiente identificado.

Arquivo principal:

```text
docs/ddd/EVENT_STORMING.md
```

## 16. Relatorio de Vulnerabilidades

Arquivo do relatorio:

```text
SECURITY_REPORT.md
```

Status do scan real:

```text
[PREENCHER - RESULTADO_FINAL_DO_SCAN_APOS_EXECUCAO_DO_OWASP_DEPENDENCY_CHECK]
```

Evidencias do scan:

```text
[PREENCHER - CAMINHO_DO_RELATORIO_HTML/JSON/SARIF_GERADO]
```

## 17. Decisoes Tecnicas

- Uso de monolito em camadas para manter simplicidade operacional no MVP.
- Aplicacao de DDD por meio de entidades de dominio, value objects, agregados e use cases.
- PostgreSQL como banco relacional pela necessidade de consistencia entre clientes, veiculos, ordens, pecas e estoque.
- Flyway para versionamento de schema e dados iniciais.
- OpenAPI First para manter o contrato REST documentado e reutilizavel.
- Spring Security com JWT stateless para proteger APIs administrativas.
- BCrypt para hash de senhas.
- Testcontainers para testes de integracao com banco PostgreSQL realista.
- Docker Compose para ambiente local reprodutivel.
- OWASP Dependency-Check para apoiar o relatorio de vulnerabilidades.

## 18. Limitacoes Conhecidas

- O envio de orcamento ao cliente e representado pela API; nao ha integracao real com e-mail, SMS ou WhatsApp.
- O historico de status da OS e simplificado para o escopo do MVP.
- O Swagger fica acessivel no ambiente local academico para facilitar a avaliacao.
- A execucao de testes de integracao depende de Docker disponivel por causa do Testcontainers.
- O relatorio de vulnerabilidades precisa ser preenchido com o resultado real do scan no ambiente final.
- O sistema nao implementa pagamento online.
- O sistema nao implementa multi-tenant completo por oficina no escopo do MVP.

## 19. Melhorias Futuras

- Pipeline CI com build, testes, cobertura e scan de vulnerabilidades.
- Auditoria detalhada de alteracoes sensiveis por usuario.
- Historico persistido de eventos e mudancas de status da OS.
- Outbox pattern para eventos de dominio.
- Integracao real com canais de notificacao ao cliente.
- Permissoes mais granulares por acao e contexto.
- Multi-tenant completo para multiplas oficinas.
- Painel analitico mais completo para indicadores operacionais.
- Observabilidade com metricas, tracing e logs estruturados.
- Rotina automatizada de atualizacao e revisao de dependencias.
