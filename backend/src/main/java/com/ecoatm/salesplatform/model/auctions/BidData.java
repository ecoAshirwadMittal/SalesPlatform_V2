package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "bid_data", schema = "auctions")
public class BidData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bid_round_id", nullable = false)
    private Long bidRoundId;

    @Column(name = "buyer_code_id", nullable = false)
    private Long buyerCodeId;

    @Column(name = "ecoid")
    private String ecoid;

    @Column(name = "merged_grade")
    private String mergedGrade;

    @Column(name = "buyer_code_type")
    private String buyerCodeType;

    @Column(name = "bid_quantity")
    private Integer bidQuantity;

    @Column(name = "bid_amount")
    private BigDecimal bidAmount;

    @Column(name = "target_price")
    private BigDecimal targetPrice;

    @Column(name = "maximum_quantity")
    private Integer maximumQuantity;

    @Column(name = "payout")
    private BigDecimal payout;

    @Column(name = "submitted_bid_quantity")
    private Integer submittedBidQuantity;

    @Column(name = "submitted_bid_amount")
    private BigDecimal submittedBidAmount;

    @Column(name = "last_valid_bid_quantity")
    private Integer lastValidBidQuantity;

    @Column(name = "last_valid_bid_amount")
    private BigDecimal lastValidBidAmount;

    @Column(name = "submitted_datetime")
    private Instant submittedDatetime;

    @Column(name = "changed_date")
    private Instant changedDate;

    @Column(name = "changed_by_id")
    private Long changedById;

    /**
     * Soft-delete flag — admin "Remove" actions flip this to TRUE rather than
     * dropping the row, preserving audit / replay (P8 Lane 3A). The schema
     * column is declared NOT NULL DEFAULT false in V61 so older entity-side
     * inserts (which never set this column) still succeed.
     */
    @Column(name = "is_deprecated", nullable = false)
    private boolean deprecated = false;

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBidRoundId() {
        return bidRoundId;
    }

    public void setBidRoundId(Long bidRoundId) {
        this.bidRoundId = bidRoundId;
    }

    public Long getBuyerCodeId() {
        return buyerCodeId;
    }

    public void setBuyerCodeId(Long buyerCodeId) {
        this.buyerCodeId = buyerCodeId;
    }

    public String getEcoid() {
        return ecoid;
    }

    public void setEcoid(String ecoid) {
        this.ecoid = ecoid;
    }

    public String getMergedGrade() {
        return mergedGrade;
    }

    public void setMergedGrade(String mergedGrade) {
        this.mergedGrade = mergedGrade;
    }

    public String getBuyerCodeType() {
        return buyerCodeType;
    }

    public void setBuyerCodeType(String buyerCodeType) {
        this.buyerCodeType = buyerCodeType;
    }

    public Integer getBidQuantity() {
        return bidQuantity;
    }

    public void setBidQuantity(Integer bidQuantity) {
        this.bidQuantity = bidQuantity;
    }

    public BigDecimal getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(BigDecimal bidAmount) {
        this.bidAmount = bidAmount;
    }

    public BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(BigDecimal targetPrice) {
        this.targetPrice = targetPrice;
    }

    public Integer getMaximumQuantity() {
        return maximumQuantity;
    }

    public void setMaximumQuantity(Integer maximumQuantity) {
        this.maximumQuantity = maximumQuantity;
    }

    public BigDecimal getPayout() {
        return payout;
    }

    public void setPayout(BigDecimal payout) {
        this.payout = payout;
    }

    public Integer getSubmittedBidQuantity() {
        return submittedBidQuantity;
    }

    public void setSubmittedBidQuantity(Integer submittedBidQuantity) {
        this.submittedBidQuantity = submittedBidQuantity;
    }

    public BigDecimal getSubmittedBidAmount() {
        return submittedBidAmount;
    }

    public void setSubmittedBidAmount(BigDecimal submittedBidAmount) {
        this.submittedBidAmount = submittedBidAmount;
    }

    public Integer getLastValidBidQuantity() {
        return lastValidBidQuantity;
    }

    public void setLastValidBidQuantity(Integer lastValidBidQuantity) {
        this.lastValidBidQuantity = lastValidBidQuantity;
    }

    public BigDecimal getLastValidBidAmount() {
        return lastValidBidAmount;
    }

    public void setLastValidBidAmount(BigDecimal lastValidBidAmount) {
        this.lastValidBidAmount = lastValidBidAmount;
    }

    public Instant getSubmittedDatetime() {
        return submittedDatetime;
    }

    public void setSubmittedDatetime(Instant submittedDatetime) {
        this.submittedDatetime = submittedDatetime;
    }

    public Instant getChangedDate() {
        return changedDate;
    }

    public void setChangedDate(Instant changedDate) {
        this.changedDate = changedDate;
    }

    public Long getChangedById() {
        return changedById;
    }

    public void setChangedById(Long changedById) {
        this.changedById = changedById;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BidData other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
