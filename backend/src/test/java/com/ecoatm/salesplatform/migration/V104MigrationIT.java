package com.ecoatm.salesplatform.migration;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the V104 migration applied cleanly against PostgreSQL — the
 * {@code pws.company_holiday} table backing the SLA-tag business-day math.
 * Extends {@link PostgresIntegrationTest} so the {@code pg-test} profile runs
 * Flyway end-to-end (V1..V104) during context startup.
 */
class V104MigrationIT extends PostgresIntegrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void company_holiday_table_exists_with_expected_columns() {
        Integer tableCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables "
                + "WHERE table_schema = 'pws' AND table_name = 'company_holiday'",
                Integer.class);
        assertThat(tableCount).isEqualTo(1);

        Integer colCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns "
                + "WHERE table_schema = 'pws' AND table_name = 'company_holiday' "
                + "AND column_name IN ('id', 'holiday_date', 'name')",
                Integer.class);
        assertThat(colCount).isEqualTo(3);
    }

    @Test
    void holiday_date_column_is_a_not_null_date() {
        String dataType = jdbc.queryForObject(
                "SELECT data_type FROM information_schema.columns "
                + "WHERE table_schema = 'pws' AND table_name = 'company_holiday' "
                + "AND column_name = 'holiday_date'",
                String.class);
        assertThat(dataType).isEqualTo("date");

        String nullable = jdbc.queryForObject(
                "SELECT is_nullable FROM information_schema.columns "
                + "WHERE table_schema = 'pws' AND table_name = 'company_holiday' "
                + "AND column_name = 'holiday_date'",
                String.class);
        assertThat(nullable).isEqualTo("NO");
    }

    @Test
    void company_holiday_table_is_seeded() {
        Integer rows = jdbc.queryForObject(
                "SELECT COUNT(*) FROM pws.company_holiday", Integer.class);
        // 7 observed holidays × 3 years (2025–2027); assert the seed is present
        // (>= so a shared dev DB that gains further rows never fails this).
        assertThat(rows).isGreaterThanOrEqualTo(21);

        Integer independenceDay2026 = jdbc.queryForObject(
                "SELECT COUNT(*) FROM pws.company_holiday WHERE holiday_date = DATE '2026-07-04'",
                Integer.class);
        assertThat(independenceDay2026).isEqualTo(1);
    }
}
