package com.ecoatm.salesplatform.service.auctions.r1init;

import com.ecoatm.salesplatform.event.RoundStartedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class R1InitListenerTest {

    @Mock
    Round1InitializationService service;

    R1InitListener listener;

    @BeforeEach
    void setUp() {
        listener = new R1InitListener(service);
    }

    @Test
    void onRoundStarted_ignoresNonRoundOneEvents() {
        listener.onRoundStarted(new RoundStartedEvent(301L, 2, 101L, 42L));
        verify(service, never()).initialize(anyLong());
    }

    @Test
    void onRoundStarted_callsServiceWithRoundId() {
        when(service.initialize(301L))
                .thenReturn(new Round1InitializationResult(301L, 101L, 42L, 0, 0, 0, 10));

        listener.onRoundStarted(new RoundStartedEvent(301L, 1, 101L, 42L));

        verify(service).initialize(301L);
    }

    @Test
    void onRoundStarted_schedulingAuctionNotFound_doesNotRethrow() {
        when(service.initialize(301L))
                .thenThrow(new SchedulingAuctionNotFoundException(301L));

        listener.onRoundStarted(new RoundStartedEvent(301L, 1, 101L, 42L));
    }

    @Test
    void onRoundStarted_runtimeException_doesNotRethrow() {
        when(service.initialize(301L))
                .thenThrow(new RuntimeException("boom"));

        listener.onRoundStarted(new RoundStartedEvent(301L, 1, 101L, 42L));
    }
}
