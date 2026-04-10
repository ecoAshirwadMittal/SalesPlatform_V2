package com.ecoatm.salesplatform;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Base class for integration tests that need a real PostgreSQL database.
 * <p>
 * Strategy: Testcontainers if Docker is available, local PostgreSQL as fallback.
 * <p>
 * Flyway runs all migrations (V1–V36) including data seeds, providing
 * a realistic database environment that catches issues invisible to
 * unit tests with mocked EntityManagers — such as wrong table/column names
 * in native SQL queries.
 * <p>
 * Subclasses inherit {@code @SpringBootTest} and {@code @ActiveProfiles("pg-test")}.
 * Add {@code @Transactional} so test data rolls back after each test.
 */
@SpringBootTest
@ActiveProfiles("pg-test")
public abstract class PostgresIntegrationTest {

    private static final boolean DOCKER_AVAILABLE = isDockerAvailable();

    // Testcontainers container — only created when Docker is present
    private static final Object CONTAINER = DOCKER_AVAILABLE ? startContainer() : null;

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        if (DOCKER_AVAILABLE && CONTAINER != null) {
            @SuppressWarnings("unchecked")
            var pg = (org.testcontainers.containers.PostgreSQLContainer<?>) CONTAINER;
            registry.add("spring.datasource.url", pg::getJdbcUrl);
            registry.add("spring.datasource.username", pg::getUsername);
            registry.add("spring.datasource.password", pg::getPassword);
        } else {
            // Fallback: use local PostgreSQL with the dev database
            registry.add("spring.datasource.url",
                    () -> "jdbc:postgresql://localhost:5432/salesplatform_dev");
            registry.add("spring.datasource.username", () -> "salesplatform");
            registry.add("spring.datasource.password", () -> "salesplatform");
        }
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    private static boolean isDockerAvailable() {
        try {
            Process p = new ProcessBuilder("docker", "info")
                    .redirectErrorStream(true).start();
            int exitCode = p.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("resource")
    private static Object startContainer() {
        try {
            var pg = new org.testcontainers.containers.PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("salesplatform_test")
                    .withUsername("salesplatform")
                    .withPassword("salesplatform");
            pg.start();
            return pg;
        } catch (Exception e) {
            System.err.println("[PostgresIntegrationTest] Testcontainers failed, will use local PG: " + e.getMessage());
            return null;
        }
    }
}
