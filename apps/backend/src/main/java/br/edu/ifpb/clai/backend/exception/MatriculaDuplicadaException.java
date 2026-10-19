package br.edu.ifpb.clai.backend.exception;

public class MatriculaDuplicadaException extends RuntimeException {
    public MatriculaDuplicadaException() {
        super("Matrícula já cadastrada.");
    }
}
