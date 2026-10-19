import { API_URL } from './config'
import { ApiError } from './api-error'

const DEFAULT_ERROR_MESSAGE = 'Não foi possível concluir a operação. Tente novamente.'

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  let response: Response
  try {
    response = await fetch(`${API_URL}${path}`, {
      ...init,
      headers: { 'Content-Type': 'application/json', Accept: 'application/json', ...init?.headers },
    })
  } catch {
    throw new ApiError('Sem conexão com o servidor.', 0)
  }

  if (!response.ok) {
    // O Spring do CLAI responde { erro: string, campos?: { campo: mensagem } }
    const body: unknown = await response.json().catch(() => null)
    let message = DEFAULT_ERROR_MESSAGE
    let campos: Record<string, string> | undefined
    if (typeof body === 'object' && body !== null) {
      if ('erro' in body && typeof body.erro === 'string') message = body.erro
      if ('campos' in body && typeof body.campos === 'object' && body.campos !== null) {
        campos = body.campos as Record<string, string>
      }
    }
    throw new ApiError(message, response.status, campos)
  }

  if (response.status === 204) return undefined as T
  return (await response.json()) as T
}

/** Unico ponto de acesso HTTP do front: nunca usar `fetch` solto nos componentes. */
export const apiClient = {
  get: <T>(path: string) => request<T>(path),
  post: <T>(path: string, body: unknown) =>
    request<T>(path, { method: 'POST', body: JSON.stringify(body) }),
  put: <T>(path: string, body: unknown) =>
    request<T>(path, { method: 'PUT', body: JSON.stringify(body) }),
}
