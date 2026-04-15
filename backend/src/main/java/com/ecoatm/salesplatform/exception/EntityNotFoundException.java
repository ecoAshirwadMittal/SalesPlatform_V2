package com.ecoatm.salesplatform.exception;

/**
 * Thrown when an entity lookup fails. Mapped to HTTP 404 by
 * {@link GlobalExceptionHandler}. Prefer this over generic RuntimeException
 * so the handler can route by type instead of scanning the message.
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entity, Object id) {
        super(entity + " not found: id=" + id);
    }
}
