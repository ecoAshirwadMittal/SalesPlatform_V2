package com.ecoatm.salesplatform.exception;

/**
 * Thrown when a caller tries to create a second auction for a week that
 * already has one. Mendix invariant — mapped to HTTP 409 by
 * {@link GlobalExceptionHandler}.
 */
public class AuctionAlreadyExistsException extends RuntimeException {

    public AuctionAlreadyExistsException(Long weekId) {
        super("An auction already exists for week id=" + weekId);
    }
}
