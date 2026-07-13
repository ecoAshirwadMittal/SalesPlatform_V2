package com.ecoatm.salesplatform.email;

import static org.assertj.core.api.Assertions.assertThat;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Verifies the V105 migration seeded BOTH counter-offer reminder templates
 * (gap-analysis 2.3 sub-feature 1, Chunks C+D) into the unified
 * {@code email.template} store.
 *
 * <p>These are the rows {@code CounterOfferReminderService} resolves by key when
 * an offer sits in {@code Buyer_Acceptance} past the configured reminder hours —
 * if a seed is missing or disabled, {@code EmailService.sendTemplated} would
 * throw and the reminder would silently never go out, so lock each row's
 * presence + enabled flag + the {@code {{var}}} placeholders the sender supplies
 * in here.
 */
class V105MigrationIT extends PostgresIntegrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Nested
    class FirstReminder {
        @Test
        @DisplayName("V105 seeds an enabled PwsCounterOfferFirstReminder referencing the sender's vars")
        void seedsFirstReminderTemplate() {
            assertSeededTemplate("PwsCounterOfferFirstReminder");
        }
    }

    @Nested
    class SecondReminder {
        @Test
        @DisplayName("V105 seeds an enabled PwsCounterOfferSecondReminder referencing the sender's vars")
        void seedsSecondReminderTemplate() {
            assertSeededTemplate("PwsCounterOfferSecondReminder");
        }
    }

    private void assertSeededTemplate(String templateKey) {
        Long count = jdbc.queryForObject(
                "SELECT count(*) FROM email.template WHERE template_key = ?",
                Long.class, templateKey);
        assertThat(count).isEqualTo(1L);

        Map<String, Object> row = jdbc.queryForMap(
                "SELECT enabled, subject, content_html, content_plain "
                        + "FROM email.template WHERE template_key = ?", templateKey);

        assertThat(row.get("enabled")).isEqualTo(Boolean.TRUE);
        assertThat((String) row.get("subject")).contains("{{offerNumber}}");

        // The seed's placeholders must match the sender's vars map keys — a drift
        // here (renamed var) would render blank in production. Guard every var
        // CounterOfferReminderService supplies against that, in both bodies.
        String html = (String) row.get("content_html");
        assertThat(html)
                .contains("{{buyerName}}")
                .contains("{{companyName}}")
                .contains("{{offerNumber}}")
                .contains("{{counterOfferUrl}}");

        String plain = (String) row.get("content_plain");
        assertThat(plain)
                .contains("{{buyerName}}")
                .contains("{{companyName}}")
                .contains("{{offerNumber}}")
                .contains("{{counterOfferUrl}}");
    }
}
