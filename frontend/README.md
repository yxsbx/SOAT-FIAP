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
Usuario: admin@autocarehub.com
Senha: autocare123
Perfil: ADMIN
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
