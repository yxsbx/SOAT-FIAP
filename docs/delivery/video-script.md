# Roteiro do Video de Apresentacao

Duracao maxima: 15 minutos

## 1. Abertura e Apresentacao do Problema

Tempo sugerido: 1 minuto

Apresentar o aluno:

```text
Meu nome e [NOME_DO_ALUNO] e esta e a apresentacao do projeto AutoCare Hub API.
```

Apresentar o problema:

```text
O problema escolhido foi a gestao operacional de uma oficina mecanica. Muitas oficinas precisam controlar clientes, veiculos, pecas, servicos, estoque, diagnosticos, orcamentos, aprovacoes e andamento das ordens de servico de forma organizada.
```

Explicar a oportunidade:

```text
O AutoCare Hub API foi criado como um MVP backend para centralizar esses fluxos e oferecer uma base evolutiva para um produto automotivo mais completo.
```

## 2. Escopo do MVP

Tempo sugerido: 1 minuto e 30 segundos

Explicar o que esta incluido no MVP:

- Cadastro e consulta de clientes.
- Cadastro e consulta de veiculos.
- Cadastro e consulta de servicos de oficina.
- Cadastro e controle de pecas e estoque.
- Criacao de ordens de servico.
- Inclusao de servicos e pecas na ordem de servico.
- Geracao de orcamento.
- Aprovacao de orcamento.
- Alteracao e acompanhamento de status da ordem de servico.
- Autenticacao e autorizacao com JWT.

Explicar o que nao esta no MVP:

- Aplicativo mobile do cliente.
- Painel web completo da oficina.
- Marketplace de lojas de pecas.
- Cupons.
- Agendamento.
- Servicos 24h.
- Integracoes com lava-jatos e outros servicos automotivos.

## 3. Explicacao da Arquitetura

Tempo sugerido: 2 minutos

Mostrar a organizacao do projeto:

```text
src/main/java/br/com/autocarehub
```

Explicar as camadas:

- `domain`: concentra regras de negocio, entidades e objetos de valor.
- `application`: concentra casos de uso, comandos, consultas e portas.
- `infrastructure`: concentra seguranca, persistencia JPA, repositories, configuracoes e adapters.
- `interfaces`: concentra controllers REST e mappers de DTOs.
- `docs`: concentra documentacao OpenAPI, DDD, seguranca e entrega.

Ponto principal da explicacao:

```text
Os controllers nao possuem regra de negocio, nao acessam repositories diretamente e nao expõem entidades JPA. Eles apenas convertem DTOs REST para comandos da application layer, chamam casos de uso e retornam DTOs de resposta.
```

Explicar seguranca:

```text
A aplicacao usa Spring Security com JWT. Os perfis ADMIN, EMPLOYEE e CUSTOMER controlam as permissoes de acesso.
```

## 4. Explicacao do DDD

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
- Evolucao futura.

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

Ponto principal da explicacao:

```text
O DDD ajudou a organizar o dominio da oficina em conceitos claros, como Cliente, Veiculo, Ordem de Servico, Diagnostico, Orcamento, Aprovacao, Execucao e Entrega.
```

## 5. Demonstracao do Swagger

Tempo sugerido: 1 minuto e 30 segundos

Abrir:

```text
http://localhost:8080/swagger-ui.html
```

Mostrar:

- Endpoint de login.
- Endpoints de clientes.
- Endpoints de veiculos.
- Endpoints de pecas.
- Endpoints de servicos.
- Endpoints de ordens de servico.

Executar login:

```text
Usuario: admin@autocarehub.com
Senha: autocare123
```

Mostrar o token JWT retornado e explicar que ele deve ser usado como Bearer token nas chamadas protegidas.

## 6. Demonstracao dos Principais Fluxos

Tempo sugerido: 3 minutos

Fluxo sugerido:

1. Criar um cliente.
2. Criar um veiculo para o cliente.
3. Criar uma peca.
4. Criar um servico de oficina.
5. Criar uma ordem de servico.
6. Adicionar o servico na ordem de servico.
7. Adicionar a peca na ordem de servico.
8. Gerar o orcamento.
9. Aprovar o orcamento.
10. Alterar o status da ordem de servico.

Explicar durante a demonstracao:

```text
Esse fluxo representa o ciclo principal do MVP: a oficina recebe o veiculo, registra a OS, faz diagnostico e orcamento, aguarda aprovacao e acompanha a execucao ate a entrega.
```

## 7. Demonstracao de Testes

Tempo sugerido: 1 minuto

Executar:

```bash
mvn test
```

Explicar cobertura dos testes:

- Testes de dominio para ServiceOrder.
- Testes de dominio para Part.
- Testes de dominio para WorkshopService.
- Testes de dominio para Customer.
- Testes de dominio para Vehicle.
- Teste de integracao cobrindo o fluxo principal da ordem de servico.

Ponto principal:

```text
Os testes validam principalmente regras criticas de negocio, como transicoes de status da OS, aprovacao de orcamento, controle de estoque e validacoes de documento, placa, preco e tempo estimado.
```

## 8. Demonstracao do Docker

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
O Docker Compose sobe a aplicacao e o PostgreSQL. Ao iniciar, a aplicacao executa as migrations do Flyway automaticamente, incluindo a criacao do usuario admin de desenvolvimento.
```

Mostrar como parar:

```bash
docker compose down
```

## 9. Demonstracao do Relatorio de Vulnerabilidades

Tempo sugerido: 1 minuto

Mostrar a documentacao:

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
O projeto usa OWASP Dependency-Check para verificar vulnerabilidades conhecidas nas dependencias Maven. O documento de seguranca possui campos para data da analise, resumo dos resultados, vulnerabilidades encontradas, plano de correcao e conclusao.
```

## 10. Conclusao e Visao Futura

Tempo sugerido: 1 minuto

Concluir:

```text
O AutoCare Hub API entrega um MVP backend para controle operacional de oficina mecanica, com arquitetura em camadas, DDD, OpenAPI First, seguranca JWT, persistencia com PostgreSQL, migrations, testes, Docker e analise de vulnerabilidades.
```

Apresentar visao futura:

- Aplicativo do cliente.
- Painel web da oficina.
- Marketplace de lojas de pecas.
- Cupons.
- Agendamento.
- Servicos 24h.
- Integracao com lava-jatos e outros servicos automotivos.

Fechamento:

```text
Essas evolucoes nao fazem parte do MVP atual, mas mostram como a base criada pode crescer para um ecossistema automotivo mais completo.
```
