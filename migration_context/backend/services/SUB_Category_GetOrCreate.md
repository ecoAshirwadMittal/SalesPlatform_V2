# Microflow Detailed Specification: SUB_Category_GetOrCreate

### 📥 Inputs (Parameters)
- **$Category** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `trim($Category)!=''`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_PWSMDM.Category** Filter: `[Category=$Category]` (Result: **$TargetCategory**)**
      2. 🔀 **DECISION:** `$TargetCategory!=empty`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `$TargetCategory`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Info****
            2. **Create **EcoATM_PWSMDM.Category** (Result: **$NewCategory**)
      - Set **Category** = `$Category`
      - Set **IsEnabledForFilter** = `true`
      - Set **Rank** = `0`
      - Set **DisplayName** = `$Category`**
            3. 🏁 **END:** Return `$NewCategory`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
      2. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.