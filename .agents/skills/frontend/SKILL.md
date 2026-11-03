---
name: frontend
description: Use ao criar ou alterar telas, componentes, chamadas à API ou formulários do frontend do CLAI, em React ou nas páginas legadas.
---

# Frontend CLAI

## Antes de alterar

- Leia `AGENTS.md` e `apps/frontend/AGENTS.md`.
- Confira o fluxo existente e os contratos reais antes de implementar.
- Leia `README.md` e `docs/arquitetura.md` antes de mudar arquitetura,
  contratos, build ou empacotamento.
- Preserve alterações preexistentes e limite a mudança ao pedido.

## Estrutura e fontes

Todos os caminhos abaixo são relativos a `apps/frontend`.

- `src/app`: inicialização, provedores, router e layout.
- `src/features`: funcionalidades; `dashboard` contém página, chamadas,
  tipos e componentes próprios. `placeholder` atende páginas inexistentes.
- `src/shared`: `api`, `components`, `types` e `utils`.
- Atenção: existe também `js/src`. Atualmente `index.html` carrega
  `/js/src/main.tsx`; Vite e TypeScript resolvem `@` para `js/src`.
  Confira esses arquivos antes de editar para atingir o código executado.
  Não sincronize árvores nem altere essa configuração fora do escopo.
- Legado ativo: `html/`, `js/*.js` e `css/`. Preserve os caminhos `.html`
  e os aliases definidos no Vite. Use links HTML para esses fluxos.
- Os HTML e scripts antigos da raiz, exceto a entrada `index.html`,
  não são a fonte do legado empacotado; não edite nem apague sem combinar.
- Não migre fluxos legados para React sem pedido explícito.

## API e estados

- No React, use `shared/api/api-client.ts`, como em
  `features/dashboard/dashboard-api.ts`; nunca use `fetch` solto em componentes.
- O cliente oferece `get`, `post` e `put` com JSON e trata resposta 204.
  Não presuma suporte a DELETE, upload multipart ou download binário.
- `shared/api/config.ts` lê `VITE_API_URL`, com fallback `/api`.
  Não repita a base nos caminhos. Use apenas `.env.example` como referência.
- `ApiError` contém `status` e `campos`; falhas de rede usam status 0.
  O contrato de erro tratado é `{ erro, campos? }`.
- Organize chamadas e tipos por funcionalidade. Para consultas React,
  siga `useQuery` do dashboard e o provedor já configurado em `app/providers.tsx`.
- Use somente endpoints existentes; não invente rotas, payloads ou permissões.
- O legado ainda usa `fetch` diretamente em `js/*.js`: preserve os contratos
  ao corrigir esses fluxos; não copie esse padrão para novos componentes React.
- Diferencie carregamento, erro e sucesso sem resultados. Reutilize
  `StateMessage` no React; ofereça nova tentativa quando aplicável.
- Não apresente falha como lista vazia ou contagem zero. O dashboard usa
  `—` enquanto não há sucesso na consulta.

## Formulários e acessibilidade

- Confira campos, limites e bloqueios no fluxo existente e no backend.
  Preserve dados preenchidos em falhas e impeça submissões duplicadas.
- Associe erros aos campos com `aria-describedby` e `aria-invalid`;
  apresente também uma mensagem geral acessível.
- React Hook Form, Zod e resolvers estão instalados, mas isso não implica
  um padrão de formulário já implementado. Não imponha migração do legado.
- Use labels associados, títulos hierárquicos, botões para ações e links
  para navegação; preserve navegação por teclado e foco visível.
- Use `role="status"` para progresso/vazio e `role="alert"` para erros,
  como em `StateMessage`. Ícones decorativos usam `aria-hidden`.
- Não comunique status apenas por cor; preserve texto, contraste e
  `aria-expanded`/`aria-controls` nos controles expansíveis.

## Nunca fazer

- Incluir dados reais de estudantes em código, exemplos, fixtures ou testes.
- Expor dados de estudantes ou laudos em logs; colocar conteúdo de laudos
  em URLs, localStorage, respostas de erro ou mensagens de diagnóstico.
- Incluir credenciais ou valores reais de `.env`, nem versionar esse arquivo.
- Simular capacidades do backend, sucesso de operações ou autenticação.
  Não reintroduza login, mocks ou MSW sem combinar com o grupo.
- Anexar ou remover laudos sem autorização explícita do usuário.

## Checklist de saída

- [ ] Código alterado corresponde à entrada e aos imports usados pelo build.
- [ ] Componentes/utilitários compartilhados foram reutilizados quando cabível.
- [ ] Contratos reais, links legados e bloqueios de edição foram preservados.
- [ ] Carregamento, vazio, erro, nova tentativa e submissão foram verificados.
- [ ] Labels, erros por campo, teclado e foco foram conferidos.
- [ ] Nenhum segredo, dado real, laudo ou artefato de build foi incluído.
- [ ] Executados `npm run lint` e `npm run build` em `apps/frontend`.
- [ ] Se houve mudança no legado, caminhos `.html` e inclusão no build conferidos.
- [ ] Resumo informa verificações executadas, falhas e limitações; o package.json
  atual não define comando de testes automatizados de frontend.
