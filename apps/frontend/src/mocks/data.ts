import type { DashboardData } from '@/features/dashboard/types'

/** Dados FICTICIOS. Nunca colocar dados reais de alunos aqui. */
export const mockDashboard: DashboardData = {
  pendencias: {
    relatoriosPendentes: 3,
    planosARevisar: 2,
    estudosDeCasoARefazer: 1,
    relatoriosApoiadores: 2,
  },
  alunosEmAtencao: [
    { id: 'aluno-001', nome: 'Aluno Exemplo A', curso: 'Técnico em Informática', alerta: 'plano_a_revisar' },
    { id: 'aluno-002', nome: 'Aluna Exemplo B', curso: 'Análise e Desenvolvimento de Sistemas', alerta: 'relatorio_pendente' },
    { id: 'aluno-003', nome: 'Aluno Exemplo C', curso: 'Engenharia Civil', alerta: 'estudo_de_caso_a_refazer' },
  ],
  atendimentosHoje: [
    { id: 'atd-001', inicio: '08:00', fim: '08:50', alunoNome: 'Aluno Exemplo A', tipo: 'atendimento_individual', status: 'agendado' },
    { id: 'atd-002', inicio: '10:00', fim: '10:50', alunoNome: 'Aluna Exemplo B', tipo: 'acompanhamento_pedagogico', status: 'agendado' },
    { id: 'atd-003', inicio: '14:00', fim: '14:50', alunoNome: 'Aluno Exemplo C', tipo: 'atendimento_individual', status: 'agendado' },
  ],
}

/** Credencial FICTICIA do mock de login: qualquer usuario + esta senha. */
export const MOCK_PASSWORD = 'clai123'
