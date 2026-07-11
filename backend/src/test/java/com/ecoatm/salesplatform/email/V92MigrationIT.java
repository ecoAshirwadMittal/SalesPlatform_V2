package com.ecoatm.salesplatform.email;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the V92 migration applied cleanly against PostgreSQL.
 * <p>
 * Task 1 of the unified-email-management build creates the three
 * {@code email}-schema tables every later task depends on, seeds the
 * singleton {@code smtp_config} row, and copies the live Partial-Credit
 * templates into the unified {@code email.template} store.
 * <p>
 * <b>V35 supersession (approved drop-and-recreate, 2026-07-11).</b> An
 * abandoned earlier migration {@code V35__email_admin_tables.sql} had created
 * dead, code-unreferenced {@code email.smtp_config} / {@code email.email_template}
 * / {@code email.email_log} tables (V35's {@code smtp_config} even carried an
 * {@code encrypted_password} column, violating design decision D2). V92 drops
 * those three first, then creates the clean design tables. Two extra assertions
 * below lock that supersession in so it can't silently regress:
 * <ul>
 *   <li>the V35 table names {@code email.email_template} / {@code email.email_log}
 *       no longer exist;</li>
 *   <li>the surviving {@code email.smtp_config} is the NEW design shape — it has
 *       no {@code encrypted_password} / {@code username} column.</li>
 * </ul>
 */
class V92MigrationIT extends PostgresIntegrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    /** True when {@code <schema>.<name>} is a base table/view in the catalog. */
    private boolean tableExists(String schema, String name) {
        Integer count = jdbc.queryForObject(
            "SELECT count(*) FROM information_schema.tables "
            + "WHERE table_schema = ? AND table_name = ?",
            Integer.class, schema, name);
        return count != null && count > 0;
    }

    /** Count of columns matching {@code name} on {@code <schema>.<table>}. */
    private int columnCount(String schema, String table, String column) {
        Integer count = jdbc.queryForObject(
            "SELECT count(*) FROM information_schema.columns "
            + "WHERE table_schema = ? AND table_name = ? AND column_name = ?",
            Integer.class, schema, table, column);
        return count == null ? 0 : count;
    }

    @Test
    @DisplayName("V92 creates the 3 design tables, the seeded disabled smtp_config, and copies the PC templates")
    void createsThreeTablesSeedAndCopiesPcTemplates() {
        assertThat(tableExists("email", "smtp_config")).isTrue();
        assertThat(tableExists("email", "template")).isTrue();
        assertThat(tableExists("email", "log")).isTrue();

        Long cfg = jdbc.queryForObject("SELECT count(*) FROM email.smtp_config", Long.class);
        assertThat(cfg).isEqualTo(1L);
        assertThat(jdbc.queryForObject(
            "SELECT enabled FROM email.smtp_config WHERE id=1", Boolean.class)).isFalse();

        // 3 PC templates copied over — count matches the live source table.
        Long tpls = jdbc.queryForObject(
            "SELECT count(*) FROM email.template WHERE template_key IN "
            + "(SELECT template_key FROM partial_credit.email_templates)", Long.class);
        assertThat(tpls).isEqualTo(jdbc.queryForObject(
            "SELECT count(*) FROM partial_credit.email_templates", Long.class));
    }

    @Test
    @DisplayName("V92 drops the abandoned V35 tables — email.email_template and email.email_log no longer exist")
    void dropsAbandonedV35Tables() {
        // If either V35 name survives, the drop-and-recreate silently regressed
        // (e.g. someone removed the DROP prefix) — fail loudly here rather than
        // let two stale, code-unreferenced tables linger in the schema.
        assertThat(tableExists("email", "email_template"))
            .as("V35 email.email_template must be dropped by V92")
            .isFalse();
        assertThat(tableExists("email", "email_log"))
            .as("V35 email.email_log must be dropped by V92")
            .isFalse();
    }

    @Test
    @DisplayName("Surviving email.smtp_config is the new design shape — no encrypted_password / username column")
    void smtpConfigIsNewDesignShapeNotV35() {
        // V35's smtp_config carried encrypted_password + username (D2 forbids a
        // password anywhere in the DB). Their absence proves the table Flyway
        // left behind is V92's design table, not V35's leftover.
        assertThat(columnCount("email", "smtp_config", "encrypted_password"))
            .as("design smtp_config must not carry a password column (D2)")
            .isZero();
        assertThat(columnCount("email", "smtp_config", "username"))
            .as("design smtp_config must not carry a username column (D2)")
            .isZero();

        // Positive confirmation: the new design-only columns are present
        // (V35 had updated_date, not changed_date, and no changed_by_id FK).
        assertThat(columnCount("email", "smtp_config", "changed_date")).isEqualTo(1);
        assertThat(columnCount("email", "smtp_config", "changed_by_id")).isEqualTo(1);
    }
}
