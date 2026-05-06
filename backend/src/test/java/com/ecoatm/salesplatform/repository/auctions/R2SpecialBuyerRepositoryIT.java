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
 * Integration coverage for {@link R2SpecialBuyerRepository}. Asserts the
 * special-treatment CTE's "all DW/WH codes must qualify" semantic from
 * design §3.7 + §7.2.
 *
 * <p>Loads the T6 base fixture ({@code r2-init-seed.sql}) plus a T7
 * extension ({@code r2-special-buyer-extra-seed.sql}) that adds 4 buyers
 * exercising the override / no-prior-bids / mixed-codes /
 * non-special-buyer branches.
 */
@Transactional
@Sql(scripts = {
    "/fixtures/auctions/r2-init-seed.sql",
    "/fixtures/auctions/r2-special-buyer-extra-seed.sql"
})
class R2SpecialBuyerRepositoryIT extends PostgresIntegrationTest {

    private static final long R2_SA_ID = 999102L;
    private static final long BRSF_ID  = 999102L;

    // Buyer A — all codes STB (no prior bids on any code)
    private static final long CODE_A_WH = 999111L;
    private static final long CODE_A_DW = 999112L;

    // Buyer B — mixed (one code has prior bid → buyer NOT all-codes STB)
    private static final long CODE_B_WH_BID    = 999113L; // submitted R1 bid
    private static final long CODE_B_DW_NO_BID = 999114L;

    // Buyer C — single code, no bid
    private static final long CODE_C_WH = 999115L;

    // Buyer D — not special at all
    private static final long CODE_D_WH = 999116L;

    @Autowired R2SpecialBuyerRepository repo;
    @Autowired JdbcTemplate jdbc;

    @Test
    @DisplayName("STB override: stb_allow_all_buyers_override=TRUE returns all special-buyer codes")
    void override_returns_all_codes() {
        // Override TRUE bypasses the prior-bid check — every special buyer's
        // DW/WH codes are eligible regardless of bid history. Buyer B's WH
        // code (which DID bid in R1) is therefore returned alongside its DW
        // code, and the bool_and aggregate evaluates TRUE for buyer B.
        //
        // V18 seeds production-style is_special_buyer=TRUE rows whose codes
        // also appear under the override branch. Use inclusion semantics
        // (contains + doesNotContain) rather than exact-set, so the test is
        // robust against the global seed.
        jdbc.update(
            "UPDATE auctions.bid_round_selection_filters "
                + "SET stb_allow_all_buyers_override = TRUE WHERE id = ?",
            BRSF_ID);

        Set<Long> result = repo.specialTreatmentBuyerCodes(R2_SA_ID);

        assertThat(result).contains(
            CODE_A_WH, CODE_A_DW,
            CODE_B_WH_BID, CODE_B_DW_NO_BID,
            CODE_C_WH);
        // Regular (non-special) buyer never enters the result.
        assertThat(result).doesNotContain(CODE_D_WH);
    }

    @Test
    @DisplayName("zero prior-round bids -> STB-eligible (Buyer A both codes, Buyer C single code)")
    void no_prior_bids_is_stb() {
        Set<Long> result = repo.specialTreatmentBuyerCodes(R2_SA_ID);

        // Buyer A: both DW/WH codes never bid -> both returned.
        assertThat(result).contains(CODE_A_WH, CODE_A_DW);
        // Buyer C: single code, never bid -> returned.
        assertThat(result).contains(CODE_C_WH);
    }

    @Test
    @DisplayName("any prior-round bid disqualifies the buyer's whole DW/WH set (Buyer B both codes excluded)")
    void any_prior_bid_disqualifies() {
        Set<Long> result = repo.specialTreatmentBuyerCodes(R2_SA_ID);

        // Buyer B's WH code bid in R1 -> the WH code itself is NOT-STB.
        // Per design 3.7, ALL of buyer B's DW/WH codes must be STB for ANY
        // to appear -> NEITHER 999113 (WH, bid) NOR 999114 (DW, no bid) is
        // returned. This is the load-bearing semantic.
        assertThat(result).doesNotContain(CODE_B_WH_BID, CODE_B_DW_NO_BID);
    }

    @Test
    @DisplayName("non-special buyers excluded entirely (is_special_buyer=FALSE)")
    void non_special_buyers_excluded() {
        Set<Long> result = repo.specialTreatmentBuyerCodes(R2_SA_ID);

        // Buyer D is NOT is_special_buyer -> never enters the special_buyers
        // CTE, so its code is filtered upstream of every downstream step.
        assertThat(result).doesNotContain(CODE_D_WH);

        // Sanity: T6 fixture's regular buyers (999101-999103) also excluded.
        assertThat(result).doesNotContain(999101L, 999102L, 999103L, 999104L, 999105L);
    }
}
