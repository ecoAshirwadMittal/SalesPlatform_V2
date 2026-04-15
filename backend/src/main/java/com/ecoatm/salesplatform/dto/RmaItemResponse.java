package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.pws.RmaItem;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class RmaItemResponse {

    private Long id;
    private Long rmaId;
    private String imei;
    private String orderNumber;
    private LocalDateTime shipDate;
    private BigDecimal salePrice;
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
}
