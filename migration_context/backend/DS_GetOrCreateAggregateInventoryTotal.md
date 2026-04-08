# Microflow Detailed Specification: DS_GetOrCreateAggregateInventoryTotal

### 📥 Inputs (Parameters)
- **$AgreegateInventoryHelper** (Type: EcoATM_Inventory.AggregateInventoryHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **AggregateInventoryHelper_Week** via Association from **$AgreegateInventoryHelper** (Result: **$SelectedWeek**)**
2. **Retrieve related **AggregateInventoryWeek_Week** via Association from **$SelectedWeek** (Result: **$AggregatedInventoryWeek**)**
3. **Retrieve related **AggregateInventoryWeek_AggregateInventoryTotal** via Association from **$AggregatedInventoryWeek** (Result: **$TotalInventoryAgreegation**)**
4. 🔀 **DECISION:** `$TotalInventoryAgreegation!=empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$TotalInventoryAgreegation`
   ➔ **If [false]:**
      1. **Create **EcoATM_Inventory.AggregateInventoryTotal** (Result: **$NewTotalInventoryAgreegation**)
      - Set **AggregateInventoryWeek_AggregateInventoryTotal** = `$AggregatedInventoryWeek`**
      2. 🏁 **END:** Return `$NewTotalInventoryAgreegation`

**Final Result:** This process concludes by returning a [Object] value.