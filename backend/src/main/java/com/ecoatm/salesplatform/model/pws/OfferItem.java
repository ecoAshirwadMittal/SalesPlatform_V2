package com.ecoatm.salesplatform.model.pws;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "offer_item", schema = "pws")
public class OfferItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id", unique = true)
    private Long legacyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;

    private String sku;

    @Column(name = "device_id")
    private Long deviceId;

    private Integer quantity;
    private BigDecimal price;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "counter_qty")
    private Integer counterQty;

    @Column(name = "counter_price")
    private BigDecimal counterPrice;

    @Column(name = "counter_total")
    private BigDecimal counterTotal;

    @Column(name = "item_status")
    private String itemStatus;

    @Column(name = "offer_drawer_status")
    private String offerDrawerStatus;

    @Column(name = "buyer_counter_status")
    private String buyerCounterStatus;

    @Column(name = "case_lot_id")
    private Long caseLotId;

    @Column(name = "final_offer_price")
    private BigDecimal finalOfferPrice;

    @Column(name = "final_offer_quantity")
    private Integer finalOfferQuantity;

    @Column(name = "final_offer_total_price")
    private BigDecimal finalOfferTotalPrice;

    @Column(name = "counter_case_price_total")
    private BigDecimal counterCasePriceTotal;

    @Column(name = "shipped_qty")
    private Integer shippedQty;

    @Column(name = "shipped_price")
    private BigDecimal shippedPrice;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

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

    public Offer getOffer() { return offer; }
    public void setOffer(Offer offer) { this.offer = offer; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public Integer getCounterQty() { return counterQty; }
    public void setCounterQty(Integer counterQty) { this.counterQty = counterQty; }

    public BigDecimal getCounterPrice() { return counterPrice; }
    public void setCounterPrice(BigDecimal counterPrice) { this.counterPrice = counterPrice; }

    public BigDecimal getCounterTotal() { return counterTotal; }
    public void setCounterTotal(BigDecimal counterTotal) { this.counterTotal = counterTotal; }

    public String getItemStatus() { return itemStatus; }
    public void setItemStatus(String itemStatus) { this.itemStatus = itemStatus; }

    public String getOfferDrawerStatus() { return offerDrawerStatus; }
    public void setOfferDrawerStatus(String offerDrawerStatus) { this.offerDrawerStatus = offerDrawerStatus; }

    public String getBuyerCounterStatus() { return buyerCounterStatus; }
    public void setBuyerCounterStatus(String buyerCounterStatus) { this.buyerCounterStatus = buyerCounterStatus; }

    public Long getCaseLotId() { return caseLotId; }
    public void setCaseLotId(Long caseLotId) { this.caseLotId = caseLotId; }

    public BigDecimal getFinalOfferPrice() { return finalOfferPrice; }
    public void setFinalOfferPrice(BigDecimal finalOfferPrice) { this.finalOfferPrice = finalOfferPrice; }

    public Integer getFinalOfferQuantity() { return finalOfferQuantity; }
    public void setFinalOfferQuantity(Integer finalOfferQuantity) { this.finalOfferQuantity = finalOfferQuantity; }

    public BigDecimal getFinalOfferTotalPrice() { return finalOfferTotalPrice; }
    public void setFinalOfferTotalPrice(BigDecimal finalOfferTotalPrice) { this.finalOfferTotalPrice = finalOfferTotalPrice; }

    public BigDecimal getCounterCasePriceTotal() { return counterCasePriceTotal; }
    public void setCounterCasePriceTotal(BigDecimal counterCasePriceTotal) { this.counterCasePriceTotal = counterCasePriceTotal; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }

    public Integer getShippedQty() { return shippedQty; }
    public void setShippedQty(Integer shippedQty) { this.shippedQty = shippedQty; }

    public BigDecimal getShippedPrice() { return shippedPrice; }
    public void setShippedPrice(BigDecimal shippedPrice) { this.shippedPrice = shippedPrice; }
}
