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

O cadastro utiliza somente as tabelas existentes `estudante` e `curso`. Por padrão, Hibernate apenas valida os mapeamentos; não cria nem altera tabelas. No Supabase já preparado, mantenha `validate`: nenhum SQL adicional é necessário. Deve existir pelo menos um curso no banco para preencher o formulário.

## Estrutura e Docker

As configurações foram separadas em `application-dev.properties` e `application-prod.properties`, mantendo `application.properties` com valores comuns. O perfil padrão é `dev`.

Ao executar diretamente, a porta padrão continua 4040. Os Compose definem `dev` ou `prod`, usam arquivos `.env` separados e executam backend e Nginx. A conexão permanece no Supabase existente: não há serviço PostgreSQL local, script de inicialização nem banco criado pelo Compose. Em desenvolvimento a configuração privada é importada; produção recebe as credenciais pelo ambiente. Essa escolha de banco externo diverge da exigência do professor de um banco no Compose, conforme documentado no README.

O frontend é incluído no JAR pelo Maven. Não há CSS. O cadastro e as consultas mantêm seus contratos HTTP. O controller consulta cursos por `CursoService`, seguindo a arquitetura em camadas.

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

O tratamento de erros é restrito aos controllers de cadastro e consulta de estudantes. Não há autenticação, edição, exclusão, abertura/alteração de estudos de caso ou laudos.

## Consulta de estudantes — T07

A T07 acrescenta `GET /api/estudantes` (busca opcional por nome/matrícula em `?busca=`) e `GET /api/estudantes/{matricula}` (dados cadastrais e histórico em `estudosDeCaso`). Acesse `/estudantes.html` e selecione **Ver dados e histórico**. As páginas são HTML sem CSS.

Os DTOs retornam os dados dentro da transação de leitura, sem depender da serialização de relações JPA lazy. O histórico usa uma consulta parametrizada às colunas existentes de `estudo_de_caso`, sem introduzir uma entidade de escrita ou executar migrações. Inclui todos os estudos vinculados à matrícula, inclusive arquivados, ordenados pelo identificador decrescente. Um estudante sem estudos retorna lista vazia; matrícula inexistente retorna 404.

As classes antes reunidas em `estudante` agora estão separadas em `controller`, `dto`, `entidade`, `exception`, `repositorio` e `servico`. O contrato do cadastro foi preservado. Depois de atualizar, reconstrua o projeto na IDE para eliminar classes compiladas dos pacotes antigos.

## Verificação pelo desenvolvedor

A compilação do backend passou com Java 21. Os testes ficaram para execução pelo solicitante, conforme pedido. Foram incluídos testes de cadastro, validação, campos opcionais, cursos, duplicidade (inclusive concorrente) e disponibilização do formulário.

```powershell
.\mvnw.cmd test
```

O H2 foi removido. Os testes de integração só são habilitados ao definir `CLAI_TEST_DATABASE_URL` explicitamente para um PostgreSQL de teste já existente chamado `clai_test`, com usuário e senha exclusivos. Não usam a conexão do Supabase da aplicação. Sem essa configuração, são ignorados. Nenhum teste foi executado nesta adequação. Consulte o README para detalhes.
