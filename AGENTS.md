# AGENTS.md

Este arquivo orienta agentes que trabalham neste repositório. As instruções valem para todo o projeto, salvo quando um `AGENTS.md` mais específico existir em um subdiretório, como `apps/frontend/AGENTS.md`.

## Visão geral

O projeto CLAI é um monorepo com:

- `apps/backend`: API Spring Boot 4 em Java 21, Maven e PostgreSQL/Supabase.
- `apps/frontend`: aplicação React 19, TypeScript, Vite e Tailwind CSS.
- páginas legadas em HTML/JavaScript dentro de `apps/frontend`, mantidas durante a migração incremental para React.
- `gateway`: configuração Nginx.
- `docs`: decisões e documentação de arquitetura.

Antes de alterar a arquitetura ou os contratos existentes, leia `README.md` e `docs/arquitetura.md`.

## Princípios de trabalho

- Preserve alterações preexistentes no worktree e limite a mudança ao escopo solicitado.
- Não adicione funcionalidades simuladas, dados fictícios ou autenticação apenas visual. A interface deve refletir somente capacidades reais do backend.
- Não exponha, copie nem versione credenciais. Use apenas os arquivos `*.example` como referência.
- Não versione laudos, planos de ação nem dados reais de estudantes, nem em exemplos, seeds, fixtures ou testes. Use dados fictícios.
- Remover ou anexar laudo exige autorização explícita do usuário. Nunca exiba o conteúdo de laudos em logs, respostas ou mensagens de erro.
- Não execute migrações, SQL destrutivo ou operações contra o banco compartilhado sem autorização explícita.
- Mantenha compatibilidade com o schema PostgreSQL existente; não crie tabelas ou colunas implicitamente.
- Prefira mudanças pequenas e coerentes com a arquitetura atual a abstrações sem caso de uso concreto.
- Atualize a documentação quando uma decisão arquitetural ou instrução de execução mudar.

## Backend

O código Java está em `apps/backend/src/main/java/br/edu/ifpb/clai/backend` e segue arquitetura em camadas:

- `controller`: rotas HTTP, validação de entrada e respostas.
- `servico`: regras de negócio e limites transacionais.
- `repositorio`: persistência JPA e consultas JDBC parametrizadas.
- `entidade`: modelo persistido, sem dependência de DTO ou HTTP.
- `dto`: contratos de entrada e saída.
- `exception`: exceções de negócio e tradução para HTTP.

Respeite o fluxo controller → serviço → repositório. Controllers não devem acessar repositórios diretamente, e serviços não devem decidir códigos HTTP. Use consultas parametrizadas; nunca concatene dados recebidos em SQL.

Comandos principais, executados em `apps/backend`:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
./mvnw test
./mvnw package
```

O backend local usa Java 21 e, por padrão, a porta `8080`. Testes de integração dependem de um PostgreSQL de teste configurado explicitamente, com o banco `clai_test`; não reutilize automaticamente a conexão da aplicação.

## Frontend

O frontend React está em `apps/frontend/src`. Preserve a organização existente por `app`, `features` e `shared`. Reutilize componentes e utilitários compartilhados antes de duplicar implementação.

A migração é incremental. Os fluxos ainda implementados em páginas legadas continuam válidos. A fonte dessas páginas é `apps/frontend/html/` (HTML), `apps/frontend/js/` (scripts) e `apps/frontend/css/` (estilos), usados pelo `vite.config.ts` e pelo `apps/backend/pom.xml`. Há cópias antigas de alguns arquivos na raiz de `apps/frontend` que não são usadas pelo build nem pelo empacotamento: não as edite nem as apague sem combinar com o grupo. Não duplique suas regras em React sem que a tarefa peça a migração completa do fluxo.

Comandos principais, executados em `apps/frontend`:

```bash
npm ci
npm run dev
npm run lint
npm run build
```

O Vite usa a porta `4040` e encaminha `/api` para o backend local na porta `8080`. Use `apps/frontend/.env.example` como modelo; não versione `apps/frontend/.env`.

Atenção: o Maven empacota `frontend/html`, `frontend/js`, `frontend/css` e `frontend/dist` (veja `apps/backend/pom.xml`), mas o `dist` do React só entra no pacote se `npm run build` for executado antes. Antes de alterar o Dockerfile, o processo de build ou o gateway, confirme o fluxo em `docs/arquitetura.md`.

## Git e colaboração

- Parta sempre de `development` atualizado e não faça commit direto nele.
- Nomeie branches como `<tipo>/task-<numero-da-issue>-<descricao-curta>`. O detalhamento está na skill `git-fluxo`.
- Não altere finais de linha de arquivos existentes. Alguns arquivos, como `apps/backend/mvnw.cmd`, são versionados com CRLF; converter o formato gera diffs que afetam todo o grupo. Combine a mudança com o grupo antes.
- Revise `git status` e `git diff` antes de cada commit, e confira que nenhum arquivo sensível entrou no stage.

## Skills do grupo

As skills ficam em `.agents/skills/`, cada uma em uma pasta com um `SKILL.md`. Antes de tarefas de Git, segurança ou dados sensíveis, leia a skill correspondente (`git-fluxo` ou `seguranca`) e siga suas instruções.

## Validação

Execute as verificações proporcionais à alteração:

- backend: `./mvnw test` e, para mudanças de empacotamento, `./mvnw package`;
- frontend: `npm run lint` e `npm run build`;
- alterações que cruzam frontend e backend: valide também os contratos HTTP e os estados de erro e vazio;
- páginas legadas: confirme que continuam incluídas no build e que seus caminhos `.html` permanecem acessíveis.

Se uma verificação não puder ser executada por falta de credenciais, banco ou dependências, registre isso claramente no resumo final. Não afirme que testes passaram sem tê-los executado.

## Critérios para concluir uma tarefa

- A mudança solicitada está implementada sem alterar comportamento fora do escopo.
- Não foram incluídos segredos, dados reais de estudantes, laudos, artefatos de build ou arquivos locais de ambiente.
- As verificações relevantes foram executadas, ou a limitação foi documentada.
- Contratos de API e compatibilidade com páginas legadas foram preservados quando não faziam parte da mudança.
- A documentação foi atualizada quando necessário.
