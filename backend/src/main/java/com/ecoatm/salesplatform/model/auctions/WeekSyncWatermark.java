package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * Per-(week, source) watermark tracking the last Snowflake (or other upstream)
 * upload that has been pulled into {@code auctions.aggregated_inventory}.
 * <p>
 * Replaces the Mendix {@code AggregatedInventoryTotals.MaxUploadTime} field with
 * a multi-source-aware composite key so future upstream feeds (e.g. MANUAL,
 * alternate marts) can coexist without stomping on each other.
 * <p>
 * The composite primary key {@code (week_id, source)} is modeled via
 * {@link IdClass} to keep both id columns as plain scalar fields — consistent
 * with the sibling entities in this package that avoid {@code @EmbeddedId}.
 * <p>
 * Timestamp columns map to {@link Instant} to match the rest of the
 * {@code model} package (see {@code AggregatedInventory}, {@code Week}). The
 * underlying column type is {@code TIMESTAMPTZ}, which round-trips cleanly
 * with {@code Instant}.
 */
@Entity
@Table(name = "week_sync_watermark", schema = "auctions")
@IdClass(WeekSyncWatermark.Key.class)
public class WeekSyncWatermark {

    @Id
    @Column(name = "week_id", nullable = false)
    private Long weekId;

    @Id
    @Column(name = "source", nullable = false, length = 32)
    private String source;

    @Column(name = "last_source_upload_at", nullable = false)
    private Instant lastSourceUploadAt;

    @Column(name = "last_synced_at", nullable = false)
    private Instant lastSyncedAt;

    @Column(name = "row_count", nullable = false)
    private int rowCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        // Fill in audit timestamps if the caller didn't supply them. The DB has
        // DEFAULT NOW() on these columns too, but populating here keeps the
        // in-memory entity consistent with the persisted row without a
        // round-trip refresh.
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (lastSyncedAt == null) {
            lastSyncedAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        // lastSyncedAt is caller-managed on update (sync service sets it
        // alongside row_count / last_source_upload_at). Only updatedAt is
        // auto-maintained here.
        updatedAt = Instant.now();
    }

    public Long getWeekId() {
        return weekId;
    }

    public void setWeekId(Long weekId) {
        this.weekId = weekId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Instant getLastSourceUploadAt() {
        return lastSourceUploadAt;
    }

    public void setLastSourceUploadAt(Instant lastSourceUploadAt) {
        this.lastSourceUploadAt = lastSourceUploadAt;
    }

    public Instant getLastSyncedAt() {
        return lastSyncedAt;
    }

    public void setLastSyncedAt(Instant lastSyncedAt) {
        this.lastSyncedAt = lastSyncedAt;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Composite primary key for {@link WeekSyncWatermark}. Required by
     * {@link IdClass} — field names and types must match the {@code @Id}
     * fields on the owning entity exactly.
     */
    public static class Key implements Serializable {

        private static final long serialVersionUID = 1L;

        private Long weekId;
        private String source;

        public Key() {
        }

        public Key(Long weekId, String source) {
            this.weekId = weekId;
            this.source = source;
        }

        public Long getWeekId() {
            return weekId;
        }

        public String getSource() {
            return source;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Key other)) {
                return false;
            }
            return Objects.equals(weekId, other.weekId)
                    && Objects.equals(source, other.source);
        }

        @Override
        public int hashCode() {
            return Objects.hash(weekId, source);
        }
    }
}
