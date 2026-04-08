# Microflow Detailed Specification: SUB_Carrier_GetOrCreate

### 📥 Inputs (Parameters)
- **$Carrier** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `trim($Carrier)!=''`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
      2. 🏁 **END:** Return `empty`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_PWSMDM.Carrier** Filter: `[Carrier=$Carrier]` (Result: **$TargetCarrier**)**
      2. 🔀 **DECISION:** `$TargetCarrier!=empty`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `$TargetCarrier`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Info****
            2. **Create **EcoATM_PWSMDM.Carrier** (Result: **$NewCarrier**)
      - Set **Carrier** = `$Carrier`
      - Set **IsEnabledForFilter** = `true`
      - Set **Rank** = `0`
      - Set **DisplayName** = `$Carrier`**
            3. 🏁 **END:** Return `$NewCarrier`

**Final Result:** This process concludes by returning a [Object] value.