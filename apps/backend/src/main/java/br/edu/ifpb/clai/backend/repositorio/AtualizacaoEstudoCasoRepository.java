package br.edu.ifpb.clai.backend.repositorio;

import br.edu.ifpb.clai.backend.dto.EstudoCasoEdicao;
import br.edu.ifpb.clai.backend.dto.ResumoEstudoCaso;
import java.util.List;
import br.edu.ifpb.clai.backend.entidade.CampoEstudoCaso;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class AtualizacaoEstudoCasoRepository {
    private static final String COLUNAS = Arrays.stream(CampoEstudoCaso.values())
            .map(campo -> "ec." + campo.coluna()).collect(Collectors.joining(", "));
    private static final String SELECT = """
            select ec.id_estudo_caso, ec.fk_matricula_estudante, e.nome_estudante,
                   ec.fk_profissional_responsavel, ec.fk_psicopedagogo_assinatura,
                   ec.fk_coordenador_clai_assinatura, ec.data_fechamento,
                   coalesce((to_jsonb(ec)->>'arquivado')::boolean, false) as estudo_arquivado,
                   coalesce((to_jsonb(ec)->>'trancado')::boolean, false) as estudo_trancado,
            """ + COLUNAS + """
             from estudo_de_caso ec join estudante e on e.matricula = ec.fk_matricula_estudante
             where ec.id_estudo_caso = ?
            """;
    private final JdbcTemplate jdbc;

    public AtualizacaoEstudoCasoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ResumoEstudoCaso> listar(String matricula) {
        String sql = """
                select ec.id_estudo_caso, ec.fk_matricula_estudante, e.nome_estudante,
                       ec.periodo_letivo, ec.data_fechamento,
                       coalesce((to_jsonb(ec)->>'arquivado')::boolean, false) as estudo_arquivado,
                       coalesce((to_jsonb(ec)->>'trancado')::boolean, false) as estudo_trancado
                from estudo_de_caso ec join estudante e on e.matricula = ec.fk_matricula_estudante
                """;
        Object[] parametros = matricula == null ? new Object[0] : new Object[] { matricula };
        if (matricula != null) sql += " where ec.fk_matricula_estudante = ?";
        sql += " order by ec.id_estudo_caso desc";
        return jdbc.query(sql, (rs, numero) -> {
            boolean arquivado = rs.getBoolean("estudo_arquivado");
            boolean trancado = rs.getBoolean("estudo_trancado");
            boolean finalizado = rs.getObject("data_fechamento", LocalDate.class) != null;
            String status = arquivado ? "ARQUIVADO" : trancado ? "TRANCADO"
                    : finalizado ? "FINALIZADO" : "EM_ANDAMENTO";
            return new ResumoEstudoCaso(rs.getInt("id_estudo_caso"),
                    rs.getString("fk_matricula_estudante"), rs.getString("nome_estudante"),
                    rs.getString("periodo_letivo"), status, !arquivado && !trancado && !finalizado);
        }, parametros);
    }

    public Optional<EstudoCasoEdicao> buscar(Integer id, boolean bloquearParaAtualizacao) {
        String sql = SELECT + (bloquearParaAtualizacao ? " for update of ec" : "");
        return jdbc.query(sql, (rs, numero) -> {
            Map<String, String> campos = new LinkedHashMap<>();
            for (CampoEstudoCaso campo : CampoEstudoCaso.values()) {
                campos.put(campo.chave(), rs.getString(campo.coluna()));
            }
            Map<String, Integer> profissionais = new LinkedHashMap<>();
            profissionais.put("idProfissionalResponsavel", rs.getObject("fk_profissional_responsavel", Integer.class));
            profissionais.put("idPsicopedagogoAssinatura", rs.getObject("fk_psicopedagogo_assinatura", Integer.class));
            profissionais.put("idCoordenadorClaiAssinatura", rs.getObject("fk_coordenador_clai_assinatura", Integer.class));
            return EstudoCasoEdicao.de(rs.getInt("id_estudo_caso"), rs.getString("fk_matricula_estudante"),
                    rs.getString("nome_estudante"), campos, profissionais, rs.getBoolean("estudo_arquivado"),
                    rs.getBoolean("estudo_trancado"), rs.getObject("data_fechamento", LocalDate.class));
        }, id).stream().findFirst();
    }

    public int atualizar(Integer id, Map<String, String> campos, Map<String, Integer> profissionais) {
        // Nomes das colunas vêm apenas do enum; nenhum nome recebido do cliente vira SQL.
        String atribuicoes = Arrays.stream(CampoEstudoCaso.values())
                .map(campo -> campo.coluna() + " = ?").collect(Collectors.joining(", "));
        var valores = new ArrayList<Object>();
        for (CampoEstudoCaso campo : CampoEstudoCaso.values()) valores.add(campos.get(campo.chave()));
        valores.add(profissionais.get("idProfissionalResponsavel"));
        valores.add(profissionais.get("idPsicopedagogoAssinatura"));
        valores.add(profissionais.get("idCoordenadorClaiAssinatura"));
        valores.add(id);
        String sql = "update estudo_de_caso ec set " + atribuicoes + """
                , fk_profissional_responsavel = ?, fk_psicopedagogo_assinatura = ?,
                  fk_coordenador_clai_assinatura = ?
                where ec.id_estudo_caso = ? and ec.data_fechamento is null
                  and not coalesce((to_jsonb(ec)->>'arquivado')::boolean, false)
                  and not coalesce((to_jsonb(ec)->>'trancado')::boolean, false)
                """;
        return jdbc.update(sql, valores.toArray());
    }
}
