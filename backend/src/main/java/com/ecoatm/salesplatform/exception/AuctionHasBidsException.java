package com.ecoatm.salesplatform.exception;

/**
 * Thrown when the admin tries to reschedule an auction that already has bids
 * recorded against one of its rounds. Mendix implicitly cascaded these away
 * on reschedule; we block to avoid silent data loss. Mapped to HTTP 409 by
 * {@link GlobalExceptionHandler}.
 */
public class AuctionHasBidsException extends RuntimeException {

    public AuctionHasBidsException() {
        super("Bids have already been submitted; reschedule is not available.");
    }
}
