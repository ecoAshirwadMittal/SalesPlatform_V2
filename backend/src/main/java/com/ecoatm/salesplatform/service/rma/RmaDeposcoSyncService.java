package com.ecoatm.salesplatform.service.rma;

import com.ecoatm.salesplatform.model.pws.Rma;
import com.ecoatm.salesplatform.repository.pws.RmaRepository;
import com.ecoatm.salesplatform.repository.pws.RmaStatusRepository;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Single-leader scheduled job that periodically asks Deposco for the status of
 * each in-flight RMA and advances the RMA to {@code Received} when Deposco
 * reports receipt. The modern port of legacy {@code ACT_UpdateRMAFromDeposco}
 * (+ {@code SUB_SyncRMAStatus}).
 *
 * <p>No real Deposco reverse-logistics endpoint exists yet, so the injected
 * {@link DeposcoRmaClient} is the no-op {@link LoggingDeposcoRmaClient} by
 * default — the job is fully wired but inert until a real client lands. The
 * toggle {@code rma.deposco-sync.enabled} is {@code false} by default, so the
 * scheduled tick short-circuits before any DB read.
 */
@Service
public class RmaDeposcoSyncService {

    static final String JOB_NAME = "rmaDeposcoSync";

    /** RMA {@code system_status} value that means Deposco has received the return. */
    static final String RECEIVED_STATUS = "Received";

    /**
     * Statuses that are terminal for the Deposco sync — an RMA in one of these
     * is never polled or advanced.
     *
     * <p>{@code Received}/{@code Canceled} are closed outcomes
     * ({@code rma_status.status_grouped_to = 'Closed'}) — legacy
     * {@code ACT_UpdateRMAFromDeposco} likewise excludes them.
     *
     * <p>{@code Submitted} is skipped as a <b>safety invariant</b>, not a data
     * coincidence: a Submitted RMA has not been through sales review. Today it
     * happens to have no {@code oracle_number} (so the Oracle-number filter
     * drops it), but RMA #3 wires the Oracle push that SETS {@code oracle_number}
     * — after which the number filter alone would no longer keep Submitted RMAs
     * out, and polling one could advance an unreviewed RMA straight to
     * {@code Received}, bypassing review. Excluding it by status closes that
     * hole. Legacy also explicitly excludes {@code Submitted}.
     *
     * <p>{@code Declined} is treated as terminal per product decision
     * 2026-07-12; legacy {@code ACT_UpdateRMAFromDeposco} polls Declined (and
     * would flip Declined -> Received on physical receipt) — an intentional
     * divergence.
     */
    static final Set<String> TERMINAL_STATUSES = Set.of("Received", "Canceled", "Declined", "Submitted");

    private static final Logger log = LoggerFactory.getLogger(RmaDeposcoSyncService.class);

    private final RmaRepository rmaRepository;
    private final RmaStatusRepository rmaStatusRepository;
    private final DeposcoRmaClient deposcoRmaClient;
    private final Clock clock;
    private final boolean enabled;

    public RmaDeposcoSyncService(RmaRepository rmaRepository,
                                 RmaStatusRepository rmaStatusRepository,
                                 DeposcoRmaClient deposcoRmaClient,
                                 Clock clock,
                                 @Value("${rma.deposco-sync.enabled:false}") boolean enabled) {
        this.rmaRepository = rmaRepository;
        this.rmaStatusRepository = rmaStatusRepository;
        this.deposcoRmaClient = deposcoRmaClient;
        this.clock = clock;
        this.enabled = enabled;
    }

    /**
     * Scheduled entry point. Short-circuits when {@code rma.deposco-sync.enabled}
     * is false (the default) so the job is inert until a real client lands.
     * {@code @SchedulerLock} makes it single-leader across instances, reusing
     * the ShedLock setup from {@code SchedulingConfig}.
     */
    @Scheduled(fixedDelayString = "${rma.deposco-sync.fixed-delay-ms:1800000}")
    @SchedulerLock(name = JOB_NAME, lockAtMostFor = "PT20M", lockAtLeastFor = "PT1M")
    public void pollDeposco() {
        if (!enabled) {
            log.info("[{}] disabled — skipping Deposco RMA status poll", JOB_NAME);
            return;
        }
        sync();
    }

    /**
     * Polls Deposco for every candidate RMA and advances the ones Deposco
     * reports as received.
     *
     * <p>Each RMA is handled in isolation: any exception thrown while polling or
     * advancing a single RMA (e.g. a real HTTP {@link DeposcoRmaClient} failing
     * on one order) is caught, logged at WARN, and the loop continues to the
     * next candidate. This matters because candidates are ordered
     * {@code createdDate ASC} — without per-row isolation, one persistently
     * failing old RMA would abort the whole tick and block every newer RMA
     * behind it on every run. Mirrors {@code EmailRetryWorker.retryFailedRows}.
     *
     * <p>Separately, each advance is persisted in its own transaction (this
     * method is not {@code @Transactional}), so a later failure also cannot roll
     * back rows already advanced this tick — but that transaction isolation is
     * distinct from, and not a substitute for, the per-row exception isolation
     * above.
     *
     * @return the number of RMAs advanced to {@code Received} this run
     */
    public int sync() {
        Instant startedAt = clock.instant();
        List<Rma> candidates = rmaRepository.findPollableForDeposcoSync(TERMINAL_STATUSES);
        int advanced = 0;
        for (Rma rma : candidates) {
            if (!isPollable(rma)) {
                continue;
            }
            try {
                Optional<DeposcoRmaStatus> reported = deposcoRmaClient.fetchStatus(rma.getOracleNumber());
                if (reportsReceived(reported)) {
                    advanceToReceived(rma);
                    advanced++;
                }
            } catch (Exception ex) {
                // Isolate the failing RMA so the rest of the batch still runs —
                // number + Oracle order number are business identifiers (no PII).
                log.warn("[{}] Deposco sync failed for RMA {} (oracle {}) — skipping to next candidate",
                        JOB_NAME, rma.getNumber(), rma.getOracleNumber(), ex);
            }
        }
        log.info("[{}] polled {} candidate RMA(s), advanced {} to {} (startedAt={})",
                JOB_NAME, candidates.size(), advanced, RECEIVED_STATUS, startedAt);
        return advanced;
    }

    /**
     * Defensive guard — redundant with the finder's SQL filter, but belt-and-
     * suspenders so the skip rules hold even if a caller passes a stale row: an
     * RMA is pollable only when it carries an Oracle number and is not already
     * in a terminal status.
     */
    private boolean isPollable(Rma rma) {
        if (rma.getOracleNumber() == null || rma.getOracleNumber().isBlank()) {
            return false;
        }
        String currentStatus = currentStatus(rma);
        return currentStatus != null && !TERMINAL_STATUSES.contains(currentStatus);
    }

    /**
     * Reads the RMA's effective status, preferring the {@code rma_status} FK
     * association (the reliable representation in the migrated data) over the
     * direct {@code system_status} column.
     */
    private String currentStatus(Rma rma) {
        if (rma.getRmaStatus() != null && rma.getRmaStatus().getSystemStatus() != null) {
            return rma.getRmaStatus().getSystemStatus();
        }
        return rma.getSystemStatus();
    }

    private boolean reportsReceived(Optional<DeposcoRmaStatus> reported) {
        return reported.map(DeposcoRmaStatus::reportedStatus)
                .filter(RECEIVED_STATUS::equalsIgnoreCase)
                .isPresent();
    }

    /**
     * Advances the RMA to {@code Received}, keeping both status representations
     * in sync (the FK association and the direct column) exactly as
     * {@code RmaService.completeReview} does.
     */
    private void advanceToReceived(Rma rma) {
        rmaStatusRepository.findBySystemStatus(RECEIVED_STATUS).ifPresent(rma::setRmaStatus);
        rma.setSystemStatus(RECEIVED_STATUS);
        rmaRepository.save(rma);
        log.info("[{}] RMA {} reported received by Deposco — advanced to {}",
                JOB_NAME, rma.getNumber(), RECEIVED_STATUS);
    }
}
