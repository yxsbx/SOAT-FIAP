# AutoCare Hub

AutoCare Hub é um MVP acadêmico desenvolvido para o Tech Challenge FIAP. A entrega principal é uma API REST em
Java/Spring Boot para gestão de uma oficina mecânica, cobrindo clientes, veículos, serviços, peças, estoque, Ordens de
Serviço, orçamento, aprovação, acompanhamento pelo cliente, segurança JWT, Swagger, Docker, testes e relatório de
vulnerabilidades.

O repositório também inclui um frontend Vue/Vite em `frontend/` para apoiar a demonstração visual. A branch final de
entrega é `main`.

## Sumário da Entrega

`docs/` concentra a documentação pública exigida para avaliação. O roteiro do vídeo fica em `video/` porque é material
de apresentação, não documento final para PDF.

Documentos oficiais:

- **Item exigido:**  Documento final de entrega
  - **Onde abrir:**  [docs/DELIVERY_DOCUMENT.md](docs/DELIVERY_DOCUMENT.md)
  - **O que comprova:**  Dados da entrega, links, escopo, arquitetura, DDD, segurança, testes, Docker, vulnerabilidades,
    limitações e conclusão.

- **Item exigido:**  PDF final gerado
  - **Onde abrir:**  `output/pdf/AutoCare_Hub_Tech_Challenge_Entrega_Final.pdf`
  - **O que comprova:**  Versão em PDF do documento final para envio.

- **Item exigido:**  DDD
  - **Onde abrir:**  [docs/DDD_DOCUMENTATION.md](docs/DDD_DOCUMENTATION.md)
  - **O que comprova:**  Domínio, linguagem ubíqua, subdomínios, bounded contexts, entidades, value objects, agregados,
    políticas e fluxos.

- **Item exigido:**  Event Storming
  - **Onde abrir:**  [docs/EVENT_STORMING.md](docs/EVENT_STORMING.md)
  - **O que comprova:**  Comandos, eventos, políticas, exceções e fluxos de OS e estoque.

- **Item exigido:**  Swagger/OpenAPI
  - **Onde abrir:**  [docs/openapi/openapi.yaml](docs/openapi/openapi.yaml) e `http://localhost:8080/swagger-ui.html`
  - **O que comprova:**  Contrato REST versionado e interface local para testar a API.

- **Item exigido:**  Relatório de vulnerabilidades
  - **Onde abrir:**  [docs/SECURITY_REPORT.md](docs/SECURITY_REPORT.md)
  - **O que comprova:**  Scans executados, vulnerabilidades encontradas, correções e riscos aceitos.

Documentos de apoio:

| Material               | Onde abrir                                                     |
| ---------------------- | -------------------------------------------------------------- |
| Guia de scans          | [docs/SECURITY_SCAN_GUIDE.md](docs/SECURITY_SCAN_GUIDE.md)     |
| Roteiro do vídeo       | [video/VIDEO_SCRIPT.md](video/VIDEO_SCRIPT.md)                 |
| Frontend demonstrativo | [frontend/README.md](frontend/README.md)                       |

## Dados da Entrega

| Campo                           | Valor                                                |
| ------------------------------- | ---------------------------------------------------- |
| Projeto                         | AutoCare Hub                                         |
| Responsável                     | Yasmin Barcelos Pires                                |
| RM                              | RM370897                                             |
| Discord                         | `yxsbx`                                              |
| Repositório                     | <https://github.com/yxsbx/SOAT-FIAP>                 |
| Branch final                    | `main`                                               |
| Acesso de avaliação             | Usuário `soatarchitecture` com acesso Read concedido |
| Data consolidada nos documentos | 28/06/2026                                           |

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
- testes automatizados, cobertura e relatório de vulnerabilidades.

## Visão Técnica Resumida

Backend:

- Java 21;
- Spring Boot 4.1.0;
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
- JUnit 5, Mockito, H2 e Testcontainers.

Frontend demonstrativo:

- Vue 3;
- Vite 8;
- Pinia;
- Vue Router;
- Lucide Vue;
- ESLint.

Infraestrutura:

- Docker;
- Docker Compose;
- Nginx para servir o frontend em container.

## Arquitetura em uma Página

O backend é um monolito em camadas:

```text
src/main/java/br/com/autocarehub
|-- domain
|   |-- enums
|   |-- exception
|   |-- model
|   |-- policy
|   |-- service
|   `-- valueobject
|-- application
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

Detalhamento completo: [docs/DDD_DOCUMENTATION.md](docs/DDD_DOCUMENTATION.md) e
[docs/DELIVERY_DOCUMENT.md](docs/DELIVERY_DOCUMENT.md).

## Execução com Docker

Pré-requisitos:

- Docker;
- Docker Compose.

Crie o `.env` a partir do exemplo:

```powershell
# Linux/macOS/Git Bash
cp .env.example .env

# PowerShell
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

O serviço da API no `docker-compose.yml` se chama `app`. Portanto, o comando correto é `docker compose logs -f app`;
`docker compose logs -f backend` não funciona neste projeto.

URLs:

```text
Frontend: http://localhost:5173
API: http://localhost:8080
Swagger: http://localhost:8080/swagger-ui.html
PostgreSQL: localhost:5432
```

O frontend usa proxy reverso para a API. Assim, ele também pode ser acessado pelo IP local da máquina, por exemplo
`http://192.168.x.x:5173`, sem depender de CORS entre navegador e backend.

Parar os serviços sem apagar dados:

```powershell
docker compose down
```

Remover o volume local do banco para recriar a base do zero:

```powershell
docker compose down -v
```

Opcional, com cuidado: `docker volume prune` remove volumes Docker não usados por outros projetos também. Use apenas se
você souber que não precisa desses volumes.

## Execução em Desenvolvimento

Backend:

```powershell
docker compose up -d postgres
mvn spring-boot:run
```

Frontend:

```powershell
cd frontend
npm ci
npm run dev
```

## Usuários de Demonstração

Os usuários seed são carregados por `src/main/resources/db/migration/V1__create_autocarehub_baseline.sql`.

A senha universal de todos os usuários seed abaixo é exclusiva do ambiente local:

```text
autocare123
```

| Usuário                               | Perfil                       | Senha         |
| ------------------------------------- | ---------------------------- | ------------- |
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
  -d "{\"username\":\"admin@autocarehub.com\",\"password\":\"autocare123\"}"
```

## Swagger e Endpoints

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

| Grupo                 | Rotas principais                                                                             |
| --------------------- | -------------------------------------------------------------------------------------------- |
| Autenticação          | `POST /api/v1/auth/login`                                                                    |
| Clientes              | `/api/v1/customers`                                                                          |
| Veículos              | `/api/v1/vehicles`                                                                           |
| Serviços              | `/api/v1/workshop-services`                                                                  |
| Peças e estoque       | `/api/v1/parts`                                                                              |
| Ordens de Serviço     | `/api/v1/service-orders`                                                                     |
| Orçamento             | `/api/v1/service-orders/{id}/budget/generate` e `/api/v1/service-orders/{id}/budget/approve` |
| Tracking do cliente   | `/api/v1/service-orders/tracking`                                                            |
| Métricas da OS        | `/api/v1/service-orders/metrics/average-execution-time`                                       |
| Usuários              | `/api/v1/users` e `/api/v1/users/me`                                                         |
| Leads de demonstração | `/api/v1/demo-leads`                                                                         |

## Validação Rápida

Com o ambiente Docker ativo:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/validate-delivery.ps1
```

Esse script valida frontend, OpenAPI, login JWT e leitura dos dados seed.

Validação completa backend:

```powershell
mvn verify
```

Validação frontend:

```powershell
cd frontend
npm run lint
npm run build
npm audit --audit-level=low
```

Scans de segurança:

```powershell
mvn dependency-check:check -DautoUpdate=false
```

Demais comandos e opções: [docs/SECURITY_SCAN_GUIDE.md](docs/SECURITY_SCAN_GUIDE.md).

## Evidências de Qualidade

Resultado de qualidade revalidado em 28/06/2026:

| Área                   | Resultado                                             |
| ---------------------- | ----------------------------------------------------- |
| Testes Maven           | 146 testes, 0 falhas, 0 erros, 0 ignorados            |
| Cobertura JaCoCo       | 96,36% instruções, 97,28% linhas e 90,28% branches    |
| Gate de cobertura      | 90% instruções, 90% linhas e 90% branches             |
| Frontend lint          | 0 erros e 0 warnings                                  |
| Frontend build         | Aprovado                                              |
| npm audit              | 0 vulnerabilidades                                    |
| OWASP Dependency-Check | 126 dependências, 0 vulneráveis                       |
| Docker Scout backend   | 0 vulnerabilidades                                    |
| Docker Scout frontend  | 0 críticas, 0 altas e 1 média sem correção disponível |
| Gitleaks               | 0 leaks em 36 commits                                 |
| Semgrep                | 0 achados em 200 arquivos com 187 regras              |

O resultado fica acima da cobertura mínima de 80% exigida para a entrega. No JaCoCo, branches representam caminhos
condicionais do código, como `if`, `else`, validações, exceções e transições de status. Por isso o gate interno também
exige 90% nessa métrica.

Relatórios e evidências gerados localmente:

```text
docs/SECURITY_REPORT.md
docs/SECURITY_SCAN_GUIDE.md
target/site/jacoco/index.html
target/site/jacoco/jacoco.csv
target/dependency-check/dependency-check-report.html
target/dependency-check/dependency-check-report.json
security-reports/frontend-dependencies/npm-audit-report.json
security-reports/docker/docker-scout-cves.txt
security-reports/docker/docker-scout-frontend-cves.txt
security-reports/secrets/gitleaks.json
security-reports/static-analysis/semgrep.json
```

Os arquivos em `target/` e `security-reports/` são saídas locais de ferramentas e ficam fora do versionamento. O resumo
oficial dos resultados está em [docs/SECURITY_REPORT.md](docs/SECURITY_REPORT.md).

## CI

O workflow [.github/workflows/quality.yml](.github/workflows/quality.yml) executa em pushes e pull requests para `main`:

- Spotless;
- testes e cobertura Maven;
- lint sem warnings;
- build frontend;
- `npm audit`;
- validação do Docker Compose;
- build das imagens Docker.

## Limitações Conhecidas

As limitações completas estão registradas em [docs/DELIVERY_DOCUMENT.md](docs/DELIVERY_DOCUMENT.md). Resumo:

- não há pagamento online;
- não há envio real de e-mail, SMS ou WhatsApp;
- histórico de status da OS é simplificado para o MVP;
- multiempresa/multitenancy está simplificado;
- Swagger fica público no ambiente local acadêmico;
- teste dinâmico dedicado de segurança permanece como melhoria futura;
- imagem frontend mantém 1 CVE média de BusyBox sem versão corrigida disponível na base analisada.

## Melhorias Futuras

O detalhamento está em [docs/DELIVERY_DOCUMENT.md](docs/DELIVERY_DOCUMENT.md) e
[docs/SECURITY_REPORT.md](docs/SECURITY_REPORT.md). Principais evoluções:

- ampliar cenários extremos e fluxos de regressão;
- criar auditoria de ações sensíveis;
- melhorar histórico detalhado de status da OS;
- integrar notificações reais para cliente;
- restringir Swagger por ambiente/perfil;
- evoluir multiempresa/multitenancy;
- reexecutar scans de segurança em cada ciclo de entrega.
