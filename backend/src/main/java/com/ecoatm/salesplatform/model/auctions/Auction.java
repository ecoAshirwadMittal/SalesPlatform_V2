package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "auctions", schema = "auctions")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id")
    private Long legacyId;

    @Column(name = "auction_title", length = 200)
    private String auctionTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "auction_status", length = 20, nullable = false)
    private AuctionStatus auctionStatus = AuctionStatus.Unscheduled;

    @Column(name = "week_id")
    private Long weekId;

    @Column(name = "created_by", length = 200)
    private String createdBy;

    @Column(name = "updated_by", length = 200)
    private String updatedBy;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Column(name = "changed_date", nullable = false)
    private Instant changedDate;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "changed_by_id")
    private Long changedById;

    public Long getId() { return id; }

    public String getAuctionTitle() { return auctionTitle; }
    public void setAuctionTitle(String auctionTitle) { this.auctionTitle = auctionTitle; }

    public AuctionStatus getAuctionStatus() { return auctionStatus; }
    public void setAuctionStatus(AuctionStatus auctionStatus) { this.auctionStatus = auctionStatus; }

    public Long getWeekId() { return weekId; }
    public void setWeekId(Long weekId) { this.weekId = weekId; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant createdDate) { this.createdDate = createdDate; }

    public Instant getChangedDate() { return changedDate; }
    public void setChangedDate(Instant changedDate) { this.changedDate = changedDate; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public Long getChangedById() { return changedById; }
    public void setChangedById(Long changedById) { this.changedById = changedById; }
}
