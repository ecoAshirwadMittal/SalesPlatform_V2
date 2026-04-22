package com.ecoatm.salesplatform.fixtures;

import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Fluent fixture builder that seeds the full parent chain required for
 * {@code BidDataCreationRepository} CTE integration tests.
 *
 * <p>Call {@link #commitAndReturnBidRoundId()} once all configuration
 * fluent-setter calls are complete. The method returns the newly inserted
 * {@code auctions.bid_rounds.id} so the consuming test can drive the CTE
 * under test against a known bid_round.
 *
 * <p>When {@code round > 1} the builder also inserts a sibling
 * {@code scheduling_auctions} row for {@code round - 1} (status {@code 'Closed'})
 * and a matching {@code bid_rounds} row so that prior-round bid data has a
 * valid FK target.
 *
 * <p>A {@code bid_round_selection_filters} row is inserted only when
 * {@link #qualificationFilter} has been called AND {@code round} is 2 or 3.
 * The table CHECK constraint rejects round=1, so filter inserts for round=1
 * are silently skipped.
 *
 * <p><strong>Buyer-code reuse:</strong> the buyer-code lookup in
 * {@link #commitAndReturnBidRoundId()} resolves
 * {@code buyer_mgmt.buyer_codes} by {@code buyer_code_type} with
 * {@code LIMIT 1}. Two scenarios in the same test class that pass the
 * same {@link #buyerCodeType(String)} (e.g. both {@code "Wholesale"})
 * therefore resolve the <em>same</em> {@code buyer_code_id}. This is
 * safe under {@code @DataJpaTest} because each test method runs in an
 * isolated rollback transaction — every fixture write (including the
 * {@code qualified_buyer_codes} row, scheduling auctions, bid rounds,
 * and bid_data) is rolled back at the end of the test. A future
 * maintainer who introduces a non-rollback integration test (e.g.
 * {@code @Commit}, {@code @Transactional(propagation = NOT_SUPPORTED)},
 * or a TestContainers IT outside {@code @DataJpaTest}) MUST be aware
 * that subsequent scenarios in the same JVM run can collide on the
 * shared buyer code — for example, the
 * {@code uq_qbc_sa_bc UNIQUE (scheduling_auction_id, buyer_code_id)}
 * constraint on {@code qualified_buyer_codes} (V72) will reject the
 * second QBC insert if both scenarios target the same scheduling
 * auction and buyer code.
 */
public final class BidDataScenario {

    private final JdbcTemplate jdbc;

    private int round = 1;
    private String buyerCodeType = "Wholesale"; // "Wholesale" or "DW"
    private boolean specialTreatment = false;
    private boolean included = true;
    private final Map<InventoryKey, InventorySpec> inventory = new HashMap<>();
    private final Map<InventoryKey, DualInventorySpec> dualInventory = new HashMap<>();
    private final Map<InventoryKey, BidSpec> priorRoundBids = new HashMap<>();
    private FilterSpec filter = null;

    private Long resolvedBuyerCodeId;
    private Long resolvedBidDataDocId;

    public BidDataScenario(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public BidDataScenario round(int r) {
        this.round = r;
        return this;
    }

    public BidDataScenario buyerCodeType(String t) {
        this.buyerCodeType = t;
        return this;
    }

    public BidDataScenario specialTreatment(boolean b) {
        this.specialTreatment = b;
        return this;
    }

    public BidDataScenario included(boolean b) {
        this.included = b;
        return this;
    }

    /**
     * Register an aggregated-inventory row keyed by {@code ecoid + "|" + grade}.
     * Column branches (non-DW vs DW) are resolved from {@link #buyerCodeType}
     * at commit time.
     */
    public BidDataScenario inventory(String ecoid, String grade, int qty, BigDecimal targetPrice) {
        inventory.put(new InventoryKey(ecoid, grade), new InventorySpec(qty, targetPrice));
        return this;
    }

    /**
     * Register an aggregated-inventory row that populates BOTH the wholesale and
     * DW column families on the same row. Used by DW-branch tests that need to
     * assert the CTE picks the DW columns even when wholesale columns are also
     * non-zero, and to verify the {@code dw_total_quantity > 0} filter gate
     * (e.g. wholesale-only rows must not surface for a DW buyer).
     *
     * <p>Inserts as {@code datawipe = true} when {@code dwQty > 0}, otherwise
     * {@code false}, matching how the legacy Snowflake sync stamps the column.
     */
    public BidDataScenario dwInventory(String ecoid, String grade,
                                       int totalQty, BigDecimal totalPrice,
                                       int dwQty,    BigDecimal dwPrice) {
        dualInventory.put(
                new InventoryKey(ecoid, grade),
                new DualInventorySpec(totalQty, totalPrice, dwQty, dwPrice));
        return this;
    }

    /**
     * Alias of {@link #included(boolean)} — sets the QBC {@code included} flag.
     * Provided for call-site clarity in tests that want to spell out the
     * QBC-vs-row distinction.
     */
    public BidDataScenario qbcIncluded(boolean b) {
        return included(b);
    }

    /**
     * Alias of {@link #specialTreatment(boolean)} — sets the QBC
     * {@code is_special_treatment} flag.
     */
    public BidDataScenario qbcSpecialTreatment(boolean b) {
        return specialTreatment(b);
    }

    /**
     * Register a prior-round bid row keyed by {@code ecoid + "|" + grade}.
     * Rows are only inserted when {@code round > 1} and this map is non-empty.
     * V73 made {@code bid_quantity} nullable — {@code submitted_bid_quantity}
     * is populated; {@code bid_data_doc_id} is left null.
     */
    public BidDataScenario priorRoundBid(String ecoid, String grade, int qty, BigDecimal amount) {
        priorRoundBids.put(new InventoryKey(ecoid, grade), new BidSpec(qty, amount));
        return this;
    }

    /**
     * Set the qualification-filter values inserted into
     * {@code auctions.bid_round_selection_filters}. Silently skipped for
     * round=1 because the table CHECK constraint rejects it.
     */
    public BidDataScenario qualificationFilter(
            BigDecimal targetPercent, BigDecimal targetValue, BigDecimal floor) {
        this.filter = new FilterSpec(targetPercent, targetValue, floor);
        return this;
    }

    /**
     * Writes all rows and returns the new {@code bid_round} id.
     *
     * <p>Insertion order:
     * <ol>
     *   <li>Resolve {@code mdm.week.id} and {@code buyer_mgmt.buyer_codes.id}.</li>
     *   <li>INSERT {@code auctions.auctions}.</li>
     *   <li>INSERT {@code auctions.scheduling_auctions} for current round (Started).
     *       If round &gt; 1, also insert a sibling for round-1 (Closed).</li>
     *   <li>INSERT {@code auctions.bid_rounds} for current round.
     *       If round &gt; 1, also insert a sibling for the prior round.</li>
     *   <li>INSERT {@code buyer_mgmt.qualified_buyer_codes} (flat post-V72).</li>
     *   <li>INSERT {@code auctions.aggregated_inventory} rows from inventory map.</li>
     *   <li>INSERT {@code auctions.bid_data} rows from priorRoundBids map (round &gt; 1 only).</li>
     *   <li>INSERT {@code auctions.bid_round_selection_filters} if filter is set
     *       and round is 2 or 3.</li>
     * </ol>
     */
    public long commitAndReturnBidRoundId() {

        // ── Step 1: resolve FK anchor rows ───────────────────────────────────────
        Long weekId = jdbc.queryForObject("SELECT id FROM mdm.week LIMIT 1", Long.class);
        if (weekId == null) {
            throw new IllegalStateException(
                    "BidDataScenario requires at least one mdm.week row — none found");
        }

        // Translate the fixture's collapsed "DW" alias into the actual schema
        // values the buyer_codes table stores (V8 enumerates the four legacy
        // Mendix values). The CTE later collapses Data_Wipe and
        // Purchasing_Order_Data_Wipe back into the 'DW' branch.
        String[] candidateTypes = "DW".equalsIgnoreCase(buyerCodeType)
                ? new String[] { "Data_Wipe", "Purchasing_Order_Data_Wipe" }
                : new String[] { buyerCodeType };

        Long buyerCodeId = null;
        for (String candidate : candidateTypes) {
            buyerCodeId = jdbc.query(
                    "SELECT id FROM buyer_mgmt.buyer_codes WHERE buyer_code_type = ? LIMIT 1",
                    rs -> rs.next() ? rs.getLong(1) : null,
                    candidate);
            if (buyerCodeId != null) {
                break;
            }
        }
        if (buyerCodeId == null) {
            throw new IllegalStateException(
                    "BidDataScenario requires at least one buyer_mgmt.buyer_codes row "
                            + "with buyer_code_type='" + buyerCodeType + "' — none found");
        }
        this.resolvedBuyerCodeId = buyerCodeId;

        // Resolve any user — bid_data_docs has a NOT NULL FK to identity.users.
        Long userId = jdbc.queryForObject(
                "SELECT id FROM identity.users LIMIT 1", Long.class);
        if (userId == null) {
            throw new IllegalStateException(
                    "BidDataScenario requires at least one identity.users row — none found");
        }

        // Insert the bid_data_docs row keyed by (user, buyer_code, week). The
        // unique constraint uq_bdd_user_buyer_week means we must reuse an
        // existing row when one is already present for this triple — multiple
        // BidDataScenario calls in the same test would otherwise collide.
        Long bidDataDocId = jdbc.queryForObject(
                "INSERT INTO auctions.bid_data_docs (user_id, buyer_code_id, week_id) "
                        + "VALUES (?, ?, ?) "
                        + "ON CONFLICT (user_id, buyer_code_id, week_id) "
                        + "DO UPDATE SET changed_date = NOW() "
                        + "RETURNING id",
                Long.class,
                userId,
                buyerCodeId,
                weekId);
        this.resolvedBidDataDocId = bidDataDocId;

        // ── Step 2: auction row ───────────────────────────────────────────────────
        Long auctionId = jdbc.queryForObject(
                "INSERT INTO auctions.auctions (auction_title, auction_status, week_id) "
                        + "VALUES (?, 'Scheduled', ?) RETURNING id",
                Long.class,
                "BidData scenario " + System.nanoTime(),
                weekId);

        // ── Step 3: scheduling_auctions ───────────────────────────────────────────
        Instant r1Start = Instant.parse("2026-04-21T16:00:00Z");
        Instant r1End   = Instant.parse("2026-04-25T07:00:00Z");
        Instant r2Start = Instant.parse("2026-04-25T13:00:00Z");
        Instant r2End   = Instant.parse("2026-04-26T08:00:00Z");
        Instant r3Start = Instant.parse("2026-04-26T11:00:00Z");
        Instant r3End   = Instant.parse("2026-04-27T12:00:00Z");

        // Prior-round sibling (round - 1, Closed) — only when round > 1
        Long priorSchedulingAuctionId = null;
        if (round > 1) {
            int priorRound = round - 1;
            Instant priorStart = (priorRound == 1) ? r1Start : r2Start;
            Instant priorEnd   = (priorRound == 1) ? r1End   : r2End;
            priorSchedulingAuctionId = jdbc.queryForObject(
                    "INSERT INTO auctions.scheduling_auctions "
                            + "(auction_id, name, round, start_datetime, end_datetime, round_status, has_round) "
                            + "VALUES (?, ?, ?, ?, ?, 'Closed', true) RETURNING id",
                    Long.class,
                    auctionId,
                    "Round " + priorRound,
                    priorRound,
                    Timestamp.from(priorStart),
                    Timestamp.from(priorEnd));
        }

        // Current round row (Started)
        String currentRoundName = (round == 3) ? "Upsell Round" : "Round " + round;
        Instant currentStart = switch (round) {
            case 1 -> r1Start;
            case 2 -> r2Start;
            default -> r3Start;
        };
        Instant currentEnd = switch (round) {
            case 1 -> r1End;
            case 2 -> r2End;
            default -> r3End;
        };
        Long schedulingAuctionId = jdbc.queryForObject(
                "INSERT INTO auctions.scheduling_auctions "
                        + "(auction_id, name, round, start_datetime, end_datetime, round_status, has_round) "
                        + "VALUES (?, ?, ?, ?, ?, 'Started', true) RETURNING id",
                Long.class,
                auctionId,
                currentRoundName,
                round,
                Timestamp.from(currentStart),
                Timestamp.from(currentEnd));

        // ── Step 4: bid_rounds ────────────────────────────────────────────────────
        Long priorBidRoundId = null;
        if (round > 1 && priorSchedulingAuctionId != null) {
            priorBidRoundId = jdbc.queryForObject(
                    "INSERT INTO auctions.bid_rounds "
                            + "(scheduling_auction_id, buyer_code_id, week_id, submitted) "
                            + "VALUES (?, ?, ?, false) RETURNING id",
                    Long.class,
                    priorSchedulingAuctionId,
                    buyerCodeId,
                    weekId);
        }

        Long bidRoundId = jdbc.queryForObject(
                "INSERT INTO auctions.bid_rounds "
                        + "(scheduling_auction_id, buyer_code_id, week_id, submitted) "
                        + "VALUES (?, ?, ?, false) RETURNING id",
                Long.class,
                schedulingAuctionId,
                buyerCodeId,
                weekId);

        // ── Step 5: qualified_buyer_codes (flat post-V72) ─────────────────────────
        // id defaults to nextval('buyer_mgmt.qualified_buyer_codes_id_seq') — omitted.
        jdbc.update(
                "INSERT INTO buyer_mgmt.qualified_buyer_codes "
                        + "(qualification_type, included, is_special_treatment, "
                        + " scheduling_auction_id, buyer_code_id) "
                        + "VALUES ('Qualified', ?, ?, ?, ?)",
                included,
                specialTreatment,
                schedulingAuctionId,
                buyerCodeId);

        // ── Step 6: aggregated_inventory ─────────────────────────────────────────
        boolean isDw = "DW".equalsIgnoreCase(buyerCodeType);
        for (Map.Entry<InventoryKey, InventorySpec> entry : inventory.entrySet()) {
            InventoryKey key  = entry.getKey();
            String ecoid      = key.ecoid();
            String grade      = key.grade();
            InventorySpec spec = entry.getValue();

            if (isDw) {
                jdbc.update(
                        "INSERT INTO auctions.aggregated_inventory "
                                + "(week_id, ecoid2, merged_grade, datawipe, "
                                + " dw_total_quantity, dw_avg_target_price) "
                                + "VALUES (?, ?, ?, true, ?, ?)",
                        weekId, ecoid, grade, spec.quantity(), spec.targetPrice());
            } else {
                jdbc.update(
                        "INSERT INTO auctions.aggregated_inventory "
                                + "(week_id, ecoid2, merged_grade, datawipe, "
                                + " total_quantity, avg_target_price) "
                                + "VALUES (?, ?, ?, false, ?, ?)",
                        weekId, ecoid, grade, spec.quantity(), spec.targetPrice());
            }
        }

        // Dual-column inventory rows — both wholesale and DW columns set on
        // the same row so the CTE's DW/Wholesale branching can be exercised
        // independently of which side has non-zero quantity.
        for (Map.Entry<InventoryKey, DualInventorySpec> entry : dualInventory.entrySet()) {
            InventoryKey key       = entry.getKey();
            String ecoid           = key.ecoid();
            String grade           = key.grade();
            DualInventorySpec spec = entry.getValue();

            jdbc.update(
                    "INSERT INTO auctions.aggregated_inventory "
                            + "(week_id, ecoid2, merged_grade, datawipe, "
                            + " total_quantity, avg_target_price, "
                            + " dw_total_quantity, dw_avg_target_price) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    weekId, ecoid, grade,
                    spec.dwQuantity() > 0,
                    spec.totalQuantity(), spec.totalPrice(),
                    spec.dwQuantity(),    spec.dwPrice());
        }

        // ── Step 7: prior-round bid_data ─────────────────────────────────────────
        // Only inserted when round > 1 and the map is non-empty.
        // bid_data_doc_id is left null per V73 (nullable column).
        if (round > 1 && priorBidRoundId != null && !priorRoundBids.isEmpty()) {
            for (Map.Entry<InventoryKey, BidSpec> entry : priorRoundBids.entrySet()) {
                InventoryKey key = entry.getKey();
                String ecoid     = key.ecoid();
                String grade     = key.grade();
                BidSpec spec     = entry.getValue();

                jdbc.update(
                        "INSERT INTO auctions.bid_data "
                                + "(bid_round_id, buyer_code_id, ecoid, merged_grade, "
                                + " submitted_bid_quantity, submitted_bid_amount) "
                                + "VALUES (?, ?, ?, ?, ?, ?)",
                        priorBidRoundId,
                        buyerCodeId,
                        ecoid,
                        grade,
                        spec.quantity(),
                        spec.amount());
            }
        }

        // ── Step 8: bid_round_selection_filters ───────────────────────────────────
        // The CHECK constraint on this table rejects round=1 — skip silently.
        if (filter != null && round >= 2) {
            jdbc.update(
                    "INSERT INTO auctions.bid_round_selection_filters "
                            + "(round, target_percent, target_value, total_value_floor) "
                            + "VALUES (?, ?, ?, ?)",
                    round,
                    filter.targetPercent(),
                    filter.targetValue(),
                    filter.floor());
        }

        return bidRoundId;
    }

    /**
     * Returns the {@code buyer_mgmt.buyer_codes.id} resolved during the most
     * recent {@link #commitAndReturnBidRoundId()} call. Throws
     * {@link IllegalStateException} when called before commit.
     */
    public long lastBuyerCodeId() {
        if (resolvedBuyerCodeId == null) {
            throw new IllegalStateException(
                    "commitAndReturnBidRoundId() must be called first");
        }
        return resolvedBuyerCodeId;
    }

    /**
     * Returns the {@code auctions.bid_data_docs.id} inserted (or upserted)
     * during the most recent {@link #commitAndReturnBidRoundId()} call. Throws
     * {@link IllegalStateException} when called before commit.
     */
    public long lastBidDataDocId() {
        if (resolvedBidDataDocId == null) {
            throw new IllegalStateException(
                    "commitAndReturnBidRoundId() must be called first");
        }
        return resolvedBidDataDocId;
    }

    // ── Value types ───────────────────────────────────────────────────────────────

    public record InventorySpec(int quantity, BigDecimal targetPrice) {}

    public record DualInventorySpec(
            int totalQuantity, BigDecimal totalPrice,
            int dwQuantity,    BigDecimal dwPrice) {}

    public record BidSpec(int quantity, BigDecimal amount) {}

    public record FilterSpec(BigDecimal targetPercent, BigDecimal targetValue, BigDecimal floor) {}

    private record InventoryKey(String ecoid, String grade) {}
}
