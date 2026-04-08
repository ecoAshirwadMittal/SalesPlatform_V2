# Microflow Detailed Specification: SUB_DAHelper_GerOrCreate

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **Administration.Account** Filter: `[id = $currentUser]` (Result: **$Account**)**
2. **Retrieve related **DAHelper_Account** via Association from **$Account** (Result: **$DAHelper**)**
3. 🔀 **DECISION:** `$DAHelper != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$DAHelper`
   ➔ **If [false]:**
      1. **Create **EcoATM_DA.DAHelper** (Result: **$NewDAHelper**)
      - Set **DAHelper_Account** = `$Account`
      - Set **DisplayDA_DataGrid** = `false`**
      2. 🏁 **END:** Return `$NewDAHelper`

**Final Result:** This process concludes by returning a [Object] value.