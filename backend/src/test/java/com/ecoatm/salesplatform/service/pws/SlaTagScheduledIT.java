package com.ecoatm.salesplatform.service.pws;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Proves the SLA-tag CRON path actually commits its tag write.
 *
 * <p>Unlike {@link SlaTagServiceIT} (class-level {@code @Transactional}, whose
 * ambient transaction MASKS this defect), this IT is deliberately
 * NON-transactional and drives the scheduled entry point
 * {@link SlaTagService#scheduledTagOverdueOffers()}. That method is not
 * {@code @Transactional} and self-invokes {@code tagOverdueOffers()}, so the
 * self-invocation bypasses the Spring transactional proxy — the
 * {@code @Transactional} on {@code tagOverdueOffers()} never applies. With no
 * ambient transaction, the underlying {@code @Modifying} update
 * {@code OfferRepository.tagOverdueOffers} must carry its OWN
 * {@code @Transactional} or it throws
 * {@code jakarta.persistence.TransactionRequiredException} and nothing is tagged.
 *
 * <ul>
 *   <li><b>Pre-fix</b> (no {@code @Transactional} on the repo method): no offer is
 *       ever flagged — the underlying update raises
 *       {@code TransactionRequiredException} — so this assertion fails.</li>
 *   <li><b>Post-fix</b>: the seeded overdue offer is flagged and the assertion
 *       passes.</li>
 * </ul>
 *
 * <p><b>Determinism ({@code auctions.lifecycle.enabled=false}).</b> The ShedLock
 * single-leader guard is orthogonal to the transaction bug (the defect is the
 * self-invocation, present however the method is invoked). Left on, it makes the
 * direct call flaky: the {@code @Scheduled} SLA task also auto-fires at startup
 * and can hold the {@code pwsSlaTag} lock, silently skipping the direct call.
 * Disabling {@code SchedulingConfig} (the only ShedLock {@code LockProvider}
 * producer) removes that lock so the direct call always runs the body — still
 * exercising the exact self-invocation path.
 *
 * <p><b>Shared-dev-DB footprint.</b> {@code OfferRepository.tagOverdueOffers} is a
 * table-wide bulk UPDATE, so any committing invocation flags EVERY currently
 * overdue SLA-tracked offer — exactly what the production cron does. On this local
 * fallback DB (Docker down → shared {@code salesplatform_dev}) that means real
 * offers get flagged too; {@link #cleanup()} restores {@code offer_beyond_sla} to
 * its all-false baseline (the V103 column default — no committed SLA run precedes
 * this IT) and deletes the seeded offer. The {@code BEFORE UPDATE}
 * {@code trg_update_updated_date} trigger re-stamps {@code updated_date} on the
 * touched rows; that is an inherent, accepted artifact of exercising a table-wide
 * mutation non-transactionally. Under Docker/CI the base class uses a throwaway
 * Testcontainers database, so there is no shared-state impact at all.
 *
 * <p><b>Trigger caveat (seed).</b> The same {@code BEFORE UPDATE} trigger resets
 * {@code updated_date = NOW()} on every UPDATE, so the overdue offer is seeded via
 * {@code INSERT} with a past {@code updated_date}, never back-dated by an UPDATE.
 */
@TestPropertySource(properties = {
        "pws.sla-tag.enabled=true",
        "auctions.lifecycle.enabled=false",
        // Push the next scheduled tick 24h out so the SLA task auto-fires at most
        // once (at startup); only that fire + the direct call commit, both cleaned
        // by @AfterEach. (fixed-delay governs subsequent ticks, not the first.)
        "pws.sla-tag.fixed-delay-ms=86400000"
})
class SlaTagScheduledIT extends PostgresIntegrationTest {

    @Autowired
    private SlaTagService slaTagService;

    @Autowired
    private JdbcTemplate jdbc;

    private Long seededOfferId;

    @AfterEach
    void cleanup() {
        if (seededOfferId != null) {
            jdbc.update("DELETE FROM pws.offer WHERE id = ?", seededOfferId);
        }
        // Restore the all-false baseline for every offer the bulk tag flagged
        // (the seeded row is already gone). Leaves the shared dev DB as found.
        jdbc.update("UPDATE pws.offer SET offer_beyond_sla = false WHERE offer_beyond_sla = true");
    }

    @Test
    void scheduledTick_commits_the_sla_tag_on_an_overdue_offer() {
        // Seed one overdue Sales_Review offer via INSERT (past updated_date — the
        // BEFORE-UPDATE trigger forbids back-dating via UPDATE). 30 days ago is
        // safely before any 2-business-day cutoff regardless of the run date.
        seededOfferId = jdbc.queryForObject(
                "INSERT INTO pws.offer (offer_type, status, updated_date, created_date, offer_beyond_sla) "
                        + "VALUES ('SYSTEM', 'Sales_Review', NOW() - INTERVAL '30 days', "
                        + "NOW() - INTERVAL '30 days', false) RETURNING id",
                Long.class);

        // Scheduled entry point (NOT tagOverdueOffers()) — the self-invocation
        // path. Pre-fix this leaves the offer untagged (the underlying update
        // throws TransactionRequiredException); post-fix it commits the tag write
        // via the repo method's own @Transactional.
        slaTagService.scheduledTagOverdueOffers();

        Boolean flagged = jdbc.queryForObject(
                "SELECT offer_beyond_sla FROM pws.offer WHERE id = ?", Boolean.class, seededOfferId);
        assertThat(flagged)
                .as("scheduled cron committed the SLA tag on the overdue offer")
                .isTrue();
    }
}
