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

    /**
     * For lookups whose key is not an entity {@code id} — e.g. round-keyed
     * singleton tables such as {@code BidRoundSelectionFilter}, where the
     * caller passes a round number that is not the row's primary key.
     * Yields messages like {@code "BidRoundSelectionFilter not found: round=2"}
     * instead of the misleading {@code "id=2"}.
     */
    public EntityNotFoundException(String entity, String keyName, Object keyValue) {
        super(entity + " not found: " + keyName + "=" + keyValue);
    }
}
