# AutoCare Hub Web

Interface web em Vue 3 para a API AutoCare Hub.

O frontend é demonstrativo, mas foi construído para mostrar a solução como produto utilizável: oficinas gerenciam
clientes, veículos, serviços, peças, estoque e ordens; clientes visualizam acompanhamento e aprovações; administradores
acompanham gestão e operação. Ele ajuda a banca a enxergar que a API não é uma peça isolada, e sim a base de uma
experiência real para pessoas e empresas.

## Executar com o projeto completo

Na raiz do repositório:

```powershell
docker compose up -d --build
```

Abra `http://localhost:5173`. O container Nginx encaminha `/api` para o backend, portanto o frontend
também pode ser acessado pelo IP local da máquina sem configuração adicional de CORS.

## Desenvolvimento com hot reload

Requisitos:

- Node.js `^20.19.0 || >=22.12.0`
- API em execução em `http://localhost:8080`

```powershell
npm ci
npm run dev
```

O Vite aceita hostnames externos e encaminha `/api` para a API local.

## Usuários seed

A senha universal de todos os usuários abaixo é `autocare123`.

| Usuário                               | Perfil                       |
|---------------------------------------|------------------------------|
| `admin@autocarehub.com`               | Admin técnico                |
| `master@autocarehub.com`              | Admin Master                 |
| `oficina.admin@autocarehub.com`       | Admin de oficina             |
| `loja.admin@autocarehub.com`          | Admin de loja de peças       |
| `oficina.funcionario@autocarehub.com` | Funcionário de oficina       |
| `loja.funcionario@autocarehub.com`    | Funcionário de loja de peças |
| `cliente@autocarehub.com`             | Cliente                      |

## Verificações

```powershell
npm run lint
npm run build
npm audit --audit-level=low
```

`VITE_API_BASE_URL` é opcional. Sem essa variável, a aplicação usa URLs relativas e os proxies do
Vite ou do Nginx.
