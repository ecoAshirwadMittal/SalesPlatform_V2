package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration coverage for {@link BidDataForAllAERepository} — the
 * special-buyer bulk INSERT that ports {@code SUB_CreateBidDataForAllAE}
 * (design §3.8 + §7.4).
 *
 * <p>The repository is invoked per-buyer-code by {@code BidDataForAllAEService}
 * (Task 10). The CTE is therefore parameterized by {@code :buyer_code_id}
 * rather than joining to {@code qualified_buyer_codes}, so this IT does not
 * need to seed QBC rows — it only needs the AE rows, the bid_round, and the
 * bid_data_doc referenced by the INSERT target.
 *
 * <p>Reuses the T6 {@code r2-init-seed.sql} (R1 SA + R2 SA + 2 AE rows) plus
 * the T7 {@code r2-special-buyer-extra-seed.sql} (special buyers + DW/WH
 * codes 999111-999115). Test setup helpers create a {@code bid_round} +
 * {@code bid_data_doc} per scenario.
 */
@Transactional
@Sql(scripts = {
    "/fixtures/auctions/r2-init-seed.sql",
    "/fixtures/auctions/r2-special-buyer-extra-seed.sql"
})
class BidDataForAllAERepositoryIT extends PostgresIntegrationTest {

    private static final long R2_SA_ID = 999102L;

    // From r2-special-buyer-extra-seed.sql:
    //   999111 → R2T-SPEC-A-WH (Wholesale, special buyer A)
    //   999112 → R2T-SPEC-A-DW (Data_Wipe,  special buyer A)
    private static final long CODE_WH_SPECIAL = 999111L;
    private static final long CODE_DW_SPECIAL = 999112L;

    // r2-init-seed.sql AE rows:
    //   999101 ECO-X grade A — total_quantity=100, dw_total_quantity=50,
    //                          avg_target_price=100.0000, dw_avg_target_price=200.0000
    //   999102 ECO-Y grade A — total_quantity=100, dw_total_quantity=50,
    //                          avg_target_price=0,       dw_avg_target_price=0
    private static final long AE_ID_X = 999101L;
    private static final long AE_ID_Y = 999102L;

    // identity.users row seeded by V15 — admin@test.com
    private static final long ADMIN_USER_ID = 9001L;
    private static final long WEEK_ID = 999100L;

    @Autowired BidDataForAllAERepository repo;
    @Autowired JdbcTemplate jdbc;

    // ---- inserts one bid_data row per (special-QBC, non-deprecated AE) ----

    @Test
    @DisplayName("inserts one bid_data row per non-deprecated AE for the week (Wholesale branch)")
    void inserts_one_per_ae_wholesale() {
        long bidRoundId = ensureBidRound(R2_SA_ID, CODE_WH_SPECIAL);
        long bidDataDocId = ensureBidDataDoc(ADMIN_USER_ID, CODE_WH_SPECIAL, WEEK_ID);

        int rowsInserted = repo.insertForSpecialBuyer(
            R2_SA_ID, CODE_WH_SPECIAL, bidRoundId, bidDataDocId);

        // 2 AE rows (999101 + 999102), both have total_quantity=100 > 0 → both
        // qualify under the WH branch's WHERE total_quantity > 0 filter.
        assertThat(rowsInserted).isEqualTo(2);

        Integer rowCount = jdbc.queryForObject(
            "SELECT COUNT(*) FROM auctions.bid_data WHERE bid_round_id = ?",
            Integer.class, bidRoundId);
        assertThat(rowCount).isEqualTo(2);
    }

    // ---- DW vs Wholesale branch correct -----------------------------------

    @Test
    @DisplayName("Wholesale buyer code reads avg_target_price + total_quantity")
    void wholesale_branch_reads_wh_columns() {
        long bidRoundId = ensureBidRound(R2_SA_ID, CODE_WH_SPECIAL);
        long bidDataDocId = ensureBidDataDoc(ADMIN_USER_ID, CODE_WH_SPECIAL, WEEK_ID);

        repo.insertForSpecialBuyer(R2_SA_ID, CODE_WH_SPECIAL, bidRoundId, bidDataDocId);

        Map<String, Object> aeXRow = jdbc.queryForMap(
            "SELECT target_price, maximum_quantity, buyer_code_type, week_id, bid_round "
                + "FROM auctions.bid_data WHERE bid_round_id = ? AND aggregated_inventory_id = ?",
            bidRoundId, AE_ID_X);

        // ECO-X WH: avg_target_price=100.0000, total_quantity=100
        assertThat(((BigDecimal) aeXRow.get("target_price")))
            .isEqualByComparingTo("100.0000");
        assertThat(aeXRow.get("maximum_quantity")).isEqualTo(100);
        assertThat(aeXRow.get("buyer_code_type")).isEqualTo("Wholesale");
        // bid_round (round 2) and week_id (cast to int) come from params.
        assertThat(aeXRow.get("bid_round")).isEqualTo(2);
        assertThat(((Number) aeXRow.get("week_id")).intValue()).isEqualTo((int) WEEK_ID);
    }

    @Test
    @DisplayName("Data_Wipe buyer code reads dw_avg_target_price + dw_total_quantity")
    void dw_branch_reads_dw_columns() {
        long bidRoundId = ensureBidRound(R2_SA_ID, CODE_DW_SPECIAL);
        long bidDataDocId = ensureBidDataDoc(ADMIN_USER_ID, CODE_DW_SPECIAL, WEEK_ID);

        // ECO-Y has dw_total_quantity=50 but dw_avg_target_price=0; the
        // WHERE filter is on quantity > 0, so ECO-Y is INCLUDED here even
        // with a zero target price (target=0 is a legitimate DW band entry).
        repo.insertForSpecialBuyer(R2_SA_ID, CODE_DW_SPECIAL, bidRoundId, bidDataDocId);

        Map<String, Object> aeXRow = jdbc.queryForMap(
            "SELECT target_price, maximum_quantity, buyer_code_type "
                + "FROM auctions.bid_data WHERE bid_round_id = ? AND aggregated_inventory_id = ?",
            bidRoundId, AE_ID_X);

        // ECO-X DW: dw_avg_target_price=200.0000, dw_total_quantity=50
        assertThat(((BigDecimal) aeXRow.get("target_price")))
            .isEqualByComparingTo("200.0000");
        assertThat(aeXRow.get("maximum_quantity")).isEqualTo(50);
        assertThat(aeXRow.get("buyer_code_type")).isEqualTo("Data_Wipe");
    }

    // ---- deprecated AEs excluded -----------------------------------------

    @Test
    @DisplayName("deprecated aggregated_inventory rows are excluded from the bulk INSERT")
    void excludes_deprecated_aes() {
        // Mark ECO-X as deprecated; only ECO-Y should remain.
        jdbc.update(
            "UPDATE auctions.aggregated_inventory SET is_deprecated = TRUE WHERE id = ?",
            AE_ID_X);

        long bidRoundId = ensureBidRound(R2_SA_ID, CODE_WH_SPECIAL);
        long bidDataDocId = ensureBidDataDoc(ADMIN_USER_ID, CODE_WH_SPECIAL, WEEK_ID);

        int rowsInserted = repo.insertForSpecialBuyer(
            R2_SA_ID, CODE_WH_SPECIAL, bidRoundId, bidDataDocId);

        // Only ECO-Y remains — ECO-X deprecated.
        assertThat(rowsInserted).isEqualTo(1);

        List<Long> aeIds = jdbc.queryForList(
            "SELECT aggregated_inventory_id FROM auctions.bid_data WHERE bid_round_id = ?",
            Long.class, bidRoundId);
        assertThat(aeIds).containsExactly(AE_ID_Y);
    }

    // ---- zero-quantity AEs excluded --------------------------------------

    @Test
    @DisplayName("AEs with total_quantity=0 are excluded for a Wholesale buyer (WHERE total_quantity > 0)")
    void excludes_zero_quantity_wholesale() {
        // Zero out ECO-X total_quantity for the WH branch test.
        jdbc.update(
            "UPDATE auctions.aggregated_inventory SET total_quantity = 0 WHERE id = ?",
            AE_ID_X);

        long bidRoundId = ensureBidRound(R2_SA_ID, CODE_WH_SPECIAL);
        long bidDataDocId = ensureBidDataDoc(ADMIN_USER_ID, CODE_WH_SPECIAL, WEEK_ID);

        int rowsInserted = repo.insertForSpecialBuyer(
            R2_SA_ID, CODE_WH_SPECIAL, bidRoundId, bidDataDocId);

        // ECO-X excluded by total_quantity=0; ECO-Y still has total_quantity=100.
        assertThat(rowsInserted).isEqualTo(1);

        List<Long> aeIds = jdbc.queryForList(
            "SELECT aggregated_inventory_id FROM auctions.bid_data WHERE bid_round_id = ?",
            Long.class, bidRoundId);
        assertThat(aeIds).containsExactly(AE_ID_Y);
    }

    @Test
    @DisplayName("AEs with dw_total_quantity=0 are excluded for a Data_Wipe buyer")
    void excludes_zero_quantity_dw() {
        // Zero out ECO-X dw_total_quantity. ECO-Y has dw_total_quantity=50
        // and remains. The CASE on bc.buyer_code_type routes the WHERE
        // filter to dw_total_quantity for DW codes.
        jdbc.update(
            "UPDATE auctions.aggregated_inventory SET dw_total_quantity = 0 WHERE id = ?",
            AE_ID_X);

        long bidRoundId = ensureBidRound(R2_SA_ID, CODE_DW_SPECIAL);
        long bidDataDocId = ensureBidDataDoc(ADMIN_USER_ID, CODE_DW_SPECIAL, WEEK_ID);

        int rowsInserted = repo.insertForSpecialBuyer(
            R2_SA_ID, CODE_DW_SPECIAL, bidRoundId, bidDataDocId);

        assertThat(rowsInserted).isEqualTo(1);

        List<Long> aeIds = jdbc.queryForList(
            "SELECT aggregated_inventory_id FROM auctions.bid_data WHERE bid_round_id = ?",
            Long.class, bidRoundId);
        assertThat(aeIds).containsExactly(AE_ID_Y);
    }

    // ---- helpers ---------------------------------------------------------

    /**
     * Creates a {@code bid_rounds} row tied to the given SA + buyer code,
     * matching the FK contract on {@code bid_data.bid_round_id}.
     */
    private long ensureBidRound(long schedulingAuctionId, long buyerCodeId) {
        return jdbc.queryForObject("""
            INSERT INTO auctions.bid_rounds
              (scheduling_auction_id, buyer_code_id, week_id, submitted)
            VALUES (?, ?, ?, FALSE)
            RETURNING id
            """, Long.class, schedulingAuctionId, buyerCodeId, WEEK_ID);
    }

    /**
     * Creates a {@code bid_data_docs} row with the unique
     * {@code (user_id, buyer_code_id, week_id)} key. Uses the seeded admin
     * user (id=9001) as the owner.
     */
    private long ensureBidDataDoc(long userId, long buyerCodeId, long weekId) {
        return jdbc.queryForObject("""
            INSERT INTO auctions.bid_data_docs
              (user_id, buyer_code_id, week_id)
            VALUES (?, ?, ?)
            RETURNING id
            """, Long.class, userId, buyerCodeId, weekId);
    }
}
