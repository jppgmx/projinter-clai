import { ArrowRight, FileText, UserPlus, Search, SquarePen, type LucideIcon } from 'lucide-react'
import { useState } from 'react'
import { Card } from '@/shared/components/card'
import { formatDate } from '@/shared/utils/format-date'

interface Shortcut {
  label: string
  to: string
  icon: LucideIcon
}

const shortcuts: Shortcut[] = [
  { label: 'Buscar estudante', to: '/estudantes.html', icon: Search },
  { label: 'Registrar estudante', to: '/cadastro-estudante.html', icon: UserPlus },
  { label: 'Abrir estudo de caso', to: '/abertura-estudo-caso.html', icon: SquarePen },
  { label: 'Consultar estudos', to: '/editar-estudo-caso.html', icon: FileText },
]

export function QuickShortcuts() {
  const [today] = useState(() => new Date())

  return (
    <Card aria-labelledby="atalhos-titulo" className="p-5">
      <div className="flex items-baseline justify-between">
        <h2 id="atalhos-titulo" className="text-base font-semibold text-gray-900">
          Atalhos rápidos
        </h2>
        <time dateTime={today.toISOString().slice(0, 10)} className="text-xs text-gray-600">
          {formatDate(today)}
        </time>
      </div>
      <ul className="mt-4 grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
        {shortcuts.map(({ label, to, icon: Icon }) => (
          <li key={to}>
            <a
              href={to}
              className="flex items-center gap-3 rounded-lg border border-gray-200 px-4 py-3 text-sm text-gray-900 hover:bg-gray-50"
            >
              <Icon aria-hidden="true" className="size-4 text-indigo-700" />
              <span className="flex-1">{label}</span>
              <ArrowRight aria-hidden="true" className="size-4 text-gray-500" />
            </a>
          </li>
        ))}
      </ul>
    </Card>
  )
}
