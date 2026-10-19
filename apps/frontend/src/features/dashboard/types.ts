import type { AlunoId } from '@/shared/types/ids'

/**
 * [PROVISORIO] Contrato do dashboard: nao existe no banco nem na API.
 * Endpoint assumido: GET /dashboard. Trocar pelo real so em `dashboard-api.ts`.
 * Nenhum dado sensivel aqui: so nome, curso e o tipo do alerta.
 */
export type AlertaAluno = 'plano_a_revisar' | 'relatorio_pendente' | 'estudo_de_caso_a_refazer'

export type TipoAtendimento = 'atendimento_individual' | 'acompanhamento_pedagogico'

export interface Pendencias {
  relatoriosPendentes: number
  planosARevisar: number
  estudosDeCasoARefazer: number
  relatoriosApoiadores: number
}

export interface AlunoEmAtencao {
  id: AlunoId
  nome: string
  curso: string
  alerta: AlertaAluno
}

export interface AtendimentoDeHoje {
  id: string
  inicio: string // hh:mm
  fim: string // hh:mm
  alunoNome: string
  tipo: TipoAtendimento
  status: 'agendado'
}

export interface DashboardData {
  pendencias: Pendencias
  alunosEmAtencao: AlunoEmAtencao[]
  atendimentosHoje: AtendimentoDeHoje[]
}
