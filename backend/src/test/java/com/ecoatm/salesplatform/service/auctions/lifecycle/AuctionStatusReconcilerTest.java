package com.ecoatm.salesplatform.service.auctions.lifecycle;

import com.ecoatm.salesplatform.model.auctions.Auction;
import com.ecoatm.salesplatform.model.auctions.AuctionStatus;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.repository.auctions.AuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuctionStatusReconcilerTest {

    @Mock private SchedulingAuctionRepository schedulingRepo;
    @Mock private AuctionRepository auctionRepo;

    private AuctionStatusReconciler reconciler;
    private final Instant now = Instant.parse("2026-04-20T12:00:00Z");

    @BeforeEach
    void setUp() {
        reconciler = new AuctionStatusReconciler(schedulingRepo, auctionRepo,
                Clock.fixed(now, ZoneOffset.UTC));
    }

    @Test
    void reconcile_allRoundsClosed_setsAuctionClosed() {
        Auction parent = auctionFixture(100L, AuctionStatus.Started);
        when(auctionRepo.findByIdForUpdate(100L)).thenReturn(Optional.of(parent));
        when(schedulingRepo.findByAuctionIdOrderByRoundAsc(100L)).thenReturn(List.of(
                roundWith(SchedulingAuctionStatus.Closed),
                roundWith(SchedulingAuctionStatus.Closed),
                roundWith(SchedulingAuctionStatus.Closed)
        ));

        reconciler.reconcile(100L);

        assertThat(parent.getAuctionStatus()).isEqualTo(AuctionStatus.Closed);
        assertThat(parent.getChangedDate()).isEqualTo(now);
        assertThat(parent.getUpdatedBy()).isEqualTo("system:lifecycle-cron");
        verify(auctionRepo).save(parent);
    }

    @Test
    void reconcile_anyRoundStarted_setsAuctionStarted() {
        Auction parent = auctionFixture(100L, AuctionStatus.Scheduled);
        when(auctionRepo.findByIdForUpdate(100L)).thenReturn(Optional.of(parent));
        when(schedulingRepo.findByAuctionIdOrderByRoundAsc(100L)).thenReturn(List.of(
                roundWith(SchedulingAuctionStatus.Closed),
                roundWith(SchedulingAuctionStatus.Started),
                roundWith(SchedulingAuctionStatus.Scheduled)
        ));

        reconciler.reconcile(100L);

        assertThat(parent.getAuctionStatus()).isEqualTo(AuctionStatus.Started);
        verify(auctionRepo).save(parent);
    }

    @Test
    void reconcile_allScheduled_isNoOp() {
        Auction parent = auctionFixture(100L, AuctionStatus.Scheduled);
        when(auctionRepo.findByIdForUpdate(100L)).thenReturn(Optional.of(parent));
        when(schedulingRepo.findByAuctionIdOrderByRoundAsc(100L)).thenReturn(List.of(
                roundWith(SchedulingAuctionStatus.Scheduled),
                roundWith(SchedulingAuctionStatus.Scheduled),
                roundWith(SchedulingAuctionStatus.Scheduled)
        ));

        reconciler.reconcile(100L);

        assertThat(parent.getAuctionStatus()).isEqualTo(AuctionStatus.Scheduled);
        verify(auctionRepo, never()).save(any());
    }

    @Test
    void reconcile_alreadyMatchesComputedStatus_skipsWrite() {
        Auction parent = auctionFixture(100L, AuctionStatus.Started);
        when(auctionRepo.findByIdForUpdate(100L)).thenReturn(Optional.of(parent));
        when(schedulingRepo.findByAuctionIdOrderByRoundAsc(100L)).thenReturn(List.of(
                roundWith(SchedulingAuctionStatus.Started),
                roundWith(SchedulingAuctionStatus.Scheduled),
                roundWith(SchedulingAuctionStatus.Scheduled)
        ));

        reconciler.reconcile(100L);

        verify(auctionRepo, never()).save(any());
        assertThat(parent.getChangedDate()).isNotEqualTo(now);
    }

    @Test
    void reconcile_missingAuction_throws() {
        when(auctionRepo.findByIdForUpdate(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reconciler.reconcile(404L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("404");
    }

    private Auction auctionFixture(long id, AuctionStatus status) {
        Auction a = new Auction();
        try {
            java.lang.reflect.Field f = Auction.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(a, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        a.setAuctionStatus(status);
        return a;
    }

    private SchedulingAuction roundWith(SchedulingAuctionStatus status) {
        SchedulingAuction r = new SchedulingAuction();
        r.setRoundStatus(status);
        return r;
    }
}
