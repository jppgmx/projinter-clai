package br.edu.ifpb.clai.backend.exception;

public class EstudanteNaoEncontradoException extends RuntimeException {
    public EstudanteNaoEncontradoException() {
        super("Estudante não encontrado.");
    }
}
