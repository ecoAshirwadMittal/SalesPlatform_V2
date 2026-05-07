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
 * Integration coverage for {@link R3BuyerQualificationRepository}. Asserts
 * all qualification branches of the R3 selection-criteria CTE against the
 * r3-lifecycle-seed.sql fixture.
 *
 * <p>Default BRSF for round=3: bid_percentage_variation=5,
 * bid_amount_variation=1.00, rank_qualification_limit=3. Tests that need
 * a different BRSF state UPDATE the row and rely on {@code @Transactional}
 * class-level rollback to isolate the change.
 */
@Transactional
@Sql(scripts = "/fixtures/auctions/r3-lifecycle-seed.sql",
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class R3BuyerQualificationRepositoryIT extends PostgresIntegrationTest {

    @Autowired R3BuyerQualificationRepository repo;
    @Autowired JdbcTemplate jdbc;

    private static final long R3_SA_ID = 6003L;

    @Test
    @DisplayName("ACME-WH qualifies via percentage variation (R2 bid 105 vs target 100, pct=5)")
    void percentage_branch_qualifies() {
        Set<Long> ids = repo.qualifiedBuyerCodes(R3_SA_ID);
        assertThat(ids).contains(60001L);  // ACME-WH
    }

    @Test
    @DisplayName("ACME-DW qualifies via percentage variation (R2 bid 88 vs DW target 90, pct=5)")
    void percentage_branch_dw_uses_dw_target() {
        Set<Long> ids = repo.qualifiedBuyerCodes(R3_SA_ID);
        assertThat(ids).contains(60002L);  // ACME-DW
    }

    @Test
    @DisplayName("BETA-WH qualifies via rank limit (rank=2 <= limit=3)")
    void rank_branch_qualifies() {
        Set<Long> ids = repo.qualifiedBuyerCodes(R3_SA_ID);
        assertThat(ids).contains(60003L);  // BETA-WH
    }

    @Test
    @DisplayName("GAMMA-WH does NOT qualify — bid below pct/amt thresholds AND rank>limit")
    void no_branch_matches_excluded() {
        Set<Long> ids = repo.qualifiedBuyerCodes(R3_SA_ID);
        assertThat(ids).doesNotContain(60004L);  // GAMMA-WH
    }

    @Test
    @DisplayName("ROW_NUMBER() keeps R2 over R1 when buyer has both")
    void latest_bid_is_r2_when_both_exist() {
        // ACME-WH has R1 (80) + R2 (105). The CTE should use 105 (R2 latest).
        // 105 >= 100 - (100*5/100) = 95 → qualifies
        Set<Long> ids = repo.qualifiedBuyerCodes(R3_SA_ID);
        assertThat(ids).contains(60001L);
    }

    @Test
    @DisplayName("rerun is idempotent — same R3 SA produces same set")
    void rerun_idempotent() {
        Set<Long> a = repo.qualifiedBuyerCodes(R3_SA_ID);
        Set<Long> b = repo.qualifiedBuyerCodes(R3_SA_ID);
        assertThat(a).isEqualTo(b);
    }

    @Test
    @DisplayName("all-NULL filters → fall-through qualify everyone with bids")
    void all_null_filters_qualifies_all() {
        // Override BRSF round=3 to NULL all three
        jdbc.update("UPDATE auctions.bid_round_selection_filters " +
                    "SET bid_percentage_variation = NULL, bid_amount_variation = NULL, " +
                    "    rank_qualification_limit = NULL " +
                    "WHERE round = 3");

        Set<Long> ids = repo.qualifiedBuyerCodes(R3_SA_ID);
        // Everyone with any submitted nonzero bid in R1/R2 is in (DISTINCT buyer_code_id):
        // ACME-WH (R1+R2), ACME-DW (R2), BETA-WH (R2), GAMMA-WH (R2), EPSI-WH (R1)
        assertThat(ids).contains(60001L, 60002L, 60003L, 60004L, 60007L);
    }

    @Test
    @DisplayName("amount-variation branch qualifies when latest_bid >= target - amount")
    void amount_branch_qualifies() {
        // Set up: only amount branch active
        jdbc.update("UPDATE auctions.bid_round_selection_filters " +
                    "SET bid_percentage_variation = NULL, bid_amount_variation = 1.00, " +
                    "    rank_qualification_limit = NULL " +
                    "WHERE round = 3");

        Set<Long> ids = repo.qualifiedBuyerCodes(R3_SA_ID);
        // ACME-WH (105 >= 100-1=99) qualifies
        assertThat(ids).contains(60001L);
        // GAMMA-WH (10 < 75-1=74) does not
        assertThat(ids).doesNotContain(60004L);
    }

    @Test
    @DisplayName("non-DW/WH buyer codes are excluded")
    void non_dw_wh_excluded() {
        // No PO-type buyer codes seeded in fixture; sanity check that all returned ids are DW/WH
        Set<Long> ids = repo.qualifiedBuyerCodes(R3_SA_ID);
        // Verify each returned buyer code is DW or WH — no PO/other types
        for (Long id : ids) {
            String type = jdbc.queryForObject(
                "SELECT buyer_code_type FROM buyer_mgmt.buyer_codes WHERE id = ?",
                String.class, id);
            assertThat(type).isIn("Wholesale", "Data_Wipe");
        }
    }
}
