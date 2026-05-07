package com.ecoatm.salesplatform.exception;

/**
 * Thrown by the R3 admin controller when a lifecycle pre-condition fails —
 * e.g. the SA is not round 3, or the predecessor {@code r3_preprocess_status}
 * is not SUCCESS. Mapped to HTTP 422 (Unprocessable Entity) by
 * {@link GlobalExceptionHandler}.
 *
 * <p>This wraps the raw {@code IllegalArgumentException} /
 * {@code IllegalStateException} thrown by {@code R3PreProcessService} and
 * {@code R3InitService} so the admin surface returns 422 while other call
 * sites that throw those standard exceptions continue to return 400/409
 * as before.
 */
public class R3LifecycleValidationException extends RuntimeException {

    public R3LifecycleValidationException(String message) {
        super(message);
    }

    public R3LifecycleValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
