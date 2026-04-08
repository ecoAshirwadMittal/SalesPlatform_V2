# Microflow Detailed Specification: ACT_Offer_BuyerAcceptAllCounters

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. 🔀 **DECISION:** `$ObjectInfo/IsCurrentUserAllowed`
   ➔ **If [false]:**
      1. **Maps to Page: **EcoATM_PWS.PWS_OfferAndCounterOffer_Review_Buyer****
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Retrieve related **Offer_BuyerCode** via Association from **$Offer** (Result: **$BuyerCode**)**
      2. **Create Variable **$TimerName** = `'AcceptAllOffers'`**
      3. **Create Variable **$Description** = `'Buyer accept the counter offer [OfferId:'+$Offer/OfferID+'] [BuyerCode:'+$BuyerCode/Code+']'`**
      4. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      5. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[EcoATM_PWS.OfferItem_Offer = $Offer] [SalesOfferItemStatus='Counter' or SalesOfferItemStatus='Accept']` (Result: **$OfferItemList**)**
      6. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OfferItemList**
         │ 1. 🔀 **DECISION:** `$IteratorOfferItem/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Counter`
         │    ➔ **If [true]:**
         │       1. 🔀 **DECISION:** `$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType != 'SPB'`
         │          ➔ **If [true]:**
         │             1. **Update **$IteratorOfferItem**
      - Set **BuyerCounterStatus** = `EcoATM_PWS.ENUM_CounterStatus.Accept`
      - Set **FinalOfferPrice** = `$IteratorOfferItem/CounterPrice`
      - Set **FinalOfferQuantity** = `$IteratorOfferItem/CounterQuantity`
      - Set **CounterTotal** = `$IteratorOfferItem/CounterPrice*$IteratorOfferItem/CounterQuantity`
      - Set **FinalOfferTotalPrice** = `$IteratorOfferItem/CounterTotal`**
         │          ➔ **If [false]:**
         │             1. **Retrieve related **OfferItem_CaseLot** via Association from **$IteratorOfferItem** (Result: **$CaseLot**)**
         │             2. **Update **$IteratorOfferItem**
      - Set **BuyerCounterStatus** = `EcoATM_PWS.ENUM_CounterStatus.Accept`
      - Set **FinalOfferPrice** = `$IteratorOfferItem/CounterPrice`
      - Set **FinalOfferQuantity** = `$IteratorOfferItem/CounterQuantity*$CaseLot/CaseLotSize`
      - Set **CounterTotal** = `$IteratorOfferItem/CounterPrice*$IteratorOfferItem/CounterQuantity * $IteratorOfferItem/EcoATM_PWS.OfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/CaseLotSize`
      - Set **CounterCasePriceTotal** = `if $IteratorOfferItem/OfferPrice != empty then $CaseLot/CaseLotSize* $IteratorOfferItem/CounterPrice* $IteratorOfferItem/CounterQuantity else 0`
      - Set **FinalOfferTotalPrice** = `$IteratorOfferItem/CounterCasePriceTotal`**
         │    ➔ **If [false]:**
         │       1. 🔀 **DECISION:** `$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType != 'SPB'`
         │          ➔ **If [false]:**
         │             1. **Retrieve related **OfferItem_CaseLot** via Association from **$IteratorOfferItem** (Result: **$CaseLot_2**)**
         │             2. **Update **$IteratorOfferItem**
      - Set **BuyerCounterStatus** = `EcoATM_PWS.ENUM_CounterStatus.Accept`
      - Set **FinalOfferPrice** = `$IteratorOfferItem/OfferPrice`
      - Set **FinalOfferQuantity** = `$IteratorOfferItem/OfferQuantity*$CaseLot_2/CaseLotSize`
      - Set **OfferTotalPrice** = `$IteratorOfferItem/OfferPrice*$IteratorOfferItem/OfferQuantity * $IteratorOfferItem/EcoATM_PWS.OfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/CaseLotSize`
      - Set **FinalOfferTotalPrice** = `$IteratorOfferItem/OfferPrice*$IteratorOfferItem/OfferQuantity * $IteratorOfferItem/EcoATM_PWS.OfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/CaseLotSize`**
         │          ➔ **If [true]:**
         │             1. **Update **$IteratorOfferItem**
      - Set **BuyerCounterStatus** = `EcoATM_PWS.ENUM_CounterStatus.Accept`
      - Set **FinalOfferPrice** = `$IteratorOfferItem/OfferPrice`
      - Set **FinalOfferQuantity** = `$IteratorOfferItem/OfferQuantity`
      - Set **OfferTotalPrice** = `$IteratorOfferItem/OfferPrice*$IteratorOfferItem/OfferQuantity`
      - Set **FinalOfferTotalPrice** = `$IteratorOfferItem/OfferTotalPrice`**
         └─ **End Loop**
      7. **AggregateList**
      8. **AggregateList**
      9. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType='PWS'` (Result: **$OfferItemList_PWS**)**
      10. **AggregateList**
      11. **Call Microflow **EcoATM_PWS.SUB_OfferItem_CalculateCaseLotSKUs** (Result: **$SKUs_CaseLots**)**
      12. **Commit/Save **$OfferItemList** to Database**
      13. **Update **$Offer** (and Save to DB)
      - Set **FinalOfferTotalSKU** = `$SKUs_PWS+$SKUs_CaseLots`
      - Set **FinalOfferTotalQty** = `$SumFinalOfferQuantity`
      - Set **FinalOfferTotalPrice** = `$SumFinalOfferPrice`**
      14. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      15. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.