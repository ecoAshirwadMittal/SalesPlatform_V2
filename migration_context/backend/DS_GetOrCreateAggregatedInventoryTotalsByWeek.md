# Microflow Detailed Specification: DS_GetOrCreateAggregatedInventoryTotalsByWeek

### 📥 Inputs (Parameters)
- **$SelectedWeek** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **AggInventoryTotals_Week** via Association from **$SelectedWeek** (Result: **$AggreegatedInventoryTotal**)**
2. 🔀 **DECISION:** `$AggreegatedInventoryTotal!=empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$AggreegatedInventoryTotal`
   ➔ **If [false]:**
      1. **Create **AuctionUI.AggreegatedInventoryTotals** (Result: **$NewAggregatedInventoryTotal**)
      - Set **AggInventoryTotals_Week** = `$SelectedWeek`**
      2. 🏁 **END:** Return `$NewAggregatedInventoryTotal`

**Final Result:** This process concludes by returning a [Object] value.