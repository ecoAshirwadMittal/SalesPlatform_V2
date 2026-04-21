package com.ecoatm.salesplatform.infra.scheduledjob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduledJobRunRecorderTest {

    @Mock
    private ScheduledJobRunRepository repository;

    private ScheduledJobRunRecorder recorder;

    @BeforeEach
    void setUp() {
        Clock fixed = Clock.fixed(Instant.parse("2026-04-20T12:00:00Z"), ZoneOffset.UTC);
        recorder = new ScheduledJobRunRecorder(repository, fixed);
    }

    @Test
    void begin_writesRunningRowAndReturnsHandle() {
        ScheduledJobRun saved = new ScheduledJobRun();
        when(repository.save(any(ScheduledJobRun.class))).thenReturn(saved);

        ScheduledJobRunRecorder.Handle handle = recorder.begin("auctionLifecycle");

        ArgumentCaptor<ScheduledJobRun> captor = ArgumentCaptor.forClass(ScheduledJobRun.class);
        verify(repository).save(captor.capture());
        ScheduledJobRun row = captor.getValue();
        assertThat(row.getJobName()).isEqualTo("auctionLifecycle");
        assertThat(row.getStatus()).isEqualTo(ScheduledJobRunStatus.RUNNING);
        assertThat(row.getStartedAt()).isEqualTo(Instant.parse("2026-04-20T12:00:00Z"));
        assertThat(row.getNodeId()).contains("-");
        assertThat(handle).isNotNull();
    }

    @Test
    void end_writesOkStatusAndCounters() {
        ScheduledJobRun row = new ScheduledJobRun();
        row.setJobName("auctionLifecycle");
        row.setStartedAt(Instant.parse("2026-04-20T11:59:59Z"));
        row.setStatus(ScheduledJobRunStatus.RUNNING);
        ScheduledJobRunRecorder.Handle handle = new ScheduledJobRunRecorder.Handle(row);

        recorder.end(handle, ScheduledJobRunStatus.OK, null,
                Map.of("roundsStarted", 2, "roundsClosed", 1, "auctionsAffected", 2, "errorCount", 0));

        verify(repository).save(row);
        assertThat(row.getStatus()).isEqualTo(ScheduledJobRunStatus.OK);
        assertThat(row.getFinishedAt()).isEqualTo(Instant.parse("2026-04-20T12:00:00Z"));
        assertThat(row.getDurationMs()).isEqualTo(1000);
        assertThat(row.getErrorMessage()).isNull();
        assertThat(row.getCounters()).containsEntry("roundsStarted", 2);
    }

    @Test
    void end_writesFailedStatusWithErrorMessage() {
        ScheduledJobRun row = new ScheduledJobRun();
        row.setStartedAt(Instant.parse("2026-04-20T12:00:00Z"));
        ScheduledJobRunRecorder.Handle handle = new ScheduledJobRunRecorder.Handle(row);

        recorder.end(handle, ScheduledJobRunStatus.FAILED, "DB unavailable", null);

        assertThat(row.getStatus()).isEqualTo(ScheduledJobRunStatus.FAILED);
        assertThat(row.getErrorMessage()).isEqualTo("DB unavailable");
        assertThat(row.getDurationMs()).isZero();
    }
}
