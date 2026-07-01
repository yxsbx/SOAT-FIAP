# Roteiro Completo do Vídeo - AutoCare Hub

Tempo máximo: 15 minutos  
Projeto: AutoCare Hub  
Apresentadora: Yasmin Barcelos Pires - RM370897  
Formato: gravação de tela com narração, alternando entre documentação, código, terminal, Swagger, frontend e evidências.

Este roteiro foi escrito para ser usado como fala pronta. Quando aparecer **Falar**, leia ou adapte a frase. Quando
aparecer **Mostrar/Clicar**, execute a ação na tela. Quando aparecer **Comprova**, cite rapidamente o requisito do Tech
Challenge que está sendo provado.

## Antes de gravar

Deixe estes itens abertos ou fáceis de acessar:

- IntelliJ ou VS Code no repositório.
- Terminal PowerShell na raiz do projeto.
- Navegador com `http://localhost:8080/swagger-ui.html`.
- Navegador com `http://localhost:5173`, se quiser mostrar o frontend demonstrativo.
- Relatório JaCoCo em `target/site/jacoco/index.html`, se já gerado.
- Relatório Allure em `target/site/allure-maven-plugin/index.html`, somente se tiver executado o profile opcional.
- Relatório OWASP ZAP em `security-reports/dast/zap-api-report.html`.
- Relatório OWASP Dependency-Check em `security-reports/backend-dependencies/dependency-check-report.html`.

Comandos de preparação:

```powershell
docker compose down -v
docker compose up -d --build
docker compose ps
docker compose logs -f app
mvn clean verify
```

Usuário demo para Swagger:

```json
{
  "username": "admin@autocarehub.com",
  "password": "autocare123"
}
```

Dados seed úteis para demonstrar:

```text
Cliente CPF: 12345678909
Veículo seed: 20000000-0000-0000-0000-000000000001
Serviço seed: 30000000-0000-0000-0000-000000000001
Peça seed: 40000000-0000-0000-0000-000000000001
Placa seed: MCA1D23
```

## 0:00 - 0:45 | Abertura e objetivo da entrega

**Mostrar/Clicar**

1. Abrir `README.md`.
2. Mostrar o título `AutoCare Hub`.
3. Mostrar a seção `Sumário da Entrega`.
4. Abrir também `docs/DELIVERY_DOCUMENT.md`.

**Falar**

"Meu nome é Yasmin Barcelos Pires, RM370897, e esta é a entrega individual do Tech Challenge FIAP. O projeto se chama
AutoCare Hub. Ele é um MVP backend monolítico para gestão de oficina mecânica, com um frontend demonstrativo apenas para
ajudar na apresentação visual. O foco da entrega é a API REST em Spring Boot."

"A proposta do sistema é centralizar o fluxo principal de uma oficina: cadastro de clientes, veículos, serviços, peças,
controle de estoque, criação da Ordem de Serviço, geração de orçamento, aprovação pelo cliente, acompanhamento da OS e
controle dos status."

"No README eu deixei o índice da entrega. Aqui eu aponto para os documentos que comprovam os requisitos: documento
final,
arquitetura, DDD, Event Storming, Domain Storytelling, requisitos, testes, análise estática, segurança, OpenAPI."

**Comprova**

- README explicativo.
- Documento final de entrega.
- Links e estrutura da documentação.
- Escopo do projeto.

## 0:45 - 1:40 | Problema de negócio e escopo do MVP

**Mostrar/Clicar**

1. No `README.md`, mostrar `Escopo do MVP`.
2. Em `docs/DELIVERY_DOCUMENT.md`, mostrar a descrição resumida do projeto.
3. Em `docs/REQUIREMENTS.md`, mostrar personas e requisitos funcionais.

**Falar**

"O problema de negócio é que uma oficina normalmente lida com informações espalhadas: cliente em um lugar, veículo em
outro, peças em planilha, orçamento em conversa e status da OS sem rastreabilidade. Isso dificulta controle de estoque,
aprovação de orçamento e acompanhamento pelo cliente."

"Por isso, o MVP resolve o ciclo central da oficina. Ele não tenta ser um ERP completo. O escopo obrigatório é backend:
CRUDs de clientes, veículos, serviços, peças e insumos; controle de estoque; criação da Ordem de Serviço; orçamento
automático; aprovação; acompanhamento via API; autenticação JWT; validações; testes; Swagger; Docker e relatório de
vulnerabilidades."

"As funcionalidades fora do escopo, como pagamento online, WhatsApp, SMS, e-mail real, fornecedores externos e deploy
cloud produtivo, estão documentadas como limitações reais do MVP."

**Comprova**

- Problema e objetivo do projeto.
- Requisitos funcionais e não funcionais.
- Escopo real sem inventar funcionalidade.

## 1:40 - 2:45 | Decisão de arquitetura

**Mostrar/Clicar**

1. Abrir `docs/ARCHITECTURE.md`.
2. Mostrar HLD/LLD e C4 no mesmo documento.
3. Mostrar árvore `src/main/java/br/com/autocarehub`.
4. Abrir rapidamente:
    - `src/main/java/br/com/autocarehub/domain/model/ServiceOrder.java`
    - `src/main/java/br/com/autocarehub/application/usecase/serviceorder/CreateServiceOrderUseCase.java`
    - `src/main/java/br/com/autocarehub/interfaces/rest/controller/ServiceOrdersController.java`
    - `src/main/java/br/com/autocarehub/infrastructure/security/SecurityConfig.java`

**Falar**

"A arquitetura escolhida foi um backend monolítico em camadas. Essa escolha é adequada para o Tech Challenge porque o
domínio é coeso e o objetivo é demonstrar o fluxo completo da oficina sem complexidade distribuída."

"As camadas estão separadas assim: `domain` concentra entidades, value objects, enums e regras de negócio; `application`
coordena os casos de uso; `infrastructure` tem persistência JPA, segurança JWT e configuração; `interfaces` expõe
controllers REST, mappers e tratamento de exceção."

"A regra principal é que controller não decide regra de negócio. Ele recebe a requisição, chama um use case e devolve a
resposta. O use case orquestra o fluxo e o domínio protege as invariantes, como status da OS, orçamento e estoque."

"A documentação de arquitetura também mantém HLD, LLD e C4 no mesmo arquivo para evitar duplicidade. O C4 mostra o
sistema, os containers e os componentes principais."

**Comprova**

- Backend monolítico.
- Arquitetura em camadas.
- Decisão arquitetural documentada.
- C4/HLD/LLD.

## 2:45 - 4:00 | DDD, linguagem ubíqua e papéis de negócio

**Mostrar/Clicar**

1. Abrir `docs/DDD_DOCUMENTATION.md`.
2. Mostrar `Linguagem ubíqua`.
3. Mostrar `Papéis de negócio`.
4. Mostrar entidades, value objects e agregados.
5. Abrir no código:
    - `ServiceOrder.java`
    - `Part.java`
    - `Document.java`
    - `Plate.java`
    - `ServiceOrderStatus.java`

**Falar**

"A documentação DDD foi escrita a partir do fluxo real de uma oficina. A Ordem de Serviço é o agregado central, porque
conecta cliente, veículo, serviços, peças, orçamento, aprovação, status e acompanhamento."

"Na linguagem ubíqua, eu mantenho os termos de negócio em português: Ordem de Serviço, orçamento, aprovação, peça,
estoque, diagnóstico, execução e entrega. Quando falo de classe, enum, endpoint ou pacote, mantenho o nome técnico em
inglês, por exemplo `ServiceOrder`, `Part`, `Document`, `Plate` e `ServiceOrderStatus`."

"Os papéis de negócio aparecem como Atendente, Mecânico, Responsável pelo estoque e Cliente. No código, isso não vira
necessariamente uma classe separada para cada papel. O backend representa autorização por usuários, roles e perfis:
administrador, funcionário, cliente, admin master, admin de oficina, admin de loja de peças e funcionários. Então a
documentação separa papel de negócio de perfil técnico."

"Para o escopo por empresa, o backend usa `companyId`, mas isso é técnico. Na tela, eu não digito ID: seleciono a
empresa
pelo nome ou cadastro uma nova empresa informando nome e tipo. O ID da empresa e o ID do usuário são gerados
automaticamente pelo backend. O Admin Master fica vinculado à empresa AutoCare Hub, do tipo `PLATFORM`. Já o admin de
oficina ou loja não escolhe empresa: o backend usa a empresa do usuário logado."

"Os value objects `Document` e `Plate` centralizam validações sensíveis. CPF/CNPJ e placa não ficam como strings soltas
sem regra. Isso ajuda a provar a validação de dados sensíveis pedida no enunciado."

"O agregado `Part` protege regras de estoque: não permitir quantidade negativa, não reservar mais do que o disponível e
não baixar quantidade inválida. O agregado `ServiceOrder` protege o ciclo da OS: orçamento antes da aprovação, aprovação
antes da execução e ordem correta dos status."

**Comprova**

- DDD aplicado.
- Linguagem ubíqua.
- Escopo de usuários por empresa.
- Entidades, value objects e agregados.
- Validação de CPF/CNPJ.
- Validação de placa.
- Regras de estoque e OS no domínio.

## 4:00 - 5:05 | Domain Storytelling e Event Storming

**Mostrar/Clicar**

1. Abrir `docs/DOMAIN_STORYTELLING.md`.
2. Mostrar histórias separadas por cenário.
3. Abrir `docs/EVENT_STORMING.md`.
4. Mostrar comandos, eventos, políticas e diagramas Mermaid.

**Falar**

"Além do DDD, eu documentei Domain Storytelling para explicar as rotinas por ator. Eu separei as histórias em cenários
porque juntar mecânico, sistema e cliente em uma única história deixaria momentos diferentes misturados. Por exemplo,
diagnóstico e montagem do orçamento é um momento da oficina; aprovação do orçamento é um momento do cliente."

"No Event Storming eu documento os comandos, eventos, políticas, agregados e exceções. Os fluxos obrigatórios aparecem
principalmente em duas frentes: criação e acompanhamento da Ordem de Serviço; e gestão de peças, insumos e estoque."

"Aqui os eventos são documentação do domínio, não um event store implementado. Como o MVP é monolítico, o fluxo é
implementado com casos de uso e transações do backend."

**Comprova**

- Domain Storytelling.
- Event Storming completo.
- Fluxo de criação e acompanhamento da OS.
- Fluxo de peças e insumos.
- Regras por ator e linha do tempo.

## 5:05 - 6:10 | Subida do projeto, Docker e Swagger

**Mostrar/Clicar**

1. No terminal, mostrar os comandos:

```powershell
docker compose down -v
docker compose up -d --build
docker compose ps
docker compose logs -f app
```

2. Mostrar `Dockerfile`.
3. Mostrar `docker-compose.yml`.
4. Abrir `http://localhost:8080/swagger-ui.html`.
5. Abrir `docs/openapi/openapi.yaml`.

**Falar**

"A execução local usa Docker Compose. Ele sobe PostgreSQL, backend e frontend demonstrativo. Eu uso `docker compose down
-v` antes da gravação para demonstrar uma base limpa, porque as migrations Flyway recriam o schema e os dados seed."

"O Dockerfile do backend usa runtime Java e o compose injeta variáveis como banco e JWT. O Swagger fica disponível em
`/swagger-ui.html`, e o contrato OpenAPI também está versionado em `docs/openapi/openapi.yaml`."

"O Swagger é importante porque comprova o contrato da API e permite demonstrar os endpoints administrativos e públicos."

**Comprova**

- Dockerfile.
- docker-compose.yml.
- Migrations Flyway.
- Swagger/OpenAPI.
- Execução local reproduzível.

## 6:10 - 7:00 | Login e JWT

**Mostrar/Clicar**

1. No Swagger, abrir `Auth`.
2. Clicar em `POST /api/v1/auth/login`.
3. Clicar em `Try it out`.
4. Usar payload:

```json
{
  "username": "admin@autocarehub.com",
  "password": "autocare123"
}
```

5. Clicar em `Execute`.
6. Copiar `accessToken`.
7. Clicar em `Authorize`.
8. Informar:

```text
Bearer <token>
```

9. Clicar em `Authorize` e fechar.

**Falar**

"As APIs administrativas exigem autenticação JWT. Primeiro eu faço login com um usuário seed acadêmico. A senha demo
existe apenas para ambiente local, e isso está documentado no README."

"O backend retorna um token Bearer. A partir desse momento, chamadas administrativas como clientes, veículos, serviços,
peças, estoque e OS podem ser testadas no Swagger."

"No código, o JWT é validado por filtro de segurança. O segredo vem de variável de ambiente, a expiração é configurável,
e senha de usuário usa BCrypt."

**Comprova**

- JWT para APIs administrativas.
- Usuários demo documentados.
- Segurança de autenticação.
- Swagger com Bearer Auth.

## 7:00 - 8:10 | CRUDs obrigatórios e validações

**Mostrar/Clicar**

1. No Swagger, abrir `Customers`.
2. Executar `GET /api/v1/customers`.
3. Abrir `POST /api/v1/customers`, mas não precisa executar se quiser economizar tempo.
4. Mostrar `Vehicles`, `Workshop Services` e `Parts`.
5. Executar pelo menos:

```text
GET /api/v1/vehicles
GET /api/v1/workshop-services
GET /api/v1/parts
```

6. Se quiser provar validação, abrir `POST /api/v1/vehicles` e mostrar o pattern de placa no schema.

**Falar**

"Aqui eu demonstro os CRUDs obrigatórios. Clientes são identificados por CPF ou CNPJ. Veículos têm placa, marca, modelo,
ano e quilometragem. Serviços têm preço base e tempo estimado. Peças e insumos têm SKU, categoria, preço, custo, estoque
total, estoque reservado e estoque disponível."

"As validações de CPF/CNPJ e placa estão no domínio e também aparecem no contrato OpenAPI. Isso evita aceitar dados
sensíveis inválidos na entrada da API."

"A listagem prova que os dados seed foram carregados pelo Flyway. A criação e atualização seguem o mesmo contrato REST,
com validações e respostas padronizadas de erro."

**Comprova**

- CRUD de clientes.
- CRUD de veículos.
- CRUD de serviços.
- CRUD de peças e insumos.
- Validação de CPF/CNPJ.
- Validação de placa.
- Banco de dados com Flyway.

## 8:10 - 9:00 | Controle de estoque

**Mostrar/Clicar**

1. No Swagger, abrir `PATCH /api/v1/parts/{partId}/stock-movement`.
2. Clicar em `Try it out`.
3. Usar `partId`:

```text
40000000-0000-0000-0000-000000000001
```

4. Usar payload, ajustando se o schema do Swagger sugerir nomes diferentes:

```json
{
  "type": "IN",
  "quantity": 1,
  "reason": "Entrada demonstrativa para o Tech Challenge"
}
```

5. Clicar em `Execute`.
6. Mostrar o retorno com estoque atualizado.
7. Abrir `Part.java` ou teste `PartTest.java`.

**Falar**

"O controle de estoque não é apenas um campo numérico. A peça possui estoque total, reservado e disponível. A regra de
negócio impede estoque negativo, reserva maior do que a disponibilidade e baixa inválida."

"A movimentação administrativa fica protegida por JWT. Quando a OS usa uma peça, a geração de orçamento pode reservar
estoque, e a aprovação confirma o fluxo de uso. Isso evita prometer peça que não existe."

**Comprova**

- Controle de estoque.
- CRUD e movimentação de peças/insumos.
- Validação de quantidade.
- Proteção administrativa por JWT.

## 9:00 - 10:45 | Criação da Ordem de Serviço e geração automática de orçamento

**Mostrar/Clicar**

1. No Swagger, abrir `POST /api/v1/service-orders`.
2. Clicar em `Try it out`.
3. Usar payload preparado:

```json
{
  "customerDocument": "12345678909",
  "vehicleId": "20000000-0000-0000-0000-000000000001",
  "diagnosticNotes": "Cliente relata barulho ao frear e solicita diagnóstico completo.",
  "services": [
    {
      "serviceId": "30000000-0000-0000-0000-000000000001",
      "quantity": 1
    }
  ],
  "parts": [
    {
      "partId": "40000000-0000-0000-0000-000000000001",
      "quantity": 1
    }
  ],
  "generateBudget": true
}
```

4. Clicar em `Execute`.
5. Copiar o `id` da OS retornada.
6. Mostrar no retorno:
    - `customerId`
    - `vehicleId`
    - `status`
    - `services`
    - `parts`
    - `servicesTotal`
    - `partsTotal`
    - `totalAmount`
    - `budgetGeneratedAt`
7. Abrir `CreateServiceOrderUseCase.java`.
8. Abrir `ServiceOrder.java`.

**Falar**

"Agora eu demonstro o fluxo principal. A criação da OS identifica o cliente por CPF/CNPJ. Neste exemplo eu uso um CPF
seed e um veículo já cadastrado, mas o contrato também permite informar dados de cliente e veículo no payload quando
for necessário criar ou vincular no fluxo."

"A OS recebe diagnóstico ou problema relatado, serviços solicitados e peças/insumos. Como `generateBudget` está `true`,
o backend gera o orçamento automaticamente no momento da criação."

"A regra de negócio é: serviços e peças compõem o orçamento; o total de serviços, total de peças e total geral são
calculados pelo backend; se o orçamento foi gerado, a OS vai para `WAITING_APPROVAL`, que no domínio corresponde a
`AGUARDANDO_APROVACAO`."

"Isso comprova a criação da OS, identificação do cliente por CPF/CNPJ, inclusão de serviços, inclusão de peças e geração
automática de orçamento."

**Comprova**

- Criação da OS.
- Identificação por CPF/CNPJ.
- Inclusão de serviços solicitados.
- Inclusão de peças e insumos.
- Geração automática de orçamento.
- Status `Aguardando aprovação`.

## 10:45 - 12:00 | Aprovação, status e atualização automática/controlada

**Mostrar/Clicar**

1. No Swagger, abrir `POST /api/v1/service-orders/{serviceOrderId}/budget/approve`.
2. Informar o `serviceOrderId` copiado.
3. Clicar em `Execute`.
4. Mostrar `approvedAt` e novo status.
5. Abrir `PATCH /api/v1/service-orders/{serviceOrderId}/status`.
6. Atualizar para:

```json
{
  "status": "IN_PROGRESS"
}
```

7. Depois atualizar para:

```json
{
  "status": "FINISHED"
}
```

8. Depois atualizar para:

```json
{
  "status": "DELIVERED"
}
```

9. Abrir:
    - `ApproveServiceOrderBudgetUseCase.java`
    - `UpdateServiceOrderStatusUseCase.java`
    - `ServiceOrderStatus.java`

**Falar**

"A aprovação do orçamento registra o aceite do cliente e libera a continuidade do atendimento. Aqui é importante
explicar
com cuidado a atualização de status."

"O sistema tem mudanças automáticas e mudanças controladas. Quando o orçamento é gerado, a OS muda automaticamente para
`WAITING_APPROVAL`. Quando o orçamento é aprovado, o backend registra a aprovação e libera a próxima etapa do fluxo. A
execução em si é uma transição administrativa, porque na vida real a oficina precisa indicar quando começou a executar o
serviço."

"Então eu não digo que tudo muda sozinho sem ação. O correto é: alguns status mudam automaticamente por regra de
negócio,
como geração de orçamento; outros mudam por endpoint administrativo, mas sempre validados pelo domínio. A API não deixa
ir para qualquer status em qualquer ordem."

"Os status exigidos estão cobertos: `RECEIVED`, `IN_DIAGNOSIS`, `WAITING_APPROVAL`, `IN_PROGRESS`, `FINISHED` e
`DELIVERED`. Na documentação de DDD eles aparecem em português como Recebida, Em diagnóstico, Aguardando aprovação, Em
execução, Finalizada e Entregue."

**Comprova**

- Aprovação de orçamento.
- Status da OS.
- Alteração automática e controlada dos status.
- Regras de transição no domínio.
- Finalização e entrega.

## 12:00 - 12:45 | Acompanhamento pelo cliente e tempo médio

**Mostrar/Clicar**

1. Abrir `GET /api/v1/service-orders/tracking`.
2. Testar com:

```text
customerDocument=12345678909
plate=MCA1D23
```

3. Mostrar retorno com status, orçamento e histórico.
4. Abrir `GET /api/v1/service-orders/metrics/average-execution-time`.
5. Executar e mostrar `completedOrders` e `averageExecutionTimeInMinutes`.
6. Abrir `TrackServiceOrderUseCase.java` e `GetAverageServiceOrderExecutionTimeUseCase.java`.

**Falar**

"O acompanhamento da OS pelo cliente acontece por API própria de tracking. O cliente não precisa acessar as APIs
administrativas. Ele consulta por dados da OS ou por CPF/CNPJ e placa, e recebe status, veículo, serviços, peças,
orçamento e histórico."

"A métrica de tempo médio calcula o tempo de execução com base nas Ordens de Serviço que têm início e fim registrados.
Isso atende ao requisito de monitoramento do tempo médio de execução."

**Comprova**

- Acompanhamento da OS pelo cliente via API.
- Listagem/detalhamento de OS para acompanhamento.
- Monitoramento do tempo médio de execução.
- Separação entre API administrativa e consulta do cliente.

## 12:45 - 13:35 | Testes, cobertura e evidências de qualidade

**Mostrar/Clicar**

1. Abrir `docs/TESTING.md`.
2. Mostrar a lista de testes unitários, integração e fluxo completo.
3. No terminal, mostrar:

```powershell
mvn clean test
mvn clean verify
```

4. Abrir `target/site/jacoco/index.html`.
5. Se tiver gerado Allure, abrir:

```text
target/site/allure-maven-plugin/index.html
```

6. Mostrar `ServiceOrderFlowIntegrationTest.java`.

**Falar**

"A estratégia de testes cobre regras de domínio, use cases, segurança, controllers REST e fluxo completo da OS. O teste
principal de fluxo passa por login, criação de cliente, veículo, serviço, peça, estoque, criação da OS, geração de
orçamento, aprovação, status, entrega, tracking e tempo médio."

"A cobertura é gerada pelo JaCoCo e o projeto usa gate acima do mínimo exigido de 80%. O Allure foi configurado como
profile opcional para gerar relatório navegável local, mas não é o gate obrigatório da entrega."

"Eu não cito Cucumber, Testcontainers, REST-assured ou Sonar como evidência executada, porque eles não fazem parte da
evidência real deste projeto."

**Comprova**

- Testes unitários.
- Testes de integração.
- Fluxos críticos testados.
- Cobertura mínima de 80%.
- Relatório JaCoCo.
- Allure opcional sem inventar evidência.

## 13:35 - 14:35 | Segurança, scans e riscos residuais

**Mostrar/Clicar**

1. Abrir `docs/SECURITY_REPORT.md`.
2. Mostrar `Segurança desde o desenho da solução`.
3. Mostrar `Avaliação de ameaças`.
4. Mostrar `OWASP Top 10`.
6. Abrir evidências:
    - `security-reports/backend-dependencies/dependency-check-report.html`
    - `security-reports/frontend-dependencies/npm-audit-report.json`
    - `security-reports/docker/docker-scout-cves.txt`
    - `security-reports/docker/docker-scout-frontend-cves.txt`
    - `security-reports/dast/zap-api-report.html`
    - `security-reports/static-analysis/semgrep.json`
    - `security-reports/secrets/gitleaks.json`

**Falar**

"A parte de segurança não foi tratada só no final. O relatório documenta Security by Design, ameaças, OWASP Top 10,
validação de dados sensíveis, JWT, BCrypt, proteção de endpoints administrativos, prevenção contra SQL Injection,
Command Injection e XSS no escopo do MVP."

"O backend usa JPA e repositories, sem SQL concatenado com entrada de usuário. Não há execução de comando do sistema no
backend, reduzindo exposição a Command Injection. No frontend demonstrativo, não foi identificado uso de `v-html`,
`innerHTML`, `eval` ou APIs equivalentes; o Vue escapa interpolação por padrão."

"Os scans reais foram: OWASP Dependency-Check, npm audit, Docker Scout, Semgrep, Gitleaks, JaCoCo e OWASP ZAP. O ZAP foi
executado contra o OpenAPI local, teve 0 falhas e 2 avisos revisados."

"O resultado final não tem vulnerabilidade crítica ou alta aberta. Existem dois riscos médios residuais documentados:
`jackson-databind` transitivo na imagem backend, porque a versão corrigida indicada pelo Docker Scout ainda não estava
disponível no Maven Central; e BusyBox na imagem frontend, porque o scanner não indicou versão corrigida. Esses riscos
estão aceitos temporariamente e mitigados por containers non-root e configuração mais restritiva."

**Comprova**

- Relatório de vulnerabilidades com scan real.
- Security by Design.
- OWASP Top 10.
- Análise estática.
- Análise de dependências.
- Análise de secrets.
- Docker Scout.
- OWASP ZAP.
- Riscos residuais documentados sem esconder vulnerabilidade.

## 14:35 - 15:00 | Fechamento e entrega

**Mostrar/Clicar**

1. Voltar para `docs/DELIVERY_DOCUMENT.md`.
2. Mostrar links da entrega.
3. Mostrar `README.md`, seção de dados da entrega.
4. Mostrar acesso `soat-architecture`.

**Falar**

"Para fechar, o AutoCare Hub entrega o MVP backend solicitado: monolito em camadas, DDD, Event Storming, CRUDs
obrigatórios, controle de estoque, Ordem de Serviço, orçamento, aprovação, tracking, status, tempo médio, JWT,
validações, Swagger, Docker, testes, cobertura e relatório de vulnerabilidades."

"O documento final consolida os links e evidências, e o usuário `soat-architecture` já possui acesso de leitura ao
repositório privado. As limitações estão documentadas sem prometer funcionalidades fora do escopo."

"Essa é a demonstração final do projeto AutoCare Hub para o Tech Challenge FIAP."

**Comprova**

- Documento final/PDF.
- Links e acesso do avaliador.
- Encerramento com rastreabilidade dos requisitos.

## Plano B se o tempo ficar curto

Se o vídeo estiver passando de 15 minutos, corte nesta ordem:

1. Não execute criação de cliente e veículo; apenas mostre as listagens e schemas.
2. Mostre apenas um relatório de scan aberto e cite os demais caminhos.
3. Não abra Allure; mostre apenas JaCoCo.
4. Não abra todos os use cases; mostre `ServiceOrder`, `CreateServiceOrderUseCase` e `ServiceOrdersController`.
5. No Event Storming, mostre apenas a linha do tempo principal da OS e a gestão de estoque.

Não corte:

- login JWT;
- criação da OS;
- orçamento;
- aprovação;
- status;
- tracking;
- testes/cobertura;
- relatório de vulnerabilidades.

## Checklist de cliques no Swagger

1. `POST /api/v1/auth/login`
2. Botão `Authorize`
3. `GET /api/v1/users/companies`
4. `POST /api/v1/users` criando uma empresa nova com `createCompany: true`, sem informar ID manualmente
5. `GET /api/v1/customers`
6. `GET /api/v1/vehicles`
7. `GET /api/v1/workshop-services`
8. `GET /api/v1/parts`
9. `PATCH /api/v1/parts/{partId}/stock-movement`
10. `POST /api/v1/service-orders`
11. `POST /api/v1/service-orders/{serviceOrderId}/budget/approve`
12. `PATCH /api/v1/service-orders/{serviceOrderId}/status`
13. `GET /api/v1/service-orders/tracking`
14. `GET /api/v1/service-orders/metrics/average-execution-time`

## Arquivos para abrir no vídeo

```text
README.md
docs/DELIVERY_DOCUMENT.md
docs/REQUIREMENTS.md
docs/ARCHITECTURE.md
docs/DDD_DOCUMENTATION.md
docs/DOMAIN_STORYTELLING.md
docs/EVENT_STORMING.md
docs/TESTING.md
docs/STATIC_ANALYSIS.md
docs/SECURITY_REPORT.md
docs/openapi/openapi.yaml
Dockerfile
docker-compose.yml
pom.xml
src/main/resources/db/migration/V1__create_autocarehub_baseline.sql
src/main/java/br/com/autocarehub/domain/model/ServiceOrder.java
src/main/java/br/com/autocarehub/domain/model/Part.java
src/main/java/br/com/autocarehub/domain/valueobject/Document.java
src/main/java/br/com/autocarehub/domain/valueobject/Plate.java
src/main/java/br/com/autocarehub/application/usecase/serviceorder/CreateServiceOrderUseCase.java
src/main/java/br/com/autocarehub/application/usecase/serviceorder/GenerateServiceOrderBudgetUseCase.java
src/main/java/br/com/autocarehub/application/usecase/serviceorder/ApproveServiceOrderBudgetUseCase.java
src/main/java/br/com/autocarehub/application/usecase/serviceorder/UpdateServiceOrderStatusUseCase.java
src/main/java/br/com/autocarehub/interfaces/rest/controller/ServiceOrdersController.java
src/test/java/br/com/autocarehub/interfaces/rest/ServiceOrderFlowIntegrationTest.java
target/site/jacoco/index.html
security-reports/dast/zap-api-report.html
security-reports/backend-dependencies/dependency-check-report.html
```

## Frases importantes para não errar

- "O frontend é demonstrativo; a entrega obrigatória é comprovada principalmente pela API, testes e documentação."
- "Eventos do Event Storming são modelagem do domínio, não implementação de event store."
- "A atualização de status mistura automação por regra de negócio e transição administrativa controlada. O orçamento
  gerado leva a OS para aguardando aprovação; execução, finalização e entrega são transições validadas pelo domínio."
- "Não há vulnerabilidades críticas ou altas abertas nos scans finais."
- "Os riscos médios residuais estão documentados e aceitos temporariamente."
- "SonarQube, Cucumber, Testcontainers e REST-assured não são citados como evidência executada porque não fazem parte da
  validação real desta entrega."
- "Os papéis Atendente, Mecânico e Responsável pelo estoque são papéis de negócio; no código, a autorização é feita por
  usuários, roles, perfis, permissões e `companyId`."
