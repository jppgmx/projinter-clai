package br.edu.ifpb.clai.backend.dto;

import br.edu.ifpb.clai.backend.entidade.Curso;

public record CursoEstudante(Integer idCurso, String nomeCurso) {
    public static CursoEstudante de(Curso curso) {
        return new CursoEstudante(curso.getIdCurso(), curso.getNomeCurso());
    }
}
