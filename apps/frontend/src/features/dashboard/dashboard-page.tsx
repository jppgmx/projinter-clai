import { PendingCards } from './components/pending-cards'
import { QuickShortcuts } from './components/quick-shortcuts'
import { StudentsInAttention } from './components/students-in-attention'
import { TodaysAppointments } from './components/todays-appointments'
import { useDashboard } from './dashboard-api'

export function DashboardPage() {
  const { data, isLoading, isError } = useDashboard()

  return (
    <div className="space-y-6">
      <h1 className="sr-only">Início</h1>
      <QuickShortcuts />
      <PendingCards pendencias={data?.pendencias} isLoading={isLoading} isError={isError} />
      <div className="grid gap-6 lg:grid-cols-2">
        <StudentsInAttention alunos={data?.alunosEmAtencao} isLoading={isLoading} isError={isError} />
        <TodaysAppointments atendimentos={data?.atendimentosHoje} isLoading={isLoading} isError={isError} />
      </div>
    </div>
  )
}
