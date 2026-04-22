package com.ecoatm.salesplatform.service.auctions.biddata;

import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.BidDataDoc;
import com.ecoatm.salesplatform.model.auctions.BidRound;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.BidDataCreationRepository;
import com.ecoatm.salesplatform.repository.auctions.BidDataRepository;
import com.ecoatm.salesplatform.repository.auctions.BidRoundRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BidDataCreationServiceTest {

    @Mock
    private BidDataCreationRepository creationRepo;

    @Mock
    private BidDataRepository bidDataRepo;

    @Mock
    private BidDataDocService docService;

    @Mock
    private BidRoundRepository bidRoundRepo;

    @Mock
    private SchedulingAuctionRepository saRepo;

    @Mock
    private AuctionRepository auctionRepo;

    @Mock
    private JdbcTemplate jdbc;

    private BidDataCreationService service;

    @BeforeEach
    void setUp() {
        service = new BidDataCreationService(
            creationRepo, bidDataRepo, docService,
            bidRoundRepo, saRepo, auctionRepo, jdbc);
    }

    @Test
    void ensureRowsExist_skipsWhenRowsAlreadyExist() {
        when(bidDataRepo.countByBidRoundId(100L)).thenReturn(42L);

        BidDataCreationResult result = service.ensureRowsExist(7L, 100L, 3L);

        assertThat(result.skipped()).isTrue();
        assertThat(result.rowsCreated()).isZero();
        assertThat(result.durationMs()).isGreaterThanOrEqualTo(0L);
        verify(creationRepo, never()).generate(anyLong(), anyLong(), anyLong());
        verify(jdbc, never()).queryForObject(
            eq("SELECT pg_advisory_xact_lock(hashtext('bid_data_gen'), ?)"),
            eq(Object.class), anyLong());
    }

    @Test
    void ensureRowsExist_acquiresAdvisoryLock_thenGenerates() {
        when(bidDataRepo.countByBidRoundId(100L)).thenReturn(0L);

        BidRound round = new BidRound();
        round.setSchedulingAuctionId(200L);
        when(bidRoundRepo.findById(100L)).thenReturn(Optional.of(round));

        SchedulingAuction sa = new SchedulingAuction();
        sa.setAuctionId(300L);
        when(saRepo.findById(200L)).thenReturn(Optional.of(sa));

        Auction auction = new Auction();
        auction.setWeekId(42L);
        when(auctionRepo.findById(300L)).thenReturn(Optional.of(auction));

        BidDataDoc doc = org.mockito.Mockito.mock(BidDataDoc.class);
        when(doc.getId()).thenReturn(55L);
        when(docService.getOrCreate(3L, 7L, 42L)).thenReturn(doc);

        when(creationRepo.generate(100L, 7L, 55L)).thenReturn(12);

        BidDataCreationResult result = service.ensureRowsExist(7L, 100L, 3L);

        assertThat(result.skipped()).isFalse();
        assertThat(result.rowsCreated()).isEqualTo(12);
        assertThat(result.durationMs()).isGreaterThanOrEqualTo(0L);
        verify(jdbc).queryForObject(
            eq("SELECT pg_advisory_xact_lock(hashtext('bid_data_gen'), ?)"),
            eq(Object.class), eq(100));
        verify(creationRepo).generate(100L, 7L, 55L);

        // Double-checked locking: pre-check → lock → re-check → generate.
        // An accidental reorder would silently break the safety guarantee.
        InOrder inOrder = inOrder(bidDataRepo, jdbc, creationRepo);
        inOrder.verify(bidDataRepo).countByBidRoundId(100L);
        inOrder.verify(jdbc).queryForObject(
            eq("SELECT pg_advisory_xact_lock(hashtext('bid_data_gen'), ?)"),
            eq(Object.class), eq(100));
        inOrder.verify(bidDataRepo).countByBidRoundId(100L);
        inOrder.verify(creationRepo).generate(100L, 7L, 55L);
    }

    @Test
    void ensureRowsExist_skipsAfterLockAcquisition_whenAnotherTxWon() {
        // First call (pre-lock) returns 0; second call (post-lock) returns 17 —
        // a concurrent transaction populated the rows while we were blocked
        // on pg_advisory_xact_lock.
        when(bidDataRepo.countByBidRoundId(100L)).thenReturn(0L, 17L);

        BidDataCreationResult result = service.ensureRowsExist(7L, 100L, 3L);

        assertThat(result.skipped()).isTrue();
        assertThat(result.rowsCreated()).isZero();
        verify(jdbc).queryForObject(
            eq("SELECT pg_advisory_xact_lock(hashtext('bid_data_gen'), ?)"),
            eq(Object.class), eq(100));
        verify(creationRepo, never()).generate(anyLong(), anyLong(), anyLong());
        verify(bidRoundRepo, never()).findById(anyLong());
    }
}
