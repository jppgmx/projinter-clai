package br.edu.ifpb.clai.backend.dto;

public record DocumentoConteudo(String nomeArquivo, String tipoMime, byte[] conteudo) {
}
