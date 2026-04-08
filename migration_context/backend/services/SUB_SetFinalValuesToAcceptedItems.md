# Microflow Detailed Specification: SUB_SetFinalValuesToAcceptedItems

### 📥 Inputs (Parameters)
- **$OfferItemList** (Type: EcoATM_PWS.OfferItem)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **Offer_BuyerCode** via Association from **$Offer** (Result: **$BuyerCode**)**
2. **Call Microflow **Custom_Logging.SUB_Log_Info****
3. **List Operation: **Filter** on **$undefined** where `EcoATM_PWS.ENUM_OfferItemStatus.Accept` (Result: **$OfferItemList_Accepted**)**
4. 🔀 **DECISION:** `$OfferItemList_Accepted!=empty`
   ➔ **If [true]:**
      1. 🔄 **LOOP:** For each **$IteratorOfferItemAccept** in **$OfferItemList_Accepted**
         │ 1. 🔀 **DECISION:** `$IteratorOfferItemAccept/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType='SPB'`
         │    ➔ **If [true]:**
         │       1. **Retrieve related **OfferItem_CaseLot** via Association from **$IteratorOfferItemAccept** (Result: **$CaseLot**)**
         │       2. **Update **$IteratorOfferItemAccept**
      - Set **FinalOfferPrice** = `if $IteratorOfferItemAccept/CounterPrice!=empty then $IteratorOfferItemAccept/CounterPrice else $IteratorOfferItemAccept/OfferPrice`
      - Set **FinalOfferQuantity** = `if $IteratorOfferItemAccept/CounterQuantity!=empty then $IteratorOfferItemAccept/CounterQuantity*$CaseLot/CaseLotSize else $IteratorOfferItemAccept/OfferQuantity*$CaseLot/CaseLotSize`
      - Set **FinalOfferTotalPrice** = `if $IteratorOfferItemAccept/CounterTotal!=empty then $IteratorOfferItemAccept/CounterTotal else $IteratorOfferItemAccept/OfferTotalPrice`**
         │    ➔ **If [false]:**
         │       1. **Update **$IteratorOfferItemAccept**
      - Set **FinalOfferPrice** = `if $IteratorOfferItemAccept/CounterPrice!=empty then $IteratorOfferItemAccept/CounterPrice else $IteratorOfferItemAccept/OfferPrice`
      - Set **FinalOfferQuantity** = `if $IteratorOfferItemAccept/CounterQuantity!=empty then $IteratorOfferItemAccept/CounterQuantity else $IteratorOfferItemAccept/OfferQuantity`
      - Set **FinalOfferTotalPrice** = `if $IteratorOfferItemAccept/CounterTotal!=empty then $IteratorOfferItemAccept/CounterTotal else $IteratorOfferItemAccept/OfferTotalPrice`**
         └─ **End Loop**
      2. **Commit/Save **$OfferItemList_Accepted** to Database**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.