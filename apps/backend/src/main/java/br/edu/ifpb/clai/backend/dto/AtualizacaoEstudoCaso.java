package br.edu.ifpb.clai.backend.dto;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Campos ausentes são preservados; valores explicitamente nulos limpam campos opcionais. */
public record AtualizacaoEstudoCaso(Map<String, String> campos, Map<String, Integer> profissionais) {
    public AtualizacaoEstudoCaso {
        campos = campos == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(campos));
        profissionais = profissionais == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(profissionais));
    }
}
