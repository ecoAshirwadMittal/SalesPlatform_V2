# Microflow Detailed Specification: ACT_BuyerAwardSummaryReport_ShowPage

### 📥 Inputs (Parameters)
- **$DAHelper** (Type: EcoATM_DA.DAHelper)
- **$CurrentObjectBuyerSummary** (Type: EcoATM_DA.BuyerSummary)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$CurrentObjectBuyerSummary != empty`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_Reports.SUB_LoadBuyerDetails****
      2. **Maps to Page: **EcoATM_Reports.PG_BuyerAwardSummaryReportReview****
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Information): `No object selected!`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.