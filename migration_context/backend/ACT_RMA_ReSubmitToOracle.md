# Microflow Detailed Specification: ACT_RMA_ReSubmitToOracle

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'ResubmitRMA'`**
2. **Create Variable **$Description** = `'Start resubmit RMA to Oracle [RMA Number: '+$RMA/Number`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Call Microflow **EcoATM_RMA.SUB_RMA_SendRMAToOracle** (Result: **$CreateOrderResponse**)**
5. **DB Retrieve **EcoATM_RMA.RMAStatus** Filter: `[ ( SystemStatus = 'Open' ) ]` (Result: **$RMAStatus_Open**)**
6. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[id = $currentUser]` (Result: **$SalesPerson**)**
7. 🔀 **DECISION:** `$CreateOrderResponse!=empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$CreateOrderResponse/ReturnCode='00'`
         ➔ **If [false]:**
            1. **Update **$RMA** (and Save to DB)
      - Set **RMA_EcoATMDirectUser_ReviewedBy** = `$SalesPerson`
      - Set **IsSuccessful** = `false`
      - Set **OracleRMAStatus** = `$CreateOrderResponse/ReturnMessage`
      - Set **OracleHTTPCode** = `$CreateOrderResponse/HTTPCode`
      - Set **OracleJSONResponse** = `$CreateOrderResponse/JSONResponse`**
            2. **Call Microflow **EcoATM_PWSIntegration.SUB_PWSErrorCode_GetMessage** (Result: **$PWSResponseConfig_1**)**
            3. **Create **EcoATM_PWS.UserMessage** (Result: **$UserMessage_Submit_1_1_1**)
      - Set **Title** = `'Submit RMA failed'`
      - Set **Message** = `if($PWSResponseConfig_1!=empty and $PWSResponseConfig_1/ByPassForUser=false) then $PWSResponseConfig_1/UserErrorCode + ' - ' + $PWSResponseConfig_1/UserErrorMessage else 'RMA is not created. Please try Re-Submitting the RMA'`
      - Set **CSSClass** = `'pws-file-upload-error'`
      - Set **IsSuccess** = `false`**
            4. **Maps to Page: **EcoATM_RMA.RMADetails_Message_View****
            5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            6. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Update **$RMA** (and Save to DB)
      - Set **ApprovalDate** = `[%CurrentDateTime%]`
      - Set **RMA_RMAStatus** = `$RMAStatus_Open`
      - Set **OracleRMAStatus** = `$CreateOrderResponse/ReturnMessage`
      - Set **OracleJSONResponse** = `$CreateOrderResponse/JSONResponse`
      - Set **OracleHTTPCode** = `$CreateOrderResponse/HTTPCode`
      - Set **OracleNumber** = `$CreateOrderResponse/OrderNumber`
      - Set **OracleId** = `$CreateOrderResponse/OrderId`**
            2. **Create **EcoATM_PWS.UserMessage** (Result: **$UserMessage_Submit**)
      - Set **Title** = `'Return Request Approved'`
      - Set **Message** = `'The buyer will be notified to ship the devices back.'`
      - Set **CSSClass** = `'pws-file-upload-success'`
      - Set **IsSuccess** = `true`**
            3. **Maps to Page: **EcoATM_RMA.RMADetails_Message_View****
            4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            5. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$RMA** (and Save to DB)
      - Set **RMA_EcoATMDirectUser_ReviewedBy** = `$SalesPerson`
      - Set **IsSuccessful** = `false`**
      2. **Create **EcoATM_PWS.UserMessage** (Result: **$UserMessage_Submit_1**)
      - Set **Title** = `'Return Request Approved'`
      - Set **Message** = `'The buyer will be notified to ship the devices back.'`
      - Set **CSSClass** = `'pws-file-upload-error'`
      - Set **IsSuccess** = `false`**
      3. **Maps to Page: **EcoATM_RMA.RMADetails_Message_View****
      4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.