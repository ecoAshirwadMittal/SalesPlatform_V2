# Microflow Detailed Specification: Sub_SyncOrderHistory

### 📥 Inputs (Parameters)
- **$DesposcoAPIsForOrderHistory** (Type: EcoATM_PWSIntegration.DesposcoAPIs)
- **$IteratorOrder** (Type: EcoATM_PWS.Order)
- **$OrderStatusList** (Type: EcoATM_PWS.OrderStatus)
- **$DesposcoAPIsList** (Type: EcoATM_PWSIntegration.DesposcoAPIs)
- **$EncodedAuth** (Type: Variable)
- **$DeposcoConfig** (Type: EcoATM_PWSIntegration.DeposcoConfig)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$IteratorOrder/OrderNumber!=empty`
   ➔ **If [true]:**
      1. **Create Variable **$APIURLForOrder** = `$DesposcoAPIsForOrderHistory/ServiceUrl +'?number='+$IteratorOrder/OrderNumber`**
      2. **Create Variable **$AccessTokenVar** = `empty`**
      3. **Call Microflow **EcoATM_PWSIntegration.ACT_GenerateDeposcoV2Token** (Result: **$AccessToken**)**
      4. **Update Variable **$AccessTokenVar** = `$AccessToken`**
      5. **RestCall**
      6. **Call Microflow **EcoATM_PWSIntegration.ACT_AuditRestAPICalls****
      7. **ImportXml**
      8. **List Operation: **Head** on **$undefined** (Result: **$OrderHistory**)**
      9. 🔀 **DECISION:** `$OrderHistory!=empty`
         ➔ **If [true]:**
            1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/SystemStatus=$OrderHistory/OrderStatus` (Result: **$OrderStatus**)**
            2. **Retrieve related **Offer_Order** via Association from **$IteratorOrder** (Result: **$Offer**)**
            3. **Retrieve related **Offer_OrderStatus** via Association from **$Offer** (Result: **$OrderStatus_2**)**
            4. 🔀 **DECISION:** `$Offer/EcoATM_PWS.Offer_OrderStatus=$OrderStatus`
               ➔ **If [false]:**
                  1. **Call Microflow **EcoATM_PWS.SUB_Offer_UpdateSnowflake****
                  2. **Update **$Offer** (and Save to DB)
      - Set **Offer_OrderStatus** = `$OrderStatus`**
                  3. 🔀 **DECISION:** `$OrderHistory/OrderStatus='Ship Complete'`
                     ➔ **If [true]:**
                        1. **Retrieve related **SKUQuantity_OrderHistory** via Association from **$OrderHistory** (Result: **$SKUQuantityList**)**
                        2. **AggregateList**
                        3. **AggregateList**
                        4. **Update **$Offer** (and Save to DB)
      - Set **OrderPackQuantity** = `round($TotalOrderPackQuantity)`
      - Set **CanceledQuantity** = `round($TotalCanceledPackQuantity)`**
                        5. **Call Microflow **EcoATM_PWSIntegration.SUB_UpdateShipmentData****
                        6. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.