package br.edu.ifpb.clai.backend.dto;

import java.time.LocalDate;

public record HistoricoEstudoCaso(
        Integer idEstudoCaso,
        String periodoLetivo,
        String datasAtendimentoAnamnese,
        String origemEncaminhamento,
        String origemEncaminhamentoOutro,
        String motivoPrincipal,
        String demandaEstudante,
        String demandaFamiliaResponsavel,
        String demandaEquipeEscolar,
        LocalDate dataFechamento) {
}
