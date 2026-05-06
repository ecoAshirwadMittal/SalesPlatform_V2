package com.ecoatm.salesplatform.service.auctions.recalc;

import com.ecoatm.salesplatform.event.BidRankingUpdatedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.BidRankingRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BidRankingServiceTest {

    @Mock BidRankingRepository repo;
    @Mock SchedulingAuctionRepository saRepo;
    @Mock RecalcStatusUpdater statusUpdater;
    @Mock ApplicationEventPublisher events;

    @InjectMocks BidRankingService service;

    private SchedulingAuction sa;

    @BeforeEach
    void setUp() {
        sa = new SchedulingAuction();
        sa.setId(9001L);
        sa.setRound(1);
        sa.setAuctionId(9001L);
    }

    @Test
    void happy_path_flips_runs_marks_success_publishes_event() {
        when(saRepo.findById(9001L)).thenReturn(Optional.of(sa));
        when(saRepo.findWeekIdById(9001L)).thenReturn(9001L);
        when(statusUpdater.tryFlipToRunning(9001L, "RANKING")).thenReturn(true);
        when(repo.rankClosedRound(9001L, 1)).thenReturn(8);

        service.run(9001L);

        verify(statusUpdater).tryFlipToRunning(9001L, "RANKING");
        verify(repo).rankClosedRound(9001L, 1);
        verify(statusUpdater).markSuccess(9001L, "RANKING");

        ArgumentCaptor<BidRankingUpdatedEvent> captor =
            ArgumentCaptor.forClass(BidRankingUpdatedEvent.class);
        verify(events).publishEvent(captor.capture());
        BidRankingUpdatedEvent e = captor.getValue();
        assertThat(e.schedulingAuctionId()).isEqualTo(9001L);
        assertThat(e.closedRound()).isEqualTo(1);
        assertThat(e.weekId()).isEqualTo(9001L);
        assertThat(e.auctionId()).isEqualTo(9001L);
    }

    @Test
    void rejects_when_status_already_running() {
        when(saRepo.findById(9001L)).thenReturn(Optional.of(sa));
        when(saRepo.findWeekIdById(9001L)).thenReturn(9001L);
        when(statusUpdater.tryFlipToRunning(9001L, "RANKING")).thenReturn(false);

        assertThatThrownBy(() -> service.run(9001L))
            .isInstanceOf(RecalcAlreadyRunningException.class);

        verify(repo, never()).rankClosedRound(anyLong(), anyInt());
        verify(events, never()).publishEvent(any());
    }

    @Test
    void marks_failed_and_rethrows_when_repo_throws() {
        when(saRepo.findById(9001L)).thenReturn(Optional.of(sa));
        when(saRepo.findWeekIdById(9001L)).thenReturn(9001L);
        when(statusUpdater.tryFlipToRunning(9001L, "RANKING")).thenReturn(true);
        RuntimeException boom = new RuntimeException("DB exploded");
        when(repo.rankClosedRound(9001L, 1)).thenThrow(boom);

        assertThatThrownBy(() -> service.run(9001L))
            .isSameAs(boom);

        verify(statusUpdater).markFailed(eq(9001L), eq("RANKING"),
            org.mockito.ArgumentMatchers.argThat(s -> s.contains("DB exploded")));
        verify(events, never()).publishEvent(any());
    }

    @Test
    void rejects_round_3() {
        sa.setRound(3);
        when(saRepo.findById(9001L)).thenReturn(Optional.of(sa));

        assertThatThrownBy(() -> service.run(9001L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("only valid for closed round 1 or 2");

        verify(statusUpdater, never()).tryFlipToRunning(anyLong(), anyString());
    }

    @Test
    void passes_full_error_message_to_status_updater() {
        // Truncation lives in RecalcStatusUpdater.markFailed; here we just verify
        // BidRankingService passes the whole message through, prefixed with the
        // exception class name. The 4000-char cap is exercised by RecalcStatusUpdater
        // tests / repository ITs.
        when(saRepo.findById(9001L)).thenReturn(Optional.of(sa));
        when(saRepo.findWeekIdById(9001L)).thenReturn(9001L);
        when(statusUpdater.tryFlipToRunning(9001L, "RANKING")).thenReturn(true);
        String huge = "x".repeat(5000);
        when(repo.rankClosedRound(9001L, 1)).thenThrow(new RuntimeException(huge));

        assertThatThrownBy(() -> service.run(9001L)).isInstanceOf(RuntimeException.class);

        ArgumentCaptor<String> errCaptor = ArgumentCaptor.forClass(String.class);
        verify(statusUpdater).markFailed(eq(9001L), eq("RANKING"), errCaptor.capture());
        assertThat(errCaptor.getValue())
            .startsWith("RuntimeException: ")
            .contains(huge);
    }

    private static int anyInt() { return org.mockito.ArgumentMatchers.anyInt(); }
}
