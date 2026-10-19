package br.edu.ifpb.clai.backend.exception;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class AtualizacaoEstudoCasoInvalidaException extends RuntimeException {
    private final Map<String, String> campos;

    public AtualizacaoEstudoCasoInvalidaException(Map<String, String> campos) {
        super("Revise os campos informados.");
        this.campos = Collections.unmodifiableMap(new LinkedHashMap<>(campos));
    }

    public Map<String, String> getCampos() { return campos; }
}
