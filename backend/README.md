# Backend - AutoCare Hub

API Spring Boot do AutoCare Hub. Nesta fase, o backend continua como um monolito modular com organização inspirada em Clean Architecture/Arquitetura Hexagonal.

## Estrutura

```text
backend/
|-- pom.xml
`-- src/
    |-- main/java/br/com/autocarehub/
    |   |-- domain/          # Regras e modelos de negocio
    |   |-- application/     # Casos de uso e portas
    |   |-- infrastructure/  # Persistencia, seguranca e configuracoes tecnicas
    |   `-- interfaces/      # Controllers REST, mappers e contrato gerado
    |-- main/resources/      # application.yml, Flyway e Swagger UI estatico
    `-- test/                # Testes unitarios e de integração
```

## Comandos

Execute a partir de `backend/`:

```bash
mvn spring-boot:run
mvn test
mvn verify
mvn dependency-check:check
```

O contrato OpenAPI versionado fica fora do backend, em `../docs/api/openapi/openapi.yaml`, para manter documentação e código separados.

## Evolução futura

Quando o projeto evoluir para microsserviços, este backend pode ser movido para `services/autocare-api/` ou ser dividido por contexto de negócio, como `customers-service`, `service-orders-service` e `inventory-service`.
