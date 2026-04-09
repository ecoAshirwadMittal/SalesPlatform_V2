package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.mdm.Device;

import java.math.BigDecimal;

public class PricingDeviceResponse {

    private Long id;
    private String sku;
    private String categoryName;
    private String brandName;
    private String modelName;
    private String carrierName;
    private String capacityName;
    private String colorName;
    private String gradeName;
    private BigDecimal currentListPrice;
    private BigDecimal futureListPrice;
    private BigDecimal currentMinPrice;
    private BigDecimal futureMinPrice;

    public static PricingDeviceResponse fromEntity(Device device) {
        PricingDeviceResponse dto = new PricingDeviceResponse();
        dto.setId(device.getId());
        dto.setSku(device.getSku());
        dto.setCategoryName(device.getCategory() != null ? device.getCategory().getDisplayName() : null);
        dto.setBrandName(device.getBrand() != null ? device.getBrand().getDisplayName() : null);
        dto.setModelName(device.getModel() != null ? device.getModel().getDisplayName() : null);
        dto.setCarrierName(device.getCarrier() != null ? device.getCarrier().getDisplayName() : null);
        dto.setCapacityName(device.getCapacity() != null ? device.getCapacity().getDisplayName() : null);
        dto.setColorName(device.getColor() != null ? device.getColor().getDisplayName() : null);
        dto.setGradeName(device.getGrade() != null ? device.getGrade().getDisplayName() : null);
        dto.setCurrentListPrice(device.getListPrice());
        dto.setFutureListPrice(device.getFutureListPrice());
        dto.setCurrentMinPrice(device.getMinPrice());
        dto.setFutureMinPrice(device.getFutureMinPrice());
        return dto;
    }

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getCarrierName() { return carrierName; }
    public void setCarrierName(String carrierName) { this.carrierName = carrierName; }

    public String getCapacityName() { return capacityName; }
    public void setCapacityName(String capacityName) { this.capacityName = capacityName; }

    public String getColorName() { return colorName; }
    public void setColorName(String colorName) { this.colorName = colorName; }

    public String getGradeName() { return gradeName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }

    public BigDecimal getCurrentListPrice() { return currentListPrice; }
    public void setCurrentListPrice(BigDecimal currentListPrice) { this.currentListPrice = currentListPrice; }

    public BigDecimal getFutureListPrice() { return futureListPrice; }
    public void setFutureListPrice(BigDecimal futureListPrice) { this.futureListPrice = futureListPrice; }

    public BigDecimal getCurrentMinPrice() { return currentMinPrice; }
    public void setCurrentMinPrice(BigDecimal currentMinPrice) { this.currentMinPrice = currentMinPrice; }

    public BigDecimal getFutureMinPrice() { return futureMinPrice; }
    public void setFutureMinPrice(BigDecimal futureMinPrice) { this.futureMinPrice = futureMinPrice; }
}
