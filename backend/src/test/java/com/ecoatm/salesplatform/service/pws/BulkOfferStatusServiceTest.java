package com.ecoatm.salesplatform.service.pws;

import com.ecoatm.salesplatform.dto.ChangeOfferStatusRequest;
import com.ecoatm.salesplatform.dto.ChangeOfferStatusResult;
import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.model.pws.Order;
import com.ecoatm.salesplatform.repository.pws.OfferRepository;
import com.ecoatm.salesplatform.repository.pws.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link BulkOfferStatusService} — the port of the Mendix
 * {@code ACT_ChangeOfferStatus_Proceed} apply logic. Uses the real
 * {@link ChangeOfferStatusValidator} (stateless) and mocks the repositories +
 * {@link JdbcTemplate} (the audit writer).
 */
@ExtendWith(MockitoExtension.class)
class BulkOfferStatusServiceTest {

    private static final String ACTOR = "admin@test.com";

    @Mock private OrderRepository orderRepository;
    @Mock private OfferRepository offerRepository;
    @Mock private JdbcTemplate jdbc;

    @Captor private ArgumentCaptor<List<Offer>> offersCaptor;

    private BulkOfferStatusService service() {
        return new BulkOfferStatusService(
                orderRepository, offerRepository, new ChangeOfferStatusValidator(), jdbc);
    }

    // ── status change: allPeriod (no from-status guard) ─────────────────

    @Test
    @DisplayName("allPeriod status change → EVERY linked offer changed (no from-status guard)")
    void allPeriodChangesEveryLinkedOffer() {
        Offer offer1 = offer(1L, "SALES_REVIEW");
        Offer offer2 = offer(2L, "DECLINED");     // different status — still changed under allPeriod
        when(orderRepository.findAllById(anyList()))
                .thenReturn(List.of(order(10L, offer1), order(11L, offer2)));
        when(offerRepository.findAllById(anyList())).thenReturn(List.of(offer1, offer2));

        ChangeOfferStatusRequest req = new ChangeOfferStatusRequest(
                true, null, null, null, "CANCELLED", false, false, List.of(10L, 11L));

        ChangeOfferStatusResult result = service().changeStatus(req, ACTOR);

        assertThat(result.matchedOrders()).isEqualTo(2);
        assertThat(result.changedOffers()).isEqualTo(2);
        assertThat(result.metadataOnly()).isFalse();
        assertThat(offer1.getStatus()).isEqualTo("CANCELLED");
        assertThat(offer2.getStatus()).isEqualTo("CANCELLED");
        assertThat(offer1.getChangedBy()).isEqualTo(ACTOR);
        assertThat(offer2.getChangedBy()).isEqualTo(ACTOR);

        verify(offerRepository).saveAll(offersCaptor.capture());
        assertThat(offersCaptor.getValue()).containsExactlyInAnyOrder(offer1, offer2);
    }

    // ── status change: date-range (the from-status safety guard) ────────

    @Test
    @DisplayName("date-range status change → ONLY offers matching fromOfferStatus changed (the guard)")
    void dateRangeChangesOnlyMatchingStatus() {
        Offer matching = offer(1L, "SALES_REVIEW");
        Offer other = offer(2L, "DECLINED");
        when(orderRepository.findByOrderDateWithinRange(any(), any()))
                .thenReturn(List.of(order(10L, matching), order(11L, other)));
        when(offerRepository.findAllById(anyList())).thenReturn(List.of(matching, other));

        ChangeOfferStatusRequest req = new ChangeOfferStatusRequest(
                false, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31),
                "SALES_REVIEW", "CANCELLED", false, false, List.of());

        ChangeOfferStatusResult result = service().changeStatus(req, ACTOR);

        assertThat(result.matchedOrders()).isEqualTo(2);
        assertThat(result.changedOffers()).isEqualTo(1);
        assertThat(matching.getStatus()).isEqualTo("CANCELLED");
        assertThat(other.getStatus()).isEqualTo("DECLINED");     // untouched — the guard held

        verify(offerRepository).saveAll(offersCaptor.capture());
        assertThat(offersCaptor.getValue()).containsExactly(matching);
    }

    // ── metadata-only path (no status change) ───────────────────────────

    @Test
    @DisplayName("metadata-only → no offer status change; resolved orders touched; audit written")
    void metadataOnlyTouchesOrdersNoStatusChange() {
        when(orderRepository.findByOrderDateWithinRange(any(), any()))
                .thenReturn(List.of(order(10L, offer(1L, "SALES_REVIEW")),
                                    order(11L, offer(2L, "SALES_REVIEW"))));

        ChangeOfferStatusRequest req = new ChangeOfferStatusRequest(
                false, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31),
                null, null, true, true, List.of());

        ChangeOfferStatusResult result = service().changeStatus(req, ACTOR);

        assertThat(result.matchedOrders()).isEqualTo(2);
        assertThat(result.changedOffers()).isZero();
        assertThat(result.metadataOnly()).isTrue();

        // No offer read or write on the metadata path.
        verifyNoInteractions(offerRepository);
        // The resolved orders are touched (the faithful "commit order list" analog).
        verify(jdbc).update(contains("pws.\"order\""), eq(10L), eq(11L));
        // Exactly one audit row, metadata action, JWT actor, count captured.
        verify(jdbc).update(contains("admin_audit_log"),
                eq("Order"), eq(0L), eq("BULK_METADATA_UPDATE"),
                contains("hasShipmentDetails=true"), eq(ACTOR), any(), any());
    }

    // ── audit row content (caller + count) ──────────────────────────────

    @Test
    @DisplayName("audit row is written with the JWT caller and the changed count")
    void auditRowCarriesCallerAndCount() {
        Offer offer1 = offer(1L, "SALES_REVIEW");
        Offer offer2 = offer(2L, "SALES_REVIEW");
        when(orderRepository.findAllById(anyList()))
                .thenReturn(List.of(order(10L, offer1), order(11L, offer2)));
        when(offerRepository.findAllById(anyList())).thenReturn(List.of(offer1, offer2));

        ChangeOfferStatusRequest req = new ChangeOfferStatusRequest(
                true, null, null, null, "CANCELLED", false, false, List.of(10L, 11L));

        service().changeStatus(req, ACTOR);

        verify(jdbc).update(contains("admin_audit_log"),
                eq("Offer"), eq(0L), eq("BULK_STATUS_CHANGE"),
                contains("changedOffers=2"), eq(ACTOR), any(), any());
        // The status-change path writes ONLY the audit row via JdbcTemplate —
        // no bulk order UPDATE (that is the metadata path).
        verifyNoMoreInteractions(jdbc);
    }

    // ── helpers ─────────────────────────────────────────────────────────

    private static Offer offer(long id, String status) {
        Offer o = new Offer();
        o.setId(id);
        o.setStatus(status);
        return o;
    }

    private static Order order(long id, Offer offer) {
        Order o = new Order();
        o.setId(id);
        o.setOffer(offer);
        return o;
    }
}
