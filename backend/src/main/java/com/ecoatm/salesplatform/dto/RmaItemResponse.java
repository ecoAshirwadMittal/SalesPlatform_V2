package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.pws.RmaItem;

import java.time.LocalDateTime;

public class RmaItemResponse {

    private Long id;
    private Long rmaId;
    private String imei;
    private String orderNumber;
    private LocalDateTime shipDate;
    private Integer salePrice;
    private String returnReason;
    private String status;
    private String statusDisplay;
    private String declineReason;
    // Device info (enriched from mdm.device)
    private String sku;
    private String deviceDescription;
    private String grade;
    private String itemType;

    public static RmaItemResponse fromEntity(RmaItem item) {
        RmaItemResponse r = new RmaItemResponse();
        r.id = item.getId();
        r.rmaId = item.getRma() != null ? item.getRma().getId() : null;
        r.imei = item.getImei();
        r.orderNumber = item.getOrderNumber();
        r.shipDate = item.getShipDate();
        r.salePrice = item.getSalePrice();
        r.returnReason = item.getReturnReason();
        r.status = item.getStatus();
        r.statusDisplay = item.getStatusDisplay();
        r.declineReason = item.getDeclineReason();
        return r;
    }

    // Getters
    public Long getId() { return id; }
    public Long getRmaId() { return rmaId; }
    public String getImei() { return imei; }
    public String getOrderNumber() { return orderNumber; }
    public LocalDateTime getShipDate() { return shipDate; }
    public Integer getSalePrice() { return salePrice; }
    public String getReturnReason() { return returnReason; }
    public String getStatus() { return status; }
    public String getStatusDisplay() { return statusDisplay; }
    public String getDeclineReason() { return declineReason; }
    public String getSku() { return sku; }
    public String getDeviceDescription() { return deviceDescription; }
    public String getGrade() { return grade; }
    public String getItemType() { return itemType; }

    // Setters for device enrichment
    public void setSku(String sku) { this.sku = sku; }
    public void setDeviceDescription(String deviceDescription) { this.deviceDescription = deviceDescription; }
    public void setGrade(String grade) { this.grade = grade; }
    public void setItemType(String itemType) { this.itemType = itemType; }
}
