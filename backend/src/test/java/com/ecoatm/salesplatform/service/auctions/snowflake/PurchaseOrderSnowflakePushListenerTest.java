package com.ecoatm.salesplatform.service.auctions.snowflake;

import com.ecoatm.salesplatform.event.PurchaseOrderChangedEvent;
import com.ecoatm.salesplatform.model.auctions.PODetail;
import com.ecoatm.salesplatform.model.auctions.PurchaseOrder;
import com.ecoatm.salesplatform.model.buyermgmt.BuyerCode;
import com.ecoatm.salesplatform.model.mdm.Week;
import com.ecoatm.salesplatform.repository.auctions.PODetailRepository;
import com.ecoatm.salesplatform.repository.auctions.PurchaseOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class PurchaseOrderSnowflakePushListenerTest {

    PurchaseOrderRepository poRepo;
    PODetailRepository detailRepo;
    PurchaseOrderSnowflakeWriter writer;
    Environment env;
    PurchaseOrderSnowflakePushListener listener;

    @BeforeEach
    void init() {
        poRepo = mock(PurchaseOrderRepository.class);
        detailRepo = mock(PODetailRepository.class);
        writer = mock(PurchaseOrderSnowflakeWriter.class);
        env = mock(Environment.class);
        when(env.getProperty("po.sync.enabled", "true")).thenReturn("true");
        listener = new PurchaseOrderSnowflakePushListener(poRepo, detailRepo, writer, env);
    }

    @Test
    void upsertEventCallsWriterUpsert() {
        PurchaseOrder po = stubPo(7L);
        when(poRepo.findByIdWithDetails(7L)).thenReturn(Optional.of(po));
        listener.onChange(new PurchaseOrderChangedEvent(7L,
                PurchaseOrderChangedEvent.Action.UPSERT));
        verify(writer).upsert(any(PurchaseOrderSnowflakePayload.class));
    }

    @Test
    void deleteEventCallsWriterDelete() {
        listener.onChange(new PurchaseOrderChangedEvent(7L,
                PurchaseOrderChangedEvent.Action.DELETE));
        verify(writer).delete(7L);
    }

    @Test
    void writerExceptionDoesNotPropagate() {
        PurchaseOrder po = stubPo(7L);
        when(poRepo.findByIdWithDetails(7L)).thenReturn(Optional.of(po));
        doThrow(new RuntimeException("Snowflake down"))
                .when(writer).upsert(any());
        listener.onChange(new PurchaseOrderChangedEvent(7L,
                PurchaseOrderChangedEvent.Action.UPSERT));  // must NOT throw
    }

    @Test
    void disabledSyncShortCircuits() {
        when(env.getProperty("po.sync.enabled", "true")).thenReturn("false");
        listener.onChange(new PurchaseOrderChangedEvent(7L,
                PurchaseOrderChangedEvent.Action.UPSERT));
        verify(writer, never()).upsert(any());
        verify(writer, never()).delete(anyLong());
    }

    @Test
    void missingPoOnUpsertLogsAndSkips() {
        when(poRepo.findByIdWithDetails(99L)).thenReturn(Optional.empty());
        listener.onChange(new PurchaseOrderChangedEvent(99L,
                PurchaseOrderChangedEvent.Action.UPSERT));  // no throw
        verify(writer, never()).upsert(any());
    }

    private static PurchaseOrder stubPo(long id) {
        PurchaseOrder po = new PurchaseOrder();
        try {
            var f = PurchaseOrder.class.getDeclaredField("id");
            f.setAccessible(true); f.set(po, id);
        } catch (Exception e) { throw new RuntimeException(e); }
        Week w1 = new Week(); w1.setYear(2025); w1.setWeekNumber(1);
        Week w2 = new Week(); w2.setYear(2025); w2.setWeekNumber(4);
        try {
            var wf = Week.class.getDeclaredField("id");
            wf.setAccessible(true); wf.set(w1, 1L); wf.set(w2, 2L);
        } catch (Exception e) { throw new RuntimeException(e); }
        po.setWeekFrom(w1); po.setWeekTo(w2);
        po.setWeekRangeLabel("X");
        BuyerCode bc = new BuyerCode(); bc.setCode("ABC");
        try {
            var bcf = BuyerCode.class.getDeclaredField("id");
            bcf.setAccessible(true); bcf.set(bc, 11L);
        } catch (Exception e) { throw new RuntimeException(e); }
        PODetail d = new PODetail();
        d.setPurchaseOrder(po); d.setBuyerCode(bc);
        d.setProductId("100"); d.setGrade("A_YYY");
        d.setPrice(new BigDecimal("10")); d.setTempBuyerCode("ABC");
        po.getDetails().add(d);
        return po;
    }
}
