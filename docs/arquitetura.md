# Decisões de arquitetura

## Arquitetura em camadas e pacotes por camada

A aplicação é um monolito Spring Boot organizado em camadas. Essa opção combina com o escopo atual de cadastro e consulta, permite que cada integrante encontre a responsabilidade de uma classe e evita estruturas de abstração sem um caso de uso concreto.

Os pacotes ficam dentro de `br.edu.ifpb.clai.backend`:

- `controller`: rotas HTTP, validação de entrada e respostas; chama serviços.
- `servico`: regras de cadastro/consulta e limites de transação; chama repositórios.
- `repositorio`: acesso ao PostgreSQL, por JPA ou consulta JDBC parametrizada.
- `entidade`: modelo persistido, com anotações JPA, sem dependência de DTO ou HTTP.
- `dto`: contratos de entrada e projeções de saída; converte dados do modelo para as consultas.
- `exception`: exceções de negócio e sua tradução para HTTP em advices limitados aos respectivos controllers.

O controller de estudantes não acessa mais o repositório de cursos diretamente: a consulta passa por `CursoService`. As regras de negócio lançam exceções próprias sem decidir códigos HTTP. A entidade Estudante recebe os valores já validados e não conhece o DTO de registro.

A resposta de criação do estudante mantém o contrato existente. As consultas usam DTOs produzidos dentro das transações de leitura para não depender da serialização de relações lazy.

## Alternativas consideradas

- Organização por funcionalidade: válida, mas foi substituída pela separação por responsabilidade solicitada pela equipe. Manter os dois padrões simultaneamente aumentaria a dificuldade de localização das classes.
- Hexagonal/Clean Architecture: trariam portas, adaptadores e mapeamentos adicionais. Não foram adotadas porque ainda não há múltiplos canais de entrada ou implementações alternativas que justifiquem esse custo.
- MVC com templates: não foi escolhido porque as páginas atuais são HTML/JavaScript que consomem a API; não são renderizadas por Thymeleaf/JTE.
- Frontend sem framework (HTML/JavaScript puro): foi a escolha inicial, para preservar as páginas já entregues. Deixou de ser a única abordagem com a adoção do React (ver a seção "Frontend React").

## Persistência e limites do escopo

O PostgreSQL existente do Supabase continua sendo a base da aplicação. As entidades mapeiam `estudante` e `curso` conforme o SQL fornecido. O histórico mantém uma projeção de leitura de `estudo_de_caso`. A US05 acrescenta uma entidade `EstudoCaso` limitada aos campos da abertura e um mapeamento de leitura de `Profissional` para selecionar o responsável existente. A gravação inicial usa `persist` dentro de uma transação; não há atualização ou exclusão de estudos, estudantes ou profissionais nessa operação.

Os campos clínicos/documentais que são `NOT NULL` no schema são solicitados explicitamente na abertura. Assim como no projeto de referência, a origem `Outro` exige descrição e respostas `Sim` exigem detalhes. Campos posteriores de acompanhamento e assinaturas continuam sem preenchimento nesta US. A situação inicial `EM_ANDAMENTO` e sua descrição seguem a referência e pertencem somente à resposta de criação: não representam uma coluna nova nem implementam as mudanças de situação de outras histórias. O advice de abertura é restrito ao seu controller; os handlers e contratos das outras US foram preservados. O controller da referência reúne várias US, mas somente a parte de criação foi adaptada.

### T11 / US07 — Edição isolada

A atualização possui controller, serviço, DTOs, repositório JDBC e advice próprios. Os contratos de cadastro, consulta de estudantes e abertura foram preservados. O GET por identificador fornece os valores para uma página HTML/JavaScript independente; o PUT aceita alterações em campos textuais e referências profissionais definidos no schema existente. Não altera a matrícula vinculada, identificadores, data de fechamento nem flags de situação.

`CampoEstudoCaso` define a lista fechada de nomes JSON, colunas e limites de tamanho. Nomes de colunas SQL vêm exclusivamente dessa lista, e os valores usam parâmetros JDBC. O serviço combina os campos recebidos com o registro existente, valida o resultado com as regras obrigatórias da abertura e verifica os profissionais. Campos omitidos mantêm seu valor; valores vazios viram nulos apenas quando a validação permite. Isso evita apagar dados de acompanhamento não enviados pelo cliente.

O serviço coordena uma transação com `SELECT ... FOR UPDATE OF ec`, verifica se o estudo permite edição e salva os valores. A cláusula de atualização também exige ausência de fechamento, arquivamento e trancamento. As flags opcionais são lidas com `to_jsonb(ec)` para manter compatibilidade com o schema fornecido sem criar colunas ou migrações. Estados de outras US somente bloqueiam a edição; nenhuma transição foi implementada.

A página envia somente as alterações e apresenta erros por campo. Se houver conflito ou resultado incerto de rede, exige carregar novamente antes de reenviar. Não há controle de versão para detectar alterações entre a carga da página e o salvamento; o bloqueio transacional serializa as gravações, mas duas pessoas alterando o mesmo campo podem sobrescrever valores. Controle de versão exigiria um contrato adicional e não foi introduzido nesta tarefa.

O Hibernate apenas valida o schema e a inicialização SQL da aplicação fica desativada. Não são criados bancos, tabelas, cursos, migrações ou cópias dos registros existentes. As credenciais particulares ficam fora do Git e não são copiadas para a imagem de produção.

A escolha de Supabase externo foi mantida por instrução expressa do responsável. Ela difere da exigência do professor de executar o banco como serviço do Compose. Esta limitação deve ser discutida pela equipe; não é considerada conformidade integral com essa parte do guia.

## Ambientes

`application.properties` reúne o que é comum. `dev` importa a configuração local privada e usa a porta 8080 por padrão; o frontend Vite fica na 4040 e encaminha `/api` para o backend. `prod` recebe a conexão por ambiente, sem importar configuração local. Cada Compose define seu perfil e usa seu arquivo privado `.env.dev` ou `.env.prod`.

Não há banco embarcado. Os testes de integração são opcionais, exigem configuração explícita de um PostgreSQL de teste já existente e isolam suas tabelas em um schema próprio. A conexão da aplicação nunca é adotada automaticamente pelos testes.

## Referências

- [Instruções do projeto CLAI](https://gist.github.com/ReneDouglas/c8682724c8d5aa75c60f5ba2f7583e12).
- [Perfis do Spring Boot](https://docs.spring.io/spring-boot/reference/features/profiles.html).
- [Propriedades dinâmicas de teste do Spring](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/ctx-management/dynamic-property-sources.html).

## Listagem de estudos para seleção na edição

A consulta geral segue a cadeia controller → serviço com transação de leitura → repositório JDBC. Um DTO de resumo retorna apenas identificador, matrícula, nome do estudante, período e situação, sem carregar os 57 campos clínicos de cada estudo. O parâmetro opcional `matricula` usa igualdade parametrizada; ausente ou vazio retorna todos os registros, por identificador decrescente. O valor é mantido como texto para preservar zeros à esquerda. A listagem não possui paginação nesta entrega.

A mesma página de edição contém a lista inicial e o filtro, em HTML/JavaScript sem CSS. Os links usam a carga existente por identificador; situações que bloqueiam edição continuam somente para leitura. Arquivamento e trancamento são consultados pelas flags opcionais existentes, sem criação de colunas ou transições de estado. Nenhuma configuração de banco, cadastro de estudantes, abertura de estudo ou regra de atualização foi alterada para acrescentar a consulta.

## Frontend React

O frontend passou a ser uma aplicação desacoplada em `apps/frontend`, com React 19, TypeScript, Vite e Tailwind CSS (task 22). Ela consome a API do backend por `/api`; em desenvolvimento, o Vite (porta 4040) encaminha essas requisições ao Spring Boot (porta 8080).

Decisões desta etapa:

- A migração é incremental: o painel inicial e o layout base (menu lateral, rotas) são React. Os formulários de cadastro, consulta, abertura e edição continuam nas páginas HTML/JavaScript existentes, acessadas por navegação normal, para não duplicar regras de negócio antes de migrá-las.
- As páginas HTML antigas também são geradas pelo build do Vite, mantendo seus caminhos `.html`.
- O painel mostra apenas dados reais de `/api/estudantes` e `/api/estudos-caso`, com estados de erro e de lista vazia. Não há login, autenticação nem dados simulados, pois o backend ainda não implementa autenticação.
- Alternativas descartadas: manter somente HTML/JavaScript (dificulta reuso de layout e componentes) e migrar todas as telas de uma vez (maior risco de regressão nas funcionalidades já validadas).

Pendência: o empacotamento de produção (`pom.xml` e `Dockerfile`) ainda copia a pasta `frontend` inteira; deve passar a usar o resultado de `npm run build` (`dist/`).

Instruções de execução local estão no `README.md`.
