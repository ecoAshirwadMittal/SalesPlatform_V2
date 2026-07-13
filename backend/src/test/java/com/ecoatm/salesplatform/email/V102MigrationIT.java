package com.ecoatm.salesplatform.email;

import static org.assertj.core.api.Assertions.assertThat;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Verifies the V102 migration seeded the {@code CreditRequestSalesApproved}
 * accounting-notification email template (gap 2.5 Task 4) into the unified
 * {@code email.template} store.
 *
 * <p>The row is what {@code AccountingEmailService} resolves by key on the
 * manual admin send — if it is missing or disabled,
 * {@code EmailService.sendTemplated} would throw and the accounting email would
 * never go out, so lock the row's presence + enabled flag + the full placeholder
 * set (a renamed var would render blank in production).
 */
class V102MigrationIT extends PostgresIntegrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    @DisplayName("V102 seeds an enabled CreditRequestSalesApproved template row referencing the service's vars")
    void seedsEnabledCreditRequestSalesApprovedTemplate() {
        Long count = jdbc.queryForObject(
                "SELECT count(*) FROM email.template WHERE template_key = 'CreditRequestSalesApproved'",
                Long.class);
        assertThat(count).isEqualTo(1L);

        Map<String, Object> row = jdbc.queryForMap(
                "SELECT enabled, subject, content_html, content_plain "
                        + "FROM email.template WHERE template_key = 'CreditRequestSalesApproved'");

        assertThat(row.get("enabled")).isEqualTo(Boolean.TRUE);
        assertThat((String) row.get("subject")).contains("{{requestNumber}}");

        // Every placeholder the AccountingEmailService var map supplies must be
        // present in the seeded bodies — a drift (renamed var) would render blank
        // in production. Guard the full var set in both the HTML and plain bodies.
        String html = (String) row.get("content_html");
        assertThat(html)
                .contains("{{requestNumber}}")
                .contains("{{weekNumber}}")
                .contains("{{buyerName}}")
                .contains("{{buyerCode}}")
                .contains("{{requestReasons}}")
                .contains("{{totalDevicesApproved}}")
                .contains("{{totalAmountApproved}}");

        String plain = (String) row.get("content_plain");
        assertThat(plain)
                .contains("{{requestNumber}}")
                .contains("{{weekNumber}}")
                .contains("{{buyerName}}")
                .contains("{{buyerCode}}")
                .contains("{{requestReasons}}")
                .contains("{{totalDevicesApproved}}")
                .contains("{{totalAmountApproved}}");
    }
}
