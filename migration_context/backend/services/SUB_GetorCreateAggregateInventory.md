# Microflow Detailed Specification: SUB_GetorCreateAggregateInventory

### 📥 Inputs (Parameters)
- **$Week1** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **AggregateInventoryWeek_Week** via Association from **$Week1** (Result: **$AggregatedInventoryWeek**)**
2. 🔀 **DECISION:** `$AggregatedInventoryWeek!= empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$AggregatedInventoryWeek`
   ➔ **If [false]:**
      1. **Create **EcoATM_Inventory.AggregateInventoryWeek** (Result: **$NewAggregatedInventoryWeek**)
      - Set **AggregateInventoryWeek_Week** = `$Week1`**
      2. 🏁 **END:** Return `$NewAggregatedInventoryWeek`

**Final Result:** This process concludes by returning a [Object] value.