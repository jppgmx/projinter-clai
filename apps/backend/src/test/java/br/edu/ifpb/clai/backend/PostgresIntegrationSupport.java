package br.edu.ifpb.clai.backend;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import java.util.UUID;

/** Conexão exclusiva do banco de testes, com schema novo a cada execução. */
abstract class PostgresIntegrationSupport {
    private static final String SCHEMA = "clai_test_" + UUID.randomUUID().toString().replace("-", "");

    @DynamicPropertySource
    static void configurarPostgres(DynamicPropertyRegistry registry) {
        String url = System.getenv("CLAI_TEST_DATABASE_URL");
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Defina CLAI_TEST_DATABASE_URL para executar testes de integração.");
        }
        if (!url.matches("^jdbc:postgresql://[^/]+/clai_test(?:[?].*)?$")) {
            throw new IllegalArgumentException("Os testes exigem um banco PostgreSQL chamado clai_test.");
        }
        if (url.contains("currentSchema=")) {
            throw new IllegalArgumentException("O schema de teste é definido automaticamente; remova currentSchema da URL.");
        }
        String senha = System.getenv("CLAI_TEST_DATABASE_PASSWORD");
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Defina CLAI_TEST_DATABASE_PASSWORD para o PostgreSQL exclusivo dos testes.");
        }
        String urlTeste = url + (url.contains("?") ? "&" : "?") + "currentSchema=" + SCHEMA;
        // Estas propriedades têm precedência sobre variáveis SPRING_DATASOURCE_* externas.
        registry.add("spring.datasource.url", () -> urlTeste);
        registry.add("spring.datasource.username", () -> variavel("CLAI_TEST_DATABASE_USERNAME", "clai_test"));
        registry.add("spring.datasource.password", () -> senha);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.datasource.hikari.connection-init-sql", () -> "CREATE SCHEMA IF NOT EXISTS " + SCHEMA);
        registry.add("spring.sql.init.mode", () -> "always");
        registry.add("spring.sql.init.schema-locations", () -> "classpath:schema-cadastro.sql");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    private static String variavel(String nome, String padrao) {
        String valor = System.getenv(nome);
        return valor == null || valor.isBlank() ? padrao : valor;
    }
}
