package com.ecoatm.salesplatform.service.partialcredit;

import com.ecoatm.salesplatform.service.partialcredit.snowflake.CreditRequestSnowflakeReader.ManifestLine;
import java.util.List;

/**
 * Output of {@code CreditRequestValidator.reconcileBarcodes(...)} — the
 * three buckets a paste/upload batch splits into, plus the banner copy that
 * the wizard surfaces verbatim ("Removed N duplicate and M not in order").
 *
 * @param validLines    one entry per accepted (deduped + in-order) barcode,
 *                      carrying the manifest fields the wizard denormalises
 *                      onto each line entity at submit time
 * @param duplicates    barcodes the buyer entered more than once in this
 *                      batch — kept for transparency; not persisted
 * @param notInOrder    barcodes that are not on the manifest for the order
 * @param banner        prerendered "Removed N duplicate and M not in order"
 *                      string, empty when both counters are zero
 */
public record BarcodeReconciliationResult(
        List<ManifestLine> validLines,
        List<String> duplicates,
        List<String> notInOrder,
        String banner) {

    public boolean hasNonValid() {
        return !duplicates.isEmpty() || !notInOrder.isEmpty();
    }

    public int duplicateCount() {
        return duplicates.size();
    }

    public int notInOrderCount() {
        return notInOrder.size();
    }
}
