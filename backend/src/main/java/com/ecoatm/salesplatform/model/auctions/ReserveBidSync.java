package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "reserve_bid_sync", schema = "auctions")
public class ReserveBidSync {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id", unique = true) private Long legacyId;
    @Column(name = "last_sync_datetime")       private Instant lastSyncDatetime;

    @Column(name = "created_date", nullable = false) private Instant createdDate = Instant.now();
    @Column(name = "changed_date", nullable = false) private Instant changedDate = Instant.now();
    @Column(name = "owner_id")       private Long ownerId;
    @Column(name = "changed_by_id")  private Long changedById;

    public Long getId() { return id; }
    public void setId(Long v) { this.id = v; }
    public Long getLegacyId() { return legacyId; }
    public void setLegacyId(Long v) { this.legacyId = v; }
    public Instant getLastSyncDatetime() { return lastSyncDatetime; }
    public void setLastSyncDatetime(Instant v) { this.lastSyncDatetime = v; }
    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant v) { this.createdDate = v; }
    public Instant getChangedDate() { return changedDate; }
    public void setChangedDate(Instant v) { this.changedDate = v; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long v) { this.ownerId = v; }
    public Long getChangedById() { return changedById; }
    public void setChangedById(Long v) { this.changedById = v; }
}
