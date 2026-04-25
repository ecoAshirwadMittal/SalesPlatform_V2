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
 * JPA entity for {@code auctions.round3_buyer_data_reports} (V62).
 * Per-buyer Round 3 submission audit row, sourced from Mendix
 * {@code auctionui$roundthreebuyersdatareport}.
 *
 * <p>Read-only from the admin surface today — Round 3 init writes these
 * rows server-side; the admin UI only browses them by week.
 */
@Entity
@Table(name = "round3_buyer_data_reports", schema = "auctions")
public class Round3BuyerDataReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id", unique = true)
    private Long legacyId;

    /**
     * Parent auction. Collapsed from the legacy
     * {@code auctionui$roundthreebuyersdatareport_auction} junction; sparse
     * in source data (740/1958), so nullable on the entity.
     */
    @Column(name = "auction_id")
    private Long auctionId;

    @Column(name = "buyer_code", length = 100)
    private String buyerCode;

    @Column(name = "company_name", length = 500)
    private String companyName;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Column(name = "total_payout", precision = 16, scale = 2)
    private BigDecimal totalPayout;

    @Column(name = "report_json", columnDefinition = "TEXT")
    private String reportJson;

    @Column(name = "submitted_datetime")
    private Instant submittedDatetime;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate = Instant.now();

    @Column(name = "changed_date", nullable = false)
    private Instant changedDate = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLegacyId() { return legacyId; }
    public void setLegacyId(Long legacyId) { this.legacyId = legacyId; }

    public Long getAuctionId() { return auctionId; }
    public void setAuctionId(Long auctionId) { this.auctionId = auctionId; }

    public String getBuyerCode() { return buyerCode; }
    public void setBuyerCode(String buyerCode) { this.buyerCode = buyerCode; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Integer getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Integer totalQuantity) { this.totalQuantity = totalQuantity; }

    public BigDecimal getTotalPayout() { return totalPayout; }
    public void setTotalPayout(BigDecimal totalPayout) { this.totalPayout = totalPayout; }

    public String getReportJson() { return reportJson; }
    public void setReportJson(String reportJson) { this.reportJson = reportJson; }

    public Instant getSubmittedDatetime() { return submittedDatetime; }
    public void setSubmittedDatetime(Instant submittedDatetime) {
        this.submittedDatetime = submittedDatetime;
    }

    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant createdDate) { this.createdDate = createdDate; }

    public Instant getChangedDate() { return changedDate; }
    public void setChangedDate(Instant changedDate) { this.changedDate = changedDate; }
}
