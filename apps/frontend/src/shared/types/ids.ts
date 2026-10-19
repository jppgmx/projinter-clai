/**
 * Identificador do aluno.
 * No banco a PK do estudante e a `matricula`. O alias isola essa decisao:
 * se o back end mudar a chave, so este tipo e os adapters mudam.
 */
export type AlunoId = string
