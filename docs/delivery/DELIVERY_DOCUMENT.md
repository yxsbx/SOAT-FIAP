# Documento de Entrega - Tech Challenge FIAP - Fase 2

**Data do documento:** 04/07/2026
**Projeto:** AutoCare Hub

## 1. Nome do grupo

**Entrega individual - Yasmin Barcelos Pires**

## 2. Participante e username no Discord

| Nome completo         | RM       | Username no Discord | Papel no projeto                      |
|-----------------------|----------|---------------------|---------------------------------------|
| Yasmin Barcelos Pires | RM370897 | `yxsbx`             | Desenvolvimento individual do projeto |

## 3. Link do repositório privado

Repositório privado: <https://github.com/yxsbx/SOAT-FIAP>

A branch final de entrega é `main`. O acesso de leitura ao repositório privado deve ser confirmado no GitHub para o
usuário avaliador `soat-architecture` antes do envio no portal.

## 4. Link da documentação

A documentação oficial da entrega está versionada no próprio repositório, nos arquivos abaixo:

| Documento                     | Local                                                                       |
|-------------------------------|-----------------------------------------------------------------------------|
| Levantamento de requisitos    | <https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/domain/REQUIREMENTS.md>         |
| Arquitetura, HLD, LLD e C4    | <https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/architecture/ARCHITECTURE.md>         |
| Documentação DDD              | <https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/domain/DDD_DOCUMENTATION.md>    |
| Domain Storytelling           | <https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/domain/DOMAIN_STORYTELLING.md>  |
| Event Storming                | <https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/domain/EVENT_STORMING.md>       |
| Contrato OpenAPI              | <https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/api/openapi/openapi.yaml>    |
| Estratégia de testes          | <https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/testing/TESTING.md>              |
| Análise estática e qualidade  | <https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/testing/STATIC_ANALYSIS.md>      |
| Relatório de vulnerabilidades | <https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/security/SECURITY_REPORT.md>      |
| Arquitetura da Fase 2         | <https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/architecture/PHASE2_ARCHITECTURE.md>  |
| Roteiro do vídeo da Fase 2    | <https://github.com/yxsbx/SOAT-FIAP/blob/main/docs/delivery/PHASE2_VIDEO_SCRIPT.md>  |
| Kubernetes                    | <https://github.com/yxsbx/SOAT-FIAP/tree/main/deploy/kubernetes>            |
| Terraform                     | <https://github.com/yxsbx/SOAT-FIAP/tree/main/infra/terraform>              |
| Pipeline CI/CD de deploy      | <https://github.com/yxsbx/SOAT-FIAP/blob/main/.github/workflows/deploy.yml> |

## 4.1 Links finais da Fase 2

| Item                   | Link                                                      |
|------------------------|-----------------------------------------------------------|
| Desenho da arquitetura | `docs/architecture/PHASE2_ARCHITECTURE.md`                             |
| Vídeo demonstrativo    | [INSERIR LINK DO VÍDEO ANTES DA ENTREGA]                  |
| PDF final no portal    | `docs/delivery/DELIVERY_DOCUMENT.pdf`                              |
| Collection Postman     | `docs/api/postman/autocarehub-phase2.postman_collection.json` |

## 4.2 Recursos escolhidos para a Fase 2

- Backend monolítico Spring Boot organizado em domínio, aplicação, portas, infraestrutura e interfaces.
- PostgreSQL como banco relacional.
- Docker e Docker Compose para execução local.
- Kubernetes com Deployments, Services, ConfigMaps, Secrets e HPA.
- Terraform com criação opcional de cluster local `kind` e provider Kubernetes para provisionamento acadêmico/local de namespace, ConfigMap, Secret e PVC do PostgreSQL demonstrativo.
- GitHub Actions para qualidade, build de imagens e deploy Kubernetes quando `KUBE_CONFIG` estiver configurado.
- Swagger/OpenAPI como contrato principal da API.

## 5. Relatório com análise de vulnerabilidades encontradas no sistema

Esta seção resume a análise de vulnerabilidades da entrega. Os detalhes completos, comandos executados e evidências
estão registrados em `docs/security/SECURITY_REPORT.md`.

### 5.1 Escopo analisado

- Dependências backend Java/Maven analisadas pelo OWASP Dependency-Check.
- Dependências frontend Vue/Vite analisadas por `npm audit`.
- Imagens Docker de backend e frontend analisadas por Docker Scout.
- API local analisada com OWASP ZAP a partir do contrato OpenAPI em `/v3/api-docs`.
- Histórico Git analisado por Gitleaks para verificar exposição de secrets.
- Código backend, frontend e Dockerfiles analisados com Semgrep.
- Evidências de cobertura registradas pelo JaCoCo como apoio à qualidade da entrega.

### 5.2 Resultado geral dos scans finais

| Categoria             | Crítica | Alta | Média | Status final                      |
|-----------------------|--------:|-----:|------:|-----------------------------------|
| Dependências backend  |       0 |    0 |     0 | Corrigido                         |
| Dependências frontend |       0 |    0 |     0 | Corrigido                         |
| Imagem backend        |       0 |    0 |     1 | Risco médio aceito                |
| Imagem frontend       |       0 |    0 |     1 | Risco médio aceito                |
| OWASP ZAP API scan    |       0 |    0 |     0 | 0 falhas e 1 aviso baixo revisado |
| Análise estática      |       0 |    0 |     0 | Semgrep sem achados               |
| Secrets               |       0 |    0 |     0 | Gitleaks sem leaks                |

Os scans finais não deixaram vulnerabilidades críticas ou altas abertas. Permanecem duas CVEs médias aceitas como risco
residual: uma em `jackson-databind` transitivo na imagem backend, porque a versão corrigida indicada pelo Docker Scout
ainda não estava publicada no Maven Central na validação, e uma em BusyBox na imagem frontend, porque o scanner não
indicou versão corrigida disponível.

### 5.3 Vulnerabilidades encontradas e tratamento

| ID       | Ferramenta             | Severidade               | Pacote/área afetada                                                | Status                 |
|----------|------------------------|--------------------------|--------------------------------------------------------------------|------------------------|
| VULN-001 | OWASP Dependency-Check | Alta/Média               | `log4j-api-2.24.3.jar`                                             | Corrigido              |
| VULN-002 | OWASP Dependency-Check | Alta                     | `postgresql-42.7.10.jar`                                           | Corrigido              |
| VULN-003 | OWASP Dependency-Check | Crítica/Alta/Média       | `spring-boot-3.5.13.jar` e starters                                | Corrigido              |
| VULN-004 | OWASP Dependency-Check | Alta/Média/Baixa         | `spring-core-6.2.17.jar` e `spring-web-6.2.17.jar`                 | Corrigido              |
| VULN-005 | OWASP Dependency-Check | Alta/Média/Baixa         | `spring-security-core-6.5.9.jar` e `spring-security-web-6.5.9.jar` | Corrigido              |
| VULN-006 | OWASP Dependency-Check | Crítica/Alta/Média/Baixa | `tomcat-embed-core-10.1.53.jar`                                    | Corrigido              |
| VULN-007 | OWASP Dependency-Check | Média                    | `commons-compress-1.24.0.jar`                                      | Corrigido              |
| VULN-008 | OWASP Dependency-Check | Média                    | `commons-lang3-3.17.0.jar`                                         | Corrigido              |
| VULN-009 | OWASP Dependency-Check | Média/Desconhecida       | `swagger-ui-5.32.2.jar`                                            | Corrigido              |
| VULN-010 | npm audit              | Alta                     | `vite`, `esbuild` e `@vitejs/plugin-vue`                           | Corrigido              |
| VULN-011 | npm audit              | Média                    | `js-yaml-4.1.1` transitivo do ESLint                               | Corrigido              |
| VULN-012 | Docker Scout           | Crítica/Alta/Média       | `/usr/bin/pebble` da imagem runtime anterior                       | Corrigido              |
| VULN-013 | Docker Scout           | Crítica/Alta/Média       | Imagem frontend Nginx 1.27/Alpine antiga                           | Corrigido              |
| RISK-001 | Docker Scout           | Média                    | BusyBox da imagem frontend atual                                   | Aceito temporariamente |
| RISK-002 | Docker Scout           | Média                    | `jackson-databind 2.21.4` transitivo da imagem backend             | Aceito temporariamente |

### 5.4 Correções aplicadas

- Atualização de dependências Maven vulneráveis e reexecução do Dependency-Check final sem achados.
- Atualização das dependências frontend vulneráveis, incluindo Vite, plugin Vue, esbuild transitivo e `js-yaml`.
- Regeneração do `package-lock.json` após as correções do frontend.
- Atualização direta do Swagger UI.
- Migração da imagem backend para runtime distroless Java 21 non-root.
- Migração da imagem frontend para Nginx unprivileged `mainline-alpine-slim`, fixada por digest.
- Execução de OWASP ZAP API scan contra `/v3/api-docs`, com 0 falhas e 1 aviso baixo revisado.
- Validação de secrets com Gitleaks, sem leaks encontrados no histórico analisado.
- Análise estática com Semgrep, sem achados no escopo executado.

### 5.5 Risco residual aceito

As vulnerabilidades mantidas após os scans finais são duas CVEs médias aceitas temporariamente. A primeira está em
`jackson-databind 2.21.4`, dependência transitiva usada na imagem backend; o Docker Scout indica correção em `2.21.5`,
mas essa versão ainda não estava disponível no Maven Central na validação. A segunda está no BusyBox da imagem frontend;
o Docker Scout informou que não havia versão corrigida disponível na base analisada.

Como mitigação, os containers permanecem configurados com usuário não privilegiado, filesystem read-only quando
aplicável e sem novos privilégios.

## 6. Evolução da Fase 2

A Fase 2 evolui o MVP da Fase 1 com:

- endpoints explícitos de decisão externa de orçamento em `POST /api/v1/service-orders/{serviceOrderId}/budget/external-approval` e `POST /api/v1/service-orders/{serviceOrderId}/budget/external-rejection`;
- endpoint legado de decisão externa de orçamento em `POST /api/v1/service-orders/{serviceOrderId}/budget/decision`;
- endpoint de atualização externa de status em `POST /api/v1/service-orders/{serviceOrderId}/status/external`;
- listagem operacional de OS movida para query no repository/adapter, ordenada por status e data e ocultando OS finalizadas e entregues;
- manifests Kubernetes em `deploy/kubernetes/`;
- estrutura Terraform em `infra/terraform/`;
- pipeline de deploy em `.github/workflows/deploy.yml`;
- README principal atualizado com exemplos rápidos de API;
- guia de scans de segurança em `docs/security/SECURITY_SCAN_GUIDE.md`;
- desenho textual/Mermaid da arquitetura em `docs/architecture/PHASE2_ARCHITECTURE.md`;
- roteiro do vídeo em `docs/delivery/PHASE2_VIDEO_SCRIPT.md`.

Validações finais registradas após as correções da Fase 2:

- `mvn test`: 167 testes, 0 falhas, 0 erros e 0 ignorados.
- `mvn clean verify`: passou com build, testes, empacotamento e gate JaCoCo aprovado.
- `npm audit --json`: resultado final com 0 vulnerabilidades.
- `terraform fmt -check`, `terraform init -backend=false` e `terraform validate`: passaram.
- `kubectl apply --dry-run=client -f deploy/kubernetes/`: não concluiu apenas por ausência de cluster Kubernetes ativo/configurado no ambiente local; não houve erro identificado nos manifests a partir dessa falha.

## 7. Conclusão

O AutoCare Hub entrega um MVP backend alinhado ao desafio proposto, cobrindo o ciclo principal de atendimento de uma
oficina mecânica: cadastro de clientes e veículos, criação de Ordem de Serviço, composição com serviços e peças, geração
e aprovação de orçamento, controle de status, estoque e consulta pelo cliente.

A gestão administrativa diferencia Admin Master, admins de oficina/loja, funcionários e cliente final. As contas de
oficinas e lojas são vinculadas por `companyId`, mas os IDs são gerados pelo backend. Na interface, o Admin Master
seleciona a empresa pelo nome ou cadastra uma nova empresa informando nome e tipo.

A entrega também inclui documentação DDD, Event Storming, contrato OpenAPI, testes automatizados, execução local com
Docker e relatório de vulnerabilidades. As vulnerabilidades críticas e altas encontradas nos scans iniciais foram
corrigidas. As duas CVEs médias restantes foram registradas como risco residual aceito, com justificativa técnica e
mitigação documentada.

Para a Fase 2, a entrega passa a demonstrar arquitetura evoluída, automação, infraestrutura como código, Kubernetes,
HPA, CI/CD e documentação de apoio. Permanecem como dados externos a confirmar antes do envio final: link do vídeo
`[INSERIR LINK DO VÍDEO ANTES DA ENTREGA]`, PDF final exportado após preencher esse link e acesso do usuário
`soat-architecture`.
