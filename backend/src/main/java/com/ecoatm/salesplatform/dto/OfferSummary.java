package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;

/**
 * Status summary card data — clones Mendix DS_GetOfferSummaryByStatus.
 * Each status tab on PWSOffers page shows: count, total SKUs, total qty, total price.
 */
public class OfferSummary {

    private String status;
    private String displayLabel;
    private long offerCount;
    private long totalSkus;
    private long totalQty;
    private BigDecimal totalPrice;

    public OfferSummary(String status, String displayLabel, long offerCount,
                        long totalSkus, long totalQty, BigDecimal totalPrice) {
        this.status = status;
        this.displayLabel = displayLabel;
        this.offerCount = offerCount;
        this.totalSkus = totalSkus;
        this.totalQty = totalQty;
        this.totalPrice = totalPrice;
    }

    public String getStatus() { return status; }
    public String getDisplayLabel() { return displayLabel; }
    public long getOfferCount() { return offerCount; }
    public long getTotalSkus() { return totalSkus; }
    public long getTotalQty() { return totalQty; }
    public BigDecimal getTotalPrice() { return totalPrice; }
}
