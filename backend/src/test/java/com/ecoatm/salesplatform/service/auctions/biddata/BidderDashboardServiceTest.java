package com.ecoatm.salesplatform.service.auctions.biddata;

import com.ecoatm.salesplatform.dto.BidderDashboardResponse;
import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.AuctionStatus;
import com.ecoatm.salesplatform.model.auctions.BidData;
import com.ecoatm.salesplatform.model.auctions.BidRound;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCode;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeRepository;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.BidDataRepository;
import com.ecoatm.salesplatform.repository.auctions.BidRoundRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BidderDashboardServiceTest {

    private static final long USER_ID = 99L;
    private static final long BUYER_CODE_ID = 50L;
    private static final long AUCTION_ID = 100L;
    private static final long ACTIVE_SA_ID = 301L;
    private static final long R1_SA_ID = 301L;
    private static final long R2_SA_ID = 302L;
    private static final long R3_SA_ID = 303L;
    private static final long BID_ROUND_ID = 401L;
    private static final Instant FIXED_NOW = Instant.parse("2026-04-22T10:00:00Z");

    @Mock private SchedulingAuctionRepository saRepo;
    @Mock private AuctionRepository auctionRepo;
    @Mock private BidRoundRepository bidRoundRepo;
    @Mock private QualifiedBuyerCodeRepository qbcRepo;
    @Mock private BidDataRepository bidDataRepo;
    @Mock private JdbcTemplate jdbc;

    private BidderDashboardService service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(FIXED_NOW, ZoneOffset.UTC);
        service = new BidderDashboardService(saRepo, auctionRepo, bidRoundRepo, qbcRepo, bidDataRepo, jdbc, clock);
        // Default ownership pass for landingRoute() — individual tests can override
        // (loadGrid does not call assertOwnership, so lenient() avoids unused-stub errors).
        lenient().when(jdbc.queryForObject(anyString(), eq(Long.class), eq(USER_ID), eq(BUYER_CODE_ID)))
                .thenReturn(1L);
    }

    @Test
    void landingRoute_noActiveAuction_returnsError() {
        when(saRepo.findFirstByRoundStatusOrderByStartDatetimeDesc(SchedulingAuctionStatus.Started))
                .thenReturn(Optional.empty());

        BidderDashboardLandingResult result = service.landingRoute(USER_ID, BUYER_CODE_ID);

        assertThat(result).isInstanceOf(BidderDashboardLandingResult.Error.class);
        assertThat(((BidderDashboardLandingResult.Error) result).reason()).isEqualTo("AUCTION_NOT_FOUND");
    }

    @Test
    void landingRoute_throwsWhenBuyerCodeNotOwnedByUser() {
        // Override the @BeforeEach default — ownership query returns 0 → user does not own buyer code.
        when(jdbc.queryForObject(anyString(), eq(Long.class), eq(USER_ID), eq(BUYER_CODE_ID)))
                .thenReturn(0L);

        assertThatThrownBy(() -> service.landingRoute(USER_ID, BUYER_CODE_ID))
                .isInstanceOf(BidDataSubmissionException.class)
                .extracting("code").isEqualTo("NOT_YOUR_BID_DATA");
    }

    @Test
    void landingRoute_buyerNotIncluded_returnsDownload() {
        SchedulingAuction activeSa = newSa(ACTIVE_SA_ID, AUCTION_ID, 1, SchedulingAuctionStatus.Started);
        when(saRepo.findFirstByRoundStatusOrderByStartDatetimeDesc(SchedulingAuctionStatus.Started))
                .thenReturn(Optional.of(activeSa));
        QualifiedBuyerCode qbc = new QualifiedBuyerCode();
        qbc.setIncluded(false);
        when(qbcRepo.findBySchedulingAuctionIdAndBuyerCodeId(ACTIVE_SA_ID, BUYER_CODE_ID))
                .thenReturn(Optional.of(qbc));

        BidderDashboardLandingResult result = service.landingRoute(USER_ID, BUYER_CODE_ID);

        assertThat(result).isInstanceOf(BidderDashboardLandingResult.Download.class);
        assertThat(((BidderDashboardLandingResult.Download) result).reason()).isEqualTo("BUYER_NOT_INCLUDED");
    }

    @Test
    void landingRoute_r2DoneAndClosed_returnsDownload() {
        // Active SA is Round 1 (currentRound != 3 satisfied).
        SchedulingAuction r1 = newSa(R1_SA_ID, AUCTION_ID, 1, SchedulingAuctionStatus.Started);
        SchedulingAuction r2 = newSa(R2_SA_ID, AUCTION_ID, 2, SchedulingAuctionStatus.Closed);
        SchedulingAuction r3 = newSa(R3_SA_ID, AUCTION_ID, 3, SchedulingAuctionStatus.Scheduled);
        r2.setHasRound(true);

        when(saRepo.findFirstByRoundStatusOrderByStartDatetimeDesc(SchedulingAuctionStatus.Started))
                .thenReturn(Optional.of(r1));
        when(saRepo.findByAuctionIdOrderByRoundAsc(AUCTION_ID)).thenReturn(List.of(r1, r2, r3));

        QualifiedBuyerCode includedQbc = new QualifiedBuyerCode();
        includedQbc.setIncluded(true);
        includedQbc.setSubmitted(false);
        when(qbcRepo.findBySchedulingAuctionIdAndBuyerCodeId(R1_SA_ID, BUYER_CODE_ID))
                .thenReturn(Optional.of(includedQbc));

        // R2 not yet submitted by this buyer -> ROUND2_DOWNLOAD path.
        QualifiedBuyerCode r2Qbc = new QualifiedBuyerCode();
        r2Qbc.setSchedulingAuctionId(R2_SA_ID);
        r2Qbc.setIncluded(true);
        r2Qbc.setSubmitted(false);
        QualifiedBuyerCode r1Qbc = new QualifiedBuyerCode();
        r1Qbc.setSchedulingAuctionId(R1_SA_ID);
        r1Qbc.setSubmitted(true);
        when(qbcRepo.findBySchedulingAuctionIdInAndBuyerCodeId(anyCollection(), eq(BUYER_CODE_ID)))
                .thenReturn(List.of(r1Qbc, r2Qbc));

        BidderDashboardLandingResult result = service.landingRoute(USER_ID, BUYER_CODE_ID);

        assertThat(result).isInstanceOf(BidderDashboardLandingResult.Download.class);
        assertThat(((BidderDashboardLandingResult.Download) result).reason()).isEqualTo("ROUND2_DOWNLOAD");
    }

    @Test
    void landingRoute_allRoundsSubmitted_returnsAllDone() {
        SchedulingAuction r1 = newSa(R1_SA_ID, AUCTION_ID, 1, SchedulingAuctionStatus.Started);
        SchedulingAuction r2 = newSa(R2_SA_ID, AUCTION_ID, 2, SchedulingAuctionStatus.Closed);
        SchedulingAuction r3 = newSa(R3_SA_ID, AUCTION_ID, 3, SchedulingAuctionStatus.Closed);

        when(saRepo.findFirstByRoundStatusOrderByStartDatetimeDesc(SchedulingAuctionStatus.Started))
                .thenReturn(Optional.of(r1));
        when(saRepo.findByAuctionIdOrderByRoundAsc(AUCTION_ID)).thenReturn(List.of(r1, r2, r3));

        QualifiedBuyerCode includedQbc = new QualifiedBuyerCode();
        includedQbc.setIncluded(true);
        includedQbc.setSubmitted(true);
        when(qbcRepo.findBySchedulingAuctionIdAndBuyerCodeId(R1_SA_ID, BUYER_CODE_ID))
                .thenReturn(Optional.of(includedQbc));

        QualifiedBuyerCode q1 = submittedQbc(R1_SA_ID);
        QualifiedBuyerCode q2 = submittedQbc(R2_SA_ID);
        QualifiedBuyerCode q3 = submittedQbc(R3_SA_ID);
        when(qbcRepo.findBySchedulingAuctionIdInAndBuyerCodeId(anyCollection(), eq(BUYER_CODE_ID)))
                .thenReturn(List.of(q1, q2, q3));

        BidderDashboardLandingResult result = service.landingRoute(USER_ID, BUYER_CODE_ID);

        assertThat(result).isInstanceOf(BidderDashboardLandingResult.AllDone.class);
    }

    @Test
    void landingRoute_activeRound_returnsGrid() {
        SchedulingAuction r1 = newSa(R1_SA_ID, AUCTION_ID, 1, SchedulingAuctionStatus.Started);
        SchedulingAuction r2 = newSa(R2_SA_ID, AUCTION_ID, 2, SchedulingAuctionStatus.Scheduled);
        SchedulingAuction r3 = newSa(R3_SA_ID, AUCTION_ID, 3, SchedulingAuctionStatus.Scheduled);

        when(saRepo.findFirstByRoundStatusOrderByStartDatetimeDesc(SchedulingAuctionStatus.Started))
                .thenReturn(Optional.of(r1));
        when(saRepo.findByAuctionIdOrderByRoundAsc(AUCTION_ID)).thenReturn(List.of(r1, r2, r3));

        QualifiedBuyerCode includedQbc = new QualifiedBuyerCode();
        includedQbc.setIncluded(true);
        includedQbc.setSubmitted(false);
        when(qbcRepo.findBySchedulingAuctionIdAndBuyerCodeId(R1_SA_ID, BUYER_CODE_ID))
                .thenReturn(Optional.of(includedQbc));

        // No "all submitted" trigger — return only the R1 QBC, unsubmitted.
        QualifiedBuyerCode r1QbcOnly = new QualifiedBuyerCode();
        r1QbcOnly.setSchedulingAuctionId(R1_SA_ID);
        r1QbcOnly.setSubmitted(false);
        when(qbcRepo.findBySchedulingAuctionIdInAndBuyerCodeId(anyCollection(), eq(BUYER_CODE_ID)))
                .thenReturn(List.of(r1QbcOnly));

        BidRound bidRound = new BidRound();
        ReflectionTestUtils.setField(bidRound, "id", BID_ROUND_ID);
        bidRound.setSchedulingAuctionId(R1_SA_ID);
        when(bidRoundRepo.findBySchedulingAuctionId(R1_SA_ID)).thenReturn(Optional.of(bidRound));

        BidderDashboardLandingResult result = service.landingRoute(USER_ID, BUYER_CODE_ID);

        assertThat(result).isInstanceOf(BidderDashboardLandingResult.Grid.class);
        BidderDashboardLandingResult.Grid grid = (BidderDashboardLandingResult.Grid) result;
        assertThat(grid.bidRoundId()).isEqualTo(BID_ROUND_ID);
        assertThat(grid.schedulingAuctionId()).isEqualTo(R1_SA_ID);
        assertThat(grid.round()).isEqualTo(1);
    }

    @Test
    void loadGrid_projectsBidDataToRowsAndTotals() {
        SchedulingAuction r1 = newSa(R1_SA_ID, AUCTION_ID, 1, SchedulingAuctionStatus.Started);
        r1.setName("Round 1");
        r1.setStartDatetime(FIXED_NOW.minusSeconds(60));
        r1.setEndDatetime(FIXED_NOW.plusSeconds(3600));

        BidRound bidRound = new BidRound();
        ReflectionTestUtils.setField(bidRound, "id", BID_ROUND_ID);
        ReflectionTestUtils.setField(bidRound, "schedulingAuction", r1);
        bidRound.setSchedulingAuctionId(R1_SA_ID);
        bidRound.setSubmitted(false);

        Auction auction = new Auction();
        auction.setId(AUCTION_ID);
        auction.setAuctionTitle("Auction 2026 / Wk17");
        auction.setAuctionStatus(AuctionStatus.Started);

        when(bidRoundRepo.findById(BID_ROUND_ID)).thenReturn(Optional.of(bidRound));
        when(saRepo.findById(R1_SA_ID)).thenReturn(Optional.of(r1));
        when(auctionRepo.findById(AUCTION_ID)).thenReturn(Optional.of(auction));

        BidData row1 = new BidData();
        row1.setId(1L);
        row1.setBidRoundId(BID_ROUND_ID);
        row1.setBuyerCodeId(BUYER_CODE_ID);
        row1.setEcoid("ECO-1");
        row1.setMergedGrade("A");
        row1.setBidQuantity(5);
        row1.setBidAmount(new BigDecimal("100.00"));
        row1.setPayout(new BigDecimal("500.00"));
        BidData row2 = new BidData();
        row2.setId(2L);
        row2.setBidRoundId(BID_ROUND_ID);
        row2.setBuyerCodeId(BUYER_CODE_ID);
        row2.setEcoid("ECO-2");
        row2.setMergedGrade("B");
        row2.setBidQuantity(3);
        row2.setBidAmount(new BigDecimal("50.00"));
        row2.setPayout(new BigDecimal("150.00"));
        when(bidDataRepo.findByBidRoundIdAndBuyerCodeIdOrderByEcoidAscMergedGradeAsc(BID_ROUND_ID, BUYER_CODE_ID))
                .thenReturn(List.of(row1, row2));

        BidderDashboardResponse response = service.loadGrid(BID_ROUND_ID, BUYER_CODE_ID);

        assertThat(response.mode()).isEqualTo("GRID");
        assertThat(response.rows()).hasSize(2);
        assertThat(response.totals().rowCount()).isEqualTo(2);
        assertThat(response.totals().totalBidQuantity()).isEqualTo(8);
        assertThat(response.totals().totalBidAmount()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(response.totals().totalPayout()).isEqualByComparingTo(new BigDecimal("650.00"));
        assertThat(response.auction().auctionTitle()).isEqualTo("Auction 2026 / Wk17");
        assertThat(response.bidRound().id()).isEqualTo(BID_ROUND_ID);
        assertThat(response.timer().active()).isTrue();
    }

    private static SchedulingAuction newSa(long id, long auctionId, int round, SchedulingAuctionStatus status) {
        SchedulingAuction sa = new SchedulingAuction();
        sa.setId(id);
        sa.setAuctionId(auctionId);
        sa.setRound(round);
        sa.setRoundStatus(status);
        sa.setHasRound(true);
        return sa;
    }

    private static QualifiedBuyerCode submittedQbc(long saId) {
        QualifiedBuyerCode q = new QualifiedBuyerCode();
        q.setSchedulingAuctionId(saId);
        q.setSubmitted(true);
        return q;
    }
}
