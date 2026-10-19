package br.edu.ifpb.clai.backend.repositorio;

import br.edu.ifpb.clai.backend.dto.SituacaoEstudoCaso;
import br.edu.ifpb.clai.backend.exception.EstruturaSituacaoIndisponivelException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class SituacaoEstudoCasoRepository {
    private final JdbcTemplate jdbc;

    public SituacaoEstudoCasoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<SituacaoEstudoCaso> buscar(Integer id, boolean bloquear) {
        String sql = """
                select ec.id_estudo_caso, ec.fk_matricula_estudante, ec.data_fechamento,
                    coalesce((to_jsonb(ec)->>'arquivado')::boolean, false) as arquivado,
                    (to_jsonb(ec)->>'data_arquivamento')::timestamp as data_arquivamento,
                    to_jsonb(ec)->>'motivo_arquivamento' as motivo_arquivamento,
                    coalesce((to_jsonb(ec)->>'trancado')::boolean, false) as trancado,
                    (to_jsonb(ec)->>'data_trancamento')::timestamp as data_trancamento,
                    to_jsonb(ec)->>'justificativa_trancamento' as justificativa_trancamento
                from estudo_de_caso ec where ec.id_estudo_caso = ?
                """ + (bloquear ? " for update of ec" : "");
        return jdbc.query(sql, (rs, numero) -> new SituacaoEstudoCaso(
                rs.getInt("id_estudo_caso"), rs.getString("fk_matricula_estudante"),
                rs.getBoolean("arquivado"), rs.getObject("data_arquivamento", LocalDateTime.class),
                rs.getString("motivo_arquivamento"), rs.getBoolean("trancado"),
                rs.getObject("data_trancamento", LocalDateTime.class),
                rs.getString("justificativa_trancamento"), rs.getObject("data_fechamento", LocalDate.class)),
                id).stream().findFirst();
    }

    private void exigirColunas(List<String> obrigatorias) {
        List<String> existentes = jdbc.queryForList("""
                select attname from pg_attribute
                where attrelid = to_regclass('estudo_de_caso') and attnum > 0 and not attisdropped
                """, String.class);
        List<String> faltantes = obrigatorias.stream().filter(coluna -> !existentes.contains(coluna)).toList();
        if (!faltantes.isEmpty()) throw new EstruturaSituacaoIndisponivelException(faltantes);
    }

    public int arquivar(Integer id, String motivo) {
        exigirColunas(List.of("arquivado", "data_arquivamento", "motivo_arquivamento"));
        return jdbc.update("""
                update estudo_de_caso set arquivado = true,
                    data_arquivamento = current_timestamp, motivo_arquivamento = ?
                where id_estudo_caso = ? and not coalesce(arquivado, false)
                """, motivo, id);
    }

    public int desarquivar(Integer id) {
        exigirColunas(List.of("arquivado", "data_arquivamento", "motivo_arquivamento"));
        return jdbc.update("""
                update estudo_de_caso set arquivado = false,
                    data_arquivamento = null, motivo_arquivamento = null
                where id_estudo_caso = ? and coalesce(arquivado, false)
                """, id);
    }

    public int trancar(Integer id, String justificativa) {
        exigirColunas(List.of("trancado", "data_trancamento", "justificativa_trancamento"));
        return jdbc.update("""
                update estudo_de_caso ec set trancado = true,
                    data_trancamento = current_timestamp, justificativa_trancamento = ?
                where ec.id_estudo_caso = ? and not coalesce(ec.trancado, false)
                    and not coalesce((to_jsonb(ec)->>'arquivado')::boolean, false)
                    and ec.data_fechamento is null
                """, justificativa, id);
    }
}
