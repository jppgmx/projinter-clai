package br.edu.ifpb.clai.backend.exception;

public class EstudoCasoNaoEncontradoException extends RuntimeException {
    public EstudoCasoNaoEncontradoException() {
        super("Estudo de caso não encontrado.");
    }
}
