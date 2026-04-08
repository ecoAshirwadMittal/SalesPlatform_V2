# Microflow Detailed Specification: DS_GetOrCreateAggregatedInventoryTotals

### 📥 Inputs (Parameters)
- **$AggInventoryHelper** (Type: AuctionUI.AggInventoryHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **AggInventoryHelper_Week** via Association from **$AggInventoryHelper** (Result: **$SelectedWeek**)**
2. **Call Microflow **AuctionUI.DS_GetOrCreateAggregatedInventoryTotalsByWeek** (Result: **$AggreegatedInventoryTotal**)**
3. 🏁 **END:** Return `$AggreegatedInventoryTotal`

**Final Result:** This process concludes by returning a [Object] value.