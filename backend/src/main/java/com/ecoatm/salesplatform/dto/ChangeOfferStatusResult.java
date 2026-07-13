package com.ecoatm.salesplatform.dto;

/**
 * Summary returned by the bulk offer-status change tool.
 *
 * @param matchedOrders  number of orders resolved by the request (the selected
 *                       {@code orderIds}, or the orders whose {@code order_date}
 *                       fell inside the date range)
 * @param changedOffers  number of offers whose status was actually changed
 *                       (always {@code 0} for a metadata-only request)
 * @param metadataOnly   whether this was a metadata-only ({@code
 *                       notOrderStatusChange}) operation
 */
public record ChangeOfferStatusResult(
        int matchedOrders,
        int changedOffers,
        boolean metadataOnly) {
}
