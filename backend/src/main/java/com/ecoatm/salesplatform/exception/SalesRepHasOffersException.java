package com.ecoatm.salesplatform.exception;

/**
 * Thrown when a delete is attempted on a sales representative that still has
 * one or more associated {@code pws.offer} rows. Ports the legacy Mendix
 * {@code ACT_DeleteSalesRep} referential guard. Mapped to HTTP 409 by
 * {@link GlobalExceptionHandler}.
 */
public class SalesRepHasOffersException extends RuntimeException {

    public SalesRepHasOffersException() {
        super("This Sales Rep has Offers Associated with it. Cannot be Deleted.");
    }
}
