# Microflow Detailed Specification: SUB_Offer_PrepareOraclePayload

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)
- **$Order** (Type: EcoATM_PWS.Order)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Description** = `'preparing jsoncontent for sending creating order [OfferID:'+$Offer/OfferID+']'`**
2. **Call Microflow **Custom_Logging.SUB_Log_Info****
3. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[EcoATM_PWS.OfferItem_Offer=$Offer] [SalesOfferItemStatus='Accept' or (SalesOfferItemStatus='Counter' and BuyerCounterStatus='Accept') or SalesOfferItemStatus='Finalize' ]` (Result: **$AcceptedOfferItemList**)**
4. **Retrieve related **Offer_BuyerCode** via Association from **$Offer** (Result: **$BuyerCode**)**
5. **Create **EcoATM_PWSIntegration.OrderRequest** (Result: **$NewOracleRequest**)
      - Set **OriginSystemOrderId** = `$Offer/OfferID`
      - Set **OrderDate** = `formatDateTime([%CurrentDateTime%], 'yyyyMMddHHmmss')`
      - Set **OrderType** = `'PWS'`
      - Set **BuyerCode** = `$BuyerCode/Code`
      - Set **OriginationSystemUser** = `$currentUser/Name`**
6. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$AcceptedOfferItemList**
   │ 1. **Create **EcoATM_PWSIntegration.OrderLineItem** (Result: **$CounteredOrderLineItem**)
      - Set **Item_number** = `$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/SKU`
      - Set **Quantity** = `toString($IteratorOfferItem/FinalOfferQuantity)`
      - Set **UnitSellingPrice** = `toString($IteratorOfferItem/FinalOfferPrice)`
      - Set **OrderLineItem_OrderRequest** = `$NewOracleRequest`**
   └─ **End Loop**
7. **ExportXml**
8. **Call Microflow **Custom_Logging.SUB_Log_Info****
9. 🏁 **END:** Return `$JSONContent`

**Final Result:** This process concludes by returning a [String] value.