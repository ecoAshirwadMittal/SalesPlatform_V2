# Microflow Detailed Specification: DS_GerOrCreateAggregateInventoryHelper

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **Administration.Account** Filter: `[id = $currentUser]` (Result: **$Account**)**
2. **Retrieve related **AggregateInventoryHelper_Account** via Association from **$Account** (Result: **$AgreegateInventoryHelper**)**
3. 🔀 **DECISION:** `$AgreegateInventoryHelper!= empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$AgreegateInventoryHelper`
   ➔ **If [false]:**
      1. **Create **EcoATM_Inventory.AggregateInventoryHelper** (Result: **$NewAgreegateInventoryHelper**)
      - Set **AggregateInventoryHelper_Account** = `$Account`**
      2. 🏁 **END:** Return `$NewAgreegateInventoryHelper`

**Final Result:** This process concludes by returning a [Object] value.