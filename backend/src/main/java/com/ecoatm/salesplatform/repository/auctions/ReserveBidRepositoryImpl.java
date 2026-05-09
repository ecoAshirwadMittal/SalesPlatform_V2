package com.ecoatm.salesplatform.repository.auctions;

import com.ecoatm.salesplatform.model.auctions.ReserveBid;
import com.ecoatm.salesplatform.service.auctions.reservebid.ReserveBidValidationException;
import com.ecoatm.salesplatform.service.auctions.reservebid.filter.FilterColumn;
import com.ecoatm.salesplatform.service.auctions.reservebid.filter.FilterOp;
import com.ecoatm.salesplatform.service.auctions.reservebid.filter.FilterSpec;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic-WHERE implementation of {@link ReserveBidRepositoryCustom}.
 *
 * Builds two parallel SQL strings (data + count) by iterating the
 * {@link FilterSpec} list and emitting one {@code AND}-joined fragment per
 * spec. Values are passed via positional parameters and wrapped in
 * {@code CAST(?N AS <type>)} so Postgres's parameter-metadata describe
 * step has a concrete type — without the cast, Postgres defaults bind
 * placeholders to {@code bytea} and crashes with
 * {@code "function lower(bytea) does not exist"}. Same fix the static
 * native query in {@code ReserveBidRepository.search()} used before this
 * dynamic-WHERE refactor (see commit e70deeb).
 *
 * Sort: when {@code Pageable.getSort()} is non-empty, the SQL gains an
 * {@code ORDER BY} clause. Sort property names are validated against the
 * {@link FilterColumn} whitelist before concatenation, blocking SQL
 * injection through the sort parameter.
 */
public class ReserveBidRepositoryImpl implements ReserveBidRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<ReserveBid> searchDynamic(List<FilterSpec> filters, Pageable pageable) {
        StringBuilder where = new StringBuilder();
        List<Object> params = new ArrayList<>();
        for (FilterSpec spec : filters) {
            String fragment = renderFragment(spec, params);
            if (where.length() > 0) where.append(" AND ");
            where.append(fragment);
        }

        String orderBy = renderOrderBy(pageable.getSort());

        String dataSql = "SELECT rb.* FROM auctions.reserve_bid rb"
                + (where.length() > 0 ? " WHERE " + where : "")
                + (orderBy.isEmpty() ? "" : " " + orderBy);
        String countSql = "SELECT COUNT(*) FROM auctions.reserve_bid rb"
                + (where.length() > 0 ? " WHERE " + where : "");

        Query data = em.createNativeQuery(dataSql, ReserveBid.class);
        Query count = em.createNativeQuery(countSql);
        for (int i = 0; i < params.size(); i++) {
            // setParameter is 1-indexed for positional binds
            data.setParameter(i + 1, params.get(i));
            count.setParameter(i + 1, params.get(i));
        }
        data.setFirstResult((int) pageable.getOffset());
        data.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<ReserveBid> content = data.getResultList();
        long total = ((Number) count.getSingleResult()).longValue();
        return new PageImpl<>(content, pageable, total);
    }

    /**
     * Render one filter spec to a SQL fragment + push its parameter onto the
     * shared positional list. Returns the fragment with {@code ?N} placeholders
     * matching the param positions just appended.
     */
    private String renderFragment(FilterSpec spec, List<Object> params) {
        String col = "rb." + spec.column().sqlName();
        FilterColumn.Kind kind = spec.column().kind();
        FilterOp op = spec.op();

        // Valueless ops first — no parameter binding.
        if (op == FilterOp.EMPTY) {
            return switch (kind) {
                case NUMERIC, DATE -> col + " IS NULL";
                case TEXT -> "(" + col + " IS NULL OR " + col + " = '')";
            };
        }
        if (op == FilterOp.NOT_EMPTY) {
            return switch (kind) {
                case NUMERIC, DATE -> col + " IS NOT NULL";
                case TEXT -> "(" + col + " IS NOT NULL AND " + col + " <> '')";
            };
        }

        // Coerce + bind value
        Object boundValue = coerce(spec);
        params.add(boundValue);
        int idx = params.size(); // positional, 1-indexed
        String castType = switch (kind) {
            case NUMERIC -> "numeric";
            case TEXT    -> "text";
            case DATE    -> "timestamptz";
        };
        String castedParam = "CAST(?" + idx + " AS " + castType + ")";

        return switch (op) {
            case EQ  -> kind == FilterColumn.Kind.TEXT
                    ? "LOWER(" + col + ") = LOWER(" + castedParam + ")"
                    : col + " = " + castedParam;
            case NEQ -> kind == FilterColumn.Kind.TEXT
                    ? "LOWER(" + col + ") <> LOWER(" + castedParam + ")"
                    : col + " <> " + castedParam;
            case GT  -> col + " > "  + castedParam;
            case GTE -> col + " >= " + castedParam;
            case LT  -> col + " < "  + castedParam;
            case LTE -> col + " <= " + castedParam;
            case CONTAINS    -> "LOWER(" + col + ") LIKE LOWER(CONCAT('%', " + castedParam + ", '%'))";
            case STARTS_WITH -> "LOWER(" + col + ") LIKE LOWER(CONCAT(" + castedParam + ", '%'))";
            case ENDS_WITH   -> "LOWER(" + col + ") LIKE LOWER(CONCAT('%', " + castedParam + "))";
            case EMPTY, NOT_EMPTY -> throw new IllegalStateException("valueless op leaked past guard");
        };
    }

    private Object coerce(FilterSpec spec) {
        return switch (spec.column().kind()) {
            case NUMERIC -> {
                try {
                    yield new BigDecimal(spec.value());
                } catch (NumberFormatException ex) {
                    throw new ReserveBidValidationException("FILTER_INVALID_NUMERIC",
                            "value '" + spec.value() + "' is not numeric for column '" +
                            spec.column().sqlName() + "'");
                }
            }
            case TEXT -> spec.value();
            case DATE -> {
                try {
                    // Accept either a full ISO instant or a YYYY-MM-DD date
                    // (interpreted as start-of-day UTC). The frontend's date
                    // picker produces YYYY-MM-DD; tests may pass full ISO.
                    String v = spec.value().trim();
                    if (v.length() == 10) {
                        yield LocalDate.parse(v).atStartOfDay(ZoneOffset.UTC).toInstant();
                    }
                    yield Instant.parse(v);
                } catch (Exception ex) {
                    throw new ReserveBidValidationException("FILTER_INVALID_DATE",
                            "value '" + spec.value() + "' is not a valid date for column '" +
                            spec.column().sqlName() + "'");
                }
            }
        };
    }

    private String renderOrderBy(Sort sort) {
        if (sort == null || sort.isUnsorted()) return "";
        StringBuilder sb = new StringBuilder("ORDER BY ");
        boolean first = true;
        for (Sort.Order order : sort) {
            FilterColumn col = FilterColumn.parse(order.getProperty()).orElseThrow(() ->
                    new ReserveBidValidationException("FILTER_INVALID_SORT",
                            "unknown sort column: " + order.getProperty()));
            if (!first) sb.append(", ");
            sb.append("rb.").append(col.sqlName()).append(' ')
              .append(order.getDirection() == Sort.Direction.DESC ? "DESC" : "ASC");
            first = false;
        }
        return sb.toString();
    }
}
