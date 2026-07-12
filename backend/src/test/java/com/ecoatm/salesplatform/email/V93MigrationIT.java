package com.ecoatm.salesplatform.email;

import static org.assertj.core.api.Assertions.assertThat;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Verifies the V93 migration seeded the {@code RMA_Approved} email template
 * (gap-analysis #3 Task C) into the unified {@code email.template} store.
 *
 * <p>The row is what {@code RmaApprovedEmailListener} resolves by key on an
 * APPROVED {@code RmaReviewCompletedEvent} — if the seed is missing or
 * disabled, {@code EmailService.sendTemplated} would throw and the approval
 * email would silently never go out, so lock the row's presence + enabled flag
 * in here.
 */
class V93MigrationIT extends PostgresIntegrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    @DisplayName("V93 seeds an enabled RMA_Approved template row referencing the listener's vars")
    void seedsEnabledRmaApprovedTemplate() {
        Long count = jdbc.queryForObject(
                "SELECT count(*) FROM email.template WHERE template_key = 'RMA_Approved'", Long.class);
        assertThat(count).isEqualTo(1L);

        Map<String, Object> row = jdbc.queryForMap(
                "SELECT enabled, subject, content_html, content_plain "
                        + "FROM email.template WHERE template_key = 'RMA_Approved'");

        assertThat(row.get("enabled")).isEqualTo(Boolean.TRUE);
        assertThat((String) row.get("subject")).contains("{{rmaNumber}}");

        // The seed's placeholders must match the listener's vars map keys — a
        // drift here (renamed var) would render blank in production. Guard the
        // load-bearing money + roll-up vars against that.
        String html = (String) row.get("content_html");
        assertThat(html)
                .contains("{{rmaNumber}}")
                .contains("{{buyerCode}}")
                .contains("{{approvedQty}}")
                .contains("{{approvedSkus}}")
                .contains("{{approvedTotalDisplay}}")
                .contains("{{approvedItemsSummary}}");
    }
}
