package com.ecoatm.salesplatform.service.partialcredit;

import org.springframework.http.HttpStatus;

/**
 * Typed failure mode for {@link CreditRequestPhotoService} so the
 * controller can map each cause to the right HTTP status without a
 * sprawl of bespoke exception classes:
 *
 * <ul>
 *   <li>{@link Reason#TOO_LARGE} → 413 Payload Too Large</li>
 *   <li>{@link Reason#UNSUPPORTED_TYPE} → 415 Unsupported Media Type</li>
 *   <li>{@link Reason#TOO_MANY_PER_LINE} → 409 Conflict</li>
 *   <li>{@link Reason#REQUEST_FINALIZED} → 409 Conflict</li>
 * </ul>
 *
 * <p>Each constant carries its own {@link HttpStatus} so callers don't
 * need a switch — the exception handler in the controller maps the
 * status directly.
 */
public class PhotoUploadException extends RuntimeException {

    public enum Reason {
        TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE),
        UNSUPPORTED_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE),
        TOO_MANY_PER_LINE(HttpStatus.CONFLICT),
        REQUEST_FINALIZED(HttpStatus.CONFLICT);

        private final HttpStatus status;

        Reason(HttpStatus status) {
            this.status = status;
        }

        public HttpStatus status() {
            return status;
        }
    }

    private final Reason reason;

    public PhotoUploadException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public Reason reason() {
        return reason;
    }
}
