# Microflow Detailed Specification: SUB_CalculateFinalizeOfferSummary

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$FinalizeOfferItemList**)**
3. **List Operation: **Filter** on **$undefined** where `EcoATM_PWS.ENUM_OfferItemStatus.Finalize` (Result: **$OnlyFinalizeOfferItemList**)**
4. **AggregateList**
5. **AggregateList**
6. **AggregateList**
7. **AggregateList**
8. **Create Variable **$SumDifference** = `0`**
9. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OnlyFinalizeOfferItemList**
   │ 1. **Update Variable **$SumDifference** = `$SumDifference+($IteratorOfferItem/CounterPrice-$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/CurrentMinPrice)`**
   └─ **End Loop**
10. **Update **$Offer** (and Save to DB)
      - Set **CounterOfferTotalSKU** = `$SKUs_Finalize`
      - Set **CounterOfferTotalQty** = `$SumFinalOfferQuantity`
      - Set **CounterOfferTotalPrice** = `$Sum_FinalOfferTotalPrice`
      - Set **CounterOfferAvgPrice** = `round($Sum_FinalOfferTotalPrice div $SumFinalOfferQuantity)`
      - Set **CounterOfferMinPercentageVariance** = `(($SumDifference)div $Sum_FinalOfferPrice)*100`
      - Set **UpdateDate** = `[%CurrentDateTime%]`
      - Set **FinalOfferTotalSKU** = `$SKUs_Finalize`
      - Set **FinalOfferTotalQty** = `$SumFinalOfferQuantity`
      - Set **FinalOfferTotalPrice** = `$Sum_FinalOfferTotalPrice`**
11. **Call Microflow **Custom_Logging.SUB_Log_Info****
12. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.