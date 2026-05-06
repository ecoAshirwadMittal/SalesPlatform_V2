package com.ecoatm.salesplatform.event;

/**
 * Published by
 * {@link com.ecoatm.salesplatform.service.auctions.r2init.R2BuyerAssignmentService}
 * after a successful R2-init run (post {@code markSuccess}, inside the
 * {@code REQUIRES_NEW} transaction so listeners observing
 * {@code AFTER_COMMIT} only fire once the QBC + bid_data writes are durable).
 *
 * <p>Carries the {@code weekId} so downstream Snowflake-push listeners do
 * not need to re-query {@code auctions} for the parent week — sub-project 5
 * does not currently push, but the field is retained for parity with the
 * 4C event shape.
 */
public record R2BuyerAssignmentCompletedEvent(
    long schedulingAuctionId,
    long auctionId,
    Long weekId,
    int qualifiedCount,
    int specialTreatmentCount
) {}
