package com.ecoatm.salesplatform.service.admin;

/**
 * Thrown by {@link QualifiedBuyerCodeAdminService#updateIncluded} when an admin
 * attempts to flip a Qualified Buyer Code's {@code included} flag while its
 * SchedulingAuction round is {@code Closed}. Mirrors the legacy Mendix
 * {@code NF_OnIncludedChanged_New} guard: a closed round is frozen, so the
 * override is rejected before any mutation (no persist, no audit row, no event).
 *
 * <p>Extends {@link IllegalStateException} so it maps to HTTP 409 CONFLICT via
 * the existing {@code GlobalExceptionHandler.handleIllegalState} handler — the
 * round-closed guard reuses the established 409 path rather than adding a new
 * handler.
 */
public class RoundClosedException extends IllegalStateException {
    public RoundClosedException() {
        super("Round cannot be modified if it is closed");
    }
}
