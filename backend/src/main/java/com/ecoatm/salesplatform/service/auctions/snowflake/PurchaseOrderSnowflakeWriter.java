package com.ecoatm.salesplatform.service.auctions.snowflake;

public interface PurchaseOrderSnowflakeWriter {
    void upsert(PurchaseOrderSnowflakePayload payload);
    void delete(long purchaseOrderId);
}
