package com.ecoatm.salesplatform.service.auctions;

import com.ecoatm.salesplatform.model.integration.SnowflakeSyncLog;
import com.ecoatm.salesplatform.repository.integration.SnowflakeSyncLogRepository;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Collaborator that persists {@code integration.snowflake_sync_log} rows on
 * an independent transaction so audit entries survive a rolled-back sync.
 *
 * <p>Each mutating method is annotated
 * {@code @Transactional(propagation = REQUIRES_NEW)} so that when the outer
 * sync transaction throws and rolls back the data-upsert batch, the
 * {@code STARTED → FAILED} log transition is still committed. Without this
 * split the sync would be silent on failures — the operator would see a
 * rolled-back upsert and no breadcrumb explaining why.
 *
 * <p>Status strings use the widened taxonomy from
 * {@code V69__integration_snowflake_sync_log_widen_status.sql}.
 */
@Component
class SyncLogWriter {

    /** Status string values — mirror the V69 CHECK constraint. */
    static final String STATUS_STARTED = "STARTED";
    static final String STATUS_COMPLETED = "COMPLETED";
    static final String STATUS_FAILED = "FAILED";
    static final String STATUS_SKIPPED_LOCKED = "SKIPPED_LOCKED";
    static final String STATUS_SKIPPED_UP_TO_DATE = "SKIPPED_UP_TO_DATE";

    /** Safety cap on error_message length to match the TEXT column's realistic bounds. */
    private static final int ERROR_MESSAGE_MAX_LEN = 1000;

    private final SnowflakeSyncLogRepository repository;

    SyncLogWriter(SnowflakeSyncLogRepository repository) {
        this.repository = repository;
    }

    /**
     * Insert a {@code STARTED} log row for the given sync + target. Returns
     * the generated id so the service can flip the status later.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long createStarted(String syncType, String targetKey) {
        SnowflakeSyncLog row = new SnowflakeSyncLog();
        row.setSyncType(syncType);
        row.setTargetKey(targetKey);
        row.setStatus(STATUS_STARTED);
        row.setRowsRead(0);
        row.setRowsUpserted(0);
        row.setStartedAt(Instant.now());
        return repository.save(row).getId();
    }

    /**
     * Single-insert log row for the lock-guard path. No prior STARTED row is
     * written because the service decides to skip before creating one.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long writeSkippedLocked(String syncType, String targetKey) {
        Instant now = Instant.now();
        SnowflakeSyncLog row = new SnowflakeSyncLog();
        row.setSyncType(syncType);
        row.setTargetKey(targetKey);
        row.setStatus(STATUS_SKIPPED_LOCKED);
        row.setRowsRead(0);
        row.setRowsUpserted(0);
        row.setStartedAt(now);
        row.setFinishedAt(now);
        return repository.save(row).getId();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markCompleted(Long logId, int rowsRead, int rowsUpserted) {
        SnowflakeSyncLog row = repository.findById(logId).orElseThrow(
                () -> new IllegalStateException("sync_log row missing id=" + logId));
        row.setStatus(STATUS_COMPLETED);
        row.setRowsRead(rowsRead);
        row.setRowsUpserted(rowsUpserted);
        row.setFinishedAt(Instant.now());
        repository.save(row);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSkippedUpToDate(Long logId) {
        SnowflakeSyncLog row = repository.findById(logId).orElseThrow(
                () -> new IllegalStateException("sync_log row missing id=" + logId));
        row.setStatus(STATUS_SKIPPED_UP_TO_DATE);
        row.setFinishedAt(Instant.now());
        repository.save(row);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(Long logId, String errorMessage) {
        SnowflakeSyncLog row = repository.findById(logId).orElseThrow(
                () -> new IllegalStateException("sync_log row missing id=" + logId));
        row.setStatus(STATUS_FAILED);
        row.setErrorMessage(truncate(errorMessage));
        row.setFinishedAt(Instant.now());
        repository.save(row);
    }

    private static String truncate(String value) {
        if (value == null) {
            return null;
        }
        return value.length() <= ERROR_MESSAGE_MAX_LEN
                ? value
                : value.substring(0, ERROR_MESSAGE_MAX_LEN);
    }
}
