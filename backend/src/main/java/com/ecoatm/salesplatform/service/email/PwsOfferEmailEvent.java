package com.ecoatm.salesplatform.service.email;

/**
 * Sealed family of PWS email trigger events. Published from transactional
 * services after state changes; delivered post-commit by
 * {@link PwsOfferEmailListener} so rollbacks do not leak notifications.
 *
 * <p>Each variant carries only the primary-key handles needed to reload the
 * offer aggregate inside the listener — events must be small and serializable
 * so they can cross thread boundaries safely.
 */
public sealed interface PwsOfferEmailEvent {

    long offerId();

    /** Offer submitted for sales review — sent only to the submitting user. */
    record OfferConfirmation(long offerId, long submittedByUserId) implements PwsOfferEmailEvent {}

    /** Oracle accepted the order — fan out to buyer users. */
    record OrderConfirmation(long offerId) implements PwsOfferEmailEvent {}

    /** Oracle error / pending order — fan out + CC sales. */
    record PendingOrder(long offerId) implements PwsOfferEmailEvent {}

    /** Counter offer is ready for buyer review — fan out with CTA. */
    record CounterOffer(long offerId) implements PwsOfferEmailEvent {}
}
