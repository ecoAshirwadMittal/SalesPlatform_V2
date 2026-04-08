# Microflow Detailed Specification: ACT_Offer_SalesAcceptAll

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
      2. **Create Variable **$TimerName** = `'AcceptAll'`**
      3. **Create Variable **$Description** = `'AcceptAll offer [OfferId:'+$Offer/OfferID+'] [BuyerCode:'+$BuyerCode/Code+']'`**
      4. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      5. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
      6. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OfferItemList**
         │ 1. 🔀 **DECISION:** `$IteratorOfferItem/SalesOfferItemStatus!=EcoATM_PWS.ENUM_OfferItemStatus.Accept`
         │    ➔ **If [true]:**
         │       1. 🔀 **DECISION:** `$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType='SPB'`
         │          ➔ **If [true]:**
         │             1. **Retrieve related **OfferItem_CaseLot** via Association from **$IteratorOfferItem** (Result: **$CaseLot**)**
         │             2. **Update **$IteratorOfferItem**
      - Set **SalesOfferItemStatus** = `EcoATM_PWS.ENUM_OfferItemStatus.Accept`
      - Set **FinalOfferPrice** = `$IteratorOfferItem/OfferPrice`
      - Set **FinalOfferQuantity** = `$IteratorOfferItem/OfferQuantity*$CaseLot/CaseLotSize`
      - Set **FinalOfferTotalPrice** = `$IteratorOfferItem/OfferTotalPrice`**
         │          ➔ **If [false]:**
         │             1. **Update **$IteratorOfferItem**
      - Set **SalesOfferItemStatus** = `EcoATM_PWS.ENUM_OfferItemStatus.Accept`
      - Set **FinalOfferPrice** = `$IteratorOfferItem/OfferPrice`
      - Set **FinalOfferQuantity** = `$IteratorOfferItem/OfferQuantity`
      - Set **FinalOfferTotalPrice** = `$IteratorOfferItem/OfferTotalPrice`**
         │    ➔ **If [false]:**
         └─ **End Loop**
      7. **Commit/Save **$OfferItemList** to Database**
      8. **Call Microflow **EcoATM_PWS.SUB_CalculateOfferSummary_NoCounterItems****
      9. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      10. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.