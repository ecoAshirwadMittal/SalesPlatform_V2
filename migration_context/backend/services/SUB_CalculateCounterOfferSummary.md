# Microflow Detailed Specification: SUB_CalculateCounterOfferSummary

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/SalesOfferItemStatus=empty or $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Decline` (Result: **$ExcludedOfferItemList**)**
4. **List Operation: **Subtract** on **$undefined** (Result: **$FinalOfferItemList**)**
5. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType='PWS'` (Result: **$FinalOfferItemList_PWS**)**
6. **AggregateList**
7. **Call Microflow **EcoATM_PWS.SUB_OfferItem_CalculateCaseLotSKUs** (Result: **$SKUs_CaseLots**)**
8. **AggregateList**
9. **AggregateList**
10. **AggregateList**
11. **Create Variable **$SumDifference** = `0`**
12. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$FinalOfferItemList**
   │ 1. 🔀 **DECISION:** `$IteratorOfferItem/SalesOfferItemStatus = EcoATM_PWS.ENUM_OfferItemStatus.Counter or $IteratorOfferItem/SalesOfferItemStatus = EcoATM_PWS.ENUM_OfferItemStatus.Finalize`
   │    ➔ **If [true]:**
   │       1. **Update Variable **$SumDifference** = `$SumDifference+($IteratorOfferItem/CounterPrice-$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/CurrentMinPrice)`**
   │    ➔ **If [false]:**
   │       1. **Update Variable **$SumDifference** = `$SumDifference+($IteratorOfferItem/OfferPrice-$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/CurrentMinPrice)`**
   └─ **End Loop**
13. **Update **$Offer** (and Save to DB)
      - Set **CounterOfferTotalSKU** = `$SKUs_PWS+$SKUs_CaseLots`
      - Set **CounterOfferTotalQty** = `$SumFinalOfferQuantity`
      - Set **CounterOfferTotalPrice** = `$Sum_FinalOfferTotalPrice`
      - Set **CounterOfferAvgPrice** = `if($Sum_FinalOfferTotalPrice>0 and $SumFinalOfferQuantity>0) then round($Sum_FinalOfferTotalPrice div $SumFinalOfferQuantity) else 0`
      - Set **CounterOfferMinPercentageVariance** = `if($Sum_FinalOfferPrice>0) then (($SumDifference)div $Sum_FinalOfferPrice)*100 else 0`
      - Set **FinalOfferTotalSKU** = `$SKUs_PWS+$SKUs_CaseLots`
      - Set **FinalOfferTotalQty** = `$SumFinalOfferQuantity`
      - Set **FinalOfferTotalPrice** = `$Sum_FinalOfferTotalPrice`**
14. **Call Microflow **Custom_Logging.SUB_Log_Info****
15. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.