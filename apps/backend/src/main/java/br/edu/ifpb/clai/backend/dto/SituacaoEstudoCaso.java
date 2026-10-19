package br.edu.ifpb.clai.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SituacaoEstudoCaso(Integer idEstudoCaso, String matriculaEstudante,
        boolean arquivado, LocalDateTime dataArquivamento, String motivoArquivamento,
        boolean trancado, LocalDateTime dataTrancamento, String justificativaTrancamento,
        LocalDate dataFechamento) {
}
