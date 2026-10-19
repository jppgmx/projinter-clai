# CLAI — Frontend

React 19, TypeScript, Vite e Tailwind CSS, com a estilização existente preservada.

```powershell
npm install
npm run dev
```

Acesso: http://localhost:4040/. O backend deve ser iniciado separadamente com Java 21 na porta 8080. Não há login nem API simulada. O painel usa GET /api/estudantes e GET /api/estudos-caso; falhas de conexão são exibidas sem números fictícios.

O menu contém apenas consulta/cadastro de estudantes e abertura/consulta/atualização de estudos de caso. Os formulários continuam nas páginas HTML existentes, acessadas por navegação normal, sem duplicar as regras em React. As rotas /alunos e /planos-estudos/estudos redirecionam para essas páginas; as demais telas provisórias foram removidas.

As tarefas de arquivamento/trancamento implementadas na outra pasta T11 não fazem parte do backend desta cópia. Agenda, planos de ação, avaliações, apoiadores, administração e autenticação não são apresentados como funcionalidades disponíveis.
