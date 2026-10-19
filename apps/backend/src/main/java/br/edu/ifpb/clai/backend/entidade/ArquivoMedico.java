package br.edu.ifpb.clai.backend.entidade;

import br.edu.ifpb.clai.backend.exception.DocumentoInvalidoException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipInputStream;

/** Valida os formatos aceitos pela referência sem depender de HTTP ou MultipartFile. */
public record ArquivoMedico(String nome, String tipoMime, byte[] conteudo) {
    public static final int TAMANHO_MAXIMO = 10 * 1024 * 1024;
    private static final Map<String, String> TIPOS = Map.of("pdf", "application/pdf", "jpg", "image/jpeg",
            "jpeg", "image/jpeg", "png", "image/png", "doc", "application/msword",
            "docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

    public static ArquivoMedico validar(String original, byte[] bytes) {
        if (bytes == null || bytes.length == 0) throw new DocumentoInvalidoException("Selecione um arquivo não vazio.");
        if (bytes.length > TAMANHO_MAXIMO) throw new DocumentoInvalidoException("Cada arquivo deve ter no máximo 10 MB.");
        String nome = original == null ? "" : original.replace('\\', '/');
        nome = nome.substring(nome.lastIndexOf('/') + 1).replaceAll("[\\p{Cntrl}]", "").strip();
        int ponto = nome.lastIndexOf('.');
        String ext = ponto < 0 ? "" : nome.substring(ponto + 1).toLowerCase(Locale.ROOT);
        if (nome.isBlank() || nome.length() > 255 || !TIPOS.containsKey(ext)) {
            throw new DocumentoInvalidoException("Use nome de até 255 caracteres e formato PDF, JPG, PNG, DOC ou DOCX.");
        }
        boolean valido = switch (ext) {
            case "pdf" -> comeca(bytes, 0x25, 0x50, 0x44, 0x46, 0x2D);
            case "jpg", "jpeg" -> comeca(bytes, 0xff, 0xd8, 0xff);
            case "png" -> comeca(bytes, 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a);
            case "doc" -> comeca(bytes, 0xd0, 0xcf, 0x11, 0xe0, 0xa1, 0xb1, 0x1a, 0xe1);
            case "docx" -> docx(bytes);
            default -> false;
        };
        if (!valido) throw new DocumentoInvalidoException("O conteúdo do arquivo não corresponde ao formato informado.");
        return new ArquivoMedico(nome, TIPOS.get(ext), bytes);
    }

    private static boolean comeca(byte[] bytes, int... assinatura) {
        if (bytes.length < assinatura.length) return false;
        for (int i = 0; i < assinatura.length; i++) if ((bytes[i] & 255) != assinatura[i]) return false;
        return true;
    }

    private static boolean docx(byte[] bytes) {
        if (!comeca(bytes, 0x50, 0x4b, 0x03, 0x04)) return false;
        boolean tipos = false, documento = false;
        try (var zip = new ZipInputStream(new ByteArrayInputStream(bytes))) {
            var buffer = new byte[8192];
            long total = 0;
            int entradas = 0;
            java.util.zip.ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (++entradas > 2000) return false;
                tipos |= "[Content_Types].xml".equals(entry.getName());
                documento |= "word/document.xml".equals(entry.getName());
                int lidos;
                while ((lidos = zip.read(buffer)) != -1) {
                    total += lidos;
                    if (total > 50L * 1024 * 1024) return false;
                }
            }
            return tipos && documento;
        } catch (IOException ex) { return false; }
    }
}
