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
     *
     * <p>After sub-project 5b's SQL refactor the {@code prior_round_biddata} CTE
     * requires {@code bid_rounds.submitted = TRUE}; use {@link BidDataScenario#priorBid}
     * (which seeds a submitted round) instead of the legacy {@code priorRoundBid}
     * helper. A BRSF row with {@code All_Buyers} admits the row unconditionally so
     * the carryforward assertion stays valid.
     */
    @Test
    void generate_priorRoundCarryforward_populatesPreviousRoundColumns() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(2)
                .buyerCodeType("Wholesale")
                .inventory("CRY1", "A", 30, new BigDecimal("20.00"))
                .priorBid("CRY1", "A", new BigDecimal("18.50"), 7)
                .qbc(false, true, "Qualified")
                .brsfR2(new BigDecimal("5"), new BigDecimal("1.00"),
                        "All_Buyers", "ShowAllInventory");

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

    /**
     * R2 Only_Qualified buyer with NO R1 bid + inv_mode=InventoryRound1QualifiedBids:
     * the no-prior-bid branch in the R2 cascade returns FALSE (inv_mode requires
     * 'ShowAllInventory' to admit when prev_amount IS NULL), so the row is invisible
     * and zero {@code bid_data} rows are inserted.
     */
    @Test
    void generate_r2_onlyQualified_belowAllThresholds_insertsZero() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(2)
                .buyerCodeType("Wholesale")
                .inventory("AAA1", "A", 10, new BigDecimal("100.00"))
                // No .priorBid(...) — prev_amount IS NULL
                .qbc(false, true, "Qualified")
                .brsfR2(new BigDecimal("5"), new BigDecimal("1.00"),
                        "Only_Qualified", "InventoryRound1QualifiedBids");

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        int inserted = repo.generate(bidRoundId, buyerCodeId, bidDataDocId);

        assertThat(inserted).isZero();
    }

    /**
     * R2 Only_Qualified Wholesale buyer whose R1 bid clears the percent threshold
     * (bid >= target * (1 - target_pct/100)) → row visible.
     */
    @Test
    void generate_r2_onlyQualified_percentBranch_visible() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(2)
                .buyerCodeType("Wholesale")
                .inventory("PCT1", "A", 10, new BigDecimal("100.00"))
                .priorBid("PCT1", "A", new BigDecimal("96.00"), 1)  // 96 >= 100*(1-5/100)=95
                .qbc(false, true, "Qualified")
                .brsfR2(new BigDecimal("5"), new BigDecimal("0.50"),
                        "Only_Qualified", "InventoryRound1QualifiedBids");

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
    }

    /**
     * R2 Only_Qualified Wholesale buyer whose R1 bid clears the value-band threshold
     * (target - bid <= target_value) → row visible.
     */
    @Test
    void generate_r2_onlyQualified_valueBranch_visible() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(2)
                .buyerCodeType("Wholesale")
                .inventory("VAL1", "A", 10, new BigDecimal("100.00"))
                .priorBid("VAL1", "A", new BigDecimal("99.00"), 1)  // target-bid=1 <= target_val=1
                .qbc(false, true, "Qualified")
                .brsfR2(new BigDecimal("0.01"), new BigDecimal("1.00"),
                        "Only_Qualified", "InventoryRound1QualifiedBids");

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
    }

    /**
     * R2 Only_Qualified buyer with target=0 and nonzero R1 bid → row visible
     * (the "target=0 AND bid>0" sub-branch).
     */
    @Test
    void generate_r2_onlyQualified_targetZeroBidNonzero_visible() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(2)
                .buyerCodeType("Wholesale")
                .inventory("ZER1", "A", 10, new BigDecimal("0.00"))    // target=0
                .priorBid("ZER1", "A", new BigDecimal("5.00"), 1)
                .qbc(false, true, "Qualified")
                .brsfR2(new BigDecimal("5"), new BigDecimal("1.00"),
                        "Only_Qualified", "InventoryRound1QualifiedBids");

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
    }

    /**
     * R2 Only_Qualified buyer with R1 bid below all per-row thresholds AND
     * inv_mode=InventoryRound1QualifiedBids → fallback branch admits because bid > 0.
     */
    @Test
    void generate_r2_onlyQualified_inventoryRound1QualifiedBidsFallback_visible() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(2)
                .buyerCodeType("Wholesale")
                .inventory("FBK1", "A", 10, new BigDecimal("100.00"))
                .priorBid("FBK1", "A", new BigDecimal("10.00"), 1)  // far below all thresholds
                .qbc(false, true, "Qualified")
                .brsfR2(new BigDecimal("5"), new BigDecimal("1.00"),
                        "Only_Qualified", "InventoryRound1QualifiedBids");

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
    }

    /**
     * R2 Only_Qualified buyer with R1 bid below all per-row thresholds AND
     * inv_mode=ShowAllInventory → admits regardless of bid amount.
     */
    @Test
    void generate_r2_onlyQualified_showAllInventory_visible() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(2)
                .buyerCodeType("Wholesale")
                .inventory("SAI1", "A", 10, new BigDecimal("100.00"))
                .priorBid("SAI1", "A", new BigDecimal("1.00"), 1)
                .qbc(false, true, "Qualified")
                .brsfR2(new BigDecimal("5"), new BigDecimal("1.00"),
                        "Only_Qualified", "ShowAllInventory");

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
    }

    /**
     * R2 Only_Qualified DW buyer reads from {@code dw_avg_target_price}, not
     * {@code avg_target_price}. Wholesale price is set to a sentinel so a
     * mis-routed CASE branch surfaces.
     */
    @Test
    void generate_r2_onlyQualified_dwBranch_usesDwTarget() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(2)
                .buyerCodeType("DW")
                // Decoy: wholesale qty=50, target=99 (would FAIL pct/value if mis-routed)
                // Real: DW qty=25, target=20; bid 19 satisfies value branch (20-19=1 <= 1)
                .dwInventory("DWB1", "A",
                        50, new BigDecimal("99.00"),
                        25, new BigDecimal("20.00"))
                .priorBid("DWB1", "A", new BigDecimal("19.00"), 1)
                .qbc(false, true, "Qualified")
                .brsfR2(new BigDecimal("0.01"), new BigDecimal("1.00"),
                        "Only_Qualified", "InventoryRound1QualifiedBids");

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
    }

    /**
     * R2 All_Buyers admits all rows regardless of R1 bid history. No R1 bid is
     * needed for the row to be visible.
     */
    @Test
    void generate_r2_allBuyers_visibleRegardlessOfBid() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(2)
                .buyerCodeType("Wholesale")
                .inventory("ALB1", "A", 10, new BigDecimal("100.00"))
                // No prior bid seeded — All_Buyers admits anyway
                .qbc(false, true, "Qualified")
                .brsfR2(new BigDecimal("5"), new BigDecimal("1.00"),
                        "All_Buyers", "InventoryRound1QualifiedBids");

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
    }

    /**
     * R2 Only_Qualified buyer with NO R1 bid + inv_mode=ShowAllInventory →
     * row visible (the {@code prb.prev_amount IS NULL} sub-branch admits).
     */
    @Test
    void generate_r2_onlyQualified_noPriorBid_showAllInventory_visible() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(2)
                .buyerCodeType("Wholesale")
                .inventory("NPB1", "A", 10, new BigDecimal("100.00"))
                // No prior bid
                .qbc(false, true, "Qualified")
                .brsfR2(new BigDecimal("5"), new BigDecimal("1.00"),
                        "Only_Qualified", "ShowAllInventory");

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
    }

    /**
     * R2 Only_Qualified buyer with NO R1 bid + inv_mode=InventoryRound1QualifiedBids →
     * row INVISIBLE. The no-prior-bid branch in the R2 cascade returns FALSE because
     * inv_mode is not 'ShowAllInventory'. Mirrors Task 2's
     * {@code generate_r2_onlyQualified_belowAllThresholds_insertsZero} but with a
     * different ecoid for fixture isolation.
     *
     * Skipped if Task 2's test already covers this exact scenario; otherwise this
     * provides a second independent witness.
     */
    @Test
    void generate_r2_onlyQualified_noPriorBid_inventoryRound1Qualified_invisible() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(2)
                .buyerCodeType("Wholesale")
                .inventory("NPB2", "A", 10, new BigDecimal("100.00"))
                // No prior bid
                .qbc(false, true, "Qualified")
                .brsfR2(new BigDecimal("5"), new BigDecimal("1.00"),
                        "Only_Qualified", "InventoryRound1QualifiedBids");

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isZero();
    }

    // -------------------------------------------------------------------------
    // R3 cascade tests (Task 6)
    // -------------------------------------------------------------------------

    /**
     * R3 with all 3 filter knobs NULL → fallthrough TRUE for every row,
     * even AEs without prior bid.
     */
    @Test
    void generate_r3_allNullFilters_visibleEvenWithoutPriorBid() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(3)
                .buyerCodeType("Wholesale")
                .inventory("ALN1", "A", 10, new BigDecimal("100.00"))
                .r3TargetPrice("ALN1", "A", new BigDecimal("100.00"))
                // No prior bid
                .qbc(false, true, "Qualified")
                .brsfR3(null, null, null);

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
    }

    /**
     * R3 percent-variation branch: latest_bid >= round3_target_price - (round3_target_price * pct/100).
     */
    @Test
    void generate_r3_pctVarBranch_visible() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(3)
                .buyerCodeType("Wholesale")
                .inventory("R3P1", "A", 10, new BigDecimal("100.00"))
                .r3TargetPrice("R3P1", "A", new BigDecimal("100.00"))
                .priorBid("R3P1", "A", new BigDecimal("96.00"), 1)  // 96 >= 100 - (100*5/100)=95
                .qbc(false, true, "Qualified")
                .brsfR3(new BigDecimal("5"), null, null);

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
    }

    /**
     * R3 amount-variation branch: latest_bid >= round3_target_price - amt.
     */
    @Test
    void generate_r3_amtVarBranch_visible() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(3)
                .buyerCodeType("Wholesale")
                .inventory("R3A1", "A", 10, new BigDecimal("100.00"))
                .r3TargetPrice("R3A1", "A", new BigDecimal("100.00"))
                .priorBid("R3A1", "A", new BigDecimal("99.00"), 1)  // 99 >= 100-1=99
                .qbc(false, true, "Qualified")
                .brsfR3(null, new BigDecimal("1.00"), null);

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
    }

    /**
     * R3 rank-limit branch: round3_bid_rank <= rank_lim.
     */
    @Test
    void generate_r3_rankLimBranch_visible() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(3)
                .buyerCodeType("Wholesale")
                .inventory("R3R1", "A", 10, new BigDecimal("100.00"))
                .r3TargetPrice("R3R1", "A", new BigDecimal("100.00"))
                .priorBidWithRank("R3R1", "A", new BigDecimal("10.00"), 1, 2)  // rank=2 <= 3
                .qbc(false, true, "Qualified")
                .brsfR3(null, null, 3);

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
    }

    /**
     * R3 with all 3 filters set + buyer bid fails ALL branches → invisible.
     */
    @Test
    void generate_r3_allFiltersSet_failAll_invisible() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(3)
                .buyerCodeType("Wholesale")
                .inventory("R3F1", "A", 10, new BigDecimal("100.00"))
                .r3TargetPrice("R3F1", "A", new BigDecimal("100.00"))
                .priorBidWithRank("R3F1", "A", new BigDecimal("10.00"), 1, 10)  // 10 < 95, 10 < 99, rank=10 > 3
                .qbc(false, true, "Qualified")
                .brsfR3(new BigDecimal("5"), new BigDecimal("1.00"), 3);

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isZero();
    }

    /**
     * R3 DISTINCT ON picks R2 over R1 by submitted_datetime when both bid the same AE.
     * R1 bid 10 (would fail R3 pct branch); R2 bid 96 (passes R3 pct branch).
     * The CTE's {@code ORDER BY submitted_datetime DESC} picks R2 → row visible.
     *
     * <p>The {@code submitted_datetime} offset logic in
     * {@link BidDataScenario#priorBid(int, String, String, BigDecimal, int)}
     * seeds R1 with a larger {@code offsetHours} (offset = currentRound - priorRound = 3-1 = 2)
     * so R1's timestamp is earlier, and R2 with a smaller offset (3-2 = 1) so R2's timestamp
     * is later. {@code ORDER BY submitted_datetime DESC} therefore picks R2 first.
     */
    @Test
    void generate_r3_distinctOn_picksR2OverR1() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(3)
                .buyerCodeType("Wholesale")
                .inventory("DST1", "A", 10, new BigDecimal("100.00"))
                .r3TargetPrice("DST1", "A", new BigDecimal("100.00"))
                // R1 bid (low — would fail R3 pct branch on its own)
                .priorBid(1, "DST1", "A", new BigDecimal("10.00"), 1)
                // R2 bid (passes R3 pct branch); seeded after R1 so submitted_datetime is later
                .priorBid(2, "DST1", "A", new BigDecimal("96.00"), 1)
                .qbc(false, true, "Qualified")
                .brsfR3(new BigDecimal("5"), null, null);

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
    }

    /**
     * R3 with rank_lim set + prev_rank IS NULL → rank branch can't match.
     * Other branches NULL → buyer fails. Verifies the {@code IS NOT NULL} guard
     * on the rank branch.
     */
    @Test
    void generate_r3_prevRankNull_rankBranchOnly_invisible() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(3)
                .buyerCodeType("Wholesale")
                .inventory("RNK1", "A", 10, new BigDecimal("100.00"))
                .r3TargetPrice("RNK1", "A", new BigDecimal("100.00"))
                .priorBid("RNK1", "A", new BigDecimal("10.00"), 1)  // round3_bid_rank=NULL
                .qbc(false, true, "Qualified")
                .brsfR3(null, null, 3);  // rank_lim only

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isZero();
    }

    // -------------------------------------------------------------------------
    // STB shortcut tests (Task 7)
    // -------------------------------------------------------------------------

    /**
     * R2 STB buyer with R1 bid that would otherwise fail all threshold branches →
     * row visible because is_special_treatment=TRUE shortcuts the cascade.
     */
    @Test
    void generate_r2_stbShortcut_visibleDespiteFailedThresholds() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(2)
                .buyerCodeType("Wholesale")
                .inventory("STB1", "A", 10, new BigDecimal("100.00"))
                .priorBid("STB1", "A", new BigDecimal("1.00"), 1)  // would fail all branches
                .qbc(true, true, "Qualified")  // is_special_treatment=TRUE
                .brsfR2(new BigDecimal("5"), new BigDecimal("1.00"),
                        "Only_Qualified", "InventoryRound1QualifiedBids");

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
    }

    /**
     * R3 STB buyer with no prior bid AND filters configured (so the threshold
     * cascade would say "invisible") → row visible because is_special_treatment.
     */
    @Test
    void generate_r3_stbShortcut_visibleDespiteNoBid() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(3)
                .buyerCodeType("Wholesale")
                .inventory("STB2", "A", 10, new BigDecimal("100.00"))
                .r3TargetPrice("STB2", "A", new BigDecimal("100.00"))
                // No prior bid
                .qbc(true, true, "Qualified")  // is_special_treatment=TRUE
                .brsfR3(new BigDecimal("5"), new BigDecimal("1.00"), 3);  // all 3 set

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(1);
    }

    /**
     * R1 sanity: round=1 + no BRSF row + no prior bid → all rows visible
     * (R1 ELSE branch in the outer CASE returns TRUE; matches pre-5b behaviour).
     */
    @Test
    void generate_r1_sanity_allRowsVisible() {
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(1)
                .buyerCodeType("Wholesale")
                .inventory("R1S1", "A", 10, new BigDecimal("100.00"))
                .inventory("R1S2", "A", 5,  new BigDecimal("50.00"))
                .qbc(false, true, "Qualified");
                // No BRSF row for round=1 (V59 chk_brsf_round forbids it)
                // No prior bid (R1 has no prior round)

        long bidRoundId   = scenario.commitAndReturnBidRoundId();
        long buyerCodeId  = scenario.lastBuyerCodeId();
        long bidDataDocId = scenario.lastBidDataDocId();

        assertThat(repo.generate(bidRoundId, buyerCodeId, bidDataDocId)).isEqualTo(2);
    }
}
