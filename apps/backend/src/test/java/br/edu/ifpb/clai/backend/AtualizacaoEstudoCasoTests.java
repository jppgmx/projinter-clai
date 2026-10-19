package br.edu.ifpb.clai.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@EnabledIfEnvironmentVariable(named = "CLAI_TEST_DATABASE_URL", matches = "jdbc:postgresql:.*")
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AtualizacaoEstudoCasoTests extends PostgresIntegrationSupport {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    private Integer id;

    @BeforeEach
    void preparar() {
        jdbc.update("DELETE FROM estudo_de_caso");
        jdbc.update("DELETE FROM estudante");
        jdbc.update("DELETE FROM curso");
        jdbc.update("DELETE FROM profissional");
        jdbc.update("DELETE FROM setor");
        jdbc.update("INSERT INTO setor (id_setor, sigla_nome) VALUES (1, 'CLAI')");
        for (int numero = 1; numero <= 3; numero++) {
            jdbc.update("INSERT INTO profissional (id_profissional, fk_id_setor, nome_profissional, papel_cargo) VALUES (?, 1, ?, 'Docente')",
                    numero, "Profissional " + numero);
        }
        jdbc.update("INSERT INTO curso (id_curso, nome_curso) VALUES (1, 'Informática')");
        jdbc.update("""
                INSERT INTO estudante (matricula, fk_id_curso, turma, nome_estudante, periodo_ingresso, data_nascimento_idade)
                VALUES ('2026001', 1, 'A', 'Ana Silva', '2026.1', '15/04/2005 (21 anos)')
                """);
        id = jdbc.queryForObject("""
                INSERT INTO estudo_de_caso (fk_matricula_estudante, fk_profissional_responsavel,
                fk_psicopedagogo_assinatura, fk_coordenador_clai_assinatura,
                periodo_letivo, datas_atendimento_anamnese, origem_encaminhamento, motivo_principal,
                deficiencia_necessidade_informada, docs_comprobatorios_flag_quais,
                condicao_associada_flag_quais, medicacao_continua_flag_quais,
                diagnosticos, historico_escolar, demanda_estudante, assinatura_coordenador_clai)
                VALUES ('2026001', 1, 2, 3, '2026.2', '18/10/2026', 'Docente', 'Motivo inicial',
                'Necessidade relatada', 'Não informado', 'Não informado', 'Não informado',
                'Informação preexistente', 'Histórico inicial', 'Demanda inicial', 'Assinatura anterior')
                RETURNING id_estudo_caso
                """, Integer.class);
    }

    @Test
    void carregaTodosOsDadosParaEdicao() throws Exception {
        mvc.perform(get("/api/estudos-caso/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.idEstudoCaso").value(id))
                .andExpect(jsonPath("$.matriculaEstudante").value("2026001"))
                .andExpect(jsonPath("$.nomeEstudante").value("Ana Silva"))
                .andExpect(jsonPath("$.campos.historicoEscolar").value("Histórico inicial"))
                .andExpect(jsonPath("$.campos.assinaturaCoordenadorClai").value("Assinatura anterior"))
                .andExpect(jsonPath("$.profissionais.idPsicopedagogoAssinatura").value(2))
                .andExpect(jsonPath("$.editavel").value(true));
    }

    @Test
    void atualizaSomenteCamposEnviadosEPreservaDemaisDados() throws Exception {
        mvc.perform(put("/api/estudos-caso/{id}", id).contentType(MediaType.APPLICATION_JSON).content("""
                {"campos":{"historicoEscolar":" Histórico atualizado ","demandaEstudante":"   "},
                 "profissionais":{"idProfissionalResponsavel":2,"idCoordenadorClaiAssinatura":null}}
                """)).andExpect(status().isOk())
                .andExpect(jsonPath("$.campos.historicoEscolar").value("Histórico atualizado"))
                .andExpect(jsonPath("$.campos.diagnosticos").value("Informação preexistente"))
                .andExpect(jsonPath("$.campos.motivoPrincipal").value("Motivo inicial"))
                .andExpect(jsonPath("$.profissionais.idProfissionalResponsavel").value(2));
        assertThat(jdbc.queryForMap("SELECT * FROM estudo_de_caso WHERE id_estudo_caso = ?", id))
                .containsEntry("demanda_estudante", null)
                .containsEntry("fk_coordenador_clai_assinatura", null)
                .containsEntry("fk_matricula_estudante", "2026001")
                .containsEntry("arquivado", false)
                .containsEntry("trancado", false)
                .containsEntry("data_fechamento", null);
        assertThat(jdbc.queryForObject("SELECT nome_estudante FROM estudante", String.class)).isEqualTo("Ana Silva");
    }

    @Test
    void validacaoNaoGravaNenhumaAlteracao() throws Exception {
        for (String dados : new String[]{
                "{\"campos\":{\"motivoPrincipal\":\" \"}}",
                "{\"campos\":{\"origemEncaminhamento\":\"Outro\"}}",
                "{\"campos\":{\"medicacaoContinuaFlagQuais\":\"Sim:\"}}",
                "{\"campos\":{\"periodoLetivo\":\"" + "1".repeat(21) + "\"}}",
                "{\"profissionais\":{\"idProfissionalResponsavel\":null}}",
                "{\"profissionais\":{\"idPsicopedagogoAssinatura\":999}}",
                "{}", "{invalido"}) {
            mvc.perform(put("/api/estudos-caso/{id}", id).contentType(MediaType.APPLICATION_JSON).content(dados))
                    .andExpect(status().isBadRequest());
        }
        assertThat(jdbc.queryForObject("SELECT motivo_principal FROM estudo_de_caso WHERE id_estudo_caso=?", String.class, id))
                .isEqualTo("Motivo inicial");
    }

    @Test
    void naoPermiteMudarVinculoOuSituacaoPeloFormulario() throws Exception {
        for (String campo : new String[]{"matriculaEstudante", "dataFechamento", "arquivado", "trancado", "motivoArquivamento"}) {
            String dados = "{\"campos\":{\"" + campo + "\":\"valor\"}}";
            mvc.perform(put("/api/estudos-caso/{id}", id).contentType(MediaType.APPLICATION_JSON).content(dados))
                    .andExpect(status().isBadRequest());
        }
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM estudo_de_caso", Integer.class)).isEqualTo(1);
    }

    @Test
    void impedeEdicaoDeEstudoArquivadoTrancadoOuFinalizado() throws Exception {
        for (String situacao : new String[]{"arquivado = TRUE", "trancado = TRUE", "data_fechamento = DATE '2026-10-18'"}) {
            jdbc.update("UPDATE estudo_de_caso SET arquivado=FALSE, trancado=FALSE, data_fechamento=NULL WHERE id_estudo_caso=?", id);
            jdbc.update("UPDATE estudo_de_caso SET " + situacao + " WHERE id_estudo_caso=?", id);
            mvc.perform(get("/api/estudos-caso/{id}", id)).andExpect(status().isOk())
                    .andExpect(jsonPath("$.editavel").value(false));
            mvc.perform(put("/api/estudos-caso/{id}", id).contentType(MediaType.APPLICATION_JSON)
                            .content("{\"campos\":{\"motivoPrincipal\":\"Tentativa\"}}"))
                    .andExpect(status().isConflict());
            assertThat(jdbc.queryForObject("SELECT motivo_principal FROM estudo_de_caso WHERE id_estudo_caso=?", String.class, id))
                    .isEqualTo("Motivo inicial");
        }
    }

    @Test
    void trataTextoComoDadoSemExecutarSql() throws Exception {
        String texto = "teste', arquivado = TRUE --";
        mvc.perform(put("/api/estudos-caso/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"campos\":{\"historicoEscolar\":\"" + texto + "\"}}"))
                .andExpect(status().isOk());
        assertThat(jdbc.queryForMap("SELECT historico_escolar, arquivado FROM estudo_de_caso WHERE id_estudo_caso=?", id))
                .containsEntry("historico_escolar", texto).containsEntry("arquivado", false);
    }

    @Test
    void estudoInexistenteNaoEhCriadoPeloPut() throws Exception {
        mvc.perform(get("/api/estudos-caso/2147483647")).andExpect(status().isNotFound());
        mvc.perform(put("/api/estudos-caso/2147483647").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"campos\":{\"motivoPrincipal\":\"Novo\"}}"))
                .andExpect(status().isNotFound());
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM estudo_de_caso", Integer.class)).isEqualTo(1);
    }

    @Test
    void serveInterfaceDeEdicao() throws Exception {
        mvc.perform(get("/editar-estudo-caso.html")).andExpect(status().isOk());
        mvc.perform(get("/editar-estudo-caso.js")).andExpect(status().isOk());
    }
}
