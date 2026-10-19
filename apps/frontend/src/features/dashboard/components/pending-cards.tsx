import { ArrowRight, FolderOpen, HeartHandshake, NotebookPen, ClipboardCheck, type LucideIcon } from 'lucide-react'
import { Link } from 'react-router-dom'
import { Card } from '@/shared/components/card'
import { StateMessage } from '@/shared/components/state-message'
import type { Pendencias } from '../types'

interface PendingItem {
  key: keyof Pendencias
  label: string
  to: string
  icon: LucideIcon
  iconBg: string
}

const items: PendingItem[] = [
  { key: 'relatoriosPendentes', label: 'Relatórios pendentes', to: '/atendimentos', icon: NotebookPen, iconBg: 'bg-amber-100 text-amber-900' },
  { key: 'planosARevisar', label: 'Planos a revisar', to: '/planos-estudos/planos', icon: ClipboardCheck, iconBg: 'bg-violet-100 text-violet-900' },
  { key: 'estudosDeCasoARefazer', label: 'Estudos de caso a refazer', to: '/planos-estudos/estudos', icon: FolderOpen, iconBg: 'bg-violet-100 text-violet-900' },
  { key: 'relatoriosApoiadores', label: 'Relatórios de apoiadores', to: '/alunos-apoiadores', icon: HeartHandshake, iconBg: 'bg-violet-100 text-violet-900' },
]

interface PendingCardsProps {
  pendencias: Pendencias | undefined
  isLoading: boolean
  isError: boolean
}

export function PendingCards({ pendencias, isLoading, isError }: PendingCardsProps) {
  return (
    <Card aria-labelledby="pendencias-titulo" className="p-5">
      <h2 id="pendencias-titulo" className="text-base font-semibold text-gray-900">
        Suas pendências
      </h2>
      {isLoading && <StateMessage kind="loading" />}
      {isError && <StateMessage kind="error" />}
      {pendencias && (
        <ul className="mt-4 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {items.map(({ key, label, to, icon: Icon, iconBg }) => (
            <li key={key} className="flex flex-col justify-between gap-6 rounded-lg p-3">
              <div className="flex items-start justify-between">
                <span className={`flex size-9 items-center justify-center rounded-lg ${iconBg}`}>
                  <Icon aria-hidden="true" className="size-5" />
                </span>
                <span className="text-xl font-semibold text-gray-900" aria-label={`${pendencias[key]} pendências`}>
                  {pendencias[key]}
                </span>
              </div>
              <div>
                <p className="text-sm font-medium text-gray-800">{label}</p>
                <Link to={to} className="mt-3 inline-flex items-center gap-1 text-sm text-indigo-700 hover:underline">
                  Ver pendências <ArrowRight aria-hidden="true" className="size-4" />
                </Link>
              </div>
            </li>
          ))}
        </ul>
      )}
    </Card>
  )
}
