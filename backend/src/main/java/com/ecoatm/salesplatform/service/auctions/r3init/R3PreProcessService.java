package com.ecoatm.salesplatform.service.auctions.r3init;

import com.ecoatm.salesplatform.event.R3PreProcessCompletedEvent;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.BidRound;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.buyermgmt.QualifiedBuyerCode;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeRepository;
import com.ecoatm.salesplatform.repository.auctions.BidRoundRepository;
import com.ecoatm.salesplatform.repository.auctions.R3BuyerQualificationRepository;
import com.ecoatm.salesplatform.repository.auctions.R3PreProcessSupportRepository;
import com.ecoatm.salesplatform.repository.auctions.R3SpecialBuyerRepository;
import com.ecoatm.salesplatform.repository.auctions.Round3BuyerDataReportRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.recalc.RecalcStatusUpdater;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Sub-project 6 — five-phase port of Mendix {@code SUB_Round3_PreProcessRoundData}.
 *
 * <p>Phases:
 * <ol>
 *   <li>Validate: load R2 + R3 SAs, check rounds, check sibling auctionId,
 *       short-circuit to SKIPPED when {@code has_round = false}.</li>
 *   <li>State flip {@code PENDING/SUCCESS/FAILED → RUNNING}; throws
 *       {@link RecalcAlreadyRunningException} if already RUNNING.</li>
 *   <li>Phase 1: delete unsubmitted R2 bids via {@link R3PreProcessSupportRepository}.</li>
 *   <li>Phase 2+3: compute qualified + special-treatment buyer-code sets.</li>
 *   <li>Phase 4: DELETE existing QBCs for R3 SA, bulk-INSERT three-set result.</li>
 *   <li>Phase 5: DELETE existing R3 reports, bulk-INSERT new report rows.</li>
 *   <li>Mark SUCCESS + publish {@link R3PreProcessCompletedEvent}.</li>
 * </ol>
 *
 * <p>{@code REQUIRES_NEW} so each invocation is independent. On {@code RuntimeException}
 * during phases 3-7, status flips to FAILED in {@link RecalcStatusUpdater#markFailed}
 * (its own REQUIRES_NEW sub-tx so the row survives parent rollback) and the
 * exception is rethrown.
 */
@Service
public class R3PreProcessService {

    private static final Logger log = LoggerFactory.getLogger(R3PreProcessService.class);
    private static final String PROCESS = "R3_PREPROCESS";

    private final SchedulingAuctionRepository saRepo;
    private final R3PreProcessSupportRepository supportRepo;
    private final R3BuyerQualificationRepository qualRepo;
    private final R3SpecialBuyerRepository       specialRepo;
    private final QualifiedBuyerCodeRepository   qbcRepo;
    private final BidRoundRepository             bidRoundRepo;
    private final Round3BuyerDataReportRepository reportRepo;
    private final RecalcStatusUpdater            statusUpdater;
    private final ApplicationEventPublisher      events;

    public R3PreProcessService(SchedulingAuctionRepository saRepo,
                               R3PreProcessSupportRepository supportRepo,
                               R3BuyerQualificationRepository qualRepo,
                               R3SpecialBuyerRepository specialRepo,
                               QualifiedBuyerCodeRepository qbcRepo,
                               BidRoundRepository bidRoundRepo,
                               Round3BuyerDataReportRepository reportRepo,
                               RecalcStatusUpdater statusUpdater,
                               ApplicationEventPublisher events) {
        this.saRepo = saRepo;
        this.supportRepo = supportRepo;
        this.qualRepo = qualRepo;
        this.specialRepo = specialRepo;
        this.qbcRepo = qbcRepo;
        this.bidRoundRepo = bidRoundRepo;
        this.reportRepo = reportRepo;
        this.statusUpdater = statusUpdater;
        this.events = events;
    }

    /**
     * Runs R3 pre-process for a sibling pair (R2 SA, R3 SA).
     *
     * @throws EntityNotFoundException        SA id unknown
     * @throws IllegalArgumentException       round mismatch or sibling check failure
     * @throws RecalcAlreadyRunningException  another caller already flipped to RUNNING
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public R3PreProcessResult run(long r2SaId, long r3SaId) {
        SchedulingAuction r2Sa = saRepo.findById(r2SaId)
            .orElseThrow(() -> new EntityNotFoundException(
                "scheduling_auction not found: id=" + r2SaId));
        SchedulingAuction r3Sa = saRepo.findById(r3SaId)
            .orElseThrow(() -> new EntityNotFoundException(
                "scheduling_auction not found: id=" + r3SaId));

        if (r2Sa.getRound() != 2) {
            throw new IllegalArgumentException(
                "R3 pre-process expects round-2 source SA; was " + r2Sa.getRound());
        }
        if (r3Sa.getRound() != 3) {
            throw new IllegalArgumentException(
                "R3 pre-process expects round-3 target SA; was " + r3Sa.getRound());
        }
        if (!r2Sa.getAuctionId().equals(r3Sa.getAuctionId())) {
            throw new IllegalArgumentException(
                "R2/R3 SAs not siblings: r2.auctionId=" + r2Sa.getAuctionId()
                    + " r3.auctionId=" + r3Sa.getAuctionId());
        }

        // has_round=false → SKIPPED short-circuit (no state flip, no event)
        if (!r3Sa.isHasRound()) {
            statusUpdater.markSkipped(r3SaId, PROCESS);
            log.info("R3_PREPROCESS skipped — has_round=false on r3SaId={}", r3SaId);
            return new R3PreProcessResult(0, 0, 0, 0, 0, 0L, true);
        }

        // State flip PENDING/SUCCESS/FAILED → RUNNING
        if (!statusUpdater.tryFlipToRunning(r3SaId, PROCESS)) {
            throw new RecalcAlreadyRunningException(
                RecalcAlreadyRunningException.Process.R3_PREPROCESS, r3SaId);
        }

        long start = System.currentTimeMillis();
        try {
            // Phase 1: delete unsubmitted R2 bids
            int deletedBids = supportRepo.deleteUnsubmittedBids(r2SaId);

            // Phase 2: regular qualification CTE
            Set<Long> qualified = qualRepo.qualifiedBuyerCodes(r3SaId);

            // Phase 3: special-treatment CTE
            Set<Long> special = specialRepo.specialTreatmentBuyerCodes(r3SaId);

            // Phase 4: DELETE existing QBCs for R3 SA, bulk-INSERT three-set result
            qbcRepo.deleteBySchedulingAuctionId(r3SaId);
            int totalRows = qbcRepo.bulkInsertForRound(
                r3SaId,
                qualified.toArray(new Long[0]),
                special.toArray(new Long[0]));

            // qualifiedCount = union of both sets (special are also Qualified)
            int qualifiedCount = qualified.size() + special.size();
            int specialCount   = special.size();
            int notQualified   = totalRows - qualifiedCount;

            // Phase 4.5: seed bid_rounds for every included QBC. Mirrors
            // Round1InitializationService:98-111 + the matching Phase 4.5
            // in R2BuyerAssignmentService — without these rows the bidder
            // dashboard's landingRoute returns BIDROUND_MISSING for every
            // otherwise-qualified bidder, rendering as the empty "Bidding
            // has ended" state. Idempotent on re-run via the
            // (sa, buyer_code) lookup.
            Long weekId = saRepo.findWeekIdById(r3SaId);
            List<QualifiedBuyerCode> includedQbcs = qbcRepo
                .findBySchedulingAuctionId(r3SaId).stream()
                .filter(QualifiedBuyerCode::isIncluded)
                .toList();
            List<BidRound> newBidRounds = new ArrayList<>(includedQbcs.size());
            for (QualifiedBuyerCode q : includedQbcs) {
                if (bidRoundRepo.findBySchedulingAuctionIdAndBuyerCodeId(
                        r3SaId, q.getBuyerCodeId()).isPresent()) {
                    continue;
                }
                BidRound br = new BidRound();
                br.setSchedulingAuctionId(r3SaId);
                br.setBuyerCodeId(q.getBuyerCodeId());
                br.setWeekId(weekId);
                br.setSubmitted(false);
                newBidRounds.add(br);
            }
            bidRoundRepo.saveAll(newBidRounds);

            // Phase 5: DELETE existing reports for R3 SA, bulk-INSERT new report rows
            reportRepo.deleteBySchedulingAuctionId(r3SaId);
            int reportRows = reportRepo.bulkInsertForSchedulingAuction(r3SaId);

            // Mark SUCCESS + publish event
            statusUpdater.markSuccess(r3SaId, PROCESS);
            long durationMs = System.currentTimeMillis() - start;
            log.info("R3_PREPROCESS success r3SaId={} qualified={} special={} notQual={} "
                    + "reports={} deleted={} ms={}",
                r3SaId, qualifiedCount, specialCount, notQualified, reportRows, deletedBids,
                durationMs);

            events.publishEvent(new R3PreProcessCompletedEvent(
                r3SaId, r3Sa.getAuctionId(), qualifiedCount, specialCount, reportRows));

            return new R3PreProcessResult(
                qualifiedCount, specialCount, notQualified, reportRows, deletedBids,
                durationMs, false);

        } catch (RuntimeException ex) {
            String msg = ex.getClass().getSimpleName() + ": " + ex.getMessage();
            statusUpdater.markFailed(r3SaId, PROCESS, msg);
            log.error("R3_PREPROCESS failed r3SaId={}", r3SaId, ex);
            throw ex;
        }
    }

    /**
     * Admin-recovery entrypoint — takes R3 SA id only, resolves R2 SA from
     * sibling lookup via {@code (auctionId, round=2)}.
     *
     * @throws EntityNotFoundException  R3 SA id unknown
     * @throws IllegalStateException    no R2 SA found for the same auction
     */
    /**
     * Admin-recovery entrypoint — takes R3 SA id only, resolves R2 SA from
     * sibling lookup via {@code (auctionId, round=2)}.
     *
     * <p>{@code REQUIRES_NEW} here ensures an active transaction exists when
     * {@code run()} is called via direct self-invocation (Spring AOP does not
     * proxy self-calls). Repositories inside {@code run()} that declare
     * {@code Propagation.MANDATORY} see this transaction.
     *
     * @throws EntityNotFoundException  R3 SA id unknown
     * @throws IllegalStateException    no R2 SA found for the same auction
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public R3PreProcessResult recalculate(long r3SaId) {
        SchedulingAuction r3Sa = saRepo.findById(r3SaId)
            .orElseThrow(() -> new EntityNotFoundException(
                "scheduling_auction not found: id=" + r3SaId));
        long r2SaId = saRepo.findByAuctionIdAndRound(r3Sa.getAuctionId(), 2)
            .map(SchedulingAuction::getId)
            .orElseThrow(() -> new IllegalStateException(
                "No R2 SA found for auctionId=" + r3Sa.getAuctionId()));
        return run(r2SaId, r3SaId);
    }
}
