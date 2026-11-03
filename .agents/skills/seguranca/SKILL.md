---
name: seguranca
description: Use sempre que a tarefa envolver credenciais, arquivos .env, laudos, planos de ação, dados de estudantes, banco de dados, logs ou commits.
---

# Segurança e dados sensíveis

## Nunca versionar
- Senhas, tokens, chaves e valores reais de `.env`. Use só arquivos `*.example`.
- Laudos, planos de ação e qualquer dado real de estudante, inclusive em testes, seeds, fixtures e exemplos. Use dados fictícios.
- Artefatos de build e arquivos locais de ambiente.

## Laudos
- Remover ou anexar laudo exige autorização explícita do usuário.
- Nunca exiba o conteúdo de um laudo em logs, respostas ou mensagens de erro.
- Valide tipo e tamanho do arquivo no backend e não confie no nome enviado pelo cliente.
- O acesso a laudos deve passar pelo fluxo controller → serviço → repositório, sem atalhos.

## Banco de dados
- Não execute migrações, SQL destrutivo ou operações no banco compartilhado sem autorização.
- Use consultas parametrizadas e nunca concatene dados recebidos em SQL.
- Testes de integração usam só o banco `clai_test`.

## Ao encontrar um segredo ou dado real
- Pare, não copie o valor para respostas, commits ou logs, e avise o usuário.
- Se já foi commitado, a credencial deve ser considerada exposta e trocada; apagar o commit não basta.
