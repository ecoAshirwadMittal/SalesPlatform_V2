package com.ecoatm.salesplatform.service.auctions.reservebid.filter;

import java.util.Optional;

/**
 * Comparator operations supported by the reserve-bid filter row. Mirrors QA
 * Mendix DataGrid 2's eight numeric ops + three text-only ops.
 *
 * Each op is parsed from a frontend-supplied wire token (D1: wire format
 * "column=op,value"). Whitelisting both column and op prevents SQL injection
 * through the dynamic WHERE-clause builder in {@code ReserveBidRepositoryImpl}.
 */
public enum FilterOp {
    EQ("eq", false),
    NEQ("neq", false),
    GT("gt", false),
    GTE("gte", false),
    LT("lt", false),
    LTE("lte", false),
    EMPTY("empty", true),
    NOT_EMPTY("notEmpty", true),
    CONTAINS("contains", false),
    STARTS_WITH("startsWith", false),
    ENDS_WITH("endsWith", false);

    private final String token;
    /** True when the value side of the filter is ignored. */
    private final boolean valueless;

    FilterOp(String token, boolean valueless) {
        this.token = token;
        this.valueless = valueless;
    }

    public String token() { return token; }
    public boolean valueless() { return valueless; }

    public static Optional<FilterOp> parse(String token) {
        if (token == null) return Optional.empty();
        String t = token.trim();
        for (FilterOp op : values()) {
            if (op.token.equalsIgnoreCase(t)) return Optional.of(op);
        }
        return Optional.empty();
    }
}
