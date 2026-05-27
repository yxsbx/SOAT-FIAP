# Relatorio de Vulnerabilidades - AutoCare Hub

## 1. Objetivo da Analise

Registrar a analise de seguranca e vulnerabilidades do projeto AutoCare Hub, contemplando dependencias, configuracoes,
codigo da aplicacao, container, exposicao de APIs e boas praticas exigidas no Tech Challenge.

## 2. Ferramenta Usada no Scan

| Campo                | Valor                                                      |
|----------------------|------------------------------------------------------------|
| Ferramenta           | [PREENCHER - exemplo: OWASP Dependency-Check Maven Plugin] |
| Versao               | [PREENCHER]                                                |
| Comando executado    | `[PREENCHER - exemplo: mvn dependency-check:check]`        |
| Local dos relatorios | `[PREENCHER - exemplo: target/dependency-check]`           |

## 3. Data da Analise

| Campo       | Valor                                |
|-------------|--------------------------------------|
| Data        | [PREENCHER - DD/MM/AAAA]             |
| Responsavel | [PREENCHER]                          |
| Ambiente    | [PREENCHER - local, CI, homologacao] |

## 4. Escopo Analisado

- Codigo fonte backend Spring Boot.
- Arquivo `pom.xml` e dependencias Maven.
- Configuracoes de seguranca e ambiente.
- Dockerfile e `docker-compose.yml`.
- Migrations e dados de demonstracao.
- Documentacao Swagger/OpenAPI.

Itens fora do escopo desta execucao:

- [PREENCHER, se houver]

## 5. Resumo Executivo

| Indicador                        | Resultado   |
|----------------------------------|-------------|
| Total de dependencias analisadas | [PREENCHER] |
| Vulnerabilidades criticas        | [PREENCHER] |
| Vulnerabilidades altas           | [PREENCHER] |
| Vulnerabilidades medias          | [PREENCHER] |
| Vulnerabilidades baixas          | [PREENCHER] |
| Riscos aceitos                   | [PREENCHER] |
| Pendencias bloqueantes           | [PREENCHER] |

Conclusao executiva:

```text
[PREENCHER apos a execucao real do scan. Nao inserir resultados estimados.]
```

## 6. Vulnerabilidades Encontradas

| ID/CVE      | Severidade                 | Arquivo/Dependencia afetada | Descricao do problema | Impacto     | Correcao aplicada | Status                                 |
|-------------|----------------------------|-----------------------------|-----------------------|-------------|-------------------|----------------------------------------|
| [PREENCHER] | [CRITICAL/HIGH/MEDIUM/LOW] | [PREENCHER]                 | [PREENCHER]           | [PREENCHER] | [PREENCHER]       | [Corrigido/Aceito como risco/Pendente] |

## 7. Evidencias do Scan

Anexar ou referenciar evidencias geradas pela ferramenta:

- Caminho do HTML: `[PREENCHER]`
- Caminho do JSON/XML/SARIF, se existir: `[PREENCHER]`
- Hash ou identificador do relatorio, se aplicavel: `[PREENCHER]`
- Prints ou trechos relevantes: `[PREENCHER]`

## 8. Boas Praticas de Seguranca Implementadas

- JWT assinado com segredo fornecido por variavel de ambiente.
- Expiracao configuravel do token JWT.
- Senhas armazenadas com BCrypt.
- APIs administrativas protegidas por Spring Security.
- CORS restrito ao frontend local esperado no MVP.
- Headers de seguranca configurados no Spring Security.
- DTOs explicitos em requests e responses.
- Jackson configurado para rejeitar campos desconhecidos.
- Validacao real de CPF/CNPJ e placa.
- Normalizacao de CPF/CNPJ e placa para evitar duplicidade por formatacao.
- Tratamento global de erros sem retorno de stacktrace.
- Dockerfile com usuario nao-root.
- `.env` local ignorado pelo Git e `.env.example` sem segredo real.

## 9. Checklist de Seguranca

| Item                     | Status        | Evidencia   | Observacao                                                      |
|--------------------------|---------------|-------------|-----------------------------------------------------------------|
| JWT                      | [OK/PENDENTE] | [PREENCHER] | Secret por variavel de ambiente e expiracao configuravel.       |
| Senhas                   | [OK/PENDENTE] | [PREENCHER] | Verificar BCrypt e ausencia de senha em responses.              |
| Secrets                  | [OK/PENDENTE] | [PREENCHER] | Confirmar que `.env` nao esta versionado.                       |
| Logs                     | [OK/PENDENTE] | [PREENCHER] | Confirmar ausencia de senha, token completo, CPF/CNPJ completo. |
| Validacao de entrada     | [OK/PENDENTE] | [PREENCHER] | CPF/CNPJ, placa, email, preco, quantidade e tamanhos.           |
| Tratamento de erro       | [OK/PENDENTE] | [PREENCHER] | Respostas padronizadas sem stacktrace.                          |
| CORS                     | [OK/PENDENTE] | [PREENCHER] | Validar origens permitidas por ambiente.                        |
| Swagger                  | [OK/PENDENTE] | [PREENCHER] | Publico no MVP academico; restringir em producao.               |
| Dependencias vulneraveis | [OK/PENDENTE] | [PREENCHER] | Preencher apos scan real.                                       |
| Docker                   | [OK/PENDENTE] | [PREENCHER] | Build multi-stage e usuario nao-root.                           |
| Banco de dados           | [OK/PENDENTE] | [PREENCHER] | Credenciais via ambiente e migrations versionadas.              |
| Dados sensiveis          | [OK/PENDENTE] | [PREENCHER] | Mascarar ou evitar retorno desnecessario.                       |

## 10. Recomendacoes Futuras

- Executar o scan em pipeline CI a cada pull request.
- Bloquear merge quando houver vulnerabilidade critica ou alta sem justificativa.
- Restringir Swagger por perfil, rede ou autenticacao em ambientes nao academicos.
- Centralizar mascaramento de CPF/CNPJ em logs e respostas administrativas, se necessario.
- Adotar auditoria de alteracoes sensiveis por usuario.
- Revisar periodicamente imagens Docker e versoes base.
