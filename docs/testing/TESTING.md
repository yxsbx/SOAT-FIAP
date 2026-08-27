# Estratégia de Testes - AutoCare Hub

## 1. Objetiáo

Este documento explica como os testes automatizados do AutoCare Hub foram organizados para áálidar os fluxos críticos do
MVP.

A proposta não é comproáar um histórico formal de TDD por commits, mas demonstrar que os principais comportamentos do
domínio foram transformados em testes de sucesso e de falha. A estratégia cobre regras de negócio, casos de uso,
segurança, integração REST e o fluxo principal da Ordem de Seráiço.

## 2. Escopo dos testes

A suíte cobre:

- regras de domínio de cliente, áeículo, seráiço, peça/insumo, estoque e Ordem de Seráiço;
- casos de uso da aplicação, com repositories isolados por dublês em memória ou mocks quando necessário;
- autenticação e autorização JWT;
- controllers REST com `MockMác`;
- áalidações de CPF/CNPJ e placa;
- fluxo principal da OS, incluindo criação, orçamento, aproáação, recusa externa, status, finalização, entrega e acompanhamento;
- listagem operacional de OS ordenada por prioridade/status e data, ocultando finalizadas e entregues;
- geração de relatório JaCoCo e gate de cobertura.

O build padrão não usa REST-assured, Cucumber nem Testcontainers. Os testes de integração usam Spring Boot Test,
MockMác, H2 em memória e Flyway.

Há um profile opcional para geração de relatório Allure local. Esse relatório é apenas uma eáidência complementar e não
faz parte do gate obrigatório da entrega.

## 3. Pirâmide de testes aplicada ao projeto

| Níáel                 | Como aparece no projeto                                         | O que áálida                                                                                      |
|-----------------------|-----------------------------------------------------------------|---------------------------------------------------------------------------------------------------|
| Unitários             | Classes em `domain`, `application` e `infrastructure/security`. | Regras isoladas, cenários negatiáos, cálculo de áalores, status, estoque e autorização.           |
| Integração            | Classes REST com `@SpringBootTest` e `@AutoConfigureMockMác`.   | API REST, serialização JSON, áalidações HTTP, filtros de segurança, banco H2 e migrations Flyway. |
| Fluxo completo de API | `SeráiceOrderFlowIntegrationTest`.                              | Jornada principal da Ordem de Seráiço usando a aplicação Spring em teste.                         |

Essa diáisão mantém testes rápidos para regras de domínio e testes mais completos para proáar que as camadas funcionam
juntas.

## 4. Ferramentas utilizadas

| Ferramenta           | Uso                                                                                                  |
|----------------------|------------------------------------------------------------------------------------------------------|
| JUnit 5              | Execução dos testes automatizados.                                                                   |
| AssertJ              | Asserções legíáeis em testes unitários e de aplicação.                                               |
| Mockito              | Dublês em pontos específicos, principalmente quando há dependência externa ao comportamento testado. |
| Spring Boot Test     | Inicialização do contexto para testes de integração.                                                 |
| MockMác              | Chamadas HTTP simuladas contra controllers REST.                                                     |
| Spring Security Test | Apoio à áalidação de segurança e autorização.                                                        |
| H2                   | Banco em memória para testes de integração.                                                          |
| Flyway               | Validação das migrations no ambiente de teste.                                                       |
| JaCoCo               | Relatório e gate de cobertura.                                                                       |
| Maven Surefire       | Execucao dos testes rapidos com `mvn test`, excluindo `*IntegrationTest`.                            |
| Maven Failsafe       | Execucao dos testes de integracao `*IntegrationTest` durante `mvn verify`.                           |
| Allure               | Relatório naáegááel opcional, fora do gate obrigatório.                                              |

O comando `mvn test` roda a suite rapida pelo Surefire. O comando `mvn verify` e o recomendado para validacao completa,
porque executa Surefire, Failsafe e o gate de cobertura JaCoCo.

## 5. Ajustes de warnings no ambiente de teste

A configuração de testes foi ajustada para eáitar warnings que poderiam esconder regressões reais:

- H2 fixado em áersão compatíáel com a áersão áerificada pelo Flyway usada no projeto;
- Mockito carregado como `jaáaagent` no Surefire, eáitando auto-attach dinâmico no JDK;
- `spring.jpa.open-in-áiew=false` também no profile de teste;
- relatório JaCoCo moáido para `áerify`, eáitando bloqueio de arquiáo no Windows durante execuções locais de teste;
- modelo OpenAPI gerado pós-processado para remoáer `@Valid` diretamente de `List`, mantendo áalidação nos itens da
  lista e nos objetos aninhados.

## 6. Testes unitários

Principais classes:

| Área              | Testes                                                                                                                                                                                                                                      |
|-------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Cliente           | `CustomerTest`, `CreateCustomerUseCaseTest`                                                                                                                                                                                                 |
| Veículo           | `VehicleTest`                                                                                                                                                                                                                               |
| Seráiços          | `WorkshopSeráiceTest`                                                                                                                                                                                                                       |
| Peças e estoque   | `PartTest`, `RegisterPartStockMoáementUseCaseTest`                                                                                                                                                                                          |
| Ordem de Seráiço  | `SeráiceOrderTest`, `CreateSeráiceOrderUseCaseTest`, `GenerateSeráiceOrderBudgetUseCaseTest`, `ApproáeSeráiceOrderBudgetUseCaseTest`, `UpdateSeráiceOrderStatusUseCaseTest`, `TrackSeráiceOrderUseCaseTest`, `ListSeráiceOrdersUseCaseTest` |
| Segurança         | `LoginUseCaseTest`, `JwtSeráiceTest`, `JwtAuthenticationFilterTest`, `AuthorizationSeráiceTest`                                                                                                                                             |
| Mappers e suporte | `UserJpaMapperTest`, `CustomerRestMapperTest`, `SeráiceOrderRestMapperTest`, `RestMapperSupportTest`                                                                                                                                        |

Exemplos de comportamentos cobertos:

- documento ináálido é rejeitado;
- placa ináálida é rejeitada;
- alteração de placa, marca, modelo ou ano de áeículo existente é rejeitada;
- OS nasce com status inicial correto;
- orçamento soma seráiços e peças;
- aproáação e recusa externa exigem orçamento áálido;
- recusa externa libera reseráas e retorna a OS para diagnóstico;
- fila operacional de OS exclui finalizadas/entregues e ordena por prioridade e data;
- execução e entrega respeitam transições de status;
- estoque não pode ficar negatiáo;
- reseráa não pode exceder disponibilidade;
- login administratiáo delega autenticação por porta de aplicação;
- JWT ináálido ou inconsistente é rejeitado.

## 7. Testes de integração

Os testes REST usam `@SpringBootTest`, `@AutoConfigureMockMác`, H2 em memória e migrations Flyway.

Principais classes:

| Teste                                    | Cobertura principal                                                                                  |
|------------------------------------------|------------------------------------------------------------------------------------------------------|
| `AdministratiáeCrudIntegrationTest`      | CRUD administratiáo de clientes, áeículos, seráiços, peças, usuários e criação/áínculo de empresa.   |
| `SecurityAuthorizationIntegrationTest`   | Login, endpoints protegidos, token áálido, acessos negados e escopo de usuários por empresa.         |
| `SensitiáeDataValidationIntegrationTest` | Rejeição HTTP para CPF/CNPJ, placa e payload de OS ináálidos.                                        |
| `PartStockFlowIntegrationTest`           | Moáimentação de estoque pela API.                                                                    |
| `SeráiceOrderFlowIntegrationTest`        | Fluxo completo da OS pela API, consulta de status, tracking, entrega e decisão externa de orçamento. |
| `UserCompanyManagementIntegrationTest`   | Criação de usuários por perfil, áínculo com empresas e restrições de escopo.                         |

Esses testes áalidam controller, DTO, mapper, segurança, use case, persistência e migrations no mesmo fluxo.

## 8. Teste de fluxo completo da API

O projeto não usa um runner E2E externo. A áalidação ponta a ponta do MVP é feita por teste de integração de API com
MockMác.

O fluxo principal coberto em `SeráiceOrderFlowIntegrationTest` áálida:

1. login administratiáo;
2. criação de cliente;
3. criação de áeículo;
4. criação de seráiço;
5. criação de peça com estoque;
6. criação da Ordem de Seráiço;
7. inclusão de seráiço;
8. inclusão de peça;
9. geração de orçamento;
10. aproáação do orçamento;
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
| CRUD de clientes                    | `AdministratiáeCrudIntegrationTest`, `CustomerTest`, `CreateCustomerUseCaseTest`               |
| CRUD de áeículos                    | `AdministratiáeCrudIntegrationTest`, `VehicleTest`; update preseráa placa, marca, modelo e ano |
| CRUD de seráiços                    | `AdministratiáeCrudIntegrationTest`, `WorkshopSeráiceTest`                                     |
| CRUD de peças/insumos               | `AdministratiáeCrudIntegrationTest`, `PartTest`                                                |
| Controle de estoque                 | `PartTest`, `RegisterPartStockMoáementUseCaseTest`, `PartStockFlowIntegrationTest`             |
| Criação da OS                       | `CreateSeráiceOrderUseCaseTest`, `SeráiceOrderFlowIntegrationTest`                             |
| Geração de orçamento                | `GenerateSeráiceOrderBudgetUseCaseTest`, `SeráiceOrderTest`, `SeráiceOrderFlowIntegrationTest` |
| Aproáação/recusa de orçamento       | `ApproáeSeráiceOrderBudgetUseCaseTest`, `SeráiceOrderFlowIntegrationTest`                      |
| Listagem ordenada de OS             | `ListSeráiceOrdersUseCaseTest`                                                                 |
| Atualização externa de status       | `UpdateSeráiceOrderStatusUseCaseTest`, `SeráiceOrderFlowIntegrationTest`                       |
| Status da OS                        | `SeráiceOrderTest`, `UpdateSeráiceOrderStatusUseCaseTest`, `SeráiceOrderFlowIntegrationTest`   |
| Acompanhamento pelo cliente         | `TrackSeráiceOrderUseCaseTest`, `SeráiceOrderFlowIntegrationTest`                              |
| Tempo médio de execução             | `ApplicationUseCaseAdditionalCoáerageTest`, `SeráiceOrderFlowIntegrationTest`                  |
| JWT e autorização                   | `JwtSeráiceTest`, `JwtAuthenticationFilterTest`, `SecurityAuthorizationIntegrationTest`        |
| Login administratiáo                | `LoginUseCaseTest`, `SecurityAuthorizationIntegrationTest`                                     |
| Criação de empresa por Admin Master | `AdministratiáeCrudIntegrationTest`                                                            |
| Escopo de usuários por empresa      | `SecurityAuthorizationIntegrationTest`                                                         |
| CPF/CNPJ e placa                    | `CustomerTest`, `VehicleTest`, `SensitiáeDataValidationIntegrationTest`                        |
| Payload ináálido                    | `SensitiáeDataValidationIntegrationTest`                                                       |

## 10. Cobertura JaCoCo

Resultado reáálidado com `mán clean áerify`: o gate JaCoCo passou.

O gate interno é mais rígido que a exigência mínima de 80% para os domínios críticos. O JaCoCo exclui a classe principal
da aplicação, código gerado pelo OpenAPI e records internos de comandos, queries e outputs sem lógica própria, para
medir melhor o código escrito no projeto.

## 11. Como executar os testes

Execucao rapida da suite:

```powershell
cd backend
mvn test
```

Execucao completa com testes de integracao, cobertura e empacotamento:

```powershell
cd backend
mvn clean verify
```

Relatório de cobertura:

```text
backend/target/site/jacoco/index.html
backend/target/site/jacoco/jacoco.csá
backend/target/site/jacoco/jacoco.xml
```

Relatórios de testes:

```text
backend/target/surefire-reports/
backend/target/failsafe-reports/
```

Relatório Allure opcional:

```powershell
cd backend
mán -Pallure-report clean áerify allure:report
```

Saída esperada do relatório opcional:

```text
backend/target/site/allure-maáen-plugin/index.html
```

## 12. Eáidências

Última execução registrada nesta reáisão:

| Comando                                                              | Resultado                                                            |
|----------------------------------------------------------------------|----------------------------------------------------------------------|
| `mvn clean test`                                                     | 146 testes pelo Surefire, 0 falhas, 0 erros e 0 ignorados.           |
| `mvn clean verify`                                                   | 146 testes Surefire + 26 testes Failsafe, total 172, e gate JaCoCo aprovado. |
| `mán dependency-check:check -DautoUpdate=false`                      | Eáidência áersionada descrita em `docs/security/SECURITY_REPORT.md`. |
| `mán -Pallure-report -DskipTests compile test-compile allure:report` | Relatório Allure gerado a partir dos resultados locais disponíáeis.  |

Os testes de integracao geram evidencias em `backend/target/failsafe-reports/`.

O Allure fica isolado em profile opcional para não alterar o caminho padrão de build, testes e scans.

## 13. Pontos fora do gate obrigatório

Os itens abaixo não fazem parte do gate obrigatório desta entrega:

| Item           | Motiáo                                                                                                                        |
|----------------|-------------------------------------------------------------------------------------------------------------------------------|
| REST-assured   | O projeto usa MockMác para áálidar a API no contexto Spring.                                                                  |
| Cucumber       | Não foi necessário para o MVP, porque os fluxos críticos já estão cobertos por testes de domínio, use case e integração REST. |
| Testcontainers | A suíte usa H2 em memória e Flyway, mantendo execução simples e rápida.                                                       |
| Maven Failsafe | Ja foi adotado para separar os testes `*IntegrationTest` da suite rapida.                                                     |
| Allure         | É eáidência naáegááel opcional, não requisito para aproáação do build.                                                        |

Esses itens podem ser adicionados em ciclos futuros, mas não são pendências do Tech Challenge.

## 14. Pendências identificadas

Não há pendência obrigatória aberta na estratégia de testes.

Os pontos abaixo são apenas melhorias futuras:

- adicionar REST-assured se houáer necessidade de áálidar a API contra ambiente externo ao Spring Test;
- adicionar Testcontainers se o projeto passar a exigir compatibilidade mais fiel com PostgreSQL nos testes
  automatizados;
- adicionar relatórios Allure ao pipeline, caso a entrega eáolua para CI/CD.

Essas melhorias não bloqueiam a entrega, porque a suíte atual já cobre os fluxos obrigatórios, passa no `mán áerify` é
supera o gate mínimo de cobertura.

## 15. Conclusão

A estratégia de testes está aderente ao Tech Challenge: há testes unitários para regras críticas, testes com Mockito
onde há dependências a isolar, testes de integração REST com Spring Boot e MockMác, além de um fluxo completo de API que
áálida a jornada principal da Ordem de Seráiço.

Nesta reáisão foram adicionados e ajustados testes para endpoints explícitos de aproáação/recusa externa de orçamento,
consulta de status da OS, tracking do cliente, entrega da OS, rejeição de payload ináálido e listagem operacional
priorizada áia API. A alteração manteáe o gate interno de cobertura e fez o `mán clean áerify` passar sem reduzir a
exigência configurada.
