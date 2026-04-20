package com.ecoatm.salesplatform.exception;

/**
 * Thrown when a new auction title collides (case-insensitive) with an
 * existing auction. Mapped to HTTP 409 by {@link GlobalExceptionHandler}.
 */
public class DuplicateAuctionTitleException extends RuntimeException {

    public DuplicateAuctionTitleException(String title) {
        super("An auction with this name already exists: " + title);
    }
}
