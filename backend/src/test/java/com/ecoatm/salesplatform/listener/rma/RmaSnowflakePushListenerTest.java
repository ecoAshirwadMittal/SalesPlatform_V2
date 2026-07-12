package com.ecoatm.salesplatform.listener.rma;

import com.ecoatm.salesplatform.event.rma.RmaReviewCompletedEvent;
import com.ecoatm.salesplatform.event.rma.RmaReviewOutcome;
import com.ecoatm.salesplatform.model.pws.Rma;
import com.ecoatm.salesplatform.model.pws.RmaItem;
import com.ecoatm.salesplatform.repository.pws.RmaItemRepository;
import com.ecoatm.salesplatform.repository.pws.RmaRepository;
import com.ecoatm.salesplatform.service.BuyerCodeLookupService;
import com.ecoatm.salesplatform.service.rma.RmaSnowflakePayload;
import com.ecoatm.salesplatform.service.rma.RmaSnowflakeWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link RmaSnowflakePushListener} — the AFTER_COMMIT reaction to
 * {@link RmaReviewCompletedEvent} that pushes the RMA snapshot to Snowflake.
 * Mirrors {@code PurchaseOrderSnowflakePushListenerTest} (writer verification +
 * disabled short-circuit + swallowed exception) and
 * {@code RmaOracleCreateListenerTest} (constructor-injected {@code enabled}
 * boolean).
 *
 * <p>Legacy {@code ACT_RMADetails_CompleteReview} calls
 * {@code SUB_SendRMADetailsToSnowflake} on <b>both</b> the approved and declined
 * branches, so this listener pushes on <b>any</b> completion — the DECLINED case
 * below asserts the push still happens (it is not gated on outcome, unlike the
 * Oracle-create listener).
 */
@ExtendWith(MockitoExtension.class)
class RmaSnowflakePushListenerTest {

    @Mock private RmaSnowflakeWriter writer;
    @Mock private RmaRepository rmaRepository;
    @Mock private RmaItemRepository rmaItemRepository;
    @Mock private BuyerCodeLookupService buyerCodeLookup;

    private RmaSnowflakePushListener listener(boolean enabled) {
        return new RmaSnowflakePushListener(writer, rmaRepository, rmaItemRepository,
                buyerCodeLookup, enabled);
    }

    private RmaReviewCompletedEvent event(RmaReviewOutcome outcome, Long rmaId) {
        return new RmaReviewCompletedEvent(rmaId, outcome, 7L, Instant.now());
    }

    private Rma stubRma(long id, String status) {
        Rma rma = new Rma();
        rma.setId(id);
        rma.setNumber("RMA-000042");
        rma.setBuyerCodeId(11L);
        rma.setSystemStatus(status);
        rma.setRequestSkus(3);
        rma.setRequestQty(5);
        rma.setRequestSalesTotal(new BigDecimal("1250.00"));
        rma.setApprovedSkus(2);
        rma.setApprovedQty(3);
        rma.setApprovedSalesTotal(new BigDecimal("800.00"));
        rma.setApprovedCount(2);
        rma.setDeclinedCount(1);
        rma.setReviewedByUserId(7L);
        return rma;
    }

    private RmaItem stubItem() {
        RmaItem item = new RmaItem();
        item.setId(100L);
        item.setDeviceId(555L);
        item.setImei("356938035643809");
        item.setOrderNumber("SO-12345");
        item.setSalePrice(new BigDecimal("400.00"));
        item.setReturnReason("DOA");
        item.setStatus("Approve");
        item.setStatusDisplay("Approved");
        return item;
    }

    @Test
    @DisplayName("APPROVED completion → writer.push called once with a full snapshot")
    void approvedCompletion_pushesSnapshot() {
        when(rmaRepository.findById(42L)).thenReturn(Optional.of(stubRma(42L, "Approved")));
        when(rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(42L)).thenReturn(List.of(stubItem()));
        when(buyerCodeLookup.findCodeById(11L)).thenReturn("ABC");

        listener(true).onRmaReviewCompleted(event(RmaReviewOutcome.APPROVED, 42L));

        ArgumentCaptor<RmaSnowflakePayload> captor = ArgumentCaptor.forClass(RmaSnowflakePayload.class);
        verify(writer).push(captor.capture());
        RmaSnowflakePayload p = captor.getValue();
        assertThat(p.rmaId()).isEqualTo(42L);
        assertThat(p.rmaNumber()).isEqualTo("RMA-000042");
        assertThat(p.buyerCodeId()).isEqualTo(11L);
        assertThat(p.buyerCode()).isEqualTo("ABC");
        assertThat(p.systemStatus()).isEqualTo("Approved");
        assertThat(p.requestSalesTotal()).isEqualByComparingTo("1250.00");
        assertThat(p.items()).hasSize(1);
        assertThat(p.items().get(0).imei()).isEqualTo("356938035643809");
        assertThat(p.items().get(0).statusDisplay()).isEqualTo("Approved");
    }

    @Test
    @DisplayName("DECLINED completion → still pushes (legacy pushes on both branches)")
    void declinedCompletion_alsoPushes() {
        when(rmaRepository.findById(42L)).thenReturn(Optional.of(stubRma(42L, "Declined")));
        when(rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(42L)).thenReturn(List.of());
        when(buyerCodeLookup.findCodeById(11L)).thenReturn("ABC");

        listener(true).onRmaReviewCompleted(event(RmaReviewOutcome.DECLINED, 42L));

        verify(writer).push(any(RmaSnowflakePayload.class));
    }

    @Test
    @DisplayName("rma.sync.enabled=false → writer.push never called")
    void disabledSync_shortCircuits() {
        listener(false).onRmaReviewCompleted(event(RmaReviewOutcome.APPROVED, 42L));

        verify(writer, never()).push(any());
        verifyNoInteractions(rmaRepository, rmaItemRepository, buyerCodeLookup);
    }

    @Test
    @DisplayName("null rmaId → skipped, no push, no NPE")
    void nullRmaId_skips() {
        listener(true).onRmaReviewCompleted(event(RmaReviewOutcome.APPROVED, null));

        verify(writer, never()).push(any());
    }

    @Test
    @DisplayName("RMA no longer exists → skipped, no push")
    void missingRma_skips() {
        when(rmaRepository.findById(99L)).thenReturn(Optional.empty());

        listener(true).onRmaReviewCompleted(event(RmaReviewOutcome.APPROVED, 99L));

        verify(writer, never()).push(any());
    }

    @Test
    @DisplayName("writer exception is swallowed — the listener never rethrows")
    void writerThrows_isSwallowed() {
        when(rmaRepository.findById(42L)).thenReturn(Optional.of(stubRma(42L, "Approved")));
        when(rmaItemRepository.findByRmaIdOrderByCreatedDateAsc(42L)).thenReturn(List.of(stubItem()));
        when(buyerCodeLookup.findCodeById(11L)).thenReturn("ABC");
        doThrow(new RuntimeException("Snowflake down")).when(writer).push(any());

        assertThatCode(() -> listener(true).onRmaReviewCompleted(event(RmaReviewOutcome.APPROVED, 42L)))
                .doesNotThrowAnyException();
        verify(writer).push(any());
    }
}
