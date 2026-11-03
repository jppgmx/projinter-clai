# AGENTS.md (frontend)

Estas instruções complementam o `AGENTS.md` da raiz e valem para `apps/frontend`.

## Estado atual

- React 19, TypeScript, Vite e Tailwind CSS, em migração incremental a partir de páginas legadas.
- Organização em `src/`: `app` (inicialização, rotas, provedores e layout), `features` (fluxos por funcionalidade, como `dashboard`) e `shared` (cliente HTTP, tratamento de erros, componentes, tipos e utilitários).
- O ponto de entrada é `src/main.tsx`, que carrega `src/app/app.tsx`.
- As páginas legadas continuam válidas. A fonte é `html/`, `js/` e `css/`, usados pelo `vite.config.ts` e pelo `pom.xml` do backend. Os `.html` e `.js` antigos na raiz de `apps/frontend` não são usados no build nem no empacotamento: não os edite nem os apague sem combinar com o grupo. Não reescreva um fluxo legado em React sem que a tarefa peça essa migração.
- Não há login nem autenticação. Não reintroduza login, mocks ou MSW sem combinar com o grupo.

## Regras

- Consuma só endpoints reais do backend, pelo cliente HTTP de `shared`.
- Trate estados de carregamento, vazio e erro em toda tela que consome a API.
- Reutilize componentes e utilitários de `shared` antes de criar outros.
- Não coloque URLs, chaves ou valores de `.env` no código. Use `.env.example` como modelo e não versione `.env`.
- Nunca exiba dados de laudos ou de estudantes em `console.log`.

## Comandos (em `apps/frontend`)

```bash
npm ci
npm run dev      # porta 4040, com /api encaminhado para o backend na 8080
npm run lint
npm run build
```

## Validação

Antes de concluir uma tarefa de frontend, rode `npm run lint` e `npm run build`. Se algo falhar ou não puder ser executado, registre isso no resumo. Se o projeto não tiver testes automatizados de frontend, diga isso explicitamente.
