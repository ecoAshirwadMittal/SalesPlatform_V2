package com.ecoatm.salesplatform.model.partialcredit.enums;

/**
 * Per-line ingest classification — set during wizard barcode reconciliation
 * (validator dedupes across the request and matches against the Snowflake
 * manifest). Reviewers see this on the admin grid.
 */
public enum LineRowStatus {
    VALID,
    DUPLICATE,
    NOT_IN_ORDER
}
