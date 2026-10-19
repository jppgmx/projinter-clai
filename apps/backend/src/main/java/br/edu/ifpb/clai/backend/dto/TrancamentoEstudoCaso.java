package br.edu.ifpb.clai.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record TrancamentoEstudoCaso(
        @NotBlank(message = "Informe a justificativa do trancamento.") String justificativa) {
}
