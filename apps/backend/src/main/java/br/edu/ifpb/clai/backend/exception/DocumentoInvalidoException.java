package br.edu.ifpb.clai.backend.exception;

public class DocumentoInvalidoException extends RuntimeException {
    public DocumentoInvalidoException(String mensagem) { super(mensagem); }
}
