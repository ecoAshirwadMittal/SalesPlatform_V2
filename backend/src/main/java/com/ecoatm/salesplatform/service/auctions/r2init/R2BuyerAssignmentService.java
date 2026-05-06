package com.ecoatm.salesplatform.service.auctions.r2init;

import com.ecoatm.salesplatform.event.R2BuyerAssignmentCompletedEvent;
import com.ecoatm.salesplatform.exception.EntityNotFoundException;
import com.ecoatm.salesplatform.exception.RecalcAlreadyRunningException;
import com.ecoatm.salesplatform.model.auctions.SchedulingAuction;
import com.ecoatm.salesplatform.model.buyermgmt.AuctionsFeatureConfig;
import com.ecoatm.salesplatform.repository.AuctionsFeatureConfigRepository;
import com.ecoatm.salesplatform.repository.QualifiedBuyerCodeRepository;
import com.ecoatm.salesplatform.repository.auctions.R2BuyerQualificationRepository;
import com.ecoatm.salesplatform.repository.auctions.R2SpecialBuyerRepository;
import com.ecoatm.salesplatform.repository.auctions.SchedulingAuctionRepository;
import com.ecoatm.salesplatform.service.auctions.recalc.RecalcStatusUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Sub-project 5 — five-phase port of Mendix
 * {@code SUB_AssignRoundTwoBuyers} +
 * {@code SUB_GenerateRound2QualifiedBuyerCodes} +
 * {@code Sub_ProcessSpecialBuyers}.
 *
 * <p>Phases (see design §4):
 * <ol>
 *   <li>Pre-checks (round, week resolution, config gate) — BEFORE the state
 *       flip so failures leave {@code r2_init_status} untouched.</li>
 *   <li>State flip {@code PENDING/SUCCESS/FAILED → RUNNING}; another caller
 *       holding RUNNING throws {@link RecalcAlreadyRunningException}.</li>
 *   <li>Compute qualified + special-treatment buyer-code sets via the two
 *       native CTE repositories (T6 + T7).</li>
 *   <li>DELETE existing QBCs for the SA, then bulk-INSERT the three-set
 *       result. V72 flattened the M:M junctions; no junction step needed.</li>
 *   <li>Per-special-treatment buyer-code, seed {@code bid_data} rows for
 *       every non-deprecated AE (T10's {@link BidDataForAllAEService}).</li>
 *   <li>Mark SUCCESS + publish {@link R2BuyerAssignmentCompletedEvent}.</li>
 * </ol>
 *
 * <p>{@code REQUIRES_NEW} so each invocation is independent of orchestrator
 * failure modes (cron tick lifecycle, admin endpoint controller tx). On any
 * RuntimeException during phases 3-5, status flips to FAILED in
 * {@link RecalcStatusUpdater#markFailed} (its own REQUIRES_NEW sub-tx so the
 * row survives the parent rollback) and the exception is rethrown.
 *
 * <p>Mirrors {@link com.ecoatm.salesplatform.service.auctions.recalc.BidRankingService}
 * — the canonical 4C status-flip + event-publish pattern.
 */
@Service
public class R2BuyerAssignmentService {

    private static final Logger log = LoggerFactory.getLogger(R2BuyerAssignmentService.class);
    private static final String PROCESS = "R2_INIT";
    private static final long FEATURE_CONFIG_ID = 1L;

    private final SchedulingAuctionRepository saRepo;
    private final AuctionsFeatureConfigRepository cfgRepo;
    private final R2BuyerQualificationRepository qualRepo;
    private final R2SpecialBuyerRepository specialRepo;
    private final QualifiedBuyerCodeRepository qbcRepo;
    private final BidDataForAllAEService specialBidDataService;
    private final RecalcStatusUpdater statusUpdater;
    private final ApplicationEventPublisher events;

    public R2BuyerAssignmentService(
            SchedulingAuctionRepository saRepo,
            AuctionsFeatureConfigRepository cfgRepo,
            R2BuyerQualificationRepository qualRepo,
            R2SpecialBuyerRepository specialRepo,
            QualifiedBuyerCodeRepository qbcRepo,
            BidDataForAllAEService specialBidDataService,
            RecalcStatusUpdater statusUpdater,
            ApplicationEventPublisher events) {
        this.saRepo = saRepo;
        this.cfgRepo = cfgRepo;
        this.qualRepo = qualRepo;
        this.specialRepo = specialRepo;
        this.qbcRepo = qbcRepo;
        this.specialBidDataService = specialBidDataService;
        this.statusUpdater = statusUpdater;
        this.events = events;
    }

    /**
     * Runs R2 buyer assignment for a round-2 SchedulingAuction.
     *
     * @throws EntityNotFoundException        404 — SA id unknown
     * @throws IllegalArgumentException       400 — round != 2
     * @throws IllegalStateException          409 — week_id unresolved or
     *                                        feature-config singleton missing
     * @throws RecalcAlreadyRunningException  409 — another caller already
     *                                        flipped status to RUNNING
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public R2BuyerAssignmentResult run(long schedulingAuctionId) {
        long start = System.currentTimeMillis();

        // Phase 1: pre-checks (BEFORE state flip — failure here leaves
        // r2_init_status untouched).
        SchedulingAuction sa = saRepo.findById(schedulingAuctionId)
            .orElseThrow(() -> new EntityNotFoundException(
                "scheduling_auction", schedulingAuctionId));

        if (sa.getRound() != 2) {
            throw new IllegalArgumentException(
                "R2 buyer assignment only valid for round 2; was " + sa.getRound());
        }

        Long weekId = saRepo.findWeekIdById(schedulingAuctionId);
        if (weekId == null) {
            throw new IllegalStateException(
                "Cannot resolve week_id for schedulingAuctionId=" + schedulingAuctionId);
        }

        AuctionsFeatureConfig cfg = cfgRepo.findById(FEATURE_CONFIG_ID)
            .orElseThrow(() -> new IllegalStateException(
                "auctions_feature_config singleton missing"));

        // Config gate: short-circuit to SKIPPED. No state flip, no event.
        if (!cfg.isCalculateRound2BuyerParticipation()) {
            statusUpdater.markSkipped(schedulingAuctionId, PROCESS);
            log.info("R2_INIT skipped (config gate FALSE) schedulingAuctionId={}",
                schedulingAuctionId);
            return new R2BuyerAssignmentResult(0, 0, 0, 0, 0L, true);
        }

        // Phase 2: state flip.
        boolean flipped = statusUpdater.tryFlipToRunning(schedulingAuctionId, PROCESS);
        if (!flipped) {
            throw new RecalcAlreadyRunningException(
                RecalcAlreadyRunningException.Process.R2_INIT, schedulingAuctionId);
        }

        try {
            // Phase 3: compute sets.
            Set<Long> qualified = qualRepo.qualifiedBuyerCodes(schedulingAuctionId);
            Set<Long> special   = specialRepo.specialTreatmentBuyerCodes(schedulingAuctionId);

            // Phase 4: DELETE + INSERT.  V72 flattened the M:M junctions; the
            // bulkInsertJunctions repo method is a documented post-V72 no-op
            // and is intentionally NOT called here (design §7.3 step 3).
            qbcRepo.deleteBySchedulingAuctionId(schedulingAuctionId);
            int totalRows = qbcRepo.bulkInsertForR2(
                schedulingAuctionId,
                qualified.toArray(new Long[0]),
                special.toArray(new Long[0]));

            // Counts: both regular-qualified and special-treatment rows write
            // qualification_type=Qualified, so qualifiedCount is the union
            // size (a code in both sets only contributes one Qualified row).
            Set<Long> unioned = new HashSet<>(qualified);
            unioned.addAll(special);
            int qualifiedCount    = unioned.size();
            int specialCount      = special.size();
            int notQualifiedCount = totalRows - qualifiedCount;

            // Phase 5: special-buyer bid_data.
            int specialBidData = specialBidDataService.generateForSpecialBuyers(
                schedulingAuctionId, special);

            // Phase 6: SUCCESS + event.
            statusUpdater.markSuccess(schedulingAuctionId, PROCESS);

            long durationMs = System.currentTimeMillis() - start;
            log.info("R2_INIT success schedulingAuctionId={} qualified={} special={} "
                    + "notQual={} bidData={} ms={}",
                schedulingAuctionId, qualifiedCount, specialCount, notQualifiedCount,
                specialBidData, durationMs);

            events.publishEvent(new R2BuyerAssignmentCompletedEvent(
                schedulingAuctionId, sa.getAuctionId(), weekId,
                qualifiedCount, specialCount));

            return new R2BuyerAssignmentResult(
                qualifiedCount, specialCount, notQualifiedCount, specialBidData,
                durationMs, false);
        } catch (RuntimeException ex) {
            String msg = ex.getClass().getSimpleName() + ": " + ex.getMessage();
            statusUpdater.markFailed(schedulingAuctionId, PROCESS, msg);
            log.error("R2_INIT failed schedulingAuctionId={}", schedulingAuctionId, ex);
            throw ex;
        }
    }

    /**
     * Admin re-run entry point. Same shape as {@link #run} but delegates;
     * exists so the controller (T13) has a stable, semantic name.
     */
    public R2BuyerAssignmentResult recalculate(long schedulingAuctionId) {
        return run(schedulingAuctionId);
    }
}
