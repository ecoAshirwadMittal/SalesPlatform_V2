# Microflow Detailed Specification: SUB_Condition_GetOrCreate

### 📥 Inputs (Parameters)
- **$Condition** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `trim($Condition)!=''`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_PWSMDM.Condition** Filter: `[Condition=$Condition]` (Result: **$TargetCondition**)**
      2. 🔀 **DECISION:** `$TargetCondition!=empty`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `$TargetCondition`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Info****
            2. **Create **EcoATM_PWSMDM.Condition** (Result: **$NewCondition**)
      - Set **Condition** = `$Condition`
      - Set **Rank** = `0`**
            3. 🏁 **END:** Return `$NewCondition`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
      2. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.