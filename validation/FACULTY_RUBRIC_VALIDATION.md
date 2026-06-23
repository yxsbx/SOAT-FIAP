# Validação pelo Roteiro da Faculdade

**Projeto:** AutoCare Hub  
**Responsável:** Yasmin Barcelos Pires - RM370897  
**Data:** 20/06/2026

## Matriz de validação

- **Risco de perda de nota:**  DDD superficial
  - **Estado atual:**  Atendido
  - **Evidência:**  `docs/DDD_DOCUMENTATION.md` descreve subdomínios, bounded contexts, entidades, value objects,
    agregado, repositórios, serviços e linguagem ubíqua.
  - **Ação restante:**  Mostrar apenas decisões centrais no vídeo.

- **Risco de perda de nota:**  Event Storming fraco
  - **Estado atual:**  Atendido
  - **Evidência:**  `docs/EVENT_STORMING.md` contém atores, comandos, eventos, políticas, exceções e diagramas Mermaid.
  - **Ação restante:**  Mostrar o fluxo da OS e a máquina de estados.

- **Risco de perda de nota:**  README sem execução clara
  - **Estado atual:**  Atendido
  - **Evidência:**  `README.md` documenta `.env`, comando Docker único, URLs, testes, Swagger, usuários e scans.
  - **Ação restante:**  Nenhuma ação técnica.

- **Risco de perda de nota:**  Docker não sobe de primeira
  - **Estado atual:**  Atendido
  - **Evidência:**  `docker compose up -d --build` validado com PostgreSQL, API e frontend.
  - **Ação restante:**  Demonstrar o comando e `docker compose ps`.

- **Risco de perda de nota:**  Swagger incompleto
  - **Estado atual:**  Atendido
  - **Evidência:**  OpenAPI versionado e `/v3/api-docs` validado com HTTP 200.
  - **Ação restante:**  Demonstrar login, Authorize e endpoints principais.

- **Risco de perda de nota:**  Testes sem fluxo principal
  - **Estado atual:**  Atendido
  - **Evidência:**  Testes de integração cobrem OS, orçamento, estoque, segurança, CRUD administrativo e persistência.
  - **Ação restante:**  Nenhuma ação técnica.

- **Risco de perda de nota:**  Cobertura abaixo de 80%
  - **Estado atual:**  Atendido globalmente
  - **Evidência:**  96,09% de instruções, 97,02% de linhas e 90,32% de branches; gates de 90/90/90; 145 testes.
  - **Ação restante:**  Explicar apenas os excludes técnicos legítimos.

- **Risco de perda de nota:**  JWT parcial
  - **Estado atual:**  Atendido
  - **Evidência:**  Login JWT, filtro Bearer, expiração, BCrypt e autorização por papel/propriedade.
  - **Ação restante:**  Mostrar login e acesso autorizado no vídeo.

- **Risco de perda de nota:**  Cliente não consulta OS
  - **Estado atual:**  Atendido
  - **Evidência:**  Endpoint por cliente e tracking protegido, com teste de autorização.
  - **Ação restante:**  Demonstrar com `cliente@autocarehub.com`.

- **Risco de perda de nota:**  Orçamento não automático
  - **Estado atual:**  Atendido
  - **Evidência:**  A criação aceita `generateBudget`; também existe geração manual controlada por status.
  - **Ação restante:**  Demonstrar um dos fluxos e explicar o outro.

- **Risco de perda de nota:**  Status não acompanha ações
  - **Estado atual:**  Atendido
  - **Evidência:**  Geração muda para `WAITING_APPROVAL`; aprovação registra aceite; execução e finalização têm
    transições explícitas.
  - **Ação restante:**  Não afirmar que aprovação inicia execução automaticamente.

- **Risco de perda de nota:**  Relatório genérico
  - **Estado atual:**  Atendido
  - **Evidência:**  Dependency-Check, npm audit, Docker Scout, Gitleaks e Semgrep têm evidências reais.
  - **Ação restante:**  Mencionar a CVE média aceita no frontend.

- **Risco de perda de nota:**  PDF sem links/evidências
  - **Estado atual:**  Atendido após regeneração
  - **Evidência:**  Documento de entrega contém links GitHub apontando para `main` e rotas locais.
  - **Ação restante:**  Abrir os links do PDF final antes do envio.

- **Risco de perda de nota:**  Vídeo só de tela
  - **Estado atual:**  Preparado
  - **Evidência:**  `video/VIDEO_SCRIPT.md` reserva tempo para arquitetura, DDD, Event Storming, segurança, testes e
    vulnerabilidades.
  - **Ação restante:**  Ensaiar e gravar em até 15 minutos.

## Riscos residuais

### Prioridade máxima

1. Gravar o vídeo seguindo o roteiro.
2. Executar o checklist rápido imediatamente antes da gravação.
3. Manter as evidências do commit final junto da entrega.
4. Confirmar que o link enviado à faculdade aponta para a branch `main`.

### Prioridade média

1. Explicar que a cobertura é global e que apenas código gerado/estrutural está excluído.
2. Mostrar aprovação e início da execução como ações separadas.
3. Demonstrar a consulta da OS pelo cliente.
4. Informar com transparência a CVE média da imagem frontend sem correção disponível.

## Evidências principais

```text
docs/DDD_DOCUMENTATION.md
docs/EVENT_STORMING.md
docs/SECURITY_REPORT.md
validation/FINAL_VALIDATION_REPORT.md
video/VIDEO_SCRIPT.md
output/pdf/AutoCare_Hub_Tech_Challenge_Entrega_Final.pdf
target/site/jacoco/index.html
target/dependency-check/dependency-check-report.html
security-reports/frontend-dependencies/npm-audit-report.json
security-reports/docker/docker-scout-cves.txt
security-reports/docker/docker-scout-frontend-cves.txt
security-reports/secrets/gitleaks.json
security-reports/static-analysis/semgrep.json
```
