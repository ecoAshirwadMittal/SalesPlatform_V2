package com.ecoatm.salesplatform.dto;

public record BuyerPermissions(
    boolean canEditSalesRep,
    boolean canToggleStatus,
    boolean canEditBuyerCodeType
) {

    public static BuyerPermissions forAdmin() {
        return new BuyerPermissions(true, true, true);
    }

    public static BuyerPermissions forCompliance() {
        return new BuyerPermissions(false, false, false);
    }
}
