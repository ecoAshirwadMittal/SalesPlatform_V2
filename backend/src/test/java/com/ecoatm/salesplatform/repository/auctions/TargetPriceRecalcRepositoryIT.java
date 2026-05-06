package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@Sql(scripts = "/fixtures/auctions/recalc-seed.sql")
class TargetPriceRecalcRepositoryIT extends PostgresIntegrationTest {

    @Autowired TargetPriceRecalcRepository repo;
    @Autowired JdbcTemplate jdbc;

    @Test
    void writes_round1_max_bid_and_round2_target_price() {
        repo.recalcClosedRound(999001L, 1);

        // (ECO-A, A): MaxBid 500 (DW01/SCWC tied), factor (Flat_Amount, 5)
        //   → MaxBid+factor = 505. EB = 700; PO = 750.
        //   GREATEST(505, 700, 750) = 750.
        assertTargetPrice("ECO-A", "A", new BigDecimal("750.0000"));

        // round1_max_bid + round1_max_bid_buyer_code: DW01, SCWC tied at 500.
        // STRING_AGG with ORDER BY code → alphabetical "DW01,SCWC".
        BigDecimal maxBid = jdbc.queryForObject(
            "SELECT round1_max_bid FROM auctions.aggregated_inventory WHERE ecoid2=? AND merged_grade=?",
            BigDecimal.class, "ECO-A", "A");
        String codes = jdbc.queryForObject(
            "SELECT round1_max_bid_buyer_code FROM auctions.aggregated_inventory WHERE ecoid2=? AND merged_grade=?",
            String.class, "ECO-A", "A");
        assertThat(maxBid).isEqualByComparingTo("500.00");
        assertThat(codes).isEqualTo("DW01,SCWC");
    }

    @Test
    void greatest_picks_eb_when_eb_beats_max_bid_plus_factor_and_po() {
        // (ECO-D, A): EB 999 in fixture, no PO row. Need to add ag_inv row
        // (fixture only has ECO-A/B/C) plus a bid_data row to drive MaxBid.
        jdbc.update("""
            INSERT INTO auctions.aggregated_inventory (id, ecoid2, week_id, merged_grade, total_quantity, dw_total_quantity)
            VALUES (999099, 'ECO-D', 999001, 'A', 100, 50)
            """);

        jdbc.update("""
            INSERT INTO auctions.bid_data
              (id, bid_round_id, buyer_code_id, ecoid, merged_grade, code, company_name,
               buyer_code_type, submitted_bid_amount, bid_round, week_id)
            VALUES (999099, 999001, 1, 'ECO-D', 'A', 'DW01', 'TestCo1', 'Wholesale', 200.00, 1, 999001)
            """);

        repo.recalcClosedRound(999001L, 1);

        // GREATEST(max_bid_plus_factor, 999, 0) = 999.
        assertTargetPrice("ECO-D", "A", new BigDecimal("999.0000"));
    }

    @Test
    void greatest_picks_max_bid_plus_factor_when_eb_and_po_are_zero() {
        repo.recalcClosedRound(999001L, 1);

        // (ECO-B, A): MaxBid=800 (DW01), factor (Flat_Amount, 5) → 805.
        // No EB row for ECO-B; PO floor 100 (below). GREATEST(805, 0, 100) = 805.
        assertTargetPrice("ECO-B", "A", new BigDecimal("805.0000"));
    }

    @Test
    void factor_band_lookup_respects_round_filter() {
        repo.recalcClosedRound(999001L, 1);

        // (ECO-C, B): MaxBid=250 (DW01), factor matched via Flat_Amount [200, 1000]
        //   → 250 + 5 = 255. EB=0; PO=0. → 255.
        assertTargetPrice("ECO-C", "B", new BigDecimal("255.0000"));
    }

    @Test
    void no_factor_match_falls_back_to_max_bid() {
        // Delete the band-filter for round 2 to force "no factor matched"
        jdbc.update("DELETE FROM auctions.target_price_factor_filters WHERE bid_round_selection_filter_id = 999002");

        repo.recalcClosedRound(999001L, 1);

        // (ECO-B, A): MaxBid=800; factor → null; CASE → fallback 800.
        // EB=0; PO=100. GREATEST(800, 0, 100) = 800.
        assertTargetPrice("ECO-B", "A", new BigDecimal("800.0000"));
    }

    @Test
    void inactive_po_outside_week_range_is_ignored() {
        // The inactive PO (id 999002) is for week 999002 (Wk12). Confirm it does
        // NOT leak into the target price for week 999001 (Wk14).
        repo.recalcClosedRound(999001L, 1);

        // If the inactive PO leaked, ECO-A target would be 9999. Verify it's 750.
        assertTargetPrice("ECO-A", "A", new BigDecimal("750.0000"));
    }

    @Test
    void writes_round3_columns_when_round_2_closes() {
        // Promote round-1 fixture rows into round 2. Scope to week_id=999001 to
        // avoid PK collision with seeded production-style bid_data already in
        // the dev DB. bid_round_id 999004 is the only round-2 bid_round in the
        // fixture (sa=999002).
        jdbc.update("""
            INSERT INTO auctions.bid_data
              (id, bid_round_id, buyer_code_id, ecoid, merged_grade, code, company_name,
               buyer_code_type, submitted_bid_amount, bid_round, week_id)
            SELECT id + 1000, 999004, buyer_code_id, ecoid, merged_grade, code, company_name,
                   buyer_code_type, submitted_bid_amount, 2, week_id
              FROM auctions.bid_data WHERE bid_round = 1 AND week_id = 999001
            """);
        jdbc.update("UPDATE auctions.scheduling_auctions SET round_status='Closed' WHERE id=999002");

        repo.recalcClosedRound(999002L, 2);

        BigDecimal r3 = jdbc.queryForObject(
            "SELECT round3_target_price FROM auctions.aggregated_inventory WHERE ecoid2=? AND merged_grade=?",
            BigDecimal.class, "ECO-A", "A");
        assertThat(r3).isEqualByComparingTo("750.0000");

        BigDecimal r2max = jdbc.queryForObject(
            "SELECT round2_max_bid FROM auctions.aggregated_inventory WHERE ecoid2=? AND merged_grade=?",
            BigDecimal.class, "ECO-A", "A");
        assertThat(r2max).isEqualByComparingTo("500.00");
    }

    private void assertTargetPrice(String ecoid, String grade, BigDecimal expected) {
        BigDecimal actual = jdbc.queryForObject(
            "SELECT round2_target_price FROM auctions.aggregated_inventory WHERE ecoid2=? AND merged_grade=?",
            BigDecimal.class, ecoid, grade);
        assertThat(actual)
            .as("round2_target_price for (%s, %s)", ecoid, grade)
            .isEqualByComparingTo(expected);
    }
}
