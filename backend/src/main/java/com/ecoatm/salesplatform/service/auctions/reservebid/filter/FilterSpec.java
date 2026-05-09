package com.ecoatm.salesplatform.service.auctions.reservebid.filter;

import com.ecoatm.salesplatform.service.auctions.reservebid.ReserveBidValidationException;

/**
 * One column-bound predicate for the dynamic filter builder.
 *
 * The {@code value} field is interpreted by the SQL builder according to
 * the column's kind: numeric → BigDecimal, date → YYYY-MM-DD parsed as
 * Instant at start of day UTC, text → string. Coercion happens at SQL
 * bind time so an invalid value surfaces as a {@link ReserveBidValidationException}
 * rather than a 500 from Postgres.
 *
 * For valueless ops ({@code EMPTY} / {@code NOT_EMPTY}), {@code value} is
 * stored as null and ignored.
 */
public record FilterSpec(FilterColumn column, FilterOp op, String value) {

    public FilterSpec {
        if (column == null) {
            throw new ReserveBidValidationException("FILTER_INVALID_COLUMN",
                    "filter column is required");
        }
        if (op == null) {
            throw new ReserveBidValidationException("FILTER_INVALID_OP",
                    "filter op is required");
        }
        if (!column.accepts(op)) {
            throw new ReserveBidValidationException("FILTER_OP_KIND_MISMATCH",
                    "op '" + op.token() + "' not legal on " + column.kind() +
                    " column '" + column.sqlName() + "'");
        }
        if (op.valueless()) {
            value = null;
        } else if (value == null || value.isBlank()) {
            // Caller built a non-valueless spec with no value — treat as no-op
            // by promoting to a valueless op? No — it's almost certainly a
            // bug, fail loud.
            throw new ReserveBidValidationException("FILTER_MISSING_VALUE",
                    "op '" + op.token() + "' requires a value on column '" +
                    column.sqlName() + "'");
        }
    }
}
