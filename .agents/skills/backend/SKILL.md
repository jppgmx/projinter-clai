---
name: backend
description: Use ao criar ou alterar endpoints, serviços, repositórios, entidades, DTOs, regras de negócio ou testes do backend Spring do CLAI.
---

# Backend CLAI

## Antes de alterar

- Leia o `AGENTS.md` da raiz e eventuais instruções mais específicas.
- Leia a skill `seguranca` ao lidar com dados sensíveis, banco ou logs.
- Confira o fluxo existente e seus testes antes de mudar contratos.
- Leia `README.md` e `docs/arquitetura.md` antes de alterar arquitetura,
  contratos ou empacotamento. Preserve mudanças preexistentes.
- Não leia nem exponha valores de `.env` ou `application-*.properties`;
  para configuração, identifique somente nomes de variáveis, sem valores.

## Estrutura e dependências

Base Java: `apps/backend/src/main/java/br/edu/ifpb/clai/backend`.

- Java 21, Spring Boot 4, Maven Wrapper e PostgreSQL.
- O pom inclui Web MVC, Jakarta Validation, Spring Data JPA e driver PostgreSQL.
  Não presuma bibliotecas de autenticação, migração ou banco em memória.
- `controller`: rotas, entrada HTTP, validação e respostas.
- `servico`: regras de negócio, coordenação e limites transacionais.
- `repositorio`: persistência com EntityManager, interfaces Spring Data
  e consultas JdbcTemplate, conforme o fluxo existente.
- `entidade`: modelo e mapeamento persistido, sem DTO ou HTTP.
  Também contém `CampoEstudoCaso` e a validação de `ArquivoMedico`.
- `dto`: contratos de entrada e saída, em geral records.
- `exception`: exceções de negócio e tradução para HTTP.

## Fluxo, transações e validação

- Respeite controller → servico → repositorio e injeção por construtor.
  Controllers não acessam repositórios; serviços não escolhem códigos HTTP.
- Mantenha `@Transactional` nos serviços para operações de escrita;
  consultas usam `@Transactional(readOnly = true)` conforme o fluxo.
- DTOs usam constraints Jakarta e normalização; controllers acionam
  `@Valid` nos contratos que dependem de Bean Validation.
- Serviços validam existência de referências, vínculos, estado e regras
  de negócio. Na atualização de estudos, há validação programática com
  `Validator`; não presuma que todo contrato usa apenas `@Valid`.
- Preserve nomes de tabelas, colunas, chaves e constraints existentes.
  A matrícula é a chave do estudante e não pode mudar na edição.
- O cadastro de estudante usa persist/flush para inserir, sem sobrescrever
  matrícula existente por merge. Preserve essa proteção contra duplicidade.

## Atualização de estudos de caso

- Consulte `AtualizacaoEstudoCasoService`, seu repositório, DTO e testes.
  O PUT existente atualiza parcialmente os mapas `campos` e `profissionais`.
- Campos ausentes são preservados; texto é normalizado com strip e branco
  vira null. Null explícito pode limpar opcionais, não obrigatórios.
- A lista textual permitida é o enum `CampoEstudoCaso`: use suas chaves,
  colunas e limites. Não aceite nomes de coluna fornecidos pelo cliente.
- Referências permitidas: `idProfissionalResponsavel`,
  `idPsicopedagogoAssinatura` e `idCoordenadorClaiAssinatura`.
  Valide IDs positivos e profissionais existentes quando não nulos.
- Rejeite atualização vazia e chaves não permitidas. Não permita alterar
  vínculo com estudante, fechamento, arquivamento ou trancamento pelos mapas.
- Valide o estado resultante, reunindo dados atuais e enviados.
  O serviço reutiliza `AberturaEstudoCaso` para obrigatórios, origem Outro
  e descrições exigidas quando informado Sim.
- Arquivado, trancado ou com data de fechamento: consulta continua possível,
  mas edição é bloqueada. `editavel` e `motivoBloqueio` vêm do backend.
- Preserve leitura com `FOR UPDATE`, transação, condições de bloqueio no
  UPDATE e exigência de exatamente uma linha alterada.
- PUT de estudo inexistente retorna erro; nunca cria um estudo.
- Mudanças de situação têm serviço e endpoints próprios. Não generalize o
  bloqueio de edição para todas as transições: arquivar, desarquivar e
  trancar possuem regras distintas em `SituacaoEstudoCasoService`.

## Erros HTTP

- Use exceções de negócio e os handlers em `exception`.
  Os `@RestControllerAdvice` atuais têm escopo por `assignableTypes`;
  confira se o controller alterado está coberto.
- Preserve `{ erro: mensagem }`, com `campos` quando houver erros por campo.
- Na atualização: inválido/JSON inválido → 400; inexistente → 404;
  estudo bloqueado ou conflito de integridade → 409.
- Situação/documentos também têm casos de estrutura indisponível → 503;
  upload acima do limite tem tratamento → 413. Confira o handler do fluxo.
- Não devolva SQL, stack traces, credenciais ou dados sensíveis em erros.

## Build e testes

Execute em `apps/backend`, com Java 21:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
./mvnw test
./mvnw package
```

- Rode testes nas mudanças de backend; package também é necessário para
  alterações de empacotamento. Não inicie a aplicação contra banco
  compartilhado sem autorização.
- Há testes unitários com JUnit, Mockito e AssertJ; integrações usam
  SpringBootTest, MockMvc e PostgreSQL com perfil test.
- Integrações exigem `CLAI_TEST_DATABASE_URL`,
  `CLAI_TEST_DATABASE_PASSWORD` e, opcionalmente, `CLAI_TEST_DATABASE_USERNAME`.
  Nunca imprima seus valores nem reutilize a conexão da aplicação.
- `PostgresIntegrationSupport` exige banco `clai_test`, rejeita currentSchema
  na URL e cria schema próprio, carregando `schema-cadastro.sql`.
  Essa fixture é exclusiva dos testes; não aplique no banco compartilhado.
- Integrações condicionadas à variável de URL podem ser ignoradas.
  Informe testes ignorados; sucesso do Maven não comprova sua execução.
- Maven empacota o legado e o dist React existente. Para incluir React
  atualizado, execute antes `npm run build` em `apps/frontend`.

## Nunca fazer

- Confiar somente no frontend para permitir operações ou bloquear edição.
- Simular autenticação, usuário autenticado ou autorização inexistente.
- Concatenar entrada em SQL/JPQL; use parâmetros. Identificadores dinâmicos
  devem vir exclusivamente de listas internas fechadas.
- Logar dados de estudantes ou conteúdo de laudos, inclusive em testes.
- Incluir credenciais, dados reais, laudos ou planos reais em código/fixtures.
- Anexar ou remover laudo sem autorização explícita; preserve validação
  de tamanho e conteúdo no backend, sem confiar só em extensão ou MIME.
- Executar migração, SQL destrutivo ou operações no banco compartilhado
  sem autorização; criar tabelas/colunas implicitamente ou simular persistência.

## Checklist de saída

- [ ] Fluxo em camadas, transações e schema preservados.
- [ ] Contratos, validações e handlers HTTP conferidos.
- [ ] Lista fechada, campos ausentes/null e bloqueios de estudos preservados.
- [ ] Testes pertinentes cobrem sucesso, inválidos, inexistentes e conflitos,
  sem gravar parcialmente em caso de falha.
- [ ] Nenhum segredo, dado real, log sensível ou artefato de build incluído.
- [ ] Testes executados; package executado quando pertinente.
- [ ] Resumo informa falhas, limitações e integrações ignoradas.
- [ ] Compatibilidade com o frontend e documentação conferidas.
