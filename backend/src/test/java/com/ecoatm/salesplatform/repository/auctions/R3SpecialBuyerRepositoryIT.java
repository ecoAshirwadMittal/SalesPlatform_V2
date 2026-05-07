package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration coverage for {@link R3SpecialBuyerRepository}. Asserts the
 * special-treatment CTE's "all DW/WH codes must qualify" semantic for R3,
 * where prior_round ∈ {1, 2} (rounds before the current round=3).
 *
 * <p>Parallel shape to {@code R2SpecialBuyerRepositoryIT}. The key difference:
 * with p.round=3, the {@code sa.round < p.round} predicate in
 * {@code prior_round_bids} captures both R1 and R2 — any bid in either
 * prior round disqualifies the buyer's entire DW/WH set.
 *
 * <p>Fixture: {@code r3-lifecycle-seed.sql} seeds:
 * <ul>
 *   <li>Delta SpecialA (buyer 6004) — DELTA-WH (60005) and DELTA-DW (60006),
 *       no bid_round rows at all → STB-eligible</li>
 *   <li>Epsilon SpecialB (buyer 6005) — EPSI-WH (60007) has an R1 bid_round,
 *       EPSI-DW (60008) has no bid_round → buyer-level disqualification applies
 *       to both codes</li>
 *   <li>Acme/Beta/Gamma (buyers 6001-6003) — not is_special_buyer → always excluded</li>
 * </ul>
 */
@Transactional
@Sql(scripts = "/fixtures/auctions/r3-lifecycle-seed.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class R3SpecialBuyerRepositoryIT extends PostgresIntegrationTest {

    private static final long R3_SA_ID = 6003L;

    @Autowired R3SpecialBuyerRepository repo;
    @Autowired JdbcTemplate jdbc;

    @Test
    @DisplayName("Delta SpecialA's codes (no R1/R2 prior bids) qualify as STB")
    void zero_prior_bids_is_stb() {
        Set<Long> ids = repo.specialTreatmentBuyerCodes(R3_SA_ID);
        // Delta SpecialA: DELTA-WH (60005), DELTA-DW (60006) — no bid_rounds at all
        assertThat(ids).contains(60005L, 60006L);
    }

    @Test
    @DisplayName("Epsilon SpecialB has prior R1 bid on EPSI-WH → entire DW/WH set disqualified")
    void any_prior_bid_disqualifies_whole_set() {
        Set<Long> ids = repo.specialTreatmentBuyerCodes(R3_SA_ID);
        // EPSI-WH (60007) had a submitted R1 bid → the buyer's prior_round_bids count > 0
        // bool_and(is_stb) = FALSE for buyer 6005 → neither EPSI-WH nor EPSI-DW appears
        assertThat(ids).doesNotContain(60007L, 60008L);
    }

    @Test
    @DisplayName("Non-special buyers (Acme/Beta/Gamma) excluded entirely")
    void non_special_buyers_excluded() {
        Set<Long> ids = repo.specialTreatmentBuyerCodes(R3_SA_ID);
        // is_special_buyer=FALSE buyers never enter the special_buyers CTE
        assertThat(ids).doesNotContain(60001L, 60002L, 60003L, 60004L);
    }

    @Test
    @DisplayName("stb_allow_all_buyers_override=TRUE returns all special-buyer codes regardless of prior bids")
    void override_returns_all_special_codes() {
        jdbc.update("UPDATE auctions.bid_round_selection_filters " +
                    "SET stb_allow_all_buyers_override = TRUE WHERE round = 3");

        Set<Long> ids = repo.specialTreatmentBuyerCodes(R3_SA_ID);
        // Override short-circuits the prior-bid check — both Delta AND Epsilon's codes returned
        assertThat(ids).contains(60005L, 60006L, 60007L, 60008L);
        // Non-special buyers still excluded even under override
        assertThat(ids).doesNotContain(60001L, 60002L, 60003L, 60004L);
    }

    @Test
    @DisplayName("rerun is idempotent — same R3 SA produces same set")
    void rerun_idempotent() {
        Set<Long> a = repo.specialTreatmentBuyerCodes(R3_SA_ID);
        Set<Long> b = repo.specialTreatmentBuyerCodes(R3_SA_ID);
        assertThat(a).isEqualTo(b);
    }
}
