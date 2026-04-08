# Microflow Detailed Specification: SUB_CreateOriginalOfferSummary

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Description** = `'creating Original Offer Summary object'`**
2. **Call Microflow **Custom_Logging.SUB_Log_Info****
3. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
4. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType= 'PWS'` (Result: **$OfferItemList_PWS**)**
5. **AggregateList**
6. **Call Microflow **EcoATM_PWS.SUB_OfferItem_CalculateCaseLotSKUs** (Result: **$TotalSKUs_CaseLots**)**
7. **AggregateList**
8. **AggregateList**
9. **AggregateList**
10. **AggregateList**
11. **Update **$Offer** (and Save to DB)
      - Set **OfferSKUCount** = `$TotalSKUs_PWS+$TotalSKUs_CaseLots`
      - Set **OfferTotalQuantity** = `$Qty`
      - Set **OfferTotalPrice** = `$Sum_OfferTotalPrice`
      - Set **OfferAvgPrice** = `round($Sum_OfferTotalPrice div $Qty)`
      - Set **OfferMinPercentageVariance** = `(($SumDifference)div $Sum_OfferPrice)*100`**
12. **Call Microflow **Custom_Logging.SUB_Log_Info****
13. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.