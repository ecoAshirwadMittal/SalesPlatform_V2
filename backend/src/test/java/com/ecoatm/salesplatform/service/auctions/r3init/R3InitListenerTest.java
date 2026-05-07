package com.ecoatm.salesplatform.service.auctions.r3init;

import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class R3InitListenerTest {

    @Mock R3InitService service;
    @InjectMocks R3InitListener listener;

    @Test
    @DisplayName("round 3 start triggers service")
    void round_3_triggers_service() {
        listener.onRoundStarted(new RoundStartedEvent(6003L, 3, 601L, 600L));
        verify(service).run(6003L);
    }

    @Test
    @DisplayName("round 1 + 2 start ignored")
    void other_rounds_ignored() {
        listener.onRoundStarted(new RoundStartedEvent(6001L, 1, 601L, 600L));
        listener.onRoundStarted(new RoundStartedEvent(6002L, 2, 601L, 600L));
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("predecessor-guard IllegalStateException logged but not propagated")
    void predecessor_guard_swallowed() {
        when(service.run(6003L)).thenThrow(
            new IllegalStateException("R3 init refused — r3_preprocess_status is PENDING"));
        listener.onRoundStarted(new RoundStartedEvent(6003L, 3, 601L, 600L));
    }

    @Test
    @DisplayName("RecalcAlreadyRunningException logged but not propagated")
    void already_running_swallowed() {
        when(service.run(6003L)).thenThrow(
            new RecalcAlreadyRunningException(
                RecalcAlreadyRunningException.Process.R3_INIT, 6003L));
        listener.onRoundStarted(new RoundStartedEvent(6003L, 3, 601L, 600L));
    }

    @Test
    @DisplayName("RuntimeException logged but not propagated")
    void unexpected_exception_swallowed() {
        when(service.run(6003L)).thenThrow(new RuntimeException("boom"));
        listener.onRoundStarted(new RoundStartedEvent(6003L, 3, 601L, 600L));
    }
}
