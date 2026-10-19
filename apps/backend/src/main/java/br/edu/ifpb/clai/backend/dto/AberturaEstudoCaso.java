package br.edu.ifpb.clai.backend.dto;

import jakarta.validation.constraints.*;

public record AberturaEstudoCaso(
        @NotBlank(message = "Selecione um estudante.")
        @Size(max = 30, message = "Use no máximo 30 caracteres.") String matriculaEstudante,
        @NotNull(message = "Selecione o profissional responsável.")
        @Positive(message = "Selecione um profissional válido.") Integer idProfissionalResponsavel,
        @NotBlank(message = "Campo obrigatório.")
        @Size(max = 20, message = "Use no máximo 20 caracteres.")
        String periodoLetivo,
        @NotBlank(message = "Campo obrigatório.")
        String datasAtendimentoAnamnese,
        @NotBlank(message = "Campo obrigatório.")
        @Size(max = 50, message = "Use no máximo 50 caracteres.")
        @Pattern(regexp = "CLAI|COPED|CAEST|Coordenação de Curso|Docente|Família|Estudante|Outro", message = "Selecione uma origem válida.")
        String origemEncaminhamento,
        @Size(max = 150, message = "Use no máximo 150 caracteres.")
        String origemEncaminhamentoOutro,
        @NotBlank(message = "Campo obrigatório.")
        String motivoPrincipal,
        String demandaEstudante,
        String demandaFamiliaResponsavel,
        String demandaEquipeEscolar,
        @NotBlank(message = "Campo obrigatório.")
        String deficienciaNecessidadeInformada,
        @NotBlank(message = "Campo obrigatório.")
        @Pattern(regexp = "(?is)(?!sim[\\s:–—,;-]*$).*", message = "Se informar Sim, descreva quais documentos.")
        String docsComprobatoriosFlagQuais,
        @NotBlank(message = "Campo obrigatório.")
        @Pattern(regexp = "(?is)(?!sim[\\s:–—,;-]*$).*", message = "Se informar Sim, descreva quais condições.")
        String condicaoAssociadaFlagQuais,
        @NotBlank(message = "Campo obrigatório.")
        @Pattern(regexp = "(?is)(?!sim[\\s:–—,;-]*$).*", message = "Se informar Sim, descreva quais medicamentos.")
        String medicacaoContinuaFlagQuais) {
    public AberturaEstudoCaso {
        matriculaEstudante = normalizar(matriculaEstudante);
        periodoLetivo = normalizar(periodoLetivo);
        datasAtendimentoAnamnese = normalizar(datasAtendimentoAnamnese);
        origemEncaminhamento = normalizar(origemEncaminhamento);
        origemEncaminhamentoOutro = normalizar(origemEncaminhamentoOutro);
        motivoPrincipal = normalizar(motivoPrincipal);
        demandaEstudante = normalizar(demandaEstudante);
        demandaFamiliaResponsavel = normalizar(demandaFamiliaResponsavel);
        demandaEquipeEscolar = normalizar(demandaEquipeEscolar);
        deficienciaNecessidadeInformada = normalizar(deficienciaNecessidadeInformada);
        docsComprobatoriosFlagQuais = normalizar(docsComprobatoriosFlagQuais);
        condicaoAssociadaFlagQuais = normalizar(condicaoAssociadaFlagQuais);
        medicacaoContinuaFlagQuais = normalizar(medicacaoContinuaFlagQuais);
        if (!"Outro".equals(origemEncaminhamento)) origemEncaminhamentoOutro = null;
    }

    @AssertTrue(message = "Descreva a origem quando selecionar Outro.")
    public boolean isOrigemOutroInformada() {
        return !"Outro".equals(origemEncaminhamento) || origemEncaminhamentoOutro != null;
    }

    private static String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.strip();
    }
}
