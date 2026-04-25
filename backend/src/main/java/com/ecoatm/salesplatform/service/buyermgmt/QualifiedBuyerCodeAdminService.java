package com.ecoatm.salesplatform.service.buyermgmt;

import com.ecoatm.salesplatform.dto.QualifiedBuyerCodePageResponse;
import com.ecoatm.salesplatform.dto.QualifiedBuyerCodeRow;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.model.buyermgmt.QualificationType;
import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCode;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Admin operations on {@code buyer_mgmt.qualified_buyer_codes}.
 *
 * <p>The {@link #qualify} method sets {@code qualification_type = 'Manual'},
 * porting Mendix behaviour where an admin can override automatic qualification
 * decisions for a given buyer code / scheduling auction pair.
 */
@Service
public class QualifiedBuyerCodeAdminService {

    private static final String WHERE =
            """
            WHERE (CAST(:saId        AS bigint) IS NULL
                   OR qbc.scheduling_auction_id = CAST(:saId AS bigint))
              AND (CAST(:buyerCodeId AS bigint) IS NULL
                   OR qbc.buyer_code_id = CAST(:buyerCodeId AS bigint))
            """;

    private static final String SELECT_SQL =
            """
            SELECT qbc.id,
                   qbc.scheduling_auction_id,
                   qbc.buyer_code_id,
                   qbc.qualification_type,
                   qbc.included,
                   qbc.submitted,
                   qbc.submitted_datetime,
                   qbc.is_special_treatment,
                   qbc.created_date,
                   qbc.changed_date
            FROM buyer_mgmt.qualified_buyer_codes qbc
            %s
            ORDER BY qbc.id DESC
            LIMIT :limit OFFSET :offset
            """;

    private static final String COUNT_SQL =
            """
            SELECT COUNT(*)
            FROM buyer_mgmt.qualified_buyer_codes qbc
            %s
            """;

    private final EntityManager em;
    private final QualifiedBuyerCodeRepository repository;

    public QualifiedBuyerCodeAdminService(EntityManager em,
                                           QualifiedBuyerCodeRepository repository) {
        this.em = em;
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public QualifiedBuyerCodePageResponse list(Long schedulingAuctionId, Long buyerCodeId,
                                                int page, int pageSize) {
        int offset = page * pageSize;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(SELECT_SQL.formatted(WHERE))
                .setParameter("saId", schedulingAuctionId)
                .setParameter("buyerCodeId", buyerCodeId)
                .setParameter("limit", pageSize)
                .setParameter("offset", offset)
                .getResultList();

        long total = ((Number) em.createNativeQuery(COUNT_SQL.formatted(WHERE))
                .setParameter("saId", schedulingAuctionId)
                .setParameter("buyerCodeId", buyerCodeId)
                .getSingleResult()).longValue();

        List<QualifiedBuyerCodeRow> content = rows.stream()
                .map(QualifiedBuyerCodeAdminService::toRow)
                .toList();

        int totalPages = pageSize == 0 ? 0 : (int) Math.ceil((double) total / pageSize);
        return new QualifiedBuyerCodePageResponse(content, page, pageSize, total, totalPages);
    }

    /**
     * Overrides qualification for a QBC row by setting
     * {@code qualification_type = 'Manual'}. This allows admins to manually
     * include a buyer code that automatic R1 init would have excluded.
     */
    @Transactional
    public QualifiedBuyerCodeRow qualify(long id) {
        QualifiedBuyerCode qbc = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("QualifiedBuyerCode", id));
        qbc.setQualificationType(QualificationType.Manual);
        qbc.setChangedDate(LocalDateTime.now());
        QualifiedBuyerCode saved = repository.save(qbc);
        return new QualifiedBuyerCodeRow(
                saved.getId(),
                saved.getSchedulingAuctionId(),
                saved.getBuyerCodeId(),
                saved.getQualificationType().name(),
                saved.isIncluded(),
                saved.isSubmitted(),
                saved.getSubmittedDatetime(),
                saved.isSpecialTreatment(),
                saved.getCreatedDate(),
                saved.getChangedDate()
        );
    }

    private static QualifiedBuyerCodeRow toRow(Object[] r) {
        return new QualifiedBuyerCodeRow(
                ((Number) r[0]).longValue(),                            // id
                ((Number) r[1]).longValue(),                            // schedulingAuctionId
                ((Number) r[2]).longValue(),                            // buyerCodeId
                (String) r[3],                                          // qualificationType
                Boolean.TRUE.equals(r[4]),                              // included
                Boolean.TRUE.equals(r[5]),                              // submitted
                toLocalDateTime(r[6]),                                  // submittedDatetime
                Boolean.TRUE.equals(r[7]),                              // specialTreatment
                toLocalDateTime(r[8]),                                  // createdDate
                toLocalDateTime(r[9])                                   // changedDate
        );
    }

    private static LocalDateTime toLocalDateTime(Object o) {
        if (o == null) return null;
        if (o instanceof Timestamp ts) return ts.toLocalDateTime();
        if (o instanceof LocalDateTime ldt) return ldt;
        return null;
    }
}
