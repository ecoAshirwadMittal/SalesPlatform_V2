package com.ecoatm.salesplatform.exception;

/**
 * Thrown when a sales representative's (first name, last name) pair collides
 * (case-insensitive) with an existing rep. Ports the legacy Mendix
 * {@code Act_SaveSaleRep} duplicate-name guard. Mapped to HTTP 409 by
 * {@link GlobalExceptionHandler}.
 */
public class DuplicateSalesRepException extends RuntimeException {

    public DuplicateSalesRepException(String firstName, String lastName) {
        super("A sales representative with this name already exists: "
                + firstName + " " + lastName);
    }
}
