# Microflow Detailed Specification: SUB_CalculateBuyerDetailsTotal

### 📥 Inputs (Parameters)
- **$BuyerSummary** (Type: EcoATM_DA.BuyerSummary)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_DA.SUB_BuyerDetailsTotal_GetOrCreate** (Result: **$BuyerDetailsTotals**)**
2. **Retrieve related **BuyerDetail_BuyerSummary** via Association from **$BuyerSummary** (Result: **$BuyerDetailsList**)**
3. **AggregateList**
4. **AggregateList**
5. **Update **$BuyerDetailsTotals**
      - Set **SalesQty** = `$SumSalesQty`
      - Set **Amount** = `$SumAmount`**
6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.