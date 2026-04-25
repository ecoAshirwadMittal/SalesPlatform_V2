package com.ecoatm.salesplatform.service.admin;

/**
 * Thrown by {@link QualifiedBuyerCodeAdminService} when the PATCH endpoint
 * is called against an unknown {@code qualified_buyer_codes.id}. Mapped to
 * HTTP 404 by {@code GlobalExceptionHandler} via the existing
 * {@code RuntimeException + "not found"} fallback path.
 */
public class QualifiedBuyerCodeNotFoundException extends RuntimeException {
    public QualifiedBuyerCodeNotFoundException(long id) {
        super("Qualified buyer code not found: " + id);
    }
}
