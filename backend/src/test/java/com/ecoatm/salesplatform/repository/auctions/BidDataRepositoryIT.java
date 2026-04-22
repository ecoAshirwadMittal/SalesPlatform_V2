package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.BidData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

/**
 * Verifies {@link BidDataRepository} derived query methods against a real
 * PostgreSQL database populated with minimal fixture rows.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("pg-test")
class BidDataRepositoryIT {

    @Autowired
    private BidDataRepository repo;

    @Autowired
    private JdbcTemplate jdbc;

    // ---------------------------------------------------------------------------
    // Shared fixture helpers
    // ---------------------------------------------------------------------------

    /** Insert the minimal parent chain and return the bid_round id. */
    private long insertParentChain() {
        Long weekId = jdbc.queryForObject("SELECT id FROM mdm.week LIMIT 1", Long.class);

        Long auctionId = jdbc.queryForObject(
                "INSERT INTO auctions.auctions (auction_title, auction_status, week_id) "
                        + "VALUES (?, 'Scheduled', ?) RETURNING id",
                Long.class,
                "BidData IT auction " + System.nanoTime(),
                weekId);

        Instant start = Instant.parse("2026-04-21T16:00:00Z");
        Instant end   = Instant.parse("2026-04-25T07:00:00Z");
        Long schedulingAuctionId = jdbc.queryForObject(
                "INSERT INTO auctions.scheduling_auctions "
                        + "(auction_id, name, round, start_datetime, end_datetime, round_status, has_round) "
                        + "VALUES (?, 'Round 1', 1, ?, ?, 'Started', true) RETURNING id",
                Long.class,
                auctionId,
                Timestamp.from(start),
                Timestamp.from(end));

        Long buyerCodeId = jdbc.queryForObject(
                "SELECT id FROM buyer_mgmt.buyer_codes LIMIT 1", Long.class);

        return jdbc.queryForObject(
                "INSERT INTO auctions.bid_rounds "
                        + "(scheduling_auction_id, buyer_code_id, week_id, submitted) "
                        + "VALUES (?, ?, ?, false) RETURNING id",
                Long.class,
                schedulingAuctionId,
                buyerCodeId,
                weekId);
    }

    private long resolveBuyerCodeId() {
        return jdbc.queryForObject(
                "SELECT id FROM buyer_mgmt.buyer_codes LIMIT 1", Long.class);
    }

    /** Insert one bid_data row with the given ecoid and mergedGrade. */
    private void insertBidData(long bidRoundId, long buyerCodeId, String ecoid, String mergedGrade) {
        jdbc.update(
                "INSERT INTO auctions.bid_data (bid_round_id, buyer_code_id, ecoid, merged_grade) "
                        + "VALUES (?, ?, ?, ?)",
                bidRoundId, buyerCodeId, ecoid, mergedGrade);
    }

    // ---------------------------------------------------------------------------
    // Tests
    // ---------------------------------------------------------------------------

    @Test
    void countByBidRoundId_returnsCount() {
        long bidRoundId = insertParentChain();
        long buyerCodeId = resolveBuyerCodeId();

        // Insert a second, unrelated bid_round to prove filtering works.
        long otherBidRoundId = insertParentChain();

        insertBidData(bidRoundId, buyerCodeId, "ECO-A", "Good");
        insertBidData(bidRoundId, buyerCodeId, "ECO-B", "Fair");
        insertBidData(otherBidRoundId, buyerCodeId, "ECO-C", "Good");

        assertThat(repo.countByBidRoundId(bidRoundId)).isEqualTo(2L);
        assertThat(repo.countByBidRoundId(otherBidRoundId)).isEqualTo(1L);
    }

    @Test
    void findByBidRoundId_ordersByEcoidThenGrade() {
        long bidRoundId = insertParentChain();
        long buyerCodeId = resolveBuyerCodeId();

        // Insert in reverse expected order to prove sorting is server-side.
        insertBidData(bidRoundId, buyerCodeId, "ECO-Z", "Good");
        insertBidData(bidRoundId, buyerCodeId, "ECO-A", "Poor");
        insertBidData(bidRoundId, buyerCodeId, "ECO-A", "Good");

        List<BidData> results = repo.findByBidRoundIdOrderByEcoidAscMergedGradeAsc(bidRoundId);

        assertThat(results).hasSize(3);
        assertThat(results.get(0).getEcoid()).isEqualTo("ECO-A");
        assertThat(results.get(0).getMergedGrade()).isEqualTo("Good");
        assertThat(results.get(1).getEcoid()).isEqualTo("ECO-A");
        assertThat(results.get(1).getMergedGrade()).isEqualTo("Poor");
        assertThat(results.get(2).getEcoid()).isEqualTo("ECO-Z");
    }
}
