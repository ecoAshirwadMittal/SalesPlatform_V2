package com.ecoatm.salesplatform.dto;

import java.util.List;

/**
 * Result envelope returned by
 * {@code POST /api/v1/bidder/bid-rounds/{id}/import}.
 *
 * @param updated  Number of {@code bid_data} rows successfully updated.
 * @param errors   Per-row validation or ownership failures. Empty when
 *                 every data row is valid.
 */
public record BidImportResult(int updated, List<BidImportRowError> errors) {}
