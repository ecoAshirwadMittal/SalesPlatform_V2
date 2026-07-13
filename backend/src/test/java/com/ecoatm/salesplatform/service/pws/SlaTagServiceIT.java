package com.ecoatm.salesplatform.service.pws;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end SLA-tag coverage against a real PostgreSQL database (V1..V104
 * applied by Flyway under the {@code pg-test} profile). Proves the refactored
 * {@code SlaTagService} flags exactly the overdue SLA-tracked offers and that
 * {@code removeAllSlaTags} clears them.
 *
 * <p><b>Trigger caveat.</b> A {@code BEFORE UPDATE} trigger on {@code pws.offer}
 * resets {@code updated_date = NOW()} on every UPDATE — so an "overdue" offer is
 * seeded via {@code INSERT} with a past {@code updated_date} (the trigger fires
 * only on UPDATE), never back-dated via an UPDATE.
 *
 * <p>{@code @Transactional} rolls back the seed + tag writes, leaving the shared
 * dev DB untouched. Assertions are scoped to the seeded rows by id (the dev DB
 * may hold other SLA-tracked offers that the service also tags in this rolled-
 * back transaction).
 */
@Transactional
class SlaTagServiceIT extends PostgresIntegrationTest {

    @Autowired
    private SlaTagService slaTagService;

    @Autowired
    private JdbcTemplate jdbc;

    private Long insertOffer(String status, String updatedInterval, boolean beyondSla) {
        return jdbc.queryForObject(
                "INSERT INTO pws.offer (offer_type, status, updated_date, created_date, offer_beyond_sla) "
                + "VALUES ('SYSTEM', ?, NOW() - (?::interval), NOW() - (?::interval), ?) RETURNING id",
                Long.class, status, updatedInterval, updatedInterval, beyondSla);
    }

    private boolean flag(Long offerId) {
        return Boolean.TRUE.equals(jdbc.queryForObject(
                "SELECT offer_beyond_sla FROM pws.offer WHERE id = ?", Boolean.class, offerId));
    }

    @Test
    void tagOverdueOffers_flags_only_the_overdue_sla_tracked_offer() {
        Long overdueReview = insertOffer("Sales_Review", "30 days", false);
        Long recentReview = insertOffer("Sales_Review", "0 days", false);
        Long overdueNonReview = insertOffer("Ordered", "30 days", false);

        int tagged = slaTagService.tagOverdueOffers();

        assertThat(tagged).isGreaterThanOrEqualTo(1);
        assertThat(flag(overdueReview)).as("overdue Sales_Review offer is tagged").isTrue();
        assertThat(flag(recentReview)).as("recently-updated offer is untouched").isFalse();
        assertThat(flag(overdueNonReview)).as("non-SLA-tracked status is untouched").isFalse();
    }

    @Test
    void removeAllSlaTags_clears_the_flag_on_sla_tracked_offers() {
        Long taggedReview = insertOffer("Sales_Review", "30 days", true);
        assertThat(flag(taggedReview)).isTrue();

        int cleared = slaTagService.removeAllSlaTags();

        assertThat(cleared).isGreaterThanOrEqualTo(1);
        assertThat(flag(taggedReview)).as("SLA tag cleared").isFalse();
    }
}
