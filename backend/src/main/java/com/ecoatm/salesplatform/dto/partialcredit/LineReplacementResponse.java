package com.ecoatm.salesplatform.dto.partialcredit;

import com.ecoatm.salesplatform.service.partialcredit.BarcodeReconciliationResult;

/**
 * Response shape for the three {@code POST .../{id}/{missing|wrong|encumbered}-lines}
 * endpoints. Carries the up-to-date {@link CreditRequestDetail} plus the
 * {@link BarcodeReconciliationResult} so the wizard can render the
 * Figma "Removed N duplicate and M not in order" banner verbatim above
 * the textarea without a second request.
 *
 * <p>The reconciliation block ships with the response (instead of being
 * cached client-side after the fact) because the banner must reflect the
 * server's view of the manifest, not the wizard's local guess. When the
 * manifest is empty (LOGGING reader / unknown order), the banner string
 * is empty and the dropped buckets are zero-length lists.
 */
public record LineReplacementResponse(
        CreditRequestDetail detail,
        BarcodeReconciliationResult reconciliation) {}
