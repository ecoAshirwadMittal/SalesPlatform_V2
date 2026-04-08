# Microflow Detailed Specification: SUB_Order_CreateFromOffer

### 📥 Inputs (Parameters)
- **$FinalOffer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'BuyerOfferPrepareSubmitOrder'`**
2. **Create Variable **$Description** = `'prepare Order based on a BuyerOffer [BuyerOffer:'+$FinalOffer/OfferID+']'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[EcoATM_PWS.OfferItem_Offer=$FinalOffer] [SalesOfferItemStatus='Accept' or (SalesOfferItemStatus='Counter' and BuyerCounterStatus='Accept') or SalesOfferItemStatus = 'Finalize']` (Result: **$AcceptedFizLIZEOfferItemList**)**
5. 🔀 **DECISION:** `$AcceptedFizLIZEOfferItemList!=empty`
   ➔ **If [true]:**
      1. **Retrieve related **Offer_BuyerCode** via Association from **$FinalOffer** (Result: **$BuyerCode**)**
      2. **Call Microflow **EcoATM_PWS.SUB_Offer_CreateOrder** (Result: **$Order**)**
      3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      4. 🏁 **END:** Return `$Order`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.