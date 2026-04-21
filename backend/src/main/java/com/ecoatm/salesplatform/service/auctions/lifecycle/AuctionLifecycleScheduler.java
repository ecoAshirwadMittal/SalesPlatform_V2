package com.ecoatm.salesplatform.service.auctions.lifecycle;

import com.ecoatm.salesplatform.infra.scheduledjob.ScheduledJobRunRecorder;
import com.ecoatm.salesplatform.infra.scheduledjob.ScheduledJobRunStatus;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "auctions.lifecycle", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AuctionLifecycleScheduler {

    private static final Logger log = LoggerFactory.getLogger(AuctionLifecycleScheduler.class);
    private static final String JOB_NAME = "auctionLifecycle";

    private final AuctionLifecycleService lifecycleService;
    private final ScheduledJobRunRecorder recorder;

    public AuctionLifecycleScheduler(AuctionLifecycleService lifecycleService,
                                     ScheduledJobRunRecorder recorder) {
        this.lifecycleService = lifecycleService;
        this.recorder = recorder;
    }

    @Scheduled(fixedDelayString = "${auctions.lifecycle.poll-ms:60000}")
    @SchedulerLock(name = JOB_NAME, lockAtLeastFor = "PT10S", lockAtMostFor = "PT55S")
    public void runTick() {
        ScheduledJobRunRecorder.Handle handle = recorder.begin(JOB_NAME);
        try {
            TickResult result = lifecycleService.tick();
            recorder.end(handle, ScheduledJobRunStatus.OK, null, result.counters());
            log.debug("Lifecycle tick complete counters={}", result.counters());
        } catch (Exception e) {
            log.error("Lifecycle tick failed", e);
            recorder.end(handle, ScheduledJobRunStatus.FAILED, e.getMessage(), null);
        }
    }
}
