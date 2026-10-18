package br.edu.ifpb.clai.backend.dto;

import br.edu.ifpb.clai.backend.entidade.Estudante;
import java.util.List;

public record DetalhesEstudante(
        String matricula,
        String nomeEstudante,
        CursoEstudante curso,
        String turma,
        String periodoIngresso,
        String dataNascimentoIdade,
        String telefoneEstudante,
        String emailEstudante,
        String nomeResponsavel,
        String grauParentesco,
        String telefoneResponsavel,
        String emailResponsavel,
        List<HistoricoEstudoCaso> estudosDeCaso) {
    public static DetalhesEstudante de(Estudante estudante, List<HistoricoEstudoCaso> historico) {
        return new DetalhesEstudante(estudante.getMatricula(), estudante.getNomeEstudante(),
                CursoEstudante.de(estudante.getCurso()), estudante.getTurma(), estudante.getPeriodoIngresso(),
                estudante.getDataNascimentoIdade(), estudante.getTelefoneEstudante(), estudante.getEmailEstudante(),
                estudante.getNomeResponsavel(), estudante.getGrauParentesco(), estudante.getTelefoneResponsavel(),
                estudante.getEmailResponsavel(), List.copyOf(historico));
    }
}
