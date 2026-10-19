# projinter-clai
Projeto Interdisciplinar da CLAI-CZ

## Portas locais do frontend React

O frontend Vite está em `http://localhost:4040/`, com `strictPort` para não trocar silenciosamente de porta. O backend no perfil dev usa `http://localhost:8080`; o proxy `/api` aponta para ele. Reinicie os processos após a alteração e remova eventuais overrides antigos de SERVER_PORT na configuração de execução. Produção permanece com sua configuração anterior.

Inicie o backend com Java 21 e o frontend com `npm run dev` dentro de `apps/frontend`. Para visualizar login e dashboard simulados, `.env` mantém `VITE_USE_MOCKS=true` como no exemplo. Credenciais Supabase continuam somente no backend privado. Nenhum SQL ou teste de integração foi executado nesta configuração.

## Frontend limitado às funcionalidades implementadas

A estilização React/Tailwind foi preservada. Foram retirados login/logout simulados, MSW e seus dados, pendências/alertas fictícios, agenda, planos, avaliações, apoiadores, demandas externas e administração. O dashboard consulta somente /api/estudantes e /api/estudos-caso e usa estados de erro/vazio em vez de dados de demonstração. Não existe autenticação implementada no backend; por isso a interface não apresenta proteção de acesso fictícia.

O menu oferece os fluxos reais já existentes em HTML: registro/consulta de estudantes, dados e histórico, abertura e atualização de estudos de caso. Esses arquivos, APIs e regras de backend permanecem intactos; não foi feita uma nova migração de formulários para React. Arquivar/trancar não foi trazido da pasta T11, pois esta cópia não tem esses endpoints. A aplicação React continua na porta 4040 com proxy para o backend 8080. Não foram executados SQL nem testes de integração.

## Estilização dos fluxos existentes

As páginas de cadastro, consulta, dados/histórico do estudante, abertura e atualização dos estudos usam agora o mesmo padrão visual do início: menu preto, fundo cinza, cartões brancos e ações em índigo. `page-styles.css` e `page-layout.js` compartilham estilos, menu e rodapé, sem bloco de usuário fictício ou funcionalidades não implementadas. O item Início retorna à aplicação React na raiz.

Os arquivos, IDs, campos e scripts de negócio anteriores foram preservados. Não se duplicaram as regras de cadastro/edição em componentes novos. A inclusão dos scripts como módulos permite gerar também essas páginas no build Vite, mantendo todos os caminhos `.html`. Formulários se adaptam a uma coluna no celular, tabelas têm rolagem e o menu oferece controle por teclado. Nenhuma API ou credencial de banco foi alterada; não foram executados SQL nem testes de integração.

## Atualização T05 a T13 e organização do frontend

Consulte `docs/auditoria-t05-t13.md` para o resultado tarefa a tarefa. Foram acrescentadas T08 (editar estudante), T10 (laudos), T12 e T13, preservando os fluxos anteriores. O banco continua sendo o Supabase existente; nenhuma migração ou SQL remoto foi executado. As funcionalidades novas dependem das tabelas/colunas já descritas na referência.

Frontend organizado em `apps/frontend/css`, `apps/frontend/html` e `apps/frontend/js`. O código React/TypeScript permanece com seus tipos e componentes em `js/src`; a stack não foi trocada. O `index.html` permanece na raiz do frontend conforme o Vite exige. As URLs antigas `.html` continuam acessíveis por compatibilidade, e as páginas também ficam em `/html/`. Nenhum segredo foi levado ao frontend.

Execute o backend com Java 21 na porta 8080 e `npm run dev` em apps/frontend para acessar http://localhost:4040/. O menu agora inclui laudos/documentos e arquivamento/trancamento. Para editar estudante, use a consulta ou os detalhes; a matrícula é somente leitura. Na lista de estudos é possível abrir edição, documentos ou situação. Motivo e justificativa registrados aparecem ao selecionar o estudo na situação.

Para empacotar: execute `npm run build` no frontend antes de compilar/empacotar o Maven no backend. O build gera as páginas e aliases e o Maven copia dist. Reinicie backend/Vite após a atualização e atualize o navegador com Ctrl+F5. Não inicie Docker automaticamente; a receita não foi executada nesta entrega.
