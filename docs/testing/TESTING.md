# Estratégia de Testes - AutoCare Hub

## 1. Objetivo

Este documento explica como os testes automatizados do AutoCare Hub foram organizados para validar os fluxos críticos do
MVP.

A proposta não é comprovar um histórico formal de TDD por commits, mas demonstrar que os principais comportamentos do
domínio foram transformados em testes de sucesso e de falha. A estratégia cobre regras de negócio, casos de uso,
segurança, integração REST e o fluxo principal da Ordem de Serviço.

## 2. Escopo dos testes

A suíte cobre:

- regras de domínio de cliente, veículo, serviço, peça/insumo, estoque e Ordem de Serviço;
- casos de uso da aplicação, com repositories isolados por dublês em memória ou mocks quando necessário;
- autenticação e autorização JWT;
- controllers REST com `MockMvc`;
- validações de CPF/CNPJ e placa;
- fluxo principal da OS, incluindo criação, orçamento, aprovação, recusa externa, status, finalização, entrega e acompanhamento;
- listagem operacional de OS ordenada por prioridade/status e data, ocultando finalizadas e entregues;
- geração de relatório JaCoCo e gate de cobertura.

O build padrão não usa REST-assured, Cucumber nem Testcontainers. Os testes de integração usam Spring Boot Test,
MockMvc, H2 em memória e Flyway.

Há um profile opcional para geração de relatório Allure local. Esse relatório é apenas uma evidência complementar e não
faz parte do gate obrigatório da entrega.

## 3. Pirâmide de testes aplicada ao projeto

| Nível                 | Como aparece no projeto                                         | O que valida                                                                                      |
|-----------------------|-----------------------------------------------------------------|---------------------------------------------------------------------------------------------------|
| Unitários             | Classes em `domain`, `application` e `infrastructure/security`. | Regras isoladas, cenários negativos, cálculo de valores, status, estoque e autorização.           |
| Integração            | Classes REST com `@SpringBootTest` e `@AutoConfigureMockMvc`.   | API REST, serialização JSON, validações HTTP, filtros de segurança, banco H2 e migrations Flyway. |
| Fluxo completo de API | `ServiceOrderFlowIntegrationTest`.                              | Jornada principal da Ordem de Serviço usando a aplicação Spring em teste.                         |

Essa divisão mantém testes rápidos para regras de domínio e testes mais completos para provar que as camadas funcionam
juntas.

## 4. Ferramentas utilizadas

| Ferramenta           | Uso                                                                                                  |
|----------------------|------------------------------------------------------------------------------------------------------|
| JUnit 5              | Execução dos testes automatizados.                                                                   |
| AssertJ              | Asserções legíveis em testes unitários e de aplicação.                                               |
| Mockito              | Dublês em pontos específicos, principalmente quando há dependência externa ao comportamento testado. |
| Spring Boot Test     | Inicialização do contexto para testes de integração.                                                 |
| MockMvc              | Chamadas HTTP simuladas contra controllers REST.                                                     |
| Spring Security Test | Apoio à validação de segurança e autorização.                                                        |
| H2                   | Banco em memória para testes de integração.                                                          |
| Flyway               | Validação das migrations no ambiente de teste.                                                       |
| JaCoCo               | Relatório e gate de cobertura.                                                                       |
| Maven Surefire       | Execução padrão dos testes com `mvn test` e `mvn verify`.                                            |
| Allure               | Relatório navegável opcional, fora do gate obrigatório.                                              |

Não há Maven Failsafe nem profiles separados para integração/E2E. Para este MVP, `mvn verify` continua sendo o comando
principal, porque executa a suíte completa e o gate de cobertura sem exigir comandos diferentes.

## 5. Ajustes de warnings no ambiente de teste

A configuração de testes foi ajustada para evitar warnings que poderiam esconder regressões reais:

- H2 fixado em versão compatível com a versão verificada pelo Flyway usada no projeto;
- Mockito carregado como `javaagent` no Surefire, evitando auto-attach dinâmico no JDK;
- `spring.jpa.open-in-view=false` também no profile de teste;
- relatório JaCoCo movido para `verify`, evitando bloqueio de arquivo no Windows durante execuções locais de teste;
- modelo OpenAPI gerado pós-processado para remover `@Valid` diretamente de `List`, mantendo validação nos itens da
  lista e nos objetos aninhados.

## 6. Testes unitários

Principais classes:

| Área              | Testes                                                                                                                                                                                                                                      |
|-------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Cliente           | `CustomerTest`, `CreateCustomerUseCaseTest`                                                                                                                                                                                                 |
| Veículo           | `VehicleTest`                                                                                                                                                                                                                               |
| Serviços          | `WorkshopServiceTest`                                                                                                                                                                                                                       |
| Peças e estoque   | `PartTest`, `RegisterPartStockMovementUseCaseTest`                                                                                                                                                                                          |
| Ordem de Serviço  | `ServiceOrderTest`, `CreateServiceOrderUseCaseTest`, `GenerateServiceOrderBudgetUseCaseTest`, `ApproveServiceOrderBudgetUseCaseTest`, `UpdateServiceOrderStatusUseCaseTest`, `TrackServiceOrderUseCaseTest`, `ListServiceOrdersUseCaseTest` |
| Segurança         | `LoginUseCaseTest`, `JwtServiceTest`, `JwtAuthenticationFilterTest`, `AuthorizationServiceTest`                                                                                                                                             |
| Mappers e suporte | `UserJpaMapperTest`, `CustomerRestMapperTest`, `ServiceOrderRestMapperTest`, `RestMapperSupportTest`                                                                                                                                        |

Exemplos de comportamentos cobertos:

- documento inválido é rejeitado;
- placa inválida é rejeitada;
- alteração de placa, marca, modelo ou ano de veículo existente é rejeitada;
- OS nasce com status inicial correto;
- orçamento soma serviços e peças;
- aprovação e recusa externa exigem orçamento válido;
- recusa externa libera reservas e retorna a OS para diagnóstico;
- fila operacional de OS exclui finalizadas/entregues e ordena por prioridade e data;
- execução e entrega respeitam transições de status;
- estoque não pode ficar negativo;
- reserva não pode exceder disponibilidade;
- login administrativo delega autenticação por porta de aplicação;
- JWT inválido ou inconsistente é rejeitado.

## 7. Testes de integração

Os testes REST usam `@SpringBootTest`, `@AutoConfigureMockMvc`, H2 em memória e migrations Flyway.

Principais classes:

| Teste                                    | Cobertura principal                                                                                  |
|------------------------------------------|------------------------------------------------------------------------------------------------------|
| `AdministrativeCrudIntegrationTest`      | CRUD administrativo de clientes, veículos, serviços, peças, usuários e criação/vínculo de empresa.   |
| `SecurityAuthorizationIntegrationTest`   | Login, endpoints protegidos, token válido, acessos negados e escopo de usuários por empresa.         |
| `SensitiveDataValidationIntegrationTest` | Rejeição HTTP para CPF/CNPJ, placa e payload de OS inválidos.                                        |
| `PartStockFlowIntegrationTest`           | Movimentação de estoque pela API.                                                                    |
| `ServiceOrderFlowIntegrationTest`        | Fluxo completo da OS pela API, consulta de status, tracking, entrega e decisão externa de orçamento. |
| `UserCompanyManagementIntegrationTest`   | Criação de usuários por perfil, vínculo com empresas e restrições de escopo.                         |

Esses testes validam controller, DTO, mapper, segurança, use case, persistência e migrations no mesmo fluxo.

## 8. Teste de fluxo completo da API

O projeto não usa um runner E2E externo. A validação ponta a ponta do MVP é feita por teste de integração de API com
MockMvc.

O fluxo principal coberto em `ServiceOrderFlowIntegrationTest` valida:

1. login administrativo;
2. criação de cliente;
3. criação de veículo;
4. criação de serviço;
5. criação de peça com estoque;
6. criação da Ordem de Serviço;
7. inclusão de serviço;
8. inclusão de peça;
9. geração de orçamento;
10. aprovação do orçamento;
11. início de diagnóstico;
12. início de execução;
13. finalização;
14. entrega;
15. acompanhamento da OS pelo cliente;
16. consulta do tempo médio de execução.

Para o escopo acadêmico, esse teste funciona como teste de sistema da API, porque exercita a aplicação Spring com banco
em memória e chamadas HTTP simuladas.

## 9. Cenários críticos cobertos

| Requisito crítico                   | Cobertura de teste                                                                             |
|-------------------------------------|------------------------------------------------------------------------------------------------|
| CRUD de clientes                    | `AdministrativeCrudIntegrationTest`, `CustomerTest`, `CreateCustomerUseCaseTest`               |
| CRUD de veículos                    | `AdministrativeCrudIntegrationTest`, `VehicleTest`; update preserva placa, marca, modelo e ano |
| CRUD de serviços                    | `AdministrativeCrudIntegrationTest`, `WorkshopServiceTest`                                     |
| CRUD de peças/insumos               | `AdministrativeCrudIntegrationTest`, `PartTest`                                                |
| Controle de estoque                 | `PartTest`, `RegisterPartStockMovementUseCaseTest`, `PartStockFlowIntegrationTest`             |
| Criação da OS                       | `CreateServiceOrderUseCaseTest`, `ServiceOrderFlowIntegrationTest`                             |
| Geração de orçamento                | `GenerateServiceOrderBudgetUseCaseTest`, `ServiceOrderTest`, `ServiceOrderFlowIntegrationTest` |
| Aprovação/recusa de orçamento       | `ApproveServiceOrderBudgetUseCaseTest`, `ServiceOrderFlowIntegrationTest`                      |
| Listagem ordenada de OS             | `ListServiceOrdersUseCaseTest`                                                                 |
| Atualização externa de status       | `UpdateServiceOrderStatusUseCaseTest`, `ServiceOrderFlowIntegrationTest`                       |
| Status da OS                        | `ServiceOrderTest`, `UpdateServiceOrderStatusUseCaseTest`, `ServiceOrderFlowIntegrationTest`   |
| Acompanhamento pelo cliente         | `TrackServiceOrderUseCaseTest`, `ServiceOrderFlowIntegrationTest`                              |
| Tempo médio de execução             | `ApplicationUseCaseAdditionalCoverageTest`, `ServiceOrderFlowIntegrationTest`                  |
| JWT e autorização                   | `JwtServiceTest`, `JwtAuthenticationFilterTest`, `SecurityAuthorizationIntegrationTest`        |
| Login administrativo                | `LoginUseCaseTest`, `SecurityAuthorizationIntegrationTest`                                     |
| Criação de empresa por Admin Master | `AdministrativeCrudIntegrationTest`                                                            |
| Escopo de usuários por empresa      | `SecurityAuthorizationIntegrationTest`                                                         |
| CPF/CNPJ e placa                    | `CustomerTest`, `VehicleTest`, `SensitiveDataValidationIntegrationTest`                        |
| Payload inválido                    | `SensitiveDataValidationIntegrationTest`                                                       |

## 10. Cobertura JaCoCo

Resultado revalidado com `mvn clean verify`: o gate JaCoCo passou.

O gate interno é mais rígido que a exigência mínima de 80% para os domínios críticos. O JaCoCo exclui a classe principal
da aplicação, código gerado pelo OpenAPI e records internos de comandos, queries e outputs sem lógica própria, para
medir melhor o código escrito no projeto.

## 11. Como executar os testes

Execução rápida da suíte:

```powershell
cd backend
mvn test
```

Execução completa com cobertura e empacotamento:

```powershell
cd backend
mvn clean verify
```

Relatório de cobertura:

```text
backend/target/site/jacoco/index.html
backend/target/site/jacoco/jacoco.csv
backend/target/site/jacoco/jacoco.xml
```

Relatórios de testes:

```text
backend/target/surefire-reports/
```

Relatório Allure opcional:

```powershell
cd backend
mvn -Pallure-report clean verify allure:report
```

Saída esperada do relatório opcional:

```text
backend/target/site/allure-maven-plugin/index.html
```

## 12. Evidências

Última execução registrada nesta revisão:

| Comando                                                              | Resultado                                                           |
|----------------------------------------------------------------------|---------------------------------------------------------------------|
| `mvn test`                                                           | 172 testes, 0 falhas, 0 erros e 0 ignorados.                        |
| `mvn clean verify`                                                   | 172 testes, 0 falhas, 0 erros, 0 ignorados e gate JaCoCo aprovado.  |
| `mvn dependency-check:check -DautoUpdate=false`                      | Evidência versionada descrita em `docs/security/SECURITY_REPORT.md`. |
| `mvn -Pallure-report -DskipTests compile test-compile allure:report` | Relatório Allure gerado a partir dos resultados locais disponíveis. |

Não há `backend/target/failsafe-reports/`, porque o projeto não usa Maven Failsafe.

O Allure fica isolado em profile opcional para não alterar o caminho padrão de build, testes e scans.

## 13. Pontos fora do gate obrigatório

Os itens abaixo não fazem parte do gate obrigatório desta entrega:

| Item           | Motivo                                                                                                                        |
|----------------|-------------------------------------------------------------------------------------------------------------------------------|
| REST-assured   | O projeto usa MockMvc para validar a API no contexto Spring.                                                                  |
| Cucumber       | Não foi necessário para o MVP, porque os fluxos críticos já estão cobertos por testes de domínio, use case e integração REST. |
| Testcontainers | A suíte usa H2 em memória e Flyway, mantendo execução simples e rápida.                                                       |
| Maven Failsafe | A suíte completa roda pelo Surefire dentro de `mvn test` e `mvn verify`.                                                      |
| Allure         | É evidência navegável opcional, não requisito para aprovação do build.                                                        |

Esses itens podem ser adicionados em ciclos futuros, mas não são pendências do Tech Challenge.

## 14. Pendências identificadas

Não há pendência obrigatória aberta na estratégia de testes.

Os pontos abaixo são apenas melhorias futuras:

- separar testes de integração em profile próprio se a suíte crescer muito;
- adicionar REST-assured se houver necessidade de validar a API contra ambiente externo ao Spring Test;
- adicionar Testcontainers se o projeto passar a exigir compatibilidade mais fiel com PostgreSQL nos testes
  automatizados;
- adicionar relatórios Allure ao pipeline, caso a entrega evolua para CI/CD.

Essas melhorias não bloqueiam a entrega, porque a suíte atual já cobre os fluxos obrigatórios, passa no `mvn verify` e
supera o gate mínimo de cobertura.

## 15. Conclusão

A estratégia de testes está aderente ao Tech Challenge: há testes unitários para regras críticas, testes com Mockito
onde há dependências a isolar, testes de integração REST com Spring Boot e MockMvc, além de um fluxo completo de API que
valida a jornada principal da Ordem de Serviço.

Nesta revisão foram adicionados e ajustados testes para endpoints explícitos de aprovação/recusa externa de orçamento,
consulta de status da OS, tracking do cliente, entrega da OS, rejeição de payload inválido e listagem operacional
priorizada via API. A alteração manteve o gate interno de cobertura e fez o `mvn clean verify` passar sem reduzir a
exigência configurada.
