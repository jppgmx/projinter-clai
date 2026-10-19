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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@EnabledIfEnvironmentVariable(named = "CLAI_TEST_DATABASE_URL", matches = "jdbc:postgresql:.*")
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class RegistroEstudanteTests extends PostgresIntegrationSupport {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;

    private static final String CADASTRO = """
            {"matricula":" 202610001 ","nomeEstudante":" Ana Silva ",
             "curso":{"idCurso":1},"turma":" A ","periodoIngresso":"2026.1",
             "dataNascimentoIdade":"15/04/2005 (21 anos)","emailEstudante":"   "}
            """;

    @BeforeEach
    void preparar() {
        jdbc.update("DELETE FROM estudante");
        jdbc.update("DELETE FROM curso");
        jdbc.update("INSERT INTO curso (id_curso, nome_curso) VALUES (1, 'Informática')");
    }

    @Test
    void registraComMapeamentoDoSchemaENormalizacao() throws Exception {
        mvc.perform(post("/api/estudantes").contentType(MediaType.APPLICATION_JSON).content(CADASTRO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricula").value("202610001"))
                .andExpect(jsonPath("$.curso.idCurso").value(1))
                .andExpect(jsonPath("$.curso.nomeCurso").value("Informática"));
        assertThat(jdbc.queryForMap("SELECT * FROM estudante WHERE matricula = '202610001'"))
                .containsEntry("nome_estudante", "Ana Silva")
                .containsEntry("turma", "A")
                .containsEntry("fk_id_curso", 1)
                .containsEntry("data_nascimento_idade", "15/04/2005 (21 anos)")
                .containsEntry("email_estudante", null);
    }

    @Test
    void preservaCamposOpcionaisEResponsavel() throws Exception {
        String dados = CADASTRO.replace("\"emailEstudante\":\"   \"", """
                "emailEstudante":"ana@example.org","telefoneEstudante":"(83) 99999-1111",
                "nomeResponsavel":"Maria Silva","grauParentesco":"Mãe",
                "telefoneResponsavel":"(83) 99999-2222","emailResponsavel":"maria@example.org"
                """);
        mvc.perform(post("/api/estudantes").contentType(MediaType.APPLICATION_JSON).content(dados))
                .andExpect(status().isCreated());
        assertThat(jdbc.queryForMap("SELECT * FROM estudante"))
                .containsEntry("email_estudante", "ana@example.org")
                .containsEntry("telefone_estudante", "(83) 99999-1111")
                .containsEntry("nome_responsavel", "Maria Silva")
                .containsEntry("grau_parentesco", "Mãe")
                .containsEntry("telefone_responsavel", "(83) 99999-2222")
                .containsEntry("email_responsavel", "maria@example.org");
    }

    @Test
    void naoSobrescreveMatriculaDuplicada() throws Exception {
        mvc.perform(post("/api/estudantes").contentType(MediaType.APPLICATION_JSON).content(CADASTRO))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/estudantes").contentType(MediaType.APPLICATION_JSON)
                        .content(CADASTRO.replace("Ana Silva", "Outro nome")))
                .andExpect(status().isConflict());
        assertThat(jdbc.queryForObject("SELECT nome_estudante FROM estudante", String.class)).isEqualTo("Ana Silva");
    }

    @Test
    void duplicidadeSimultaneaNaoAtualizaCadastro() throws Exception {
        var partida = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var tarefas = List.of(CADASTRO, CADASTRO.replace("Ana Silva", "Outro nome")).stream()
                    .map(dados -> executor.submit(() -> {
                        partida.await();
                        return mvc.perform(post("/api/estudantes").contentType(MediaType.APPLICATION_JSON)
                                .content(dados)).andReturn().getResponse().getStatus();
                    })).toList();
            partida.countDown();
            assertThat(List.of(tarefas.get(0).get(10, TimeUnit.SECONDS), tarefas.get(1).get(10, TimeUnit.SECONDS)))
                    .containsExactlyInAnyOrder(201, 409);
        }
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM estudante", Integer.class)).isEqualTo(1);
    }

    @Test
    void rejeitaCursoInexistente() throws Exception {
        mvc.perform(post("/api/estudantes").contentType(MediaType.APPLICATION_JSON)
                        .content(CADASTRO.replace("\"idCurso\":1", "\"idCurso\":99")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.erro").value("Curso informado não encontrado."));
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM estudante", Integer.class)).isZero();
    }

    @Test
    void validaObrigatorios() throws Exception {
        mvc.perform(post("/api/estudantes").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.matricula").exists())
                .andExpect(jsonPath("$.campos.nomeEstudante").exists())
                .andExpect(jsonPath("$.campos.turma").exists())
                .andExpect(jsonPath("$.campos.periodoIngresso").exists())
                .andExpect(jsonPath("$.campos.dataNascimentoIdade").exists())
                .andExpect(jsonPath("$.campos.curso").exists());
    }

    @Test
    void validaLimitesEmailsECursoSemId() throws Exception {
        for (String dados : List.of(
                CADASTRO.replace(" 202610001 ", "1".repeat(31)),
                CADASTRO.replace("Ana Silva", " "),
                CADASTRO.replace("\"emailEstudante\":\"   \"", "\"emailEstudante\":\"invalido\""),
                CADASTRO.replace("\"emailEstudante\":\"   \"", "\"emailResponsavel\":\"invalido\""),
                CADASTRO.replace("{\"idCurso\":1}", "{}"),
                CADASTRO.replace("\"idCurso\":1", "\"idCurso\":0"))) {
            mvc.perform(post("/api/estudantes").contentType(MediaType.APPLICATION_JSON).content(dados))
                    .andExpect(status().isBadRequest());
        }
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM estudante", Integer.class)).isZero();
    }

    @Test
    void rejeitaJsonInvalido() throws Exception {
        mvc.perform(post("/api/estudantes").contentType(MediaType.APPLICATION_JSON).content("{invalido"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.erro").exists());
    }

    @Test
    void consultaCursosParaPreencherFormulario() throws Exception {
        mvc.perform(get("/api/cursos")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCurso").value(1));
        jdbc.update("DELETE FROM curso");
        mvc.perform(get("/api/cursos")).andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test
    void serveFormularioEAssetsNaMesmaOrigem() throws Exception {
        mvc.perform(get("/cadastro-estudante.html")).andExpect(status().isOk());
        mvc.perform(get("/cadastro-estudante.js")).andExpect(status().isOk());
    }
}
