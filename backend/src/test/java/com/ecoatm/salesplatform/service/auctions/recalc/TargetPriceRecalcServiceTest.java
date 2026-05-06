package com.ecoatm.salesplatform.service.auctions.recalc;

import com.ecoatm.salesplatform.event.TargetPriceRecalculatedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.repository.auctions.TargetPriceRecalcRepository;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetPriceRecalcServiceTest {

    @Mock TargetPriceRecalcRepository repo;
    @Mock SchedulingAuctionRepository saRepo;
    @Mock RecalcStatusUpdater statusUpdater;
    @Mock ApplicationEventPublisher events;

    @InjectMocks TargetPriceRecalcService service;

    private SchedulingAuction sa;

    @BeforeEach
    void setUp() {
        sa = new SchedulingAuction();
        sa.setId(9001L);
        sa.setRound(1);
        sa.setAuctionId(9001L);
    }

    @Test
    void happy_path_publishes_target_price_event() {
        when(saRepo.findById(9001L)).thenReturn(Optional.of(sa));
        when(saRepo.findWeekIdById(9001L)).thenReturn(9001L);
        when(statusUpdater.tryFlipToRunning(9001L, "TARGET_PRICE")).thenReturn(true);
        when(repo.recalcClosedRound(9001L, 1)).thenReturn(6);

        service.run(9001L);

        verify(statusUpdater).markSuccess(9001L, "TARGET_PRICE");
        ArgumentCaptor<TargetPriceRecalculatedEvent> c =
            ArgumentCaptor.forClass(TargetPriceRecalculatedEvent.class);
        verify(events).publishEvent(c.capture());
        assertThat(c.getValue().closedRound()).isEqualTo(1);
    }

    @Test
    void rejects_when_already_running() {
        when(saRepo.findById(9001L)).thenReturn(Optional.of(sa));
        when(statusUpdater.tryFlipToRunning(9001L, "TARGET_PRICE")).thenReturn(false);

        assertThatThrownBy(() -> service.run(9001L))
            .isInstanceOf(RecalcAlreadyRunningException.class);
        verify(repo, never()).recalcClosedRound(anyLong(), anyInt());
    }

    @Test
    void marks_failed_and_rethrows() {
        when(saRepo.findById(9001L)).thenReturn(Optional.of(sa));
        when(statusUpdater.tryFlipToRunning(9001L, "TARGET_PRICE")).thenReturn(true);
        RuntimeException boom = new RuntimeException("kaboom");
        when(repo.recalcClosedRound(9001L, 1)).thenThrow(boom);

        assertThatThrownBy(() -> service.run(9001L)).isSameAs(boom);
        verify(statusUpdater).markFailed(eq(9001L), eq("TARGET_PRICE"),
            org.mockito.ArgumentMatchers.argThat(s -> s.contains("kaboom")));
        verify(events, never()).publishEvent(any());
    }

    @Test
    void rejects_round_3() {
        sa.setRound(3);
        when(saRepo.findById(9001L)).thenReturn(Optional.of(sa));

        assertThatThrownBy(() -> service.run(9001L))
            .isInstanceOf(IllegalArgumentException.class);
        verify(statusUpdater, never()).tryFlipToRunning(anyLong(), anyString());
    }

    @Test
    void passes_full_error_message_to_status_updater() {
        // Truncation lives in RecalcStatusUpdater.markFailed; here we just verify
        // TargetPriceRecalcService passes the whole message through, prefixed with
        // the exception class name. The 4000-char cap is exercised by
        // RecalcStatusUpdater tests / repository ITs.
        when(saRepo.findById(9001L)).thenReturn(Optional.of(sa));
        when(statusUpdater.tryFlipToRunning(9001L, "TARGET_PRICE")).thenReturn(true);
        String huge = "y".repeat(5000);
        when(repo.recalcClosedRound(9001L, 1)).thenThrow(new RuntimeException(huge));

        assertThatThrownBy(() -> service.run(9001L)).isInstanceOf(RuntimeException.class);
        ArgumentCaptor<String> err = ArgumentCaptor.forClass(String.class);
        verify(statusUpdater).markFailed(eq(9001L), eq("TARGET_PRICE"), err.capture());
        assertThat(err.getValue())
            .startsWith("RuntimeException: ")
            .contains(huge);
    }
}
