package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;

public class PricingUpdateRequest {

    private Long deviceId;
    private BigDecimal futureListPrice;
    private BigDecimal futureMinPrice;

    public PricingUpdateRequest() {}

    public PricingUpdateRequest(Long deviceId, BigDecimal futureListPrice, BigDecimal futureMinPrice) {
        this.deviceId = deviceId;
        this.futureListPrice = futureListPrice;
        this.futureMinPrice = futureMinPrice;
    }

    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }

    public BigDecimal getFutureListPrice() { return futureListPrice; }
    public void setFutureListPrice(BigDecimal futureListPrice) { this.futureListPrice = futureListPrice; }

    public BigDecimal getFutureMinPrice() { return futureMinPrice; }
    public void setFutureMinPrice(BigDecimal futureMinPrice) { this.futureMinPrice = futureMinPrice; }
}
