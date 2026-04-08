package com.ecoatm.salesplatform.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CartItemRequest {

    @NotBlank(message = "SKU is required")
    private String sku;

    private Long deviceId;
    private Long caseLotId;

    @NotNull(message = "Offer price is required")
    @Min(value = 0, message = "Offer price must be non-negative")
    private BigDecimal offerPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be non-negative")
    private Integer quantity;

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }

    public Long getCaseLotId() { return caseLotId; }
    public void setCaseLotId(Long caseLotId) { this.caseLotId = caseLotId; }

    public BigDecimal getOfferPrice() { return offerPrice; }
    public void setOfferPrice(BigDecimal offerPrice) { this.offerPrice = offerPrice; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
