package com.ecoatm.salesplatform.dto.partialcredit;

import java.util.List;

/**
 * Per-reason "replace all my lines" payload. The wizard sends every
 * accepted-and-deduped barcode the buyer entered for that reason in a
 * single call; the server clears the existing lines for the
 * (request, reason) pair and writes a fresh set. Idempotent.
 *
 * <p>For Wrong Device the {@code wrongLines} field carries
 * (expectedBarcode, actualImeiOrModel) tuples; for Missing /
 * Encumbered we just use the {@code barcodes} field.
 */
public record SetLinesRequest(
        List<String> barcodes,
        List<WrongLineInput> wrongLines) {

    public record WrongLineInput(String expectedBarcode, String actualImeiOrModel) {}
}
