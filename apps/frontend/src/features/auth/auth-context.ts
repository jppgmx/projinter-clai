import { createContext } from 'react'
import type { AuthUser, LoginPayload } from './types'

export interface AuthContextValue {
  user: AuthUser | null
  signIn: (payload: LoginPayload) => Promise<void>
  signOut: () => void
}

export const AuthContext = createContext<AuthContextValue | null>(null)
