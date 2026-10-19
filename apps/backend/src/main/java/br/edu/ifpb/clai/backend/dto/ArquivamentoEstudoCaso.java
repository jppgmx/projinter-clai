package br.edu.ifpb.clai.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record ArquivamentoEstudoCaso(
        @NotBlank(message = "Informe o motivo do arquivamento.") String motivo) {
}
