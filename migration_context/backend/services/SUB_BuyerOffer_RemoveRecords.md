# Microflow Detailed Specification: SUB_BuyerOffer_RemoveRecords

### 📥 Inputs (Parameters)
- **$BuyerOffer** (Type: EcoATM_PWS.BuyerOffer)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Description** = `'remove all existing BuyerOfferItems related to the BuyerOffer [OfferId:'+$BuyerOffer/OfferID+']'`**
2. **Call Microflow **Custom_Logging.SUB_Log_Info****
3. **Retrieve related **BuyerOfferItem_BuyerOffer** via Association from **$BuyerOffer** (Result: **$BuyerOfferItemList**)**
4. 🔀 **DECISION:** `$BuyerOfferItemList!=empty`
   ➔ **If [true]:**
      1. **Delete**
      2. **Update **$BuyerOffer** (and Save to DB)
      - Set **OfferTotal** = `0`
      - Set **OfferStatus** = `empty`
      - Set **OfferSKUs** = `0`
      - Set **OfferQuantity** = `0`**
      3. **Call Microflow **Custom_Logging.SUB_Log_Info****
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$BuyerOffer** (and Save to DB)
      - Set **OfferTotal** = `0`
      - Set **OfferStatus** = `empty`
      - Set **OfferSKUs** = `0`
      - Set **OfferQuantity** = `0`**
      2. **Call Microflow **Custom_Logging.SUB_Log_Info****
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.