# Microflow Detailed Specification: SUB_PWSConfiguration_GetOrCreate

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWSIntegration.PWSConfiguration**  (Result: **$PWSConfiguration**)**
2. 🔀 **DECISION:** `$PWSConfiguration!=empty`
   ➔ **If [true]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. 🏁 **END:** Return `$PWSConfiguration`
   ➔ **If [false]:**
      1. **Create **EcoATM_PWSIntegration.PWSConfiguration** (Result: **$NewPWSConfiguration**)**
      2. **Call Microflow **Custom_Logging.SUB_Log_Info****
      3. 🏁 **END:** Return `$NewPWSConfiguration`

**Final Result:** This process concludes by returning a [Object] value.