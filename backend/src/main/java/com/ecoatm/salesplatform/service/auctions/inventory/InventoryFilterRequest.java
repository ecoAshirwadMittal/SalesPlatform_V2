package com.ecoatm.salesplatform.service.auctions.inventory;

import com.ecoatm.salesplatform.service.auctions.reservebid.filter.FilterOp;

/**
 * One column-bound filter request for the inventory search endpoint.
 *
 * The controller accepts each text column's value in either of two
 * wire shapes:
 *
 * <ul>
 *   <li><b>New shape (preferred):</b> {@code grades=contains,A_YYY},
 *       {@code grades=startsWith,A_}, {@code brand=empty}. The op token
 *       prefix matches {@link FilterOp#token()}; the value follows the
 *       comma. Valueless ops omit the value.</li>
 *
 *   <li><b>Legacy shape:</b> bare value (e.g. {@code grades=A_YYY})
 *       coupled with a sibling {@code gradesMode=contains|equals}
 *       param. Maintained for one release of backward compatibility
 *       with callers that pre-date the comparator dropdown.</li>
 * </ul>
 *
 * Disambiguation: a value is treated as new-shape iff its prefix up to
 * the first comma parses as a {@link FilterOp} token. Anything else is
 * legacy.
 *
 * @param op    The op to apply. Null means "no filter on this column".
 * @param value The value side. Null when {@link FilterOp#valueless()}
 *              or when {@code op} is null.
 */
public record InventoryFilterRequest(FilterOp op, String value) {

    private static final InventoryFilterRequest NONE = new InventoryFilterRequest(null, null);

    public static InventoryFilterRequest none() { return NONE; }

    public boolean active() { return op != null; }

    /**
     * Parse a single column's filter from the new + legacy wire shapes.
     *
     * @param raw         The query-param value as the controller received
     *                    it. Null / blank means no filter on this column.
     * @param legacyMode  The sibling {@code xxxMode} query param, when
     *                    {@code raw} is in the legacy bare-value shape.
     *                    Accepts {@code "contains"} (default) or
     *                    {@code "equals"}.
     */
    public static InventoryFilterRequest parse(String raw, String legacyMode) {
        if (raw == null || raw.isBlank()) return NONE;

        int comma = raw.indexOf(',');
        if (comma > 0) {
            String maybeOpToken = raw.substring(0, comma);
            FilterOp op = FilterOp.parse(maybeOpToken).orElse(null);
            if (op != null) {
                String value = op.valueless() ? null : raw.substring(comma + 1);
                if (!op.valueless() && (value == null || value.isEmpty())) {
                    return NONE;
                }
                return new InventoryFilterRequest(op, value);
            }
        }
        // Bare valueless ops (e.g. "empty"): the prefix-only shape with
        // no comma. Same parse path as the new shape minus the value.
        FilterOp bareOp = FilterOp.parse(raw).orElse(null);
        if (bareOp != null && bareOp.valueless()) {
            return new InventoryFilterRequest(bareOp, null);
        }

        // Legacy bare value with sibling mode param.
        FilterOp op = "equals".equalsIgnoreCase(legacyMode) ? FilterOp.EQ : FilterOp.CONTAINS;
        return new InventoryFilterRequest(op, raw);
    }
}
