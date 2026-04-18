package com.ecoatm.salesplatform.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feature-flagged Snowflake datasource (Theme 2 infrastructure).
 *
 * <p>Only registers when {@code snowflake.enabled=true}. Postgres remains the
 * primary datasource — this bean is intentionally NOT marked {@code @Primary}.
 * Phases 4–5 (aggregated inventory sync service) will inject it by qualifier.
 *
 * <p>Fail-fast: when enabled, a blank {@code snowflake.username} or
 * {@code snowflake.password} short-circuits bean construction with an
 * {@link IllegalStateException}. The password value is never logged.
 */
@Configuration
@ConditionalOnProperty(name = "snowflake.enabled", havingValue = "true")
@EnableConfigurationProperties(SnowflakeDataSourceConfig.SnowflakeProperties.class)
public class SnowflakeDataSourceConfig {

    public static final String SNOWFLAKE_DATASOURCE = "snowflakeDataSource";
    private static final String POOL_NAME = "snowflake-pool";
    private static final String DRIVER_CLASS = "net.snowflake.client.jdbc.SnowflakeDriver";

    @Bean(name = SNOWFLAKE_DATASOURCE)
    @Qualifier(SNOWFLAKE_DATASOURCE)
    public DataSource snowflakeDataSource(SnowflakeProperties props) {
        requireNonBlank(props.username(), "snowflake.username");
        requireNonBlank(props.password(), "snowflake.password");
        requireNonBlank(props.jdbcUrl(), "snowflake.jdbc-url");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.jdbcUrl());
        config.setUsername(props.username());
        config.setPassword(props.password());
        // Pin the driver class so Hikari skips the DriverManager lookup at
        // construction — otherwise Hikari eagerly resolves a driver for the
        // URL, which fails in environments where the Snowflake driver has
        // not auto-registered yet.
        config.setDriverClassName(DRIVER_CLASS);
        config.setPoolName(POOL_NAME);
        config.setMaximumPoolSize(props.pool().maximumSize());
        // Lazy init — HikariCP otherwise opens a probe connection on startup
        // which would hang any env where enabled=true but the network path
        // to Snowflake is not yet reachable (e.g. first prod deploy).
        config.setInitializationFailTimeout(-1);
        return new HikariDataSource(config);
    }

    private static void requireNonBlank(String value, String propertyName) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "snowflake.enabled=true but required property is blank: " + propertyName);
        }
    }

    /**
     * Binds {@code snowflake.*} keys. Nested {@link Pool} mirrors
     * {@code snowflake.pool.maximum-size}.
     */
    @ConfigurationProperties(prefix = "snowflake")
    public record SnowflakeProperties(
            boolean enabled,
            String jdbcUrl,
            String username,
            String password,
            Pool pool) {

        public SnowflakeProperties {
            if (pool == null) {
                pool = new Pool(3);
            }
        }

        public record Pool(int maximumSize) {}
    }
}
