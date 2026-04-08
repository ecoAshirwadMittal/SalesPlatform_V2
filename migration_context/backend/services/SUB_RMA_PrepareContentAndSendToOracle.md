# Microflow Detailed Specification: SUB_RMA_PrepareContentAndSendToOracle

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Description** = `'Prepare data and submit to Oracle RMA [RMAId;'+$RMA/Number+']'`**
2. **Call Microflow **Custom_Logging.SUB_Log_Info****
3. **Call Microflow **EcoATM_RMA.SUB_RMA_PrepareOraclePayload** (Result: **$JSONContent**)**
4. 🔀 **DECISION:** `$JSONContent!=empty`
   ➔ **If [true]:**
      1. **Update **$RMA**
      - Set **JSONContent** = `$JSONContent`**
      2. **Call Microflow **EcoATM_RMA.SUB_RMA_SendRMAToOracle** (Result: **$CreateOrderResponse**)**
      3. **Call Microflow **Custom_Logging.SUB_Log_Info****
      4. 🏁 **END:** Return `$CreateOrderResponse`
   ➔ **If [false]:**
      1. **Create **EcoATM_PWSIntegration.OracleResponse** (Result: **$GeneralErrorCreateOrderResponse**)
      - Set **ReturnMessage** = `'JSON Content Generation Failed'`**
      2. **Call Microflow **Custom_Logging.SUB_Log_Error****
      3. 🏁 **END:** Return `$GeneralErrorCreateOrderResponse`

**Final Result:** This process concludes by returning a [Object] value.