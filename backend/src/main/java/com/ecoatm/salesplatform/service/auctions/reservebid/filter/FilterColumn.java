package com.ecoatm.salesplatform.service.auctions.reservebid.filter;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * Whitelisted reserve-bid columns admissible in {@code FilterSpec}. Column
 * names are SQL identifiers (the underlying repository uses native SQL).
 * Each column declares its {@link Kind}, which constrains the legal
 * {@link FilterOp}s — e.g. CONTAINS is not legal on a numeric column.
 */
public enum FilterColumn {
    // RBL-D2: product_id is BIGINT (V94), matching the Mendix legacy INTEGER
    // productid. Kind.NUMERIC gives numeric EQ/NEQ and true numeric </> ordering
    // (73 < 100, not lexicographic '73' > '100'). NUMERIC is also required for
    // correctness now the column is BIGINT: the TEXT branch of the dynamic-WHERE
    // builder wraps the column in LOWER(...), which errors on a bigint column.
    PRODUCT_ID("product_id", Kind.NUMERIC),
    GRADE("grade", Kind.TEXT),
    BRAND("brand", Kind.TEXT),
    MODEL("model", Kind.TEXT),
    BID("bid", Kind.NUMERIC),
    LAST_UPDATE_DATETIME("last_update_datetime", Kind.DATE);

    public enum Kind { NUMERIC, TEXT, DATE }

    private static final Set<FilterOp> NUMERIC_OPS = EnumSet.of(
            FilterOp.EQ, FilterOp.NEQ, FilterOp.GT, FilterOp.GTE,
            FilterOp.LT, FilterOp.LTE, FilterOp.EMPTY, FilterOp.NOT_EMPTY);
    private static final Set<FilterOp> TEXT_OPS = EnumSet.of(
            FilterOp.EQ, FilterOp.NEQ, FilterOp.GT, FilterOp.GTE,
            FilterOp.LT, FilterOp.LTE, FilterOp.EMPTY, FilterOp.NOT_EMPTY,
            FilterOp.CONTAINS, FilterOp.STARTS_WITH, FilterOp.ENDS_WITH);
    private static final Set<FilterOp> DATE_OPS = EnumSet.of(
            FilterOp.EQ, FilterOp.NEQ, FilterOp.GT, FilterOp.GTE,
            FilterOp.LT, FilterOp.LTE, FilterOp.EMPTY, FilterOp.NOT_EMPTY);

    private final String sqlName;
    private final Kind kind;

    FilterColumn(String sqlName, Kind kind) {
        this.sqlName = sqlName;
        this.kind = kind;
    }

    public String sqlName() { return sqlName; }
    public Kind kind() { return kind; }

    public Set<FilterOp> legalOps() {
        return switch (kind) {
            case NUMERIC -> NUMERIC_OPS;
            case TEXT    -> TEXT_OPS;
            case DATE    -> DATE_OPS;
        };
    }

    public boolean accepts(FilterOp op) {
        return legalOps().contains(op);
    }

    /** Parses by either SQL column name (e.g. {@code product_id}) or
     *  camelCase frontend alias (e.g. {@code productId}). */
    public static Optional<FilterColumn> parse(String name) {
        if (name == null) return Optional.empty();
        String n = name.trim();
        for (FilterColumn c : values()) {
            if (c.sqlName.equalsIgnoreCase(n) || toCamel(c.sqlName).equalsIgnoreCase(n)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }

    private static String toCamel(String snake) {
        StringBuilder sb = new StringBuilder();
        boolean upperNext = false;
        for (int i = 0; i < snake.length(); i++) {
            char ch = snake.charAt(i);
            if (ch == '_') { upperNext = true; continue; }
            sb.append(upperNext ? Character.toUpperCase(ch) : ch);
            upperNext = false;
        }
        return sb.toString();
    }
}
