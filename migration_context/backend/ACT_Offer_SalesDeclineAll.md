# Microflow Detailed Specification: ACT_Offer_SalesDeclineAll

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. 🔀 **DECISION:** `$ObjectInfo/IsCurrentUserAllowed`
   ➔ **If [false]:**
      1. **Maps to Page: **EcoATM_PWS.PWS_OfferAndCounterOffer_Review****
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Retrieve related **Offer_BuyerCode** via Association from **$Offer** (Result: **$BuyerCode**)**
      2. **Create Variable **$TimerName** = `'DeclineAll'`**
      3. **Create Variable **$Description** = `'DeclineAll offer [OfferId:'+$Offer/OfferID+'] [BuyerCode:'+$BuyerCode/Code+']'`**
      4. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      5. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
      6. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OfferItemList**
         │ 1. 🔀 **DECISION:** `$IteratorOfferItem/SalesOfferItemStatus!=EcoATM_PWS.ENUM_OfferItemStatus.Decline`
         │    ➔ **If [true]:**
         │       1. **Update **$IteratorOfferItem**
      - Set **SalesOfferItemStatus** = `EcoATM_PWS.ENUM_OfferItemStatus.Decline`
      - Set **FinalOfferPrice** = `0`
      - Set **FinalOfferQuantity** = `0`
      - Set **FinalOfferTotalPrice** = `0`**
         │    ➔ **If [false]:**
         └─ **End Loop**
      7. **Commit/Save **$OfferItemList** to Database**
      8. **Update **$Offer** (and Save to DB)
      - Set **CounterOfferAvgPrice** = `0`
      - Set **CounterOfferMinPercentageVariance** = `0`
      - Set **CounterOfferTotalPrice** = `0`
      - Set **CounterOfferTotalQty** = `0`
      - Set **CounterOfferTotalSKU** = `0`
      - Set **FinalOfferTotalPrice** = `0`
      - Set **FinalOfferTotalQty** = `0`
      - Set **FinalOfferTotalSKU** = `0`**
      9. **Call Microflow **EcoATM_PWS.SUB_Offer_UpdateSnowflake****
      10. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      11. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.