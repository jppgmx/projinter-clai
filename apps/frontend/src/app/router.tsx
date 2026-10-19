import { createBrowserRouter } from 'react-router-dom'
import { LoginPage } from '@/features/auth/login-page'
import { ProtectedRoute } from '@/features/auth/protected-route'
import { DashboardPage } from '@/features/dashboard/dashboard-page'
import { PlaceholderPage } from '@/features/placeholder/placeholder-page'
import { AppLayout } from './layout/app-layout'

export const router = createBrowserRouter([
  { path: '/login', element: <LoginPage /> },
  {
    element: <ProtectedRoute />,
    children: [
      {
        element: <AppLayout />,
        children: [
          { path: '/', element: <DashboardPage /> },
          { path: '/alunos', element: <PlaceholderPage title="Alunos" /> },
          { path: '/alunos/:alunoId', element: <PlaceholderPage title="Página do aluno" /> },
          { path: '/atendimentos', element: <PlaceholderPage title="Atendimentos" /> },
          { path: '/atendimentos/:atendimentoId', element: <PlaceholderPage title="Atendimento" /> },
          { path: '/planos-estudos/planos', element: <PlaceholderPage title="Planos de ação" /> },
          { path: '/planos-estudos/estudos', element: <PlaceholderPage title="Estudos de caso" /> },
          { path: '/avaliacoes-adaptadas', element: <PlaceholderPage title="Avaliações adaptadas" /> },
          { path: '/alunos-apoiadores', element: <PlaceholderPage title="Alunos apoiadores" /> },
          { path: '/demandas-externas', element: <PlaceholderPage title="Demandas externas" /> },
          { path: '/administracao', element: <PlaceholderPage title="Administração" /> },
        ],
      },
    ],
  },
  { path: '*', element: <PlaceholderPage title="Página não encontrada" /> },
])
