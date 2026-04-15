package com.ecoatm.salesplatform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.model.pws.OfferItem;
import com.ecoatm.salesplatform.repository.EcoATMDirectUserRepository;
import com.ecoatm.salesplatform.repository.mdm.DeviceRepository;
import com.ecoatm.salesplatform.service.email.EmailMessage;
import com.ecoatm.salesplatform.service.email.EmailSender;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;

/**
 * Unit tests for {@link PWSEmailService} — verifies recipient resolution,
 * subject lines, template selection, CC rules, and failure swallowing
 * against the Mendix microflow specs in {@code migration_context/backend/services}.
 */
class PWSEmailServiceTest {

    @Mock private TemplateEngine templateEngine;
    @Mock private EmailSender emailSender;
    @Mock private EcoATMDirectUserRepository directUserRepository;
    @Mock private DeviceRepository deviceRepository;

    private PWSEmailService service;

    private static final String SALES = "sales@ecoatmdirect.com";
    private static final String CO_URL = "https://buy.ecoatmdirect.com/p/counter-offer/";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(templateEngine.process(anyString(), any(IContext.class))).thenReturn("<html>rendered</html>");
        when(deviceRepository.findAllById(any())).thenReturn(Collections.emptyList());
        service = new PWSEmailService(
                templateEngine, emailSender, directUserRepository, deviceRepository, SALES, CO_URL);
    }

    private Offer newOffer(long id, long buyerCodeId) {
        Offer offer = new Offer();
        offer.setId(id);
        offer.setOfferNumber("OFR-" + id);
        offer.setBuyerCodeId(buyerCodeId);
        offer.setTotalPrice(new BigDecimal("100.00"));
        offer.setItems(List.of(newItem()));
        return offer;
    }

    private OfferItem newItem() {
        OfferItem i = new OfferItem();
        i.setSku("SKU-1");
        i.setQuantity(2);
        i.setPrice(new BigDecimal("50.00"));
        i.setTotalPrice(new BigDecimal("100.00"));
        return i;
    }

    // ── offer confirmation ──────────────────────────────────────────────────

    @Test
    @DisplayName("offer confirmation sends to submitting user only")
    void offerConfirmation_toSubmittingUser() {
        when(directUserRepository.findEmailByUserId(7L))
                .thenReturn(List.<Object[]>of(new Object[] {"alice@buyer.com", "Alice"}));
        when(directUserRepository.findBuyerCompanyNameByBuyerCodeId(10L))
                .thenReturn(List.of("Acme"));

        service.sendOfferConfirmationEmail(newOffer(42L, 10L), 7L);

        ArgumentCaptor<EmailMessage> cap = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailSender).send(cap.capture());
        EmailMessage msg = cap.getValue();
        assertThat(msg.to()).containsExactly("alice@buyer.com");
        assertThat(msg.cc()).isEmpty();
        assertThat(msg.subject()).isEqualTo("Offer Confirmation — OFR-42");
        verify(templateEngine).process(eq("email/pws-offer-confirmation"), any(IContext.class));
    }

    @Test
    @DisplayName("offer confirmation skips when user lookup empty")
    void offerConfirmation_skipsWhenNoUser() {
        when(directUserRepository.findEmailByUserId(7L)).thenReturn(List.of());
        service.sendOfferConfirmationEmail(newOffer(42L, 10L), 7L);
        verify(emailSender, never()).send(any());
    }

    // ── order confirmation ──────────────────────────────────────────────────

    @Test
    @DisplayName("order confirmation fans out to all buyer users, no CC")
    void orderConfirmation_allBuyerUsers() {
        when(directUserRepository.findActiveEmailsByBuyerCodeId(10L))
                .thenReturn(List.<Object[]>of(
                        new Object[] {"a@b.com", "A"},
                        new Object[] {"b@b.com", "B"}));
        when(directUserRepository.findBuyerCompanyNameByBuyerCodeId(10L))
                .thenReturn(List.of("Acme"));

        service.sendOrderConfirmationEmail(newOffer(42L, 10L));

        ArgumentCaptor<EmailMessage> cap = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailSender).send(cap.capture());
        assertThat(cap.getValue().to()).containsExactly("a@b.com", "b@b.com");
        assertThat(cap.getValue().cc()).isEmpty();
        assertThat(cap.getValue().subject()).isEqualTo("Order Confirmation — OFR-42");
        verify(templateEngine).process(eq("email/pws-order-confirmation"), any(IContext.class));
    }

    // ── pending order ───────────────────────────────────────────────────────

    @Test
    @DisplayName("pending order CCs the sales address")
    void pendingOrder_ccsSales() {
        when(directUserRepository.findActiveEmailsByBuyerCodeId(10L))
                .thenReturn(List.<Object[]>of(new Object[] {"a@b.com", "A"}));
        when(directUserRepository.findBuyerCompanyNameByBuyerCodeId(10L))
                .thenReturn(List.of("Acme"));

        service.sendPendingOrderEmail(newOffer(42L, 10L));

        ArgumentCaptor<EmailMessage> cap = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailSender).send(cap.capture());
        assertThat(cap.getValue().to()).containsExactly("a@b.com");
        assertThat(cap.getValue().cc()).containsExactly(SALES);
        assertThat(cap.getValue().subject()).isEqualTo("Order Pending — OFR-42");
        verify(templateEngine).process(eq("email/pws-pending-order"), any(IContext.class));
    }

    // ── counter offer ───────────────────────────────────────────────────────

    @Test
    @DisplayName("counter offer uses counter-offer template and fans out to buyers")
    void counterOffer_rendersCounterTemplate() {
        when(directUserRepository.findActiveEmailsByBuyerCodeId(10L))
                .thenReturn(List.<Object[]>of(new Object[] {"a@b.com", "A"}));
        when(directUserRepository.findBuyerCompanyNameByBuyerCodeId(10L))
                .thenReturn(List.of("Acme"));

        Offer offer = newOffer(42L, 10L);
        offer.getItems().get(0).setItemStatus("Counter");
        offer.getItems().get(0).setCounterQty(3);
        offer.getItems().get(0).setCounterPrice(new BigDecimal("40.00"));
        offer.getItems().get(0).setCounterTotal(new BigDecimal("120.00"));

        service.sendCounterOfferEmail(offer);

        ArgumentCaptor<EmailMessage> cap = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailSender).send(cap.capture());
        assertThat(cap.getValue().subject()).isEqualTo("Counter Offer — OFR-42");
        verify(templateEngine).process(eq("email/pws-counter-offer"), any(IContext.class));
    }

    // ── failure swallowing ──────────────────────────────────────────────────

    @Test
    @DisplayName("sender exception is swallowed, not rethrown")
    void senderException_swallowed() {
        when(directUserRepository.findEmailByUserId(7L))
                .thenReturn(List.<Object[]>of(new Object[] {"alice@buyer.com", "Alice"}));
        when(directUserRepository.findBuyerCompanyNameByBuyerCodeId(10L))
                .thenReturn(List.of("Acme"));
        doThrow(new RuntimeException("smtp down")).when(emailSender).send(any());

        // must not throw
        service.sendOfferConfirmationEmail(newOffer(42L, 10L), 7L);
    }
}
