# Refinamento Técnico - AutoCare Hub

## 1. Objetivo do refinamento

Este documento mostra como o problema de negócio da oficina foi transformado em uma solução técnica no AutoCare Hub.

O refinamento partiu da jornada principal exigida no Tech Challenge, com foco na Ordem de Serviço. A partir desse fluxo, foram definidas as decisões técnicas do MVP: backend monolítico, arquitetura em camadas, domínio protegido, API REST, banco relacional, autenticação JWT, execução local com Docker, testes automatizados e documentação.

## 2. Jornada técnica refinada

A jornada abaixo representa o fluxo principal implementado no backend:

1. O atendente ou usuário administrativo informa o CPF/CNPJ do cliente.
2. O backend valida o documento com o value object `Document`.
3. O sistema localiza o cliente pelo documento ou permite cadastrar um novo cliente no fluxo da OS.
4. O atendente informa ou seleciona o veículo.
5. O backend valida a placa com o value object `Plate`.
6. O backend garante que o veículo está vinculado ao cliente usado na OS.
7. O atendente cria a Ordem de Serviço.
8. O sistema registra os serviços solicitados.
9. O sistema registra peças ou insumos quando eles fazem parte do atendimento.
10. O backend valida a disponibilidade de estoque pelo agregado `Part`.
11. O sistema gera o orçamento com base nos serviços e nas peças.
12. O backend reserva peças quando a geração do orçamento exige essa proteção de estoque.
13. O cliente aprova o orçamento, ou a oficina registra a aprovação recebida do cliente.
14. O backend registra a aprovação da OS e libera o fluxo para execução.
15. A oficina inicia o diagnóstico ou a execução, conforme a transição solicitada.
16. A oficina finaliza a OS.
17. A oficina registra a entrega do veículo.
18. O cliente consulta o acompanhamento da OS pela API.
19. O administrador consulta listagens, detalhes e tempo médio de execução.

O MVP não implementa WhatsApp, e-mail, SMS, pagamento online, agenda externa, fornecedores externos, ERP, mensageria ou deploy produtivo em cloud. O fluxo principal fica concentrado no backend, no banco PostgreSQL e no frontend demonstrativo.

## 3. Requisitos de negócio transformados em decisões técnicas

| Requisito de negócio             | Decisão técnica                                     | Justificativa                                                               | Evidência no projeto                                                |
|----------------------------------|-----------------------------------------------------|-----------------------------------------------------------------------------|---------------------------------------------------------------------|
| Gerenciar clientes               | CRUD REST e entidade `Customer`.                    | Clientes são a base para veículos e Ordens de Serviço.                      | `CustomersController`, `Customer`, `CustomerRepository`.            |
| Identificar cliente por CPF/CNPJ | Value object `Document`.                            | Evita duplicar validação de documento em vários pontos da aplicação.        | `domain/valueobject/Document.java`.                                 |
| Gerenciar veículos               | CRUD REST e entidade `Vehicle`.                     | O veículo precisa estar vinculado ao cliente da OS.                         | `VehiclesController`, `Vehicle`, `CreateServiceOrderUseCase`.       |
| Validar placa                    | Value object `Plate`.                               | Centraliza validação de placa antiga e Mercosul.                            | `domain/valueobject/Plate.java`.                                    |
| Gerenciar serviços               | CRUD REST e entidade `WorkshopService`.             | Serviços compõem o orçamento da OS.                                         | `WorkshopServicesController`, `WorkshopService`.                    |
| Gerenciar peças e insumos        | CRUD REST e agregado `Part`.                        | Peças impactam estoque, orçamento e execução do serviço.                    | `PartsController`, `Part`, use cases de estoque.                    |
| Criar Ordem de Serviço           | Use case + agregado `ServiceOrder`.                 | A OS concentra status, itens, orçamento e datas importantes do atendimento. | `CreateServiceOrderUseCase`, `ServiceOrder`.                        |
| Gerar orçamento                  | Regra em `ServiceOrder` e orquestração em use case. | O total depende dos itens da OS e deve ser calculado de forma consistente.  | `ServiceOrder.generateBudget`, `GenerateServiceOrderBudgetUseCase`. |
| Aprovar orçamento                | Regra em `ServiceOrder` e orquestração em use case. | A aprovação muda a fase da OS e libera a continuidade do fluxo.             | `ApproveServiceOrderBudgetUseCase`.                                 |
| Acompanhar OS                    | Endpoint de tracking.                               | Cliente acompanha a OS sem acessar APIs administrativas.                    | `TrackServiceOrderUseCase`, `/api/v1/service-orders/tracking`.      |
| Monitorar tempo médio            | Use case de métrica.                                | O indicador usa OS com início e fim de execução registrados.                | `GetAverageServiceOrderExecutionTimeUseCase`.                       |
| Proteger APIs administrativas    | JWT e Spring Security.                              | Operações internas exigem autenticação e autorização.                       | `SecurityConfig`, `JwtAuthenticationFilter`, `JwtService`.          |

### Requisitos técnicos da solução

| Requisito técnico      | Decisão adotada                                                           | Justificativa                                                                        | Onde aparece                                               |
|------------------------|---------------------------------------------------------------------------|--------------------------------------------------------------------------------------|------------------------------------------------------------|
| Backend monolítico     | Uma aplicação Spring Boot.                                                | O MVP valida o fluxo da oficina sem a complexidade de uma arquitetura distribuída.   | `AutoCareHubApiApplication`, `Dockerfile`.                 |
| Arquitetura em camadas | `interfaces`, `application`, `domain`, `infrastructure`.                  | Separa entrada, orquestração, regra de negócio e detalhes técnicos.                  | `src/main/java/br/com/autocarehub`.                        |
| API REST               | Controllers versionados em `/api/v1`.                                     | Facilita consumo pelo frontend, pelo Swagger e pela avaliação local.                 | `interfaces/rest/controller`, `docs/openapi/openapi.yaml`. |
| Swagger/OpenAPI        | Contrato versionado e Swagger UI.                                         | Documenta endpoints e ajuda na validação manual da API.                              | `docs/openapi/openapi.yaml`, `/swagger-ui.html`.           |
| PostgreSQL             | Banco relacional principal.                                               | O domínio tem relações claras entre cliente, veículo, OS, serviços, peças e estoque. | `docker-compose.yml`, `application.yml`.                   |
| Dockerfile             | Imagem backend com runtime restrito.                                      | Reduz superfície do runtime e padroniza execução.                                    | `Dockerfile`.                                              |
| Docker Compose         | Backend, banco e frontend local.                                          | Facilita demonstração e validação local.                                             | `docker-compose.yml`.                                      |
| JWT                    | Bearer Token para APIs protegidas.                                        | Atende ao requisito de segurança sem depender de provedor externo.                   | `SecurityConfig`, `JwtService`.                            |
| Validação sensível     | `Document` e `Plate`.                                                     | Mantém regras de CPF/CNPJ e placa centralizadas no domínio.                          | `domain/valueobject`.                                      |
| Testes automatizados   | JUnit, Mockito, MockMvc e H2.                                             | Valida domínio, use cases, REST e segurança.                                         | `src/test/java`, `docs/TESTING.md`.                        |
| Cobertura mínima       | JaCoCo no `mvn verify`.                                                   | Garante cobertura acima da exigência mínima do projeto.                              | `pom.xml`, `target/site/jacoco`.                           |
| Vulnerabilidades       | Dependency-Check, npm audit, Semgrep, Gitleaks, Docker Scout e OWASP ZAP. | Registra riscos encontrados, tratados e aceitos.                                     | `docs/SECURITY_REPORT.md`.                                 |

## 4. Dúvidas técnicas e decisões tomadas

| Dúvida técnica                                          | Decisão                                                                                    | Motivo                                                                         |
|---------------------------------------------------------|--------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------|
| Como representar CPF/CNPJ?                              | Criar `Document` como value object.                                                        | Documento é uma regra sensível e aparece em cliente, tracking e criação de OS. |
| Como validar placa?                                     | Criar `Plate` como value object.                                                           | Placa precisa ser validada de forma consistente em veículo e acompanhamento.   |
| Como controlar status da OS?                            | Criar métodos de transição em `ServiceOrder`.                                              | Evita que controller ou use case alterem status livremente.                    |
| Quando gerar orçamento?                                 | Na criação da OS, quando solicitado, ou no endpoint específico de geração.                 | O MVP precisa permitir geração automática e também geração explícita.          |
| Quando reservar estoque?                                | Na geração do orçamento, quando a OS contém peças.                                         | A reserva evita prometer uma peça sem disponibilidade.                         |
| Quando baixar estoque?                                  | Após aprovação ou confirmação da reserva, conforme regra implementada no fluxo de estoque. | A baixa representa consumo ou compromisso efetivo da peça.                     |
| Como impedir estoque negativo?                          | Concentrar regras em `Part`.                                                               | O agregado protege disponibilidade, reserva e baixa.                           |
| Como separar API administrativa da consulta do cliente? | JWT para rotas administrativas e endpoint específico de tracking.                          | Cliente acompanha OS sem permissão administrativa.                             |
| Como calcular tempo médio?                              | Criar use case de consulta para a métrica.                                                 | A regra depende de várias OS e não pertence a uma única entidade isolada.      |
| Como documentar a API?                                  | Versionar o OpenAPI em `docs/openapi/openapi.yaml`.                                        | O contrato documenta endpoints e apoia geração de interfaces.                  |
| Como rodar localmente com banco?                        | Usar Docker Compose com backend, PostgreSQL e frontend.                                    | Facilita avaliação local sem instalação manual do banco.                       |
| Como proteger senha?                                    | Usar BCrypt via `PasswordEncoder`.                                                         | Senhas não ficam em texto puro.                                                |

## 5. Spikes e validações técnicas

As validações foram feitas no próprio projeto, sem necessidade de POCs separadas em outro repositório.

| Validação técnica                 | Objetivo                                                                          | Resultado                                               | Evidência                                                    |
|-----------------------------------|-----------------------------------------------------------------------------------|---------------------------------------------------------|--------------------------------------------------------------|
| Execução local com Docker Compose | Confirmar que backend, PostgreSQL e frontend sobem juntos.                        | Configuração válida.                                    | `docker-compose.yml`, `Dockerfile`, `frontend/Dockerfile`.   |
| Conexão com PostgreSQL            | Garantir persistência relacional para o MVP.                                      | Backend usa JDBC/JPA com variáveis de ambiente.         | `application.yml`, `docker-compose.yml`.                     |
| Migrations Flyway                 | Garantir schema reproduzível.                                                     | Migration baseline aplicada em testes e execução local. | `src/main/resources/db/migration`.                           |
| Swagger/OpenAPI                   | Confirmar documentação e teste manual da API.                                     | Swagger exposto em `/swagger-ui.html`.                  | `docs/openapi/openapi.yaml`, `SecurityConfig`.               |
| Autenticação JWT                  | Confirmar login e proteção das rotas administrativas.                             | Login retorna token e filtros validam Bearer Token.     | `LoginUseCase`, `JwtAuthenticationFilter`.                   |
| Fluxo completo da OS              | Validar criação, orçamento, aprovação, execução, finalização, entrega e tracking. | Fluxo coberto por teste de API.                         | `ServiceOrderFlowIntegrationTest`.                           |
| Cálculo de orçamento              | Validar soma de serviços e peças.                                                 | Regra testada em domínio e use cases.                   | `ServiceOrderTest`, `GenerateServiceOrderBudgetUseCaseTest`. |
| Controle de estoque               | Validar reserva, baixa e bloqueio de saldo inválido.                              | Regras concentradas em `Part`.                          | `PartTest`, `PartStockFlowIntegrationTest`.                  |
| Cobertura JaCoCo                  | Garantir cobertura acima do mínimo.                                               | `mvn verify` passa no gate de cobertura.                | `target/site/jacoco`.                                        |
| Scans de segurança                | Consolidar vulnerabilidades e riscos aceitos.                                     | Resultado documentado separadamente.                    | `docs/SECURITY_REPORT.md`.                                   |

## 6. Arquitetura da solução

O AutoCare Hub usa um backend monolítico em camadas:

- `interfaces`: controllers REST, mappers REST, exceptions HTTP e adaptação do contrato OpenAPI;
- `application`: use cases, comandos, consultas e portas de repositório;
- `domain`: entidades, agregados, value objects, enums e exceções de domínio;
- `infrastructure`: JPA, adapters de persistência, segurança JWT, configuração e integração com bibliotecas.

Essa separação permite que o domínio da oficina fique protegido de detalhes como HTTP, JPA, Docker e Swagger.

A arquitetura C4 está documentada em:

```text
docs/ARCHITECTURE.md
```

## 7. Tecnologias e ferramentas utilizadas

| Tecnologia/ferramenta      | Uso no AutoCare Hub                               |
|----------------------------|---------------------------------------------------|
| Java 21                    | Linguagem do backend.                             |
| Spring Boot                | Base da aplicação monolítica.                     |
| Spring Web MVC             | Exposição da API REST.                            |
| Spring Security            | Proteção das rotas e integração do filtro JWT.    |
| JJWT                       | Emissão e validação de tokens JWT.                |
| BCrypt                     | Hash de senhas.                                   |
| Spring Data JPA/Hibernate  | Persistência relacional.                          |
| PostgreSQL 16              | Banco principal em execução local.                |
| H2                         | Banco em memória para testes automatizados.       |
| Flyway                     | Versionamento do schema.                          |
| OpenAPI Generator          | Geração de interfaces REST a partir do contrato.  |
| Springdoc/Swagger UI       | Documentação navegável da API.                    |
| Docker e Docker Compose    | Execução local reproduzível.                      |
| JUnit 5, Mockito e MockMvc | Testes unitários, de aplicação e integração REST. |
| JaCoCo                     | Cobertura e gate de qualidade.                    |
| OWASP Dependency-Check     | Scan de dependências backend.                     |
| npm audit                  | Scan de dependências frontend.                    |
| Docker Scout               | Scan das imagens Docker.                          |
| Semgrep                    | Análise estática de segurança.                    |
| Gitleaks                   | Verificação de secrets no histórico Git.          |
| OWASP ZAP                  | Scan dinâmico complementar da API local.          |
| Vue/Vite                   | Frontend demonstrativo.                           |

## 8. Integrações e dependências

Integrações reais do MVP:

- PostgreSQL, usado pelo backend via JPA/JDBC;
- frontend demonstrativo Vue/Vite, que consome a API REST;
- Swagger UI, servido pela própria aplicação para teste manual da API;
- Docker Compose, usado para orquestrar backend, banco e frontend localmente.

O MVP não depende de integrações externas reais. Não há pagamento online, WhatsApp, e-mail, SMS, fornecedor externo, ERP, API Gateway, Kafka, mensageria ou cloud produtiva no código.

## 9. Estratégia de implementação

A implementação foi organizada a partir do fluxo central da oficina:

1. Modelar clientes, veículos, serviços, peças e OS no domínio.
2. Criar value objects para dados sensíveis e regras repetidas.
3. Implementar use cases para cada operação da API.
4. Criar adapters JPA para persistência.
5. Expor controllers REST a partir do contrato OpenAPI.
6. Proteger operações administrativas com JWT.
7. Cobrir regras críticas com testes unitários e fluxos REST com testes de integração.
8. Empacotar e rodar localmente com Docker Compose.
9. Consolidar arquitetura, DDD, testes, segurança e entrega nos documentos.

Essa estratégia mantém o MVP simples e rastreável: cada requisito obrigatório tem um ponto técnico claro no código ou na documentação.

## 10. Riscos técnicos tratados

| Risco                          | Tratamento aplicado                                                  |
|--------------------------------|----------------------------------------------------------------------|
| Ambiente difícil de reproduzir | Docker Compose e `.env.example`.                                     |
| API difícil de entender        | Swagger/OpenAPI versionado.                                          |
| Dados inválidos                | `Document`, `Plate` e validações de request.                         |
| Acesso indevido                | JWT nas APIs administrativas e regras no `SecurityConfig`.           |
| Senhas expostas                | BCrypt em criação e troca de senha.                                  |
| Estoque inconsistente          | Regras de disponibilidade, reserva e baixa protegidas em `Part`.     |
| Status inconsistente da OS     | Transições controladas em `ServiceOrder`.                            |
| Orçamento incorreto            | Cálculo centralizado em `ServiceOrder` e testes de domínio/use case. |
| Banco inconsistente            | Migration Flyway versionada.                                         |
| Regressão em fluxo crítico     | Testes unitários e integração REST.                                  |
| Dependências vulneráveis       | Scans e relatório de vulnerabilidades.                               |
| Secrets versionados            | `.gitignore`, `.env.example` sem secrets e Gitleaks.                 |

## 11. Itens fora do escopo do MVP

Os itens abaixo não são pendências da entrega; ficaram fora do escopo para manter o MVP focado no backend da oficina:

- microserviços;
- API Gateway;
- mensageria;
- Kafka ou filas;
- pagamento online;
- integração com WhatsApp, e-mail ou SMS;
- integração com fornecedores;
- integração com ERP;
- aplicativo mobile real;
- autenticação OAuth2 externa;
- deploy produtivo em cloud;
- observabilidade avançada com tracing distribuído.

## 12. Pendências identificadas

Não há pendência obrigatória aberta neste refinamento técnico.

Os pontos abaixo são apenas melhorias futuras, caso o MVP evolua para um produto real:

- publicar o frontend e o backend em ambiente cloud;
- criar pipeline CI/CD com os scans automatizados;
- adicionar observabilidade produtiva com métricas, logs estruturados e tracing;
- integrar canais reais de comunicação com o cliente;
- integrar pagamento, fornecedores ou ERP;
- separar módulos ou serviços apenas se a escala justificar.

Esses pontos não bloqueiam a entrega do Tech Challenge, porque não fazem parte dos requisitos obrigatórios desta fase.

## 13. Conclusão

O refinamento técnico está aderente ao escopo da FIAP porque mostra como a jornada da oficina virou uma solução implementada em backend monolítico, com camadas claras, domínio protegido, persistência relacional, autenticação, contrato OpenAPI, execução local e validação automatizada.

Não foi identificada necessidade de refatoração estrutural no backend nesta revisão. A principal melhoria foi documentar melhor as decisões e a rastreabilidade entre negócio, código e arquitetura.
