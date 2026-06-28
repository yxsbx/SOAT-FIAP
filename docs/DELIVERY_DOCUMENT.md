# Documento Final de Entrega - Tech Challenge

**Data da entrega:** 20/06/2026

## 1. Nome do grupo

**Entrega individual - Yasmin Barcelos Pires**

## 2. Participantes

| Nome completo         | RM       | Papel no projeto                      |
| --------------------- | -------- | ------------------------------------- |
| Yasmin Barcelos Pires | RM370897 | Desenvolvimento individual do projeto |

## 3. Usernames no Discord

| Participante          | Username no Discord |
| --------------------- | ------------------- |
| Yasmin Barcelos Pires | `yxsbx`             |

## 4. Nome do projeto

**AutoCare Hub**

## 5. Link do repositório privado

<https://github.com/yxsbx/SOAT-FIAP>

O acesso de leitura ao repositório privado foi concedido ao usuário `soatarchitecture`. A branch final de entrega é
`main`.

## 6. Link da documentação DDD ou Miro

Documentação DDD versionada no repositório:

<https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/DDD_DOCUMENTATION.md>

Event Storming versionado no repositório:

<https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/EVENT_STORMING.md>

Não foi informado um quadro externo no Miro. A documentação oficial da entrega está versionada nos arquivos acima.

## 7. Link ou rota do Swagger

Swagger local com a API em execução:

```text
http://localhost:8080/swagger-ui.html
```

Contrato OpenAPI versionado:

<https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/openapi/openapi.yaml>

Não há URL pública informada. Para avaliação local, deve-se usar a rota do Swagger apresentada acima.

## 8. Descrição resumida do projeto

O AutoCare Hub é um MVP acadêmico desenvolvido para o Tech Challenge FIAP. A solução entrega uma API REST para gestão de
uma oficina mecânica, centralizando clientes, veículos, serviços, peças, estoque, Ordens de Serviço, geração de
orçamento, aprovação de orçamento e acompanhamento do atendimento.

O foco principal da entrega é o backend monolítico em Java/Spring Boot, com documentação OpenAPI/Swagger, autenticação
JWT, persistência relacional com PostgreSQL, migrations com Flyway, Docker, testes automatizados e relatório de
vulnerabilidades. O repositório também inclui um frontend demonstrativo em Vue/Vite para apoiar a apresentação da
experiência web.

## 9. Objetivo do MVP

O objetivo do MVP é entregar um backend funcional, testável e documentado para apoiar o ciclo de atendimento de uma
oficina mecânica, desde o cadastro do cliente até a entrega do veículo.

Objetivos específicos:

- Administrar clientes, veículos, serviços, peças e insumos.
- Criar Ordens de Serviço completas.
- Gerar orçamentos automaticamente a partir de serviços e peças.
- Permitir a aprovação de orçamentos.
- Controlar o status da Ordem de Serviço.
- Permitir a consulta do andamento da OS pelo cliente.
- Proteger APIs administrativas com JWT.
- Validar dados sensíveis, como CPF/CNPJ e placa.
- Disponibilizar documentação Swagger/OpenAPI.
- Entregar execução local reproduzível com Docker.
- Demonstrar testes automatizados e análise de vulnerabilidades.

## 10. Resumo das funcionalidades entregues

Funcionalidades principais entregues:

- Autenticação com JWT.
- CRUD administrativo de clientes.
- CRUD administrativo de veículos.
- CRUD administrativo de serviços da oficina.
- CRUD administrativo de peças e insumos.
- Controle de estoque com entrada, saída, reserva, liberação e baixa.
- Criação de Ordem de Serviço com cliente identificado por CPF/CNPJ.
- Cadastro ou vinculação de veículo na criação da OS.
- Inclusão de serviços solicitados na OS.
- Inclusão de peças e insumos na OS.
- Geração automática de orçamento.
- Aprovação de orçamento.
- Início controlado da execução após aprovação.
- Controle de status da OS.
- Consulta de OS pelo cliente via API.
- Métrica de tempo médio de execução de Ordens de Serviço finalizadas.
- Cadastro e consulta de interessados em parceria.
- Gestão básica de usuários, permissões e preferências.
- Documentação Swagger/OpenAPI.
- Dockerfile e `docker-compose.yml`.
- Testes unitários e de integração.
- Documentação DDD e Event Storming.
- Relatório de vulnerabilidades.

## 11. Resumo da arquitetura

O backend foi implementado como um monolito em camadas, separando regras de negócio, casos de uso, infraestrutura e
interfaces REST.

Estrutura principal:

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

Responsabilidades:

- `domain`: entidades, value objects, enums, exceções, políticas e regras de negócio.
- `application`: use cases que coordenam os fluxos da aplicação.
- `infrastructure`: persistência JPA, configurações, segurança e adaptadores.
- `interfaces`: controllers REST, tratamento de exceções e mapeamento de DTOs.

Os controllers não acessam repositories diretamente. Eles delegam as chamadas para use cases, que aplicam as regras de
negócio e utilizam portas/adaptadores para persistência.

Tecnologias principais:

- Java 21.
- Spring Boot 4.1.0.
- Spring Web MVC.
- Spring Security.
- Spring Data JPA.
- PostgreSQL 16.
- Flyway.
- Maven.
- JWT com JJWT.
- Springdoc Swagger UI.
- OpenAPI Generator.
- JaCoCo.
- OWASP Dependency-Check.
- JUnit 5, Mockito, H2 e Testcontainers.

## 12. Justificativa do banco de dados

O banco escolhido para o ambiente principal foi o PostgreSQL.

A escolha é adequada ao domínio porque o MVP trabalha com dados relacionais e exige consistência entre entidades:

- Um cliente pode ter vários veículos.
- Uma Ordem de Serviço pertence a um cliente e a um veículo.
- Uma Ordem de Serviço possui serviços e peças.
- Peças possuem quantidade em estoque, quantidade reservada e disponibilidade.
- Movimentações de estoque precisam manter integridade.
- Usuários podem estar associados a perfis, empresas e permissões.

O PostgreSQL oferece transações, integridade referencial, índices, maturidade operacional e boa integração com Spring
Data JPA e Flyway. Nos testes automatizados, o projeto usa H2 e/ou Testcontainers conforme o tipo de teste, mantendo a
execução local reproduzível.

## 13. Aplicação de DDD

O projeto aplica DDD de forma pragmática dentro de um monolito em camadas.

Elementos aplicados:

- Entidades de domínio: `Customer`, `Vehicle`, `ServiceOrder`, `WorkshopService`, `Part`, `Budget`, `BudgetItem`,
  `StockMovement`, `User`.
- Value Objects: `Document`, `Plate`, `Money`, `Address`.
- Enums de domínio: `ServiceOrderStatus`, `StockMovementType`, `DocumentType`, `UserRole`.
- Exceções de domínio: `DomainException`, `InvalidServiceOrderStatusTransitionException`.
- Política de domínio: `PlatformFeePolicy`.
- Use cases de aplicação para criação de OS, geração de orçamento, aprovação de orçamento, controle de estoque, CRUDs
  administrativos e autenticação.

A Ordem de Serviço é o agregado central do atendimento. Ela conecta cliente, veículo, serviços, peças, orçamento, status
e histórico de andamento.

Documentação completa:

```text
docs/DDD_DOCUMENTATION.md
```

## 14. Event Storming

O Event Storming foi documentado para os fluxos principais do MVP:

- Criação e acompanhamento da Ordem de Serviço.
- Gestão de peças e insumos.

O documento registra atores, comandos, eventos de domínio, agregados, políticas, regras de negócio, exceções, fluxos
principais, fluxos alternativos e pontos de decisão.

No MVP, os eventos são usados como linguagem de modelagem e documentação. O sistema não implementa um event store
dedicado.

Documento versionado:

```text
docs/EVENT_STORMING.md
```

Os status internos do domínio usam nomes em português, como `AGUARDANDO_APROVACAO` e `EM_EXECUCAO`. Nas respostas e
requests da API, esses valores aparecem pelos códigos externos documentados no OpenAPI, como `WAITING_APPROVAL` e
`IN_PROGRESS`.

As mudanças automáticas de status ocorrem nas regras de domínio do fluxo principal: a OS nasce como recebida, a geração
do orçamento altera para aguardando aprovação e a aprovação registra o aceite do cliente. O início da execução, a
finalização e a entrega são transições controladas por endpoint administrativo, para evitar avançar a OS sem ação
explícita da oficina.

## 15. Linguagem Ubíqua

| Termo                   | Significado                                                                    |
| ----------------------- | ------------------------------------------------------------------------------ |
| Cliente                 | Pessoa física ou jurídica atendida pela oficina, identificada por CPF ou CNPJ. |
| Documento               | CPF ou CNPJ validado, normalizado e usado para evitar duplicidade.             |
| Veículo                 | Veículo pertencente a um cliente, identificado por placa, marca, modelo e ano. |
| Placa                   | Identificador do veículo, aceitando o formato brasileiro antigo e o Mercosul.  |
| Ordem de Serviço        | Registro central do atendimento da oficina.                                    |
| Status da OS            | Etapa atual da Ordem de Serviço.                                               |
| Serviço                 | Atividade executável pela oficina, com descrição, preço e tempo estimado.      |
| Peça/Insumo             | Item físico usado no atendimento e controlado em estoque.                      |
| Estoque                 | Quantidade total, reservada, disponível e mínima de peças/insumos.             |
| Movimentação de estoque | Registro de entrada, saída, venda, reserva confirmada ou ajuste.               |
| Orçamento               | Composição financeira da OS, calculada por serviços e peças.                   |
| Aprovação               | Aceite do cliente para execução do orçamento.                                  |
| Baixa de estoque        | Redução definitiva do estoque após aprovação ou saída registrada.              |

## 16. Segurança implementada

Controles de segurança implementados:

- Autenticação JWT por `POST /api/v1/auth/login`.
- Token JWT assinado com segredo vindo de variável de ambiente.
- Expiração configurável por `JWT_EXPIRATION_MINUTES`.
- APIs administrativas protegidas por Bearer Token.
- Autorização centralizada por Spring Security.
- Senhas com BCrypt.
- DTOs explícitos para requests/responses.
- Rejeição de campos desconhecidos via Jackson.
- Validação real de CPF e CNPJ.
- Validação de placa brasileira antiga e Mercosul.
- Validação de e-mail, tamanhos máximos, preços e quantidades não negativos.
- Tratamento global de exceções REST.
- Erros padronizados, sem retorno intencional de stacktrace ao usuário.
- CORS configurável e sem wildcard.
- OpenAPI desabilitável por variável de ambiente; Swagger UI local servido como página estática para a avaliação.
- Uso de `.env.example` sem secrets reais.

## 17. Testes e cobertura

O projeto possui testes unitários e de integração para regras de domínio, use cases, segurança, autorização e fluxos
REST principais. A validação completa é executada com:

```powershell
mvn verify
```

Resultado de qualidade revalidado em 28/06/2026:

| Métrica    | Coberto | Não coberto | Cobertura |
| ---------- | ------: | ----------: | --------: |
| Instruções |   9.870 |         372 |    96,37% |
| Branches   |     455 |          49 |    90,28% |
| Linhas     |   2.362 |          66 |    97,28% |
| Métodos    |     644 |          32 |    95,27% |

Resultado documentado: 147 testes automatizados e `mvn verify` concluindo com sucesso. O gate exige no mínimo 90% de
instruções, linhas e branches, acima da cobertura mínima de 80% exigida para a entrega.

No JaCoCo, branches representam caminhos condicionais do código, como `if`, `else`, validações, exceções e transições de
status. A entrega passou a exigir 90% também nessa métrica, evitando que apenas linhas executadas escondam caminhos
alternativos não testados.

Relatórios gerados localmente:

```text
target/site/jacoco/index.html
target/site/jacoco/jacoco.csv
```

## 18. Docker e execução local

Pré-requisitos:

- Java 21.
- Maven 3.9+.
- Docker.
- Docker Compose.

Criar `.env` a partir do exemplo:

```powershell
# Linux/macOS/Git Bash
cp .env.example .env

# PowerShell
Copy-Item .env.example .env
```

Variáveis obrigatórias mínimas:

```text
POSTGRES_PASSWORD=[PREENCHER - senha local do PostgreSQL]
JWT_SECRET=[PREENCHER - segredo local com pelo menos 32 bytes]
```

Para demonstrar o projeto rodando do zero:

```powershell
docker compose down
docker compose down --remove-orphans
docker compose down -v
docker compose up -d --build
docker compose ps
docker compose logs -f app
```

Esse fluxo para containers antigos, remove o volume local do PostgreSQL, recria banco/API/frontend e permite mostrar as
migrations Flyway criando a base nos logs. O serviço da API no `docker-compose.yml` se chama `app`; por isso o comando
real de logs é `docker compose logs -f app`.

Parar ambiente:

```powershell
docker compose down
```

Remover volume local do banco:

```powershell
docker compose down -v
```

Opcional, com cuidado: `docker volume prune` remove volumes Docker não usados por outros projetos também. Use apenas
quando for intencional limpar volumes órfãos do Docker.

Executar somente o PostgreSQL pelo Compose e a API pelo Maven:

```powershell
docker compose up -d postgres
mvn spring-boot:run
```

Serviços locais:

```text
Frontend: http://localhost:5173
API: http://localhost:8080
Swagger: http://localhost:8080/swagger-ui.html
PostgreSQL: localhost:5432
```

O frontend usa proxy reverso para a API e pode ser acessado por `localhost` ou pelo IP local da máquina sem depender de
uma origem CORS adicional. Para desenvolvimento com hot reload:

```powershell
cd frontend
npm ci
npm run dev
```

## 19. Relatório de vulnerabilidades

O relatório oficial de vulnerabilidades está versionado em:

<https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/SECURITY_REPORT.md>

O roteiro de execução dos scans fica em:

```text
docs/SECURITY_SCAN_GUIDE.md
```

Resumo dos scans finais documentados:

- OWASP Dependency-Check backend: scan final com 0 vulnerabilidades reportadas.
- `npm audit` frontend: scan final com 0 vulnerabilidades reportadas.
- Docker Scout backend: 0 vulnerabilidades na imagem final distroless.
- Docker Scout frontend: 0 críticas, 0 altas e 1 média sem correção disponível na base.
- Gitleaks: 0 leaks em 36 commits.
- Semgrep: 0 achados e 0 erros em 200 arquivos com 187 regras.

## 20. Vulnerabilidades encontradas

Os scans iniciais encontraram vulnerabilidades em dependências backend, dependências frontend e imagens Docker. Todas as
vulnerabilidades críticas e altas foram corrigidas antes da entrega.

Resumo por origem:

- **Origem:**  Backend Maven
  - **Exemplos de pacotes/áreas afetadas:**  Spring Boot, Spring Framework, Spring Security, Tomcat, PostgreSQL JDBC,
    Log4j API, Commons Compress, Commons Lang e Swagger UI
  - **Status final:**  Corrigido

- **Origem:**  Frontend npm
  - **Exemplos de pacotes/áreas afetadas:**  `vite`, `esbuild`, `@vitejs/plugin-vue` e `js-yaml` transitivo
  - **Status final:**  Corrigido

- **Origem:**  Imagem backend
  - **Exemplos de pacotes/áreas afetadas:**  Runtime anterior com pacote `/usr/bin/pebble` e CVEs de base
  - **Status final:**  Corrigido

- **Origem:**  Imagem frontend
  - **Exemplos de pacotes/áreas afetadas:**  Base Nginx/Alpine anterior com CVEs críticas, altas e médias
  - **Status final:**  Corrigido

- **Origem:**  Imagem frontend atual
  - **Exemplos de pacotes/áreas afetadas:**  BusyBox com 1 CVE média sem versão corrigida disponível
  - **Status final:**  Aceito temporariamente

A tabela completa com IDs `VULN-001` a `VULN-013`, evidências e impacto está no relatório oficial de vulnerabilidades:
`docs/SECURITY_REPORT.md`.

## 21. Correções aplicadas

Correções aplicadas:

- Atualização de dependências Maven vulneráveis.
- Atualização de dependências frontend vulneráveis.
- Regeneração do `package-lock.json`.
- Atualização direta do Swagger UI.
- Migração da imagem backend para runtime distroless Java 21 non-root.
- Migração da imagem frontend para Nginx unprivileged `mainline-alpine-slim`, fixada por digest.
- Reexecução dos scans finais.

A CVE média do BusyBox foi aceita temporariamente porque o scanner não informa versão corrigida. O container permanece
non-root, read-only e sem novos privilégios.

## 22. Limitações conhecidas

Limitações do MVP:

- Não há pagamento online.
- Não há envio real de e-mail, SMS ou WhatsApp.
- O histórico de status da OS é simplificado.
- O controle de múltiplas oficinas/lojas existe de forma simplificada no mesmo monolito.
- O Swagger fica público no ambiente local acadêmico.
- Não há event store para eventos de domínio.
- Um teste dinâmico dedicado de segurança permanece como melhoria futura.
- Cenários extremos e fluxos de regressão podem ser ampliados.
- A imagem frontend mantém 1 CVE média de base sem correção disponível.

## 23. Melhorias futuras

Melhorias planejadas ou recomendadas:

- Ampliar cenários extremos, regressões funcionais e testes de contrato.
- Criar auditoria de ações sensíveis.
- Melhorar o histórico detalhado de status da OS.
- Integrar notificações reais por e-mail, SMS ou WhatsApp.
- Restringir o Swagger por ambiente/perfil em produção.
- Evoluir o suporte multiempresa/multitenancy.
- Adicionar pipeline CI com build, testes, cobertura e scans de segurança.
- Reexecutar Docker Scout, Gitleaks e Semgrep em cada ciclo de entrega.
- Evoluir métricas operacionais da oficina.
- Implementar integrações externas para pagamento, agenda e comunicação.
- Avaliar a implementação futura de eventos de domínio persistidos.

## 24. Conclusão

O AutoCare Hub entrega um MVP backend coerente com o desafio proposto, cobrindo o ciclo principal de atendimento de uma
oficina mecânica: cadastro de clientes e veículos, criação de Ordem de Serviço, composição com serviços e peças, geração
e aprovação de orçamento, controle de status, estoque e consulta pelo cliente.

A solução utiliza arquitetura em camadas, aplica DDD de forma pragmática, documenta Event Storming e linguagem ubíqua,
disponibiliza contrato OpenAPI/Swagger, implementa autenticação JWT, valida dados sensíveis e oferece execução local com
Docker.

Os testes automatizados e a análise de vulnerabilidades documentada reforçam a qualidade da entrega. As vulnerabilidades
identificadas nos scans iniciais de dependências foram corrigidas, e os scans finais de backend e frontend não
reportaram vulnerabilidades abertas. As limitações restantes estão registradas como escopo conhecido do MVP e como
oportunidades de evolução futura.
