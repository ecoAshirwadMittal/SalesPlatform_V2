# Microflow Detailed Specification: SUB_Grade_GetOrCreate

### 📥 Inputs (Parameters)
- **$Grade** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `trim($Grade)!=''`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_PWSMDM.Grade** Filter: `[Grade=$Grade]` (Result: **$TargetGrade**)**
      2. 🔀 **DECISION:** `$TargetGrade!=empty`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `$TargetGrade`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Info****
            2. **Create **EcoATM_PWSMDM.Grade** (Result: **$NewGrade**)
      - Set **Grade** = `$Grade`
      - Set **IsEnabledForFilter** = `true`
      - Set **Rank** = `0`
      - Set **DisplayName** = `$Grade`**
            3. 🏁 **END:** Return `$NewGrade`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
      2. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.