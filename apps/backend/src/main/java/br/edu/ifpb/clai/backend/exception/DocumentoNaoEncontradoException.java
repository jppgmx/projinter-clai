package br.edu.ifpb.clai.backend.exception;

public class DocumentoNaoEncontradoException extends RuntimeException {
    public DocumentoNaoEncontradoException() { super("Documento não encontrado neste estudo de caso."); }
}
