package com.ecoatm.salesplatform.service.admin;

import com.ecoatm.salesplatform.dto.QualifiedBuyerCodeAdminListResponse;
import com.ecoatm.salesplatform.dto.QualifiedBuyerCodeAdminRow;
import com.ecoatm.salesplatform.model.buyermgmt.QualificationType;
import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCode;
import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCodeAudit;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeAuditRepository;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * P8 Lane 3B — admin Qualified Buyer Codes view + manual qualify/unqualify.
 *
 * <p>The PATCH semantics mirror the Mendix legacy: when an admin flips
 * {@code included} via this endpoint, the row's {@code qualification_type}
 * is forced to {@code Manual}. Subsequent auto-recompute jobs (e.g. R1 init,
 * R2 selection-criteria sync) that re-derive Qualified/Not_Qualified rows
 * must skip {@code Manual} rows so the admin's decision sticks.
 */
@Service
public class QualifiedBuyerCodeAdminService {

    private final QualifiedBuyerCodeRepository qbcRepo;
    private final QualifiedBuyerCodeAuditRepository auditRepo;

    @PersistenceContext
    private EntityManager em;

    public QualifiedBuyerCodeAdminService(QualifiedBuyerCodeRepository qbcRepo,
                                          QualifiedBuyerCodeAuditRepository auditRepo) {
        this.qbcRepo = qbcRepo;
        this.auditRepo = auditRepo;
    }

    @Transactional(readOnly = true, timeout = 10)
    public QualifiedBuyerCodeAdminListResponse list(long schedulingAuctionId) {
        // JOIN to buyer_codes so the admin grid shows the human code
        // (e.g. "AA600WHL") without a follow-up lookup. Sort on code so the
        // grid matches the QA dropdown ordering.
        Query q = em.createNativeQuery("""
                SELECT qbc.id, qbc.scheduling_auction_id, qbc.buyer_code_id,
                       bc.code, qbc.qualification_type, qbc.included,
                       qbc.is_special_treatment
                  FROM buyer_mgmt.qualified_buyer_codes qbc
                  JOIN buyer_mgmt.buyer_codes bc ON bc.id = qbc.buyer_code_id
                 WHERE qbc.scheduling_auction_id = ?1
                 ORDER BY bc.code ASC
                """);
        q.setParameter(1, schedulingAuctionId);
        @SuppressWarnings("unchecked")
        List<Object[]> raw = q.getResultList();
        List<QualifiedBuyerCodeAdminRow> rows = new ArrayList<>(raw.size());
        for (Object[] r : raw) {
            rows.add(new QualifiedBuyerCodeAdminRow(
                    ((Number) r[0]).longValue(),
                    ((Number) r[1]).longValue(),
                    ((Number) r[2]).longValue(),
                    (String) r[3],
                    (String) r[4],
                    Boolean.TRUE.equals(r[5]),
                    Boolean.TRUE.equals(r[6])
            ));
        }
        return new QualifiedBuyerCodeAdminListResponse(rows, rows.size());
    }

    @Transactional(timeout = 10)
    public QualifiedBuyerCodeAdminRow updateIncluded(long id, boolean included, long actingUserId) {
        QualifiedBuyerCode qbc = qbcRepo.findById(id)
                .orElseThrow(() -> new QualifiedBuyerCodeNotFoundException(id));

        boolean oldIncluded = qbc.isIncluded();
        QualificationType oldType = qbc.getQualificationType();

        qbc.setIncluded(included);
        qbc.setQualificationType(QualificationType.Manual);
        qbc.setChangedDate(LocalDateTime.now());
        qbc.setChangedById(actingUserId);
        QualifiedBuyerCode saved = qbcRepo.save(qbc);

        QualifiedBuyerCodeAudit audit = new QualifiedBuyerCodeAudit();
        audit.setQualifiedBuyerCodeId(saved.getId());
        audit.setSchedulingAuctionId(saved.getSchedulingAuctionId());
        audit.setBuyerCodeId(saved.getBuyerCodeId());
        audit.setOldIncluded(oldIncluded);
        audit.setNewIncluded(saved.isIncluded());
        audit.setOldQualificationType(oldType == null ? null : oldType.name());
        audit.setNewQualificationType(QualificationType.Manual.name());
        audit.setChangedById(actingUserId);
        audit.setCreatedDate(Instant.now());
        auditRepo.save(audit);

        // The list-projection JOIN is reused via a one-shot lookup so the
        // PATCH response shape matches the GET row shape.
        return list(saved.getSchedulingAuctionId()).rows().stream()
                .filter(r -> r.id() == saved.getId())
                .findFirst()
                .orElse(new QualifiedBuyerCodeAdminRow(
                        saved.getId(),
                        saved.getSchedulingAuctionId(),
                        saved.getBuyerCodeId(),
                        null,
                        QualificationType.Manual.name(),
                        saved.isIncluded(),
                        saved.isSpecialTreatment()
                ));
    }
}
