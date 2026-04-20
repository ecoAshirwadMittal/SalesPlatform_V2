package com.ecoatm.salesplatform.model.pws;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "offer", schema = "pws")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id", unique = true)
    private Long legacyId;

    @Column(name = "offer_type", nullable = false)
    private String offerType;

    @Column(nullable = false)
    private String status;

    @Column(name = "total_qty")
    private Integer totalQty;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "offer_number")
    private String offerNumber;

    @Column(name = "buyer_code_id")
    private Long buyerCodeId;

    @Column(name = "sales_rep_id")
    private Long salesRepId;

    @Column(name = "submission_date")
    private LocalDateTime submissionDate;

    @Column(name = "sales_review_completed_on")
    private LocalDateTime salesReviewCompletedOn;

    @Column(name = "canceled_on")
    private LocalDateTime canceledOn;

    @Column(name = "final_offer_submitted_on")
    private LocalDateTime finalOfferSubmittedOn;

    @Column(name = "counter_offer_total_sku")
    private Integer counterOfferTotalSku;

    @Column(name = "counter_offer_total_qty")
    private Integer counterOfferTotalQty;

    @Column(name = "counter_offer_total_price")
    private BigDecimal counterOfferTotalPrice;

    @Column(name = "final_offer_total_sku")
    private Integer finalOfferTotalSku;

    @Column(name = "final_offer_total_qty")
    private Integer finalOfferTotalQty;

    @Column(name = "final_offer_total_price")
    private BigDecimal finalOfferTotalPrice;

    @Column(name = "counter_response_submitted_on")
    private LocalDateTime counterResponseSubmittedOn;

    @Column(name = "visible_in_history", nullable = false)
    private boolean visibleInHistory = true;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "changed_by")
    private String changedBy;

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfferItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLegacyId() { return legacyId; }
    public void setLegacyId(Long legacyId) { this.legacyId = legacyId; }

    public String getOfferType() { return offerType; }
    public void setOfferType(String offerType) { this.offerType = offerType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getTotalQty() { return totalQty; }
    public void setTotalQty(Integer totalQty) { this.totalQty = totalQty; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public String getOfferNumber() { return offerNumber; }
    public void setOfferNumber(String offerNumber) { this.offerNumber = offerNumber; }

    public Long getBuyerCodeId() { return buyerCodeId; }
    public void setBuyerCodeId(Long buyerCodeId) { this.buyerCodeId = buyerCodeId; }

    public Long getSalesRepId() { return salesRepId; }
    public void setSalesRepId(Long salesRepId) { this.salesRepId = salesRepId; }

    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate; }

    public LocalDateTime getSalesReviewCompletedOn() { return salesReviewCompletedOn; }
    public void setSalesReviewCompletedOn(LocalDateTime salesReviewCompletedOn) { this.salesReviewCompletedOn = salesReviewCompletedOn; }

    public LocalDateTime getCanceledOn() { return canceledOn; }
    public void setCanceledOn(LocalDateTime canceledOn) { this.canceledOn = canceledOn; }

    public LocalDateTime getFinalOfferSubmittedOn() { return finalOfferSubmittedOn; }
    public void setFinalOfferSubmittedOn(LocalDateTime finalOfferSubmittedOn) { this.finalOfferSubmittedOn = finalOfferSubmittedOn; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }

    public Integer getCounterOfferTotalSku() { return counterOfferTotalSku; }
    public void setCounterOfferTotalSku(Integer counterOfferTotalSku) { this.counterOfferTotalSku = counterOfferTotalSku; }

    public Integer getCounterOfferTotalQty() { return counterOfferTotalQty; }
    public void setCounterOfferTotalQty(Integer counterOfferTotalQty) { this.counterOfferTotalQty = counterOfferTotalQty; }

    public BigDecimal getCounterOfferTotalPrice() { return counterOfferTotalPrice; }
    public void setCounterOfferTotalPrice(BigDecimal counterOfferTotalPrice) { this.counterOfferTotalPrice = counterOfferTotalPrice; }

    public Integer getFinalOfferTotalSku() { return finalOfferTotalSku; }
    public void setFinalOfferTotalSku(Integer finalOfferTotalSku) { this.finalOfferTotalSku = finalOfferTotalSku; }

    public Integer getFinalOfferTotalQty() { return finalOfferTotalQty; }
    public void setFinalOfferTotalQty(Integer finalOfferTotalQty) { this.finalOfferTotalQty = finalOfferTotalQty; }

    public BigDecimal getFinalOfferTotalPrice() { return finalOfferTotalPrice; }
    public void setFinalOfferTotalPrice(BigDecimal finalOfferTotalPrice) { this.finalOfferTotalPrice = finalOfferTotalPrice; }

    public LocalDateTime getCounterResponseSubmittedOn() { return counterResponseSubmittedOn; }
    public void setCounterResponseSubmittedOn(LocalDateTime counterResponseSubmittedOn) { this.counterResponseSubmittedOn = counterResponseSubmittedOn; }

    public boolean isVisibleInHistory() { return visibleInHistory; }
    public void setVisibleInHistory(boolean visibleInHistory) { this.visibleInHistory = visibleInHistory; }

    public List<OfferItem> getItems() { return items; }
    public void setItems(List<OfferItem> items) { this.items = items; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
}
