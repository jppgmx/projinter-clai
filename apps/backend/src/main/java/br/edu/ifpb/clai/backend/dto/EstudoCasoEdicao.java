package br.edu.ifpb.clai.backend.dto;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public record EstudoCasoEdicao(Integer idEstudoCaso, String matriculaEstudante, String nomeEstudante,
        Map<String, String> campos, Map<String, Integer> profissionais, boolean arquivado, boolean trancado,
        LocalDate dataFechamento, String status, boolean editavel, String motivoBloqueio) {
    public EstudoCasoEdicao {
        campos = Collections.unmodifiableMap(new LinkedHashMap<>(campos));
        profissionais = Collections.unmodifiableMap(new LinkedHashMap<>(profissionais));
    }

    public static EstudoCasoEdicao de(Integer id, String matricula, String nome,
            Map<String, String> campos, Map<String, Integer> profissionais,
            boolean arquivado, boolean trancado, LocalDate dataFechamento) {
        String motivo = arquivado ? "Estudo de caso arquivado não pode ser alterado."
                : trancado ? "Estudo de caso trancado não pode ser alterado."
                : dataFechamento != null ? "Estudo de caso finalizado não pode ser alterado." : null;
        String status = dataFechamento != null ? "FINALIZADO" : trancado ? "TRANCADO" : "EM_ANDAMENTO";
        return new EstudoCasoEdicao(id, matricula, nome, campos, profissionais, arquivado, trancado,
                dataFechamento, status, motivo == null, motivo);
    }
}
