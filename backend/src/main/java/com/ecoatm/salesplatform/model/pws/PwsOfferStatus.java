package com.ecoatm.salesplatform.model.pws;

/**
 * Single source of truth for PWS offer/order/item status literals.
 * Mirrors Mendix ENUM_PWSOfferStatus, ENUM_PWSOrderStatus, ENUM_OfferItemStatus,
 * ENUM_CounterStatus, and ENUM_OfferDrawerStatus (Mixed_Case after V30 migration).
 */
public final class PwsOfferStatus {

    private PwsOfferStatus() {}

    // ENUM_PWSOfferStatus / ENUM_PWSOrderStatus
    public static final String DRAFT = "Draft";
    public static final String SALES_REVIEW = "Sales_Review";
    public static final String BUYER_ACCEPTANCE = "Buyer_Acceptance";
    public static final String SUBMITTED = "Submitted";
    public static final String PENDING_REVIEW = "Pending_Review";
    public static final String ORDERED = "Ordered";
    public static final String PENDING_ORDER = "Pending_Order";
    public static final String DECLINED = "Declined";
    public static final String CANCELED = "Canceled";

    // ENUM_OfferItemStatus
    public static final String ITEM_ACCEPT = "Accept";
    public static final String ITEM_COUNTER = "Counter";
    public static final String ITEM_DECLINE = "Decline";
    public static final String ITEM_FINALIZE = "Finalize";

    // ENUM_CounterStatus
    public static final String COUNTER_ACCEPT = "Accept";
    public static final String COUNTER_DECLINE = "Decline";

    // ENUM_OfferDrawerStatus
    public static final String DRAWER_SALES_REVIEW = "Sales_Review";
    public static final String DRAWER_ORDERED = "Ordered";
    public static final String DRAWER_ACCEPTED = "Accepted";
    public static final String DRAWER_COUNTERED = "Countered";
    public static final String DRAWER_SELLER_DECLINED = "Seller_Declined";
    public static final String DRAWER_BUYER_DECLINED = "Buyer_Declined";
}
