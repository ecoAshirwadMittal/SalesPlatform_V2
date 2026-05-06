package com.ecoatm.salesplatform.service.auctions.recalc;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.event.BidRankingUpdatedEvent;
import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.event.TargetPriceRecalculatedEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end happy-path IT: publish a RoundClosedEvent, let the
 * synchronous AFTER_COMMIT listener chain run RANKING + TARGET_PRICE,
 * and verify both the DB state and the published events.
 *
 * <p>Not @Transactional — AFTER_COMMIT only fires after the publisher's
 * tx commits, and @Transactional rolls the publishing tx back. We use
 * TransactionTemplate to wrap the publishEvent call in a committed sub-tx.
 *
 * <p>{@link #cleanup} DELETEs fixture rows after each test so the next run
 * can re-apply @Sql cleanly. The recalc services use REQUIRES_NEW and commit
 * independently, so DB state survives without explicit teardown.
 */
@Sql(scripts = "/fixtures/auctions/recalc-seed.sql")
@Import(RecalcEndToEndIT.TestConfig.class)
class RecalcEndToEndIT extends PostgresIntegrationTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        EventCapture eventCapture() {
            return new EventCapture();
        }
    }

    @Autowired ApplicationEventPublisher publisher;
    @Autowired JdbcTemplate jdbc;
    @Autowired EventCapture capture;
    @Autowired TransactionTemplate txTemplate;

    @AfterEach
    void cleanup() {
        // Reverse-FK order. All fixture IDs are in the 999xxx range.
        // bid_data carries computed ranks; aggregated_inventory carries computed prices —
        // delete first since they reference scheduling_auctions / bid_rounds / weeks.
        jdbc.update("DELETE FROM auctions.bid_data WHERE week_id = 999001 OR id BETWEEN 1000001 AND 1099999");
        jdbc.update("DELETE FROM auctions.po_detail WHERE purchase_order_id IN (999001, 999002)");
        jdbc.update("DELETE FROM auctions.purchase_order WHERE id IN (999001, 999002)");
        jdbc.update("DELETE FROM auctions.target_price_factor_filters " +
            "WHERE bid_round_selection_filter_id IN (999002, 999003) " +
            "   OR target_price_factor_id IN (999001, 999002, 999003)");
        jdbc.update("DELETE FROM auctions.bid_round_selection_filters WHERE id IN (999002, 999003)");
        jdbc.update("DELETE FROM auctions.target_price_factors WHERE id IN (999001, 999002, 999003)");
        jdbc.update("DELETE FROM auctions.reserve_bid WHERE id IN (999001, 999002)");
        jdbc.update("DELETE FROM auctions.aggregated_inventory WHERE id BETWEEN 999001 AND 999099");
        jdbc.update("DELETE FROM auctions.bid_rounds WHERE id BETWEEN 999001 AND 999005");
        jdbc.update("DELETE FROM auctions.scheduling_auctions WHERE id IN (999001, 999002, 999003)");
        jdbc.update("DELETE FROM auctions.auctions WHERE id = 999001");
        jdbc.update("DELETE FROM mdm.week WHERE id IN (999001, 999002)");
        capture.clear();
    }

    @Test
    void round_close_runs_both_processes_and_publishes_both_events() {
        capture.clear();

        // Wrap publishEvent in a committed sub-tx so AFTER_COMMIT listeners fire.
        txTemplate.execute(status -> {
            publisher.publishEvent(new RoundClosedEvent(999001L, 1, 999001L, 999001L));
            return null;
        });

        // Status columns flipped to SUCCESS by the two recalc services.
        assertThat(jdbc.queryForObject(
            "SELECT ranking_status FROM auctions.scheduling_auctions WHERE id = 999001",
            String.class)).isEqualTo("SUCCESS");
        assertThat(jdbc.queryForObject(
            "SELECT target_price_status FROM auctions.scheduling_auctions WHERE id = 999001",
            String.class)).isEqualTo("SUCCESS");

        // Spot-check that ranks landed in round2_bid_rank.
        Integer rankRow = jdbc.queryForObject(
            "SELECT round2_bid_rank FROM auctions.bid_data WHERE id = 999001", Integer.class);
        assertThat(rankRow).isPositive();

        // Spot-check that target prices landed.
        java.math.BigDecimal target = jdbc.queryForObject(
            "SELECT round2_target_price FROM auctions.aggregated_inventory WHERE ecoid2='ECO-A' AND merged_grade='A'",
            java.math.BigDecimal.class);
        assertThat(target).isPositive();

        // Both events were published synchronously.
        assertThat(capture.bidRanking).hasSize(1);
        assertThat(capture.targetPrice).hasSize(1);
    }

    static class EventCapture {
        final List<BidRankingUpdatedEvent> bidRanking = new ArrayList<>();
        final List<TargetPriceRecalculatedEvent> targetPrice = new ArrayList<>();

        @EventListener
        void onBR(BidRankingUpdatedEvent e) { bidRanking.add(e); }

        @EventListener
        void onTP(TargetPriceRecalculatedEvent e) { targetPrice.add(e); }

        void clear() {
            bidRanking.clear();
            targetPrice.clear();
        }
    }
}
