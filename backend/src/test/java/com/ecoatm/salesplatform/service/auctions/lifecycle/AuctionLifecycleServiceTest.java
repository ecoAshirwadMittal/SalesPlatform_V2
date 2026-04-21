package com.ecoatm.salesplatform.service.auctions.lifecycle;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.event.RoundStartedEvent;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuctionLifecycleServiceTest {

    @Mock private SchedulingAuctionRepository schedulingRepo;
    @Mock private RoundTransitionService roundTransitions;
    @Mock private AuctionStatusReconciler statusReconciler;

    private AuctionLifecycleService service;
    private final Instant now = Instant.parse("2026-04-20T12:00:00Z");

    @BeforeEach
    void setUp() {
        service = new AuctionLifecycleService(schedulingRepo, roundTransitions, statusReconciler,
                Clock.fixed(now, ZoneOffset.UTC));
    }

    @Test
    void tick_runsCloseThenStartThenReconcile() {
        when(schedulingRepo.findIdsToClose(now)).thenReturn(List.of(11L));
        when(schedulingRepo.findIdsToStart(now)).thenReturn(List.of(22L));
        when(roundTransitions.closeRound(11L)).thenReturn(new RoundClosedEvent(11L, 1, 100L, 999L));
        when(roundTransitions.startRound(22L)).thenReturn(new RoundStartedEvent(22L, 2, 100L, 999L));

        TickResult result = service.tick();

        assertThat(result.counters().get("roundsClosed")).isEqualTo(1);
        assertThat(result.counters().get("roundsStarted")).isEqualTo(1);
        assertThat(result.counters().get("auctionsAffected")).isEqualTo(1);
        assertThat(result.errorCount()).isZero();
        verify(statusReconciler).reconcile(100L);
    }

    @Test
    void tick_swallowsPerRowExceptionAndCountsError() {
        when(schedulingRepo.findIdsToClose(now)).thenReturn(List.of(11L, 12L, 13L));
        when(schedulingRepo.findIdsToStart(now)).thenReturn(List.of());
        when(roundTransitions.closeRound(11L)).thenReturn(new RoundClosedEvent(11L, 1, 100L, 999L));
        when(roundTransitions.closeRound(12L)).thenThrow(new RuntimeException("boom"));
        when(roundTransitions.closeRound(13L)).thenReturn(new RoundClosedEvent(13L, 3, 100L, 999L));

        TickResult result = service.tick();

        assertThat(result.counters().get("roundsClosed")).isEqualTo(2);
        assertThat(result.errorCount()).isEqualTo(1);
        verify(statusReconciler).reconcile(100L);
    }

    @Test
    void tick_alreadyTransitionedException_isBenign() {
        when(schedulingRepo.findIdsToClose(now)).thenReturn(List.of(11L));
        when(schedulingRepo.findIdsToStart(now)).thenReturn(List.of());
        when(roundTransitions.closeRound(11L))
                .thenThrow(new RoundAlreadyTransitionedException(11L));

        TickResult result = service.tick();

        assertThat(result.counters().get("roundsClosed")).isEqualTo(0);
        assertThat(result.errorCount()).isZero();
        verify(statusReconciler, never()).reconcile(anyLong());
    }

    @Test
    void tick_deduplicatesAffectedAuctions() {
        when(schedulingRepo.findIdsToClose(now)).thenReturn(List.of(11L, 12L));
        when(schedulingRepo.findIdsToStart(now)).thenReturn(List.of(33L));
        when(roundTransitions.closeRound(11L)).thenReturn(new RoundClosedEvent(11L, 1, 100L, 999L));
        when(roundTransitions.closeRound(12L)).thenReturn(new RoundClosedEvent(12L, 2, 100L, 999L));
        when(roundTransitions.startRound(33L)).thenReturn(new RoundStartedEvent(33L, 1, 200L, 888L));

        service.tick();

        verify(statusReconciler).reconcile(100L);
        verify(statusReconciler).reconcile(200L);
        verify(statusReconciler, times(2)).reconcile(anyLong());
    }

    @Test
    void tick_reconcilerFailureIsCountedNotPropagated() {
        when(schedulingRepo.findIdsToClose(now)).thenReturn(List.of(11L));
        when(schedulingRepo.findIdsToStart(now)).thenReturn(List.of());
        when(roundTransitions.closeRound(11L)).thenReturn(new RoundClosedEvent(11L, 1, 100L, 999L));
        doThrow(new RuntimeException("DB down")).when(statusReconciler).reconcile(100L);

        TickResult result = service.tick();

        assertThat(result.errorCount()).isEqualTo(1);
        assertThat(result.counters().get("roundsClosed")).isEqualTo(1);
    }
}
