package com.ecoatm.salesplatform.model.partialcredit.enums;

/**
 * The five canonical system statuses a credit request can be in. The
 * value is stored on the credit_request_statuses config table as the
 * `system_status` column; instances of {@code CreditRequest} reference
 * the config row by FK so admins can tune the display text + color
 * without changing this enum.
 */
public enum SystemStatus {
    DRAFT,
    PENDING_APPROVAL,
    UNDER_REVIEW,
    APPROVED,
    DECLINED
}
