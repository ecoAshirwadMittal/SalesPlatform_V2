# Microflow Detailed Specification: SUB_Color_GetOrCreate

### 📥 Inputs (Parameters)
- **$Color** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `trim($Color)!=''`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_PWSMDM.Color** Filter: `[Color=$Color]` (Result: **$TargetColor**)**
      2. 🔀 **DECISION:** `$TargetColor!=empty`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `$TargetColor`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Info****
            2. **Create **EcoATM_PWSMDM.Color** (Result: **$NewColor**)
      - Set **Color** = `$Color`
      - Set **IsEnabledForFilter** = `true`
      - Set **Rank** = `0`
      - Set **DisplayName** = `$Color`**
            3. 🏁 **END:** Return `$NewColor`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
      2. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.