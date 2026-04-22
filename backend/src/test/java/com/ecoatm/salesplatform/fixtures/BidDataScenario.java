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
 */
public final class BidDataScenario {

    private final JdbcTemplate jdbc;

    private int round = 1;
    private String buyerCodeType = "Wholesale"; // "Wholesale" or "DW"
    private boolean specialTreatment = false;
    private boolean included = true;
    private final Map<String, InventorySpec> inventory = new HashMap<>();
    private final Map<String, BidSpec> priorRoundBids = new HashMap<>();
    private FilterSpec filter = null;

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
        inventory.put(ecoid + "|" + grade, new InventorySpec(qty, targetPrice));
        return this;
    }

    /**
     * Register a prior-round bid row keyed by {@code ecoid + "|" + grade}.
     * Rows are only inserted when {@code round > 1} and this map is non-empty.
     * V73 made {@code bid_quantity} nullable — {@code submitted_bid_quantity}
     * is populated; {@code bid_data_doc_id} is left null.
     */
    public BidDataScenario priorRoundBid(String ecoid, String grade, int qty, BigDecimal amount) {
        priorRoundBids.put(ecoid + "|" + grade, new BidSpec(qty, amount));
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
        Long buyerCodeId = jdbc.queryForObject(
                "SELECT id FROM buyer_mgmt.buyer_codes WHERE buyer_code_type = ? LIMIT 1",
                Long.class,
                buyerCodeType);

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
        for (Map.Entry<String, InventorySpec> entry : inventory.entrySet()) {
            String[] parts    = entry.getKey().split("\\|", 2);
            String ecoid      = parts[0];
            String grade      = parts[1];
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

        // ── Step 7: prior-round bid_data ─────────────────────────────────────────
        // Only inserted when round > 1 and the map is non-empty.
        // bid_data_doc_id is left null per V73 (nullable column).
        if (round > 1 && priorBidRoundId != null && !priorRoundBids.isEmpty()) {
            for (Map.Entry<String, BidSpec> entry : priorRoundBids.entrySet()) {
                String[] parts = entry.getKey().split("\\|", 2);
                String ecoid   = parts[0];
                String grade   = parts[1];
                BidSpec spec   = entry.getValue();

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

    // ── Value types ───────────────────────────────────────────────────────────────

    public record InventorySpec(int quantity, BigDecimal targetPrice) {}

    public record BidSpec(int quantity, BigDecimal amount) {}

    public record FilterSpec(BigDecimal targetPercent, BigDecimal targetValue, BigDecimal floor) {}
}
