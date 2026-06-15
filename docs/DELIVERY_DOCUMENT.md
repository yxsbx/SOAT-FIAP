# Documento Final de Entrega - Tech Challenge FIAP

## 1. Nome do Grupo

```text
[PREENCHER - NOME_DO_GRUPO]
```

## 2. Participantes

| Nome completo | RM | Papel no projeto |
| --- | --- | --- |
| [PREENCHER - NOME_DO_PARTICIPANTE_1] | [PREENCHER - RM] | [PREENCHER] |
| [PREENCHER - NOME_DO_PARTICIPANTE_2] | [PREENCHER - RM] | [PREENCHER] |
| [PREENCHER - NOME_DO_PARTICIPANTE_3] | [PREENCHER - RM] | [PREENCHER] |

## 3. Usernames no Discord

| Participante | Username no Discord |
| --- | --- |
| [PREENCHER - NOME_DO_PARTICIPANTE_1] | [PREENCHER - DISCORD] |
| [PREENCHER - NOME_DO_PARTICIPANTE_2] | [PREENCHER - DISCORD] |
| [PREENCHER - NOME_DO_PARTICIPANTE_3] | [PREENCHER - DISCORD] |

## 4. Projeto

AutoCare Hub.

## 5. Resumo da Solucao

O AutoCare Hub e um MVP de backend monolitico para gestao de oficina mecanica, desenvolvido para o Tech Challenge FIAP. A API centraliza cadastro de clientes, veículos, servicos, pecas e insumos, controle de estoque, criacao de Ordens de Servico, geracao e aprovacao de orcamentos e acompanhamento da OS pelo cliente.

A solucao aplica arquitetura em camadas, DDD, APIs REST documentadas com Swagger/OpenAPI, autenticacao JWT, validacoes de CPF/CNPJ e placa, Flyway, PostgreSQL, Docker, testes automatizados e analise de vulnerabilidades.

## 6. Links

| Item | Link ou caminho |
| --- | --- |
| Repositorio privado | `[PREENCHER - LINK_DO_REPOSITORIO_PRIVADO]` |
| Documentacao DDD | `docs/DDD_DOCUMENTATION.md` |
| Event Storming | `docs/EVENT_STORMING.md` |
| Swagger local | `http://localhost:8080/swagger-ui.html` |
| Contrato OpenAPI | `docs/openapi/openapi.yaml` |
| Guia de scans | `docs/SECURITY_SCAN_GUIDE.md` |
| Relatorio de vulnerabilidades | `docs/SECURITY_REPORT.md` |

Link externo da documentacao, se publicada em PDF, Miro ou plataforma equivalente:

```text
[PREENCHER - LINK_DA_DOCUMENTACAO_PUBLICADA]
```

## 7. Como Executar

Pre-requisitos:

- Java 21.
- Maven 3.9 ou superior.
- Docker e Docker Compose.
- Git.

Configurar variaveis locais:

```bash
cp .env.example .env
```

Preencher no `.env`:

```text
POSTGRES_PASSWORD=[PREENCHER - SENHA_LOCAL_DO_POSTGRES]
JWT_SECRET=[PREENCHER - SEGREDO_LOCAL_COM_PELO_MENOS_32_BYTES]
```

Executar API e banco com Docker Compose:

```bash
docker compose up --build
```

Executar somente PostgreSQL pelo Compose e API pelo Maven:

```bash
docker compose up -d postgres
mvn spring-boot:run
```

URLs locais:

```text
API: http://localhost:8080
Swagger: http://localhost:8080/swagger-ui.html
PostgreSQL: localhost:5432
```

## 8. Testes e Cobertura

Executar testes:

```bash
mvn test
```

Executar testes com verificacao de cobertura:

```bash
mvn verify
```

Relatorio JaCoCo:

```text
target/site/jacoco/index.html
```

Resultado local validado:

| Indicador | Resultado |
| --- | ---: |
| Testes automatizados | 108 |
| Cobertura de instrucoes | 95,36% |
| Cobertura de linhas | 97,35% |
| Regra configurada | minimo de 95% para instrucoes e linhas no escopo de negocio |

A regra de 95% supera o criterio academico de 80%. Os testes de integracao dependem de Docker quando usam Testcontainers.

## 9. Funcionalidades Entregues

- CRUD de clientes com validacao de CPF/CNPJ.
- CRUD de veículos com validacao de placa.
- CRUD de servicos da oficina.
- CRUD de pecas e insumos.
- Controle de estoque com entrada, saida, reserva, liberacao e baixa.
- Criacao de Ordem de Servico com cliente, veículo, servicos e pecas.
- Geracao automatica de orcamento.
- Aprovacao de orcamento.
- Transicoes controladas de status da OS.
- Acompanhamento da OS pelo cliente via API.
- Metrica de tempo medio de execucao.
- Autenticacao JWT para APIs administrativas.
- Administracao de usuarios e preferencias.
- Swagger/OpenAPI.
- Dockerfile e `docker-compose.yml`.
- Relatorio de vulnerabilidades.

## 10. Arquitetura

O projeto e um backend monolitico em camadas:

```text
src/main/java/br/com/autocarehub
├── domain
├── application
├── infrastructure
└── interfaces
```

Principios aplicados:

- controllers sem regra de negocio;
- casos de uso coordenando fluxos da aplicacao;
- regras centrais no dominio;
- portas de repositorio na camada de aplicacao;
- persistencia e seguranca na infraestrutura;
- DTOs de API separados das entidades de persistencia;
- migrations Flyway em `src/main/resources/db/migration`.

## 11. DDD

A documentacao DDD completa esta em `docs/DDD_DOCUMENTATION.md`.

Resumo:

- Entidades: `Customer`, `Vehicle`, `WorkshopService`, `Part`, `ServiceOrder`, `StockMovement`, `User`, `DemoLead`.
- Value Objects: `Document`, `Plate`, `Money`, `Address`, `BudgetItem`.
- Agregados principais: `ServiceOrder`, `Part`, `Customer` e `Vehicle`.
- Bounded Contexts: Atendimento de Oficina, Cadastro de Clientes e Veiculos, Catalogo de Servicos, Gestao de Pecas e Estoque, Orcamentos e Aprovacao, Identidade e Acesso.
- Linguagem ubiqua documentada no arquivo DDD consolidado.

## 12. Event Storming

O Event Storming completo esta em `docs/EVENT_STORMING.md`.

Fluxos documentados:

1. Criacao da Ordem de Servico.
2. Acompanhamento da Ordem de Servico.
3. Gestao de pecas e insumos.

Eventos principais:

- `OrdemServicoCriada`
- `OrcamentoGerado`
- `OrcamentoAprovado`
- `OrdemServicoEmExecucao`
- `OrdemServicoFinalizada`
- `VeiculoEntregue`
- `PecaReservada`
- `PecaBaixadaDoEstoque`
- `EstoqueInsuficienteIdentificado`

## 13. Seguranca

Controles implementados:

- JWT com segredo configurado por variavel de ambiente.
- Senhas com BCrypt.
- APIs administrativas protegidas por Spring Security.
- DTOs explicitos para evitar exposicao direta de entidades.
- CPF/CNPJ e placa validados no dominio.
- CORS configuravel.
- Swagger desabilitavel por variavel de ambiente.
- `.env.example` sem secrets reais.

## 14. Vulnerabilidades

Guia de execucao:

```text
docs/SECURITY_SCAN_GUIDE.md
```

Relatorio final:

```text
docs/SECURITY_REPORT.md
```

Resultado registrado no relatorio:

- OWASP Dependency-Check backend: 0 vulnerabilidades no scan final.
- `npm audit` frontend: 0 vulnerabilidades no audit final.
- Docker image scan: pendente de execucao.
- Secrets scan: pendente de execucao.
- Analise estatica complementar: pendente de execucao.

## 15. Usuarios Demo

```text
admin@autocarehub.com
master@autocarehub.com
oficina.admin@autocarehub.com
loja.admin@autocarehub.com
oficina.funcionario@autocarehub.com
loja.funcionario@autocarehub.com
cliente@autocarehub.com
```

Senha demo:

```text
[PREENCHER - SENHA_DEMO_LOCAL]
```

## 16. Limitacoes Conhecidas

- Nao ha pagamento online.
- Nao ha envio real de e-mail, SMS ou WhatsApp.
- O historico de status da OS e simplificado para o escopo do MVP.
- O controle de parceiros e lojas permanece no mesmo monolito.
- O Swagger fica publico no ambiente local academico.
- Scans de Docker, secrets e SAST ainda dependem de execucao manual se forem exigidos na submissao final.

## 17. Melhorias Futuras

- Pipeline CI com build, testes, cobertura e scans.
- Auditoria detalhada de alteracoes sensiveis.
- Historico persistido de eventos e mudancas de status da OS.
- Outbox pattern para eventos de dominio.
- Integracao real com canais de notificacao ao cliente.
- Permissoes mais granulares por acao e contexto.
- Multi-tenant completo para multiplas oficinas.
