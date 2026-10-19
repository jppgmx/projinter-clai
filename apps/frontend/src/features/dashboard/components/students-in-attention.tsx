import { UserRound } from 'lucide-react'
import { Link } from 'react-router-dom'
import { Card } from '@/shared/components/card'
import { StateMessage } from '@/shared/components/state-message'
import { StatusBadge } from '@/shared/components/status-badge'
import type { AlertaAluno, AlunoEmAtencao } from '../types'

const alertLabels: Record<AlertaAluno, string> = {
  plano_a_revisar: 'Plano a revisar',
  relatorio_pendente: 'Relatório pendente',
  estudo_de_caso_a_refazer: 'Estudo de caso a refazer',
}

interface StudentsInAttentionProps {
  alunos: AlunoEmAtencao[] | undefined
  isLoading: boolean
  isError: boolean
}

export function StudentsInAttention({ alunos, isLoading, isError }: StudentsInAttentionProps) {
  return (
    <Card aria-labelledby="atencao-titulo">
      <div className="flex items-center justify-between border-b border-gray-100 px-4 py-3">
        <h2 id="atencao-titulo" className="text-base font-semibold text-gray-900">
          Alunos em atenção
        </h2>
        <Link to="/alunos" className="text-xs text-gray-700 hover:underline">
          Ver todos &rsaquo;
        </Link>
      </div>
      {isLoading && <StateMessage kind="loading" />}
      {isError && <StateMessage kind="error" />}
      {alunos?.length === 0 && <StateMessage kind="empty" message="Nenhum aluno em atenção no momento." />}
      {alunos && alunos.length > 0 && (
        <ul className="divide-y divide-gray-100">
          {alunos.map((aluno) => (
            <li key={aluno.id}>
              {/* [ABERTO] rota da pagina do aluno */}
              <Link to={`/alunos/${aluno.id}`} className="flex items-start gap-3 px-4 py-3 hover:bg-gray-50">
                <span className="flex size-7 shrink-0 items-center justify-center rounded-full bg-gray-100 text-gray-600">
                  <UserRound aria-hidden="true" className="size-4" />
                </span>
                <span className="flex flex-col gap-1">
                  <span className="text-sm font-medium text-gray-900">{aluno.nome}</span>
                  <StatusBadge variant="alert" label={alertLabels[aluno.alerta]} />
                  <span className="text-xs text-gray-600">{aluno.curso}</span>
                </span>
              </Link>
            </li>
          ))}
        </ul>
      )}
    </Card>
  )
}
