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
        assertTargetPrice("1001", "A", new BigDecimal("750.0000"));

        // round1_max_bid + round1_max_bid_buyer_code: DW01, SCWC tied at 500.
        // STRING_AGG with ORDER BY code → alphabetical "DW01,SCWC".
        BigDecimal maxBid = jdbc.queryForObject(
            "SELECT round1_max_bid FROM auctions.aggregated_inventory WHERE ecoid2=? AND merged_grade=?",
            BigDecimal.class, "1001", "A");
        String codes = jdbc.queryForObject(
            "SELECT round1_max_bid_buyer_code FROM auctions.aggregated_inventory WHERE ecoid2=? AND merged_grade=?",
            String.class, "1001", "A");
        assertThat(maxBid).isEqualByComparingTo("500.00");
        assertThat(codes).isEqualTo("DW01,SCWC");
    }

    @Test
    void greatest_picks_eb_when_eb_beats_max_bid_plus_factor_and_po() {
        // (ECO-D, A): EB 999 in fixture, no PO row. Need to add ag_inv row
        // (fixture only has ECO-A/B/C) plus a bid_data row to drive MaxBid.
        jdbc.update("""
            INSERT INTO auctions.aggregated_inventory (id, ecoid2, week_id, merged_grade, total_quantity, dw_total_quantity)
            VALUES (999099, '1004', 999001, 'A', 100, 50)
            """);

        jdbc.update("""
            INSERT INTO auctions.bid_data
              (id, bid_round_id, buyer_code_id, ecoid, merged_grade, code, company_name,
               buyer_code_type, submitted_bid_amount, bid_round, week_id)
            VALUES (999099, 999001, 1, '1004', 'A', 'DW01', 'TestCo1', 'Wholesale', 200.00, 1, 999001)
            """);

        repo.recalcClosedRound(999001L, 1);

        // GREATEST(max_bid_plus_factor, 999, 0) = 999.
        assertTargetPrice("1004", "A", new BigDecimal("999.0000"));
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
    void percentage_factor_branch_computes_percent_of_max_bid() {
        // (ECO-C, A): MaxBid=150 falls in [0, 200] band → Percentage_Factor 10.
        // Formula: ROUND(150 * 10 / 100, 2) = 15.00. No EB row for ECO-C, no PO.
        // GREATEST(15.00, 0, 0) = 15.00. This pins the current Percentage_Factor
        // arithmetic. Note: a value of 15 means "10% of MaxBid IS the result",
        // not "MaxBid plus 10%". Change this test if the business rule changes.
        repo.recalcClosedRound(999001L, 1);

        assertTargetPrice("ECO-C", "A", new BigDecimal("15.0000"));
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
        assertTargetPrice("1001", "A", new BigDecimal("750.0000"));
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
            BigDecimal.class, "1001", "A");
        assertThat(r3).isEqualByComparingTo("750.0000");

        BigDecimal r2max = jdbc.queryForObject(
            "SELECT round2_max_bid FROM auctions.aggregated_inventory WHERE ecoid2=? AND merged_grade=?",
            BigDecimal.class, "1001", "A");
        assertThat(r2max).isEqualByComparingTo("500.00");
    }

    @Test
    void po_floor_matches_auction_week_by_business_weekId_not_surrogate() {
        // Regression for the 4C PO-floor week-matching bug (task #37): the po_max
        // CTE must match a PO to the auction week by the *business* weekId
        // (mdm.week.week_id = year*100 + weekNumber, chronological & monotonic
        // with week_start_datetime), NOT the non-chronological surrogate
        // mdm.week.id. This is the 4C consumer side of the exact week-model bug
        // gap 0.1 fixed on the PO-overlap (producer) side
        // (PurchaseOrderRepository.findOverlappingWeekRange).
        //
        // Surrogate ids are deliberately scrambled vs. chronological (business)
        // order — exactly the shape the V65 seed produces (surrogate assigned via
        // GROUP BY with no ORDER BY):
        //   biz Wk10 = 999910 → surrogate 999550   (decoy PO "from")
        //   biz Wk11 = 999911 → surrogate 999600   (decoy PO "to")
        //   biz Wk20 = 999920 → surrogate 999520   (covering PO "from")
        //   biz Wk21 = 999921 → surrogate 999599   (AUCTION week)
        //   biz Wk22 = 999922 → surrogate 999521   (covering PO "to")
        jdbc.update("""
            INSERT INTO mdm.week (id, week_id, year, week_number, week_start_datetime, week_end_datetime, week_display, week_display_short, week_number_string)
            VALUES
              (999550, 999910, 2026, 10, '2026-03-02 00:00:00+00', '2026-03-08 23:59:59+00', '2026 / Wk10', 'Wk10', '10'),
              (999600, 999911, 2026, 11, '2026-03-09 00:00:00+00', '2026-03-15 23:59:59+00', '2026 / Wk11', 'Wk11', '11'),
              (999520, 999920, 2026, 20, '2026-05-11 00:00:00+00', '2026-05-17 23:59:59+00', '2026 / Wk20', 'Wk20', '20'),
              (999599, 999921, 2026, 21, '2026-05-18 00:00:00+00', '2026-05-24 23:59:59+00', '2026 / Wk21', 'Wk21', '21'),
              (999521, 999922, 2026, 22, '2026-05-25 00:00:00+00', '2026-05-31 23:59:59+00', '2026 / Wk22', 'Wk22', '22')
            """);

        // Auction + closed round-1 SA on the AUCTION week (surrogate 999599 / biz 999921).
        jdbc.update("""
            INSERT INTO auctions.auctions (id, auction_title, auction_status, week_id)
            VALUES (999501, 'Task37 Week-Match Auction', 'Started', 999599)
            """);
        jdbc.update("""
            INSERT INTO auctions.scheduling_auctions (id, auction_id, round, round_status, ranking_status, target_price_status)
            VALUES (999501, 999501, 1, 'Closed', 'PENDING', 'PENDING')
            """);
        jdbc.update("""
            INSERT INTO auctions.bid_rounds (id, scheduling_auction_id, buyer_code_id, week_id)
            VALUES (999501, 999501, 1, 999599)
            """);

        // ECO-Z ag_inv on the auction week + one bid → MaxBid 300, factor
        // Flat_Amount +5 (band [200,1000]) → max_bid_plus_factor 305. No EB row.
        jdbc.update("""
            INSERT INTO auctions.aggregated_inventory (id, ecoid2, week_id, merged_grade, total_quantity, dw_total_quantity)
            VALUES (999501, 'ECO-Z', 999599, 'A', 100, 50)
            """);
        jdbc.update("""
            INSERT INTO auctions.bid_data
              (id, bid_round_id, buyer_code_id, ecoid, merged_grade, code, company_name,
               buyer_code_type, submitted_bid_amount, bid_round, week_id)
            VALUES (999501, 999501, 1, 'ECO-Z', 'A', 'DW01', 'TestCoZ', 'Wholesale', 300.00, 1, 999599)
            """);

        // COVERING PO: business [Wk20..Wk22] = [999920..999922] CONTAINS the
        // auction's business week 999921. But its SURROGATE range [999520..999521]
        // does NOT contain the auction surrogate 999599 → the pre-fix surrogate
        // `p.week_id BETWEEN po.week_from_id AND po.week_to_id` MISSES this PO.
        jdbc.update("""
            INSERT INTO auctions.purchase_order (id, week_from_id, week_to_id, week_range_label, valid_year_week, total_records)
            VALUES (999501, 999520, 999521, 'Wk20-22 2026 (covering)', TRUE, 1)
            """);
        jdbc.update("""
            INSERT INTO auctions.po_detail (id, purchase_order_id, buyer_code_id, product_id, grade, model_name, price, qty_cap)
            VALUES (999501, 999501, 1, 'ECO-Z', 'A', 'ModelZ', 5000.0000, 10)
            """);

        // DECOY PO: business [Wk10..Wk11] = [999910..999911] does NOT contain the
        // auction's business week 999921. But its SURROGATE range [999550..999600]
        // DOES contain surrogate 999599 → the pre-fix surrogate BETWEEN WRONGLY
        // picks this PO's price (8000) as the floor.
        jdbc.update("""
            INSERT INTO auctions.purchase_order (id, week_from_id, week_to_id, week_range_label, valid_year_week, total_records)
            VALUES (999502, 999550, 999600, 'Wk10-11 2026 (decoy)', TRUE, 1)
            """);
        jdbc.update("""
            INSERT INTO auctions.po_detail (id, purchase_order_id, buyer_code_id, product_id, grade, model_name, price, qty_cap)
            VALUES (999502, 999502, 1, 'ECO-Z', 'A', 'ModelZ', 8000.0000, 10)
            """);

        repo.recalcClosedRound(999501L, 1);

        // Correct floor is the chronologically-covering PO (5000), NOT the decoy
        // (8000): GREATEST(max_bid_plus_factor=305, EB=0, PO=5000) = 5000.
        // Pre-fix the surrogate BETWEEN matches the decoy (8000) and misses the
        // covering PO → 8000. So this assertion FAILS pre-fix, PASSES post-fix.
        assertTargetPrice("ECO-Z", "A", new BigDecimal("5000.0000"));
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
