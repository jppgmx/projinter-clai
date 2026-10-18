# Registro do estudante — T05 e T06 (US02)

Implementação limitada às tarefas T05 e T06 da Sprint 1: cadastro no backend e formulário integrado. US05 e US06 são outras histórias (estudo de caso e laudo) e não fazem parte desta entrega. O arquivo de Backlog mencionado não estava disponível na pasta.

## Executar

Pré-requisito: JDK 21. O projeto mantém o Spring Boot 4.1.1 já configurado, sem Lombok.

No PowerShell, a partir da raiz do projeto:

```powershell
cd apps/backend
.\mvnw.cmd spring-boot:run
```

Acesse <http://localhost:4040/cadastro-estudante.html>.

O formulário está em `apps/frontend` e é incluído nos recursos estáticos pelo Maven. Isso permite usar a API na mesma origem, sem iniciar framework frontend, definir rotas gerais ou alterar CORS.

A página contém somente HTML sem CSS e o JavaScript necessário para consultar cursos e enviar o cadastro. A implementação visual fica a cargo do integrante responsável pelo frontend.

## Banco existente

As três propriedades de conexão de `Referencia/Separar/src/main/resources/application.properties` foram copiadas para `apps/backend/application-local.properties`, ignorado pelo Git. Esse arquivo é carregado tanto ao executar pela raiz do projeto quanto por `apps/backend`. As variáveis `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD` podem substituir seus valores.

Em outro ambiente, copie `application-local.properties.example` para `application-local.properties` e preencha as credenciais. `SERVER_PORT` altera a porta, cujo padrão é 4040.

O cadastro utiliza somente as tabelas existentes `estudante` e `curso`. Por padrão, Hibernate apenas valida os mapeamentos; não cria nem altera tabelas. `SPRING_JPA_HIBERNATE_DDL_AUTO` permite configurar esse comportamento, conforme a infraestrutura atualizada. No Supabase já preparado, mantenha `validate`: nenhum SQL adicional é necessário. Deve existir pelo menos um curso no banco para preencher o formulário.

## Estrutura e Docker

Os arquivos de Docker Compose, Dockerfile, Nginx, `.env.example` e regras de Git da nova estrutura foram incorporados à raiz deste projeto. As entidades, validações e endpoints de cadastro foram preservados.

Ao executar diretamente, a porta padrão continua 4040. Nos dois Compose, `SERVER_PORT=8080` mantém o backend compatível com o Nginx fornecido. O ambiente de desenvolvimento monta também `apps/frontend`; o build de produção inclui essa pasta no contexto para empacotar o HTML e o JavaScript no JAR. Não há CSS.

Os Compose usam seu próprio PostgreSQL local com as variáveis `POSTGRES_DB`, `POSTGRES_USER` e `POSTGRES_PASSWORD` do `.env`. As variáveis `SPRING_DATASOURCE_*` do container substituem a configuração local do Supabase. Um volume de banco novo precisa receber o schema antes de iniciar com `validate`. As instruções de execução estão no README da raiz.

O SQL e a referência usam `turma` e `data_nascimento_idade` (texto, até 60 caracteres). Esses nomes prevalecem sobre os nomes diferentes apresentados no HTML de entidades. O formulário preserva esse formato e não acrescenta colunas.

## API

`GET /api/cursos` consulta os cursos existentes, ordenados por nome, para o formulário.

`POST /api/estudantes` registra o estudante:

```json
{
  "matricula": "202610001",
  "nomeEstudante": "Ana Silva",
  "curso": { "idCurso": 1 },
  "turma": "A",
  "periodoIngresso": "2026.1",
  "dataNascimentoIdade": "15/04/2005 (21 anos)",
  "telefoneEstudante": "(83) 99999-1111",
  "emailEstudante": "ana@example.org",
  "nomeResponsavel": "Maria Silva",
  "grauParentesco": "Mãe",
  "telefoneResponsavel": "(83) 99999-2222",
  "emailResponsavel": "maria@example.org"
}
```

Use um `idCurso` existente. Os campos após `dataNascimentoIdade` são opcionais. Textos são aparados e campos opcionais vazios viram `null`. Os limites de tamanho seguem o SQL e os dois e-mails são validados.

Respostas: `201` com o estudante criado, `400` para dados inválidos ou curso inexistente, `409` para matrícula duplicada/conflito de integridade. Erros de validação retornam `erro` e um mapa `campos`. A matrícula é chave primária e o cadastro sempre usa INSERT, impedindo a atualização de um estudante existente, inclusive em requisições concorrentes.

O tratamento de erros é restrito ao controller deste cadastro. Não há autenticação, edição, exclusão, consulta de estudantes, estudos de caso ou laudos.

## Verificação pelo desenvolvedor

A compilação do backend passou com Java 21. Os testes ficaram para execução pelo solicitante, conforme pedido. Foram incluídos testes de cadastro, validação, campos opcionais, cursos, duplicidade (inclusive concorrente) e disponibilização do formulário.

```powershell
.\mvnw.cmd test
```

Os testes usam o perfil `test` e um banco H2 em memória separado por contexto, com apenas as tabelas do cadastro; não utilizam o Supabase.
