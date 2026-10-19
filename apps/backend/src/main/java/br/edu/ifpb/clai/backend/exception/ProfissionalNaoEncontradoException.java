package br.edu.ifpb.clai.backend.exception;

public class ProfissionalNaoEncontradoException extends RuntimeException {
    public ProfissionalNaoEncontradoException() {
        super("Profissional responsável não encontrado.");
    }
}
