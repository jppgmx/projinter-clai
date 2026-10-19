package br.edu.ifpb.clai.backend.dto;

public record ResumoEstudoCaso(Integer idEstudoCaso, String matriculaEstudante,
        String nomeEstudante, String periodoLetivo, String status, boolean editavel) {
}
