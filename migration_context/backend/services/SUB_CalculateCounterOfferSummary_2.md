# Microflow Detailed Specification: SUB_CalculateCounterOfferSummary_2

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/SalesOfferItemStatus=empty or $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Decline` (Result: **$ExcludedOfferItemList**)**
4. **List Operation: **Subtract** on **$undefined** (Result: **$AcceptedOrCounteredOfferItemList**)**
5. **AggregateList**
6. **AggregateList**
7. **AggregateList**
8. **Update **$Offer** (and Save to DB)
      - Set **CounterOfferTotalSKU** = `$SKUs_Counter`
      - Set **CounterOfferTotalQty** = `$SumFinalOfferQuantity`
      - Set **CounterOfferTotalPrice** = `$Sum_FinalOfferTotalPrice`
      - Set **CounterOfferAvgPrice** = `round($Sum_FinalOfferTotalPrice div $SumFinalOfferQuantity)`
      - Set **CounterOfferMinPercentageVariance** = `(($SumDifference)div $Sum_FinalOfferPrice)*100`
      - Set **UpdateDate** = `[%CurrentDateTime%]`
      - Set **FinalOfferTotalSKU** = `$SKUs_Counter`
      - Set **FinalOfferTotalQty** = `$SumFinalOfferQuantity`
      - Set **FinalOfferTotalPrice** = `$Sum_FinalOfferTotalPrice`**
9. **Call Microflow **Custom_Logging.SUB_Log_Info****
10. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.