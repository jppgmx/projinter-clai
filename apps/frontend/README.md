# CLAI — Frontend

React 19, TypeScript, Vite e Tailwind CSS, com a estilização existente preservada.

```powershell
npm install
npm run dev
```

Acesso: http://localhost:4040/. O backend deve ser iniciado separadamente com Java 21 na porta 8080. Não há login nem API simulada. O painel usa GET /api/estudantes e GET /api/estudos-caso; falhas de conexão são exibidas sem números fictícios.

O menu contém apenas consulta/cadastro de estudantes e abertura/consulta/atualização de estudos de caso. Os formulários continuam nas páginas HTML existentes, acessadas por navegação normal, sem duplicar as regras em React. As rotas /alunos e /planos-estudos/estudos redirecionam para essas páginas; as demais telas provisórias foram removidas.

As tarefas de arquivamento/trancamento implementadas na outra pasta T11 não fazem parte do backend desta cópia. Agenda, planos de ação, avaliações, apoiadores, administração e autenticação não são apresentados como funcionalidades disponíveis.

## Atualização T05 a T13 e organização do frontend

Consulte `docs/auditoria-t05-t13.md` para o resultado tarefa a tarefa. Foram acrescentadas T08 (editar estudante), T10 (laudos), T12 e T13, preservando os fluxos anteriores. O banco continua sendo o Supabase existente; nenhuma migração ou SQL remoto foi executado. As funcionalidades novas dependem das tabelas/colunas já descritas na referência.

Frontend organizado em `apps/frontend/css`, `apps/frontend/html` e `apps/frontend/js`. O código React/TypeScript permanece com seus tipos e componentes em `js/src`; a stack não foi trocada. O `index.html` permanece na raiz do frontend conforme o Vite exige. As URLs antigas `.html` continuam acessíveis por compatibilidade, e as páginas também ficam em `/html/`. Nenhum segredo foi levado ao frontend.

Execute o backend com Java 21 na porta 8080 e `npm run dev` em apps/frontend para acessar http://localhost:4040/. O menu agora inclui laudos/documentos e arquivamento/trancamento. Para editar estudante, use a consulta ou os detalhes; a matrícula é somente leitura. Na lista de estudos é possível abrir edição, documentos ou situação. Motivo e justificativa registrados aparecem ao selecionar o estudo na situação.

Para empacotar: execute `npm run build` no frontend antes de compilar/empacotar o Maven no backend. O build gera as páginas e aliases e o Maven copia dist. Reinicie backend/Vite após a atualização e atualize o navegador com Ctrl+F5. Não inicie Docker automaticamente; a receita não foi executada nesta entrega.
