package com.ecoatm.salesplatform.partialcredit;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.model.partialcredit.enums.SystemStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the V89 migration applied cleanly against PostgreSQL. Extends
 * {@link PostgresIntegrationTest} so the {@code pg-test} profile runs Flyway
 * end-to-end (V1..V89) during context startup — the {@code test} profile uses
 * H2 with Flyway disabled and cannot exercise real SQL migrations.
 * <p>
 * Asserts the load-bearing pieces of Sprint 1 Task 8:
 * <ul>
 *   <li>{@code partial_credit} schema exists with all 7 tables</li>
 *   <li>5 {@code credit_request_statuses} seed rows in {@code sort_order}
 *       (matching {@link SystemStatus} enum constants)</li>
 *   <li>4 {@code PartialCredit_*} role rows in {@code identity.user_roles}
 *       at IDs 1101–1104 (clear of prod range 1–11 and dev-seed 1001–1006)</li>
 *   <li>Key indexes from V89 are present</li>
 * </ul>
 */
class PartialCreditMigrationIT extends PostgresIntegrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    @DisplayName("partial_credit schema and 7 expected tables exist after V89")
    void partial_credit_schema_and_tables_exist() {
        Integer schemaCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM information_schema.schemata "
            + "WHERE schema_name = 'partial_credit'",
            Integer.class);
        assertThat(schemaCount).isEqualTo(1);

        // V89 creates exactly these 7 tables. Asserting on the full set
        // catches both missing tables and accidentally-added ones.
        List<String> expectedTables = Arrays.asList(
            "credit_request_photos",
            "credit_request_statuses",
            "credit_request_uploads",
            "credit_requests",
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
    @DisplayName("5 credit_request_statuses seed rows present in sort_order matching SystemStatus enum")
    void credit_request_statuses_seeded_in_sort_order() {
        // Ordered by sort_order ascending — matches the V89 INSERT order:
        // DRAFT(0), PENDING_APPROVAL(1), UNDER_REVIEW(2), APPROVED(3), DECLINED(4).
        List<String> systemStatuses = jdbc.queryForList(
            "SELECT system_status FROM partial_credit.credit_request_statuses "
            + "ORDER BY sort_order ASC",
            String.class);

        assertThat(systemStatuses).containsExactly(
            SystemStatus.DRAFT.name(),
            SystemStatus.PENDING_APPROVAL.name(),
            SystemStatus.UNDER_REVIEW.name(),
            SystemStatus.APPROVED.name(),
            SystemStatus.DECLINED.name());

        // Defence-in-depth: confirm DRAFT is flagged as default and the
        // PENDING_APPROVAL row uses the orange chip color from the design.
        Boolean draftIsDefault = jdbc.queryForObject(
            "SELECT is_default FROM partial_credit.credit_request_statuses "
            + "WHERE system_status = 'DRAFT'",
            Boolean.class);
        assertThat(draftIsDefault).isTrue();

        String pendingColor = jdbc.queryForObject(
            "SELECT color_hex FROM partial_credit.credit_request_statuses "
            + "WHERE system_status = 'PENDING_APPROVAL'",
            String.class);
        assertThat(pendingColor).isEqualTo("#D08214");
    }

    @Test
    @DisplayName("4 PartialCredit_* roles seeded in identity.user_roles at IDs 1101–1104")
    void partial_credit_roles_seeded_in_user_roles() {
        // The plan reserves IDs 1101–1104 for partial-credit roles to stay
        // clear of the prod range (1–11) and the dev seed (1001–1006).
        List<String> rolesById = jdbc.queryForList(
            "SELECT name FROM identity.user_roles "
            + "WHERE id BETWEEN 1101 AND 1104 ORDER BY id ASC",
            String.class);

        assertThat(rolesById).containsExactly(
            "PartialCredit_Buyer",
            "PartialCredit_SalesRep",
            "PartialCredit_SalesOps",
            "PartialCredit_Admin");
    }

    @Test
    @DisplayName("V89 creates the expected indexes on partial_credit tables")
    void partial_credit_indexes_present() {
        // The status+date index is load-bearing for the buyer landing
        // page list query; if it disappears we want CI to scream.
        Integer statusDateIdxCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM pg_indexes "
            + "WHERE schemaname = 'partial_credit' "
            + "  AND tablename = 'credit_requests' "
            + "  AND indexname = 'idx_cr_status_request_date'",
            Integer.class);
        assertThat(statusDateIdxCount).isEqualTo(1);

        Integer buyerIdxCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM pg_indexes "
            + "WHERE schemaname = 'partial_credit' "
            + "  AND tablename = 'credit_requests' "
            + "  AND indexname = 'idx_cr_buyer_code'",
            Integer.class);
        assertThat(buyerIdxCount).isEqualTo(1);

        Integer orderIdxCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM pg_indexes "
            + "WHERE schemaname = 'partial_credit' "
            + "  AND tablename = 'credit_requests' "
            + "  AND indexname = 'idx_cr_order_number'",
            Integer.class);
        assertThat(orderIdxCount).isEqualTo(1);
    }
}
