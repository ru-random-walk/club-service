package ru.random.walk.club_service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SuppressWarnings("resource")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractPostgresContainerTest {

    private static final PostgreSQLContainer<?> DATABASE_CONTAINER;

    static {
        DATABASE_CONTAINER = new PostgreSQLContainer<>("postgres:latest")
                .withDatabaseName("random_walk_postgres")
                .withUsername("postgres")
                .withPassword("postgres");
        DATABASE_CONTAINER.start();
    }

    @BeforeAll
    public static void setUp() {
    }

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DATABASE_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", DATABASE_CONTAINER::getUsername);
        registry.add("spring.datasource.password", DATABASE_CONTAINER::getPassword);
    }
}
