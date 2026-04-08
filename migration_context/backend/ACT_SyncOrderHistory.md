# Microflow Detailed Specification: ACT_SyncOrderHistory

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$OrderSyncTimer** = `'OrderSyncTimer'`**
2. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
3. **DB Retrieve **EcoATM_PWS.Order** Filter: `[not(HasShipmentDetails)] [EcoATM_PWS.Offer_Order/EcoATM_PWS.Offer/EcoATM_PWS.Offer_OrderStatus/EcoATM_PWS.OrderStatus/SystemStatus!='Canceled' or not(EcoATM_PWS.Offer_Order/EcoATM_PWS.Offer/EcoATM_PWS.Offer_OrderStatus/EcoATM_PWS.OrderStatus)]` (Result: **$OrderList**)**
4. **DB Retrieve **EcoATM_PWSIntegration.DeposcoConfig**  (Result: **$DeposcoConfig**)**
5. **Retrieve related **DesposcoAPIs_DeposcoConfig** via Association from **$DeposcoConfig** (Result: **$DesposcoAPIsList**)**
6. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/ServiceName = EcoATM_PWSIntegration.ENUM_DeposcoServices.OrderHistory` (Result: **$DesposcoAPIsForOrderHistory**)**
7. **DB Retrieve **EcoATM_PWS.OrderStatus**  (Result: **$OrderStatusList**)**
8. **Call Microflow **EcoATM_PWSIntegration.SUB_GenerateDeposcoPassword** (Result: **$EncodedAuth**)**
9. 🔄 **LOOP:** For each **$IteratorOrder** in **$OrderList**
   │ 1. **Call Microflow **EcoATM_PWSIntegration.SUB_SyncOrderHistory_TryCatch****
   └─ **End Loop**
10. **Commit/Save **$OrderList** to Database**
11. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
12. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.