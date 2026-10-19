export interface StudentSummary {
  matricula: string
  nomeEstudante: string
  curso: { idCurso: number; nomeCurso: string }
  turma: string
  periodoIngresso: string
}

export interface StudySummary {
  idEstudoCaso: number
  matriculaEstudante: string
  nomeEstudante: string
  periodoLetivo: string
  status: string
  editavel: boolean
}
