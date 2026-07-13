package com.ecoatm.salesplatform.email;

import static org.assertj.core.api.Assertions.assertThat;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Verifies the V99 migration seeded the {@code ManualQualification} email
 * template (gap-analysis 2.4 sub-feature 2, the email half) into the unified
 * {@code email.template} store.
 *
 * <p>The row is what {@code ManualQualificationEmailListener} resolves by key on
 * a Started + included {@code QualificationOverriddenEvent} — if the seed is
 * missing or disabled, {@code EmailService.sendTemplated} would throw and the
 * notification would silently never go out, so lock the row's presence +
 * enabled flag + the {@code {{var}}} placeholders the listener supplies in here.
 */
class V99MigrationIT extends PostgresIntegrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    @DisplayName("V99 seeds an enabled ManualQualification template row referencing the listener's vars")
    void seedsEnabledManualQualificationTemplate() {
        Long count = jdbc.queryForObject(
                "SELECT count(*) FROM email.template WHERE template_key = 'ManualQualification'",
                Long.class);
        assertThat(count).isEqualTo(1L);

        Map<String, Object> row = jdbc.queryForMap(
                "SELECT enabled, subject, content_html, content_plain "
                        + "FROM email.template WHERE template_key = 'ManualQualification'");

        assertThat(row.get("enabled")).isEqualTo(Boolean.TRUE);
        assertThat((String) row.get("subject")).contains("{{buyerCode}}");

        // The seed's placeholders must match the listener's vars map keys — a
        // drift here (renamed var) would render blank in production. Guard every
        // var the listener supplies against that.
        String html = (String) row.get("content_html");
        assertThat(html)
                .contains("{{buyerCode}}")
                .contains("{{schedulingAuctionId}}")
                .contains("{{qualifiedAtDisplay}}");

        String plain = (String) row.get("content_plain");
        assertThat(plain)
                .contains("{{buyerCode}}")
                .contains("{{schedulingAuctionId}}")
                .contains("{{qualifiedAtDisplay}}");
    }
}
