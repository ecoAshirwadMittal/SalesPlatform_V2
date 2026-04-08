# Microflow Detailed Specification: SUB_PWSErrorCode_GetMessage

### 📥 Inputs (Parameters)
- **$ErrorCode** (Type: Variable)
- **$SourceSystem** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWSIntegration.PWSResponseConfig** Filter: `[SourceSystem=$SourceSystem] [SourceErrorCode=$ErrorCode]` (Result: **$PWSResponseConfig**)**
2. 🔀 **DECISION:** `$PWSResponseConfig!=empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$PWSResponseConfig`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
      2. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.