# CLAI — Campus Cajazeiras

## Sobre

Sistema de apoio à Coordenação Local de Acessibilidade e Inclusão do IFPB Campus Cajazeiras. A implementação atual permite registrar estudantes, consultar seus dados cadastrais e visualizar o histórico de estudos de caso já existente no banco. O atendimento é destinado aos profissionais que acompanham os estudantes.

O escopo funcional entregue é T05/T06 e T07. O formulário e as consultas usam HTML sem CSS. Não foram implementadas autenticação, edição de cadastro, abertura/alteração de estudos, arquivamento, trancamento ou laudos.

## Stack utilizada

| Componente | Tecnologia |
| --- | --- |
| Backend | Java 21, Spring Boot 4.1.1, Spring MVC, Spring Data JPA e Jakarta Validation |
| Persistência | PostgreSQL existente no Supabase; JPA e JDBC |
| Frontend | HTML5 e JavaScript, servidos pela aplicação Spring Boot, sem CSS |
| Build | Maven Wrapper 3.10.0; sem Lombok |
| Containers | Docker e Docker Compose v2; imagens de Java 21 |
| Gateway | Nginx 1.27 |
| Testes de integração opcionais | JUnit 5, MockMvc e PostgreSQL de teste explicitamente configurado; sem H2 |

## Estrutura e arquitetura

O repositório reúne backend, frontend, gateway, configuração e documentação em um monorepo:

```text
apps/
  backend/       Aplicação Spring Boot e Dockerfile
  frontend/      Fontes HTML/JS empacotadas no JAR do backend
  .dockerignore
gateway/        Configuração do Nginx
docs/           Decisões de arquitetura
docker-compose-dev.yml
docker-compose-prod.yml
.env.dev.example
.env.prod.example
.env.test.example
AGENTS.md
```

Foi escolhida uma arquitetura em camadas, com pacotes por responsabilidade: `controller`, `servico`, `repositorio`, `entidade`, `dto` e `exception`. Controllers chamam serviços; serviços coordenam regras e transações; repositórios acessam o banco. Os detalhes e as alternativas descartadas estão em [docs/arquitetura.md](docs/arquitetura.md).

O frontend não é um serviço independente: suas fontes ficam em `apps/frontend` e o Maven as inclui nos recursos estáticos do backend. O Nginx encaminha tanto as páginas quanto `/api` à aplicação.

## Guia de instalação

### Pré-requisitos

- JDK 21 para executar fora do Docker. O Maven Wrapper já está no projeto.
- Acesso à conexão PostgreSQL existente do Supabase e ao schema já fornecido.
- Docker com suporte a containers Linux e Compose v2, se for usar os ambientes em container. A instalação do Docker foi cancelada neste computador; a execução dos containers ainda precisa ser validada.

### Executar pelo IntelliJ ou Maven

O perfil padrão é `dev`. O arquivo privado `apps/backend/application-local.properties` preserva a conexão do Supabase e é carregado ao iniciar pela raiz do projeto ou por `apps/backend`. Em outra máquina, copie `application-local.properties.example` para `application-local.properties` e preencha a conexão existente.

```powershell
cd apps/backend
.\mvnw.cmd spring-boot:run
```

Acesse:

- Cadastro: <http://localhost:4040/cadastro-estudante.html>.
- Consulta: <http://localhost:4040/estudantes.html>.

O Hibernate está configurado com `validate` e a inicialização SQL está desativada. A aplicação não cria banco, tabelas ou cursos. O formulário usa os cursos reais já cadastrados no Supabase.

### Docker de desenvolvimento

Na raiz do projeto, copie o modelo se ainda não houver um arquivo privado `.env.dev`:

```powershell
Copy-Item .env.dev.example .env.dev
```

A conexão continua no `application-local.properties` privado, montado com o backend. Ajuste portas e log em `.env.dev` se necessário. Depois:

```powershell
docker compose -f docker-compose-dev.yml --env-file .env.dev up -d
```

Acesse <http://localhost/cadastro-estudante.html> ou <http://localhost/estudantes.html> pelo Nginx. O backend também fica em `http://localhost:8080` por padrão. As portas podem ser alteradas em `.env.dev`.

### Docker de produção

Copie `.env.prod.example` para `.env.prod` apenas se ainda não existir. Preencha `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD` com a conexão existente do Supabase. Esse arquivo é privado e não deve ser commitado.

```powershell
docker compose -f docker-compose-prod.yml --env-file .env.prod up -d --build
```

O perfil `prod` recebe as credenciais por ambiente e não importa `application-local.properties`. O build exclui credenciais locais e saídas de compilação. O acesso ocorre pelo Nginx na porta `HTTP_PORT` (80 por padrão). Se os dois ambientes estiverem ativos, use portas HTTP diferentes.

### Configurações por ambiente

| Ambiente | Spring Boot | Variáveis do Compose |
| --- | --- | --- |
| Desenvolvimento | `application-dev.properties` | `.env.dev` |
| Produção | `application-prod.properties` | `.env.prod` |
| Testes opcionais | `src/test/resources/application-test.properties` | Variáveis `CLAI_TEST_DATABASE_*`, descritas em `.env.test.example` |

`application.properties` contém somente configurações comuns e o perfil padrão. Os Compose definem `SPRING_PROFILES_ACTIVE` de forma explícita. O Spring não carrega `.env.*` automaticamente: `--env-file` serve ao Compose. Arquivos reais `.env.*` e a configuração local ficam fora do Git; apenas os exemplos são versionados.

### Testes

Os testes não foram executados, conforme solicitado. Não há H2 nem container SQL criado pelo projeto. Os testes de integração só são habilitados quando `CLAI_TEST_DATABASE_URL` é definida explicitamente e exigem um PostgreSQL de teste já existente chamado `clai_test`, com usuário e senha próprios. Não utilize a conexão do Supabase que contém os dados da aplicação.

O suporte de teste usa um schema isolado por execução dentro dessa base de teste e não reutiliza `SPRING_DATASOURCE_*` nem `application-local.properties`. `.env.test.example` apenas documenta as variáveis; ele não é carregado automaticamente pelo Maven. Sem configuração explícita, os testes de integração são ignorados.

```powershell
# Após configurar as três variáveis CLAI_TEST_DATABASE_* para uma base de testes:
cd apps/backend
.\mvnw.cmd test
```

## API entregue

| Método e caminho | Uso |
| --- | --- |
| `POST /api/estudantes` | Registrar estudante |
| `GET /api/cursos` | Consultar os cursos existentes para o cadastro |
| `GET /api/estudantes?busca=Ana` | Listar/buscar por nome ou matrícula |
| `GET /api/estudantes/{matricula}` | Dados cadastrais e histórico de estudos de caso |

O histórico inclui todos os estudos vinculados à matrícula, inclusive arquivados, em ordem decrescente de identificador. Ele não altera os estudos. Detalhes dos campos: [apps/backend/CADASTRO-ESTUDANTE.md](apps/backend/CADASTRO-ESTUDANTE.md).

## Fluxo de desenvolvimento

Siga o [guia do professor](https://gist.github.com/ReneDouglas/c8682724c8d5aa75c60f5ba2f7583e12): branches de trabalho nascem de `development`, usam o número real da issue (`feature/task-{id}-{descricao}`, `fix/task-{id}-{descricao}` ou `docs/task-{id}-{descricao}`) e entram por PR para `development`. `main` e `development` devem ser protegidas; a `main` só recebe PR da `development`.

Use Conventional Commits, descreva o que foi feito e referencie a issue no PR. Outro integrante revisa o PR e testa a tarefa. No GitHub Projects, use as etapas Backlog, Ready, In progress, In review, To test e Done; registre responsável, estimativa, datas previstas e horas realizadas. Os labels e o parecer do teste devem seguir o guia.

O professor `ReneDouglas` deve ter acesso ao repositório e ao quadro. A pasta de trabalho atual não contém `.git`; branches, proteções, colaboradores e Kanban não foram configurados nem verificados remotamente. As instruções para agentes estão em `AGENTS.md` e devem ser versionadas sem credenciais.

## Diferença mantida em relação ao guia

Por solicitação do responsável, a aplicação usa o Supabase existente, sem criar um PostgreSQL local. Os Compose contêm somente backend e Nginx. Isso diverge da exigência das seções 2.3 e 5 do guia, que pede o banco como serviço do Compose; essa escolha precisa ser alinhada com o professor. Nenhum SQL foi executado no Supabase durante estas alterações.
