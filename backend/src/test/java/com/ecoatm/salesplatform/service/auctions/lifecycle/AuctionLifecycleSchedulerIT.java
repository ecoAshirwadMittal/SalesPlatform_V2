package com.ecoatm.salesplatform.service.auctions.lifecycle;

import com.ecoatm.salesplatform.PostgresIntegrationTest;
import com.ecoatm.salesplatform.infra.scheduledjob.ScheduledJobRun;
import com.ecoatm.salesplatform.infra.scheduledjob.ScheduledJobRunRepository;
import com.ecoatm.salesplatform.infra.scheduledjob.ScheduledJobRunStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for {@link AuctionLifecycleScheduler}, the entry point that
 * wraps {@link AuctionLifecycleService#tick()} with the
 * {@code ScheduledJobRunRecorder} audit row.
 *
 * <p>The scheduler bean is enabled and Spring's {@code @Scheduled} fires once
 * at startup. Manually invoking {@code scheduler.runTick()} from the test
 * thread races with the auto-firing scheduler-1 thread under
 * {@code @SchedulerLock(lockAtLeastFor = PT10S)}, so we instead capture the
 * highest pre-existing {@code id} from {@code infra.scheduled_job_run},
 * poll for a new {@code auctionLifecycle} row written after that id, and
 * assert on its audit columns. {@code poll-ms=86400000} prevents a second
 * scheduler firing during the test window.
 *
 * <p>This intentionally does <strong>not</strong> use {@code @Transactional}
 * — the audit row is written from the scheduler-1 thread in its own
 * transaction, and a test-thread {@code @Transactional} would hide it.
 */
@TestPropertySource(properties = {
        "auctions.lifecycle.enabled=true",
        "auctions.lifecycle.poll-ms=86400000"
})
class AuctionLifecycleSchedulerIT extends PostgresIntegrationTest {

    @Autowired private ScheduledJobRunRepository runRepo;

    @Test
    @DisplayName("scheduler @Scheduled fires on startup and writes an audit row tagged OK with non-null duration and node id")
    void scheduler_startupFire_writesAuditRow() throws InterruptedException {
        Instant deadline = Instant.now().plus(Duration.ofSeconds(20));
        Optional<ScheduledJobRun> latest = Optional.empty();

        while (Instant.now().isBefore(deadline)) {
            latest = runRepo.findAll().stream()
                    .filter(r -> "auctionLifecycle".equals(r.getJobName()))
                    .filter(r -> r.getStatus() != null
                              && r.getStatus() != ScheduledJobRunStatus.RUNNING)
                    .max(Comparator.comparing(ScheduledJobRun::getId));
            if (latest.isPresent()) break;
            Thread.sleep(200);
        }

        ScheduledJobRun row = latest.orElseThrow(() ->
                new AssertionError("scheduler did not write an auctionLifecycle audit row within 20s"));

        assertThat(row.getStatus()).isEqualTo(ScheduledJobRunStatus.OK);
        assertThat(row.getDurationMs()).isNotNull();
        assertThat(row.getNodeId()).isNotBlank();
        assertThat(row.getNodeId()).contains("-");
    }
}
