# Microflow Detailed Specification: SUB_UploadInventoryForCurrentWeek

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_MDM.Week** Filter: `[WeekEndDateTime < '[%CurrentDateTime%]']` (Result: **$Week**)**
2. **Call Microflow **AuctionUI.SUB_LoadAggregatedInventoryTotals****
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.