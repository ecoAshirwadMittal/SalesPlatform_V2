# Microflow Detailed Specification: DS_FetchDespocoConfiguration

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWSIntegration.DeposcoConfig**  (Result: **$DeposcoConfig**)**
2. 🔀 **DECISION:** `$DeposcoConfig != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$DeposcoConfig`
   ➔ **If [false]:**
      1. **Create **EcoATM_PWSIntegration.DeposcoConfig** (Result: **$NewDeposcoConfig**)**
      2. 🏁 **END:** Return `$NewDeposcoConfig`

**Final Result:** This process concludes by returning a [Object] value.