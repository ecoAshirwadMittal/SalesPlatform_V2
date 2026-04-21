package com.ecoatm.salesplatform.exception;

import java.util.List;

/**
 * Thrown when the schedule payload fails the per-round {@code end > start}
 * validation (Mendix {@code VAL_Schedule_Auction}). Errors are accumulated
 * into a comma-separated message for the client — matching the Mendix
 * user-facing warning string format.
 *
 * <p>Mapped to HTTP 400 by {@link GlobalExceptionHandler}.
 */
public class RoundValidationException extends RuntimeException {

    private final List<String> errors;

    public RoundValidationException(List<String> errors) {
        super(String.join(", ", errors));
        this.errors = List.copyOf(errors);
    }

    public List<String> errors() {
        return errors;
    }
}
