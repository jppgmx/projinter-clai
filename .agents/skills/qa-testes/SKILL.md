---
name: qa-testes
description: Use ao escrever, rodar ou revisar testes, ou ao validar uma entrega do CLAI antes de passar para "To test".
---

# QA e testes CLAI

## Escopo e critérios de aceite

- Leia `AGENTS.md` e as instruções específicas da área alterada.
- Leia a issue e seus critérios de aceite, usando o conteúdo fornecido ou
  a integração disponível. Se faltarem, solicite a referência; não invente.
- Relacione cada critério a um cenário, resultado esperado e evidência.
  Registre como aprovado, reprovado ou não verificado.
- Confira o comportamento observável e os contratos reais do código.
  Não aprove uma entrega apenas porque compila ou porque há testes novos.
- Preserve mudanças preexistentes e mantenha a validação no escopo.
  Validar não autoriza alterar o status da issue ou publicar comentários.

## Backend: ferramentas existentes

Testes em `apps/backend/src/test/java/br/edu/ifpb/clai/backend`.

- Unitários usam JUnit, AssertJ e Mockito, como
  `SituacaoEstudoCasoServiceTests` e `EdicaoEstudanteTests`.
- `ArquivoMedicoTests` verifica nome, tamanho e conteúdo de arquivos.
- Integrações usam SpringBootTest, MockMvc, JdbcTemplate e PostgreSQL,
  com `@ActiveProfiles("test")` e `PostgresIntegrationSupport`.
- Consulte os testes de cadastro, consulta, abertura e atualização antes
  de acrescentar cenários. Não presuma H2 ou Testcontainers configurados.
- Verifique resposta HTTP e efeitos persistidos, inclusive ausência de
  gravação em rejeições. Nos unitários, confira interações relevantes
  com repositórios sem testar apenas detalhes internos da implementação.

## Executar backend com isolamento

Em `apps/backend`, com Java 21:

```bash
./mvnw test
./mvnw package
```

- Rode test para mudanças de backend; package também para empacotamento.
- Integrações exigem `CLAI_TEST_DATABASE_URL` e
  `CLAI_TEST_DATABASE_PASSWORD`; `CLAI_TEST_DATABASE_USERNAME` é opcional.
  Identifique somente os nomes, nunca imprima os valores.
- O suporte exige banco PostgreSQL chamado `clai_test` e rejeita
  currentSchema na URL. Define schema `clai_test_` com UUID, inicializa
  `schema-cadastro.sql` e configura Hibernate para validar o schema.
- A conexão de teste é explícita e tem precedência sobre a da aplicação.
  Nunca reutilize automaticamente a conexão da aplicação.
- Fixtures e limpezas SQL pertencem exclusivamente ao schema de teste.
  Não execute essas operações no banco compartilhado.
- As integrações usam EnabledIfEnvironmentVariable e podem ser ignoradas
  sem a URL. Confira o resumo e os relatórios em `target/surefire-reports`;
  informe quantos testes executaram, falharam ou foram ignorados.
- Sucesso do Maven com integrações ignoradas não valida persistência.
  Não remova as proteções de banco para fazer testes rodarem.

## Frontend: cobertura atual e validação

- O package.json não define script de testes nem ferramentas dedicadas
  para testes de componentes ou ponta a ponta. Não invente `npm test`
  nem adicione ferramentas sem que a tarefa peça.
- Em `apps/frontend`, execute:

```bash
npm run lint
npm run build
```

- Lint usa oxlint; build executa TypeScript e Vite. Esses comandos não
  comprovam comportamento de telas ou integração com a API.
- Valide manualmente o fluxo alterado com backend e dados fictícios:
  sucesso, carregamento, vazio, erro, nova tentativa e submissão.
- Confira labels, erros por campo, teclado, foco e ações bloqueadas.
- Preserve acessibilidade dos caminhos `.html` e inclusão do legado
  no build quando afetado. Há testes MockMvc que verificam entrega de
  páginas/assets; isso não equivale a executar JavaScript no navegador.
- Em alterações entre front e back, confira payloads, códigos HTTP e
  mensagens apresentados pela interface.

## Casos de borda conforme o fluxo

- Matrícula inexistente: detalhe retorna 404; busca sem resultados retorna
  lista vazia. Confira o contrato do endpoint, sem igualar esses casos.
- Estudo inexistente: consultar/atualizar não deve criar registros.
- Estudo arquivado, trancado ou finalizado: consulta pode continuar;
  atualização é rejeitada com 409 e não altera dados.
- Campo fora da lista `CampoEstudoCaso`, referência profissional não
  permitida ou tentativa de mudar vínculo/situação: rejeitar com 400.
- Atualização vazia, JSON inválido, obrigatórios em branco, limites
  excedidos, referências inexistentes e distinção entre ausente e null.
- Matrícula duplicada, inclusive concorrente: não sobrescrever cadastro.
- Histórico: não misturar estudos de estudantes diferentes.
- Texto semelhante a SQL deve permanecer dado, sem executar comandos.
- Resposta sem permissão: quando houver contrato real de autorização,
  verificar o status definido, ausência de dados/gravação e tratamento
  pela interface. Atualmente não há autenticação implementada; registre
  essa lacuna se exigida pela issue. Não invente proteção 401/403,
  usuário autenticado ou login para declarar o cenário aprovado.

## Dados e evidências

- Use somente dados fictícios em testes, fixtures e validações manuais.
- Não leia nem mostre valores de `.env` ou application-*.properties.
  Nunca inclua credenciais em testes, comandos documentados ou evidências.
- Não use dados reais de estudantes, laudos ou planos reais.
  Não exponha esses dados em logs, screenshots ou relatórios.
- Anexar/remover laudo exige autorização explícita; use somente arquivos
  sintéticos quando o cenário estiver autorizado.
- Registre comando, resultado, cenário e limitação sem copiar payloads
  sensíveis. Não afirme que algo passou sem executar a verificação.

## Checklist antes de "To test"

- [ ] Critérios da issue associados a cenários e evidências.
- [ ] Sucesso e bordas pertinentes verificados; rejeições não gravam dados.
- [ ] Backend testado em clai_test com schema isolado quando necessário.
- [ ] Testes executados, falhos e ignorados discriminados.
- [ ] Lint/build do front e package do backend executados quando pertinentes.
- [ ] Fluxo manual, contratos HTTP e legado conferidos quando afetados.
- [ ] Lacunas de autorização e testes do front registradas quando relevantes.
- [ ] Apenas dados fictícios; nenhum segredo ou artefato gerado incluído.
- [ ] Falhas e critérios não verificados claramente informados no resumo.
