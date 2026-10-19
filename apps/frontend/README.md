# CLAI — Front end

React + Vite + TypeScript + Tailwind CSS. API simulada com MSW (o back end Spring é separado e não será alterado).

## Rodar

```bash
npm install
cp .env.example .env
npm run dev
```

Login de teste (mock): qualquer usuário + senha `clai123`.

## Variáveis (.env)

- `VITE_API_URL`: base da API (`/api` em dev, passa pelo proxy do Vite).
- `VITE_API_PROXY_TARGET`: destino do proxy (Spring).
- `VITE_USE_MOCKS`: `true` liga o MSW; `false` usa a API real.

## shadcn/ui

O alias `@` já está configurado. Para adicionar o shadcn: `npx shadcn@latest init`.

## Pendências [ABERTO]

Autenticação, formato de erro, permissões por perfil, subitens do menu "Planos e Estudos de caso", rotas do aluno/atendimento, telas sem mockup.
