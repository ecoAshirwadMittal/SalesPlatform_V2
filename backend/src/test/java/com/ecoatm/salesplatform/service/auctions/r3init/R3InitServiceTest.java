package com.ecoatm.salesplatform.service.auctions.r3init;

import com.ecoatm.salesplatform.event.R3InitCompletedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.RecalcStatus;
import com.ecoatm.salesplatform.model.auctions.ScheduleAuctionInitStatus;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.recalc.RecalcStatusUpdater;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class R3InitServiceTest {

    @Mock SchedulingAuctionRepository saRepo;
    @Mock RecalcStatusUpdater         statusUpdater;
    @Mock ApplicationEventPublisher   events;

    @InjectMocks R3InitService service;

    private SchedulingAuction r3Sa(long id, RecalcStatus preprocessStatus) {
        SchedulingAuction sa = new SchedulingAuction();
        sa.setId(id); sa.setAuctionId(600L); sa.setRound(3);
        sa.setR3PreprocessStatus(preprocessStatus);
        sa.setRound3InitStatus(ScheduleAuctionInitStatus.Pending);
        return sa;
    }

    @Test
    @DisplayName("happy path — predecessor SUCCESS, status RUNNING→SUCCESS, round3InitStatus=Complete, event")
    void happy_path() {
        long id = 6003L;
        SchedulingAuction sa = r3Sa(id, RecalcStatus.SUCCESS);
        when(saRepo.findById(id)).thenReturn(Optional.of(sa));
        when(statusUpdater.tryFlipToRunning(id, "R3_INIT")).thenReturn(true);

        R3InitResult result = service.run(id);

        assertThat(result.durationMs()).isGreaterThanOrEqualTo(0);
        assertThat(sa.getRound3InitStatus()).isEqualTo(ScheduleAuctionInitStatus.Complete);
        verify(saRepo).save(sa);
        verify(statusUpdater).markSuccess(id, "R3_INIT");
        verify(events).publishEvent(any(R3InitCompletedEvent.class));
    }

    @Test
    @DisplayName("predecessor PENDING → IllegalStateException pre-flip, no save, no event")
    void predecessor_pending_refuses() {
        long id = 6003L;
        when(saRepo.findById(id)).thenReturn(Optional.of(r3Sa(id, RecalcStatus.PENDING)));

        assertThatThrownBy(() -> service.run(id))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("PENDING");
        verify(statusUpdater, never()).tryFlipToRunning(anyLong(), any());
        verify(events, never()).publishEvent(any());
    }

    @Test
    @DisplayName("predecessor FAILED → IllegalStateException")
    void predecessor_failed_refuses() {
        long id = 6003L;
        when(saRepo.findById(id)).thenReturn(Optional.of(r3Sa(id, RecalcStatus.FAILED)));
        assertThatThrownBy(() -> service.run(id))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("predecessor SKIPPED → IllegalStateException")
    void predecessor_skipped_refuses() {
        long id = 6003L;
        when(saRepo.findById(id)).thenReturn(Optional.of(r3Sa(id, RecalcStatus.SKIPPED)));
        assertThatThrownBy(() -> service.run(id))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("round != 3 → IllegalArgumentException pre-flip")
    void wrong_round_throws() {
        long id = 6002L;
        SchedulingAuction r2 = new SchedulingAuction();
        r2.setId(id); r2.setRound(2); r2.setR3PreprocessStatus(RecalcStatus.SUCCESS);
        when(saRepo.findById(id)).thenReturn(Optional.of(r2));

        assertThatThrownBy(() -> service.run(id))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("round 3");
    }

    @Test
    @DisplayName("status RUNNING → RecalcAlreadyRunningException")
    void already_running_throws() {
        long id = 6003L;
        when(saRepo.findById(id)).thenReturn(Optional.of(r3Sa(id, RecalcStatus.SUCCESS)));
        when(statusUpdater.tryFlipToRunning(id, "R3_INIT")).thenReturn(false);

        assertThatThrownBy(() -> service.run(id))
            .isInstanceOf(RecalcAlreadyRunningException.class);
    }

    @Test
    @DisplayName("save throw → markFailed and propagate, round3InitStatus left at Complete on entity (set before save)")
    void save_throw_marks_failed() {
        long id = 6003L;
        SchedulingAuction sa = r3Sa(id, RecalcStatus.SUCCESS);
        when(saRepo.findById(id)).thenReturn(Optional.of(sa));
        when(statusUpdater.tryFlipToRunning(id, "R3_INIT")).thenReturn(true);
        when(saRepo.save(sa)).thenThrow(new RuntimeException("db down"));

        assertThatThrownBy(() -> service.run(id))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("db down");
        verify(statusUpdater).markFailed(eq(id), eq("R3_INIT"),
            org.mockito.ArgumentMatchers.contains("db down"));
    }
}
