import { ShieldCheck } from 'lucide-react'
import { Outlet } from 'react-router-dom'
import { Sidebar } from './sidebar'

export function AppLayout() {
  return (
    <div className="flex min-h-screen bg-gray-100">
      <Sidebar />
      <div className="flex min-w-0 flex-1 flex-col">
        <main className="flex-1 p-6">
          <Outlet />
        </main>
        <footer className="px-6 pb-4 text-xs text-gray-600">
          <p className="flex items-center gap-2 py-3">
            <ShieldCheck aria-hidden="true" className="size-4" />
            <span>
              <strong>Acesso restrito.</strong> Laudos e informações sensíveis são exclusivas da equipe autorizada.
            </span>
          </p>
          <div className="flex justify-between border-t border-gray-300 pt-3">
            <span>CLAI · Sistema de Acompanhamento Inclusivo</span>
            <span>Inclusão se constrói em conjunto.</span>
          </div>
        </footer>
      </div>
    </div>
  )
}
