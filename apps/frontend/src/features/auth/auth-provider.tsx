import { useCallback, useMemo, useState, type ReactNode } from 'react'
import { login } from './auth-api'
import { AuthContext, type AuthContextValue } from './auth-context'
import type { AuthUser, LoginPayload } from './types'

/**
 * Sessao apenas em memoria (nada em localStorage: dado sensivel).
 * Consequencia: recarregar a pagina volta ao login. [ABERTO] ate definir a autenticacao.
 */
export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null)

  const signIn = useCallback(async (payload: LoginPayload) => {
    setUser(await login(payload))
  }, [])

  const signOut = useCallback(() => setUser(null), [])

  const value = useMemo<AuthContextValue>(() => ({ user, signIn, signOut }), [user, signIn, signOut])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
