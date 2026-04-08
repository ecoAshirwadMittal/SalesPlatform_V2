# Microflow Detailed Specification: ACT_RMADetails_CompleteReview

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'RMAReview'`**
2. **Create Variable **$Description** = `'Complete review for RMA [RMA:'+$RMA/Number+'] [BuyerCode:'+$RMA/EcoATM_RMA.RMA_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code+']'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Call Microflow **EcoATM_RMA.SUB_CalculateApprovedRMAValues** (Result: **$Status**)**
5. **Retrieve related **RMAItem_RMA** via Association from **$RMA** (Result: **$RMAItemList**)**
6. 🔄 **LOOP:** For each **$IteratorRMAItem** in **$RMAItemList**
   │ 1. **Update **$IteratorRMAItem**
      - Set **StatusDisplay** = `if $IteratorRMAItem/Status= EcoATM_RMA.ENUM_RMAItemStatus.Approve then 'Approved' else if $IteratorRMAItem/Status= EcoATM_RMA.ENUM_RMAItemStatus.Decline then 'Declined' else ''`**
   └─ **End Loop**
7. **Commit/Save **$RMAItemList** to Database**
8. **List Operation: **Filter** on **$undefined** where `EcoATM_RMA.ENUM_RMAItemStatus.Approve` (Result: **$RMAItemList_Approved**)**
9. **AggregateList**
10. **AggregateList**
11. **Update **$RMA**
      - Set **ApprovedCount** = `$Count_Approved`
      - Set **DeclinedCount** = `$Count_Total-$Count_Approved`**
12. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[id = $currentUser]` (Result: **$SalesPerson**)**
13. 🔀 **DECISION:** `$Status = 'Approved'`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_RMA.SUB_RMA_PrepareContentAndSendToOracle** (Result: **$CreateOrderResponse**)**
      2. 🔀 **DECISION:** `$CreateOrderResponse!=empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$CreateOrderResponse/ReturnCode='00' or true`
               ➔ **If [true]:**
                  1. **DB Retrieve **EcoATM_RMA.RMAStatus** Filter: `[ ( SystemStatus = 'Approved' ) ]` (Result: **$RMAStatus_Approved**)**
                  2. **Update **$RMA** (and Save to DB)
      - Set **ApprovalDate** = `[%CurrentDateTime%]`
      - Set **RMA_EcoATMDirectUser_ReviewedBy** = `$SalesPerson`
      - Set **RMA_RMAStatus** = `$RMAStatus_Approved`
      - Set **OracleRMAStatus** = `$CreateOrderResponse/ReturnMessage`
      - Set **OracleJSONResponse** = `$CreateOrderResponse/JSONResponse`
      - Set **OracleHTTPCode** = `$CreateOrderResponse/HTTPCode`
      - Set **OracleNumber** = `$CreateOrderResponse/OrderNumber`
      - Set **OracleId** = `$CreateOrderResponse/OrderId`
      - Set **SystemStatus** = `$RMAStatus_Approved/SystemStatus`**
                  3. **Create **EcoATM_PWS.UserMessage** (Result: **$UserMessage_Submit**)
      - Set **Title** = `'RMA Request Approved'`
      - Set **Message** = `'The buyer will be notified to ship the devices back.'`
      - Set **CSSClass** = `'pws-file-upload-success'`
      - Set **IsSuccess** = `true`**
                  4. **Maps to Page: **EcoATM_RMA.RMADetails_Message_View****
                  5. **Maps to Page: **EcoATM_RMA.RMA_RequestsOverview_Sales****
                  6. **Call Microflow **EcoATM_RMA.SUB_SendEmail_RMAApproved****
                  7. **Call Microflow **EcoATM_RMA.SUB_SendRMADetailsToSnowflake****
                  8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  9. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Update **$RMA** (and Save to DB)
      - Set **RMA_EcoATMDirectUser_ReviewedBy** = `$SalesPerson`
      - Set **IsSuccessful** = `false`
      - Set **OracleRMAStatus** = `$CreateOrderResponse/ReturnMessage`
      - Set **OracleHTTPCode** = `$CreateOrderResponse/HTTPCode`
      - Set **OracleJSONResponse** = `$CreateOrderResponse/JSONResponse`**
                  2. **Call Microflow **EcoATM_PWSIntegration.SUB_PWSErrorCode_GetMessage** (Result: **$PWSResponseConfig**)**
                  3. **Create **EcoATM_PWS.UserMessage** (Result: **$UserMessage_Submit_1_1**)
      - Set **Title** = `'Submit RMA failed'`
      - Set **Message** = `if($PWSResponseConfig!=empty and $PWSResponseConfig/ByPassForUser=false) then $PWSResponseConfig/UserErrorCode + ' - ' + $PWSResponseConfig/UserErrorMessage else 'RMA is not created. Please try Re-Submitting the RMA'`
      - Set **CSSClass** = `'pws-file-upload-error'`
      - Set **IsSuccess** = `false`**
                  4. **Maps to Page: **EcoATM_RMA.RMADetails_Message_View****
                  5. **Maps to Page: **EcoATM_RMA.RMA_RequestsOverview_Sales****
                  6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  7. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Update **$RMA** (and Save to DB)
      - Set **RMA_EcoATMDirectUser_ReviewedBy** = `$SalesPerson`
      - Set **IsSuccessful** = `false`**
            2. **Create **EcoATM_PWS.UserMessage** (Result: **$UserMessage_Submit_1**)
      - Set **Title** = `'RMA Submission Failed'`
      - Set **Message** = `'RMA is not placed. Please try Re-Submitting the RMA'`
      - Set **CSSClass** = `'pws-file-upload-error'`
      - Set **IsSuccess** = `false`**
            3. **Maps to Page: **EcoATM_RMA.RMADetails_Message_View****
            4. **Maps to Page: **EcoATM_RMA.RMA_RequestsOverview_Sales****
            5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            6. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$Status = 'Declined'`
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **DB Retrieve **EcoATM_RMA.RMAStatus** Filter: `[ ( SystemStatus = 'Declined' ) ]` (Result: **$RMAStatus_Declined**)**
            2. **Update **$RMA** (and Save to DB)
      - Set **ApprovalDate** = `[%CurrentDateTime%]`
      - Set **RMA_EcoATMDirectUser_ReviewedBy** = `$SalesPerson`
      - Set **RMA_RMAStatus** = `$RMAStatus_Declined`**
            3. **Maps to Page: **EcoATM_RMA.RMA_RequestsOverview_Sales****
            4. **Call Microflow **EcoATM_RMA.SUB_SendRMADetailsToSnowflake****
            5. **Create **EcoATM_PWS.UserMessage** (Result: **$UserMessage_Submit_2**)
      - Set **Title** = `'RMA Request Declined'`
      - Set **Message** = `'The buyer will be notified.'`
      - Set **CSSClass** = `'pws-file-upload-success'`
      - Set **IsSuccess** = `true`**
            6. **Maps to Page: **EcoATM_RMA.RMADetails_Message_View****
            7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.