package com.ecoatm.salesplatform.service.email;

import com.ecoatm.salesplatform.model.pws.Offer;
import com.ecoatm.salesplatform.repository.pws.OfferRepository;
import com.ecoatm.salesplatform.service.PWSEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Bridges transactional business operations to the async PWS email service.
 *
 * <p>Listens for {@link PwsOfferEmailEvent} variants after the originating
 * transaction commits — this guarantees that rollbacks never produce
 * customer-facing notifications. Each handler reloads the offer aggregate in
 * a fresh {@code REQUIRES_NEW} transaction so lazy {@code items} collections
 * can be initialized before being handed to the {@code @Async} sender, which
 * runs on a different thread and therefore has no shared persistence context.
 */
@Component
public class PwsOfferEmailListener {

    private static final Logger log = LoggerFactory.getLogger(PwsOfferEmailListener.class);

    private final OfferRepository offerRepository;
    private final PWSEmailService emailService;

    public PwsOfferEmailListener(OfferRepository offerRepository, PWSEmailService emailService) {
        this.offerRepository = offerRepository;
        this.emailService = emailService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOfferConfirmation(PwsOfferEmailEvent.OfferConfirmation event) {
        loadOffer(event.offerId())
                .ifPresent(offer -> emailService.sendOfferConfirmationEmail(offer, event.submittedByUserId()));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderConfirmation(PwsOfferEmailEvent.OrderConfirmation event) {
        loadOffer(event.offerId()).ifPresent(emailService::sendOrderConfirmationEmail);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPendingOrder(PwsOfferEmailEvent.PendingOrder event) {
        loadOffer(event.offerId()).ifPresent(emailService::sendPendingOrderEmail);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCounterOffer(PwsOfferEmailEvent.CounterOffer event) {
        loadOffer(event.offerId()).ifPresent(emailService::sendCounterOfferEmail);
    }

    private java.util.Optional<Offer> loadOffer(long offerId) {
        var offer = offerRepository.findByIdWithItems(offerId);
        if (offer.isEmpty()) {
            log.warn("PWS email event received for unknown offerId={}", offerId);
        }
        return offer;
    }
}
