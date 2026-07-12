package com.ecoatm.salesplatform.event.rma;

/**
 * Terminal outcome of an RMA review, carried by {@link RmaReviewCompletedEvent}.
 *
 * <p>Mirrors the two branches of the legacy {@code ACT_RMADetails_CompleteReview}
 * microflow: an RMA whose items are all/partly approved finalises as
 * {@link #APPROVED}; one whose items are all declined finalises as
 * {@link #DECLINED}. A type-safe enum (rather than the raw {@code "Approved"} /
 * {@code "Declined"} status strings the {@code rma} table stores) gives the
 * downstream {@code @TransactionalEventListener}s a firm contract to gate on —
 * the Oracle-create listener acts only on {@link #APPROVED}.
 */
public enum RmaReviewOutcome {
    APPROVED,
    DECLINED
}
