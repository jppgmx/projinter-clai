---
name: git-fluxo
description: Use ao criar branches, fazer commits, abrir pull requests ou resolver problemas de Git no projeto CLAI.
---

# Fluxo Git

## Antes de começar
- Rode `git status`. Se houver alterações, não as descarte sem confirmar com o usuário. Prefira `git stash push -u -m "descricao"`.
- Atualize a base: `git switch development && git pull origin development`.

## Branches
- Formato: `<tipo>/task-<numero>-<descricao-curta>`, em minúsculas e com hífen.
- Tipos: `feature`, `fix`, `docs`.
- Use o número da issue do GitHub, que aparece na URL depois de `/issues/`. Se o grupo usar outra convenção, siga a do professor.
- Nunca faça commit direto em `development`.

## Commits
- Um commit por mudança lógica, no formato `tipo(escopo): descricao no imperativo`, por exemplo `docs: revisa AGENTS.md`.
- Antes de commitar, rode `git diff --cached` e confira que não entrou segredo, laudo ou dado real de estudante.
- Não altere finais de linha de arquivos existentes (ex.: `apps/backend/mvnw.cmd` usa CRLF).

## Pull request
- Descreva o que mudou, por que mudou e como testar.
- Vincule a issue com `Closes #<numero>`.
- O PR precisa ser revisado e testado por outro membro antes do merge.

## Nunca sem autorização explícita
- `git reset --hard`, `git push --force`, `git clean -fd`, `git stash drop`.
