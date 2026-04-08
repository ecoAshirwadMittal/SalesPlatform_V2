# Microflow Detailed Specification: SUB_Capacity_GetOrCreate

### 📥 Inputs (Parameters)
- **$Capacity** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `trim($Capacity)!=''`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
      2. 🏁 **END:** Return `empty`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_PWSMDM.Capacity** Filter: `[Capacity=$Capacity]` (Result: **$TargetCapacity**)**
      2. 🔀 **DECISION:** `$TargetCapacity!=empty`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `$TargetCapacity`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Info****
            2. **Create **EcoATM_PWSMDM.Capacity** (Result: **$NewCapacity**)
      - Set **Capacity** = `$Capacity`
      - Set **IsEnabledForFilter** = `true`
      - Set **Rank** = `0`
      - Set **DisplayName** = `$Capacity`**
            3. 🏁 **END:** Return `$NewCapacity`

**Final Result:** This process concludes by returning a [Object] value.