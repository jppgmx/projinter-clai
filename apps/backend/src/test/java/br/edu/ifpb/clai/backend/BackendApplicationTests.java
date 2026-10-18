package br.edu.ifpb.clai.backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;

@EnabledIfEnvironmentVariable(named = "CLAI_TEST_DATABASE_URL", matches = "jdbc:postgresql:.*")
@ActiveProfiles("test")
@SpringBootTest
class BackendApplicationTests extends PostgresIntegrationSupport {

    @Test
    void contextLoads() {
    }

}
