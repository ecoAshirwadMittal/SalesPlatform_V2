package com.ecoatm.salesplatform.service.auctions.reservebid;

import com.ecoatm.salesplatform.infra.scheduledjob.ScheduledJobRunRecorder;
import com.ecoatm.salesplatform.infra.scheduledjob.ScheduledJobRunStatus;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReserveBidSyncScheduledJob {

    private static final Logger log = LoggerFactory.getLogger(ReserveBidSyncScheduledJob.class);
    public static final String JOB_NAME = "reserve-bid-sync";

    private final ReserveBidService service;
    private final ScheduledJobRunRecorder recorder;
    private final Environment env;

    public ReserveBidSyncScheduledJob(ReserveBidService service,
                                      ScheduledJobRunRecorder recorder,
                                      Environment env) {
        this.service = service;
        this.recorder = recorder;
        this.env = env;
    }

    @Scheduled(fixedDelayString = "${eb.sync.fixed-delay-ms:1800000}")
    @SchedulerLock(name = JOB_NAME, lockAtMostFor = "PT20M", lockAtLeastFor = "PT1M")
    public void run() {
        if (!Boolean.TRUE.equals(env.getProperty("eb.sync.enabled", Boolean.class, true))) {
            log.info("[{}] disabled", JOB_NAME);
            return;
        }
        ScheduledJobRunRecorder.Handle handle = recorder.begin(JOB_NAME);
        try {
            int rows = service.runScheduledSync();
            recorder.end(handle, ScheduledJobRunStatus.OK, null, Map.of("rowsFetched", rows));
        } catch (Exception ex) {
            log.error("[{}] failed", JOB_NAME, ex);
            recorder.end(handle, ScheduledJobRunStatus.FAILED, ex.getMessage(), null);
        }
    }
}
