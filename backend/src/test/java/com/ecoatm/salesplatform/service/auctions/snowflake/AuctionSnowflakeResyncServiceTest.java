package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.SyncLogWriter;
import com.ecoatm.salesplatform.service.auctions.snowflake.AuctionSnowflakeResyncService.AuctionSnowflakeResyncResult;
import com.ecoatm.salesplatform.service.auctions.snowflake.AuctionSnowflakeResyncService.RoundOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuctionSnowflakeResyncServiceTest {

    @Mock AuctionRepository auctionRepo;
    @Mock SchedulingAuctionRepository saRepo;
    @Mock BidRankingSnowflakeWriter bidRankingWriter;
    @Mock TargetPriceSnowflakeWriter targetPriceWriter;
    @Mock SyncLogWriter syncLogWriter;

    AuctionSnowflakeResyncService service;

    @BeforeEach
    void setUp() {
        service = new AuctionSnowflakeResyncService(
            auctionRepo, saRepo, bidRankingWriter, targetPriceWriter, syncLogWriter);
    }

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    private Auction auction(long id, Long weekId) {
        Auction a = new Auction();
        a.setId(id);
        a.setWeekId(weekId);
        return a;
    }

    private SchedulingAuction sa(int round, SchedulingAuctionStatus status) {
        SchedulingAuction s = new SchedulingAuction();
        s.setRound(round);
        s.setRoundStatus(status);
        return s;
    }

    // ---------------------------------------------------------------------------
    // Test cases
    // ---------------------------------------------------------------------------

    @Test
    @DisplayName("happy path: 2 closed rounds → both writers called for each round")
    void happyPath_twoClosedRounds_bothWritersCalled() {
        long auctionId = 101L;
        long weekId = 601L;

        when(auctionRepo.findById(auctionId)).thenReturn(Optional.of(auction(auctionId, weekId)));
        when(saRepo.findByAuctionIdOrderByRoundAsc(auctionId)).thenReturn(List.of(
            sa(1, SchedulingAuctionStatus.Closed),
            sa(2, SchedulingAuctionStatus.Closed),
            sa(3, SchedulingAuctionStatus.Scheduled)
        ));

        AuctionSnowflakeResyncResult result = service.resync(auctionId);

        // R1 closed → targetRound=2; R2 closed → targetRound=3
        verify(bidRankingWriter).pushBidRankings(weekId, 2);
        verify(targetPriceWriter).pushTargetPrices(weekId, 2);
        verify(bidRankingWriter).pushBidRankings(weekId, 3);
        verify(targetPriceWriter).pushTargetPrices(weekId, 3);

        assertThat(result.outcomes()).hasSize(2);
        assertThat(result.totalSuccesses()).isEqualTo(2);
        assertThat(result.totalFailures()).isEqualTo(0);
        assertThat(result.auctionId()).isEqualTo(auctionId);
        assertThat(result.weekId()).isEqualTo(weekId);

        RoundOutcome r1outcome = result.outcomes().get(0);
        assertThat(r1outcome.closedRound()).isEqualTo(1);
        assertThat(r1outcome.targetRound()).isEqualTo(2);
        assertThat(r1outcome.bidRankingPushed()).isTrue();
        assertThat(r1outcome.targetPricePushed()).isTrue();
    }

    @Test
    @DisplayName("R3 closed is skipped: targetRound=4 exceeds max guard")
    void r3ClosedIsSkipped() {
        long auctionId = 102L;
        long weekId = 602L;

        when(auctionRepo.findById(auctionId)).thenReturn(Optional.of(auction(auctionId, weekId)));
        when(saRepo.findByAuctionIdOrderByRoundAsc(auctionId)).thenReturn(List.of(
            sa(1, SchedulingAuctionStatus.Closed),
            sa(2, SchedulingAuctionStatus.Closed),
            sa(3, SchedulingAuctionStatus.Closed)
        ));

        AuctionSnowflakeResyncResult result = service.resync(auctionId);

        // R3 closed → targetRound=4 > 3, skipped; only R1+R2 closed processed
        verify(bidRankingWriter).pushBidRankings(weekId, 2);
        verify(targetPriceWriter).pushTargetPrices(weekId, 2);
        verify(bidRankingWriter).pushBidRankings(weekId, 3);
        verify(targetPriceWriter).pushTargetPrices(weekId, 3);

        // Outcomes only for R1 + R2 (R3 skipped by guard)
        assertThat(result.outcomes()).hasSize(2);
        assertThat(result.totalSuccesses()).isEqualTo(2);
        assertThat(result.totalFailures()).isEqualTo(0);
    }

    @Test
    @DisplayName("auction not found → EntityNotFoundException")
    void auctionNotFound_throws() {
        when(auctionRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.resync(999L))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("999");

        verify(saRepo, never()).findByAuctionIdOrderByRoundAsc(anyLong());
    }

    @Test
    @DisplayName("auction with null weekId → IllegalStateException")
    void nullWeekId_throws() {
        when(auctionRepo.findById(103L)).thenReturn(Optional.of(auction(103L, null)));

        assertThatThrownBy(() -> service.resync(103L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("no weekId");

        verify(saRepo, never()).findByAuctionIdOrderByRoundAsc(anyLong());
    }

    @Test
    @DisplayName("bid-ranking writer throws → syncLogWriter.writeFailed called; target-price writer still invoked")
    void bidRankingWriterThrows_failureIsolated_targetPriceStillCalled() {
        long auctionId = 104L;
        long weekId = 604L;

        when(auctionRepo.findById(auctionId)).thenReturn(Optional.of(auction(auctionId, weekId)));
        when(saRepo.findByAuctionIdOrderByRoundAsc(auctionId)).thenReturn(List.of(
            sa(1, SchedulingAuctionStatus.Closed)
        ));

        doThrow(new RuntimeException("snowflake unreachable"))
            .when(bidRankingWriter).pushBidRankings(weekId, 2);

        AuctionSnowflakeResyncResult result = service.resync(auctionId);

        // target-price writer is still called despite bid-ranking failure
        verify(targetPriceWriter).pushTargetPrices(weekId, 2);

        // failure is logged via SyncLogWriter
        verify(syncLogWriter).writeFailed(
            eq("BID_RANKING"),
            eq("auctionId=" + auctionId + ",weekId=" + weekId + ",targetRound=2"),
            contains("snowflake unreachable"));

        assertThat(result.totalSuccesses()).isEqualTo(0);
        assertThat(result.totalFailures()).isEqualTo(1);

        RoundOutcome outcome = result.outcomes().get(0);
        assertThat(outcome.bidRankingPushed()).isFalse();
        assertThat(outcome.targetPricePushed()).isTrue();
    }

    @Test
    @DisplayName("no closed rounds → empty outcomes, zero counts")
    void noClosedRounds_emptyResult() {
        long auctionId = 105L;
        long weekId = 605L;

        when(auctionRepo.findById(auctionId)).thenReturn(Optional.of(auction(auctionId, weekId)));
        when(saRepo.findByAuctionIdOrderByRoundAsc(auctionId)).thenReturn(List.of(
            sa(1, SchedulingAuctionStatus.Started),
            sa(2, SchedulingAuctionStatus.Scheduled),
            sa(3, SchedulingAuctionStatus.Unscheduled)
        ));

        AuctionSnowflakeResyncResult result = service.resync(auctionId);

        verify(bidRankingWriter, never()).pushBidRankings(anyLong(), anyInt());
        verify(targetPriceWriter, never()).pushTargetPrices(anyLong(), anyInt());
        verify(syncLogWriter, never()).writeFailed(anyString(), anyString(), anyString());

        assertThat(result.outcomes()).isEmpty();
        assertThat(result.totalSuccesses()).isEqualTo(0);
        assertThat(result.totalFailures()).isEqualTo(0);
    }
}
