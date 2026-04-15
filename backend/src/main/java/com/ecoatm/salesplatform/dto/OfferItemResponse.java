package com.ecoatm.salesplatform.dto;

import com.ecoatm.salesplatform.model.mdm.Device;
import com.ecoatm.salesplatform.model.pws.CaseLot;
import com.ecoatm.salesplatform.model.pws.OfferItem;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OfferItemResponse {

    private Long id;
    private String sku;
    private Long deviceId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private String itemStatus;
    private String offerDrawerStatus;
    private String buyerCounterStatus;

    // Counter offer fields (set by sales rep)
    private Integer counterQty;
    private BigDecimal counterPrice;
    private BigDecimal counterTotal;

    // Final offer fields (buyer's counter response)
    private BigDecimal finalOfferPrice;
    private Integer finalOfferQuantity;
    private BigDecimal finalOfferTotalPrice;
    private BigDecimal counterCasePriceTotal;

    // Device item type for grid grouping (PWS vs SPB)
    private String itemType;
    // Case lot size for SPB items
    private Integer caseLotSize;

    // Flattened device details for display
    private String categoryName;
    private String brandName;
    private String modelName;
    private String carrierName;
    private String capacityName;
    private String colorName;
    private String gradeName;
    private BigDecimal listPrice;
    private BigDecimal minPrice;
    private Integer availableQty;

    public static OfferItemResponse fromEntity(OfferItem item, Device device) {
        return fromEntityFull(item, device, null);
    }

    public static OfferItemResponse fromEntityFull(OfferItem item, Device device, CaseLot caseLot) {
        OfferItemResponse r = new OfferItemResponse();
        r.id = item.getId();
        r.sku = item.getSku();
        r.deviceId = item.getDeviceId();
        r.quantity = item.getQuantity();
        r.price = item.getPrice();
        r.totalPrice = item.getTotalPrice();
        r.itemStatus = item.getItemStatus();
        r.offerDrawerStatus = item.getOfferDrawerStatus();
        r.buyerCounterStatus = item.getBuyerCounterStatus();
        r.counterQty = item.getCounterQty();
        r.counterPrice = item.getCounterPrice();
        r.counterTotal = item.getCounterTotal();
        r.finalOfferPrice = item.getFinalOfferPrice();
        r.finalOfferQuantity = item.getFinalOfferQuantity();
        r.finalOfferTotalPrice = item.getFinalOfferTotalPrice();
        r.counterCasePriceTotal = item.getCounterCasePriceTotal();

        if (device != null) {
            r.categoryName = device.getCategory() != null ? device.getCategory().getDisplayName() : null;
            r.brandName = device.getBrand() != null ? device.getBrand().getDisplayName() : null;
            r.modelName = device.getModel() != null ? device.getModel().getDisplayName() : null;
            r.carrierName = device.getCarrier() != null ? device.getCarrier().getDisplayName() : null;
            r.capacityName = device.getCapacity() != null ? device.getCapacity().getDisplayName() : null;
            r.colorName = device.getColor() != null ? device.getColor().getDisplayName() : null;
            r.gradeName = device.getGrade() != null ? device.getGrade().getDisplayName() : null;
            r.listPrice = device.getListPrice();
            r.minPrice = device.getMinPrice();
            r.availableQty = device.getAtpQty() != null ? device.getAtpQty() : device.getAvailableQty();
            r.itemType = device.getItemType();
        }

        if (caseLot != null) {
            r.caseLotSize = caseLot.getCaseLotSize();
        }

        return r;
    }
}
