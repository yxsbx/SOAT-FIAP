# Roteiro do Video de Apresentação

Duração maxima: 15 minutos

## 1. Abertura e Apresentação do Problema

Tempo sugerido: 1 minuto

Apresentar o aluno:

```text
Meu nome e [NOME_DO_ALUNO] e esta e a apresentação do projeto AutoCare Hub API.
```

Apresentar o problema:

```text
O problema escolhido foi a gestão operacional de uma oficina mecânica. Muitas oficinas precisam controlar clientes, veículos, peças, serviços, estoque, diagnósticos, orçamentos, aprovacoes e andamento das ordens de serviço de forma organizada.
```

Explicar a oportunidade:

```text
O AutoCare Hub API foi criado como um MVP backend para centralizar esses fluxos e oferecer uma base evolutiva para um produto automotivo mais completo.
```

## 2. Escopo do MVP

Tempo sugerido: 1 minuto e 30 segundos

Explicar o que esta incluido no MVP:

- Cadastro e consulta de clientes.
- Cadastro e consulta de veículos.
- Cadastro e consulta de serviços de oficina.
- Cadastro e controle de peças e estoque.
- Criação de ordens de serviço.
- Inclusao de serviços e peças na ordem de serviço.
- Geração de orçamento.
- Aprovação de orçamento.
- Alteração e acompanhamento de status da ordem de serviço.
- Autenticação e autorização com JWT.

Explicar o que nao esta no MVP:

- Aplicativo mobile do cliente.
- Painel web completo da oficina.
- Marketplace de lojas de peças.
- Cupons.
- Agendamento.
- Serviços 24h.
- Integracoes com lava-jatos e outros serviços automotivos.

## 3. Explicação da Arquitetura

Tempo sugerido: 2 minutos

Mostrar a organização do projeto:

```text
src/main/java/br/com/autocarehub
```

Explicar as camadas:

- `domain`: concentra regras de negocio, entidades e objetos de valor.
- `application`: concentra casos de uso, comandos, consultas e portas.
- `infrastructure`: concentra seguranca, persistencia JPA, repositories, configuracoes e adapters.
- `interfaces`: concentra controllers REST e mappers de DTOs.
- `docs`: concentra documentação OpenAPI, DDD, seguranca e entrega.

Ponto principal da explicação:

```text
Os controllers nao possuem regra de negocio, nao acessam repositories diretamente e nao expõem entidades JPA. Eles apenas convertem DTOs REST para comandos da application layer, chamam casos de uso e retornam DTOs de resposta.
```

Explicar seguranca:

```text
A aplicação usa Spring Security com JWT. Os perfis ADMIN, EMPLOYEE e CUSTOMER controlam as permissoes de acesso.
```

## 4. Explicação do DDD

Tempo sugerido: 2 minutos

Mostrar a pasta:

```text
docs/ddd
```

Explicar os principais documentos:

- Contexto do produto.
- Linguagem ubiqua.
- Event storming.
- Bounded contexts.
- Agregados e regras de negocio.
- Context map.
- Evolução futura.

Explicar os bounded contexts do MVP:

- Customer and Vehicle Management.
- Service Order Management.
- Inventory Management.
- Service Catalog Management.

Explicar o contexto futuro:

- Future Marketplace.

Explicar agregados principais:

- Customer.
- Vehicle.
- ServiceOrder.
- Part.
- WorkshopService.

Ponto principal da explicação:

```text
O DDD ajudou a organizar o domínio da oficina em conceitos claros, como Cliente, Veículo, Ordem de Servico, Diagnostico, Orçamento, Aprovação, Execução e Entrega.
```

## 5. Demonstração do Swagger

Tempo sugerido: 1 minuto e 30 segundos

Abrir:

```text
http://localhost:8080/swagger-ui.html
```

Mostrar:

- Endpoint de login.
- Endpoints de clientes.
- Endpoints de veículos.
- Endpoints de peças.
- Endpoints de serviços.
- Endpoints de ordens de serviço.

Executar login:

```text
Usuario: admin@autocarehub.com
Senha: <SENHA_DEMO_LOCAL>
```

Mostrar o token JWT retornado e explicar que ele deve ser usado como Bearer token nas chamadas protegidas.

## 6. Demonstração dos Principais Fluxos

Tempo sugerido: 3 minutos

Fluxo sugerido:

1. Criar um cliente.
2. Criar um veículo para o cliente.
3. Criar uma peça.
4. Criar um serviço de oficina.
5. Criar uma ordem de serviço.
6. Adicionar o serviço na ordem de serviço.
7. Adicionar a peça na ordem de serviço.
8. Gerar o orçamento.
9. Aprovar o orçamento.
10. Alterar o status da ordem de serviço.

Explicar durante a demonstração:

```text
Esse fluxo representa o ciclo principal do MVP: a oficina recebe o veículo, registra a OS, faz diagnostico e orçamento, aguarda aprovação e acompanha a execução ate a entrega.
```

## 7. Demonstração de Testes

Tempo sugerido: 1 minuto

Executar:

```bash
mvn test
```

Explicar cobertura dos testes:

- Testes de domínio para ServiceOrder.
- Testes de domínio para Part.
- Testes de domínio para WorkshopService.
- Testes de domínio para Customer.
- Testes de domínio para Vehicle.
- Teste de integração cobrindo o fluxo principal da ordem de serviço.

Ponto principal:

```text
Os testes validam principalmente regras criticas de negocio, como transicoes de status da OS, aprovação de orçamento, controle de estoque e validacoes de documento, placa, preco e tempo estimado.
```

## 8. Demonstração do Docker

Tempo sugerido: 1 minuto

Mostrar os arquivos:

- `Dockerfile`
- `docker-compose.yml`

Executar:

```bash
docker compose up --build
```

Explicar:

```text
O Docker Compose sobe a aplicação e o PostgreSQL. Ao iniciar, a aplicação executa as migrations do Flyway automaticamente, incluindo a criação do usuario admin de desenvolvimento.
```

Mostrar como parar:

```bash
docker compose down
```

## 9. Demonstração do Relatorio de Vulnerabilidades

Tempo sugerido: 1 minuto

Mostrar a documentação:

```text
docs/security/vulnerability-analysis.md
```

Executar:

```bash
mvn dependency-check:check
```

Mostrar o diretorio dos relatorios:

```text
target/dependency-check
```

Explicar:

```text
O projeto usa OWASP Dependency-Check para verificar vulnerabilidades conhecidas nas dependencias Maven. O documento de seguranca possui campos para data da analise, resumo dos resultados, vulnerabilidades encontradas, plano de correção e conclusao.
```

## 10. Conclusao e Visao Futura

Tempo sugerido: 1 minuto

Concluir:

```text
O AutoCare Hub API entrega um MVP backend para controle operacional de oficina mecânica, com arquitetura em camadas, DDD, OpenAPI First, seguranca JWT, persistencia com PostgreSQL, migrations, testes, Docker e analise de vulnerabilidades.
```

Apresentar visao futura:

- Aplicativo do cliente.
- Painel web da oficina.
- Marketplace de lojas de peças.
- Cupons.
- Agendamento.
- Serviços 24h.
- Integração com lava-jatos e outros serviços automotivos.

Fechamento:

```text
Essas evolucoes nao fazem parte do MVP atual, mas mostram como a base criada pode crescer para um ecossistema automotivo mais completo.
```
