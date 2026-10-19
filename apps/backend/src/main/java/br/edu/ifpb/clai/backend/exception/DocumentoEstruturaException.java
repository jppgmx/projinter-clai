package br.edu.ifpb.clai.backend.exception;

public class DocumentoEstruturaException extends RuntimeException {
    public DocumentoEstruturaException() {
        super("A tabela documento_medico prevista na referência não está disponível. A aplicação não cria tabelas automaticamente.");
    }
}
