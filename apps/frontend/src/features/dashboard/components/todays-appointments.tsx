import { ChevronRight } from 'lucide-react'
import { Link } from 'react-router-dom'
import { Card } from '@/shared/components/card'
import { StateMessage } from '@/shared/components/state-message'
import { StatusBadge } from '@/shared/components/status-badge'
import type { AtendimentoDeHoje, TipoAtendimento } from '../types'

const tipoLabels: Record<TipoAtendimento, string> = {
  atendimento_individual: 'Atendimento individual',
  acompanhamento_pedagogico: 'Acompanhamento pedagógico',
}

interface TodaysAppointmentsProps {
  atendimentos: AtendimentoDeHoje[] | undefined
  isLoading: boolean
  isError: boolean
}

export function TodaysAppointments({ atendimentos, isLoading, isError }: TodaysAppointmentsProps) {
  return (
    <Card aria-labelledby="hoje-titulo">
      <div className="flex items-center justify-between border-b border-gray-100 px-4 py-3">
        <h2 id="hoje-titulo" className="text-base font-semibold text-gray-900">
          Atendimentos de hoje
        </h2>
        <Link to="/atendimentos" className="text-xs text-gray-700 hover:underline">
          Ver agenda &rsaquo;
        </Link>
      </div>
      {isLoading && <StateMessage kind="loading" />}
      {isError && <StateMessage kind="error" />}
      {atendimentos?.length === 0 && <StateMessage kind="empty" message="Nenhum atendimento agendado para hoje." />}
      {atendimentos && atendimentos.length > 0 && (
        <ul className="divide-y divide-gray-100">
          {atendimentos.map((item) => (
            <li key={item.id}>
              {/* [ABERTO] rota de detalhe do atendimento */}
              <Link to={`/atendimentos/${item.id}`} className="flex items-center gap-4 px-4 py-3 hover:bg-gray-50">
                <span className="w-12 shrink-0 text-sm text-gray-900">
                  <span className="block font-medium">{item.inicio}</span>
                  <span className="block text-xs text-gray-600">{item.fim}</span>
                </span>
                <span className="flex flex-1 flex-col gap-1 border-l border-gray-200 pl-4">
                  <span className="text-sm font-medium text-gray-900">{item.alunoNome}</span>
                  <span className="text-xs text-gray-600">{tipoLabels[item.tipo]}</span>
                  <span>
                    <StatusBadge variant="scheduled" label="Agendado" />
                  </span>
                </span>
                <ChevronRight aria-hidden="true" className="size-4 text-gray-500" />
              </Link>
            </li>
          ))}
        </ul>
      )}
    </Card>
  )
}
