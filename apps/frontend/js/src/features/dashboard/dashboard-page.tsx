import { FileText, Users } from 'lucide-react'
import { Card } from '@/shared/components/card'
import { StateMessage } from '@/shared/components/state-message'
import { QuickShortcuts } from './components/quick-shortcuts'
import { useDashboard } from './dashboard-api'

const labels: Record<string, string> = { EM_ANDAMENTO: 'Em andamento', ARQUIVADO: 'Arquivado', TRANCADO: 'Trancado', FINALIZADO: 'Finalizado' }

export function DashboardPage() {
  const { students, studies } = useDashboard()
  return (
    <div className="space-y-6">
      <h1 className="sr-only">Início</h1>
      <QuickShortcuts />
      <Card aria-labelledby="resumo-titulo" className="p-5">
        <h2 id="resumo-titulo" className="text-base font-semibold text-gray-900">Visão geral</h2>
        <div className="mt-5 grid gap-6 sm:grid-cols-2">
          <a href="/estudantes.html" className="flex items-center gap-4 rounded-lg border border-gray-200 p-5 hover:bg-gray-50"><Users className="size-7 text-indigo-700" aria-hidden="true" /><div><span className="text-2xl font-semibold">{students.isSuccess ? students.data.length : '—'}</span><span className="block text-sm text-gray-600">Estudantes cadastrados</span></div></a>
          <a href="/editar-estudo-caso.html" className="flex items-center gap-4 rounded-lg border border-gray-200 p-5 hover:bg-gray-50"><FileText className="size-7 text-indigo-700" aria-hidden="true" /><div><span className="text-2xl font-semibold">{studies.isSuccess ? studies.data.length : '—'}</span><span className="block text-sm text-gray-600">Estudos de caso</span></div></a>
        </div>
      </Card>
      <div className="grid gap-6 lg:grid-cols-2">
        <Card aria-labelledby="estudantes-titulo" className="p-5">
          <div className="flex items-center justify-between"><h2 id="estudantes-titulo" className="text-base font-semibold text-gray-900">Estudantes cadastrados</h2><a href="/estudantes.html" className="text-xs text-indigo-700">Ver todos ›</a></div>
          {students.isPending ? <StateMessage kind="loading" /> : students.isError ? <><StateMessage kind="error" message="Não foi possível consultar estudantes. Confira o backend na porta 8080." /><button onClick={() => void students.refetch()} type="button" className="text-sm text-indigo-700 underline">Tentar novamente</button></> : !students.data.length ? <StateMessage kind="empty" message="Nenhum estudante cadastrado." /> :
            <ul className="mt-3 divide-y divide-gray-200">{students.data.slice(0, 4).map(student => <li key={student.matricula} className="py-4"><a className="text-sm font-medium text-gray-900 hover:text-indigo-700" href={`/detalhes-estudante.html?matricula=${encodeURIComponent(student.matricula)}`}>{student.nomeEstudante}</a><p className="mt-1 text-xs text-gray-600">{student.curso?.nomeCurso} · {student.matricula}</p></li>)}</ul>}
        </Card>
        <Card aria-labelledby="estudos-titulo" className="p-5">
          <div className="flex items-center justify-between"><h2 id="estudos-titulo" className="text-base font-semibold text-gray-900">Estudos de caso recentes</h2><a href="/editar-estudo-caso.html" className="text-xs text-indigo-700">Ver todos ›</a></div>
          {studies.isPending ? <StateMessage kind="loading" /> : studies.isError ? <><StateMessage kind="error" message="Não foi possível consultar estudos de caso. Confira o backend na porta 8080." /><button onClick={() => void studies.refetch()} type="button" className="text-sm text-indigo-700 underline">Tentar novamente</button></> : !studies.data.length ? <StateMessage kind="empty" message="Nenhum estudo de caso cadastrado." /> :
            <ul className="mt-3 divide-y divide-gray-200">{studies.data.slice(0, 4).map(study => <li key={study.idEstudoCaso} className="py-4"><a href={`/editar-estudo-caso.html?id=${encodeURIComponent(study.idEstudoCaso)}`} className="text-sm font-medium text-gray-900 hover:text-indigo-700">{study.nomeEstudante}</a><p className="mt-1 text-xs text-gray-600">Estudo #{study.idEstudoCaso} · {study.periodoLetivo}</p><span className="mt-2 inline-block rounded bg-violet-100 px-2 py-0.5 text-xs text-violet-900">{labels[study.status] || study.status}</span></li>)}</ul>}
        </Card>
      </div>
    </div>
  )
}
