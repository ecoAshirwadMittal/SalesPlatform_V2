package com.ecoatm.salesplatform.service;

import com.ecoatm.salesplatform.dto.BuyerOverviewPageResponse;
import com.ecoatm.salesplatform.dto.BuyerOverviewResponse;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Backs the Buyers Overview admin grid — Mendix parity for
 * AuctionUI.Buyer_Overview.
 *
 * The `buyer_codes_display` column is computed at read time from
 * buyer_mgmt.buyer_code_buyers ⟶ buyer_mgmt.buyer_codes via string_agg.
 * We intentionally do not denormalize it onto buyers — avoids the
 * stale-cache bug the Mendix model carried.
 */
@Service
public class BuyerOverviewService {

    private final EntityManager em;

    public BuyerOverviewService(EntityManager em) {
        this.em = em;
    }

    /**
     * Status values in the DB are {'Active','Disabled'} plus legacy empty/null.
     * Mendix renders empty status with the Disabled icon — we normalize here so
     * the frontend only branches on two values.
     */
    private static String normalizeStatus(String raw) {
        if (raw == null || raw.isBlank()) return "Disabled";
        return raw;
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public BuyerOverviewPageResponse search(
            String companyName,
            String buyerCodes,
            String status,
            int page,
            int pageSize) {

        String nameFilter = blankToNull(companyName);
        String codesFilter = blankToNull(buyerCodes);
        String statusFilter = blankToNull(status);

        int offset = page * pageSize;

        // The codes subquery is computed once per buyer and re-used in the
        // codes filter. Parameters are bound — no SQL injection.
        String baseSql = """
                SELECT b.id,
                       b.company_name,
                       b.status,
                       COALESCE((
                           SELECT string_agg(bc.code, ', ' ORDER BY bc.code)
                           FROM buyer_mgmt.buyer_code_buyers bcb
                           JOIN buyer_mgmt.buyer_codes bc
                               ON bc.id = bcb.buyer_code_id
                              AND bc.soft_delete = false
                           WHERE bcb.buyer_id = b.id
                       ), '') AS buyer_codes_display
                FROM buyer_mgmt.buyers b
                WHERE (CAST(:name AS text) IS NULL OR LOWER(b.company_name) LIKE LOWER(CONCAT('%', CAST(:name AS text), '%')))
                  AND (CAST(:status AS text) IS NULL OR b.status = CAST(:status AS text))
                """;

        // The codes filter must be applied against the aggregated result, so
        // wrap in a subselect.
        String filteredSql = """
                SELECT * FROM ( %s ) buyers_with_codes
                WHERE (CAST(:codes AS text) IS NULL OR LOWER(buyer_codes_display) LIKE LOWER(CONCAT('%%', CAST(:codes AS text), '%%')))
                ORDER BY company_name ASC
                LIMIT :limit OFFSET :offset
                """.formatted(baseSql);

        List<Object[]> rows = em.createNativeQuery(filteredSql)
                .setParameter("name", nameFilter)
                .setParameter("status", statusFilter)
                .setParameter("codes", codesFilter)
                .setParameter("limit", pageSize)
                .setParameter("offset", offset)
                .getResultList();

        String countSql = """
                SELECT COUNT(*) FROM ( %s ) buyers_with_codes
                WHERE (CAST(:codes AS text) IS NULL OR LOWER(buyer_codes_display) LIKE LOWER(CONCAT('%%', CAST(:codes AS text), '%%')))
                """.formatted(baseSql);

        Number total = (Number) em.createNativeQuery(countSql)
                .setParameter("name", nameFilter)
                .setParameter("status", statusFilter)
                .setParameter("codes", codesFilter)
                .getSingleResult();

        long totalElements = total == null ? 0L : total.longValue();

        List<BuyerOverviewResponse> content = rows.stream()
                .map(r -> new BuyerOverviewResponse(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        (String) r[3],
                        normalizeStatus((String) r[2])
                ))
                .toList();

        int totalPages = pageSize == 0 ? 0 : (int) Math.ceil((double) totalElements / pageSize);

        return new BuyerOverviewPageResponse(content, page, pageSize, totalElements, totalPages);
    }
}
