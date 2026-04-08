# Microflow Detailed Specification: SUB_Model_GetOrCreate

### 📥 Inputs (Parameters)
- **$Model** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `trim($Model)!=''`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_PWSMDM.Model** Filter: `[Model=$Model]` (Result: **$TargetModel**)**
      2. 🔀 **DECISION:** `$TargetModel!=empty`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `$TargetModel`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Info****
            2. **Create **EcoATM_PWSMDM.Model** (Result: **$NewModel**)
      - Set **Model** = `$Model`
      - Set **Rank** = `0`
      - Set **DisplayName** = `$Model`**
            3. 🏁 **END:** Return `$NewModel`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
      2. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.