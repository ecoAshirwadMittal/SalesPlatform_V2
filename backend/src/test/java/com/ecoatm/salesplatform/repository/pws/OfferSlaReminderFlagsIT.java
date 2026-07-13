package com.ecoatm.salesplatform.repository.pws;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.model.pws.Offer;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Round-trips the three V103 boolean flags on {@link Offer} against a real
 * PostgreSQL database and proves the previously-broken
 * {@code PWSAdminController.setSLATags} SQL now runs.
 *
 * <p>{@code @Transactional} rolls back every seed + write, so the shared dev DB
 * is left untouched.
 */
@Transactional
class OfferSlaReminderFlagsIT extends PostgresIntegrationTest {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void flags_default_false_and_round_trip_to_true() {
        Offer offer = new Offer();
        offer.setOfferType("SYSTEM");
        offer.setStatus("Sales_Review");
        Long id = offerRepository.saveAndFlush(offer).getId();

        // Evict so the reload reflects the DB defaults, not the in-session object.
        em.clear();
        Offer reloaded = offerRepository.findById(id).orElseThrow();
        assertThat(reloaded.isOfferBeyondSla()).isFalse();
        assertThat(reloaded.isFirstReminderSent()).isFalse();
        assertThat(reloaded.isSecondReminderSent()).isFalse();

        reloaded.setOfferBeyondSla(true);
        reloaded.setFirstReminderSent(true);
        reloaded.setSecondReminderSent(true);
        offerRepository.saveAndFlush(reloaded);

        em.clear();
        Offer afterUpdate = offerRepository.findById(id).orElseThrow();
        assertThat(afterUpdate.isOfferBeyondSla()).isTrue();
        assertThat(afterUpdate.isFirstReminderSent()).isTrue();
        assertThat(afterUpdate.isSecondReminderSent()).isTrue();
    }

    @Test
    void setSLATags_and_removeSLATags_statements_execute_without_missing_column_error() {
        // Verbatim PWSAdminController.setSLATags / removeSLATags UPDATEs. Before V103
        // these threw `column "offer_beyond_sla" does not exist` at runtime; the test
        // erroring on that exception is the regression guard. A BEFORE UPDATE trigger
        // on pws.offer resets updated_date = NOW(), so an overdue-row flip cannot be
        // seeded here — the deterministic column write is covered below and the
        // JPA round-trip proves default/read-back.
        int setCount = jdbc.update(
                "UPDATE pws.offer SET offer_beyond_sla = true, updated_date = NOW() "
                + "WHERE status IN ('Sales_Review', 'SALES_REVIEW', 'Buyer_Acceptance', 'BUYER_ACCEPTANCE') "
                + "AND updated_date < NOW() - INTERVAL '2 days' AND (offer_beyond_sla IS NULL OR offer_beyond_sla = false)");
        assertThat(setCount).isGreaterThanOrEqualTo(0);

        int removeCount = jdbc.update(
                "UPDATE pws.offer SET offer_beyond_sla = false, updated_date = NOW() "
                + "WHERE status IN ('Sales_Review', 'SALES_REVIEW', 'Buyer_Acceptance', 'BUYER_ACCEPTANCE') "
                + "AND offer_beyond_sla = true");
        assertThat(removeCount).isGreaterThanOrEqualTo(0);
    }

    @Test
    void offer_beyond_sla_is_writable_via_raw_jdbc() {
        Offer offer = new Offer();
        offer.setOfferType("SYSTEM");
        offer.setStatus("Sales_Review");
        Long id = offerRepository.saveAndFlush(offer).getId();

        int updated = jdbc.update("UPDATE pws.offer SET offer_beyond_sla = true WHERE id = ?", id);
        assertThat(updated).isEqualTo(1);

        Boolean flagged = jdbc.queryForObject(
                "SELECT offer_beyond_sla FROM pws.offer WHERE id = ?", Boolean.class, id);
        assertThat(flagged).isTrue();
    }
}
