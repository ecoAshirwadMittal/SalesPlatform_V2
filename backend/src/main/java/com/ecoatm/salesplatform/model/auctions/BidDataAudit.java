package com.ecoatm.salesplatform.model.auctions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Audit row for admin actions on {@code auctions.bid_data}. Mirrors the
 * {@link ReserveBidAudit}-style trail recommended by the EB module design:
 * a self-contained snapshot of the pre-action values so the audit row stays
 * meaningful even if the parent bid_data is later hard-deleted by a cleanup
 * job.
 */
@Entity
@Table(name = "bid_data_audit", schema = "auctions")
public class BidDataAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bid_data_id", nullable = false)
    private Long bidDataId;

    /** One of: SOFT_DELETE, RESTORE. */
    @Column(name = "action", nullable = false, length = 20)
    private String action;

    @Column(name = "bid_round_id")
    private Long bidRoundId;

    @Column(name = "buyer_code_id")
    private Long buyerCodeId;

    @Column(name = "bid_amount")
    private BigDecimal bidAmount;

    @Column(name = "bid_quantity")
    private Integer bidQuantity;

    @Column(name = "submitted_bid_amount")
    private BigDecimal submittedBidAmount;

    @Column(name = "submitted_bid_quantity")
    private Integer submittedBidQuantity;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate = Instant.now();

    @Column(name = "changed_by_id")
    private Long changedById;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBidDataId() { return bidDataId; }
    public void setBidDataId(Long v) { this.bidDataId = v; }
    public String getAction() { return action; }
    public void setAction(String v) { this.action = v; }
    public Long getBidRoundId() { return bidRoundId; }
    public void setBidRoundId(Long v) { this.bidRoundId = v; }
    public Long getBuyerCodeId() { return buyerCodeId; }
    public void setBuyerCodeId(Long v) { this.buyerCodeId = v; }
    public BigDecimal getBidAmount() { return bidAmount; }
    public void setBidAmount(BigDecimal v) { this.bidAmount = v; }
    public Integer getBidQuantity() { return bidQuantity; }
    public void setBidQuantity(Integer v) { this.bidQuantity = v; }
    public BigDecimal getSubmittedBidAmount() { return submittedBidAmount; }
    public void setSubmittedBidAmount(BigDecimal v) { this.submittedBidAmount = v; }
    public Integer getSubmittedBidQuantity() { return submittedBidQuantity; }
    public void setSubmittedBidQuantity(Integer v) { this.submittedBidQuantity = v; }
    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant v) { this.createdDate = v; }
    public Long getChangedById() { return changedById; }
    public void setChangedById(Long v) { this.changedById = v; }
}
