# Microflow Detailed Specification: SUB_BuyerAwardSummaryTotals_GetOrCreate

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **BuyerAwardSummaryTotals_Week** via Association from **$Week** (Result: **$BuyerSummaryTotal**)**
2. 🔀 **DECISION:** `$BuyerSummaryTotal != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$BuyerSummaryTotal`
   ➔ **If [false]:**
      1. **Create **EcoATM_Reports.BuyerAwardSummaryTotals** (Result: **$NewBuyerSummaryTotal**)
      - Set **BuyerAwardSummaryTotals_Week** = `$Week`**
      2. 🏁 **END:** Return `$NewBuyerSummaryTotal`

**Final Result:** This process concludes by returning a [Object] value.