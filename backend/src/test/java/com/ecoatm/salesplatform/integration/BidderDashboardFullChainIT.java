package com.ecoatm.salesplatform.integration;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.fixtures.BidDataScenario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end integration test for the bidder dashboard. Exercises the full
 * chain: seed one Wholesale Round-1 auction (via {@link BidDataScenario}) →
 * open dashboard (lazy bid_data materialization) → save an individual bid →
 * submit → assert the {@code bid_*} values slid to {@code submitted_*} with
 * {@code last_valid_*} still null → edit + resubmit → assert the resubmit
 * slide moved the prior {@code submitted_*} into {@code last_valid_*} → close
 * the round out-of-band → verify a third submit is blocked with
 * {@code 409 Conflict} + {@code code = "ROUND_CLOSED"}.
 *
 * <p>The class is intentionally <strong>not</strong> {@code @Transactional}:
 * {@code BidDataSubmissionService.save} and {@code submit} both open their
 * own {@code REQUIRES_NEW} transactions, so fixture writes must commit before
 * the HTTP call fires. Cleanup is explicit in {@link #cleanup()}.
 *
 * <p>Feature flags: lifecycle cron, R1 init listener, and Snowflake push are
 * all disabled so they can't mutate rows or log stacks during this test.
 */
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "auctions.lifecycle.enabled=false",
        "auctions.r1-init.enabled=false",
        "auctions.snowflake-push.enabled=false"
})
class BidderDashboardFullChainIT extends PostgresIntegrationTest {

    /** Dev bidder seeded by V15 + V26 — user_id 9005 is linked to buyer_id 73 and 91. */
    private static final long BIDDER_USER_ID = 9005L;
    private static final String BIDDER_USERNAME = "bidder@buyerco.com";
    /**
     * V26 seed links user 9005 to this buyer_id. The fixture resolves an
     * arbitrary Wholesale buyer_code via {@code LIMIT 1}, so we bridge the
     * user→buyer_code ownership chain by inserting a
     * {@code buyer_code_buyers} row linking the resolved buyer_code to this
     * buyer (the OWNERSHIP_SQL 2-hop JOIN then finds a shared buyer_id for
     * the bidder).
     */
    private static final long BIDDER_BUYER_ID = 73L;

    @Autowired private MockMvc mvc;
    @Autowired private JdbcTemplate jdbc;

    // Ids tracked for FK-safe cleanup. Ordered roughly parent → leaf so the
    // DELETE statements in @AfterEach can iterate in the reverse order.
    private final List<Long> createdBidRoundIds = new ArrayList<>();
    private final List<Long> createdBidDataDocIds = new ArrayList<>();
    private final List<Long> createdBuyerCodeBuyerLinks = new ArrayList<>(); // tracks buyer_code_id
    private Long createdAuctionId;
    private Long createdSchedulingAuctionId;

    /**
     * Fixture-seeded {@code aggregated_inventory} rows are keyed on
     * {@code (ecoid2, merged_grade, datawipe, week_id)} via
     * {@code uq_agi_ecoid_grade_dw_week}. Sweep them by the ECO-*
     * prefix so a crashed prior run (which would leave orphaned rows)
     * cannot poison the next attempt with a duplicate-key error.
     */
    private static final String FIXTURE_ECOID_PREFIX = "ECO-";
    /**
     * {@link com.ecoatm.salesplatform.fixtures.BidDataScenario} stamps every
     * auction it creates with a title starting with {@code "BidData scenario "}.
     * Sweeping by this prefix in cleanup lets a crashed prior run's auctions
     * (and their cascaded scheduling_auctions / bid_rounds / QBC rows) get
     * reaped regardless of the id we tracked this test — belt-and-suspenders.
     */
    private static final String FIXTURE_AUCTION_TITLE_PREFIX = "BidData scenario ";

    @AfterEach
    void cleanup() {
        // Child → parent FK delete order. Each statement is resilient to the
        // row already being gone (e.g. CASCADE from a previous delete).
        safe(() -> {
            if (!createdBidRoundIds.isEmpty()) {
                jdbc.update("DELETE FROM auctions.bid_data WHERE bid_round_id = ANY(?)",
                        createdBidRoundIds.stream().mapToLong(Long::longValue).toArray());
            }
        });
        // bid_submit_log has ON DELETE SET NULL, so no explicit delete needed.
        // qualified_buyer_codes FK-to-scheduling_auctions has ON DELETE CASCADE
        // (V72), so deleting the parent auction + scheduling_auction below
        // sweeps the QBC row we inserted via the fixture.
        safe(() -> {
            if (createdSchedulingAuctionId != null) {
                jdbc.update("DELETE FROM auctions.scheduling_auctions WHERE id = ?",
                        createdSchedulingAuctionId);
            }
        });
        safe(() -> {
            if (createdAuctionId != null) {
                jdbc.update("DELETE FROM auctions.auctions WHERE id = ?", createdAuctionId);
            }
        });
        // Belt-and-suspenders: sweep orphaned fixture auctions from any earlier
        // crashed run so a duplicate-key on aggregated_inventory doesn't keep
        // poisoning subsequent attempts.
        safe(() -> jdbc.update(
                "DELETE FROM auctions.auctions WHERE auction_title LIKE ?",
                FIXTURE_AUCTION_TITLE_PREFIX + "%"));
        // aggregated_inventory has no FK to the parent auction — sweep by the
        // fixture's ecoid prefix so a crashed prior run can't collide.
        safe(() -> jdbc.update(
                "DELETE FROM auctions.aggregated_inventory WHERE ecoid2 LIKE ?",
                FIXTURE_ECOID_PREFIX + "%"));
        safe(() -> {
            for (Long id : createdBidDataDocIds) {
                jdbc.update("DELETE FROM auctions.bid_data_docs WHERE id = ?", id);
            }
        });
        safe(() -> {
            for (Long bcId : createdBuyerCodeBuyerLinks) {
                jdbc.update(
                        "DELETE FROM buyer_mgmt.buyer_code_buyers "
                                + "WHERE buyer_code_id = ? AND buyer_id = ?",
                        bcId, BIDDER_BUYER_ID);
            }
        });

        createdBidRoundIds.clear();
        createdBidDataDocIds.clear();
        createdBuyerCodeBuyerLinks.clear();
        createdSchedulingAuctionId = null;
        createdAuctionId = null;
    }

    @Test
    @DisplayName("full chain: open → save → submit → edit → resubmit → close → submit blocked")
    void end_to_end_open_save_submit_resubmit_close_blocks() throws Exception {
        // ── Stage 1: seed ───────────────────────────────────────────────────
        BidDataScenario scenario = new BidDataScenario(jdbc)
                .round(1)
                .buyerCodeType("Wholesale")
                .inventory("ECO-A", "AB", 50, new BigDecimal("6.50"))
                .inventory("ECO-B", "DE", 10, new BigDecimal("1.00"))
                .qbcIncluded(true);
        long bidRoundId = scenario.commitAndReturnBidRoundId();
        long buyerCodeId = scenario.lastBuyerCodeId();

        createdBidRoundIds.add(bidRoundId);
        createdBidDataDocIds.add(scenario.lastBidDataDocId());
        // Record the auction + scheduling_auction so @AfterEach can sweep
        // them (and the QBC row via V72 cascade).
        Long schedulingAuctionId = jdbc.queryForObject(
                "SELECT scheduling_auction_id FROM auctions.bid_rounds WHERE id = ?",
                Long.class, bidRoundId);
        createdSchedulingAuctionId = schedulingAuctionId;
        createdAuctionId = jdbc.queryForObject(
                "SELECT auction_id FROM auctions.scheduling_auctions WHERE id = ?",
                Long.class, schedulingAuctionId);

        // Wire ownership: link the fixture-resolved buyer_code to the bidder's
        // buyer_id (V26 seed links user 9005 to buyer_id 73). ON CONFLICT
        // DO NOTHING is correct — if another test already linked them, reusing
        // the row is harmless and cleanup won't delete a row it didn't own
        // because the query tracks by (buyer_code_id, buyer_id) pair.
        int linked = jdbc.update("""
                INSERT INTO buyer_mgmt.buyer_code_buyers (buyer_code_id, buyer_id)
                VALUES (?, ?)
                ON CONFLICT DO NOTHING
                """, buyerCodeId, BIDDER_BUYER_ID);
        if (linked == 1) {
            // Only track for deletion if we actually created the link.
            createdBuyerCodeBuyerLinks.add(buyerCodeId);
        }

        // ── Stage 2: open dashboard (materializes bid_data rows) ────────────
        mvc.perform(get("/api/v1/bidder/dashboard")
                        .with(jwtBidder())
                        .param("buyerCodeId", String.valueOf(buyerCodeId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mode").value("GRID"))
                .andExpect(jsonPath("$.bidRound.id").value((int) bidRoundId))
                .andExpect(jsonPath("$.rows.length()").value(2));

        // Resolve the bid_data id for ECO-A/AB — the first row we'll edit + submit.
        Long bidDataId = jdbc.queryForObject("""
                SELECT id FROM auctions.bid_data
                WHERE bid_round_id = ? AND ecoid = ? AND merged_grade = ?
                """, Long.class, bidRoundId, "ECO-A", "AB");
        assertThat(bidDataId).isNotNull();

        // ── Stage 3: PUT save — first edit ──────────────────────────────────
        mvc.perform(put("/api/v1/bidder/bid-data/" + bidDataId)
                        .with(jwtBidder())
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"bidQuantity\":5,\"bidAmount\":10.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bidQuantity").value(5))
                .andExpect(jsonPath("$.bidAmount").value(10.00));

        // ── Stage 4: POST submit — first submit ─────────────────────────────
        mvc.perform(post("/api/v1/bidder/bid-rounds/" + bidRoundId + "/submit")
                        .with(jwtBidder())
                        .with(csrf())
                        .param("buyerCodeId", String.valueOf(buyerCodeId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rowCount").value(2))
                .andExpect(jsonPath("$.resubmit").value(false));

        // ── Stage 5: assert the slide — first submit ────────────────────────
        // After the first submit: bid_* → submitted_*, last_valid_* still null.
        assertSubmittedState(bidDataId, 5, new BigDecimal("10.00"), null, null);

        // ── Stage 6: PUT save — second edit ─────────────────────────────────
        mvc.perform(put("/api/v1/bidder/bid-data/" + bidDataId)
                        .with(jwtBidder())
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"bidQuantity\":7,\"bidAmount\":12.50}"))
                .andExpect(status().isOk());

        // ── Stage 7: POST submit — second submit (resubmit=true) ────────────
        mvc.perform(post("/api/v1/bidder/bid-rounds/" + bidRoundId + "/submit")
                        .with(jwtBidder())
                        .with(csrf())
                        .param("buyerCodeId", String.valueOf(buyerCodeId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resubmit").value(true));

        // ── Stage 8: assert the slide — second submit ───────────────────────
        // After the second submit: bid_* → submitted_*, prior submitted_* → last_valid_*.
        assertSubmittedState(bidDataId, 7, new BigDecimal("12.50"), 5, new BigDecimal("10.00"));

        // ── Stage 9: close the round out-of-band ────────────────────────────
        jdbc.update(
                "UPDATE auctions.scheduling_auctions SET round_status = 'Closed' WHERE id = ?",
                schedulingAuctionId);

        // ── Stage 10: third submit — blocked with 409 + ROUND_CLOSED ────────
        mvc.perform(post("/api/v1/bidder/bid-rounds/" + bidRoundId + "/submit")
                        .with(jwtBidder())
                        .with(csrf())
                        .param("buyerCodeId", String.valueOf(buyerCodeId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ROUND_CLOSED"));
    }

    private void assertSubmittedState(long bidDataId,
                                      Integer expectedSubmittedQty,
                                      BigDecimal expectedSubmittedAmount,
                                      Integer expectedLastValidQty,
                                      BigDecimal expectedLastValidAmount) {
        Integer actualSubQty = jdbc.queryForObject(
                "SELECT submitted_bid_quantity FROM auctions.bid_data WHERE id = ?",
                Integer.class, bidDataId);
        BigDecimal actualSubAmount = jdbc.queryForObject(
                "SELECT submitted_bid_amount FROM auctions.bid_data WHERE id = ?",
                BigDecimal.class, bidDataId);
        Integer actualLastQty = jdbc.queryForObject(
                "SELECT last_valid_bid_quantity FROM auctions.bid_data WHERE id = ?",
                Integer.class, bidDataId);
        BigDecimal actualLastAmount = jdbc.queryForObject(
                "SELECT last_valid_bid_amount FROM auctions.bid_data WHERE id = ?",
                BigDecimal.class, bidDataId);

        assertThat(actualSubQty).isEqualTo(expectedSubmittedQty);
        if (expectedSubmittedAmount == null) {
            assertThat(actualSubAmount).isNull();
        } else {
            assertThat(actualSubAmount).isEqualByComparingTo(expectedSubmittedAmount);
        }
        assertThat(actualLastQty).isEqualTo(expectedLastValidQty);
        if (expectedLastValidAmount == null) {
            assertThat(actualLastAmount).isNull();
        } else {
            assertThat(actualLastAmount).isEqualByComparingTo(expectedLastValidAmount);
        }
    }

    /** Auth shape that matches what {@code JwtAuthenticationFilter} installs. */
    private static RequestPostProcessor jwtBidder() {
        return authentication(new UsernamePasswordAuthenticationToken(
                BIDDER_USER_ID,
                BIDDER_USERNAME,
                List.of(new SimpleGrantedAuthority("ROLE_Bidder"))));
    }

    /**
     * Run a cleanup step and swallow any exception — a failure in one step
     * must not mask failures in subsequent steps or leave other tests with
     * stale state. Logs are intentionally suppressed because @AfterEach runs
     * after every test, including green ones.
     */
    private static void safe(Runnable r) {
        try {
            r.run();
        } catch (RuntimeException ignored) {
            // swallowed
        }
    }
}
