# AutoCare Hub

AutoCare Hub é um MVP acadêmico desenvolvido para o Tech Challenge FIAP. A entrega principal é uma API REST em Java/Spring Boot para gestão de uma oficina mecânica, cobrindo clientes, veículos, serviços, peças, estoque, Ordens de Serviço, orçamento, aprovação, acompanhamento pelo cliente, segurança JWT, Swagger/OpenAPI, Docker, testes automatizados e relatório de vulnerabilidades.

O repositório também inclui um frontend demonstrativo em Vue/Vite, localizado em `frontend/`, usado para apoiar a apresentação visual do MVP. A branch final de entrega é `main`.

## Sumário da entrega

A pasta `docs/` concentra a documentação usada na avaliação.

| Documento                     | Onde abrir                                                                                       | O que comprova                                                                                        |
|-------------------------------|--------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------|
| Documento final de entrega    | [docs/DELIVERY_DOCUMENT.md](docs/DELIVERY_DOCUMENT.md)                                           | Dados da entrega, links, escopo, segurança, testes, vulnerabilidades e conclusão.                     |
| PDF final gerado              | `output/pdf/AutoCare_Hub_Tech_Challenge_Entrega_Final.pdf`                                       | Versão em PDF do documento final para envio.                                                          |
| DDD                           | [docs/DDD_DOCUMENTATION.md](docs/DDD_DOCUMENTATION.md)                                           | Domínio, linguagem ubíqua, subdomínios, bounded contexts, entidades, value objects e agregados.       |
| Domain Storytelling           | [docs/DOMAIN_STORYTELLING.md](docs/DOMAIN_STORYTELLING.md)                                       | Histórias do domínio por ator, objetos de trabalho, atividades e cenários alternativos.               |
| Levantamento de requisitos    | [docs/REQUIREMENTS.md](docs/REQUIREMENTS.md)                                                     | Personas, jornada da solução, requisitos funcionais, requisitos não funcionais e rastreabilidade.     |
| Arquitetura                   | [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)                                                     | Arquitetura em camadas, HLD, LLD, C4 Model, decisões técnicas e relação com requisitos.               |
| Refinamento técnico           | [docs/TECHNICAL_REFINEMENT.md](docs/TECHNICAL_REFINEMENT.md)                                     | Jornada técnica da OS, decisões de implementação, validações, riscos tratados e aderência do backend. |
| Event Storming                | [docs/EVENT_STORMING.md](docs/EVENT_STORMING.md)                                                 | Comandos, eventos, políticas, pontos de atenção e fluxos de OS e estoque.                             |
| Swagger/OpenAPI               | [docs/openapi/openapi.yaml](docs/openapi/openapi.yaml) e `http://localhost:8080/swagger-ui.html` | Contrato REST versionado e interface local para testar a API.                                         |
| Relatório de vulnerabilidades | [docs/SECURITY_REPORT.md](docs/SECURITY_REPORT.md)                                               | Scans executados, vulnerabilidades encontradas, correções aplicadas e riscos aceitos.                 |
| Guia de scans                 | [docs/SECURITY_SCAN_GUIDE.md](docs/SECURITY_SCAN_GUIDE.md)                                       | Comandos para reproduzir scans e gerar evidências locais.                                             |
| Análise estática e qualidade  | [docs/STATIC_ANALYSIS.md](docs/STATIC_ANALYSIS.md)                                               | Ferramentas de qualidade, cobertura, lint, análise estática e evidências locais.                      |
| Estratégia de testes          | [docs/TESTING.md](docs/TESTING.md)                                                               | Testes unitários, integração REST, fluxo completo de API, comandos e cobertura JaCoCo.                |
| Frontend demonstrativo        | [frontend/README.md](frontend/README.md)                                                         | Como executar e entender o frontend usado na demonstração.                                            |

## Dados da entrega

| Campo                           | Valor                                                 |
|---------------------------------|-------------------------------------------------------|
| Projeto                         | AutoCare Hub                                          |
| Responsável                     | Yasmin Barcelos Pires                                 |
| RM                              | RM370897                                              |
| Discord                         | `yxsbx`                                               |
| Repositório                     | <https://github.com/yxsbx/SOAT-FIAP>                  |
| Branch final                    | `main`                                                |
| Acesso de avaliação             | Usuário `soat-architecture` com acesso Read concedido |
| Data consolidada nos documentos | 29/06/2026                                            |

## Escopo do MVP

O Tech Challenge solicita um MVP backend para uma oficina mecânica. O AutoCare Hub entrega:

- cadastro de clientes;
- cadastro de veículos;
- cadastro de serviços;
- cadastro de peças e insumos;
- controle de estoque;
- criação de Ordem de Serviço;
- geração e aprovação de orçamento;
- mudança de status da Ordem de Serviço;
- acompanhamento da OS pelo cliente via API;
- autenticação e autorização com JWT;
- Swagger/OpenAPI;
- Docker e execução local reproduzível;
- testes automatizados;
- cobertura JaCoCo;
- análise estática;
- relatório de vulnerabilidades.

Ficam fora do MVP: pagamento online, envio real de e-mail/SMS/WhatsApp, integração com fornecedores, ERP, mensageria, app mobile real e deploy produtivo em cloud.

## Visão técnica resumida

Backend:

- Java 21;
- Spring Boot;
- Spring Web MVC;
- Spring Security;
- Spring Data JPA;
- PostgreSQL 16;
- Flyway;
- Maven;
- JJWT;
- Springdoc Swagger UI;
- JaCoCo;
- OWASP Dependency-Check;
- JUnit 5, Mockito, MockMvc e H2.

Frontend demonstrativo:

- Vue 3;
- Vite;
- Pinia;
- Vue Router;
- Lucide Vue;
- ESLint.

Infraestrutura:

- Docker;
- Docker Compose;
- Nginx para servir o frontend em container.

## Arquitetura em uma página

O backend é um monolito em camadas:

```text
src/main/java/br/com/autocarehub
|-- domain
|   |-- enums
|   |-- exception
|   |-- model
|   |-- service
|   `-- valueobject
|-- application
|   |-- port
|   `-- usecase
|-- infrastructure
|   |-- config
|   |-- persistence
|   `-- security
`-- interfaces
    `-- rest
        |-- controller
        |-- exception
        `-- mapper
```

Detalhamento completo:

- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md): HLD, LLD, C4 e decisões arquiteturais;
- [docs/REQUIREMENTS.md](docs/REQUIREMENTS.md): RFs, RNFs e matriz de rastreabilidade;
- [docs/DDD_DOCUMENTATION.md](docs/DDD_DOCUMENTATION.md): domínio, linguagem ubíqua e agregados;
- [docs/DELIVERY_DOCUMENT.md](docs/DELIVERY_DOCUMENT.md): consolidação final da entrega.

## Execução com Docker

Pré-requisitos:

- Docker;
- Docker Compose.

Crie o `.env` a partir do exemplo.

Linux, macOS ou Git Bash:

```bash
cp .env.example .env
```

PowerShell:

```powershell
Copy-Item .env.example .env
```

Preencha pelo menos:

```text
POSTGRES_PASSWORD=[PREENCHER - senha local do PostgreSQL]
JWT_SECRET=[PREENCHER - segredo local com pelo menos 32 bytes]
```

Para demonstrar o projeto rodando do zero, pare containers antigos, remova volumes locais do banco e suba tudo novamente:

```powershell
docker compose down
docker compose down --remove-orphans
docker compose down -v
docker compose up -d --build
docker compose ps
```

Acompanhe a inicialização da API e as migrations Flyway criando a base:

```powershell
docker compose logs -f app
```

O serviço da API no `docker-compose.yml` se chama `app`. Portanto, o comando correto é:

```powershell
docker compose logs -f app
```

URLs locais:

```text
Frontend: http://localhost:5173
API: http://localhost:8080
Swagger: http://localhost:8080/swagger-ui.html
PostgreSQL: localhost:5432
```

Parar os serviços sem apagar dados:

```powershell
docker compose down
```

Remover o volume local do banco para recriar a base do zero:

```powershell
docker compose down -v
```

Opcional, com cuidado:

```powershell
docker volume prune
```

Esse comando remove volumes Docker não usados por outros projetos. Use apenas se souber que não precisa desses volumes.

## Execução em desenvolvimento

Backend com PostgreSQL pelo Docker Compose:

```powershell
docker compose up -d postgres
mvn spring-boot:run
```

Frontend com hot reload:

```powershell
cd frontend
npm ci
npm run dev
```

## Usuários de demonstração

Os usuários seed são carregados por:

```text
src/main/resources/db/migration/V1__create_autocarehub_baseline.sql
```

A senha universal dos usuários seed abaixo é exclusiva do ambiente local acadêmico:

```text
autocare123
```

| Usuário                               | Perfil                       | Senha         |
|---------------------------------------|------------------------------|---------------|
| `admin@autocarehub.com`               | Admin técnico inicial        | `autocare123` |
| `master@autocarehub.com`              | Admin Master da plataforma   | `autocare123` |
| `oficina.admin@autocarehub.com`       | Admin de oficina             | `autocare123` |
| `loja.admin@autocarehub.com`          | Admin de loja de peças       | `autocare123` |
| `oficina.funcionario@autocarehub.com` | Funcionário de oficina       | `autocare123` |
| `loja.funcionario@autocarehub.com`    | Funcionário de loja de peças | `autocare123` |
| `cliente@autocarehub.com`             | Cliente final demo           | `autocare123` |

Exemplo de login:

```powershell
curl -X POST http://localhost:8080/api/v1/auth/login `
  -H "Content-Type: application/json" `
  -d "{"username":"admin@autocarehub.com","password":"autocare123"}"
```

## Swagger e endpoints

Swagger local:

```text
http://localhost:8080/swagger-ui.html
```

Contrato OpenAPI versionado:

```text
docs/openapi/openapi.yaml
```

Autenticação no Swagger:

1. Execute `POST /api/v1/auth/login`.
2. Copie o token retornado.
3. Clique em `Authorize`.
4. Informe `Bearer <token>`.

Principais grupos de endpoints:

| Grupo               | Rotas principais                                                                             |
|---------------------|----------------------------------------------------------------------------------------------|
| Autenticação        | `POST /api/v1/auth/login`                                                                    |
| Clientes            | `/api/v1/customers`                                                                          |
| Veículos            | `/api/v1/vehicles`                                                                           |
| Serviços            | `/api/v1/workshop-services`                                                                  |
| Peças e estoque     | `/api/v1/parts`                                                                              |
| Ordens de Serviço   | `/api/v1/service-orders`                                                                     |
| Orçamento           | `/api/v1/service-orders/{serviceOrderId}/budget/generate` e `/api/v1/service-orders/{serviceOrderId}/budget/approve` |
| Tracking do cliente | `/api/v1/service-orders/tracking`                                                            |
| Métricas da OS      | `/api/v1/service-orders/metrics/average-execution-time`                                      |
| Usuários            | `/api/v1/users` e `/api/v1/users/me`                                                         |

## Validação rápida

Com o ambiente Docker ativo:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/validate-delivery.ps1
```

Esse script valida frontend, OpenAPI, login JWT e leitura dos dados seed.

Validação completa backend:

```powershell
mvn spotless:check
mvn test
mvn clean verify
```

Validação frontend:

```powershell
cd frontend
npm run lint
npm run build
npm audit --audit-level=low
cd ..
```

Scan de dependências backend:

```powershell
mvn dependency-check:check -DautoUpdate=false
```

O guia completo de scans está em:

```text
docs/SECURITY_SCAN_GUIDE.md
```

## Evidências de qualidade

Resultado de qualidade revalidado em 29/06/2026:

| Área                   | Resultado                                          |
|------------------------|----------------------------------------------------|
| Testes Maven           | 143 testes, 0 falhas, 0 erros e 0 ignorados        |
| Cobertura JaCoCo       | 96,31% instruções, 97,25% linhas e 90,12% branches |
| Gate de cobertura      | 90% instruções, 90% linhas e 90% branches          |
| Frontend lint          | 0 erros e 0 warnings                               |
| Frontend build         | Aprovado                                           |
| npm audit              | 0 vulnerabilidades                                 |
| OWASP Dependency-Check | 103 dependências e 0 vulnerabilidades              |
| Docker Scout backend   | 0 críticas, 0 altas e 1 média residual aceita      |
| Docker Scout frontend  | 0 críticas, 0 altas e 1 média residual aceita      |
| OWASP ZAP              | 0 falhas e 2 avisos revisados                      |
| Gitleaks               | 0 leaks em 36 commits                              |
| Semgrep                | 0 achados em 200 arquivos com 187 regras           |

O resultado fica acima da cobertura mínima de 80% exigida para a entrega. No JaCoCo, branches representam caminhos condicionais do código, como `if`, `else`, validações, exceções e transições de status. Por isso, o gate interno também exige 90% nessa métrica.

Relatórios e evidências:

```text
docs/SECURITY_REPORT.md
docs/SECURITY_SCAN_GUIDE.md
docs/STATIC_ANALYSIS.md
docs/TESTING.md
target/site/jacoco/index.html
target/site/jacoco/jacoco.csv
security-reports/backend-dependencies/dependency-check-report.html
security-reports/backend-dependencies/dependency-check-report.json
security-reports/frontend-dependencies/npm-audit-report.json
security-reports/docker/docker-scout-cves.txt
security-reports/docker/docker-scout-frontend-cves.txt
security-reports/dast/zap-api-report.html
security-reports/dast/zap-api-report.json
security-reports/secrets/gitleaks.json
security-reports/static-analysis/semgrep.json
```

Os arquivos em `security-reports/` ficam versionados como evidência revisada da entrega. A pasta `target/` continua fora do versionamento e permanece como saída local das ferramentas Maven.

O resumo oficial dos resultados está em:

```text
docs/SECURITY_REPORT.md
```

## CI

O workflow [.github/workflows/quality.yml](.github/workflows/quality.yml) executa em pushes e pull requests para `main`:

- Spotless;
- testes e cobertura Maven;
- lint sem warnings;
- build frontend;
- `npm audit`;
- validação do Docker Compose;
- build das imagens Docker.

## Limitações conhecidas

As limitações completas estão registradas em [docs/DELIVERY_DOCUMENT.md](docs/DELIVERY_DOCUMENT.md). Resumo:

- não há pagamento online;
- não há envio real de e-mail, SMS ou WhatsApp;
- histórico de status da OS é simplificado para o MVP;
- multiempresa/multitenancy está simplificado;
- Swagger fica público no ambiente local acadêmico;
- OWASP ZAP foi executado como análise dinâmica complementar da API e teve apenas avisos revisados;
- a imagem backend mantém 1 CVE média em `jackson-databind` transitivo, aceita temporariamente porque a versão corrigida indicada pelo Docker Scout ainda não estava disponível no Maven Central;
- a imagem frontend mantém 1 CVE média de BusyBox, aceita temporariamente porque o Docker Scout não indicou versão corrigida disponível.

Essas limitações não impedem a execução do fluxo principal exigido no Tech Challenge.

## Evoluções técnicas

O detalhamento está em [docs/DELIVERY_DOCUMENT.md](docs/DELIVERY_DOCUMENT.md) e [docs/SECURITY_REPORT.md](docs/SECURITY_REPORT.md). Principais evoluções futuras:

- ampliar cenários extremos e fluxos de regressão;
- criar auditoria de ações sensíveis;
- melhorar histórico detalhado de status da OS;
- integrar notificações reais para cliente;
- restringir Swagger por ambiente/perfil;
- evoluir multiempresa/multitenancy;
- automatizar scans de segurança em pipeline;
- reexecutar scans de segurança em cada ciclo de entrega.
