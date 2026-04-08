# Microflow Detailed Specification: SUB_RMA_SendRMAToOracle

### 📥 Inputs (Parameters)
- **$JSONContent** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_PWSIntegration.SUB_PWSConfiguration_GetOrCreate** (Result: **$PWSConfiguration**)**
2. 🔀 **DECISION:** `$PWSConfiguration/IsOracleCreateRMAAPIOn`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_PWSIntegration.CWS_PostToken** (Result: **$Token**)**
      2. 🔀 **DECISION:** `$Token!=empty`
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_RMA.CWS_PostCreateRMA** (Result: **$CreateOrderResponse**)**
            2. 🏁 **END:** Return `$CreateOrderResponse`
         ➔ **If [false]:**
            1. **Create **EcoATM_PWSIntegration.OracleResponse** (Result: **$GeneralErrorCreateOrderResponse**)
      - Set **ReturnMessage** = `'No Token Generated'`**
            2. 🏁 **END:** Return `$GeneralErrorCreateOrderResponse`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
      2. **Create **EcoATM_PWSIntegration.OracleResponse** (Result: **$GeneralErrorCreateOrderResponse_1**)
      - Set **ReturnMessage** = `'Toggle Turned Off'`**
      3. 🏁 **END:** Return `$GeneralErrorCreateOrderResponse_1`

**Final Result:** This process concludes by returning a [Object] value.