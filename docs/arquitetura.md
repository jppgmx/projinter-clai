# Decisões de arquitetura

## Arquitetura em camadas e pacotes por camada

A aplicação é um monolito Spring Boot organizado em camadas. Essa opção combina com o escopo atual de cadastro e consulta, permite que cada integrante encontre a responsabilidade de uma classe e evita estruturas de abstração sem um caso de uso concreto.

Os pacotes ficam dentro de `br.edu.ifpb.clai.backend`:

- `controller`: rotas HTTP, validação de entrada e respostas; chama serviços.
- `servico`: regras de cadastro/consulta e limites de transação; chama repositórios.
- `repositorio`: acesso ao PostgreSQL, por JPA ou consulta JDBC parametrizada.
- `entidade`: modelo persistido, com anotações JPA, sem dependência de DTO ou HTTP.
- `dto`: contratos de entrada e projeções de saída; converte dados do modelo para as consultas.
- `exception`: exceções de negócio e sua tradução para HTTP no advice limitado aos controllers de estudantes.

O controller de estudantes não acessa mais o repositório de cursos diretamente: a consulta passa por `CursoService`. As regras de negócio lançam exceções próprias sem decidir códigos HTTP. A entidade Estudante recebe os valores já validados e não conhece o DTO de registro.

A resposta de criação do estudante mantém o contrato existente. As consultas usam DTOs produzidos dentro das transações de leitura para não depender da serialização de relações lazy.

## Alternativas consideradas

- Organização por funcionalidade: válida, mas foi substituída pela separação por responsabilidade solicitada pela equipe. Manter os dois padrões simultaneamente aumentaria a dificuldade de localização das classes.
- Hexagonal/Clean Architecture: trariam portas, adaptadores e mapeamentos adicionais. Não foram adotadas porque ainda não há múltiplos canais de entrada ou implementações alternativas que justifiquem esse custo.
- MVC com templates: não foi escolhido porque as páginas atuais são HTML/JavaScript que consomem a API; não são renderizadas por Thymeleaf/JTE.
- Frontend com framework e serviço próprio: não foi introduzido para preservar o HTML sem CSS e o trabalho do integrante responsável pela interface. Os arquivos ficam em `apps/frontend` e são empacotados pelo Maven no JAR.

## Persistência e limites do escopo

O PostgreSQL existente do Supabase continua sendo a base da aplicação. As entidades mapeiam `estudante` e `curso` conforme o SQL fornecido. O histórico mantém uma projeção de leitura de `estudo_de_caso`. A US05 acrescenta uma entidade `EstudoCaso` limitada aos campos da abertura e um mapeamento de leitura de `Profissional` para selecionar o responsável existente. A gravação inicial usa `persist` dentro de uma transação; não há atualização ou exclusão de estudos, estudantes ou profissionais nessa operação.

Os campos clínicos/documentais que são `NOT NULL` no schema são solicitados explicitamente na abertura. Assim como no projeto de referência, a origem `Outro` exige descrição e respostas `Sim` exigem detalhes. Campos posteriores de acompanhamento e assinaturas continuam sem preenchimento nesta US. A situação inicial `EM_ANDAMENTO` e sua descrição seguem a referência e pertencem somente à resposta de criação: não representam uma coluna nova nem implementam as mudanças de situação de outras histórias. O advice de abertura é restrito ao seu controller; os handlers e contratos das outras US foram preservados. O controller da referência reúne várias US, mas somente a parte de criação foi adaptada.

O Hibernate apenas valida o schema e a inicialização SQL da aplicação fica desativada. Não são criados bancos, tabelas, cursos, migrações ou cópias dos registros existentes. As credenciais particulares ficam fora do Git e não são copiadas para a imagem de produção.

A escolha de Supabase externo foi mantida por instrução expressa do responsável. Ela difere da exigência do professor de executar o banco como serviço do Compose. Esta limitação deve ser discutida pela equipe; não é considerada conformidade integral com essa parte do guia.

## Ambientes

`application.properties` reúne o que é comum. `dev` importa a configuração local privada e usa a porta direta 4040 por padrão. `prod` recebe a conexão por ambiente, sem importar configuração local. Cada Compose define seu perfil e usa seu arquivo privado `.env.dev` ou `.env.prod`.

Não há banco embarcado. Os testes de integração são opcionais, exigem configuração explícita de um PostgreSQL de teste já existente e isolam suas tabelas em um schema próprio. A conexão da aplicação nunca é adotada automaticamente pelos testes.

## Referências

- [Instruções do projeto CLAI](https://gist.github.com/ReneDouglas/c8682724c8d5aa75c60f5ba2f7583e12).
- [Perfis do Spring Boot](https://docs.spring.io/spring-boot/reference/features/profiles.html).
- [Propriedades dinâmicas de teste do Spring](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/ctx-management/dynamic-property-sources.html).
