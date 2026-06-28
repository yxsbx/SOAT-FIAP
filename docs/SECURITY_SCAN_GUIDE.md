# Guia de Scans de Vulnerabilidade - AutoCare Hub

Este guia é um roteiro operacional para executar scans de segurança do AutoCare Hub, salvar evidências locais e atualizar
o relatório final. Ele não substitui o `docs/SECURITY_REPORT.md` e não deve registrar resultado que ainda não foi
validado por execução real.

Depois de rodar os comandos, mantenha os arquivos brutos em `security-reports/` ou `target/` e consolide no
`docs/SECURITY_REPORT.md` apenas o resumo relevante para a entrega: ferramenta, versão, data, comando, evidência,
achados, correções e riscos aceitos.

## Estrutura de resultados

Use a estrutura abaixo para armazenar evidências:

```text
security-reports/
  backend-dependencies/
  frontend-dependencies/
  docker/
  static-analysis/
  secrets/
```

Os relatórios gerados são evidências locais e ficam fora do versionamento por padrão via `.gitignore`. Versione arquivos
brutos somente se a entrega exigir explicitamente e depois de conferir que não contêm secrets, tokens, senhas, CPF/CNPJ
real, caminhos sensíveis ou dados pessoais.

## Pré-requisitos

Obrigatórios para o projeto:

- Java 21
- Maven 3.9+
- Node.js compatível com o frontend
- npm
- Docker

Ferramentas usadas ou aceitas no processo de validação:

- OWASP Dependency-Check, já configurado via Maven.
- `npm audit`, disponível com npm.
- Docker Scout ou Trivy para imagem Docker.
- Semgrep para análise estática, via CLI local ou imagem Docker.
- Gitleaks ou TruffleHog para secrets.

No relatório final, cite somente as ferramentas realmente executadas. Se trocar Docker Scout por Trivy, ou Gitleaks por
TruffleHog, atualize também os nomes das evidências e a descrição no `docs/SECURITY_REPORT.md`.

## 1. Scan de dependências backend

O backend usa Maven e já possui o plugin OWASP Dependency-Check configurado no `pom.xml`.

Comando:

```powershell
mvn dependency-check:check -DautoUpdate=false
```

Relatórios gerados pelo plugin:

```text
target/dependency-check/
```

Copiar evidências para a pasta da entrega:

```powershell
New-Item -ItemType Directory -Force security-reports/backend-dependencies
Copy-Item target/dependency-check/* security-reports/backend-dependencies/ -Recurse -Force
```

Observações:

- O build está configurado para falhar com CVSS alto conforme `failBuildOnCVSS`.
- Use `docs/security/dependency-check-suppressions.xml` apenas para falso positivo justificado.
- Não ignore vulnerabilidades sem registrar motivo no relatório final.

## 2. Scan de dependências frontend

O frontend fica em `frontend/` e usa npm.

Comandos:

```powershell
cd frontend
npm audit --json | Out-File -Encoding utf8 ..\security-reports\frontend-dependencies\npm-audit-report.json
npm audit | Out-File -Encoding utf8 ..\security-reports\frontend-dependencies\npm-audit-report.txt
cd ..
```

Se quiser validar build e lint junto ao scan:

```powershell
cd frontend
npm run lint
npm run build
cd ..
```

Interpretação:

- Priorize `critical` e `high`.
- Para `moderate`, avalie se a dependência é usada em runtime ou apenas em desenvolvimento.
- Para dependências transitivas, registre se há atualização direta disponível ou se depende de pacote terceiro.

## 3. Scan de imagem Docker

Primeiro gere a imagem local:

```powershell
docker build -t autocarehub-api:scan .
```

### Opção A - Trivy

Se o Trivy estiver instalado:

```powershell
trivy image --severity CRITICAL,HIGH,MEDIUM --format table autocarehub-api:scan |
  Out-File -Encoding utf8 security-reports/docker/trivy-image.txt
trivy image --severity CRITICAL,HIGH,MEDIUM --format json autocarehub-api:scan |
  Out-File -Encoding utf8 security-reports/docker/trivy-image.json
```

Usando Docker:

```powershell
docker run --rm `
  -v /var/run/docker.sock:/var/run/docker.sock `
  -v ${PWD}/security-reports/docker:/reports `
  aquasec/trivy:latest image `
  --severity CRITICAL,HIGH,MEDIUM `
  --format json `
  --output /reports/trivy-image.json `
  autocarehub-api:scan
```

### Opção B - Docker Scout

Se Docker Scout estiver disponível:

```powershell
docker scout cves autocarehub-api:scan | Out-File -Encoding utf8 security-reports/docker/docker-scout-cves.txt
docker scout cves soat-fiap-frontend:latest | Out-File -Encoding utf8 security-reports/docker/docker-scout-frontend-cves.txt
```

O Dockerfile já executa a aplicação com usuário não-root, usa `read_only` no compose para o serviço da API e adiciona
`no-new-privileges`.

## 4. Scan estático de código

### Backend

Validações já disponíveis no projeto:

```powershell
mvn test
mvn verify
mvn spotless:check
```

Salvar evidências:

```powershell
mvn verify | Tee-Object -FilePath security-reports/static-analysis/maven-verify.txt
mvn spotless:check | Tee-Object -FilePath security-reports/static-analysis/spotless-check.txt
```

O comando `mvn spotless:check` se aplica porque o projeto possui Spotless configurado no `pom.xml`.

### Frontend

```powershell
cd frontend
npm run lint | Tee-Object -FilePath ..\security-reports\static-analysis\frontend-eslint.txt
cd ..
```

### Semgrep, se disponível

Via CLI local:

```powershell
semgrep scan --config auto --json --output security-reports/static-analysis/semgrep.json
semgrep scan --config auto | Out-File -Encoding utf8 security-reports/static-analysis/semgrep.txt
```

Via Docker:

```powershell
docker run --rm `
  -v ${PWD}:/src `
  -w /src `
  semgrep/semgrep semgrep scan `
  --config auto `
  --json `
  --output security-reports/static-analysis/semgrep.json
```

Observação: Semgrep pode apontar falsos positivos. Registre a decisão no relatório final em vez de apagar achados sem
análise.

## 5. Scan de secrets

### Gitleaks

Se o Gitleaks estiver instalado:

```powershell
gitleaks detect --source . --report-format json --report-path security-reports/secrets/gitleaks.json
gitleaks detect --source . --report-format sarif --report-path security-reports/secrets/gitleaks.sarif
```

Via Docker:

```powershell
docker run --rm `
  -v ${PWD}:/repo `
  zricethezav/gitleaks:latest detect `
  --source /repo `
  --report-format json `
  --report-path /repo/security-reports/secrets/gitleaks.json
```

### TruffleHog, alternativa

```powershell
trufflehog filesystem . --json | Out-File -Encoding utf8 security-reports/secrets/trufflehog.jsonl
```

O que revisar manualmente:

- `.env` não deve ser versionado.
- `.env.example` deve conter apenas placeholders.
- `JWT_SECRET`, `POSTGRES_PASSWORD`, tokens e senhas reais não devem aparecer em código, logs ou documentação.
- Seeds acadêmicos podem conter senha demo documentada, mas não devem ser usados em produção.

## 6. Comandos consolidados

Criar pastas:

```powershell
New-Item -ItemType Directory -Force `
  security-reports/backend-dependencies, `
  security-reports/frontend-dependencies, `
  security-reports/docker, `
  security-reports/static-analysis, `
  security-reports/secrets
```

Executar validações principais:

```powershell
mvn verify | Tee-Object -FilePath security-reports/static-analysis/maven-verify.txt
mvn dependency-check:check -DautoUpdate=false
Copy-Item target/dependency-check/* security-reports/backend-dependencies/ -Recurse -Force

cd frontend
npm audit --json | Out-File -Encoding utf8 ..\security-reports\frontend-dependencies\npm-audit-report.json
npm run lint | Tee-Object -FilePath ..\security-reports\static-analysis\frontend-eslint.txt
cd ..

docker build -t autocarehub-api:scan .
docker compose build frontend
```

Depois rode pelo menos uma ferramenta para container e uma para secrets, conforme disponibilidade local.

## 7. Onde salvar os resultados

Sugestão de arquivos:

```text
security-reports/backend-dependencies/dependency-check-report.html
security-reports/backend-dependencies/dependency-check-report.json
security-reports/frontend-dependencies/npm-audit-report.json
security-reports/frontend-dependencies/npm-audit-report.txt
security-reports/docker/docker-scout-cves.txt
security-reports/docker/docker-scout-frontend-cves.txt
security-reports/docker/trivy-image.json
security-reports/docker/trivy-image.txt
security-reports/static-analysis/maven-verify.txt
security-reports/static-analysis/frontend-eslint.txt
security-reports/static-analysis/semgrep.json
security-reports/secrets/gitleaks.json
security-reports/secrets/gitleaks.sarif
```

Antes de anexar ou versionar qualquer relatório, confira se ele não contém tokens, senhas, CPF/CNPJ real, caminhos
sensíveis ou dados pessoais.

## 8. Como interpretar severidades

Critério recomendado:

| Severidade | Tratamento recomendado                                                         |
| ---------- | ------------------------------------------------------------------------------ |
| Critical   | Corrigir antes da entrega ou justificar formalmente se for falso positivo.     |
| High       | Corrigir antes da entrega sempre que houver atualização segura disponível.     |
| Medium     | Avaliar explorabilidade, uso em runtime e impacto no MVP. Corrigir se simples. |
| Low        | Registrar e corrigir se não gerar risco de regressão.                          |
| Info       | Registrar como observação técnica ou melhoria de processo.                     |

Para dependências:

- Verifique se a biblioteca vulnerável roda em produção ou apenas em testes/desenvolvimento.
- Prefira atualização de versão.
- Evite suppressions sem justificativa.

Para código:

- Priorize autenticação, autorização, secrets, SQL Injection, exposição de stacktrace e dados sensíveis.
- Valide se o achado é realmente alcançável por entrada externa.

## 9. Como priorizar correções

Ordem recomendada:

1. Secrets reais versionados ou expostos.
2. JWT, senha, hash ou token exposto em log/response.
3. Endpoints administrativos sem autenticação/autorização.
4. SQL Injection, mass assignment e execução de entrada externa.
5. Stacktrace ou erro interno retornado ao usuário.
6. Dependências `critical` e `high` usadas em runtime.
7. CORS permissivo, Swagger produtivo exposto e Docker rodando como root.
8. Dependências de desenvolvimento com severidade média/baixa.

## 10. Como consolidar no relatório final

No `docs/SECURITY_REPORT.md`, para cada ferramenta executada, registre:

- ferramenta;
- versão, quando possível;
- data da execução;
- comando usado;
- pasta/arquivo de saída;
- resumo dos achados por severidade;
- vulnerabilidades corrigidas;
- falsos positivos aceitos com justificativa;
- riscos aceitos, quando houver.

Modelo de evidência:

```text
Ferramenta: OWASP Dependency-Check
Comando: mvn dependency-check:check -DautoUpdate=false
Saída: security-reports/backend-dependencies/dependency-check-report.html
Resumo: [preencher após execução]
Status: [sem achados/corrigido/aceito como risco]
```

Não preencha resultados antes de executar as ferramentas.

## 11. Checklist antes da entrega

Antes de finalizar a documentação:

- confirmar que `docs/SECURITY_REPORT.md` cita apenas ferramentas executadas de verdade;
- conferir se `.env`, `target/` e `security-reports/` continuam fora do versionamento;
- validar se os caminhos de evidência do relatório final existem localmente ou podem ser gerados pelos comandos acima;
- revisar se não há tokens, senhas, CPF/CNPJ real ou caminhos sensíveis nos arquivos que serão publicados;
- reexecutar `mvn verify` e `mvn dependency-check:check -DautoUpdate=false`;
- se Docker Scout, Semgrep, Gitleaks ou npm audit forem reexecutados, atualizar o resumo final com a data e o resultado mais recente.
