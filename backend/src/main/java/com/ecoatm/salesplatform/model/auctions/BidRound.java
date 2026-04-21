package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Minimal JPA projection of {@code auctions.bid_rounds}.
 *
 * <p>Only the columns we actually query on are mapped — we need this entity
 * today solely to gate the Save Schedule flow (reject reschedule when bids
 * already exist for a parent round). The full Mendix {@code BidRound}
 * surface (submission state, SharePoint upload flags, etc.) will be mapped
 * by the Bidding module when that port lands.
 */
@Entity
@Table(name = "bid_rounds", schema = "auctions")
public class BidRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheduling_auction_id", nullable = false)
    private Long schedulingAuctionId;

    public Long getId() {
        return id;
    }

    public Long getSchedulingAuctionId() {
        return schedulingAuctionId;
    }

    public void setSchedulingAuctionId(Long schedulingAuctionId) {
        this.schedulingAuctionId = schedulingAuctionId;
    }
}
