# Microflow Detailed Specification: SUB_CalculateOfferSummary_NoCounterItems

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
3. **List Operation: **Filter** on **$undefined** where `EcoATM_PWS.ENUM_OfferItemStatus.Accept` (Result: **$OfferItemList_Accept**)**
4. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType='PWS'` (Result: **$OfferItemList_PWS**)**
5. **AggregateList**
6. **Call Microflow **EcoATM_PWS.SUB_OfferItem_CalculateCaseLotSKUs** (Result: **$SKUs_CaseLots**)**
7. **AggregateList**
8. **AggregateList**
9. **Update **$Offer** (and Save to DB)
      - Set **CounterOfferTotalSKU** = `empty`
      - Set **CounterOfferTotalQty** = `empty`
      - Set **CounterOfferTotalPrice** = `empty`
      - Set **CounterOfferAvgPrice** = `empty`
      - Set **CounterOfferMinPercentageVariance** = `empty`
      - Set **FinalOfferTotalSKU** = `$SKUs_PWS+$SKUs_CaseLots`
      - Set **FinalOfferTotalQty** = `$SumFinalOfferQuantity`
      - Set **FinalOfferTotalPrice** = `$SumFinalOfferTotalPrice`**
10. **Call Microflow **Custom_Logging.SUB_Log_Info****
11. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.