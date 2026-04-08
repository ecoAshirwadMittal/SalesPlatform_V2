# Microflow Detailed Specification: ACT_Offer_BuyerAcceptAllCounters_original

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **Offer_BuyerCode** via Association from **$Offer** (Result: **$BuyerCode**)**
2. **Create Variable **$TimerName** = `'AcceptAllOffers'`**
3. **Create Variable **$Description** = `'Buyer accept the counter offer [OfferId:'+$Offer/OfferID+'] [BuyerCode:'+$BuyerCode/Code+']'`**
4. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
5. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[EcoATM_PWS.OfferItem_Offer = $Offer] [SalesOfferItemStatus='Counter' or SalesOfferItemStatus='Accept']` (Result: **$OfferItemList**)**
6. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OfferItemList**
   │ 1. 🔀 **DECISION:** `$IteratorOfferItem/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Counter`
   │    ➔ **If [true]:**
   │       1. **Update **$IteratorOfferItem**
      - Set **BuyerCounterStatus** = `EcoATM_PWS.ENUM_CounterStatus.Accept`
      - Set **FinalOfferPrice** = `$IteratorOfferItem/CounterPrice`
      - Set **FinalOfferQuantity** = `$IteratorOfferItem/CounterQuantity`
      - Set **CounterTotal** = `$IteratorOfferItem/CounterPrice*$IteratorOfferItem/CounterQuantity`
      - Set **FinalOfferTotalPrice** = `$IteratorOfferItem/CounterTotal`**
   │    ➔ **If [false]:**
   │       1. **Update **$IteratorOfferItem**
      - Set **BuyerCounterStatus** = `EcoATM_PWS.ENUM_CounterStatus.Accept`
      - Set **FinalOfferPrice** = `$IteratorOfferItem/OfferPrice`
      - Set **FinalOfferQuantity** = `$IteratorOfferItem/OfferQuantity`
      - Set **OfferTotalPrice** = `$IteratorOfferItem/OfferPrice*$IteratorOfferItem/OfferQuantity`
      - Set **FinalOfferTotalPrice** = `$IteratorOfferItem/OfferTotalPrice`**
   └─ **End Loop**
7. **AggregateList**
8. **AggregateList**
9. **AggregateList**
10. **Commit/Save **$OfferItemList** to Database**
11. **Update **$Offer** (and Save to DB)
      - Set **OfferStatus** = `EcoATM_PWS.ENUM_PWSOrderStatus.Ordered`
      - Set **FinalOfferSubmittedOn** = `[%CurrentDateTime%]`
      - Set **FinalOfferTotalSKU** = `$CountSKU`
      - Set **FinalOfferTotalQty** = `$SumFinalOfferQuantity`
      - Set **FinalOfferTotalPrice** = `$SumFinalOfferPrice`
      - Set **UpdateDate** = `[%CurrentDateTime%]`**
12. **Close current page/popup**
13. **Maps to Page: **EcoATM_PWS.PWSBuyerCounterOffer_Overview****
14. **Create **EcoATM_PWS.UserMessage** (Result: **$AcceptedUserMessage**)
      - Set **Title** = `'Offer response submitted'`
      - Set **Message** = `'You will receive details about your order shortly.'`
      - Set **CSSClass** = `'pws-file-upload-success'`
      - Set **IsSuccess** = `true`**
15. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
16. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
17. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.