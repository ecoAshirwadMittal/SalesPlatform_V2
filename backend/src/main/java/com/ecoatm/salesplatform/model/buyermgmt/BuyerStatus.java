package com.ecoatm.salesplatform.model.buyermgmt;

/**
 * Buyer lifecycle status. Mirrors the legacy Mendix
 * `ecoatm_buyermanagement$buyer.status` enumeration
 * (`Active` | `Disabled`).
 *
 * Legacy rows occasionally carry null/empty values; callers that need
 * Mendix parity should treat missing values as {@link #Disabled}.
 */
public enum BuyerStatus {
    Active,
    Disabled
}
