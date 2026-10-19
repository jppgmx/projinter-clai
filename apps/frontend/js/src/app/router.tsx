import { createBrowserRouter, Navigate } from 'react-router-dom'
import { DashboardPage } from '@/features/dashboard/dashboard-page'
import { PlaceholderPage } from '@/features/placeholder/placeholder-page'
import { AppLayout } from './layout/app-layout'
import { ExistingPage } from './existing-page'

export const router = createBrowserRouter([
  {
    element: <AppLayout />,
    children: [
      { path: '/', element: <DashboardPage /> },
      { path: '/login', element: <Navigate to="/" replace /> },
      { path: '/alunos', element: <ExistingPage to="/estudantes.html" /> },
      { path: '/alunos/:alunoId', element: <ExistingPage to="/detalhes-estudante.html" student /> },
      { path: '/planos-estudos/estudos', element: <ExistingPage to="/editar-estudo-caso.html" /> },
      { path: '*', element: <PlaceholderPage title="Página não encontrada" /> },
    ],
  },
])
