package com.ecoatm.salesplatform.migration;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the V103 migration applied cleanly against PostgreSQL — the three
 * SLA / counter-offer-reminder flag columns on {@code pws.offer}. Extends
 * {@link PostgresIntegrationTest} so the {@code pg-test} profile runs Flyway
 * end-to-end (V1..V103) during context startup; the {@code test} profile uses
 * H2 with Flyway disabled and cannot exercise real SQL migrations.
 *
 * <p>Why these columns matter: {@code PWSAdminController.setSLATags} /
 * {@code removeSLATags} already write {@code offer_beyond_sla}, so before this
 * migration those endpoints threw {@code column "offer_beyond_sla" does not
 * exist} at runtime. The migration makes them functional.
 */
class V103MigrationIT extends PostgresIntegrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void all_three_sla_reminder_flag_columns_exist_on_pws_offer() {
        Integer colCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM information_schema.columns "
            + "WHERE table_schema = 'pws' AND table_name = 'offer' "
            + "AND column_name IN ('offer_beyond_sla', 'first_reminder_sent', 'second_reminder_sent')",
            Integer.class);
        assertThat(colCount).isEqualTo(3);
    }

    @Test
    void flag_columns_are_boolean_not_null_with_false_default() {
        for (String column : new String[] {
                "offer_beyond_sla", "first_reminder_sent", "second_reminder_sent"}) {
            String dataType = jdbc.queryForObject(
                "SELECT data_type FROM information_schema.columns "
                + "WHERE table_schema = 'pws' AND table_name = 'offer' AND column_name = ?",
                String.class, column);
            assertThat(dataType).as("%s data_type", column).isEqualTo("boolean");

            String nullable = jdbc.queryForObject(
                "SELECT is_nullable FROM information_schema.columns "
                + "WHERE table_schema = 'pws' AND table_name = 'offer' AND column_name = ?",
                String.class, column);
            assertThat(nullable).as("%s is_nullable", column).isEqualTo("NO");

            String columnDefault = jdbc.queryForObject(
                "SELECT column_default FROM information_schema.columns "
                + "WHERE table_schema = 'pws' AND table_name = 'offer' AND column_name = ?",
                String.class, column);
            assertThat(columnDefault).as("%s column_default", column).contains("false");
        }
    }
}
