package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.BidRound;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

/**
 * Verifies the {@link BidRound} entity correctly maps the direct submit-state
 * columns ({@code submitted}, {@code submitted_datetime},
 * {@code submitted_by_user_id}) and delegates round timing/status to the
 * parent {@code scheduling_auctions} row through a lazy {@link
 * com.ecoatm.salesplatform.model.auctions.SchedulingAuction} association.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("pg-test")
class BidRoundRepositoryIT {

    @Autowired
    private BidRoundRepository bidRoundRepository;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void bidRound_mapsSubmitStateAndDelegatesTimingFromSchedulingAuction() {
        // Arrange — pick a real seeded week + buyer code; pick a seeded
        // ecoatm_direct_users.user_id to satisfy the submitted_by_user_id FK.
        Long weekId = jdbc.queryForObject("SELECT id FROM mdm.week LIMIT 1", Long.class);
        Long buyerCodeId = jdbc.queryForObject(
                "SELECT id FROM buyer_mgmt.buyer_codes LIMIT 1", Long.class);
        List<Long> userIds = jdbc.queryForList(
                "SELECT user_id FROM user_mgmt.ecoatm_direct_users LIMIT 1", Long.class);
        Long submittedByUserId = userIds.isEmpty() ? null : userIds.get(0);

        // Insert minimal auction + scheduling_auction (Started, with timing).
        Long auctionId = jdbc.queryForObject(
                "INSERT INTO auctions.auctions "
                        + "(auction_title, auction_status, week_id) "
                        + "VALUES (?, 'Scheduled', ?) RETURNING id",
                Long.class,
                "BidRound IT auction " + System.nanoTime(),
                weekId);

        Instant start = Instant.parse("2026-04-21T16:00:00Z");
        Instant end = Instant.parse("2026-04-25T07:00:00Z");
        Long schedulingAuctionId = jdbc.queryForObject(
                "INSERT INTO auctions.scheduling_auctions "
                        + "(auction_id, name, round, start_datetime, end_datetime, "
                        + " round_status, has_round) "
                        + "VALUES (?, 'Round 1', 1, ?, ?, 'Started', true) "
                        + "RETURNING id",
                Long.class,
                auctionId,
                Timestamp.from(start),
                Timestamp.from(end));

        // Insert the bid_round under test.
        Long bidRoundId = jdbc.queryForObject(
                "INSERT INTO auctions.bid_rounds "
                        + "(scheduling_auction_id, buyer_code_id, week_id, "
                        + " submitted, submitted_datetime, submitted_by_user_id) "
                        + "VALUES (?, ?, ?, true, ?, ?) RETURNING id",
                Long.class,
                schedulingAuctionId,
                buyerCodeId,
                weekId,
                Timestamp.from(Instant.parse("2026-04-22T10:00:00Z")),
                submittedByUserId);

        // Act
        Optional<BidRound> found = bidRoundRepository.findById(bidRoundId);

        // Assert
        assertThat(found).isPresent();
        BidRound round = found.get();

        assertThat(round.getSubmitted()).isTrue();
        assertThat(round.getSubmittedDatetime()).isNotNull();
        if (submittedByUserId == null) {
            assertThat(round.getSubmittedByUserId()).isNull();
        } else {
            assertThat(round.getSubmittedByUserId()).isEqualTo(submittedByUserId);
        }

        // Delegated getters resolve through the lazy SchedulingAuction association.
        assertThat(round.getRoundStatus()).isEqualTo("Started");
        assertThat(round.getStartDatetime()).isNotNull();
        assertThat(round.getEndDatetime()).isNotNull();
    }
}
