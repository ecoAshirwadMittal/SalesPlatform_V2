package com.ecoatm.salesplatform.email;

import static org.assertj.core.api.Assertions.assertThat;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Verifies the V101 migration seeded the {@code CreditRequestSubmitted} email
 * template (gap 2.5 Task 1) into the unified {@code email.template} store.
 *
 * <p>The row is what {@code CreditRequestSubmittedEmailListener} resolves by key
 * on a {@code CreditRequestSubmittedEvent} — if the seed is missing or disabled,
 * {@code EmailService.sendTemplated} would throw and the confirmation email
 * would silently never go out, so lock the row's presence + enabled flag +
 * placeholder set in here.
 */
class V101MigrationIT extends PostgresIntegrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    @DisplayName("V101 seeds an enabled CreditRequestSubmitted template row referencing the listener's vars")
    void seedsEnabledCreditRequestSubmittedTemplate() {
        Long count = jdbc.queryForObject(
                "SELECT count(*) FROM email.template WHERE template_key = 'CreditRequestSubmitted'", Long.class);
        assertThat(count).isEqualTo(1L);

        Map<String, Object> row = jdbc.queryForMap(
                "SELECT enabled, subject, content_html, content_plain "
                        + "FROM email.template WHERE template_key = 'CreditRequestSubmitted'");

        assertThat(row.get("enabled")).isEqualTo(Boolean.TRUE);
        assertThat((String) row.get("subject")).contains("{{requestNumber}}");

        // The seed's placeholders must match the listener's vars map keys — a
        // drift here (renamed var) would render blank in production. Guard the
        // full var set against that.
        String html = (String) row.get("content_html");
        assertThat(html)
                .contains("{{requestNumber}}")
                .contains("{{buyerName}}")
                .contains("{{requestReasons}}")
                .contains("{{totalDevices}}");
    }
}
