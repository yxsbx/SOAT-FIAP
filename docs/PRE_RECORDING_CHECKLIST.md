# Checklist Pré-Gravação - Tech Challenge

Responsável: Yasmin Barcelos Pires - RM370897  
Data de referência: 20/06/2026

Use este checklist antes de gravar o vídeo. A ideia é chegar no dia da gravação com ambiente, dados, documentação e acessos já validados.

## Checklist completo

### 1. Subir o projeto com docker-compose

- [ ] Confirmar que o arquivo `.env` existe.
- [ ] Confirmar que `POSTGRES_PASSWORD` está preenchido no `.env`.
- [ ] Confirmar que `JWT_SECRET` está preenchido e tem pelo menos 32 bytes.
- [x] Subir PostgreSQL, API e frontend:

```powershell
docker compose up -d --build
```

- [ ] Confirmar containers ativos:

```powershell
docker compose ps
```

### 2. Confirmar backend funcionando

- [ ] Abrir a API local:

```text
http://localhost:8080
```

- [ ] Confirmar que a aplicação subiu sem erro nos logs:

```powershell
docker compose logs app
```

- [ ] Se estiver rodando a API pelo Maven, confirmar:

```powershell
mvn spring-boot:run
```

### 3. Confirmar frontend funcionando

- [x] Confirmar o container `autocarehub-web` em `docker compose ps`.

- [ ] Abrir:

```text
http://localhost:5173
```

- [ ] Confirmar que a tela carrega sem erro visível.
- [ ] Confirmar login sem erro de CORS por `http://localhost:5173`.
- [ ] Opcional: confirmar acesso pelo IP local da máquina.

### 4. Confirmar banco populado com dados de demo

- [ ] Confirmar que as migrations Flyway rodaram.
- [ ] Conferir se os dados de seed da migration baseline estão disponíveis.
- [ ] Validar pelo Swagger ou banco que existem registros de:
  - [ ] clientes;
  - [ ] veículos;
  - [ ] serviços;
  - [ ] peças;
  - [ ] usuários.

Arquivo de referência:

```text
src/main/resources/db/migration/V1__create_autocarehub_baseline.sql
```

### 5. Confirmar Swagger acessível

- [ ] Abrir:

```text
http://localhost:8080/swagger-ui.html
```

- [ ] Confirmar que a página carrega.
- [ ] Confirmar que os endpoints aparecem agrupados.
- [ ] Confirmar que o botão `Authorize` está disponível.

### 6. Fazer login com usuário admin

- [ ] Abrir no Swagger:

```text
POST /api/v1/auth/login
```

- [ ] Usar um usuário admin válido dos dados de demo.
- [x] Senha universal de todos os usuários seed: `autocare123`.
- [ ] Copiar o token retornado.
- [ ] Clicar em `Authorize`.
- [ ] Informar:

```text
Bearer <token>
```

- [ ] Confirmar que endpoints administrativos respondem com o token.

### 7. Testar criação de cliente

- [ ] Abrir:

```text
POST /api/v1/customers
```

- [ ] Criar cliente com CPF/CNPJ válido.
- [ ] Confirmar resposta `201`.
- [ ] Guardar o `id` do cliente para a demonstração.

### 8. Testar criação de veículo

- [ ] Abrir:

```text
POST /api/v1/vehicles
```

- [ ] Criar veículo vinculado ao cliente criado.
- [ ] Usar placa válida no formato antigo ou Mercosul.
- [ ] Confirmar resposta `201`.
- [ ] Guardar o `id` do veículo.

### 9. Testar criação de serviço

- [ ] Abrir:

```text
POST /api/v1/workshop-services
```

- [ ] Criar serviço com nome, descrição, preço e tempo estimado.
- [ ] Confirmar resposta `201`.
- [ ] Guardar o `id` do serviço.

### 10. Testar criação de peça

- [ ] Abrir:

```text
POST /api/v1/parts
```

- [ ] Criar peça com preço, estoque e estoque mínimo.
- [ ] Confirmar resposta `201`.
- [ ] Guardar o `id` da peça.

### 11. Testar criação de OS

- [ ] Abrir:

```text
POST /api/v1/service-orders
```

- [ ] Criar OS usando:
  - [ ] documento do cliente;
  - [ ] `vehicleId`;
  - [ ] serviço criado;
  - [ ] peça criada, se fizer sentido para a demo.
- [ ] Confirmar resposta `201`.
- [ ] Guardar o `id` da OS.

### 12. Testar geração automática de orçamento

- [ ] Criar uma OS com `generateBudget: true`, ou usar endpoint manual:

```text
POST /api/v1/service-orders/{serviceOrderId}/budget/generate
```

- [ ] Confirmar que o orçamento foi gerado.
- [ ] Confirmar que o valor total reflete serviços e peças.

### 13. Testar aprovação de orçamento

- [ ] Abrir:

```text
POST /api/v1/service-orders/{serviceOrderId}/budget/approve
```

- [ ] Aprovar orçamento de uma OS em status compatível.
- [ ] Confirmar resposta de sucesso.

### 14. Testar mudança automática de status

- [ ] Confirmar que OS com orçamento gerado fica em `WAITING_APPROVAL`.
- [ ] Confirmar que a aprovação registra `approvedAt` e confirma a baixa das peças reservadas.
- [ ] Alterar a OS aprovada para `IN_PROGRESS` e confirmar o início da execução.
- [ ] Confirmar que status inválido é bloqueado quando aplicável.

Endpoints úteis:

```text
GET   /api/v1/service-orders
PATCH /api/v1/service-orders/{serviceOrderId}/status
```

### 15. Testar consulta da OS pelo cliente

- [ ] Abrir endpoint de acompanhamento:

```text
GET /api/v1/service-orders/tracking?serviceOrderId={serviceOrderId}
```

- [ ] Confirmar que a resposta mostra status, dados da OS e informações de orçamento.
- [ ] Se usar endpoint por cliente, testar também:

```text
GET /api/v1/customers/{customerId}/service-orders
```

### 16. Testar controle de estoque

- [ ] Confirmar estoque inicial da peça.
- [ ] Testar reserva:

```text
PATCH /api/v1/parts/{partId}/reserve
```

- [ ] Testar liberação de reserva:

```text
PATCH /api/v1/parts/{partId}/release-reservation
```

- [ ] Testar baixa/commit de reserva:

```text
PATCH /api/v1/parts/{partId}/commit-reservation
```

- [ ] Testar movimentação:

```text
PATCH /api/v1/parts/{partId}/stock-movement
```

- [ ] Confirmar que quantidade disponível e reservada mudam corretamente.

### 17. Rodar testes automatizados

- [ ] Rodar:

```powershell
mvn test
```

- [ ] Rodar validação completa:

```powershell
mvn verify
```

- [ ] Confirmar que todos os testes passam.

### 18. Verificar cobertura

- [ ] Abrir relatório JaCoCo:

```text
target/site/jacoco/index.html
```

- [ ] Conferir métricas documentadas no relatório.
- [ ] Confirmar que a cobertura usada na entrega está atualizada em:

```text
docs/SECURITY_REPORT.md
docs/DELIVERY_DOCUMENT.md
```

### 19. Rodar scan de segurança

- [ ] Rodar scan backend:

```powershell
mvn dependency-check:check
```

- [ ] Rodar audit frontend:

```powershell
cd frontend
npm audit --json
```

- [ ] Se exigido, rodar scans complementares:
  - [x] Docker image scan com Docker Scout.
  - [x] Secrets com Gitleaks.
  - [x] SAST com Semgrep.

Guia:

```text
docs/SECURITY_SCAN_GUIDE.md
```

### 20. Atualizar relatório de vulnerabilidades

- [ ] Conferir relatório principal:

```text
docs/SECURITY_REPORT.md
```

- [ ] Atualizar resultados se algum scan novo foi executado.
- [ ] Confirmar caminhos das evidências.
- [ ] Confirmar vulnerabilidades encontradas.
- [ ] Confirmar correções aplicadas.
- [ ] Confirmar pendências conhecidas.

### 21. Conferir README

- [ ] Abrir:

```text
README.md
```

- [ ] Conferir:
  - [ ] descrição do projeto;
  - [ ] funcionalidades;
  - [ ] tecnologias;
  - [ ] arquitetura;
  - [ ] execução local;
  - [ ] Docker;
  - [ ] Swagger;
  - [ ] testes;
  - [ ] vulnerabilidades;
  - [ ] limitações.

### 22. Conferir documentação DDD

- [ ] Abrir:

```text
docs/DDD_DOCUMENTATION.md
```

- [ ] Conferir:
  - [ ] contexto do problema;
  - [ ] linguagem ubíqua;
  - [ ] subdomínios;
  - [ ] bounded contexts;
  - [ ] entidades;
  - [ ] value objects;
  - [ ] agregados;
  - [ ] repositórios;
  - [ ] use cases.

- [ ] Conferir Event Storming:

```text
docs/EVENT_STORMING.md
```

### 23. Conferir documento final de entrega

- [ ] Abrir:

```text
docs/DELIVERY_DOCUMENT.md
```

- [ ] Preencher campos obrigatórios:
  - [x] entrega individual;
  - [x] participante;
  - [x] RM;
  - [x] username do Discord: `yxsbx`;
  - [x] link do repositório privado;
  - [x] documentação DDD e Event Storming no repositório;
  - [x] rota local do Swagger.

- [ ] Conferir se as 24 seções estão completas.

### 24. Conferir links do PDF

- [x] Converter o Markdown final para PDF.
- [x] Abrir o PDF gerado.
- [x] Clicar nos links principais:
  - [x] repositório privado;
  - [x] documentação DDD e Event Storming;
  - [x] contrato OpenAPI;
  - [x] relatórios de segurança e validação.
- [x] Conferir que os únicos placeholders restantes são secrets locais do `.env`.
- [x] Conferir legibilidade de tabelas no PDF.

### 25. Conferir acesso ao repositório privado para o usuário soatarchitecture

- [x] Confirmar que `https://github.com/yxsbx/SOAT-FIAP` é privado.
- [x] Confirmar que a conta `yxsbx` possui permissão administrativa.
- [x] Consultar colaboradores e convites pendentes em 20/06/2026.
- [x] Acesso Read concedido a `soatarchitecture`, conforme confirmação da responsável.
- [x] Confirmar que o usuário abaixo possui acesso:

```text
soatarchitecture
```

- [x] No GitHub, abrir `Settings > Collaborators`.
- [x] Buscar `soatarchitecture` e conferir a permissão de leitura.
- [ ] Confirmar novamente o acesso após o push final na branch `main`.
- [ ] Validar que o link do repositório no documento final aponta para o repositório correto.

## Versão resumida para o dia da gravação

Use esta versão como checklist rápido logo antes de clicar em gravar.

- [ ] `.env` preenchido com `POSTGRES_PASSWORD` e `JWT_SECRET`.
- [ ] `docker compose up -d --build` executado com sucesso.
- [ ] Backend ativo em `http://localhost:8080`.
- [ ] Swagger abre em `http://localhost:8080/swagger-ui.html`.
- [ ] Frontend ativo em `http://localhost:5173`.
- [ ] Banco tem dados de demo.
- [ ] Login admin testado e token JWT pronto.
- [ ] `Authorize` do Swagger configurado com `Bearer <token>`.
- [ ] IDs separados para cliente, veículo, serviço, peça e OS.
- [ ] Criação de cliente testada.
- [ ] Criação de veículo testada.
- [ ] Criação de serviço testada.
- [ ] Criação de peça testada.
- [ ] Criação de OS testada.
- [ ] Geração de orçamento testada.
- [ ] Aprovação de orçamento testada.
- [ ] Mudança de status validada.
- [ ] Consulta de acompanhamento da OS testada.
- [ ] Controle de estoque testado.
- [ ] `mvn test` ou `mvn verify` passando.
- [ ] Relatório JaCoCo conferido.
- [ ] Scan de segurança conferido.
- [ ] `docs/SECURITY_REPORT.md` atualizado.
- [ ] `README.md` conferido.
- [ ] `docs/DDD_DOCUMENTATION.md` conferido.
- [ ] `docs/EVENT_STORMING.md` conferido.
- [ ] `docs/DELIVERY_DOCUMENT.md` preenchido e revisado.
- [ ] PDF final aberto e links conferidos.
- [x] Usuário `soatarchitecture` com acesso Read ao repositório privado.
- [ ] Alterações finais disponíveis na branch `main`.
- [ ] Roteiro do vídeo aberto em `docs/VIDEO_SCRIPT.md`.
- [ ] Abas já preparadas: Swagger, README, DDD, Event Storming, relatório de segurança e sistema.
