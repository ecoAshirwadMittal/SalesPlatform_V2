package com.ecoatm.salesplatform.service.auctions.r2init;

import com.ecoatm.salesplatform.event.RoundStartedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class R2BuyerAssignmentListenerTest {

    @Mock
    R2BuyerAssignmentService service;

    @InjectMocks
    R2BuyerAssignmentListener listener;

    @Test
    @DisplayName("round=2 triggers service.run with the roundId (schedulingAuctionId)")
    void round_2_triggers_service() {
        listener.onRoundStarted(new RoundStartedEvent(502L, 2, 100L, 999L));

        verify(service).run(502L);
    }

    @Test
    @DisplayName("round=1 does NOT trigger service")
    void round_1_skipped() {
        listener.onRoundStarted(new RoundStartedEvent(501L, 1, 100L, 999L));

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("round=3 does NOT trigger service")
    void round_3_skipped() {
        listener.onRoundStarted(new RoundStartedEvent(503L, 3, 100L, 999L));

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("RecalcAlreadyRunningException is logged, not propagated")
    void already_running_swallowed() {
        when(service.run(anyLong())).thenThrow(new RecalcAlreadyRunningException(
            RecalcAlreadyRunningException.Process.R2_INIT, 502L));

        assertThatCode(() ->
            listener.onRoundStarted(new RoundStartedEvent(502L, 2, 100L, 999L))
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("any RuntimeException is logged, not propagated")
    void unexpected_exception_swallowed() {
        when(service.run(anyLong())).thenThrow(new RuntimeException("boom"));

        assertThatCode(() ->
            listener.onRoundStarted(new RoundStartedEvent(502L, 2, 100L, 999L))
        ).doesNotThrowAnyException();
    }
}
