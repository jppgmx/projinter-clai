# projinter-clai
Projeto Interdisciplinar da CLAI-CZ

A infraestrutura de `Nova Estrutura/projinter-clai-development` foi incorporada ao projeto desta pasta. O cadastro de estudantes permanece em `apps/backend` e o formulário HTML sem CSS em `apps/frontend`.

## Execução direta (Java 21)

```powershell
cd apps/backend
.\mvnw.cmd spring-boot:run
```

Acesse <http://localhost:4040/cadastro-estudante.html>. As credenciais locais do Supabase continuam em `apps/backend/application-local.properties`, ignorado pelo Git. Elas são carregadas ao iniciar pela raiz ou por `apps/backend`; variáveis de ambiente podem substituí-las.

## Docker de desenvolvimento

Na raiz do projeto:

```powershell
Copy-Item .env.example .env
docker compose -f docker-compose-dev.yml up -d
```

Se já existir `.env`, preserve seus valores. Ajuste as credenciais antes de iniciar. Este Compose usa o PostgreSQL local do container, com dados em `docker-data/postgres-dev`, independentemente das credenciais do Supabase.

O exemplo de `.env` usa `SPRING_JPA_HIBERNATE_DDL_AUTO=validate`: o banco precisa ter o schema fornecido antes de iniciar o backend, incluindo `curso` e `estudante`, e ao menos um curso para preencher o formulário. Para preparar um banco novo, pode-se iniciar primeiro apenas o serviço `postgres`. O schema completo fornecido contém comandos DROP; não deve ser reaplicado sobre um banco com dados que precisam ser preservados. Nenhum SQL é executado automaticamente por esta adaptação.

Acesse <http://localhost/cadastro-estudante.html> pelo Nginx ou <http://localhost:8080/cadastro-estudante.html> diretamente. O backend usa a porta 8080 dentro dos containers. O formulário é montado ao lado do backend e incluído nos recursos pelo Maven; reinicie o backend após alterar seus arquivos.

## Docker de produção

Com `.env` preenchido e o schema já preparado no PostgreSQL de produção:

```powershell
docker compose -f docker-compose-prod.yml up -d --build
```

Acesse <http://localhost/cadastro-estudante.html>. O build usa `apps` como contexto para incluir backend e formulário no JAR. Credenciais locais e saídas de compilação ficam fora do contexto pela configuração `apps/.dockerignore`. Os dados de produção ficam em `docker-data/postgres-prod`, separados dos de desenvolvimento.

Os ambientes Docker de desenvolvimento e produção publicam a mesma porta 80 e devem ser iniciados separadamente. Pare o ambiente em uso antes de iniciar o outro.

Detalhes da API e do cadastro: [apps/backend/CADASTRO-ESTUDANTE.md](apps/backend/CADASTRO-ESTUDANTE.md).
