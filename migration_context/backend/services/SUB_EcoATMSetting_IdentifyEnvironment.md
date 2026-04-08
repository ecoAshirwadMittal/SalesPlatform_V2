# Microflow Detailed Specification: SUB_EcoATMSetting_IdentifyEnvironment

### 📥 Inputs (Parameters)
- **$EcoATMSetting** (Type: Custom_Logging.EcoATMSetting)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **Environment_EcoATMSetting** via Association from **$EcoATMSetting** (Result: **$EnvironmentList**)**
2. **JavaCallAction**
3. **List Operation: **FindByExpression** on **$undefined** where `contains($currentObject/Url,$ApplicationURL)` (Result: **$CurrentEnvironment**)**
4. 🔀 **DECISION:** `$CurrentEnvironment!=empty`
   ➔ **If [true]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. 🏁 **END:** Return `$CurrentEnvironment`
   ➔ **If [false]:**
      1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/IsDefault` (Result: **$DefaultEnvironment**)**
      2. **Call Microflow **Custom_Logging.SUB_Log_Info****
      3. 🏁 **END:** Return `$DefaultEnvironment`

**Final Result:** This process concludes by returning a [Object] value.