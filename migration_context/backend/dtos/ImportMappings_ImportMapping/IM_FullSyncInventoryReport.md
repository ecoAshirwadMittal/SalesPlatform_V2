# Import Mapping: IM_FullSyncInventoryReport

**JSON Structure:** `EcoATM_PWSIntegration.JSON_FullSyncReport`

## Mapping Structure

- **JsonObject** (Object) → `EcoATM_PWS.SKUSyncDetail`
  - **Sku** (Value)
    - Attribute: `EcoATM_PWS.SKUSyncDetail.SKU`
  - **Previoussynctime** (Value)
    - Attribute: `EcoATM_PWS.SKUSyncDetail.PreviousSyncTime`
  - **Previoussyncqty** (Value)
    - Attribute: `EcoATM_PWS.SKUSyncDetail.PreviousSyncQty`
  - **Synctime** (Value)
    - Attribute: `EcoATM_PWS.SKUSyncDetail.SyncTime`
  - **Syncqty** (Value)
    - Attribute: `EcoATM_PWS.SKUSyncDetail.SyncQty`
  - **Deltaqty** (Value)
    - Attribute: `EcoATM_PWS.SKUSyncDetail.DeltaQty`
  - **Notfound** (Value)
    - Attribute: `EcoATM_PWS.SKUSyncDetail.NotFound`
