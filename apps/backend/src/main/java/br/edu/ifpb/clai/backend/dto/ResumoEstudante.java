package br.edu.ifpb.clai.backend.dto;

import br.edu.ifpb.clai.backend.entidade.Estudante;

public record ResumoEstudante(
        String matricula, String nomeEstudante, CursoEstudante curso,
        String turma, String periodoIngresso) {
    public static ResumoEstudante de(Estudante estudante) {
        return new ResumoEstudante(estudante.getMatricula(), estudante.getNomeEstudante(),
                CursoEstudante.de(estudante.getCurso()), estudante.getTurma(), estudante.getPeriodoIngresso());
    }
}
