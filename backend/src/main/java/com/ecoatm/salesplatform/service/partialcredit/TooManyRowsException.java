package com.ecoatm.salesplatform.service.partialcredit;

/**
 * Thrown by {@link PartialCreditExcelExportService} when the filter
 * resolves to more rows than the hard cap (5,000). The admin
 * controller maps this to {@code 413 Payload Too Large} with a
 * {@code {error, limit, matched}} body so the UI can render the
 * "narrow your filters" toast inline.
 */
public class TooManyRowsException extends RuntimeException {

    private final int limit;
    private final long matched;

    public TooManyRowsException(int limit, long matched) {
        super("Export would include " + matched + " rows (limit: " + limit + ")");
        this.limit = limit;
        this.matched = matched;
    }

    public int limit() { return limit; }
    public long matched() { return matched; }
}
