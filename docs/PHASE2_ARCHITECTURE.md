# Desenho de arquitetura - Fase 2

O AutoCare Hub permanece como monolito modular com Arquitetura Hexagonal/Clean Architecture. A evolucao da Fase 2
adiciona automacao, infraestrutura como codigo, Kubernetes e escalabilidade horizontal sem transformar o sistema em
microsservicos.

## Diagrama

```mermaid
flowchart LR
    User["Usuario ou avaliador"] --> Web["Frontend demonstrativo Vue/Vite"]
    User --> Swagger["Swagger UI / OpenAPI"]
    Web --> Api["Backend Spring Boot"]
    Swagger --> Api

    subgraph App["Monolito AutoCare Hub"]
        Controllers["Interfaces REST"]
        UseCases["Aplicacao / Casos de uso"]
        Domain["Dominio"]
        Ports["Portas de saida"]
        Adapters["Adaptadores de infraestrutura"]
        Controllers --> UseCases
        UseCases --> Domain
        UseCases --> Ports
        Ports --> Adapters
    end

    Api --> Controllers
    Adapters --> Db["PostgreSQL"]

    subgraph Docker["Execucao local"]
        Compose["docker-compose.yml"]
        BackendImage["Imagem backend"]
        FrontendImage["Imagem frontend"]
        Compose --> BackendImage
        Compose --> FrontendImage
        Compose --> Db
    end

    subgraph K8s["Kubernetes"]
        DeployApi["Deployment backend"]
        DeployWeb["Deployment frontend"]
        SvcApi["Service backend"]
        SvcWeb["Service frontend"]
        ConfigMap["ConfigMap"]
        Secret["Secret"]
        HpaApi["HPA backend"]
        HpaWeb["HPA frontend"]
        Postgres["PostgreSQL academico"]
        DeployApi --> SvcApi
        DeployWeb --> SvcWeb
        ConfigMap --> DeployApi
        Secret --> DeployApi
        Secret --> Postgres
        HpaApi --> DeployApi
        HpaWeb --> DeployWeb
        DeployApi --> Postgres
    end

    subgraph IaC["Terraform"]
        Tf["infra/"]
        Tf --> ConfigMap
        Tf --> Secret
    end

    subgraph Pipeline["GitHub Actions"]
        Quality["Quality workflow"]
        Deploy["Deploy workflow"]
        Quality --> Tests["Testes e cobertura"]
        Deploy --> BackendImage
        Deploy --> FrontendImage
        Deploy --> K8s
    end
```

## Componentes

- Frontend: demonstracao visual em Vue/Vite.
- Backend: API REST Spring Boot com Clean Architecture/Hexagonal dentro de um monolito.
- Dominio: entidades, value objects, status e regras de negocio.
- Aplicacao: casos de uso, politicas de aplicacao e portas de saida.
- Infraestrutura: JPA, repositories, seguranca JWT, BCrypt e configuracoes.
- Docker: execucao local reproduzivel.
- Kubernetes: Deployments, Services, ConfigMaps, Secrets e HPA.
- Terraform: provisionamento base do namespace, ConfigMap e Secret em cluster local/acadêmico.
- CI/CD: qualidade, testes, build de imagens e deploy opcional no cluster.

## Refatoracao de aplicacao

- `UsersController` atua como adaptador REST e delega gestao de usuarios para casos de uso especificos.
- `CreateManagedUserUseCase`, `UpdateManagedUserUseCase`, `ListManageableUsersUseCase`,
  `ListManageableCompaniesUseCase` e `ListPartnerUsersUseCase` concentram regras de escopo, perfil e empresa.
- `UserManagementPolicy` centraliza a politica de administracao de usuarios por perfil e empresa.
- `AuthenticationGateway` e uma porta de saida da aplicacao; `SpringSecurityAuthenticationGateway` adapta
  `AuthenticationManager` e `JwtService`.
- `PasswordHasher` e uma porta de saida da aplicacao; `BCryptPasswordHasher` e o adaptador de infraestrutura baseado em
  Spring Security/BCrypt.
- `application` e `domain` nao importam Spring nem classes de `infrastructure`.
- Endpoints, Flyway, Swagger/OpenAPI e Docker permanecem sem mudanca de contrato por causa da refatoracao.

## Decisao de ambiente

Nao foi assumida uma cloud especifica. A Fase 2 esta preparada para demonstracao local/acadêmica com Kubernetes e
Terraform, mantendo placeholders onde credenciais ou ambiente real precisam ser definidos.
