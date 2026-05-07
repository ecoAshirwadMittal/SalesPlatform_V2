package com.ecoatm.salesplatform.service.auctions.r3init;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class R3PreProcessListenerTest {

    @Mock R3PreProcessService service;
    @Mock SchedulingAuctionRepository saRepo;
    @InjectMocks R3PreProcessListener listener;

    private SchedulingAuction r3Sa(long id) {
        SchedulingAuction sa = new SchedulingAuction();
        sa.setId(id);
        sa.setRound(3);
        sa.setAuctionId(600L);
        return sa;
    }

    @Test
    @DisplayName("round 2 close + R3 SA exists → service.run(r2, r3)")
    void round_2_triggers_service() {
        when(saRepo.findByAuctionIdAndRound(600L, 3)).thenReturn(Optional.of(r3Sa(6003L)));
        listener.onRoundClosed(new RoundClosedEvent(6002L, 2, 600L, 601L));
        verify(service).run(6002L, 6003L);
    }

    @Test
    @DisplayName("round 1 close → no service call")
    void round_1_skipped() {
        listener.onRoundClosed(new RoundClosedEvent(6001L, 1, 600L, 601L));
        verifyNoInteractions(service, saRepo);
    }

    @Test
    @DisplayName("round 3 close → no service call (R3 pre-process is R2-close only)")
    void round_3_skipped() {
        listener.onRoundClosed(new RoundClosedEvent(6003L, 3, 600L, 601L));
        verifyNoInteractions(service, saRepo);
    }

    @Test
    @DisplayName("no R3 SA for the auction → silent log + return, no service call")
    void no_r3_sa_silent_skip() {
        when(saRepo.findByAuctionIdAndRound(601L, 3)).thenReturn(Optional.empty());
        listener.onRoundClosed(new RoundClosedEvent(6012L, 2, 601L, 602L));
        verify(service, never()).run(anyLong(), anyLong());
    }

    @Test
    @DisplayName("RecalcAlreadyRunningException is logged, not propagated")
    void already_running_swallowed() {
        when(saRepo.findByAuctionIdAndRound(600L, 3)).thenReturn(Optional.of(r3Sa(6003L)));
        when(service.run(6002L, 6003L)).thenThrow(
            new RecalcAlreadyRunningException(
                RecalcAlreadyRunningException.Process.R3_PREPROCESS, 6003L));
        // Should not throw out of listener
        listener.onRoundClosed(new RoundClosedEvent(6002L, 2, 600L, 601L));
    }

    @Test
    @DisplayName("any RuntimeException is logged, not propagated")
    void unexpected_exception_swallowed() {
        when(saRepo.findByAuctionIdAndRound(600L, 3)).thenReturn(Optional.of(r3Sa(6003L)));
        when(service.run(6002L, 6003L)).thenThrow(new RuntimeException("boom"));
        listener.onRoundClosed(new RoundClosedEvent(6002L, 2, 600L, 601L));
    }
}
