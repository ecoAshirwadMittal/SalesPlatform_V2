package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.mdm.Device;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Read-only projection of a Device for API responses.
 * Flattens lookup entity names so the consumer doesn't need to resolve IDs.
 */
public class DeviceResponse {

    private Long id;
    private String sku;
    private String deviceCode;
    private String description;

    private BigDecimal listPrice;
    private BigDecimal minPrice;
    private Integer availableQty;
    private Integer reservedQty;
    private Integer atpQty;
    private BigDecimal weight;
    private String itemType;
    private Boolean isActive;

    private String brandName;
    private String categoryName;
    private String modelName;
    private String conditionName;
    private String capacityName;
    private String carrierName;
    private String colorName;
    private String gradeName;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public static DeviceResponse fromEntity(Device d) {
        DeviceResponse r = new DeviceResponse();
        r.id = d.getId();
        r.sku = d.getSku();
        r.deviceCode = d.getDeviceCode();
        r.description = d.getDescription();
        r.listPrice = d.getListPrice();
        r.minPrice = d.getMinPrice();
        r.availableQty = d.getAvailableQty();
        r.reservedQty = d.getReservedQty();
        r.atpQty = d.getAtpQty();
        r.weight = d.getWeight();
        r.itemType = d.getItemType();
        r.isActive = d.getIsActive();
        r.createdDate = d.getCreatedDate();
        r.updatedDate = d.getUpdatedDate();

        if (d.getBrand() != null)     r.brandName     = d.getBrand().getDisplayName();
        if (d.getCategory() != null)  r.categoryName  = d.getCategory().getDisplayName();
        if (d.getModel() != null)     r.modelName     = d.getModel().getDisplayName();
        if (d.getCondition() != null) r.conditionName = d.getCondition().getDisplayName();
        if (d.getCapacity() != null)  r.capacityName  = d.getCapacity().getDisplayName();
        if (d.getCarrier() != null)   r.carrierName   = d.getCarrier().getDisplayName();
        if (d.getColor() != null)     r.colorName     = d.getColor().getDisplayName();
        if (d.getGrade() != null)     r.gradeName     = d.getGrade().getDisplayName();

        return r;
    }

    // Getters
    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getDeviceCode() { return deviceCode; }
    public String getDescription() { return description; }
    public BigDecimal getListPrice() { return listPrice; }
    public BigDecimal getMinPrice() { return minPrice; }
    public Integer getAvailableQty() { return availableQty; }
    public Integer getReservedQty() { return reservedQty; }
    public Integer getAtpQty() { return atpQty; }
    public BigDecimal getWeight() { return weight; }
    public String getItemType() { return itemType; }
    public Boolean getIsActive() { return isActive; }
    public String getBrandName() { return brandName; }
    public String getCategoryName() { return categoryName; }
    public String getModelName() { return modelName; }
    public String getConditionName() { return conditionName; }
    public String getCapacityName() { return capacityName; }
    public String getCarrierName() { return carrierName; }
    public String getColorName() { return colorName; }
    public String getGradeName() { return gradeName; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public LocalDateTime getUpdatedDate() { return updatedDate; }
}
