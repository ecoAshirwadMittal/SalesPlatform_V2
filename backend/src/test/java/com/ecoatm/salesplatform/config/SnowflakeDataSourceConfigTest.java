package com.ecoatm.salesplatform.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Verifies the Phase 0 feature flag behavior on {@link SnowflakeDataSourceConfig}:
 *
 * <ul>
 *   <li>Bean is absent when {@code snowflake.enabled=false} (default dev/test).</li>
 *   <li>Bean registers as a Hikari pool when enabled with full credentials.</li>
 *   <li>Startup fails fast when enabled with a blank username.</li>
 * </ul>
 *
 * Uses {@link ApplicationContextRunner} so no database connection is attempted —
 * Hikari's {@code initializationFailTimeout=-1} defers real connection attempts
 * until the first {@code getConnection()}, and these tests never call it.
 */
class SnowflakeDataSourceConfigTest {

    // Exclude Spring Boot's default datasource autoconfig so the test context
    // isn't polluted by an H2 primary DataSource — we only care about the
    // conditional Snowflake bean here.
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(
                    org.springframework.boot.autoconfigure.AutoConfigurations.of(
                            DataSourceAutoConfiguration.class))
            .withUserConfiguration(SnowflakeDataSourceConfig.class)
            .withPropertyValues("spring.autoconfigure.exclude=" + DataSourceAutoConfiguration.class.getName());

    @Test
    @DisplayName("bean absent when snowflake.enabled=false")
    void beanAbsent_whenSnowflakeDisabled() {
        contextRunner
                .withPropertyValues("snowflake.enabled=false")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.containsBean("snowflakeDataSource")).isFalse();
                });
    }

    @Test
    @DisplayName("bean registers as Hikari pool when enabled with credentials")
    void beanPresent_whenEnabledWithCreds() {
        contextRunner
                .withPropertyValues(
                        "snowflake.enabled=true",
                        "snowflake.jdbc-url=jdbc:snowflake://fake.snowflakecomputing.com/?db=TEST",
                        "snowflake.username=u",
                        "snowflake.password=p",
                        "snowflake.pool.maximum-size=5")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasBean("snowflakeDataSource");

                    DataSource ds = context.getBean("snowflakeDataSource", DataSource.class);
                    assertThat(ds).isInstanceOf(HikariDataSource.class);

                    HikariDataSource hikari = (HikariDataSource) ds;
                    assertThat(hikari.getPoolName()).isEqualTo("snowflake-pool");
                    assertThat(hikari.getMaximumPoolSize()).isEqualTo(5);
                    assertThat(hikari.getJdbcUrl()).isEqualTo("jdbc:snowflake://fake.snowflakecomputing.com/?db=TEST");
                    assertThat(hikari.getUsername()).isEqualTo("u");
                });
    }

    @Test
    @DisplayName("fails fast when enabled with blank username")
    void failFast_whenEnabledWithBlankUsername() {
        contextRunner
                .withPropertyValues(
                        "snowflake.enabled=true",
                        "snowflake.jdbc-url=jdbc:snowflake://fake.snowflakecomputing.com/?db=TEST",
                        "snowflake.username=",
                        "snowflake.password=p")
                .run(context -> {
                    assertThat(context).hasFailed();

                    Throwable root = context.getStartupFailure();
                    assertThat(root).isNotNull();

                    // Walk the cause chain — Spring wraps bean-construction errors.
                    Throwable cursor = root;
                    IllegalStateException match = null;
                    while (cursor != null) {
                        if (cursor instanceof IllegalStateException ise) {
                            match = ise;
                            break;
                        }
                        cursor = cursor.getCause();
                    }

                    assertThat(match)
                            .as("IllegalStateException must appear in failure chain")
                            .isNotNull();
                    assertThat(match.getMessage()).contains("snowflake.username");
                });
    }
}
