import { delay, http, HttpResponse } from 'msw'
import type { AuthUser, LoginPayload } from '@/features/auth/types'
import { API_URL } from '@/shared/api/config'
import { MOCK_PASSWORD, mockDashboard } from './data'

export const handlers = [
  http.post(`${API_URL}/auth/login`, async ({ request }) => {
    const { usuario, senha } = (await request.json()) as LoginPayload
    await delay(300)
    if (!usuario || senha !== MOCK_PASSWORD) {
      return HttpResponse.json({ message: 'Usuário ou senha inválidos.' }, { status: 401 })
    }
    const user: AuthUser = { nome: 'Usuário Exemplo', perfil: 'psicopedagogo' }
    return HttpResponse.json(user)
  }),

  http.get(`${API_URL}/dashboard`, async () => {
    await delay(300)
    return HttpResponse.json(mockDashboard)
  }),
]
