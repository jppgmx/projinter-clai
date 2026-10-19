package br.edu.ifpb.clai.backend.repositorio;

import br.edu.ifpb.clai.backend.dto.DocumentoResumo;
import br.edu.ifpb.clai.backend.dto.DocumentoConteudo;
import br.edu.ifpb.clai.backend.entidade.ArquivoMedico;
import br.edu.ifpb.clai.backend.exception.DocumentoEstruturaException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class DocumentoMedicoRepository {
    private final JdbcTemplate jdbc;
    public DocumentoMedicoRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    private void exigirTabela() {
        if (!Boolean.TRUE.equals(jdbc.queryForObject("select to_regclass('documento_medico') is not null", Boolean.class))) {
            throw new DocumentoEstruturaException();
        }
    }
    public List<DocumentoResumo> listar(Integer estudo) {
        exigirTabela();
        return jdbc.query("""
                select id_documento, nome_arquivo, tipo_mime, tamanho_bytes, data_upload
                from documento_medico where fk_id_estudo_caso = ? order by id_documento
                """, (rs, n) -> new DocumentoResumo(rs.getInt("id_documento"), rs.getString("nome_arquivo"),
                rs.getString("tipo_mime"), rs.getLong("tamanho_bytes"), rs.getObject("data_upload", LocalDateTime.class)), estudo);
    }
    public void salvar(Integer estudo, ArquivoMedico arquivo) {
        exigirTabela();
        jdbc.update("""
                insert into documento_medico (fk_id_estudo_caso, nome_arquivo, tipo_mime, tamanho_bytes, conteudo)
                values (?, ?, ?, ?, ?)
                """, estudo, arquivo.nome(), arquivo.tipoMime(), arquivo.conteudo().length, arquivo.conteudo());
    }
    public Optional<DocumentoConteudo> buscar(Integer estudo, Integer documento) {
        exigirTabela();
        return jdbc.query("""
                select nome_arquivo, tipo_mime, conteudo from documento_medico
                where id_documento = ? and fk_id_estudo_caso = ?
                """, (rs, n) -> new DocumentoConteudo(rs.getString("nome_arquivo"), rs.getString("tipo_mime"),
                rs.getBytes("conteudo")), documento, estudo).stream().findFirst();
    }
}
