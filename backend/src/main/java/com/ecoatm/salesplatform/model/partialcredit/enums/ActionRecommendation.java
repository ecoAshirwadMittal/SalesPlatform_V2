package com.ecoatm.salesplatform.model.partialcredit.enums;

/**
 * Recommendation engine output for Wrong-Device lines (SPKB-3661). Binary
 * Accept / Decline per the 2026-05-11 decision (no MANUAL_REVIEW state).
 */
public enum ActionRecommendation {
    ACCEPT,
    DECLINE
}
