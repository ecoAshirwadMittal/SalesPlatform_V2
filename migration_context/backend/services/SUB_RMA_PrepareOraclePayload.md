# Microflow Detailed Specification: SUB_RMA_PrepareOraclePayload

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Description** = `'Preparing JSON content for sending RMA [RMAId:'+$RMA/Number+']'`**
2. **Call Microflow **Custom_Logging.SUB_Log_Info****
3. **Retrieve related **RMAItem_RMA** via Association from **$RMA** (Result: **$RMAItemList**)**
4. **List Operation: **Filter** on **$undefined** where `EcoATM_RMA.ENUM_RMAItemStatus.Approve` (Result: **$RMAItemList_Approved**)**
5. **Retrieve related **RMA_BuyerCode** via Association from **$RMA** (Result: **$BuyerCode**)**
6. **Create **EcoATM_PWSIntegration.RMARequest** (Result: **$NewRMARequest**)
      - Set **OriginSystemOrderId** = `$RMA/Number`
      - Set **OrderType** = `'PWS-RMA'`
      - Set **OrderDate** = `formatDateTime([%CurrentDateTime%], 'yyyyMMddHHmmss')`
      - Set **BuyerCode** = `$BuyerCode/Code`
      - Set **OriginSystemUser** = `$currentUser/Name`**
7. 🔄 **LOOP:** For each **$IteratorRMAItem** in **$RMAItemList_Approved**
   │ 1. **Create **EcoATM_PWSIntegration.RMALineItem** (Result: **$NewRMALineItem**)
      - Set **ItemNumber** = `$IteratorRMAItem/EcoATM_RMA.RMAItem_Device/EcoATM_PWSMDM.Device/SKU`
      - Set **UnitSellingPrice** = `toString($IteratorRMAItem/SalePrice)`
      - Set **RMAOriginalOrder** = `$IteratorRMAItem/EcoATM_RMA.RMAItem_Order/EcoATM_PWS.Order/OrderNumber`
      - Set **RMAIMEI** = `$IteratorRMAItem/IMEI`
      - Set **RMAReason** = `$IteratorRMAItem/ReturnReason`
      - Set **RMALineItem_RMARequest** = `$NewRMARequest`**
   └─ **End Loop**
8. **ExportXml**
9. **Call Microflow **Custom_Logging.SUB_Log_Info****
10. 🏁 **END:** Return `$JSONContent`

**Final Result:** This process concludes by returning a [String] value.