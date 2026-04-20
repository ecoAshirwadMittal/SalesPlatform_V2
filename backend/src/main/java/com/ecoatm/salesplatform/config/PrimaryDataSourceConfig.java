package com.ecoatm.salesplatform.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Primary (Postgres) datasource — made explicit so it coexists cleanly with
 * {@link SnowflakeDataSourceConfig} when {@code snowflake.enabled=true}.
 *
 * <p>Why this exists: Spring Boot's {@code DataSourceAutoConfiguration} is
 * gated by {@code @ConditionalOnMissingBean(DataSource.class)}. The moment
 * the Snowflake bean is registered, auto-config backs off and the app is
 * left with only the Snowflake pool — which causes Flyway to hunt for a
 * Postgres-shaped DataSource and fail with
 * {@code "Flyway migration DataSource missing"}. Declaring the Postgres
 * DataSource explicitly here restores it and pins it as {@code @Primary}
 * so Flyway, JPA, and unqualified autowires consistently resolve to it.
 *
 * <p>The bean method returns {@link HikariDataSource} (not the {@code DataSource}
 * interface) so that {@code @ConfigurationProperties("spring.datasource.hikari")}
 * reaches Hikari's own setters ({@code maximumPoolSize}, {@code connectionTimeout},
 * etc.) — binding against the interface would silently ignore those properties.
 */
@Configuration
public class PrimaryDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariDataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }
}
