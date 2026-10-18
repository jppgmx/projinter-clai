package br.edu.ifpb.clai.backend.estudante;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record RegistroEstudante(
        @NotBlank(message = "Campo obrigatório.")
        @Size(max = 30, message = "Use no máximo 30 caracteres.")
        String matricula,
        @NotBlank(message = "Campo obrigatório.")
        @Size(max = 50, message = "Use no máximo 50 caracteres.")
        String turma,
        @NotBlank(message = "Campo obrigatório.")
        @Size(max = 150, message = "Use no máximo 150 caracteres.")
        String nomeEstudante,
        @NotBlank(message = "Campo obrigatório.")
        @Size(max = 20, message = "Use no máximo 20 caracteres.")
        String periodoIngresso,
        @NotBlank(message = "Campo obrigatório.")
        @Size(max = 60, message = "Use no máximo 60 caracteres.")
        String dataNascimentoIdade,
        @Size(max = 30, message = "Use no máximo 30 caracteres.")
        String telefoneEstudante,
        @Email(message = "Informe um e-mail válido.")
        @Size(max = 150, message = "Use no máximo 150 caracteres.")
        String emailEstudante,
        @Size(max = 150, message = "Use no máximo 150 caracteres.")
        String nomeResponsavel,
        @Size(max = 60, message = "Use no máximo 60 caracteres.")
        String grauParentesco,
        @Size(max = 30, message = "Use no máximo 30 caracteres.")
        String telefoneResponsavel,
        @Email(message = "Informe um e-mail válido.")
        @Size(max = 150, message = "Use no máximo 150 caracteres.")
        String emailResponsavel,
        @NotNull(message = "Selecione um curso.") @Valid CursoSelecionado curso) {
    public RegistroEstudante {
        matricula = normalizar(matricula);
        turma = normalizar(turma);
        nomeEstudante = normalizar(nomeEstudante);
        periodoIngresso = normalizar(periodoIngresso);
        dataNascimentoIdade = normalizar(dataNascimentoIdade);
        telefoneEstudante = normalizar(telefoneEstudante);
        emailEstudante = normalizar(emailEstudante);
        nomeResponsavel = normalizar(nomeResponsavel);
        grauParentesco = normalizar(grauParentesco);
        telefoneResponsavel = normalizar(telefoneResponsavel);
        emailResponsavel = normalizar(emailResponsavel);
    }

    private static String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.strip();
    }

    public record CursoSelecionado(
            @NotNull(message = "Selecione um curso.")
            @Positive(message = "Selecione um curso válido.") Integer idCurso) {
    }
}
