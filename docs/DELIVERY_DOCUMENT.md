# Documento Final de Entrega - Tech Challenge

**Data do documento:** 28/06/2026
**Projeto:** AutoCare Hub

## 1. Nome do grupo

**Entrega individual - Yasmin Barcelos Pires**

## 2. Participante

| Nome completo         | RM       | Papel no projeto                      |
| --------------------- | -------- | ------------------------------------- |
| Yasmin Barcelos Pires | RM370897 | Desenvolvimento individual do projeto |

## 3. Username no Discord

| Participante | Username no Discord |
| --- | --- |
| Yasmin Barcelos Pires | `yxsbx` |

## 4. Link do repositório privado

<https://github.com/yxsbx/SOAT-FIAP>

O acesso de leitura ao repositório privado foi concedido ao usuário `soat-architecture`. A branch final de entrega é `main`.

## 5. Link da documentação

A documentação oficial da entrega está versionada no próprio repositório, nos arquivos abaixo:

| Documento | Local |
| --- | --- |
| Levantamento de requisitos | <https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/REQUIREMENTS.md> |
| Arquitetura, HLD, LLD e C4 | <https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/ARCHITECTURE.md> |
| Documentação DDD | <https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/DDD_DOCUMENTATION.md> |
| Domain Storytelling | <https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/DOMAIN_STORYTELLING.md> |
| Event Storming | <https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/EVENT_STORMING.md> |
| Contrato OpenAPI | <https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/openapi/openapi.yaml> |
| Estratégia de testes | <https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/TESTING.md> |
| Análise estática e qualidade | <https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/STATIC_ANALYSIS.md> |
| Relatório de vulnerabilidades | <https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/SECURITY_REPORT.md> |
| Guia dos scans de segurança | <https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/SECURITY_SCAN_GUIDE.md> |

## 6. Swagger/OpenAPI

Com a aplicação em execução local, o Swagger pode ser acessado em:

```text
http://localhost:8080/swagger-ui.html
```

O contrato OpenAPI também está versionado no repositório em `docs/openapi/openapi.yaml`.

## 7. Descrição resumida do projeto

O AutoCare Hub é um MVP desenvolvido para o Tech Challenge FIAP. A proposta é entregar uma API REST para apoiar a rotina de uma oficina mecânica, centralizando o cadastro de clientes, veículos, serviços, peças, estoque e Ordens de Serviço.

O sistema cobre o fluxo principal do atendimento: identificação do cliente, cadastro ou vínculo do veículo, criação da Ordem de Serviço, inclusão de serviços e peças, geração automática do orçamento, aprovação do orçamento, controle de status e consulta do andamento pelo cliente.

A entrega foi construída como um backend monolítico em Java e Spring Boot, com persistência relacional em PostgreSQL, migrations com Flyway, autenticação JWT, documentação Swagger/OpenAPI, Docker, testes automatizados e relatório de vulnerabilidades. O repositório também possui um frontend demonstrativo em Vue/Vite para apoiar a apresentação e tornar o funcionamento do MVP mais visual.

## 8. Objetivo do MVP

O objetivo do MVP é entregar um backend funcional, testável e documentado para organizar o ciclo de atendimento de uma oficina mecânica, desde o cadastro do cliente até a entrega do veículo.

Objetivos específicos:

- administrar clientes, veículos, serviços, peças e insumos;
- criar Ordens de Serviço completas;
- gerar orçamentos automáticamente a partir de serviços e peças;
- permitir a aprovação de orçamentos;
- controlar o status da Ordem de Serviço;
- permitir a consulta do andamento da OS pelo cliente;
- proteger APIs administrativas com JWT;
- validar dados sensíveis, como CPF/CNPJ e placa;
- disponibilizar documentação Swagger/OpenAPI;
- entregar execução local reproduzível com Docker;
- demonstrar testes automatizados e análise de vulnerabilidades.

## 9. Funcionalidades entregues

Funcionalidades principais do backend:

- autenticação com JWT;
- CRUD administrativo de clientes;
- CRUD administrativo de veículos;
- CRUD administrativo de serviços da oficina;
- CRUD administrativo de peças e insumos;
- controle de estoque com entrada, saída, reserva, liberação e baixa;
- criação de Ordem de Serviço com cliente identificado por CPF/CNPJ;
- cadastro ou vínculo de veículo na criação da OS;
- inclusão de serviços solicitados na OS;
- inclusão de peças e insumos na OS;
- geração automática de orçamento;
- aprovação de orçamento;
- início controlado da execução após aprovação;
- controle de status da OS;
- consulta de OS pelo cliente via API;
- métrica de tempo médio de execução de Ordens de Serviço finalizadas;
- documentação Swagger/OpenAPI;
- Dockerfile e `docker-compose.yml`;
- testes unitários e de integração;
- documentação DDD e Event Storming;
- relatório de vulnerabilidades.

O frontend demonstrativo foi incluído para facilitar a apresentação do produto e mostrar como a API poderia ser usada por uma oficina, seus funcionários e seus clientes.

## 10. Resumo da arquitetura

O backend foi implementado como um monolito em camadas. A separação foi organizada para manter as regras de negócio no domínio, os fluxos da aplicação nos casos de uso, os detalhes técnicos na infraestrutura e a exposição da API nos controllers REST.

Estrutura principal:

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

Responsabilidades principais:

- `domain`: entidades, value objects, enums, exceções e regras de negócio;
- `application`: use cases que coordenam os fluxos do sistema;
- `infrastructure`: persistência JPA, configurações, segurança e adaptadores;
- `interfaces`: controllers REST, tratamento de exceções e mapeamento de DTOs.

Os controllers não acessam repositories diretamente. Eles recebem as requisições, delegam a execução para os use cases e retornam as respostas da API. As regras do negócio ficam concentradas no domínio e nos fluxos de aplicação.

Tecnologias principais utilizadas:

- Java 21;
- Spring Boot;
- Spring Web MVC;
- Spring Security;
- Spring Data JPA;
- PostgreSQL 16;
- Flyway;
- Maven;
- JWT com JJWT;
- Springdoc Swagger UI;
- OpenAPI Generator;
- JaCoCo;
- OWASP Dependency-Check;
- JUnit 5, Mockito, MockMvc e H2.

## 11. Justificativa do banco de dados

O banco escolhido para o ambiente principal foi o PostgreSQL.

A escolha é adequada ao domínio porque o MVP trabalha com dados relacionais e exige consistência entre entidades:

- um cliente pode ter vários veículos;
- uma Ordem de Serviço pertence a um cliente e a um veículo;
- uma Ordem de Serviço possui serviços e peças;
- peças possuem quantidade em estoque, quantidade reservada e quantidade disponível;
- movimentações de estoque precisam manter histórico e integridade;
- usuários administrativos precisam ser autenticados e autorizados.

O PostgreSQL oferece transações, integridade referencial, índices, maturidade operacional e boa integração com Spring Data JPA e Flyway. Para testes automatizados, o projeto utiliza H2 em memória com Spring Boot Test, MockMvc e migrations Flyway.

## 12. Aplicação de DDD

O projeto aplica DDD de forma pragmática dentro de um monolito em camadas.

Elementos aplicados:

- entidades de domínio: `Customer`, `Vehicle`, `ServiceOrder`, `WorkshopService`, `Part`, `Budget`, `BudgetItem`, `StockMovement` e `User`;
- value objects: `Document`, `Plate`, `Money` e `Address`;
- enums de domínio: `ServiceOrderStatus`, `StockMovementType`, `DocumentType` e `UserRole`;
- exceções de domínio para regras inválidas e transições de status;
- use cases de aplicação para criação de OS, geração de orçamento, aprovação de orçamento, controle de estoque, CRUDs administrativos e autenticação.

A Ordem de Serviço é o agregado central do atendimento. Ela conecta cliente, veículo, serviços, peças, orçamento, status e datas importantes do fluxo.

A documentação completa está em:

```text
docs/DDD_DOCUMENTATION.md
```

## 13. Event Storming

O Event Storming foi documentado para os fluxos principais exigidos no MVP:

- criação da Ordem de Serviço;
- acompanhamento da Ordem de Serviço;
- gestão de peças e insumos.

O documento registra atores, comandos, eventos de domínio, agregados, políticas, regras de negócio, exceções, fluxos principais, fluxos alternativos e pontos de decisão.

No MVP, os eventos são usados como linguagem de modelagem e documentação do domínio. O sistema não implementa Event Sourcing nem event store dedicado, porque o requisito do desafio pede a documentação DDD e não a persistência de eventos.

Documento versionado:

```text
docs/EVENT_STORMING.md
```

Os status internos do domínio usam nomes como `RECEBIDA`, `EM_DIAGNOSTICO`, `AGUARDANDO_APROVACAO`, `EM_EXECUCAO`, `FINALIZADA` e `ENTREGUE`. Na API, esses valores são expostos pelos códigos documentados no OpenAPI, como `RECEIVED`, `IN_DIAGNOSIS`, `WAITING_APPROVAL`, `IN_PROGRESS`, `FINISHED` e `DELIVERED`.

As mudanças de status seguem as ações do fluxo principal. A OS nasce como recebida, a geração do orçamento altera o status para aguardando aprovação e a aprovação registra o aceite do cliente. O início da execução, a finalização e a entrega são transições controladas por endpoint administrativo, evitando que a OS avance sem uma ação explícita da oficina.

## 14. Linguagem Ubíqua

| Termo | Significado |
| --- | --- |
| Cliente | Pessoa física ou jurídica atendida pela oficina, identificada por CPF ou CNPJ. |
| Documento | CPF ou CNPJ validado, normalizado e usado para evitar duplicidade. |
| Veículo | Veículo pertencente a um cliente, identificado por placa, marca, modelo e ano. |
| Placa | Identificador do veículo, aceitando o formato brasileiro antigo e o padrão Mercosul. |
| Ordem de Serviço | Registro central do atendimento da oficina. |
| Status da OS | Etapa atual da Ordem de Serviço. |
| Serviço | Atividade executável pela oficina, com descrição, preço e tempo estimado. |
| Peça/Insumo | Item físico usado no atendimento e controlado em estoque. |
| Estoque | Quantidade total, reservada, disponível e mínima de peças/insumos. |
| Movimentação de estoque | Registro de entrada, saída, reserva, baixa ou ajuste de estoque. |
| Orçamento | Composição financeira da OS, calculada com base em serviços e peças. |
| Aprovação | Aceite do cliente para execução do orçamento. |
| Baixa de estoque | Redução definitiva do estoque após aprovação do orçamento ou saída registrada. |

## 15. Segurança implementada

Controles de segurança implementados:

- autenticação JWT por `POST /api/v1/auth/login`;
- token JWT assinado com segredo vindo de variável de ambiente;
- expiração configurável por `JWT_EXPIRATION_MINUTES`;
- APIs administrativas protegidas por Bearer Token;
- autorização centralizada por Spring Security;
- senhas com BCrypt;
- DTOs explícitos para requests e responses;
- rejeição de campos desconhecidos via Jackson;
- validação real de CPF e CNPJ;
- validação de placa brasileira antiga e Mercosul;
- validação de e-mail, tamanhos máximos, preços e quantidades não negativos;
- tratamento global de exceções REST;
- erros padronizados, sem retorno intencional de stacktrace ao usuário;
- CORS configurável e sem wildcard;
- uso de `.env.example` sem secrets reais.

## 16. Testes e cobertura

O projeto possui testes unitários e de integração para regras de domínio, use cases, segurança, autorização e fluxos REST principais.

A validação completa é executada com:

```powershell
mvn verify
```

Resultado de qualidade documentado em 28/06/2026:

| Métrica | Coberto | Não coberto | Cobertura |
| --- | ---: | ---: | ---: |
| Instruções | 9.701 | 372 | 96,31% |
| Branches | 447 | 49 | 90,12% |
| Linhas | 2.333 | 66 | 97,25% |
| Métodos | 641 | 32 | 95,25% |

Resultado documentado: 143 testes automatizados e `mvn verify` concluindo com sucesso. O gate do projeto exige pelo menos 90% de instruções, linhas e branches, ficando acima da cobertura mínima de 80% exigida para a entrega.

Relatórios gerados localmente:

```text
target/site/jacoco/index.html
target/site/jacoco/jacoco.csv
```

## 17. Docker e execução local

Pré-requisitos:

- Java 21;
- Maven 3.9+;
- Docker;
- Docker Compose.

Criar o arquivo `.env` a partir do exemplo em Linux, macOS ou Git Bash:

```bash
cp .env.example .env
```

Criar o arquivo `.env` a partir do exemplo no PowerShell:

```powershell
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

Esse fluxo para containers antigos, remove o volume local do PostgreSQL, recria banco, API e frontend, e permite mostrar as migrations Flyway criando a base nos logs. O serviço da API no `docker-compose.yml` se chama `app`, por isso o comando de logs é `docker compose logs -f app`.

Parar o ambiente:

```powershell
docker compose down
```

Remover o volume local do banco:

```powershell
docker compose down -v
```

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

Para desenvolvimento do frontend com hot reload:

```powershell
cd frontend
npm ci
npm run dev
```

## 18. Relatório de vulnerabilidades

O relatório oficial de vulnerabilidades está versionado em:

<https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/SECURITY_REPORT.md>

O roteiro de execução dos scans fica em:

```text
docs/SECURITY_SCAN_GUIDE.md
```

Resumo dos scans finais documentados:

| Ferramenta | Escopo | Resultado final |
| --- | --- | --- |
| OWASP Dependency-Check | Backend Maven | 0 vulnerabilidades reportadas no scan final |
| npm audit | Frontend | 0 vulnerabilidades reportadas no scan final |
| Docker Scout | Imagem backend | 0 críticas, 0 altas e 1 média residual |
| Docker Scout | Imagem frontend | 0 críticas, 0 altas e 1 média sem correção disponível na base |
| OWASP ZAP | API local via OpenAPI | 0 falhas e 2 avisos revisados |
| Gitleaks | Repositório Git | 0 leaks em 36 commits |
| Semgrep | Código-fonte | 0 achados e 0 erros em 200 arquivos com 187 regras |

## 19. Vulnerabilidades encontradas e tratadas

Os scans iniciais encontraram vulnerabilidades em dependências backend, dependências frontend e imagens Docker. As vulnerabilidades críticas e altas foram corrigidas antes da entrega.

Resumo por origem:

| Origem | Exemplos de áreas afetadas | Status final |
| --- | --- | --- |
| Backend Maven | Spring Boot, Spring Framework, Spring Security, Tomcat, PostgreSQL JDBC, Log4j API, Commons Compress, Commons Lang e Swagger UI | Corrigido |
| Frontend npm | `vite`, `esbuild`, `@vitejs/plugin-vue` e `js-yaml` transitivo | Corrigido |
| Imagem backend | Runtime anterior com pacote `/usr/bin/pebble` e CVEs de base | Corrigido |
| Imagem backend atual | `jackson-databind` transitivo com 1 CVE média e correção ainda indisponível no Maven Central | Aceito temporariamente |
| Imagem frontend anterior | Base Nginx/Alpine anterior com CVEs críticas, altas e médias | Corrigido |
| Imagem frontend atual | BusyBox com 1 CVE média sem versão corrigida disponível | Aceito temporariamente |

A tabela completa com IDs `VULN-001` a `VULN-013`, evidências, impacto e tratamento está no relatório oficial de vulnerabilidades em `docs/SECURITY_REPORT.md`.

## 20. Correções de segurança aplicadas

Correções aplicadas durante a estabilização da entrega:

- atualização de dependências Maven vulneráveis;
- atualização de dependências frontend vulneráveis;
- regeneração do `package-lock.json`;
- atualização do Swagger UI;
- migração da imagem backend para runtime distroless Java 21 non-root;
- migração da imagem frontend para Nginx unprivileged `mainline-alpine-slim`, fixada por digest;
- reexecução dos scans finais.

As CVEs médias restantes foram registradas no relatório como riscos residuais aceitos: `jackson-databind` transitivo na
imagem backend, porque a versão corrigida indicada pelo Docker Scout ainda não está publicada no Maven Central, e BusyBox
na imagem frontend, porque o scanner não indicou versão corrigida disponível na base utilizada. Os containers permanecem
non-root, read-only e sem novos privilégios.

## 21. Escopo não incluído no MVP

Algumas funcionalidades foram deixadas fora do MVP por não fazerem parte do requisito obrigatório da fase:

- pagamento online;
- envio real de e-mail, SMS ou WhatsApp;
- agenda de atendimento;
- integração com fornecedores;
- event store ou Event Sourcing;
- aplicação mobile nativa;
- implantação pública em ambiente produtivo.

Esses pontos não impedem a execução do fluxo principal pedido no Tech Challenge. Eles representam possíveis evoluções para uma versão futura do produto.

## 22. Conclusão

O AutoCare Hub entrega um MVP backend coerente com o desafio proposto, cobrindo o ciclo principal de atendimento de uma oficina mecânica: cadastro de clientes e veículos, criação de Ordem de Serviço, composição com serviços e peças, geração e aprovação de orçamento, controle de status, estoque e consulta pelo cliente.

A solução utiliza arquitetura em camadas, aplica DDD de forma pragmática, documenta Event Storming e linguagem ubíqua, disponibiliza contrato OpenAPI/Swagger, implementa autenticação JWT, valida dados sensíveis e oferece execução local com Docker.

Os testes automatizados e a análise de vulnerabilidades documentada reforçam a qualidade da entrega. As vulnerabilidades identificadas nos scans iniciais foram tratadas, e os resultados finais registrados no relatório de segurança demonstram que o MVP foi entregue com foco em funcionalidade, qualidade e segurança.
