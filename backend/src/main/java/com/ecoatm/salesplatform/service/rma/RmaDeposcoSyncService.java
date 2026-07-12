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
     * is never polled or advanced. Legacy {@code ACT_UpdateRMAFromDeposco}
     * excludes {@code Canceled}/{@code Received} (and un-pushed {@code Submitted},
     * which by definition has no Oracle number); {@code Declined} is added here
     * because it is a closed outcome in the modern status model
     * ({@code rma_status.status_grouped_to = 'Declined'}).
     */
    static final Set<String> TERMINAL_STATUSES = Set.of("Received", "Canceled", "Declined");

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
     * reports as received. Each advance is persisted independently, so one bad
     * row can never roll back the rest of the batch.
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
            Optional<DeposcoRmaStatus> reported = deposcoRmaClient.fetchStatus(rma.getOracleNumber());
            if (reportsReceived(reported)) {
                advanceToReceived(rma);
                advanced++;
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
