package br.edu.ifpb.clai.backend.repositorio;

import br.edu.ifpb.clai.backend.dto.HistoricoEstudoCaso;
import br.edu.ifpb.clai.backend.entidade.EstudoCaso;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public class EstudoCasoRepository {
    private final JdbcTemplate jdbc;
    private final EntityManager entityManager;

    public EstudoCasoRepository(JdbcTemplate jdbc, EntityManager entityManager) {
        this.jdbc = jdbc;
        this.entityManager = entityManager;
    }

    public void cadastrar(EstudoCaso estudo) {
        entityManager.persist(estudo);
        entityManager.flush();
    }

    public List<HistoricoEstudoCaso> consultarHistorico(String matricula) {
        // Consulta somente as colunas do histórico no schema existente.
        // Não filtra por situação: estudos arquivados também pertencem ao histórico.
        return jdbc.query("""
                select id_estudo_caso, periodo_letivo, datas_atendimento_anamnese,
                       origem_encaminhamento, origem_encaminhamento_outro, motivo_principal,
                       demanda_estudante, demanda_familia_responsavel, demanda_equipe_escolar,
                       data_fechamento
                from estudo_de_caso
                where fk_matricula_estudante = ?
                order by id_estudo_caso desc
                """, (rs, numero) -> new HistoricoEstudoCaso(
                rs.getInt("id_estudo_caso"), rs.getString("periodo_letivo"),
                rs.getString("datas_atendimento_anamnese"), rs.getString("origem_encaminhamento"),
                rs.getString("origem_encaminhamento_outro"), rs.getString("motivo_principal"),
                rs.getString("demanda_estudante"), rs.getString("demanda_familia_responsavel"),
                rs.getString("demanda_equipe_escolar"), rs.getObject("data_fechamento", LocalDate.class)),
                matricula);
    }
}
