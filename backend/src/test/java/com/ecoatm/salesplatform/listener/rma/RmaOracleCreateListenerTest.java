package com.ecoatm.salesplatform.listener.rma;

import com.ecoatm.salesplatform.event.rma.RmaReviewCompletedEvent;
import com.ecoatm.salesplatform.event.rma.RmaReviewOutcome;
import com.ecoatm.salesplatform.service.rma.RmaOracleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link RmaOracleCreateListener} — the guard + swallow behaviour
 * of the AFTER_COMMIT reaction to {@link RmaReviewCompletedEvent}. The actual
 * "build payload / submitRma / write oracle_* columns" work is proven in
 * {@code RmaOracleServiceTest}; here we only assert the listener gates on
 * outcome + enabled flag and can never surface an exception (it must not affect
 * the already-committed review).
 */
@ExtendWith(MockitoExtension.class)
class RmaOracleCreateListenerTest {

    @Mock private RmaOracleService rmaOracleService;

    private RmaReviewCompletedEvent event(RmaReviewOutcome outcome, Long rmaId) {
        return new RmaReviewCompletedEvent(rmaId, outcome, 7L, Instant.now());
    }

    @Test
    @DisplayName("APPROVED outcome → createRmaInOracle called with the rmaId")
    void approved_triggersOracleCreate() {
        RmaOracleCreateListener listener = new RmaOracleCreateListener(rmaOracleService, true);

        listener.onRmaReviewCompleted(event(RmaReviewOutcome.APPROVED, 42L));

        verify(rmaOracleService).createRmaInOracle(42L);
    }

    @Test
    @DisplayName("DECLINED outcome → createRmaInOracle never called")
    void declined_skipsOracleCreate() {
        RmaOracleCreateListener listener = new RmaOracleCreateListener(rmaOracleService, true);

        listener.onRmaReviewCompleted(event(RmaReviewOutcome.DECLINED, 42L));

        verify(rmaOracleService, never()).createRmaInOracle(anyLong());
    }

    @Test
    @DisplayName("disabled flag → createRmaInOracle never called even for APPROVED")
    void disabled_skipsOracleCreate() {
        RmaOracleCreateListener listener = new RmaOracleCreateListener(rmaOracleService, false);

        listener.onRmaReviewCompleted(event(RmaReviewOutcome.APPROVED, 42L));

        verify(rmaOracleService, never()).createRmaInOracle(anyLong());
    }

    @Test
    @DisplayName("null rmaId → createRmaInOracle never called (no NPE)")
    void nullRmaId_skips() {
        RmaOracleCreateListener listener = new RmaOracleCreateListener(rmaOracleService, true);

        listener.onRmaReviewCompleted(event(RmaReviewOutcome.APPROVED, null));

        verify(rmaOracleService, never()).createRmaInOracle(anyLong());
    }

    @Test
    @DisplayName("createRmaInOracle throwing is swallowed — the listener never rethrows")
    void oracleCreateThrows_isSwallowed() {
        RmaOracleCreateListener listener = new RmaOracleCreateListener(rmaOracleService, true);
        doThrow(new RuntimeException("Oracle boom")).when(rmaOracleService).createRmaInOracle(42L);

        assertThatCode(() -> listener.onRmaReviewCompleted(event(RmaReviewOutcome.APPROVED, 42L)))
                .doesNotThrowAnyException();
        verify(rmaOracleService).createRmaInOracle(42L);
    }
}
