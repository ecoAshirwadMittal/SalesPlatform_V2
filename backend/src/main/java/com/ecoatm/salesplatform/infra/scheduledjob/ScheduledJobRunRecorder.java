package com.ecoatm.salesplatform.infra.scheduledjob;

import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;

@Component
public class ScheduledJobRunRecorder {

    private final ScheduledJobRunRepository repository;
    private final Clock clock;
    private final String nodeId;

    public ScheduledJobRunRecorder(ScheduledJobRunRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
        this.nodeId = computeNodeId();
    }

    public Handle begin(String jobName) {
        ScheduledJobRun row = new ScheduledJobRun();
        row.setJobName(jobName);
        row.setStartedAt(clock.instant());
        row.setStatus(ScheduledJobRunStatus.RUNNING);
        row.setNodeId(nodeId);
        repository.save(row);
        return new Handle(row);
    }

    public void end(Handle handle, ScheduledJobRunStatus status,
                    String errorMessage, Map<String, Object> counters) {
        ScheduledJobRun row = handle.row();
        Instant now = clock.instant();
        row.setFinishedAt(now);
        row.setStatus(status);
        row.setErrorMessage(errorMessage);
        row.setCounters(counters);
        row.setDurationMs((int) (now.toEpochMilli() - row.getStartedAt().toEpochMilli()));
        repository.save(row);
    }

    private static String computeNodeId() {
        String name = ManagementFactory.getRuntimeMXBean().getName(); // pid@hostname
        int at = name.indexOf('@');
        if (at < 0) return name;
        String pid = name.substring(0, at);
        String host = name.substring(at + 1);
        return host + "-" + pid;
    }

    public record Handle(ScheduledJobRun row) {}
}
