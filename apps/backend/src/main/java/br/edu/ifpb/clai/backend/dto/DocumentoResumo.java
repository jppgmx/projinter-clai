package br.edu.ifpb.clai.backend.dto;

import java.time.LocalDateTime;

public record DocumentoResumo(Integer idDocumento, String nomeArquivo, String tipoMime,
        long tamanhoBytes, LocalDateTime dataUpload) {
}
