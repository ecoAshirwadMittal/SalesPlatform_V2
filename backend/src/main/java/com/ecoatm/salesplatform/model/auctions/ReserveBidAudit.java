package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "reserve_bid_audit", schema = "auctions")
public class ReserveBidAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id", unique = true) private Long legacyId;

    @Column(name = "reserve_bid_id", nullable = false) private Long reserveBidId;

    @Column(name = "old_price", nullable = false, precision = 14, scale = 4) private BigDecimal oldPrice;
    @Column(name = "new_price", nullable = false, precision = 14, scale = 4) private BigDecimal newPrice;

    @Column(name = "created_date", nullable = false) private Instant createdDate = Instant.now();
    @Column(name = "changed_date", nullable = false) private Instant changedDate = Instant.now();
    @Column(name = "owner_id")       private Long ownerId;
    @Column(name = "changed_by_id")  private Long changedById;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getLegacyId() { return legacyId; }
    public void setLegacyId(Long v) { this.legacyId = v; }
    public Long getReserveBidId() { return reserveBidId; }
    public void setReserveBidId(Long v) { this.reserveBidId = v; }
    public BigDecimal getOldPrice() { return oldPrice; }
    public void setOldPrice(BigDecimal v) { this.oldPrice = v; }
    public BigDecimal getNewPrice() { return newPrice; }
    public void setNewPrice(BigDecimal v) { this.newPrice = v; }
    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant v) { this.createdDate = v; }
    public Instant getChangedDate() { return changedDate; }
    public void setChangedDate(Instant v) { this.changedDate = v; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long v) { this.ownerId = v; }
    public Long getChangedById() { return changedById; }
    public void setChangedById(Long v) { this.changedById = v; }
}
