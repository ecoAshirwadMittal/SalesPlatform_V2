package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.model.pws.CaseLot;

import java.math.BigDecimal;

public class CaseLotResponse {

    private Long id;
    private Long deviceId;
    private String caseLotId;
    private String sku;
    private String modelName;
    private String carrierName;
    private String capacityName;
    private String colorName;
    private String gradeName;
    private Integer caseLotSize;
    private Integer caseLotAtpQty;
    private BigDecimal unitPrice;
    private BigDecimal caseLotPrice;

    public static CaseLotResponse fromEntity(CaseLot cl) {
        CaseLotResponse r = new CaseLotResponse();
        r.id = cl.getId();
        r.caseLotId = cl.getCaseLotId();
        r.caseLotSize = cl.getCaseLotSize();
        r.caseLotAtpQty = cl.getCaseLotAtpQty();
        r.caseLotPrice = cl.getCaseLotPrice();

        Device d = cl.getDevice();
        if (d != null) {
            r.deviceId = d.getId();
            r.sku = d.getSku();
            r.unitPrice = d.getListPrice();
            if (d.getModel() != null)    r.modelName    = d.getModel().getDisplayName();
            if (d.getCarrier() != null)  r.carrierName  = d.getCarrier().getDisplayName();
            if (d.getCapacity() != null) r.capacityName = d.getCapacity().getDisplayName();
            if (d.getColor() != null)    r.colorName    = d.getColor().getDisplayName();
            if (d.getGrade() != null)    r.gradeName    = d.getGrade().getDisplayName();
        }
        return r;
    }

    public Long getId() { return id; }
    public Long getDeviceId() { return deviceId; }
    public String getCaseLotId() { return caseLotId; }
    public String getSku() { return sku; }
    public String getModelName() { return modelName; }
    public String getCarrierName() { return carrierName; }
    public String getCapacityName() { return capacityName; }
    public String getColorName() { return colorName; }
    public String getGradeName() { return gradeName; }
    public Integer getCaseLotSize() { return caseLotSize; }
    public Integer getCaseLotAtpQty() { return caseLotAtpQty; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getCaseLotPrice() { return caseLotPrice; }
}
