package com.ecoatm.salesplatform.service.auctions.reservebid;

import com.ecoatm.salesplatform.dto.ReserveBidRequest;
import com.ecoatm.salesplatform.dto.ReserveBidRow;
import com.ecoatm.salesplatform.event.ReserveBidChangedEvent;
import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import com.ecoatm.salesplatform.repository.auctions.ReserveBidAuditRepository;
import com.ecoatm.salesplatform.repository.auctions.ReserveBidRepository;
import com.ecoatm.salesplatform.repository.auctions.ReserveBidSyncRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReserveBidServiceTest {

    @Mock ReserveBidRepository repo;
    @Mock ReserveBidAuditRepository auditRepo;
    @Mock ReserveBidSyncRepository syncRepo;
    @Mock ApplicationEventPublisher publisher;

    ReserveBidService service;

    @BeforeEach
    void setUp() {
        service = new ReserveBidService(repo, auditRepo, syncRepo, publisher, null, null);
    }

    @Test
    void create_persistsRowAndPublishesEvent() {
        when(repo.existsByProductIdAndGrade("77101", "A_YYY")).thenReturn(false);
        when(repo.save(any(ReserveBid.class))).thenAnswer(inv -> {
            ReserveBid rb = inv.getArgument(0);
            rb.setId(42L);
            return rb;
        });

        ReserveBidRow created = service.create(99L,
                new ReserveBidRequest("77101", "A_YYY", "Apple", "iPhone",
                        new BigDecimal("10"), null, null, null));

        assertThat(created.id()).isEqualTo(42L);
        verify(auditRepo, never()).save(any());   // no audit on CREATE

        ArgumentCaptor<ReserveBidChangedEvent> evt = ArgumentCaptor.forClass(ReserveBidChangedEvent.class);
        verify(publisher).publishEvent(evt.capture());
        assertThat(evt.getValue().action()).isEqualTo(ReserveBidChangedEvent.Action.UPSERT);
        assertThat(evt.getValue().changedIds()).containsExactly(42L);
    }

    @Test
    void create_rejectsDuplicateProductGrade() {
        when(repo.existsByProductIdAndGrade("77102", "A_YYY")).thenReturn(true);

        assertThatThrownBy(() -> service.create(99L,
                new ReserveBidRequest("77102", "A_YYY", null, null, BigDecimal.ONE, null, null, null)))
                .isInstanceOf(ReserveBidException.class)
                .hasFieldOrPropertyWithValue("code", "DUPLICATE_PRODUCT_GRADE");
    }

    @Test
    void update_writesAuditOnPriceChange() {
        ReserveBid existing = new ReserveBid();
        existing.setId(5L);
        existing.setProductId("77103");
        existing.setGrade("A_YYY");
        existing.setBid(new BigDecimal("10.00"));
        when(repo.findById(5L)).thenReturn(Optional.of(existing));
        when(repo.save(any(ReserveBid.class))).thenReturn(existing);

        service.update(99L, 5L,
                new ReserveBidRequest("77103", "A_YYY", null, null,
                        new BigDecimal("12.00"), null, null, null));

        verify(auditRepo).save(argThat(a ->
                a.getOldPrice().compareTo(new BigDecimal("10.00")) == 0 &&
                a.getNewPrice().compareTo(new BigDecimal("12.00")) == 0));
    }

    @Test
    void update_skipsAuditWhenPriceUnchanged() {
        ReserveBid existing = new ReserveBid();
        existing.setId(6L);
        existing.setProductId("77104");
        existing.setGrade("A_YYY");
        existing.setBid(new BigDecimal("10.00"));
        when(repo.findById(6L)).thenReturn(Optional.of(existing));
        when(repo.save(any(ReserveBid.class))).thenReturn(existing);

        service.update(99L, 6L,
                new ReserveBidRequest("77104", "A_YYY", "NewBrand", null,
                        new BigDecimal("10.00"), null, null, null));

        verify(auditRepo, never()).save(any());
    }

    @Test
    void delete_publishesDeleteEvent() {
        ReserveBid existing = new ReserveBid();
        existing.setId(7L);
        when(repo.findById(7L)).thenReturn(Optional.of(existing));

        service.delete(7L);

        verify(repo).delete(existing);
        ArgumentCaptor<ReserveBidChangedEvent> evt = ArgumentCaptor.forClass(ReserveBidChangedEvent.class);
        verify(publisher).publishEvent(evt.capture());
        assertThat(evt.getValue().action()).isEqualTo(ReserveBidChangedEvent.Action.DELETE);
        assertThat(evt.getValue().changedIds()).containsExactly(7L);
    }

    @Test
    void delete_throwsOnMissing() {
        when(repo.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(ReserveBidException.class)
                .hasFieldOrPropertyWithValue("code", "RESERVE_BID_NOT_FOUND");
    }
}
