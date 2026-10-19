package br.edu.ifpb.clai.backend.exception;

public class CursoInvalidoException extends RuntimeException {
    public CursoInvalidoException() {
        super("Curso informado não encontrado.");
    }
}
