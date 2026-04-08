# Microflow Detailed Specification: SUB_Execute_CleanStep

### 📥 Inputs (Parameters)
- **$query** (Type: Variable)
- **$StepInfo** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **JavaCallAction**
3. 🔀 **DECISION:** `$IsSuccess`
   ➔ **If [true]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Error****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.