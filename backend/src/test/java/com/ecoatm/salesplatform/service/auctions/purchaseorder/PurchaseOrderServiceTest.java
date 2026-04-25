package com.ecoatm.salesplatform.service.auctions.purchaseorder;

import com.ecoatm.salesplatform.dto.PurchaseOrderRequest;
import com.ecoatm.salesplatform.dto.PurchaseOrderRow;
import com.ecoatm.salesplatform.event.PurchaseOrderChangedEvent;
import com.ecoatm.salesplatform.model.auctions.PurchaseOrder;
import com.ecoatm.salesplatform.model.auctions.PurchaseOrderLifecycleState;
import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.auctions.PODetailRepository;
import com.ecoatm.salesplatform.repository.auctions.PurchaseOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PurchaseOrderServiceTest {

    PurchaseOrderRepository poRepo;
    PODetailRepository detailRepo;
    PurchaseOrderValidator validator;
    ApplicationEventPublisher events;
    PurchaseOrderService service;

    @BeforeEach
    void init() {
        poRepo = mock(PurchaseOrderRepository.class);
        detailRepo = mock(PODetailRepository.class);
        validator = mock(PurchaseOrderValidator.class);
        events = mock(ApplicationEventPublisher.class);
        service = new PurchaseOrderService(poRepo, detailRepo, validator, events);
    }

    @Test
    void createValidPersistsAndPublishesEvent() {
        Week from = makeWeek(1L, 202501, "2025 / Wk1");
        Week to = makeWeek(2L, 202504, "2025 / Wk4");
        when(validator.resolveWeekRange(1L, 2L))
                .thenReturn(new PurchaseOrderValidator.WeekRange(from, to));
        when(poRepo.save(any(PurchaseOrder.class))).thenAnswer(inv -> {
            PurchaseOrder po = inv.getArgument(0);
            try {
                var idField = PurchaseOrder.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(po, 42L);
            } catch (Exception e) { throw new RuntimeException(e); }
            return po;
        });
        when(detailRepo.countByPurchaseOrderId(42L)).thenReturn(0L);

        PurchaseOrderRow row = service.create(new PurchaseOrderRequest(1L, 2L));

        assertThat(row.id()).isEqualTo(42L);
        assertThat(row.weekRangeLabel()).isEqualTo("2025 / Wk1 - 2025 / Wk4");
        verify(events).publishEvent(new PurchaseOrderChangedEvent(42L,
                PurchaseOrderChangedEvent.Action.UPSERT));
    }

    @Test
    void findByIdNotFoundThrows() {
        when(poRepo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOfSatisfying(PurchaseOrderException.class, ex ->
                        assertThat(ex.getCode()).isEqualTo("PURCHASE_ORDER_NOT_FOUND"));
    }

    @Test
    void deletePublishesDeleteEvent() {
        PurchaseOrder po = makePo(7L);
        when(poRepo.findById(7L)).thenReturn(Optional.of(po));
        service.delete(7L);
        verify(poRepo).delete(po);
        verify(events).publishEvent(new PurchaseOrderChangedEvent(7L,
                PurchaseOrderChangedEvent.Action.DELETE));
    }

    @Test
    void rowMappingDerivesLifecycleState() {
        Week from = makeWeek(1L, 202501, "2025 / Wk1");
        Week to = makeWeek(2L, 202504, "2025 / Wk4");
        from.setWeekStartDateTime(Instant.now().minusSeconds(86400 * 30));
        from.setWeekEndDateTime(Instant.now().minusSeconds(86400 * 24));
        to.setWeekStartDateTime(Instant.now().minusSeconds(86400 * 23));
        to.setWeekEndDateTime(Instant.now().minusSeconds(86400 * 17));

        PurchaseOrder po = makePo(11L);
        po.setWeekFrom(from); po.setWeekTo(to);
        po.setWeekRangeLabel("2025 / Wk1 - 2025 / Wk4");

        when(poRepo.findById(11L)).thenReturn(Optional.of(po));
        when(detailRepo.countByPurchaseOrderId(11L)).thenReturn(0L);

        PurchaseOrderRow row = service.findById(11L);
        assertThat(row.state()).isEqualTo(PurchaseOrderLifecycleState.CLOSED);
    }

    private static Week makeWeek(long id, int weekId, String label) {
        Week w = new Week();
        try {
            var f = Week.class.getDeclaredField("id");
            f.setAccessible(true); f.set(w, id);
        } catch (Exception e) { throw new RuntimeException(e); }
        w.setWeekId(weekId);
        w.setWeekDisplay(label);
        w.setWeekStartDateTime(Instant.now().atOffset(ZoneOffset.UTC).minusDays(1).toInstant());
        w.setWeekEndDateTime(Instant.now().atOffset(ZoneOffset.UTC).plusDays(6).toInstant());
        w.setYear(2025);
        w.setWeekNumber(1);
        return w;
    }

    private static PurchaseOrder makePo(long id) {
        PurchaseOrder po = new PurchaseOrder();
        try {
            var f = PurchaseOrder.class.getDeclaredField("id");
            f.setAccessible(true); f.set(po, id);
        } catch (Exception e) { throw new RuntimeException(e); }
        return po;
    }
}
