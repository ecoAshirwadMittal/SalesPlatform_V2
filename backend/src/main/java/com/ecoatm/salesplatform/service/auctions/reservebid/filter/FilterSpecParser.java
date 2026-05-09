package com.ecoatm.salesplatform.service.auctions.reservebid.filter;

import com.ecoatm.salesplatform.service.auctions.reservebid.ReserveBidValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Translates query-string parameters into a list of {@link FilterSpec}s.
 *
 * Two wire formats are accepted (D6 — backwards compat for one release):
 *
 * <ol>
 *   <li><b>New shape (preferred):</b> {@code column=op,value} — e.g.
 *       {@code productId=eq,73} or {@code grade=contains,A_YYY}. Valueless
 *       ops omit the value: {@code brand=empty}.</li>
 *
 *   <li><b>Legacy shape:</b> bare values (e.g. {@code productId=73},
 *       {@code grade=A_YYY}, {@code minBid=100}, {@code maxBid=200},
 *       {@code updatedSince=2025-01-01}) interpreted with column-specific
 *       defaults: numeric → EQ, text → CONTAINS, minBid → GTE on bid,
 *       maxBid → LTE on bid, updatedSince → GTE on last_update_datetime.</li>
 * </ol>
 *
 * Disambiguation: a value is treated as new shape iff its prefix up to the
 * first comma parses as a {@link FilterOp}. Anything else is legacy.
 */
public final class FilterSpecParser {

    private FilterSpecParser() {}

    public static List<FilterSpec> parse(Map<String, String> params) {
        List<FilterSpec> out = new ArrayList<>();
        for (Map.Entry<String, String> e : params.entrySet()) {
            String key = e.getKey();
            String raw = e.getValue();
            if (raw == null || raw.isBlank()) continue;

            // Legacy bid-range shape — translate to two specs on `bid`.
            if ("minBid".equals(key)) {
                out.add(new FilterSpec(FilterColumn.BID, FilterOp.GTE, raw.trim()));
                continue;
            }
            if ("maxBid".equals(key)) {
                out.add(new FilterSpec(FilterColumn.BID, FilterOp.LTE, raw.trim()));
                continue;
            }
            if ("updatedSince".equals(key)) {
                out.add(new FilterSpec(FilterColumn.LAST_UPDATE_DATETIME, FilterOp.GTE, raw.trim()));
                continue;
            }

            // Skip non-filter params the controller layer pulls separately.
            if ("page".equals(key) || "size".equals(key) || "sort".equals(key)) continue;

            // Column-keyed param. Reject unknown keys silently (so unrelated
            // request parameters like pagination/auth do not poison the
            // filter list, and so the spec doesn't have to enumerate every
            // controller-level param).
            var maybeCol = FilterColumn.parse(key);
            if (maybeCol.isEmpty()) continue;
            FilterColumn col = maybeCol.get();

            // Detect new shape by trying to parse the prefix up to the first
            // comma as an op token.
            int commaIdx = raw.indexOf(',');
            String maybeOpToken = commaIdx < 0 ? raw.trim() : raw.substring(0, commaIdx).trim();
            var maybeOp = FilterOp.parse(maybeOpToken);

            if (maybeOp.isPresent()) {
                FilterOp op = maybeOp.get();
                String value = commaIdx < 0 ? null : raw.substring(commaIdx + 1);
                if (op.valueless()) {
                    out.add(new FilterSpec(col, op, null));
                } else {
                    if (value == null) {
                        throw new ReserveBidValidationException("FILTER_MISSING_VALUE",
                                "op '" + op.token() + "' requires a value on column '" +
                                col.sqlName() + "'");
                    }
                    out.add(new FilterSpec(col, op, value));
                }
            } else {
                // Legacy bare-value shape: pick a default op by column kind.
                FilterOp defaultOp = switch (col.kind()) {
                    case NUMERIC -> FilterOp.EQ;
                    case TEXT    -> FilterOp.CONTAINS;
                    case DATE    -> FilterOp.GTE;
                };
                out.add(new FilterSpec(col, defaultOp, raw.trim()));
            }
        }
        return out;
    }
}
