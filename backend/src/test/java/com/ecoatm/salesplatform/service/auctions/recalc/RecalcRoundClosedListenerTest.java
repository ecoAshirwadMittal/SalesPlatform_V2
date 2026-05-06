package com.ecoatm.salesplatform.service.auctions.recalc;

import com.ecoatm.salesplatform.event.RoundClosedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecalcRoundClosedListenerTest {

    @Mock RecalcOrchestrator orchestrator;

    @InjectMocks RecalcRoundClosedListener listener;

    @Test
    void triggers_for_round_1() {
        listener.onRoundClosed(new RoundClosedEvent(9001L, 1, 9001L, 9001L));

        verify(orchestrator).runForClosedRound(9001L);
    }

    @Test
    void triggers_for_round_2() {
        listener.onRoundClosed(new RoundClosedEvent(9002L, 2, 9001L, 9001L));

        verify(orchestrator).runForClosedRound(9002L);
    }

    @Test
    void skips_round_3() {
        listener.onRoundClosed(new RoundClosedEvent(9003L, 3, 9001L, 9001L));

        verify(orchestrator, never()).runForClosedRound(anyLong());
    }

    @Test
    void swallows_orchestrator_throw() {
        doThrow(new RuntimeException("boom"))
            .when(orchestrator).runForClosedRound(9001L);

        // Must not throw — would corrupt other AFTER_COMMIT listeners.
        listener.onRoundClosed(new RoundClosedEvent(9001L, 1, 9001L, 9001L));
    }

    private static long anyLong() { return org.mockito.ArgumentMatchers.anyLong(); }
}
