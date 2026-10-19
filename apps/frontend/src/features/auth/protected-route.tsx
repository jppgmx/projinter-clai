import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from './use-auth'

/** Esconde rotas de quem nao esta logado. Seguranca real: back end. */
export function ProtectedRoute() {
  const { user } = useAuth()
  return user ? <Outlet /> : <Navigate to="/login" replace />
}
