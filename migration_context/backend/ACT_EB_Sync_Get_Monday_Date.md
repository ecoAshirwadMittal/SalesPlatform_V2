# Microflow Detailed Specification: ACT_EB_Sync_Get_Monday_Date

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `@EcoATM_EB.const_EB_Sync_Test_Mode = true`
   ➔ **If [true]:**
      1. **Create Variable **$testmondaydate** = `@EcoATM_EB.const_EB_Sync_Test_Date`**
      2. 🏁 **END:** Return `$testmondaydate`
   ➔ **If [false]:**
      1. **Create Variable **$mondaydate** = `formatDateTime(addDays(beginOfWeek([%CurrentDateTime%]), 5), 'yyyy-MM-dd')`**
      2. 🏁 **END:** Return `$mondaydate`

**Final Result:** This process concludes by returning a [String] value.