# AutoCare Hub

## Tech Challenge FIAP — Fase 2

AutoCare Hub e uma solução academica para gestão de oficina mecanica. O projeto centraliza o cadastro de clientes,
veiculos, servicos, pecas, estoque e Ordens de Servico, permitindo que a oficina registre uma OS, gere orcamento,
aprove o atendimento e acompanhe o fluxo operacional pela API.

Na Fase 1, o projeto entregou um MVP backend em Java/Spring Boot com API REST, autenticação JWT, persistencia em
PostgreSQL, migrações Flyway, Swagger/OpenAPI, Docker, testes automatizados, cobertura JaCoCo e evidencias de
segurança. O feedback da fase indicou que a documentação da API ja estava detalhada no OpenAPI/Swagger, mas que o
README precisava trazer exemplos mais diretos de uso.

Na Fase 2, o projeto evolui para qualidade, escalabilidade, resiliencia, automação e deploy. O objetivo e preparar a
aplicação para Clean Architecture ou Arquitetura Hexagonal, revisar Docker, adicionar Kubernetes, Terraform, CI/CD,
deploy automatizado, escalabilidade automatica e documentação atualizada.

O repositorio tambem inclui um frontend demonstrativo em Vue/Vite em [frontend/](frontend/). Ele não substitui a API,
mas torna o produto mais visual e palpável, mostrando como pessoas e empresas poderiam consumir os fluxos do backend.

## Objetivos da Fase 2

- Refatorar pontos do código com Clean Code.
- Consolidar Clean Architecture ou Arquitetura Hexagonal.
- Melhorar testes unitarios, integração e fluxo de API.
- Revisar Dockerfile e Docker Compose.
- Criar manifestos Kubernetes.
- Criar scripts Terraform.
- Evoluir CI/CD para build, testes, imagem Docker e deploy.
- Preparar escalabilidade automatica, incluindo HPA no Kubernetes.
- Automatizar deploy da aplicação e aplicação dos manifests.
- Ajustar APIs conforme roteiro da Fase 2, sem quebrar o contrato existente sem necessidade.

## Funcionalidades principais

### Funcionalidades da Fase 1

- Clientes.
- Veiculos.
- Servicos.
- Pecas e insumos.
- Controle de estoque.
- Criação de Ordem de Servico.
- Geração e aprovação de orcamento.
- Acompanhamento da Ordem de Servico.
- Autenticação e autorização com JWT.
- Swagger/OpenAPI.

### Evoluções da Fase 2

- Abertura de OS retornando identificador unico: ja existe no fluxo `POST /api/v1/service-orders`.
- Consulta de status da OS: ja existe via `GET /api/v1/service-orders/{serviceOrderId}` e tracking.
- Endpoint de aprovação de orcamento: ja existe em `POST /api/v1/service-orders/{serviceOrderId}/budget/approve`.
- Endpoint de aprovação/recusa de orcamento por notificação externa em `POST /api/v1/service-orders/{serviceOrderId}/budget/decision`.
- Listagem de OS ordenada por prioridade/status e data.
- Exclusão logica da listagem principal de OS finalizadas e entregues.
- Atualização de status via ferramenta externa, como email, em `POST /api/v1/service-orders/{serviceOrderId}/status/external`.
- Preparação para Kubernetes, Terraform e CI/CD de deploy.

## Arquitetura da aplicação

O backend atual e um monolito Spring Boot organizado com Arquitetura Hexagonal/Clean Architecture: `domain` concentra
modelos e regras, `application` orquestra casos de uso e portas, `infrastructure` implementa adaptadores de persistencia
e segurança, e `interfaces` adapta o contrato REST/OpenAPI para a aplicação.

Na refatoração da Fase 2, regras de gestão de usuários foram removidas do `UsersController` e movidas para casos de uso
e política de aplicação. A autenticação e o hash de senhas tambem passaram a depender das portas
`AuthenticationGateway` e `PasswordHasher`, implementadas por adaptadores em `infrastructure/security`, evitando
dependencia direta de Spring Security nos casos de uso.

Arquitetura atual:

- Backend monolitico Java/Spring Boot.
- Banco de dados PostgreSQL.
- Migrações com Flyway.
- API REST documentada por Swagger/OpenAPI.
- Autenticação JWT.
- Frontend demonstrativo Vue/Vite.
- Dockerfile e Docker Compose para execução local.
- Pipeline GitHub Actions de qualidade, testes, build frontend e build Docker.

Arquitetura consolidada na Fase 2:

- Clean Architecture/Hexagonal Architecture dentro do monolito.
- Domínio independente de frameworks sempre que possivel.
- Camada de aplicação com casos de uso e portas.
- Infraestrutura com adaptadores de banco, segurança e integrações externas.
- Interfaces REST e futuras entradas externas desacopladas da regra de negocio.
- Kubernetes para deploy, Services, ConfigMaps, Secrets e HPA.
- Terraform para provisionamento da infraestrutura.
- CI/CD com build, testes, imagem Docker e deploy automatizado.

Desenho da arquitetura:

[docs/PHASE2_ARCHITECTURE.md](docs/PHASE2_ARCHITECTURE.md)

Documentação detalhada:

- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)
- [docs/DDD_DOCUMENTATION.md](docs/DDD_DOCUMENTATION.md)
- [docs/EVENT_STORMING.md](docs/EVENT_STORMING.md)
- [docs/TECHNICAL_REFINEMENT.md](docs/TECHNICAL_REFINEMENT.md)

## Estrutura de pastas

```text
.
|-- src/
|   |-- main/java/                  # Backend Spring Boot
|   |-- main/resources/             # Configuração, Flyway e assets estaticos
|   `-- test/java/                  # Testes unitarios e de integração
|-- docs/
|   |-- openapi/                    # Contrato OpenAPI
|   |-- ARCHITECTURE.md
|   |-- CI_CD.md
|   |-- DDD_DOCUMENTATION.md
|   |-- EVENT_STORMING.md
|   |-- SECURITY_REPORT.md
|   |-- STATIC_ANALYSIS.md
|   `-- TESTING.md
|-- frontend/                       # Frontend demonstrativo Vue/Vite
|-- security-reports/               # Evidencias de segurança versionadas
|-- scripts/                        # Scripts auxiliares de validação
|-- .github/workflows/
|   |-- quality.yml                  # Pipeline de qualidade
|   `-- deploy.yml                   # Pipeline de build, imagem e deploy Kubernetes
|-- k8s/                            # Manifests Kubernetes da Fase 2
|-- infra/                          # Terraform da Fase 2
|-- Dockerfile
|-- docker-compose.yml
|-- pom.xml
`-- README.md
```

## Tecnologias utilizadas

- Java 21.
- Spring Boot.
- Spring Web MVC.
- Spring Security.
- Spring Data JPA.
- Maven.
- PostgreSQL 16.
- Flyway.
- Docker.
- Docker Compose.
- Swagger/OpenAPI com Springdoc.
- JWT com JJWT.
- JUnit 5, Mockito, MockMvc e H2.
- JaCoCo.
- OWASP Dependency-Check.
- Spotless.
- Vue 3, Vite, Pinia, Vue Router e Lucide Vue no frontend demonstrativo.
- ESLint no frontend.
- GitHub Actions para pipeline de qualidade.
- Kubernetes.
- Terraform.
- CI/CD de deploy com GitHub Actions.

## Pre-requisitos

- Java 21.
- Maven.
- Docker.
- Docker Compose.
- Node.js 22 ou versão compativel com o frontend.
- npm.
- kubectl.
- Terraform.
- GitHub Actions como ferramenta atual de CI/CD.

## Variáveis de ambiente

Copie os arquivos de exemplo antes de rodar localmente:

```bash
cp .env.example .env
cp frontend/.env.example frontend/.env
```

No PowerShell:

```powershell
Copy-Item .env.example .env
Copy-Item frontend/.env.example frontend/.env
```

Não versione o `.env` real. Ele deve conter valores locais ou de ambiente seguro.

Variáveis principais do backend:

| Variável | Objetivo |
| -------- | -------- |
| `POSTGRES_DB` | Nome do banco local. |
| `POSTGRES_USER` | Usuario do PostgreSQL. |
| `POSTGRES_PASSWORD` | Senha local do PostgreSQL. |
| `POSTGRES_PORT` | Porta exposta do PostgreSQL. |
| `APP_PORT` | Porta da API no Docker Compose. |
| `FRONTEND_PORT` | Porta do frontend no Docker Compose. |
| `JWT_SECRET` | Segredo usado para assinar tokens JWT. |
| `JWT_EXPIRATION_MINUTES` | Tempo de expiração do token. |
| `APP_CORS_ALLOWED_ORIGINS` | Origens liberadas no CORS. |
| `DB_URL` | URL JDBC usada ao rodar o backend fora do container. |
| `DB_USERNAME` | Usuario do banco ao rodar com Maven. |
| `DB_PASSWORD` | Senha do banco ao rodar com Maven. |

Variáveis principais do frontend:

| Variável | Objetivo |
| -------- | -------- |
| `VITE_API_BASE_URL` | URL da API. Pode ficar vazia para usar o proxy do Vite/Nginx. |
| `VITE_DEMO_PASSWORD` | Senha demonstrativa opcional para a interface. |

## Como rodar localmente com Docker

Crie o arquivo local de ambiente a partir do template seguro:

```bash
cp .env.example .env
```

Edite o `.env` local e troque pelo menos `POSTGRES_PASSWORD` e `JWT_SECRET`. O arquivo `.env` real não deve ser
versionado.

Sequência recomendada para subir do zero:

```bash
docker compose down
docker compose down -v
docker compose down --remove-orphans
docker compose up -d --build
docker compose ps
docker compose logs -f
```

Comandos úteis de limpeza e acompanhamento:

```bash
docker compose down
docker compose down -v
docker compose down --remove-orphans
docker compose up -d --build
docker compose logs -f
docker compose logs -f backend
docker compose logs -f frontend
docker compose ps
```

URLs locais:

| Recurso | URL |
| ------- | --- |
| Frontend | <http://localhost:5173> |
| API | <http://localhost:8080> |
| Swagger UI | <http://localhost:8080/swagger-ui.html> |
| Healthcheck backend | <http://localhost:8080/actuator/health> |
| PostgreSQL | `localhost:5432` |

O `docker-compose.yml` sobe PostgreSQL, backend e frontend. O backend aguarda o banco ficar saudável antes de iniciar,
executa as migrations Flyway no startup e expõe `/actuator/health` para healthcheck. O frontend Nginx encaminha `/api`,
`/v3/api-docs`, `/swagger-ui` e `/openapi.yaml` para o backend, então a aplicação web funciona em
`http://localhost:5173` sem configurar uma URL absoluta de API.

Para desenvolvimento fora do Nginx, o CORS de dev vem de `APP_CORS_ALLOWED_ORIGINS` no `.env.example`:

```text
http://localhost:5173,http://127.0.0.1:5173
```

## Como rodar backend localmente

Suba apenas o PostgreSQL pelo Docker Compose:

```bash
docker compose up -d postgres
mvn spring-boot:run
```

O backend usa as variáveis `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` e demais configurações do `.env` ou do
ambiente local.

## Como rodar frontend localmente

```bash
cd frontend
npm install
npm run dev
```

O frontend le `VITE_API_BASE_URL` de `frontend/.env`. Deixe vazio para usar o proxy configurado no Vite/Nginx, ou defina
a URL da API quando necessario.

## Como rodar fora do notebook com Codespaces

Use esta opção quando o notebook não suportar Docker Desktop localmente. A configuração em [.devcontainer](.devcontainer)
prepara um ambiente remoto com Java 21, Maven, Node.js 22, Terraform e PostgreSQL.

Passos:

1. Faça push do repositório para o GitHub.
2. No GitHub, abra `Code > Codespaces > Create codespace on main` ou na branch que estiver usando.
3. Aguarde o `postCreateCommand` baixar dependências Maven e npm.
4. No terminal do Codespace, rode o backend:

```bash
mvn spring-boot:run
```

5. Em outro terminal do Codespace, rode o frontend:

```bash
cd frontend
npm run dev -- --host 0.0.0.0
```

O Codespaces expõe as portas `8080` e `5173` pelo painel `Ports`. O PostgreSQL roda dentro do ambiente remoto, então o
notebook usa apenas navegador/terminal e não precisa abrir Docker Desktop.

## Swagger/OpenAPI

- Swagger UI local: <http://localhost:8080/swagger-ui.html>
- Contrato OpenAPI: [docs/openapi/openapi.yaml](docs/openapi/openapi.yaml)
- Collection: [docs/postman/autocarehub-phase2.postman_collection.json](docs/postman/autocarehub-phase2.postman_collection.json)

Para autenticar no Swagger:

1. Execute `POST /api/v1/auth/login`.
2. Copie o token retornado.
3. Clique em `Authorize`.
4. Informe `Bearer <token>`.

## Fluxo rapido da API

Os exemplos abaixo são minimos e usam dados seed quando possivel. Para detalhes completos de schemas, consulte o
OpenAPI.

### 1. Login e token JWT

- Objetivo: autenticar usuário e obter token.
- Metodo: `POST`.
- Endpoint: `/api/v1/auth/login`.
- Bearer token: não.

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@autocarehub.com","password":"autocare123"}'
```

Guarde o token retornado:

```bash
TOKEN="[TOKEN_RETORNADO]"
```

### 2. Criar cliente

- Objetivo: cadastrar cliente.
- Metodo: `POST`.
- Endpoint: `/api/v1/customers`.
- Bearer token: sim.

```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Yasmin Barcelos",
    "document": "52998224725",
    "phone": "11987654321",
    "email": "yasmin.cliente@example.com",
    "address": {
      "street": "Avenida Paulista",
      "number": "1000",
      "neighborhood": "Bela Vista",
      "city": "São Paulo",
      "state": "SP",
      "zipCode": "01310-100"
    }
  }'
```

### 3. Listar clientes

- Objetivo: consultar clientes cadastrados.
- Metodo: `GET`.
- Endpoint: `/api/v1/customers?page=0&size=10`.
- Bearer token: sim.

```bash
curl http://localhost:8080/api/v1/customers?page=0\&size=10 \
  -H "Authorization: Bearer $TOKEN"
```

### 4. Criar veiculo

- Objetivo: cadastrar veiculo vinculado a cliente existente.
- Metodo: `POST`.
- Endpoint: `/api/v1/vehicles`.
- Bearer token: sim.

```bash
curl -X POST http://localhost:8080/api/v1/vehicles \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "10000000-0000-0000-0000-000000000001",
    "plate": "TST1A23",
    "brand": "Honda",
    "model": "Civic Touring",
    "year": 2021,
    "mileage": 42000
  }'
```

### 5. Listar veiculos

- Objetivo: consultar veiculos.
- Metodo: `GET`.
- Endpoint: `/api/v1/vehicles?page=0&size=10&active=true`.
- Bearer token: sim.

```bash
curl "http://localhost:8080/api/v1/vehicles?page=0&size=10&active=true" \
  -H "Authorization: Bearer $TOKEN"
```

### 6. Listar servicos

- Objetivo: consultar catalogo de servicos.
- Metodo: `GET`.
- Endpoint: `/api/v1/workshop-services?page=0&size=10`.
- Bearer token: sim.

```bash
curl "http://localhost:8080/api/v1/workshop-services?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

### 7. Listar pecas

- Objetivo: consultar pecas e insumos.
- Metodo: `GET`.
- Endpoint: `/api/v1/parts?page=0&size=10&active=true`.
- Bearer token: sim.

```bash
curl "http://localhost:8080/api/v1/parts?page=0&size=10&active=true" \
  -H "Authorization: Bearer $TOKEN"
```

### 8. Abrir Ordem de Servico

- Objetivo: criar OS e receber identificador unico na resposta.
- Metodo: `POST`.
- Endpoint: `/api/v1/service-orders`.
- Bearer token: sim.

```bash
curl -X POST http://localhost:8080/api/v1/service-orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerDocument": "12345678909",
    "vehicleId": "20000000-0000-0000-0000-000000000001",
    "diagnosticNotes": "Cliente relata ruido ao frear e vibração no pedal.",
    "services": [
      {
        "serviceId": "30000000-0000-0000-0000-000000000004",
        "quantity": 1
      }
    ],
    "parts": [
      {
        "partId": "40000000-0000-0000-0000-000000000005",
        "quantity": 1
      }
    ],
    "generateBudget": true
  }'
```

### 9. Gerar orcamento

- Objetivo: gerar orcamento para OS existente.
- Metodo: `POST`.
- Endpoint: `/api/v1/service-orders/{serviceOrderId}/budget/generate`.
- Bearer token: sim.

```bash
SERVICE_ORDER_ID="[ID_DA_OS]"

curl -X POST "http://localhost:8080/api/v1/service-orders/$SERVICE_ORDER_ID/budget/generate" \
  -H "Authorization: Bearer $TOKEN"
```

### 10. Aprovar orcamento

- Objetivo: aprovar orcamento gerado.
- Metodo: `POST`.
- Endpoint: `/api/v1/service-orders/{serviceOrderId}/budget/approve`.
- Bearer token: sim.

```bash
curl -X POST "http://localhost:8080/api/v1/service-orders/$SERVICE_ORDER_ID/budget/approve" \
  -H "Authorization: Bearer $TOKEN"
```

### 11. Aprovar ou recusar orcamento por notificação externa

- Objetivo: permitir que uma ferramenta externa, como email, registre aprovação ou recusa.
- Metodo: `POST`.
- Endpoint: `/api/v1/service-orders/{serviceOrderId}/budget/decision`.
- Bearer token: sim. Cliente dono da OS, administrador ou colaborador da oficina.
- Body:

```json
{
  "decision": "REJECTED",
  "source": "email",
  "reason": "Cliente recusou o orcamento por email."
}
```

### 12. Consultar status da OS

- Objetivo: consultar andamento pelo identificador da OS.
- Metodo: `GET`.
- Endpoint: `/api/v1/service-orders/{serviceOrderId}`.
- Bearer token: sim.

```bash
curl "http://localhost:8080/api/v1/service-orders/$SERVICE_ORDER_ID" \
  -H "Authorization: Bearer $TOKEN"
```

Tambem existe tracking por id da OS, CPF/CNPJ ou placa:

```bash
curl "http://localhost:8080/api/v1/service-orders/tracking?serviceOrderId=$SERVICE_ORDER_ID" \
  -H "Authorization: Bearer $TOKEN"
```

### 13. Listar OS por filtros atuais

- Objetivo: listar Ordens de Servico.
- Metodo: `GET`.
- Endpoint: `/api/v1/service-orders?page=0&size=10&status=IN_PROGRESS`.
- Bearer token: sim.

```bash
curl "http://localhost:8080/api/v1/service-orders?page=0&size=10&status=IN_PROGRESS" \
  -H "Authorization: Bearer $TOKEN"
```

Listagem ordenada por prioridade/status e data:

- Objetivo: atender requisito da Fase 2.
- Metodo: `GET`.
- Endpoint: `/api/v1/service-orders?page=0&size=10`.
- Status: implementado. A fila principal retorna `IN_PROGRESS`, `WAITING_APPROVAL`, `IN_DIAGNOSIS` e `RECEIVED`, nessa ordem de prioridade; dentro do mesmo status, retorna as OS mais antigas primeiro.

### 14. Atualizar status por ferramenta externa

- Objetivo: simular ferramenta externa, como email, notificando nova etapa da OS.
- Metodo: `POST`.
- Endpoint: `/api/v1/service-orders/{serviceOrderId}/status/external`.
- Bearer token: sim. Administrador ou colaborador da oficina.

```bash
curl -X POST "http://localhost:8080/api/v1/service-orders/$SERVICE_ORDER_ID/status/external" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "FINISHED",
    "source": "email",
    "message": "Mecânico informou finalização pelo fluxo externo."
  }'
```

## Endpoints principais

| Metodo | Endpoint | Protegido por JWT | Objetivo |
| ------ | -------- | ----------------- | -------- |
| `POST` | `/api/v1/auth/login` | Não | Autenticar usuário e emitir JWT. |
| `POST` | `/api/v1/customers` | Sim | Criar cliente. |
| `GET` | `/api/v1/customers` | Sim | Listar clientes. |
| `GET` | `/api/v1/customers/{customerId}` | Sim | Buscar cliente por id. |
| `PUT` | `/api/v1/customers/{customerId}` | Sim | Atualizar cliente. |
| `DELETE` | `/api/v1/customers/{customerId}` | Sim | Remover logicamente cliente. |
| `POST` | `/api/v1/vehicles` | Sim | Criar veiculo. |
| `GET` | `/api/v1/vehicles` | Sim | Listar veiculos. |
| `PUT` | `/api/v1/vehicles/{vehicleId}` | Sim | Atualizar quilometragem/status; placa, marca, modelo e ano permanecem imutáveis. |
| `GET` | `/api/v1/customers/{customerId}/vehicles` | Sim | Listar veiculos de um cliente. |
| `POST` | `/api/v1/workshop-services` | Sim | Criar servico da oficina. |
| `GET` | `/api/v1/workshop-services` | Sim | Listar servicos. |
| `POST` | `/api/v1/parts` | Sim | Criar peca ou insumo. |
| `GET` | `/api/v1/parts` | Sim | Listar pecas e insumos. |
| `PATCH` | `/api/v1/parts/{partId}/stock` | Sim | Atualizar estoque. |
| `POST` | `/api/v1/parts/{partId}/stock-movement` | Sim | Registrar movimento de estoque. |
| `POST` | `/api/v1/service-orders` | Sim | Abrir Ordem de Servico e retornar identificador unico. |
| `GET` | `/api/v1/service-orders` | Sim | Listar Ordens de Servico com filtros atuais. |
| `GET` | `/api/v1/service-orders/{serviceOrderId}` | Sim | Consultar OS e status. |
| `GET` | `/api/v1/service-orders/tracking` | Sim | Acompanhar progresso da OS. |
| `POST` | `/api/v1/service-orders/{serviceOrderId}/services` | Sim | Adicionar servico a OS. |
| `POST` | `/api/v1/service-orders/{serviceOrderId}/parts` | Sim | Adicionar peca a OS. |
| `POST` | `/api/v1/service-orders/{serviceOrderId}/budget/generate` | Sim | Gerar orcamento. |
| `POST` | `/api/v1/service-orders/{serviceOrderId}/budget/approve` | Sim | Aprovar orcamento. |
| `PATCH` | `/api/v1/service-orders/{serviceOrderId}/status` | Sim | Atualizar status da OS. |
| `GET` | `/api/v1/service-orders/metrics/average-execution-time` | Sim | Consultar tempo medio de execução. |
| `POST` | `/api/v1/service-orders/{serviceOrderId}/budget/decision` | Sim | Aprovar ou recusar orcamento por notificação externa. |
| `POST` | `/api/v1/service-orders/{serviceOrderId}/status/external` | Sim | Atualizar status por ferramenta externa simulada. |
| `GET` | `/api/v1/service-orders` | Sim | Listar OS ordenadas por prioridade/status e data, ocultando finalizadas e entregues. |

## Testes

Executar testes:

```bash
mvn clean test
```

Executar verificação completa:

```bash
mvn clean verify
```

Relatorio JaCoCo local:

```text
target/site/jacoco/index.html
```

Cobertura minima configurada no projeto:

- 90% de instruções.
- 90% de linhas.
- 90% de branches.

Documentação complementar de testes:

- [docs/TESTING.md](docs/TESTING.md)
- [docs/STATIC_ANALYSIS.md](docs/STATIC_ANALYSIS.md)

## Seguranca

O projeto possui:

- Autenticação e autorização com JWT.
- Validação de CPF/CNPJ.
- Validação de placa.
- Configuração de CORS por variável de ambiente.
- Uso de `.env.example` para evitar versionamento de secrets reais.
- `.gitignore` cobrindo `.env`, arquivos locais de chave/certificado, kubeconfig local e estado sensível do Terraform.
- Kubernetes Secrets com placeholders no repositório e criação de valores reais por ambiente/CI.
- Relatorios de vulnerabilidades e evidencias de scans em `security-reports/`.
- OWASP Dependency-Check no backend.
- `npm audit` no frontend.
- Evidencias complementares de Docker Scout, OWASP ZAP, Gitleaks e Semgrep documentadas na entrega.

Resultados de scan devem ser atualizados somente apos execução real das ferramentas. A revisão da Fase 2 fica
consolidada no relatório abaixo, incluindo pendências quando alguma ferramenta local não estiver disponível.

Documentos:

- [docs/SECURITY_REPORT.md](docs/SECURITY_REPORT.md)
- [docs/SECURITY_SCAN_GUIDE.md](docs/SECURITY_SCAN_GUIDE.md)

## Docker

Arquivos atuais:

- [Dockerfile](Dockerfile)
- [docker-compose.yml](docker-compose.yml)
- [frontend/Dockerfile](frontend/Dockerfile)

Comandos principais:

```bash
docker compose config --quiet
docker compose build
docker compose up -d --build
docker compose logs -f
docker compose down
```

Docker na Fase 2:

- Dockerfile backend com build multi-stage.
- Dockerfile frontend em [frontend/Dockerfile](frontend/Dockerfile).
- Docker Compose para execução local com PostgreSQL, backend e frontend.
- Variáveis por ambiente via `.env.example`.
- Publicação de imagens preparada no workflow [.github/workflows/deploy.yml](.github/workflows/deploy.yml).

## Kubernetes

Status: implementado para demonstração local/acadêmica.

Local:

```text
k8s/
```

Manifestos principais:

- `00-namespace.yaml`: namespace `autocarehub`.
- `01-configmap.yaml`: variáveis não sensíveis.
- `02-secret.yaml`: placeholders seguros para senha do banco e segredo JWT.
- `03-postgres-pvc.yaml`, `04-postgres-deployment.yaml`, `05-postgres-service.yaml`: PostgreSQL demonstrativo no cluster.
- `06-backend-deployment.yaml`, `07-backend-service.yaml`, `08-backend-hpa.yaml`: API Spring Boot, Service interno `backend` e HPA por CPU/memoria.
- `09-frontend-deployment.yaml`, `10-frontend-service.yaml`, `11-frontend-hpa.yaml`: frontend Vue/Nginx e HPA por CPU.

Antes de aplicar, substitua os placeholders em `k8s/02-secret.yaml` por valores seguros do ambiente. Não versione secrets reais.

Validação local dos manifests:

```bash
kubectl apply --dry-run=client -f k8s/
```

Comandos principais:

```bash
kubectl apply -f k8s/
kubectl get pods -n autocarehub
kubectl get svc -n autocarehub
kubectl get hpa -n autocarehub
kubectl logs -n autocarehub deploy/autocarehub-api
kubectl delete -f k8s/
```

Acesso local para demonstração:

```bash
kubectl port-forward -n autocarehub svc/backend 8080:8080
kubectl port-forward -n autocarehub svc/autocarehub-web 5173:8080
```

Limitações: o PostgreSQL em Kubernetes e demonstrativo para a Fase 2; ambientes produtivos devem avaliar banco gerenciado,
backup e replicação. O HPA depende do Metrics Server instalado no cluster. As imagens precisam estar publicadas no registry
ou carregadas no runtime local do cluster.

## Terraform

Status: implementado para demonstração local/acadêmica.

Local:

```text
infra/
```

Decisão de ambiente: o projeto não assume AWS, Azure, GCP ou outro provedor cloud. A infraestrutura da Fase 2 foi
modelada para Kubernetes local/acadêmico. O modo padrão usa um cluster ja existente, como `kind`, `minikube` ou cluster
disponibilizado para avaliação; opcionalmente, o Terraform cria um cluster local com `kind`.

Arquivos:

- `infra/main.tf`: criação opcional de cluster `kind`, provider Kubernetes e recursos base.
- `infra/variables.tf`: variáveis parametrizáveis.
- `infra/outputs.tf`: outputs uteis para conferencia e deploy.
- `infra/terraform.tfvars.example`: exemplo seguro com placeholders.
- `infra/README.md`: instruções detalhadas.

Recursos provisionados:

- Namespace `autocarehub`.
- ConfigMap `autocarehub-config`.
- Secret `autocarehub-secret`, com valores recebidos por variáveis locais/CI.
- PVC `autocarehub-postgres-data` para o PostgreSQL demonstrativo.

Comandos:

```bash
cd infra
terraform init
terraform fmt
terraform validate
terraform plan
terraform apply
terraform destroy
```

Para criar tambem um cluster local com `kind`, instale Docker Desktop e `kind`, depois execute:

```bash
terraform apply -var="create_kind_cluster=true"
```

Variáveis sensíveis devem ser informadas fora do repositorio, por exemplo:

```bash
export TF_VAR_postgres_password="substituir-localmente"
export TF_VAR_jwt_secret="segredo-com-pelo-menos-32-bytes"
```

Depois do `terraform apply`, aplique apenas os workloads Kubernetes que não são gerenciados pelo Terraform, conforme
detalhado em [infra/README.md](infra/README.md). Não aplique `k8s/02-secret.yaml` por cima do Secret criado pelo
Terraform, porque o manifesto Kubernetes usa placeholders.

Limitações: o modo `kind` cria cluster local, não cluster gerenciado em cloud. O Terraform não cria banco gerenciado em
cloud; ele provisiona a base necessaria no cluster configurado no kubeconfig local ou no cluster `kind` criado.

## CI/CD

Pipelines atuais:

- [.github/workflows/quality.yml](.github/workflows/quality.yml)
- [.github/workflows/deploy.yml](.github/workflows/deploy.yml)
- [docs/CI_CD.md](docs/CI_CD.md)

`quality.yml` executa:

- Spotless no backend.
- `mvn verify`.
- `npm ci`.
- Lint frontend.
- Build frontend.
- `npm audit`.
- Validação do Docker Compose.
- Build das imagens Docker.

`deploy.yml` executa:

- Build e testes do backend com `mvn -B verify`.
- Instalação, lint e build do frontend.
- Validação estrutural dos YAMLs de `k8s/`.
- `terraform fmt -check`, `terraform init -backend=false` e `terraform validate`.
- Build das imagens Docker do backend e frontend.
- Publicação das imagens no GitHub Container Registry quando a execução estiver em `main` ou for manual.
- Deploy real no Kubernetes somente quando os secrets necessários estiverem configurados.
- Aplicação de namespace, ConfigMap e PVC.
- Criação/atualização do Secret Kubernetes a partir dos GitHub Actions Secrets, sem usar placeholders.
- Aplicação do PostgreSQL, backend, frontend, Services e HPAs.
- Atualização dos Deployments para as imagens geradas no SHA da própria pipeline.
- Verificação de rollout e listagem de pods, services e HPAs.

Secrets necessários para deploy real:

| Secret | Uso |
| ------ | --- |
| `KUBE_CONFIG` | Kubeconfig em base64 para acesso ao cluster. |
| `POSTGRES_PASSWORD` | Senha do PostgreSQL usada no Secret Kubernetes. |
| `JWT_SECRET` | Segredo JWT com pelo menos 32 bytes. |

O push para o GitHub Container Registry usa `GITHUB_TOKEN`, secret automatico do GitHub Actions. Ele nao deve ser
exposto nem substituido por token pessoal sem necessidade.

Se qualquer secret obrigatório estiver ausente, a pipeline mantém build, testes, validações e build de imagens, mas registra
que o deploy real foi pulado. Esse comportamento é intencional para permitir execução acadêmica sem expor credenciais.

O deploy do banco é demonstrável pelo manifesto do PostgreSQL em Kubernetes e pelas migrations Flyway executadas no
startup do backend. A pipeline aplica o PVC e o Deployment do PostgreSQL antes dos workloads da aplicação.

Execução:

```bash
git push origin main
```

Tambem e possivel executar manualmente pela aba GitHub Actions, selecionando o workflow `Deploy` e acionando
`Run workflow`. Detalhes de variaveis, secrets e demonstracao estao em [docs/CI_CD.md](docs/CI_CD.md).

## Documentação complementar

| Documento | Link |
| --------- | ---- |
| DDD | [docs/DDD_DOCUMENTATION.md](docs/DDD_DOCUMENTATION.md) |
| Event Storming | [docs/EVENT_STORMING.md](docs/EVENT_STORMING.md) |
| Domain Storytelling | [docs/DOMAIN_STORYTELLING.md](docs/DOMAIN_STORYTELLING.md) |
| Requisitos | [docs/REQUIREMENTS.md](docs/REQUIREMENTS.md) |
| Arquitetura | [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) |
| Refinamento tecnico | [docs/TECHNICAL_REFINEMENT.md](docs/TECHNICAL_REFINEMENT.md) |
| OpenAPI | [docs/openapi/openapi.yaml](docs/openapi/openapi.yaml) |
| Collection Postman | [docs/postman/autocarehub-phase2.postman_collection.json](docs/postman/autocarehub-phase2.postman_collection.json) |
| Testes | [docs/TESTING.md](docs/TESTING.md) |
| Analise estatica | [docs/STATIC_ANALYSIS.md](docs/STATIC_ANALYSIS.md) |
| CI/CD | [docs/CI_CD.md](docs/CI_CD.md) |
| Seguranca | [docs/SECURITY_REPORT.md](docs/SECURITY_REPORT.md) |
| Guia de scan de segurança | [docs/SECURITY_SCAN_GUIDE.md](docs/SECURITY_SCAN_GUIDE.md) |
| Documento de entrega | [docs/DELIVERY_DOCUMENT.md](docs/DELIVERY_DOCUMENT.md) |
| Frontend demonstrativo | [frontend/README.md](frontend/README.md) |
| Desenho da arquitetura | [docs/PHASE2_ARCHITECTURE.md](docs/PHASE2_ARCHITECTURE.md) |
| Roteiro do video | [docs/PHASE2_VIDEO_SCRIPT.md](docs/PHASE2_VIDEO_SCRIPT.md) |
| Video | [PREENCHER APOS PUBLICAR NO YOUTUBE OU VIMEO] |

## Entrega da Fase 2

Checklist de preparação:

- [x] Codigo refatorado.
- [x] Dockerfile revisado.
- [x] `docker-compose.yml` revisado.
- [x] `/k8s` criado.
- [x] `/infra` criado.
- [x] Pipeline CI/CD de deploy criada.
- [x] README atualizado como documento principal.
- [x] Swagger/OpenAPI disponível em [docs/openapi/openapi.yaml](docs/openapi/openapi.yaml).
- [x] Collection da API adicionada ou linkada.
- [ ] Video de ate 15 minutos publicado e link preenchido.
- [ ] PDF final regenerado apos preencher o link real do video.
- [ ] Acesso ao usuário `soat-architecture` confirmado no GitHub antes do envio.

## Historico das fases

- Fase 1: MVP backend da oficina com API REST, JWT, Swagger/OpenAPI, Docker, testes, cobertura e relatorios de
  segurança.
- Fase 2: evolução para qualidade, arquitetura, escalabilidade, infraestrutura como código, Kubernetes, CI/CD e deploy
  automatizado.
