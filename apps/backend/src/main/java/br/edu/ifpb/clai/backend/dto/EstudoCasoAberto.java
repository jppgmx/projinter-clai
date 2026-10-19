package br.edu.ifpb.clai.backend.dto;

import br.edu.ifpb.clai.backend.entidade.EstudoCaso;

public record EstudoCasoAberto(Integer idEstudoCaso, String matriculaEstudante,
                               String periodoLetivo, ProfissionalResponsavel profissionalResponsavel,
                               String status, String statusDescricao) {
    public static EstudoCasoAberto de(EstudoCaso estudo) {
        // Situação da abertura, não uma coluna nova nem uma operação de mudança de status.
        return new EstudoCasoAberto(estudo.getIdEstudoCaso(), estudo.getEstudante().getMatricula(),
                estudo.getPeriodoLetivo(), ProfissionalResponsavel.de(estudo.getProfissionalResponsavel()),
                "EM_ANDAMENTO", "Em andamento");
    }
}
