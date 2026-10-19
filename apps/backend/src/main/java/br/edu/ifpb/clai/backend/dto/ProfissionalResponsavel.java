package br.edu.ifpb.clai.backend.dto;

import br.edu.ifpb.clai.backend.entidade.Profissional;

public record ProfissionalResponsavel(Integer idProfissional, String nomeProfissional, String papelCargo) {
    public static ProfissionalResponsavel de(Profissional profissional) {
        return new ProfissionalResponsavel(profissional.getIdProfissional(),
                profissional.getNomeProfissional(), profissional.getPapelCargo());
    }
}
