# Microflow Detailed Specification: SUB_Offer_PrepareOraclePayload_2

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)
- **$Order** (Type: EcoATM_PWS.Order)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
3. **Retrieve related **Offer_BuyerCode** via Association from **$Offer** (Result: **$BuyerCode**)**
4. **Create **EcoATM_PWSIntegration.OrderRequest** (Result: **$NewOracleRequest**)
      - Set **OriginSystemOrderId** = `toString($Order/PWSNumber)`
      - Set **OrderDate** = `formatDateTime([%CurrentDateTime%], 'yyyyMMddHHmmss')`
      - Set **OrderType** = `'PWS'`
      - Set **BuyerCode** = `$BuyerCode/Code`
      - Set **OriginationSystemUser** = `$currentUser/Name`**
5. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OfferItemList**
   │ 1. **Create **EcoATM_PWSIntegration.OrderLineItem** (Result: **$CounteredOrderLineItem**)
      - Set **Item_number** = `$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/SKU`
      - Set **Quantity** = `toString($IteratorOfferItem/FinalOfferQuantity)`
      - Set **UnitSellingPrice** = `toString($IteratorOfferItem/FinalOfferPrice)`
      - Set **OrderLineItem_OrderRequest** = `$NewOracleRequest`**
   └─ **End Loop**
6. **ExportXml**
7. **Call Microflow **Custom_Logging.SUB_Log_Info****
8. 🏁 **END:** Return `$JSONContent`

**Final Result:** This process concludes by returning a [String] value.