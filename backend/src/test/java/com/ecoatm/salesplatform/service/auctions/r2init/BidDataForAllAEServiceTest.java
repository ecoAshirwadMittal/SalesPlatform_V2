package com.ecoatm.salesplatform.service.auctions.r2init;

import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.BidDataDoc;
import com.ecoatm.salesplatform.model.auctions.BidRound;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.BidDataForAllAERepository;
import com.ecoatm.salesplatform.repository.auctions.BidDataRepository;
import com.ecoatm.salesplatform.repository.auctions.BidRoundRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.biddata.BidDataDocService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BidDataForAllAEServiceTest {

    private static final long SA_ID = 502L;
    private static final long AUCTION_ID = 200L;
    private static final long WEEK_ID = 300L;
    private static final long SYSTEM_USER_ID = 9001L;
    private static final long CODE_1003 = 1003L;
    private static final long CODE_1004 = 1004L;

    @Mock BidDataForAllAERepository repo;
    @Mock BidRoundRepository bidRoundRepo;
    @Mock BidDataRepository bidDataRepo;
    @Mock BidDataDocService docService;
    @Mock SchedulingAuctionRepository saRepo;
    @Mock AuctionRepository auctionRepo;

    BidDataForAllAEService service;

    @BeforeEach
    void setUp() {
        service = new BidDataForAllAEService(
                repo, bidRoundRepo, bidDataRepo, docService, saRepo, auctionRepo);
    }

    @Test
    @DisplayName("for each special QBC, ensures bid_round + bid_data_doc and inserts bid_data rows")
    void per_qbc_invocation() {
        stubSaAndAuction();

        // Both buyer codes have no existing bid_round → service must create both.
        when(bidRoundRepo.findBySchedulingAuctionIdAndBuyerCodeId(SA_ID, CODE_1003))
                .thenReturn(Optional.empty());
        when(bidRoundRepo.findBySchedulingAuctionIdAndBuyerCodeId(SA_ID, CODE_1004))
                .thenReturn(Optional.empty());
        when(bidRoundRepo.save(any(BidRound.class))).thenAnswer(inv -> {
            BidRound br = inv.getArgument(0);
            // assign id based on which buyer code we're creating for
            long assignedId = br.getBuyerCodeId() == CODE_1003 ? 7001L : 7002L;
            return savedBidRound(br, assignedId);
        });

        when(bidDataRepo.countByBidRoundId(7001L)).thenReturn(0L);
        when(bidDataRepo.countByBidRoundId(7002L)).thenReturn(0L);

        when(docService.getOrCreate(SYSTEM_USER_ID, CODE_1003, WEEK_ID))
                .thenReturn(bidDataDoc(8001L));
        when(docService.getOrCreate(SYSTEM_USER_ID, CODE_1004, WEEK_ID))
                .thenReturn(bidDataDoc(8002L));

        when(repo.insertForSpecialBuyer(SA_ID, CODE_1003, 7001L, 8001L)).thenReturn(5);
        when(repo.insertForSpecialBuyer(SA_ID, CODE_1004, 7002L, 8002L)).thenReturn(5);

        int total = service.generateForSpecialBuyers(SA_ID, Set.of(CODE_1003, CODE_1004));

        assertThat(total).isEqualTo(10);

        // Two new bid_round rows created with the right field-set.
        ArgumentCaptor<BidRound> brCaptor = ArgumentCaptor.forClass(BidRound.class);
        verify(bidRoundRepo, times(2)).save(brCaptor.capture());
        List<BidRound> saved = brCaptor.getAllValues();
        assertThat(saved).allSatisfy(br -> {
            assertThat(br.getSchedulingAuctionId()).isEqualTo(SA_ID);
            assertThat(br.getWeekId()).isEqualTo(WEEK_ID);
            assertThat(br.getSubmitted()).isFalse();
        });
        assertThat(saved).extracting(BidRound::getBuyerCodeId)
                .containsExactlyInAnyOrder(CODE_1003, CODE_1004);

        // The inserts hit the repo with the resolved ids.
        verify(repo).insertForSpecialBuyer(SA_ID, CODE_1003, 7001L, 8001L);
        verify(repo).insertForSpecialBuyer(SA_ID, CODE_1004, 7002L, 8002L);
    }

    @Test
    @DisplayName("empty buyerCodeIds set → 0 total, no DB calls")
    void empty_set_short_circuits() {
        int total = service.generateForSpecialBuyers(SA_ID, Set.of());

        assertThat(total).isZero();
        verifyNoInteractions(saRepo, auctionRepo, bidRoundRepo, bidDataRepo, docService, repo);
    }

    @Test
    @DisplayName("idempotency: bid_round already has bid_data → skip insert, doc.getOrCreate not called")
    void existing_bid_data_skips_insert() {
        stubSaAndAuction();

        BidRound existing = newBidRound(7001L, CODE_1003);
        when(bidRoundRepo.findBySchedulingAuctionIdAndBuyerCodeId(SA_ID, CODE_1003))
                .thenReturn(Optional.of(existing));
        when(bidDataRepo.countByBidRoundId(7001L)).thenReturn(5L);

        int total = service.generateForSpecialBuyers(SA_ID, Set.of(CODE_1003));

        assertThat(total).isZero();
        verify(docService, never()).getOrCreate(any(Long.class), any(Long.class), any(Long.class));
        verify(repo, never()).insertForSpecialBuyer(
                any(Long.class), any(Long.class), any(Long.class), any(Long.class));
        // No fresh bid_round created either.
        verify(bidRoundRepo, never()).save(any(BidRound.class));
    }

    @Test
    @DisplayName("re-uses existing bid_round; only creates when none found")
    void existing_bid_round_reused() {
        stubSaAndAuction();

        BidRound existing1003 = newBidRound(7001L, CODE_1003);
        when(bidRoundRepo.findBySchedulingAuctionIdAndBuyerCodeId(SA_ID, CODE_1003))
                .thenReturn(Optional.of(existing1003));
        when(bidRoundRepo.findBySchedulingAuctionIdAndBuyerCodeId(SA_ID, CODE_1004))
                .thenReturn(Optional.empty());
        when(bidRoundRepo.save(any(BidRound.class))).thenAnswer(inv -> {
            BidRound br = inv.getArgument(0);
            return savedBidRound(br, 7002L);
        });

        when(bidDataRepo.countByBidRoundId(7001L)).thenReturn(0L);
        when(bidDataRepo.countByBidRoundId(7002L)).thenReturn(0L);

        when(docService.getOrCreate(SYSTEM_USER_ID, CODE_1003, WEEK_ID))
                .thenReturn(bidDataDoc(8001L));
        when(docService.getOrCreate(SYSTEM_USER_ID, CODE_1004, WEEK_ID))
                .thenReturn(bidDataDoc(8002L));

        when(repo.insertForSpecialBuyer(SA_ID, CODE_1003, 7001L, 8001L)).thenReturn(4);
        when(repo.insertForSpecialBuyer(SA_ID, CODE_1004, 7002L, 8002L)).thenReturn(6);

        int total = service.generateForSpecialBuyers(SA_ID, Set.of(CODE_1003, CODE_1004));

        assertThat(total).isEqualTo(10);
        // Only one save call — for the missing 1004 row.
        verify(bidRoundRepo, times(1)).save(any(BidRound.class));
    }

    private void stubSaAndAuction() {
        SchedulingAuction sa = new SchedulingAuction();
        sa.setId(SA_ID);
        sa.setAuctionId(AUCTION_ID);
        sa.setRound(2);
        when(saRepo.findById(SA_ID)).thenReturn(Optional.of(sa));

        Auction auction = new Auction();
        auction.setId(AUCTION_ID);
        auction.setWeekId(WEEK_ID);
        when(auctionRepo.findById(AUCTION_ID)).thenReturn(Optional.of(auction));
    }

    private static BidRound newBidRound(long id, long buyerCodeId) {
        BidRound br = new BidRound();
        setId(br, id);
        br.setSchedulingAuctionId(SA_ID);
        br.setBuyerCodeId(buyerCodeId);
        br.setWeekId(WEEK_ID);
        br.setSubmitted(false);
        return br;
    }

    /** Returns a fresh BidRound with the same fields as {@code src} plus the supplied id. */
    private static BidRound savedBidRound(BidRound src, long assignedId) {
        BidRound br = new BidRound();
        br.setSchedulingAuctionId(src.getSchedulingAuctionId());
        br.setBuyerCodeId(src.getBuyerCodeId());
        br.setWeekId(src.getWeekId());
        br.setSubmitted(src.getSubmitted());
        setId(br, assignedId);
        return br;
    }

    /** BidRound has no setId — JPA-managed; reflectively set for test stubs. */
    private static void setId(BidRound br, long id) {
        try {
            java.lang.reflect.Field idField = BidRound.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(br, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static BidDataDoc bidDataDoc(long id) {
        BidDataDoc doc = new BidDataDoc();
        // BidDataDoc has no setId; rely on JPA-managed id. The test only
        // asserts on the value the service threads through to the repo,
        // so we use reflection to set it directly.
        try {
            java.lang.reflect.Field idField = BidDataDoc.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(doc, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        return doc;
    }
}
