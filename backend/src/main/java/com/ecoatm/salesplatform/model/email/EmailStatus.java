package com.ecoatm.salesplatform.model.email;

/**
 * Lifecycle status of an {@link EmailLog} row. Mirrors the
 * {@code email.log.status} CHECK constraint added by V92
 * ({@code CHECK (status IN ('PENDING','SENT','FAILED'))}).
 */
public enum EmailStatus {
    PENDING,
    SENT,
    FAILED
}
