package com.ecoatm.salesplatform.dto;

import java.math.BigDecimal;

/**
 * DTO for creating or updating a device in the PWS inventory.
 * Lookup fields (brand, model, etc.) accept either an ID or a name string.
 * If the name doesn't exist yet, the service will auto-create the lookup entry.
 */
public class DeviceRequest {

    private String sku;
    private String deviceCode;
    private String description;

    private BigDecimal listPrice;
    private BigDecimal minPrice;
    private BigDecimal futureListPrice;
    private BigDecimal futureMinPrice;

    private Integer availableQty;
    private Integer reservedQty;
    private Integer atpQty;
    private BigDecimal weight;
    private String itemType;

    // Lookup references — pass ID or name
    private Long brandId;
    private String brandName;

    private Long categoryId;
    private String categoryName;

    private Long modelId;
    private String modelName;

    private Long conditionId;
    private String conditionName;

    private Long capacityId;
    private String capacityName;

    private Long carrierId;
    private String carrierName;

    private Long colorId;
    private String colorName;

    private Long gradeId;
    private String gradeName;

    // Getters and Setters
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getDeviceCode() { return deviceCode; }
    public void setDeviceCode(String deviceCode) { this.deviceCode = deviceCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getListPrice() { return listPrice; }
    public void setListPrice(BigDecimal listPrice) { this.listPrice = listPrice; }

    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }

    public BigDecimal getFutureListPrice() { return futureListPrice; }
    public void setFutureListPrice(BigDecimal futureListPrice) { this.futureListPrice = futureListPrice; }

    public BigDecimal getFutureMinPrice() { return futureMinPrice; }
    public void setFutureMinPrice(BigDecimal futureMinPrice) { this.futureMinPrice = futureMinPrice; }

    public Integer getAvailableQty() { return availableQty; }
    public void setAvailableQty(Integer availableQty) { this.availableQty = availableQty; }

    public Integer getReservedQty() { return reservedQty; }
    public void setReservedQty(Integer reservedQty) { this.reservedQty = reservedQty; }

    public Integer getAtpQty() { return atpQty; }
    public void setAtpQty(Integer atpQty) { this.atpQty = atpQty; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public Long getBrandId() { return brandId; }
    public void setBrandId(Long brandId) { this.brandId = brandId; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Long getModelId() { return modelId; }
    public void setModelId(Long modelId) { this.modelId = modelId; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public Long getConditionId() { return conditionId; }
    public void setConditionId(Long conditionId) { this.conditionId = conditionId; }
    public String getConditionName() { return conditionName; }
    public void setConditionName(String conditionName) { this.conditionName = conditionName; }

    public Long getCapacityId() { return capacityId; }
    public void setCapacityId(Long capacityId) { this.capacityId = capacityId; }
    public String getCapacityName() { return capacityName; }
    public void setCapacityName(String capacityName) { this.capacityName = capacityName; }

    public Long getCarrierId() { return carrierId; }
    public void setCarrierId(Long carrierId) { this.carrierId = carrierId; }
    public String getCarrierName() { return carrierName; }
    public void setCarrierName(String carrierName) { this.carrierName = carrierName; }

    public Long getColorId() { return colorId; }
    public void setColorId(Long colorId) { this.colorId = colorId; }
    public String getColorName() { return colorName; }
    public void setColorName(String colorName) { this.colorName = colorName; }

    public Long getGradeId() { return gradeId; }
    public void setGradeId(Long gradeId) { this.gradeId = gradeId; }
    public String getGradeName() { return gradeName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }
}
