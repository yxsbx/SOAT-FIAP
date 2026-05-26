# AutoCare Hub Web

Frontend Vue 3 para a API AutoCare Hub.

## Requisitos

- Node 18+
- API rodando em `http://localhost:8080`

## Rodar

```bash
npm install
npm run dev
```

Abra:

```text
http://localhost:5173
```

Login local:

```text
Admin Master: master@autocarehub.com / autocare123
Admin de oficina: oficina.admin@autocarehub.com / autocare123
Admin de loja de peças: loja.admin@autocarehub.com / autocare123
Funcionário de oficina: oficina.funcionario@autocarehub.com / autocare123
Funcionário de loja de peças: loja.funcionario@autocarehub.com / autocare123
Cliente: cliente@autocarehub.com / autocare123
```

## Scripts uteis

```bash
npm run backend:db
npm run backend:api
npm run backend:test
npm run backend:security
npm run build
```

Para mudar a URL da API, copie `.env.example` para `.env` e ajuste `VITE_API_BASE_URL`.
