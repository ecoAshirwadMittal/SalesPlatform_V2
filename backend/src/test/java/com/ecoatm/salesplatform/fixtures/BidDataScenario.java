package com.ecoatm.salesplatform.fixtures;

import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private String qualificationType = "Qualified";
    private final Map<InventoryKey, InventorySpec> inventory = new HashMap<>();
    private final Map<InventoryKey, DualInventorySpec> dualInventory = new HashMap<>();
    private final Map<InventoryKey, BidSpec> priorRoundBids = new HashMap<>();
    private FilterSpec filter = null;
    private FilterSpecR2 filterR2 = null;
    private FilterSpecR3 filterR3 = null;
    private final List<ExtendedBidSpec> extendedPriorBids = new ArrayList<>();
    private final List<R3TargetPriceSpec> r3TargetPrices = new ArrayList<>();

    private Long resolvedBuyerCodeId;
    private Long resolvedBidDataDocId;

    // Resolved during commitAndReturnBidRoundId() — used by priorBid helpers
    private Long resolvedAuctionId;
    private Long resolvedWeekId;
    private Long resolvedUserId;

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
     * Set all three QBC fields in one call: {@code is_special_treatment},
     * {@code included}, and {@code qualification_type}.
     *
     * <p>Unlike the individual {@link #included} / {@link #specialTreatment}
     * setters (which default to {@code 'Qualified'}), this overload also
     * controls {@code qualificationType} so R2/R3 tests can seed
     * {@code 'Not_Qualified'} rows for exclusion-branch coverage.
     */
    public BidDataScenario qbc(boolean isSpecialTreatment, boolean included, String qualificationType) {
        this.specialTreatment = isSpecialTreatment;
        this.included = included;
        this.qualificationType = qualificationType;
        return this;
    }

    /**
     * Register a {@code bid_round_selection_filters} row for round 2.
     *
     * <p>{@code qualMode} ∈ {@code {'All_Buyers','Only_Qualified'}}.
     * {@code invMode} ∈ {@code {'ShowAllInventory','InventoryRound1QualifiedBids'}}.
     * {@code stb_allow_all_buyers_override} is set to {@code false}.
     * Whole-percent convention: {@code targetPercent=5} means 5% (stored as-is
     * in the {@code NUMERIC(10,4)} column).
     *
     * <p>This method is an alternative to {@link #qualificationFilter} for
     * round-2 scenarios that need explicit {@code qualMode} and {@code invMode}
     * control. If both are called, {@code brsfR2} takes precedence.
     */
    public BidDataScenario brsfR2(BigDecimal targetPercent, BigDecimal targetValue,
                                  String qualMode, String invMode) {
        this.filterR2 = new FilterSpecR2(targetPercent, targetValue, qualMode, invMode);
        return this;
    }

    /**
     * Register a {@code bid_round_selection_filters} row for round 3.
     *
     * <p>Any of the three parameters may be {@code null} (→ SQL NULL). The
     * other columns default to {@code 'Only_Qualified'} /
     * {@code 'InventoryRound1QualifiedBids'} / {@code stb_allow_all_buyers_override=false}.
     */
    public BidDataScenario brsfR3(BigDecimal pctVar, BigDecimal amtVar, Integer rankLim) {
        this.filterR3 = new FilterSpecR3(pctVar, amtVar, rankLim);
        return this;
    }

    /**
     * Seed a prior-round {@code bid_data} row for the scenario's buyer code at
     * {@code priorRound = currentRound - 1}.
     *
     * <p>Delegates to {@link #priorBid(int, String, String, BigDecimal, int)}
     * with {@code round3BidRank = null}.
     */
    public BidDataScenario priorBid(String ecoid, String mergedGrade,
                                    BigDecimal amount, int quantity) {
        return priorBid(round - 1, ecoid, mergedGrade, amount, quantity);
    }

    /**
     * Seed a prior-round {@code bid_data} row for round {@code priorRound}
     * and set {@code round3_bid_rank} to the given value.
     *
     * <p>Delegates to {@link #priorBid(int, String, String, BigDecimal, int)}
     * with the supplied rank.
     */
    public BidDataScenario priorBidWithRank(String ecoid, String mergedGrade,
                                            BigDecimal amount, int quantity,
                                            Integer round3BidRank) {
        extendedPriorBids.add(
                new ExtendedBidSpec(round - 1, ecoid, mergedGrade, amount, quantity, round3BidRank));
        return this;
    }

    /**
     * Seed a prior-round {@code bid_data} row for an arbitrary {@code priorRound}.
     *
     * <p>At commit time the builder will look up or auto-create:
     * <ol>
     *   <li>A {@code scheduling_auctions} row for {@code (auctionId, priorRound)}</li>
     *   <li>A {@code bid_rounds} row for {@code (prior_sa, buyerCodeId)}</li>
     *   <li>A {@code bid_data_docs} row for the prior round</li>
     * </ol>
     * then INSERT one {@code bid_data} row with the supplied values.
     * The {@code submitted_datetime} offset is
     * {@code (this.round - priorRound) * 1 hour} before NOW so that older
     * rounds have earlier timestamps (R1 &lt; R2 when both priors are seeded).
     *
     * <p>This overload is intentionally {@code public} — Task 6 test 6 calls
     * {@code .priorBid(1, ...)} and {@code .priorBid(2, ...)} directly to seed
     * both R1 and R2 priors for an R3 scenario.
     */
    public BidDataScenario priorBid(int priorRound, String ecoid, String mergedGrade,
                                    BigDecimal amount, int quantity) {
        extendedPriorBids.add(
                new ExtendedBidSpec(priorRound, ecoid, mergedGrade, amount, quantity, null));
        return this;
    }

    /**
     * Update {@code auctions.aggregated_inventory.round3_target_price} for the
     * given {@code (ecoid, mergedGrade)} row (matched by {@code week_id},
     * {@code ecoid2}, and {@code merged_grade}).
     *
     * <p>The UPDATE runs during {@link #commitAndReturnBidRoundId()} after all
     * inventory INSERTs, so the inventory row must already be registered via
     * {@link #inventory} or {@link #dwInventory}.
     */
    public BidDataScenario r3TargetPrice(String ecoid, String mergedGrade,
                                         BigDecimal round3TargetPrice) {
        r3TargetPrices.add(new R3TargetPriceSpec(ecoid, mergedGrade, round3TargetPrice));
        return this;
    }

    /**
     * @deprecated Inserts a prior bid_data row against an unsubmitted bid_round.
     *             Sub-project 5b's {@code prior_round_biddata} CTE filters
     *             {@code br_prev.submitted = TRUE}, so rows seeded by this method
     *             are invisible to the CTE. Use {@link #priorBid(String, String, BigDecimal, int)}
     *             instead for any R2/R3 scenario that needs the prior bid to be
     *             visible to {@code BidDataCreationRepository.generate}.
     */
    @Deprecated
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
        this.resolvedWeekId = weekId;

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
        this.resolvedUserId = userId;

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
        this.resolvedAuctionId = auctionId;

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
        // qualificationType defaults to 'Qualified'; the .qbc(...) overload lets
        // R2/R3 tests seed 'Not_Qualified' rows for exclusion-branch coverage.
        jdbc.update(
                "INSERT INTO buyer_mgmt.qualified_buyer_codes "
                        + "(qualification_type, included, is_special_treatment, "
                        + " scheduling_auction_id, buyer_code_id) "
                        + "VALUES (?, ?, ?, ?, ?)",
                qualificationType,
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
        // filterR2 / filterR3 take precedence over the legacy qualificationFilter
        // when present; they allow explicit control of qualMode and invMode.
        if (filterR2 != null) {
            jdbc.update(
                    "INSERT INTO auctions.bid_round_selection_filters "
                            + "(round, target_percent, target_value, "
                            + " regular_buyer_qualification, regular_buyer_inventory_options, "
                            + " stb_allow_all_buyers_override) "
                            + "VALUES (2, ?, ?, ?, ?, false)",
                    filterR2.targetPercent(),
                    filterR2.targetValue(),
                    filterR2.qualMode(),
                    filterR2.invMode());
        } else if (filterR3 != null) {
            jdbc.update(
                    "INSERT INTO auctions.bid_round_selection_filters "
                            + "(round, bid_percentage_variation, bid_amount_variation, "
                            + " rank_qualification_limit, "
                            + " regular_buyer_qualification, regular_buyer_inventory_options, "
                            + " stb_allow_all_buyers_override) "
                            + "VALUES (3, ?, ?, ?, 'Only_Qualified', "
                            + "         'InventoryRound1QualifiedBids', false)",
                    filterR3.pctVar(),
                    filterR3.amtVar(),
                    filterR3.rankLim());
        } else if (filter != null && round >= 2) {
            jdbc.update(
                    "INSERT INTO auctions.bid_round_selection_filters "
                            + "(round, target_percent, target_value, total_value_floor) "
                            + "VALUES (?, ?, ?, ?)",
                    round,
                    filter.targetPercent(),
                    filter.targetValue(),
                    filter.floor());
        }

        // ── Step 9: r3_target_price updates ──────────────────────────────────────
        // Applied after inventory INSERTs so the rows to UPDATE exist.
        for (R3TargetPriceSpec spec : r3TargetPrices) {
            jdbc.update(
                    "UPDATE auctions.aggregated_inventory "
                            + "SET round3_target_price = ? "
                            + "WHERE week_id = ? AND ecoid2 = ? AND merged_grade = ?",
                    spec.round3TargetPrice(),
                    weekId,
                    spec.ecoid(),
                    spec.mergedGrade());
        }

        // ── Step 10: extended prior-round bid_data rows ───────────────────────────
        // .priorBid(int, ...) and .priorBidWithRank(...) accumulate specs here.
        // Each spec may reference a different priorRound, requiring a separate
        // scheduling_auction lookup-or-create within this auction.
        for (ExtendedBidSpec spec : extendedPriorBids) {
            insertExtendedPriorBid(spec, auctionId, buyerCodeId, weekId, userId);
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

    /**
     * Look up or create the prior-round scheduling_auction, bid_round, and
     * bid_data_docs for the given spec, then INSERT a bid_data row.
     *
     * <p>The {@code submitted_datetime} is offset from NOW by
     * {@code (this.round - spec.priorRound) * 1 hour} so that older rounds
     * sort earlier in time (R1 &lt; R2 when both priors are seeded for R3).
     */
    private void insertExtendedPriorBid(ExtendedBidSpec spec,
                                        Long auctionId,
                                        Long buyerCodeId,
                                        Long weekId,
                                        Long userId) {
        int priorRound = spec.priorRound();

        // Derive fixed timestamps for the prior SA so look-up-or-create is stable.
        Instant priorSaStart = switch (priorRound) {
            case 1 -> Instant.parse("2026-04-21T16:00:00Z");
            case 2 -> Instant.parse("2026-04-25T13:00:00Z");
            default -> Instant.parse("2026-04-26T11:00:00Z");
        };
        Instant priorSaEnd = switch (priorRound) {
            case 1 -> Instant.parse("2026-04-25T07:00:00Z");
            case 2 -> Instant.parse("2026-04-26T08:00:00Z");
            default -> Instant.parse("2026-04-27T12:00:00Z");
        };

        // Look up or create the prior scheduling_auction within this auction.
        Long priorSaId = jdbc.query(
                "SELECT id FROM auctions.scheduling_auctions "
                        + "WHERE auction_id = ? AND round = ? LIMIT 1",
                rs -> rs.next() ? rs.getLong(1) : null,
                auctionId, priorRound);
        if (priorSaId == null) {
            priorSaId = jdbc.queryForObject(
                    "INSERT INTO auctions.scheduling_auctions "
                            + "(auction_id, name, round, start_datetime, end_datetime, "
                            + " round_status, has_round) "
                            + "VALUES (?, ?, ?, ?, ?, 'Closed', true) RETURNING id",
                    Long.class,
                    auctionId,
                    "Round " + priorRound,
                    priorRound,
                    Timestamp.from(priorSaStart),
                    Timestamp.from(priorSaEnd));
        }

        // Offset so older rounds have earlier submitted_datetime.
        long offsetHours = (long) (round - priorRound);
        Instant submittedAt = Instant.now().minusSeconds(offsetHours * 3600L);

        // Look up or create the prior bid_round.
        // If a prior bid_round was already created (submitted=false by the main commit
        // sequence), mark it submitted=true and stamp submitted_datetime so the
        // prior_round_biddata CTE (which filters br_prev.submitted=TRUE) can see it.
        Long priorBidRoundId = jdbc.query(
                "SELECT id FROM auctions.bid_rounds "
                        + "WHERE scheduling_auction_id = ? AND buyer_code_id = ? LIMIT 1",
                rs -> rs.next() ? rs.getLong(1) : null,
                priorSaId, buyerCodeId);
        if (priorBidRoundId == null) {
            priorBidRoundId = jdbc.queryForObject(
                    "INSERT INTO auctions.bid_rounds "
                            + "(scheduling_auction_id, buyer_code_id, week_id, "
                            + " submitted, submitted_datetime) "
                            + "VALUES (?, ?, ?, true, ?) RETURNING id",
                    Long.class,
                    priorSaId,
                    buyerCodeId,
                    weekId,
                    Timestamp.from(submittedAt));
        } else {
            // Ensure the existing row is marked submitted=true so the new
            // prior_round_biddata CTE (br_prev.submitted = TRUE) picks it up.
            jdbc.update(
                    "UPDATE auctions.bid_rounds "
                            + "SET submitted = true, submitted_datetime = ? "
                            + "WHERE id = ?",
                    Timestamp.from(submittedAt),
                    priorBidRoundId);
        }

        // Look up or create the prior bid_data_docs.
        Long priorDocId = jdbc.queryForObject(
                "INSERT INTO auctions.bid_data_docs (user_id, buyer_code_id, week_id) "
                        + "VALUES (?, ?, ?) "
                        + "ON CONFLICT (user_id, buyer_code_id, week_id) "
                        + "DO UPDATE SET changed_date = NOW() "
                        + "RETURNING id",
                Long.class,
                userId,
                buyerCodeId,
                weekId);

        // Resolve aggregated_inventory_id by (week_id, ecoid2, merged_grade).
        Long aggInvId = jdbc.query(
                "SELECT id FROM auctions.aggregated_inventory "
                        + "WHERE week_id = ? AND ecoid2 = ? AND merged_grade = ? LIMIT 1",
                rs -> rs.next() ? rs.getLong(1) : null,
                weekId, spec.ecoid(), spec.mergedGrade());

        // Derive the buyer code text for the denormalized 'code' column.
        String codeText = jdbc.query(
                "SELECT code FROM buyer_mgmt.buyer_codes WHERE id = ? LIMIT 1",
                rs -> rs.next() ? rs.getString(1) : null,
                buyerCodeId);

        jdbc.update(
                "INSERT INTO auctions.bid_data "
                        + "(bid_round_id, buyer_code_id, aggregated_inventory_id, "
                        + " ecoid, merged_grade, code, "
                        + " bid_amount, submitted_bid_amount, submitted_bid_quantity, "
                        + " submitted_datetime, bid_quantity, target_price, "
                        + " buyer_code_type, bid_round, week_id, bid_data_doc_id, "
                        + " round3_bid_rank) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, ?, ?, ?, ?, ?)",
                priorBidRoundId,
                buyerCodeId,
                aggInvId,
                spec.ecoid(),
                spec.mergedGrade(),
                codeText,
                spec.amount(),
                spec.amount(),
                spec.quantity(),
                Timestamp.from(submittedAt),
                spec.quantity(),
                // buyer_code_type: collapse DW aliases to 'DW' for fixture clarity
                "DW".equalsIgnoreCase(buyerCodeType) ? "DW" : buyerCodeType,
                priorRound,
                // week_id on bid_data is an INT denormalized column (not FK)
                weekId.intValue(),
                priorDocId,
                spec.round3BidRank());
    }

    // ── Value types ───────────────────────────────────────────────────────────────

    public record InventorySpec(int quantity, BigDecimal targetPrice) {}

    public record DualInventorySpec(
            int totalQuantity, BigDecimal totalPrice,
            int dwQuantity,    BigDecimal dwPrice) {}

    public record BidSpec(int quantity, BigDecimal amount) {}

    public record FilterSpec(BigDecimal targetPercent, BigDecimal targetValue, BigDecimal floor) {}

    /** Round-2 BRSF with explicit qualification mode and inventory mode. */
    public record FilterSpecR2(BigDecimal targetPercent, BigDecimal targetValue,
                               String qualMode, String invMode) {}

    /**
     * Round-3 BRSF. Any field may be {@code null} (→ SQL NULL).
     * Maps to {@code bid_percentage_variation}, {@code bid_amount_variation},
     * and {@code rank_qualification_limit} (V84 columns).
     */
    public record FilterSpecR3(BigDecimal pctVar, BigDecimal amtVar, Integer rankLim) {}

    /** Extended bid spec for {@link #priorBid} / {@link #priorBidWithRank}. */
    public record ExtendedBidSpec(int priorRound, String ecoid, String mergedGrade,
                                  BigDecimal amount, int quantity, Integer round3BidRank) {}

    /** Spec for {@link #r3TargetPrice} — patches {@code aggregated_inventory} after INSERT. */
    public record R3TargetPriceSpec(String ecoid, String mergedGrade, BigDecimal round3TargetPrice) {}

    private record InventoryKey(String ecoid, String grade) {}
}
