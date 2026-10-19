import { apiClient } from '@/shared/api/api-client'
import type { AuthUser, LoginPayload } from './types'

/**
 * [ABERTO] Autenticacao (JWT ou cookie de sessao) nao definida.
 * Este e o unico arquivo a mudar quando for definida.
 * Endpoint assumido: POST /auth/login -> AuthUser
 */
export function login(payload: LoginPayload): Promise<AuthUser> {
  return apiClient.post<AuthUser>('/auth/login', payload)
}
