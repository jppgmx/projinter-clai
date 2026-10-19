# Auditoria T05 a T13

Análise da cópia `projinter-clai-feature-task-22-migrar-frontend-react`, comparada à Sprint 1 e ao projeto `Referencia/Separar`. O backlog completo não foi localizado; a avaliação se limita às tarefas e regras fornecidas. Compilar não comprova execução no Supabase.

| Tarefa | Antes | Entrega nesta atualização |
| --- | --- | --- |
| T05 / US02 — Registro backend | Implementada | Preservada: POST /api/estudantes, validações e mapeamento |
| T06 / US02 — Formulário de registro | Implementada | Preservado e organizado em html/js/css |
| T07 / US03 — Consulta, dados e histórico | Implementada | Preservada; links para edição e documentos |
| T08 / US04 — Editar estudante | Ausente | PUT /api/estudantes/{matricula}, formulário preenchido e validações; matrícula e histórico preservados |
| T09 / US05 — Abrir estudo | Implementada | Preservada; somente referências reais |
| T10 / US06 — Laudos | Ausente | Envio multipart, listagem, visualização e download na tabela documento_medico existente |
| T11 / US07 — Atualizar estudo | Implementada | Preservada e integrada às ações novas |
| T12 / US08 — Arquivamento com motivo | Ausente | PATCH arquivar, situação, motivo/data e desarquivar já solicitado anteriormente |
| T13 / US09 — Trancamento com justificativa | Ausente | PATCH trancar, justificativa/data; impede repetição, arquivado ou finalizado |

## Persistência e limites

Não foram criados bancos, tabelas, migrações nem dados de demonstração. A T10 depende da tabela `documento_medico` da migração fornecida, com conteudo BYTEA. T12/T13 dependem das seis colunas de arquivamento/trancamento da V2 da referência. A aplicação informa indisponibilidade se a estrutura faltar. A existência desses campos no Supabase não foi presumida nem verificada por SQL remoto.

Documentos aceitam PDF/JPG/PNG/DOC/DOCX com verificação de assinatura, até 10 MB por arquivo e 10 arquivos por envio. DOCX precisa conter estrutura Word. Lista não carrega conteúdo binário; download sempre verifica o vínculo do documento com o estudo. Estudos bloqueados não recebem novos documentos. Não foram acrescentados exclusão de documentos, autenticação, criação de estudos em cascata, destrancamento ou finalização.

As regras de T12/T13 seguem o controller concreto da referência; ele permite arquivar finalizado/trancado, mas não trancar arquivado. O resumo da US09 na Sprint tem redação diferente sobre estudos arquivados. Não há desarquivamento implícito. Desarquivar limpa a data e o motivo atuais, preservando trancamento e fechamento.

## Verificação

Foram preparados testes unitários para edição, validação de documentos e transições de situação; apenas compilados. Build e lint do frontend e compilação Java 21 são verificados separadamente. Nenhum teste de integração ou SQL no Supabase foi executado.

O Dockerfile foi ajustado para gerar o Vite antes do Maven, e o Nginx permite o tamanho de requisição multipart previsto. Não foram iniciados containers nem executado build Docker. As rotas React já existentes também encaminham para o index quando servidas pelo Spring; as rotas REST permanecem separadas. Os aliases HTML e os assets produzidos foram conferidos sem consultar o banco.
