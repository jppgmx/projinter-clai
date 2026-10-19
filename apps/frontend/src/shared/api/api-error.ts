/** Tipo unico de erro do front. */
export class ApiError extends Error {
  readonly status: number
  /** Erros por campo devolvidos pelo Spring em 400: { campo: mensagem } */
  readonly campos?: Record<string, string>

  constructor(message: string, status: number, campos?: Record<string, string>) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.campos = campos
  }
}
