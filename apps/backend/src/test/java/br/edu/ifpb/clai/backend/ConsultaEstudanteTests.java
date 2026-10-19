package br.edu.ifpb.clai.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@EnabledIfEnvironmentVariable(named = "CLAI_TEST_DATABASE_URL", matches = "jdbc:postgresql:.*")
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ConsultaEstudanteTests extends PostgresIntegrationSupport {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;

    @BeforeEach
    void preparar() {
        jdbc.update("DELETE FROM estudo_de_caso");
        jdbc.update("DELETE FROM profissional");
        jdbc.update("DELETE FROM setor");
        jdbc.update("INSERT INTO setor (id_setor, sigla_nome) VALUES (1, 'CLAI')");
        jdbc.update("INSERT INTO profissional (id_profissional, fk_id_setor, nome_profissional, papel_cargo) VALUES (1, 1, 'Responsável teste', 'Docente')");
        jdbc.update("DELETE FROM estudante");
        jdbc.update("DELETE FROM curso");
        jdbc.update("INSERT INTO curso (id_curso, nome_curso) VALUES (1, 'Informática')");
        for (String[] estudante : new String[][]{{"2026001", "Ana Silva"}, {"2026002", "Bruno Souza"}}) {
            jdbc.update("""
                    INSERT INTO estudante (matricula, fk_id_curso, turma, nome_estudante,
                    periodo_ingresso, data_nascimento_idade, telefone_estudante, email_estudante,
                    nome_responsavel, grau_parentesco, telefone_responsavel, email_responsavel)
                    VALUES (?, 1, 'A', ?, '2026.1', '15/04/2005 (21 anos)', '83999991111',
                    'estudante@example.org', 'Maria Silva', 'Mãe', '83999992222', 'maria@example.org')
                    """, estudante[0], estudante[1]);
        }
    }

    @Test
    void listaRegistrosExistentesSemDadosDeContato() throws Exception {
        mvc.perform(get("/api/estudantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nomeEstudante").value("Ana Silva"))
                .andExpect(jsonPath("$[0].curso.nomeCurso").value("Informática"))
                .andExpect(jsonPath("$[0].emailEstudante").doesNotExist());
    }

    @Test
    void buscaPorNomeOuMatriculaSemDiferenciarMaiusculas() throws Exception {
        for (String busca : new String[]{" SILVA ", "2026001"}) {
            mvc.perform(get("/api/estudantes").param("busca", busca))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].matricula").value("2026001"));
        }
        for (String busca : new String[]{"inexistente", "%", "' OR 1=1 --"}) {
            mvc.perform(get("/api/estudantes").param("busca", busca))
                    .andExpect(status().isOk()).andExpect(content().json("[]"));
        }
    }

    @Test
    void detalhaCadastroCompletoSemEstudos() throws Exception {
        mvc.perform(get("/api/estudantes/2026001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value("2026001"))
                .andExpect(jsonPath("$.nomeEstudante").value("Ana Silva"))
                .andExpect(jsonPath("$.curso.idCurso").value(1))
                .andExpect(jsonPath("$.turma").value("A"))
                .andExpect(jsonPath("$.periodoIngresso").value("2026.1"))
                .andExpect(jsonPath("$.dataNascimentoIdade").value("15/04/2005 (21 anos)"))
                .andExpect(jsonPath("$.telefoneEstudante").value("83999991111"))
                .andExpect(jsonPath("$.emailEstudante").value("estudante@example.org"))
                .andExpect(jsonPath("$.nomeResponsavel").value("Maria Silva"))
                .andExpect(jsonPath("$.grauParentesco").value("Mãe"))
                .andExpect(jsonPath("$.telefoneResponsavel").value("83999992222"))
                .andExpect(jsonPath("$.emailResponsavel").value("maria@example.org"))
                .andExpect(jsonPath("$.estudosDeCaso").isEmpty());
    }

    @Test
    void historicoIncluiArquivadosSemMisturarEstudantesOuAlterarRegistros() throws Exception {
        inserirEstudo(10, "2026001", true);
        inserirEstudo(20, "2026001", false);
        inserirEstudo(30, "2026002", false);
        mvc.perform(get("/api/estudantes/2026001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estudosDeCaso.length()").value(2))
                .andExpect(jsonPath("$.estudosDeCaso[0].idEstudoCaso").value(20))
                .andExpect(jsonPath("$.estudosDeCaso[1].idEstudoCaso").value(10))
                .andExpect(jsonPath("$.estudosDeCaso[0].motivoPrincipal").value("Acompanhamento"))
                .andExpect(jsonPath("$.estudosDeCaso[0].demandaEstudante").value("Apoio"))
                .andExpect(jsonPath("$.estudosDeCaso[0].dataFechamento").value("2026-10-01"));
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM estudo_de_caso", Integer.class)).isEqualTo(3);
        assertThat(jdbc.queryForObject("SELECT arquivado FROM estudo_de_caso WHERE id_estudo_caso=10", Boolean.class)).isTrue();
    }

    @Test
    void retorna404ParaEstudanteInexistente() throws Exception {
        mvc.perform(get("/api/estudantes/inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Estudante não encontrado."));
    }

    @Test
    void disponibilizaPaginasDeConsultaSemCss() throws Exception {
        for (String arquivo : new String[]{"estudantes.html", "estudantes.js",
                "detalhes-estudante.html", "detalhes-estudante.js"}) {
            mvc.perform(get("/" + arquivo)).andExpect(status().isOk());
        }
    }

    private void inserirEstudo(int id, String matricula, boolean arquivado) {
        jdbc.update("""
                INSERT INTO estudo_de_caso (id_estudo_caso, fk_matricula_estudante, periodo_letivo,
                datas_atendimento_anamnese, origem_encaminhamento, motivo_principal,
                demanda_estudante, data_fechamento, arquivado, fk_profissional_responsavel,
                deficiencia_necessidade_informada, docs_comprobatorios_flag_quais,
                condicao_associada_flag_quais, medicacao_continua_flag_quais)
                VALUES (?, ?, '2026.1', '01/10/2026', 'CLAI', 'Acompanhamento', 'Apoio', DATE '2026-10-01', ?,
                1, 'Apoio pedagógico', 'Não informado', 'Não informado', 'Não informado')
                """, id, matricula, arquivado);
    }
}
