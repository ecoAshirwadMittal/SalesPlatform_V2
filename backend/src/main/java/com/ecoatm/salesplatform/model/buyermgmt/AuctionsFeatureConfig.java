package com.ecoatm.salesplatform.model.buyermgmt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Singleton feature-flag row for auctions behavior. Ports the Mendix
 * {@code EcoATM_BuyerManagement.AuctionsFeature} entity — one row globally,
 * accessed via
 * {@code EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig}.
 *
 * <p>Only the fields consumed by the Auction Scheduling + R1 init flows are
 * mapped: the R2/R3 minute offsets (used in schedule-defaults math), the
 * Snowflake-audit gate, and the R1 target-price floor
 * ({@code minimumAllowedBid}). Other columns (SharePoint retry count,
 * bidder filters) stay unmapped until their respective ports land.
 */
@Entity
@Table(name = "auctions_feature_config", schema = "buyer_mgmt")
public class AuctionsFeatureConfig {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "auction_round2_minutes_offset", nullable = false)
    private int auctionRound2MinutesOffset;

    @Column(name = "auction_round3_minutes_offset", nullable = false)
    private int auctionRound3MinutesOffset;

    @Column(name = "send_auction_data_to_snowflake", nullable = false)
    private boolean sendAuctionDataToSnowflake;

    @Column(name = "minimum_allowed_bid", nullable = false)
    private BigDecimal minimumAllowedBid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getAuctionRound2MinutesOffset() {
        return auctionRound2MinutesOffset;
    }

    public void setAuctionRound2MinutesOffset(int auctionRound2MinutesOffset) {
        this.auctionRound2MinutesOffset = auctionRound2MinutesOffset;
    }

    public int getAuctionRound3MinutesOffset() {
        return auctionRound3MinutesOffset;
    }

    public void setAuctionRound3MinutesOffset(int auctionRound3MinutesOffset) {
        this.auctionRound3MinutesOffset = auctionRound3MinutesOffset;
    }

    public boolean isSendAuctionDataToSnowflake() {
        return sendAuctionDataToSnowflake;
    }

    public void setSendAuctionDataToSnowflake(boolean sendAuctionDataToSnowflake) {
        this.sendAuctionDataToSnowflake = sendAuctionDataToSnowflake;
    }

    public BigDecimal getMinimumAllowedBid() {
        return minimumAllowedBid;
    }

    public void setMinimumAllowedBid(BigDecimal minimumAllowedBid) {
        this.minimumAllowedBid = minimumAllowedBid;
    }
}
