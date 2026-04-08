package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Single row in the PWSOffers data grid.
 * Includes offer fields + joined buyer/order info.
 */
public class OfferListItem {

    private Long offerId;
    private String offerNumber;
    private String status;
    private String orderNumber;
    private String buyerName;
    private String buyerCode;
    private String salesRepName;
    private Integer totalSkus;
    private Integer totalQty;
    private BigDecimal totalPrice;
    private LocalDateTime submissionDate;
    private LocalDateTime updatedDate;

    // Getters and setters
    public Long getOfferId() { return offerId; }
    public void setOfferId(Long offerId) { this.offerId = offerId; }

    public String getOfferNumber() { return offerNumber; }
    public void setOfferNumber(String offerNumber) { this.offerNumber = offerNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getBuyerCode() { return buyerCode; }
    public void setBuyerCode(String buyerCode) { this.buyerCode = buyerCode; }

    public String getSalesRepName() { return salesRepName; }
    public void setSalesRepName(String salesRepName) { this.salesRepName = salesRepName; }

    public Integer getTotalSkus() { return totalSkus; }
    public void setTotalSkus(Integer totalSkus) { this.totalSkus = totalSkus; }

    public Integer getTotalQty() { return totalQty; }
    public void setTotalQty(Integer totalQty) { this.totalQty = totalQty; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
}
