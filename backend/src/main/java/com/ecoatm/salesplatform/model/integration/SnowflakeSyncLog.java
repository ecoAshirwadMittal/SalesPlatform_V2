package com.ecoatm.salesplatform.model.integration;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * JPA entity over {@code integration.snowflake_sync_log}.
 *
 * <p>Each row records one invocation of
 * {@code AggregatedInventorySnowflakeSyncService.syncWeek} (or any future
 * Snowflake-backed sync). The service writes the row in three visible steps
 * via {@code SyncLogWriter}:
 * <ol>
 *   <li>{@code createStarted} — inserts STARTED with {@code startedAt = now}
 *       and null {@code finishedAt}.</li>
 *   <li>{@code markCompleted} / {@code markSkippedUpToDate} / {@code markFailed} /
 *       {@code writeSkippedLocked} — sets the terminal status and
 *       {@code finishedAt}.</li>
 * </ol>
 *
 * <p>All log writes run in a {@code REQUIRES_NEW} transaction so that a
 * rolled-back outer sync transaction still leaves the FAILED row behind as
 * evidence. See
 * {@code docs/tasks/aggregated-inventory-snowflake-sync-plan.md} §3.3.
 *
 * <p>V67 declares both timestamp columns as plain {@code TIMESTAMP} (no zone).
 * Combined with Spring's {@code spring.jpa.properties.hibernate.jdbc.time_zone=UTC}
 * setting in {@code application.yml}, {@link Instant} round-trips cleanly via
 * UTC — no manual conversion is required at the entity boundary.
 */
@Entity
@Table(name = "snowflake_sync_log", schema = "integration")
public class SnowflakeSyncLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sync_type", nullable = false, length = 64)
    private String syncType;

    @Column(name = "target_key", length = 128)
    private String targetKey;

    @Column(name = "status", nullable = false, length = 32)
    private String status;

    @Column(name = "rows_read", nullable = false)
    private int rowsRead;

    @Column(name = "rows_upserted", nullable = false)
    private int rowsUpserted;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSyncType() {
        return syncType;
    }

    public void setSyncType(String syncType) {
        this.syncType = syncType;
    }

    public String getTargetKey() {
        return targetKey;
    }

    public void setTargetKey(String targetKey) {
        this.targetKey = targetKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRowsRead() {
        return rowsRead;
    }

    public void setRowsRead(int rowsRead) {
        this.rowsRead = rowsRead;
    }

    public int getRowsUpserted() {
        return rowsUpserted;
    }

    public void setRowsUpserted(int rowsUpserted) {
        this.rowsUpserted = rowsUpserted;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }
}
