package br.edu.ifpb.clai.backend;

import br.edu.ifpb.clai.backend.entidade.ArquivoMedico;
import br.edu.ifpb.clai.backend.exception.DocumentoInvalidoException;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import static org.assertj.core.api.Assertions.*;

class ArquivoMedicoTests {
    @Test
    void normalizaNomeSemUsarCaminhosDoCliente() {
        var arquivo = ArquivoMedico.validar("C:\\pasta\\laudo.pdf", "%PDF-1.7\nconteudo".getBytes(StandardCharsets.UTF_8));
        assertThat(arquivo.nome()).isEqualTo("laudo.pdf");
        assertThat(arquivo.tipoMime()).isEqualTo("application/pdf");
    }
    @Test
    void rejeitaFormatoDisfarcadoEVazio() {
        assertThatThrownBy(() -> ArquivoMedico.validar("laudo.pdf", "<script>alert(1)</script>".getBytes(StandardCharsets.UTF_8))).isInstanceOf(DocumentoInvalidoException.class);
        assertThatThrownBy(() -> ArquivoMedico.validar("laudo.png", new byte[0])).isInstanceOf(DocumentoInvalidoException.class);
        assertThatThrownBy(() -> ArquivoMedico.validar("arquivo.exe", new byte[]{1,2,3})).isInstanceOf(DocumentoInvalidoException.class);
    }
    @Test
    void rejeitaLimiteExcedido() {
        assertThatThrownBy(() -> ArquivoMedico.validar("laudo.pdf", new byte[ArquivoMedico.TAMANHO_MAXIMO + 1])).isInstanceOf(DocumentoInvalidoException.class);
    }
    @Test
    void docxExigeEstruturaDeDocumentoWord() throws Exception {
        var bytes = new ByteArrayOutputStream();
        try (var zip = new ZipOutputStream(bytes)) {
            zip.putNextEntry(new ZipEntry("qualquer.txt")); zip.write(1); zip.closeEntry();
        }
        assertThatThrownBy(() -> ArquivoMedico.validar("falso.docx", bytes.toByteArray())).isInstanceOf(DocumentoInvalidoException.class);
        bytes.reset();
        try (var zip = new ZipOutputStream(bytes)) {
            for (String nome : new String[]{"[Content_Types].xml", "word/document.xml"}) {
                zip.putNextEntry(new ZipEntry(nome)); zip.write("<xml/>".getBytes(StandardCharsets.UTF_8)); zip.closeEntry();
            }
        }
        assertThat(ArquivoMedico.validar("laudo.docx", bytes.toByteArray()).tipoMime()).contains("wordprocessingml");
    }
}
