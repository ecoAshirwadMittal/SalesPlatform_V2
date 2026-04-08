package com.ecoatm.salesplatform.dto;

public class RmaSummaryResponse {

    private String status;
    private String displayLabel;
    private long rmaCount;
    private long totalPrice;
    private long totalSkus;
    private long totalQty;

    public RmaSummaryResponse(String status, String displayLabel, long rmaCount,
                               long totalPrice, long totalSkus, long totalQty) {
        this.status = status;
        this.displayLabel = displayLabel;
        this.rmaCount = rmaCount;
        this.totalPrice = totalPrice;
        this.totalSkus = totalSkus;
        this.totalQty = totalQty;
    }

    public String getStatus() { return status; }
    public String getDisplayLabel() { return displayLabel; }
    public long getRmaCount() { return rmaCount; }
    public long getTotalPrice() { return totalPrice; }
    public long getTotalSkus() { return totalSkus; }
    public long getTotalQty() { return totalQty; }
}
