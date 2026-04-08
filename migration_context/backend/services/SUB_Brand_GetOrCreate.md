# Microflow Detailed Specification: SUB_Brand_GetOrCreate

### 📥 Inputs (Parameters)
- **$Brand** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `trim($Brand)!=''`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
      2. 🏁 **END:** Return `empty`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_PWSMDM.Brand** Filter: `[Brand=$Brand]` (Result: **$TargetBrand**)**
      2. 🔀 **DECISION:** `$TargetBrand!=empty`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `$TargetBrand`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Info****
            2. **Create **EcoATM_PWSMDM.Brand** (Result: **$NewBrand**)
      - Set **Brand** = `$Brand`
      - Set **IsEnabledForFilter** = `true`
      - Set **Rank** = `0`
      - Set **DisplayName** = `$Brand`**
            3. 🏁 **END:** Return `$NewBrand`

**Final Result:** This process concludes by returning a [Object] value.