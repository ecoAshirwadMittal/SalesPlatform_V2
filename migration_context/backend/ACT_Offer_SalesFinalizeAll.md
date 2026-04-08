# Microflow Detailed Specification: ACT_Offer_SalesFinalizeAll

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
      2. **Create Variable **$TimerName** = `'Finalize All'`**
      3. **Create Variable **$Description** = `'Finalize All offer [OfferId:'+$Offer/OfferID+'] [BuyerCode:'+$BuyerCode/Code+']'`**
      4. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      5. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
      6. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OfferItemList**
         │ 1. 🔀 **DECISION:** `$IteratorOfferItem/SalesOfferItemStatus`
         │    ➔ **If [Accept]:**
         │       1. **Update **$IteratorOfferItem**
      - Set **SalesOfferItemStatus** = `EcoATM_PWS.ENUM_OfferItemStatus.Finalize`
      - Set **FinalOfferPrice** = `0`
      - Set **FinalOfferQuantity** = `0`
      - Set **FinalOfferTotalPrice** = `0`**
         │    ➔ **If [Finalize]:**
         │    ➔ **If [Decline]:**
         │       1. **Update **$IteratorOfferItem**
      - Set **SalesOfferItemStatus** = `EcoATM_PWS.ENUM_OfferItemStatus.Finalize`
      - Set **FinalOfferPrice** = `0`
      - Set **FinalOfferQuantity** = `0`
      - Set **FinalOfferTotalPrice** = `0`**
         │    ➔ **If [Counter]:**
         │       1. 🔀 **DECISION:** `$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType='SPB'`
         │          ➔ **If [true]:**
         │             1. **Retrieve related **OfferItem_CaseLot** via Association from **$IteratorOfferItem** (Result: **$CaseLot**)**
         │             2. **Update **$IteratorOfferItem**
      - Set **SalesOfferItemStatus** = `EcoATM_PWS.ENUM_OfferItemStatus.Finalize`
      - Set **FinalOfferPrice** = `$IteratorOfferItem/CounterPrice`
      - Set **FinalOfferQuantity** = `$IteratorOfferItem/CounterQuantity*$CaseLot/CaseLotSize`
      - Set **FinalOfferTotalPrice** = `$IteratorOfferItem/CounterCasePriceTotal`**
         │          ➔ **If [false]:**
         │             1. **Update **$IteratorOfferItem**
      - Set **SalesOfferItemStatus** = `EcoATM_PWS.ENUM_OfferItemStatus.Finalize`
      - Set **FinalOfferPrice** = `$IteratorOfferItem/CounterPrice`
      - Set **FinalOfferQuantity** = `$IteratorOfferItem/CounterQuantity`
      - Set **FinalOfferTotalPrice** = `$IteratorOfferItem/CounterTotal`**
         │    ➔ **If [(empty)]:**
         │       1. **Update **$IteratorOfferItem**
      - Set **SalesOfferItemStatus** = `EcoATM_PWS.ENUM_OfferItemStatus.Finalize`
      - Set **FinalOfferPrice** = `0`
      - Set **FinalOfferQuantity** = `0`
      - Set **FinalOfferTotalPrice** = `0`**
         └─ **End Loop**
      7. **Commit/Save **$OfferItemList** to Database**
      8. **Call Microflow **EcoATM_PWS.SUB_Offer_UpdateSnowflake****
      9. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      10. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.