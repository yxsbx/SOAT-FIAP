# Roteiro do Vídeo de Apresentação - Tech Challenge

Tempo máximo: 15 minutos  
Projeto: AutoCare Hub  
Apresentadora: Yasmin Barcelos Pires - RM370897  
Data: 20/06/2026  
Formato sugerido: gravação de tela com narração objetiva, alternando entre documentação, código, Swagger e demonstração
rápida.

## 0:00 - 0:40 | 1. Apresentação

**O que falar**

- Informar: "Meu nome é Yasmin Barcelos Pires e esta é uma entrega individual."
- Informar que o projeto apresentado é o AutoCare Hub, desenvolvido para o Tech Challenge FIAP.
- Explicar que o foco da entrega é um backend para gestão de oficina mecânica, com frontend demonstrativo.

**O que mostrar na tela**

- Abrir `docs/DELIVERY_DOCUMENT.md`.
- Mostrar as seções:
    - `1. Nome do grupo/entrega individual`.
    - `2. Participantes`.
    - `4. Nome do projeto`.

**Arquivos ou endpoints**

```text
docs/DELIVERY_DOCUMENT.md
```

**Pontos comprovados do roteiro da faculdade**

- Identificação da aluna responsável pela entrega individual.
- Identificação do projeto entregue.

## 0:40 - 1:20 | 2. Contexto do problema

**O que falar**

- Oficinas mecânicas costumam lidar com dados espalhados: clientes, veículos, serviços, peças, estoque, orçamentos e
  status da OS.
- Essa fragmentação dificulta rastreabilidade, comunicação com o cliente, controle de estoque e gestão do atendimento.
- O AutoCare Hub centraliza esse fluxo em uma API REST.

**O que mostrar na tela**

- Abrir o `README.md` no início.
- Destacar `Contexto do Desafio` e `Problema Resolvido`.

**Arquivos ou endpoints**

```text
README.md
docs/DELIVERY_DOCUMENT.md
```

**Pontos comprovados do roteiro da faculdade**

- Contextualização do domínio.
- Problema de negócio que motivou o MVP.

## 1:20 - 2:00 | 3. Objetivo do sistema

**O que falar**

- O objetivo do MVP é administrar o ciclo de atendimento da oficina.
- O sistema permite cadastrar clientes, veículos, serviços e peças.
- Também permite criar Ordem de Serviço, gerar orçamento, aprovar orçamento, controlar estoque e acompanhar a OS.

**O que mostrar na tela**

- Mostrar a seção `9. Objetivo do MVP` em `docs/DELIVERY_DOCUMENT.md`.
- Mostrar rapidamente a lista de funcionalidades da seção `10. Resumo das funcionalidades entregues`.

**Arquivos ou endpoints**

```text
docs/DELIVERY_DOCUMENT.md
README.md
```

**Pontos comprovados do roteiro da faculdade**

- Objetivo do MVP.
- Escopo funcional entregue.

## 2:00 - 2:40 | 4. Visão geral da solução

**O que falar**

- A solução é composta por backend Java/Spring Boot, banco PostgreSQL e frontend Vue/Vite demonstrativo.
- O backend expõe API REST documentada com Swagger/OpenAPI.
- O ambiente local é reproduzível com Docker Compose.

**O que mostrar na tela**

- Mostrar a raiz do projeto no editor.
- Mostrar rapidamente:
    - `src/`.
    - `frontend/`.
    - `docs/`.
    - `docker-compose.yml`.
    - `Dockerfile`.

**Arquivos ou endpoints**

```text
src/main/java/br/com/autocarehub
frontend/
docs/
Dockerfile
docker-compose.yml
```

**Pontos comprovados do roteiro da faculdade**

- Visão geral da solução.
- Organização do projeto.
- Entrega backend com documentação e infraestrutura local.

## 2:40 - 3:40 | 5. Arquitetura do projeto

**O que falar**

- O backend foi organizado como monolito em camadas.
- A camada `domain` concentra regras de negócio.
- A camada `application` concentra os use cases.
- A camada `infrastructure` contém persistência, segurança e configurações.
- A camada `interfaces` contém controllers REST e mappers.
- Controllers não acessam repositórios diretamente; eles delegam para use cases.

**O que mostrar na tela**

- Mostrar a árvore de pacotes.
- Abrir exemplos rápidos:
    - Entidade/agregado de domínio.
    - Use case.
    - Controller.
    - Adapter/repository.

**Arquivos ou endpoints**

```text
src/main/java/br/com/autocarehub/domain
src/main/java/br/com/autocarehub/application/usecase
src/main/java/br/com/autocarehub/infrastructure
src/main/java/br/com/autocarehub/interfaces/rest
```

**Pontos comprovados do roteiro da faculdade**

- Arquitetura da solução.
- Separação de responsabilidades.
- Organização técnica do backend.

## 3:40 - 4:40 | 6. Aplicação de DDD

**O que falar**

- O projeto aplica DDD de forma pragmática.
- A Ordem de Serviço é o agregado central.
- Foram modeladas entidades como `Customer`, `Vehicle`, `ServiceOrder`, `Part` e `WorkshopService`.
- Foram criados value objects como `Document`, `Plate`, `Money` e `Address`.
- As regras de negócio ficam no domínio e nos use cases.

**O que mostrar na tela**

- Abrir `docs/DDD_DOCUMENTATION.md`.
- Mostrar a seção de Linguagem Ubíqua, agregados e value objects.
- Abrir rapidamente `Document.java`, `Plate.java` ou `ServiceOrder.java`.

**Arquivos ou endpoints**

```text
docs/DDD_DOCUMENTATION.md
src/main/java/br/com/autocarehub/domain/model/ServiceOrder.java
src/main/java/br/com/autocarehub/domain/valueobject/Document.java
src/main/java/br/com/autocarehub/domain/valueobject/Plate.java
```

**Pontos comprovados do roteiro da faculdade**

- Aplicação de DDD.
- Linguagem ubíqua.
- Entidades, value objects, agregados e regras de domínio.

## 4:40 - 5:20 | 7. Event Storming

**O que falar**

- O Event Storming foi usado para mapear comandos, eventos, agregados, políticas e exceções.
- Os principais fluxos modelados foram criação/acompanhamento da OS e gestão de estoque.
- No MVP, os eventos são documentação de modelagem; não há event store implementado.

**O que mostrar na tela**

- Abrir `docs/EVENT_STORMING.md`.
- Mostrar o fluxo de criação da Ordem de Serviço.
- Mostrar comandos e eventos como `CriarOrdemServico`, `OrdemServicoCriada`, `GerarOrcamento` e `OrcamentoGerado`.

**Arquivos ou endpoints**

```text
docs/EVENT_STORMING.md
```

**Pontos comprovados do roteiro da faculdade**

- Event Storming.
- Modelagem do domínio.
- Fluxos de negócio e eventos principais.

## 5:20 - 6:10 | 8. Fluxo de criação da Ordem de Serviço

**O que falar**

- A criação da OS identifica o cliente por CPF/CNPJ.
- O sistema vincula ou cadastra o veículo.
- A OS exige ao menos um serviço solicitado.
- Peças e insumos são opcionais.
- O fluxo valida documento, placa, vínculo de veículo e serviço ativo.

**O que mostrar na tela**

- Abrir o Swagger e localizar `POST /api/v1/service-orders`.
- Mostrar o contrato de request.
- Se possível, mostrar uma requisição de exemplo já preparada.

**Arquivos ou endpoints**

```text
http://localhost:8080/swagger-ui.html
POST /api/v1/service-orders
docs/openapi/openapi.yaml
src/main/java/br/com/autocarehub/application/usecase/serviceorder/CreateServiceOrderUseCase.java
```

**Pontos comprovados do roteiro da faculdade**

- Criação de Ordem de Serviço.
- Validações de domínio.
- Uso da API REST documentada.

## 6:10 - 6:50 | 9. Geração e aprovação de orçamento

**O que falar**

- O orçamento é gerado a partir dos serviços e peças vinculados à OS.
- Ao gerar orçamento, a OS passa para aguardando aprovação.
- Ao aprovar orçamento, o aceite é registrado e as reservas de peças são confirmadas.
- O início da execução é uma transição posterior para `IN_PROGRESS`.
- O fluxo impede transições inválidas.

**O que mostrar na tela**

- Mostrar endpoints de orçamento no Swagger.
- Abrir o domínio ou use cases de geração e aprovação.

**Arquivos ou endpoints**

```text
POST /api/v1/service-orders/{serviceOrderId}/budget/generate
POST /api/v1/service-orders/{serviceOrderId}/budget/approve
src/main/java/br/com/autocarehub/application/usecase/serviceorder/GenerateServiceOrderBudgetUseCase.java
src/main/java/br/com/autocarehub/application/usecase/serviceorder/ApproveServiceOrderBudgetUseCase.java
src/main/java/br/com/autocarehub/domain/model/ServiceOrder.java
```

**Pontos comprovados do roteiro da faculdade**

- Geração de orçamento.
- Aprovação de orçamento.
- Regras de transição de status.

## 6:50 - 7:30 | 10. Acompanhamento da OS pelo cliente

**O que falar**

- O cliente consegue acompanhar o andamento da OS.
- A consulta retorna status, dados da OS, orçamento e histórico simplificado.
- Esse fluxo dá transparência ao atendimento sem exigir contato manual constante com a oficina.

**O que mostrar na tela**

- Abrir endpoint de consulta/acompanhamento da OS no Swagger.
- Mostrar resposta esperada ou schema de response.

**Arquivos ou endpoints**

```text
GET /api/v1/customers/{customerId}/service-orders
GET /api/v1/service-orders/tracking?serviceOrderId={serviceOrderId}
src/main/java/br/com/autocarehub/application/usecase/serviceorder/TrackServiceOrderUseCase.java
```

**Pontos comprovados do roteiro da faculdade**

- Acompanhamento da Ordem de Serviço.
- Consulta pelo cliente.
- Transparência do fluxo de atendimento.

## 7:30 - 8:15 | 11. Gestão de clientes, veículos, serviços e peças

**O que falar**

- O sistema possui CRUDs administrativos para as entidades base da oficina.
- Clientes e veículos sustentam a criação da OS.
- Serviços representam o catálogo da oficina.
- Peças e insumos alimentam orçamento e estoque.

**O que mostrar na tela**

- Mostrar no Swagger os grupos de endpoints.
- Mostrar rapidamente a tela do frontend demonstrativo, se estiver autenticada.

**Arquivos ou endpoints**

```text
GET    /api/v1/customers
POST   /api/v1/customers
GET    /api/v1/vehicles
POST   /api/v1/vehicles
GET    /api/v1/workshop-services
POST   /api/v1/workshop-services
GET    /api/v1/parts
POST   /api/v1/parts
```

**Pontos comprovados do roteiro da faculdade**

- Cadastro de clientes.
- Cadastro de veículos.
- Cadastro de serviços.
- Cadastro de peças/insumos.
- APIs administrativas.

## 8:15 - 8:55 | 12. Controle de estoque

**O que falar**

- O estoque controla quantidade total, reservada e disponível.
- O sistema registra entrada, saída, reserva, liberação e baixa.
- Isso evita que uma peça seja comprometida sem disponibilidade.

**O que mostrar na tela**

- Mostrar endpoints de estoque no Swagger.
- Abrir `Part.java` para mostrar regras de estoque.

**Arquivos ou endpoints**

```text
PATCH /api/v1/parts/{partId}/stock
PATCH /api/v1/parts/{partId}/stock-movement
PATCH /api/v1/parts/{partId}/reserve
PATCH /api/v1/parts/{partId}/release-reservation
PATCH /api/v1/parts/{partId}/commit-reservation
src/main/java/br/com/autocarehub/domain/model/Part.java
```

**Pontos comprovados do roteiro da faculdade**

- Controle de estoque.
- Gestão de peças e insumos.
- Regras de negócio no domínio.

## 8:55 - 9:40 | 13. Segurança e JWT

**O que falar**

- O login é feito por `POST /api/v1/auth/login`.
- A API emite JWT assinado com segredo configurado por variável de ambiente.
- Endpoints administrativos exigem Bearer Token.
- Senhas são protegidas com BCrypt.
- O sistema valida CPF/CNPJ, placa, e-mail, tamanhos e dados numéricos.

**O que mostrar na tela**

- Mostrar o endpoint de login no Swagger.
- Mostrar o botão `Authorize` do Swagger.
- Abrir a configuração de segurança.

**Arquivos ou endpoints**

```text
POST /api/v1/auth/login
src/main/java/br/com/autocarehub/infrastructure/security
src/main/java/br/com/autocarehub/infrastructure/security/SecurityConfig.java
src/main/resources/application.yml
```

**Pontos comprovados do roteiro da faculdade**

- Segurança da API.
- Autenticação JWT.
- Proteção de endpoints administrativos.
- Validação de dados sensíveis.

## 9:40 - 10:15 | 14. Swagger

**O que falar**

- A API é documentada com OpenAPI/Swagger.
- O contrato está versionado no repositório.
- O Swagger facilita a avaliação e a execução dos endpoints.
- Em ambiente não acadêmico, o Swagger pode ser desabilitado por variável de ambiente.

**O que mostrar na tela**

- Abrir `http://localhost:8080/swagger-ui.html`.
- Mostrar `docs/openapi/openapi.yaml`.
- Mostrar um endpoint expandido.

**Arquivos ou endpoints**

```text
http://localhost:8080/swagger-ui.html
docs/openapi/openapi.yaml
```

**Pontos comprovados do roteiro da faculdade**

- Documentação da API.
- Contrato OpenAPI.
- Rota do Swagger.

## 10:15 - 10:55 | 15. Docker e execução local

**O que falar**

- O projeto possui Dockerfile e Docker Compose.
- O Compose sobe PostgreSQL, API e frontend com um único comando.
- Variáveis sensíveis ficam em `.env`, com exemplo em `.env.example`.
- A execução local expõe frontend em `5173`, API em `8080` e PostgreSQL em `5432`.
- O frontend usa proxy reverso para a API, evitando erro de CORS ao acessar pelo IP local.

**O que mostrar na tela**

- Abrir `docker-compose.yml`.
- Abrir `Dockerfile`.
- Abrir `frontend/Dockerfile` e `frontend/nginx.conf`.
- Mostrar comandos no terminal, sem precisar executar se o tempo estiver curto.

**Arquivos ou endpoints**

```text
Dockerfile
frontend/Dockerfile
frontend/nginx.conf
docker-compose.yml
.env.example
```

Comandos:

```powershell
Copy-Item .env.example .env
docker compose up -d --build
docker compose ps
docker compose down
```

**Pontos comprovados do roteiro da faculdade**

- Docker.
- Execução local reproduzível.
- Configuração por variáveis de ambiente.

## 10:55 - 11:40 | 16. Testes e cobertura

**O que falar**

- O projeto possui testes unitários e de integração.
- Os testes cobrem domínio, use cases, segurança, autorização e fluxos REST.
- O relatório JaCoCo está configurado no Maven.
- A documentação registra 145 testes automatizados e `mvn verify` com sucesso.
- A cobertura global é 96,09% de instruções, 97,02% de linhas e 90,32% de branches.
- Os gates automatizados exigem 90% de instruções, linhas e branches.
- REST e infraestrutura entram na medição; apenas código gerado e records sem lógica são excluídos.

**O que mostrar na tela**

- Mostrar a pasta `src/test`.
- Mostrar `pom.xml` com JaCoCo.
- Mostrar o relatório de cobertura, se estiver gerado.

**Arquivos ou endpoints**

```text
src/test/java
pom.xml
target/site/jacoco/index.html
docs/SECURITY_REPORT.md
```

Comandos:

```powershell
mvn test
mvn verify
```

**Pontos comprovados do roteiro da faculdade**

- Testes automatizados.
- Cobertura.
- Evidência de qualidade técnica.

## 11:40 - 12:25 | 17. Relatório de vulnerabilidades

**O que falar**

- O projeto possui relatório de vulnerabilidades em `docs/SECURITY_REPORT.md`.
- Foram executados OWASP Dependency-Check para backend e `npm audit` para frontend.
- Vulnerabilidades iniciais foram corrigidas por atualização de dependências.
- O resultado final documentado é 0 vulnerabilidades no backend e 0 no frontend para os scans executados.
- O Docker Scout do backend retornou 0 vulnerabilidades após a migração para runtime distroless.
- A imagem frontend foi reduzida de 75 CVEs para 1 CVE média sem correção disponível, sem críticas ou altas.
- Gitleaks analisou 36 commits sem encontrar secrets.
- Semgrep executou 187 regras em 200 arquivos sem encontrar problemas.

**O que mostrar na tela**

- Abrir `docs/SECURITY_REPORT.md`.
- Mostrar a tabela de vulnerabilidades encontradas.
- Mostrar a seção de correções aplicadas.
- Mostrar os caminhos dos relatórios.

**Arquivos ou endpoints**

```text
docs/SECURITY_REPORT.md
validation/SECURITY_SCAN_GUIDE.md
target/dependency-check/dependency-check-report.html
security-reports/frontend-dependencies/npm-audit-report.json
security-reports/docker/docker-scout-cves.txt
security-reports/docker/docker-scout-frontend-cves.txt
```

Comandos:

```powershell
mvn dependency-check:check
cd frontend
npm audit --json
```

**Pontos comprovados do roteiro da faculdade**

- Relatório de vulnerabilidades.
- Vulnerabilidades encontradas.
- Correções aplicadas.
- Evidências de scan.

## 12:25 - 14:20 | 18. Demonstração rápida do sistema

**O que falar**

- Demonstrar o fluxo principal do MVP de ponta a ponta.
- Manter a demonstração curta e objetiva.
- Se algum dado já estiver cadastrado, usar dados prontos para economizar tempo.

**O que mostrar na tela**

Opção A - Demonstração pelo Swagger:

1. Fazer login.
2. Autorizar no Swagger com Bearer Token.
3. Listar clientes.
4. Listar veículos.
5. Criar uma Ordem de Serviço ou abrir uma OS existente.
6. Gerar orçamento.
7. Aprovar orçamento.
8. Consultar acompanhamento da OS.
9. Mostrar peças/estoque.

Opção B - Demonstração pelo frontend:

1. Abrir a tela do dashboard.
2. Criar ou abrir uma Ordem de Serviço.
3. Mostrar cliente, veículo, defeitos, valores e finalização.
4. Gerar orçamento quando a OS estiver aguardando orçamento.
5. Aprovar orçamento quando estiver aguardando aprovação.
6. Mostrar status atualizado.

**Arquivos ou endpoints**

Swagger:

```text
POST /api/v1/auth/login
GET  /api/v1/customers
GET  /api/v1/vehicles
POST /api/v1/service-orders
POST /api/v1/service-orders/{serviceOrderId}/budget/generate
POST /api/v1/service-orders/{serviceOrderId}/budget/approve
GET  /api/v1/service-orders/tracking?serviceOrderId={serviceOrderId}
GET  /api/v1/parts
```

Frontend:

```text
http://localhost:5173
frontend/src/pages/DashboardView.vue
```

**Pontos comprovados do roteiro da faculdade**

- Funcionamento prático do MVP.
- API REST.
- Fluxo de OS.
- Orçamento.
- Aprovação.
- Acompanhamento.
- Gestão de cadastros e estoque.
- Segurança via autenticação.

## 14:20 - 15:00 | 19. Conclusão

**O que falar**

- Reforçar que o AutoCare Hub entrega o ciclo principal de atendimento de oficina.
- Relembrar os pontos técnicos: DDD, Event Storming, API REST, Swagger, JWT, PostgreSQL, Docker, testes e
  vulnerabilidades.
- Citar limitações conhecidas: sem pagamento online, sem notificação real, Swagger público no ambiente acadêmico e scans
  complementares ainda recomendados.
- Encerrar informando que o projeto está documentado e pronto para avaliação.

**O que mostrar na tela**

- Voltar para `docs/DELIVERY_DOCUMENT.md`.
- Mostrar a seção `24. Conclusão`.
- Opcionalmente mostrar a tela final do dashboard ou Swagger.

**Arquivos ou endpoints**

```text
docs/DELIVERY_DOCUMENT.md
README.md
```

**Pontos comprovados do roteiro da faculdade**

- Síntese da entrega.
- Limitações conhecidas.
- Melhorias futuras.
- Fechamento da apresentação.

## Checklist antes de gravar

- Confirmar que nome, RM e informação do Discord estão corretos em `docs/DELIVERY_DOCUMENT.md`.
- Confirmar o link do repositório privado.
- Confirmar que `soatarchitecture` mantém acesso Read e que a entrega está na branch `main`.
- Confirmar se haverá link externo de Miro ou se será usado apenas `docs/EVENT_STORMING.md`.
- Subir a API localmente.
- Abrir o Swagger antes de iniciar a gravação.
- Separar um usuário válido para login.
- Usar a senha acadêmica local `autocare123` para os usuários seed.
- Separar IDs de cliente, veículo, OS e peça para não perder tempo.
- Deixar os três containers rodando antes da gravação.
- Evitar executar comandos demorados durante o vídeo; mostrar comandos e resultados já gerados quando possível.

## Comandos úteis para preparar a gravação

Ambiente completo:

```powershell
Copy-Item .env.example .env
docker compose up -d --build
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```

Frontend: `http://localhost:5173`

Testes:

```powershell
mvn test
mvn verify
```

Vulnerabilidades:

```powershell
mvn dependency-check:check
cd frontend
npm audit --json
```
