# Microflow Detailed Specification: SUB_BuyerDetailsTotal_GetOrCreate

### 📥 Inputs (Parameters)
- **$BuyerSummary** (Type: EcoATM_DA.BuyerSummary)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **BuyerDetailTotals_BuyerSummary** via Association from **$BuyerSummary** (Result: **$BuyerDetailsTotal**)**
2. 🔀 **DECISION:** `$BuyerDetailsTotal != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$BuyerDetailsTotal`
   ➔ **If [false]:**
      1. **Create **EcoATM_DA.BuyerDetailTotals** (Result: **$NewBuyerDetailsTotal**)
      - Set **BuyerDetailTotals_BuyerSummary** = `$BuyerSummary`**
      2. 🏁 **END:** Return `$NewBuyerDetailsTotal`

**Final Result:** This process concludes by returning a [Object] value.