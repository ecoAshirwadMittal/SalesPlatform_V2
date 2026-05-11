package com.ecoatm.salesplatform.model.partialcredit.enums;

/**
 * Encumbrance check outcome. Phase 1 reviewer enters this manually;
 * Phase 2 will be driven by an automated Prolog integration.
 */
public enum PrologResult {
    ENCUMBERED,
    NOT_ENCUMBERED,
    PENDING
}
