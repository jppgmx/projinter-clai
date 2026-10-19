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
class AberturaEstudoCasoTests extends PostgresIntegrationSupport {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;

    private static final String ABERTURA = """
            {"matriculaEstudante":" 2026001 ","idProfissionalResponsavel":1,
             "periodoLetivo":" 2026.2 ","datasAtendimentoAnamnese":"18/10/2026",
             "origemEncaminhamento":"Docente","motivoPrincipal":" Acompanhamento inicial ",
             "demandaEstudante":"Apoio pedagógico","demandaFamiliaResponsavel":" ",
             "deficienciaNecessidadeInformada":"Necessidade informada pelo estudante",
             "docsComprobatoriosFlagQuais":"Não informado",
             "condicaoAssociadaFlagQuais":"Não informado","medicacaoContinuaFlagQuais":"Não informado"}
            """;

    @BeforeEach
    void preparar() {
        jdbc.update("DELETE FROM estudo_de_caso");
        jdbc.update("DELETE FROM estudante");
        jdbc.update("DELETE FROM curso");
        jdbc.update("DELETE FROM profissional");
        jdbc.update("DELETE FROM setor");
        jdbc.update("INSERT INTO setor (id_setor, sigla_nome) VALUES (1, 'CLAI')");
        jdbc.update("INSERT INTO profissional (id_profissional, fk_id_setor, nome_profissional, papel_cargo) VALUES (1, 1, 'Responsável teste', 'Docente')");
        jdbc.update("INSERT INTO curso (id_curso, nome_curso) VALUES (1, 'Informática')");
        jdbc.update("""
                INSERT INTO estudante (matricula, fk_id_curso, turma, nome_estudante, periodo_ingresso, data_nascimento_idade)
                VALUES ('2026001', 1, 'A', 'Ana Silva', '2026.1', '15/04/2005 (21 anos)')
                """);
    }

    @Test
    void abreEstudoComReferenciasExistentesESemAlterarCadastro() throws Exception {
        mvc.perform(post("/api/estudos-caso").contentType(MediaType.APPLICATION_JSON).content(ABERTURA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idEstudoCaso").isNumber())
                .andExpect(jsonPath("$.matriculaEstudante").value("2026001"))
                .andExpect(jsonPath("$.periodoLetivo").value("2026.2"))
                .andExpect(jsonPath("$.profissionalResponsavel.idProfissional").value(1))
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"))
                .andExpect(jsonPath("$.statusDescricao").value("Em andamento"));
        assertThat(jdbc.queryForMap("SELECT * FROM estudo_de_caso"))
                .containsEntry("fk_matricula_estudante", "2026001")
                .containsEntry("fk_profissional_responsavel", 1)
                .containsEntry("motivo_principal", "Acompanhamento inicial")
                .containsEntry("demanda_familia_responsavel", null)
                .containsEntry("docs_comprobatorios_flag_quais", "Não informado")
                .containsEntry("data_fechamento", null)
                .containsEntry("arquivado", false);
        assertThat(jdbc.queryForObject("SELECT nome_estudante FROM estudante", String.class)).isEqualTo("Ana Silva");
        mvc.perform(get("/api/estudantes/2026001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estudosDeCaso.length()").value(1))
                .andExpect(jsonPath("$.estudosDeCaso[0].motivoPrincipal").value("Acompanhamento inicial"));
    }

    @Test
    void validaTodosOsObrigatoriosDoSchema() throws Exception {
        mvc.perform(post("/api/estudos-caso").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.matriculaEstudante").exists())
                .andExpect(jsonPath("$.campos.idProfissionalResponsavel").exists())
                .andExpect(jsonPath("$.campos.periodoLetivo").exists())
                .andExpect(jsonPath("$.campos.datasAtendimentoAnamnese").exists())
                .andExpect(jsonPath("$.campos.origemEncaminhamento").exists())
                .andExpect(jsonPath("$.campos.motivoPrincipal").exists())
                .andExpect(jsonPath("$.campos.deficienciaNecessidadeInformada").exists())
                .andExpect(jsonPath("$.campos.docsComprobatoriosFlagQuais").exists())
                .andExpect(jsonPath("$.campos.condicaoAssociadaFlagQuais").exists())
                .andExpect(jsonPath("$.campos.medicacaoContinuaFlagQuais").exists());
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM estudo_de_caso", Integer.class)).isZero();
    }

    @Test
    void outroExigeDescricaoEOrigensDevemRespeitarSchema() throws Exception {
        mvc.perform(post("/api/estudos-caso").contentType(MediaType.APPLICATION_JSON)
                        .content(ABERTURA.replace("Docente", "Outro")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.origemEncaminhamentoOutro").exists());
        mvc.perform(post("/api/estudos-caso").contentType(MediaType.APPLICATION_JSON)
                        .content(ABERTURA.replace("Docente", "Inexistente")))
                .andExpect(status().isBadRequest());
        String dados = ABERTURA.replace("Docente", "Outro").replace(
                "\"motivoPrincipal\":", "\"origemEncaminhamentoOutro\":\"Serviço externo\",\"motivoPrincipal\":");
        mvc.perform(post("/api/estudos-caso").contentType(MediaType.APPLICATION_JSON).content(dados))
                .andExpect(status().isCreated());
        assertThat(jdbc.queryForObject("SELECT origem_encaminhamento_outro FROM estudo_de_caso", String.class))
                .isEqualTo("Serviço externo");
    }

    @Test
    void referenciasInexistentesNaoCriamRegistros() throws Exception {
        mvc.perform(post("/api/estudos-caso").contentType(MediaType.APPLICATION_JSON)
                        .content(ABERTURA.replace("2026001", "9999999")))
                .andExpect(status().isNotFound());
        mvc.perform(post("/api/estudos-caso").contentType(MediaType.APPLICATION_JSON)
                        .content(ABERTURA.replace("\"idProfissionalResponsavel\":1", "\"idProfissionalResponsavel\":99")))
                .andExpect(status().isBadRequest());
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM estudo_de_caso", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM estudante", Integer.class)).isEqualTo(1);
    }

    @Test
    void simExigeDetalhesComoNaReferencia() throws Exception {
        for (String campo : new String[]{"docsComprobatoriosFlagQuais", "condicaoAssociadaFlagQuais",
                "medicacaoContinuaFlagQuais"}) {
            String dados = ABERTURA.replace("\"" + campo + "\":\"Não informado\"", "\"" + campo + "\":\"Sim: --\"");
            mvc.perform(post("/api/estudos-caso").contentType(MediaType.APPLICATION_JSON).content(dados))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.campos." + campo).exists());
        }
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM estudo_de_caso", Integer.class)).isZero();
    }

    @Test
    void validaLimitesIdentificadoresEJson() throws Exception {
        for (String dados : new String[]{ABERTURA.replace(" 2026.2 ", "1".repeat(21)),
                ABERTURA.replace("\"idProfissionalResponsavel\":1", "\"idProfissionalResponsavel\":0"), "{invalido"}) {
            mvc.perform(post("/api/estudos-caso").contentType(MediaType.APPLICATION_JSON).content(dados))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void profissionaisExistentesESemResultados() throws Exception {
        mvc.perform(get("/api/estudos-caso/profissionais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProfissional").value(1))
                .andExpect(jsonPath("$[0].nomeProfissional").value("Responsável teste"));
        jdbc.update("DELETE FROM profissional");
        mvc.perform(get("/api/estudos-caso/profissionais"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test
    void serveFormularioSemCss() throws Exception {
        mvc.perform(get("/abertura-estudo-caso.html")).andExpect(status().isOk());
        mvc.perform(get("/abertura-estudo-caso.js")).andExpect(status().isOk());
    }
}
