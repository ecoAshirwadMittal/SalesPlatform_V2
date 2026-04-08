package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.pws.Offer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OfferResponse {

    private Long offerId;
    private String offerNumber;
    private String status;
    private Long buyerCodeId;
    private Integer totalSkus;
    private Integer totalQty;
    private BigDecimal totalPrice;
    private List<OfferItemResponse> items;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    // Counter offer summary cards
    private Integer counterOfferTotalSkus;
    private Integer counterOfferTotalQty;
    private BigDecimal counterOfferTotalPrice;
    private Integer finalOfferTotalSkus;
    private Integer finalOfferTotalQty;
    private BigDecimal finalOfferTotalPrice;

    public static OfferResponse fromEntity(Offer offer, List<OfferItemResponse> items) {
        OfferResponse r = new OfferResponse();
        r.offerId = offer.getId();
        r.offerNumber = offer.getOfferNumber();
        r.status = offer.getStatus();
        r.buyerCodeId = offer.getBuyerCodeId();
        r.totalQty = offer.getTotalQty() != null ? offer.getTotalQty() : 0;
        r.totalPrice = offer.getTotalPrice() != null ? offer.getTotalPrice() : BigDecimal.ZERO;
        r.totalSkus = items != null ? (int) items.stream()
                .filter(i -> i.getQuantity() != null && i.getQuantity() > 0).count() : 0;
        r.items = items;
        r.createdDate = offer.getCreatedDate();
        r.updatedDate = offer.getUpdatedDate();
        r.counterOfferTotalSkus = offer.getCounterOfferTotalSku();
        r.counterOfferTotalQty = offer.getCounterOfferTotalQty();
        r.counterOfferTotalPrice = offer.getCounterOfferTotalPrice();
        r.finalOfferTotalSkus = offer.getFinalOfferTotalSku();
        r.finalOfferTotalQty = offer.getFinalOfferTotalQty();
        r.finalOfferTotalPrice = offer.getFinalOfferTotalPrice();
        return r;
    }

    // Getters
    public Long getOfferId() { return offerId; }
    public String getOfferNumber() { return offerNumber; }
    public String getStatus() { return status; }
    public Long getBuyerCodeId() { return buyerCodeId; }
    public Integer getTotalSkus() { return totalSkus; }
    public Integer getTotalQty() { return totalQty; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public List<OfferItemResponse> getItems() { return items; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public Integer getCounterOfferTotalSkus() { return counterOfferTotalSkus; }
    public Integer getCounterOfferTotalQty() { return counterOfferTotalQty; }
    public BigDecimal getCounterOfferTotalPrice() { return counterOfferTotalPrice; }
    public Integer getFinalOfferTotalSkus() { return finalOfferTotalSkus; }
    public Integer getFinalOfferTotalQty() { return finalOfferTotalQty; }
    public BigDecimal getFinalOfferTotalPrice() { return finalOfferTotalPrice; }

    // Setters
    public void setOfferId(Long offerId) { this.offerId = offerId; }
    public void setOfferNumber(String offerNumber) { this.offerNumber = offerNumber; }
    public void setStatus(String status) { this.status = status; }
    public void setBuyerCodeId(Long buyerCodeId) { this.buyerCodeId = buyerCodeId; }
    public void setTotalSkus(Integer totalSkus) { this.totalSkus = totalSkus; }
    public void setTotalQty(Integer totalQty) { this.totalQty = totalQty; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public void setItems(List<OfferItemResponse> items) { this.items = items; }
}
