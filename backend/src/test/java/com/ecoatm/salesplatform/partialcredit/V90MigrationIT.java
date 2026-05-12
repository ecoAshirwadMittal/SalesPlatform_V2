package com.ecoatm.salesplatform.partialcredit;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the V90 migration applied cleanly against PostgreSQL.
 * <p>
 * Sprint 4 Chunk 1 deliverables:
 * <ul>
 *   <li>{@code partial_credit.email_templates} with 3 seed rows
 *       ({@code ReviewCompleted_Approved}, {@code ReviewCompleted_Declined},
 *       {@code PhotoUploadRequested})</li>
 *   <li>{@code partial_credit.email_audit} with the two expected indexes</li>
 *   <li>Three new on-behalf columns on {@code credit_requests} plus the
 *       partial index that supports the "find on-behalf requests for user
 *       X" query</li>
 *   <li>Legacy {@code credit_requests} rows default {@code is_on_behalf} to
 *       FALSE — the ALTER must not surface NULLs through the NOT NULL
 *       constraint</li>
 * </ul>
 */
class V90MigrationIT extends PostgresIntegrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    @DisplayName("V90 adds email_templates and email_audit tables to partial_credit")
    void v90_adds_email_tables_to_partial_credit_schema() {
        // Re-asserts the table-set explicitly so an accidental rename or
        // missed table in V90 fails CI rather than only showing up in a
        // downstream service test.
        List<String> expectedTables = Arrays.asList(
            "credit_request_photos",
            "credit_request_statuses",
            "credit_request_uploads",
            "credit_requests",
            "email_audit",
            "email_templates",
            "encumbered_device_lines",
            "missing_device_lines",
            "wrong_device_lines"
        );
        List<String> actualTables = jdbc.queryForList(
            "SELECT table_name FROM information_schema.tables "
            + "WHERE table_schema = 'partial_credit' ORDER BY table_name",
            String.class);
        assertThat(actualTables).containsExactlyElementsOf(expectedTables);
    }

    @Test
    @DisplayName("email_templates has the 3 seeded keys, all enabled by default")
    void email_templates_seed_present_and_enabled() {
        List<String> keys = jdbc.queryForList(
            "SELECT template_key FROM partial_credit.email_templates ORDER BY template_key ASC",
            String.class);
        assertThat(keys).containsExactly(
            "PhotoUploadRequested",
            "ReviewCompleted_Approved",
            "ReviewCompleted_Declined");

        Integer enabledCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM partial_credit.email_templates WHERE enabled = TRUE",
            Integer.class);
        assertThat(enabledCount).isEqualTo(3);

        // The Approved subject must contain the {{requestNumber}} variable
        // — Chunk 2's listener flip depends on this exact substitution. If
        // an admin or migration accidentally strips the placeholder, every
        // approved-email send would emit a subject with literal {{ }}.
        String approvedSubject = jdbc.queryForObject(
            "SELECT subject FROM partial_credit.email_templates "
            + "WHERE template_key = 'ReviewCompleted_Approved'",
            String.class);
        assertThat(approvedSubject).contains("{{requestNumber}}");

        // The Approved body must mention approvedTotalDisplay; the
        // Declined body must NOT (the legacy listener only emits the
        // credit-total line on approval, and we want the seed to mirror
        // that split exactly).
        String approvedHtml = jdbc.queryForObject(
            "SELECT body_html FROM partial_credit.email_templates "
            + "WHERE template_key = 'ReviewCompleted_Approved'",
            String.class);
        assertThat(approvedHtml).contains("{{approvedTotalDisplay}}");

        String declinedHtml = jdbc.queryForObject(
            "SELECT body_html FROM partial_credit.email_templates "
            + "WHERE template_key = 'ReviewCompleted_Declined'",
            String.class);
        assertThat(declinedHtml).doesNotContain("{{approvedTotalDisplay}}");
    }

    @Test
    @DisplayName("email_audit has the request-id and sent-at indexes")
    void email_audit_indexes_present() {
        Integer requestIdxCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM pg_indexes "
            + "WHERE schemaname = 'partial_credit' "
            + "  AND tablename = 'email_audit' "
            + "  AND indexname = 'idx_pc_email_audit_request'",
            Integer.class);
        assertThat(requestIdxCount).isEqualTo(1);

        Integer sentAtIdxCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM pg_indexes "
            + "WHERE schemaname = 'partial_credit' "
            + "  AND tablename = 'email_audit' "
            + "  AND indexname = 'idx_pc_email_audit_sent_at'",
            Integer.class);
        assertThat(sentAtIdxCount).isEqualTo(1);
    }

    @Test
    @DisplayName("credit_requests gains the three on-behalf columns and a partial index")
    void credit_requests_on_behalf_columns_present() {
        List<String> onBehalfCols = jdbc.queryForList(
            "SELECT column_name FROM information_schema.columns "
            + "WHERE table_schema = 'partial_credit' "
            + "  AND table_name = 'credit_requests' "
            + "  AND column_name IN ('is_on_behalf','on_behalf_of_id','on_behalf_buyer_code_id') "
            + "ORDER BY column_name ASC",
            String.class);
        assertThat(onBehalfCols).containsExactly(
            "is_on_behalf",
            "on_behalf_buyer_code_id",
            "on_behalf_of_id");

        // is_on_behalf is NOT NULL DEFAULT FALSE — the default needs to be
        // recorded so the ALTER didn't accidentally backfill NULLs that the
        // NOT NULL constraint would then reject on the next session restart.
        String isOnBehalfDefault = jdbc.queryForObject(
            "SELECT column_default FROM information_schema.columns "
            + "WHERE table_schema = 'partial_credit' "
            + "  AND table_name = 'credit_requests' "
            + "  AND column_name = 'is_on_behalf'",
            String.class);
        assertThat(isOnBehalfDefault).contains("false");

        Integer partialIdxCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM pg_indexes "
            + "WHERE schemaname = 'partial_credit' "
            + "  AND tablename = 'credit_requests' "
            + "  AND indexname = 'idx_cr_on_behalf_of'",
            Integer.class);
        assertThat(partialIdxCount).isEqualTo(1);
    }

    @Test
    @DisplayName("PartialCredit_* user_roles rows from V89 stay orphaned — no user_role_assignments")
    void partial_credit_role_rows_remain_orphaned() {
        // Sprint 4 §7 decision: the four V89-seeded PartialCredit_* role
        // rows (1101-1104) are NOT mapped to any user. If this count is
        // non-zero, a migration somewhere has started seeding mappings —
        // surface that immediately because the controllers don't expect
        // those roles to be granted.
        Integer assignedCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM identity.user_role_assignments "
            + "WHERE role_id BETWEEN 1101 AND 1104",
            Integer.class);
        assertThat(assignedCount).isEqualTo(0);
    }
}
