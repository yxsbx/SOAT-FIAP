# AutoCare Hub Web

Interface web em Vue 3 para a API AutoCare Hub.

## Requisitos

- Node 18+
- API em execução em `http://localhost:8080`

## Executar

```bash
npm install
npm run dev
```

Abra:

```text
http://localhost:5173
```

Acesso local:

```text
Admin Master: master@autocarehub.com / <SENHA_DEMO_LOCAL>
Admin de oficina: oficina.admin@autocarehub.com / <SENHA_DEMO_LOCAL>
Admin de loja de peças: loja.admin@autocarehub.com / <SENHA_DEMO_LOCAL>
Funcionário de oficina: oficina.funcionario@autocarehub.com / <SENHA_DEMO_LOCAL>
Funcionário de loja de peças: loja.funcionario@autocarehub.com / <SENHA_DEMO_LOCAL>
Cliente: cliente@autocarehub.com / <SENHA_DEMO_LOCAL>
```

## Scripts úteis

```bash
npm run backend:db
npm run backend:api
npm run backend:test
npm run backend:security
npm run build
```

Para mudar a URL da API, copie `.env.example` para `.env` e ajuste `VITE_API_BASE_URL`.
