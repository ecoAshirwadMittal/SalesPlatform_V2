package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.fixtures.BidDataScenario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

/**
 * Integration tests for {@link BidDataCreationRepository#generate}.
 *
 * <p>Runs against the real PostgreSQL test database (Flyway-built schema)
 * because the CTE relies on Postgres-only syntax (multi-CTE INSERT ... SELECT,
 * RETURNING, ::bigint casts). The H2-backed {@code test} profile cannot
 * support it.
 *
 * <p>{@code @Import(BidDataCreationRepository.class)} is required because
 * {@code @DataJpaTest} only auto-detects Spring Data interfaces — it does not
 * scan the custom {@code @Repository}-annotated POJO.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("pg-test")
@Import(BidDataCreationRepository.class)
class BidDataCreationRepositoryIT {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private BidDataCreationRepository repo;

    @Test
    void generate_r1_wholesale_buyer_producesOneRowPerInventoryLine() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(1)
                .buyerCodeType("Wholesale")
                .inventory("AAA1", "A", 10, new BigDecimal("25.00"))
                .inventory("AAA1", "B", 5,  new BigDecimal("15.00"));

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        int inserted = repo.generate(bidRoundId, buyerCodeId, bidDataDocId);

        assertThat(inserted).isEqualTo(2);

        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM auctions.bid_data WHERE bid_round_id = ?",
                Integer.class, bidRoundId);
        assertThat(count).isEqualTo(2);
    }

    @Test
    void generate_idempotent_skipsWhenRowsExist() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(1)
                .buyerCodeType("Wholesale")
                .inventory("AAA1", "A", 10, new BigDecimal("25.00"));

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        int first  = repo.generate(bidRoundId, buyerCodeId, bidDataDocId);
        int second = repo.generate(bidRoundId, buyerCodeId, bidDataDocId);

        assertThat(first).isEqualTo(1);
        assertThat(second).isEqualTo(0);
    }

    /**
     * DW buyer must read from the {@code dw_*} columns (not {@code total_*} /
     * {@code avg_target_price}) when a row carries both wholesale and DW
     * quantities. Locks the CTE branch in {@code params.buyer_code_type='DW'}
     * → CASE WHEN ... THEN qr.dw_avg_target_price ELSE qr.avg_target_price.
     */
    @Test
    void generate_dwBuyer_usesDwColumns() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(1)
                .buyerCodeType("DW")
                // wholesale columns set to a distinct sentinel value so a
                // mis-routed CASE branch surfaces as a wrong target_price /
                // maximum_quantity in the inserted row.
                .dwInventory("DW01", "A",
                        77,  new BigDecimal("99.99"),  // wholesale qty / price
                        25,  new BigDecimal("12.50")); // DW qty / price

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        int inserted = repo.generate(bidRoundId, buyerCodeId, bidDataDocId);

        assertThat(inserted).isEqualTo(1);

        Map<String, Object> row = jdbc.queryForMap(
                "SELECT target_price, maximum_quantity, buyer_code_type "
                        + "FROM auctions.bid_data WHERE bid_round_id = ?",
                bidRoundId);

        assertThat(((BigDecimal) row.get("target_price")))
                .isEqualByComparingTo(new BigDecimal("12.50"));
        assertThat(((Number) row.get("maximum_quantity")).intValue())
                .isEqualTo(25);
        assertThat(row.get("buyer_code_type")).isEqualTo("DW");
    }

    /**
     * DW buyer must skip rows whose {@code dw_total_quantity = 0} even when
     * the wholesale {@code total_quantity > 0}. Locks the inventory CTE
     * predicate {@code params.buyer_code_type='DW' AND ai.dw_total_quantity > 0}.
     */
    @Test
    void generate_dwBuyer_skipsRowsWithZeroDwQty() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(1)
                .buyerCodeType("DW")
                // Eligible DW row.
                .dwInventory("DW02", "A",
                        50,  new BigDecimal("80.00"),
                        10,  new BigDecimal("11.00"))
                // Wholesale-only row — DW buyer must not see it.
                .dwInventory("WH01", "A",
                        50,  new BigDecimal("80.00"),
                        0,   new BigDecimal("0.0000"));

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        int inserted = repo.generate(bidRoundId, buyerCodeId, bidDataDocId);

        assertThat(inserted).isEqualTo(1);

        String ecoid = jdbc.queryForObject(
                "SELECT ecoid FROM auctions.bid_data WHERE bid_round_id = ?",
                String.class, bidRoundId);
        assertThat(ecoid).isEqualTo("DW02");
    }

    /**
     * QBC row with {@code included = false} disqualifies the buyer entirely —
     * the {@code inventory_qualified} CTE filters {@code WHERE q.included = true}
     * so no rows reach the INSERT.
     */
    @Test
    void generate_unqualifiedBuyer_insertsZeroRows() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(1)
                .buyerCodeType("Wholesale")
                .qbcIncluded(false)
                .inventory("UNQ1", "A", 10, new BigDecimal("25.00"))
                .inventory("UNQ1", "B", 5,  new BigDecimal("15.00"));

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        int inserted = repo.generate(bidRoundId, buyerCodeId, bidDataDocId);

        assertThat(inserted).isZero();

        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM auctions.bid_data WHERE bid_round_id = ?",
                Integer.class, bidRoundId);
        assertThat(count).isZero();
    }

    /**
     * Round 2 carryforward — when a prior R1 {@code bid_data} row exists for
     * the same {@code (buyer_code_id, ecoid, merged_grade)} triple, the new R2
     * row's {@code previous_round_bid_quantity} / {@code previous_round_bid_amount}
     * must reflect the R1 {@code submitted_*} values. Locks the
     * {@code prior_round_biddata} CTE join.
     */
    @Test
    void generate_priorRoundCarryforward_populatesPreviousRoundColumns() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(2)
                .buyerCodeType("Wholesale")
                .inventory("CRY1", "A", 30, new BigDecimal("20.00"))
                .priorRoundBid("CRY1", "A", 7, new BigDecimal("18.50"));

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        int inserted = repo.generate(bidRoundId, buyerCodeId, bidDataDocId);

        assertThat(inserted).isEqualTo(1);

        Map<String, Object> row = jdbc.queryForMap(
                "SELECT previous_round_bid_quantity, previous_round_bid_amount "
                        + "FROM auctions.bid_data WHERE bid_round_id = ?",
                bidRoundId);

        assertThat(((Number) row.get("previous_round_bid_quantity")).intValue())
                .isEqualTo(7);
        assertThat(((BigDecimal) row.get("previous_round_bid_amount")))
                .isEqualByComparingTo(new BigDecimal("18.50"));
    }

    /**
     * No inventory rows for the week → CTE inserts zero rows. The parent
     * chain (auction, scheduling_auction, bid_round, qualified buyer code)
     * still exists, so this isolates the inventory CTE from the qualification
     * gate.
     */
    @Test
    void generate_emptyInventory_insertsZeroRows() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(1)
                .buyerCodeType("Wholesale");

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        // Precondition: the QBC chain must be in place so that a zero
        // result is unambiguously attributable to the inventory-side
        // filter, not to the silent-zero "missing QBC" mode (b) called
        // out in BidDataCreationRepository#generate's @implNote.
        Long schedulingAuctionId = jdbc.queryForObject(
                "SELECT scheduling_auction_id FROM auctions.bid_rounds WHERE id = ?",
                Long.class, bidRoundId);
        Integer qbcCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM buyer_mgmt.qualified_buyer_codes "
                        + "WHERE scheduling_auction_id = ? "
                        + "  AND buyer_code_id         = ? "
                        + "  AND included              = true",
                Integer.class, schedulingAuctionId, buyerCodeId);
        assertThat(qbcCount)
                .as("QBC chain must be set up before testing inventory filter")
                .isEqualTo(1);

        int inserted = repo.generate(bidRoundId, buyerCodeId, bidDataDocId);

        assertThat(inserted).isZero();

        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM auctions.bid_data WHERE bid_round_id = ?",
                Integer.class, bidRoundId);
        assertThat(count).isZero();
    }

    /**
     * Special-treatment buyer with {@code included = true} sees at least one
     * row. Today the CTE's {@code row_visible} stub is constant TRUE, so this
     * test passes against the current shape and locks the contract for
     * sub-project 4: when the real {@code row_visible} logic lands, special
     * treatment must continue to bypass per-row qualification gates.
     */
    @Test
    void generate_specialTreatment_bypassesRowVisibleFilter() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(1)
                .buyerCodeType("Wholesale")
                .qbcIncluded(true)
                .qbcSpecialTreatment(true)
                .inventory("SPC1", "A", 8, new BigDecimal("33.33"));

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        int inserted = repo.generate(bidRoundId, buyerCodeId, bidDataDocId);

        assertThat(inserted).isEqualTo(1);
    }
}
