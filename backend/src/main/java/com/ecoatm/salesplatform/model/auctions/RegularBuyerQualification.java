package com.ecoatm.salesplatform.model.auctions;

/**
 * Mirrors Mendix {@code EcoATM_BuyerManagement.Enum_RegularBuyerQualification}.
 * Values match the {@code chk_brsf_regular_qual} CHECK constraint in V59.
 *
 * <p>Controls which buyers qualify for the configured round (2 or 3):
 * either only buyers with an active target qualification, or every buyer.
 */
public enum RegularBuyerQualification {
    Only_Qualified,
    All_Buyers
}
