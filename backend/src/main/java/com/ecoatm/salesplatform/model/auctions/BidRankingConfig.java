package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bid_ranking_config", schema = "auctions")
public class BidRankingConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id")
    private Long legacyId;

    @Column(name = "display_rank", nullable = false)
    private int displayRank;

    @Column(name = "minimum_bid", nullable = false, precision = 14, scale = 2)
    private BigDecimal minimumBid = BigDecimal.ZERO;

    @Column(name = "maximum_rank", nullable = false)
    private int maximumRank;

    @Column(name = "include_reserve_floor", nullable = false)
    private boolean includeReserveFloor = true;

    public Long getId() { return id; }
    public int getDisplayRank() { return displayRank; }
    public BigDecimal getMinimumBid() { return minimumBid; }
    public int getMaximumRank() { return maximumRank; }
    public boolean isIncludeReserveFloor() { return includeReserveFloor; }
    public void setIncludeReserveFloor(boolean v) { this.includeReserveFloor = v; }
}
