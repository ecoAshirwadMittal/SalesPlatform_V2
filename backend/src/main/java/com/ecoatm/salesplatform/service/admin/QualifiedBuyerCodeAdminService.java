package com.ecoatm.salesplatform.service.admin;

import com.ecoatm.salesplatform.dto.QualifiedBuyerCodeAdminListResponse;
import com.ecoatm.salesplatform.dto.QualifiedBuyerCodeAdminRow;
import com.ecoatm.salesplatform.event.buyermgmt.QualificationOverriddenEvent;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuctionStatus;
import com.ecoatm.salesplatform.model.buyermgmt.QualificationType;
import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCode;
import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCodeAudit;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeAuditRepository;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.context.ApplicationEventPublisher;
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
 *
 * <p>Gap-analysis 2.4 sub-feature 2 (the modern {@code _New} single path) adds
 * two behaviours to {@link #updateIncluded}, faithful to the legacy
 * {@code NF_OnIncludedChanged_New}:
 * <ul>
 *   <li><b>Round-status guard</b> — a {@code Closed} round is frozen: the
 *       override is rejected with {@link RoundClosedException} (HTTP 409) before
 *       any persist/audit/event.</li>
 *   <li><b>Override event</b> — every successful override publishes a
 *       {@link com.ecoatm.salesplatform.event.buyermgmt.QualificationOverriddenEvent}
 *       inside the committing transaction; the Task 4 listener consumes it and
 *       sends the manual-qualification email only for
 *       {@code roundStatus=Started && included=true}.</li>
 * </ul>
 * The modern path deliberately does <b>not</b> re-seed bid-data (that lived only
 * in the legacy {@code _Legacy} path) and does not push to Snowflake.
 */
@Service
public class QualifiedBuyerCodeAdminService {

    private final QualifiedBuyerCodeRepository qbcRepo;
    private final QualifiedBuyerCodeAuditRepository auditRepo;
    private final SchedulingAuctionRepository saRepo;
    private final ApplicationEventPublisher eventPublisher;

    @PersistenceContext
    private EntityManager em;

    public QualifiedBuyerCodeAdminService(QualifiedBuyerCodeRepository qbcRepo,
                                          QualifiedBuyerCodeAuditRepository auditRepo,
                                          SchedulingAuctionRepository saRepo,
                                          ApplicationEventPublisher eventPublisher) {
        this.qbcRepo = qbcRepo;
        this.auditRepo = auditRepo;
        this.saRepo = saRepo;
        this.eventPublisher = eventPublisher;
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

        // Round-status guard (legacy NF_OnIncludedChanged_New, modern _New path):
        // a Closed round is frozen — reject the override before ANY mutation so
        // nothing is persisted, no audit row is written, and no event fires. The
        // QBC has a NOT NULL FK to its scheduling_auction (V72, ON DELETE
        // CASCADE), so a missing SA is a data-integrity fault → 404.
        SchedulingAuction sa = saRepo.findById(qbc.getSchedulingAuctionId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "scheduling_auction not found: id=" + qbc.getSchedulingAuctionId()));
        if (sa.getRoundStatus() == SchedulingAuctionStatus.Closed) {
            throw new RoundClosedException();
        }

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

        // Publish the override as facts inside the committing transaction. Task 4
        // attaches an AFTER_COMMIT listener and decides whether to email (the
        // manual-qualification email fires only for roundStatus=Started &&
        // included=true — but that condition lives in the listener, not here).
        eventPublisher.publishEvent(new QualificationOverriddenEvent(
                saved.getId(),
                saved.getBuyerCodeId(),
                saved.getSchedulingAuctionId(),
                saved.isIncluded(),
                sa.getRoundStatus(),
                actingUserId,
                Instant.now()));

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
