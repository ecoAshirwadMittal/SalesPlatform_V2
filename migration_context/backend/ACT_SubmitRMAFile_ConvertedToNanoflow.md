# Microflow Detailed Specification: ACT_SubmitRMAFile_ConvertedToNanoflow

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)
- **$RMAFile** (Type: EcoATM_RMA.RMAFile)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'RMAUploadNewRequest'`**
2. **Create Variable **$Description** = `'importing New RMA Request excel document for [BuyerCode:'+ $BuyerCode/Code + ']'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **ImportExcelData**
5. 🔀 **DECISION:** `$RMARequest_ImportHelperList != empty`
   ➔ **If [false]:**
      1. **Delete**
      2. **Delete**
      3. **Update **$RMAFile** (and Save to DB)
      - Set **InvalidReason** = `'Invalid file. Zero IMEIs or serial numbers found.'`**
      4. **Create **EcoATM_PWS.UserMessage** (Result: **$NewUserMessage_1_1**)
      - Set **Title** = `'Your file is incomplete'`
      - Set **Message** = `'Invalid file. Zero IMEIs or serial numbers found. Please reach out to ecoATM support if you need assistance.'`
      - Set **CSSClass** = `'rma-file-upload-error'`
      - Set **IsSuccess** = `false`**
      5. **Maps to Page: **EcoATM_RMA.RMA_Message_View****
      6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      7. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/Return_Reason = empty or trim($currentObject/Return_Reason) = ''` (Result: **$RMARequest_ImportHelper_EmptyReasonList**)**
      2. 🔀 **DECISION:** `$RMARequest_ImportHelper_EmptyReasonList = empty`
         ➔ **If [false]:**
            1. **Delete**
            2. **Delete**
            3. **Update **$RMAFile** (and Save to DB)
      - Set **InvalidReason** = `length($RMARequest_ImportHelper_EmptyReasonList) + ' items are missing a return reason'`**
            4. **Create **EcoATM_PWS.UserMessage** (Result: **$NewUserMessage_1**)
      - Set **Title** = `'Your file is incomplete'`
      - Set **Message** = `length($RMARequest_ImportHelper_EmptyReasonList) + ' items are missing a return reason Please reach out to ecoATM support if you need assistance.'`
      - Set **CSSClass** = `'rma-file-upload-error'`
      - Set **IsSuccess** = `false`**
            5. **Maps to Page: **EcoATM_RMA.RMA_Message_View****
            6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            7. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_RMA.VAL_RMARequestFile** (Result: **$RMAItemList**)**
            2. 🔀 **DECISION:** `$RMAFile/IsValid`
               ➔ **If [true]:**
                  1. **Create Variable **$RMARequestId** = `''`**
                  2. **Retrieve related **RMAId_BuyerCode** via Association from **$BuyerCode** (Result: **$RMAId**)**
                  3. 🔀 **DECISION:** `$RMAId != empty`
                     ➔ **If [true]:**
                        1. **Update **$RMAId** (and Save to DB)
      - Set **MaxRMAId** = `$RMAId/MaxRMAId + 1`**
                        2. **Update Variable **$RMARequestId** = `'RMA' + $BuyerCode/Code + formatDateTime([%CurrentDateTime%], 'yy') + $RMAId/MaxRMAId`**
                        3. **Call Microflow **EcoATM_RMA.SUB_CalculateRMARequestSummary****
                        4. **DB Retrieve **EcoATM_RMA.RMAStatus** Filter: `[ ( SystemStatus = 'Pending Approval' ) ]` (Result: **$RMAStatus_PendingApproval**)**
                        5. **Update **$RMA** (and Save to DB)
      - Set **Number** = `$RMARequestId`
      - Set **SubmittedDate** = `[%CurrentDateTime%]`
      - Set **RMA_RMAStatus** = `$RMAStatus_PendingApproval`**
                        6. **Commit/Save **$RMAItemList** to Database**
                        7. **Commit/Save **$RMAFile** to Database**
                        8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        9. **Create **EcoATM_PWS.UserMessage** (Result: **$UserMessage**)
      - Set **Title** = `'Your return has been submitted!'`
      - Set **Message** = `'Please do not ship any devices yet. ecoATM will review your request and notify you upon approval.'`
      - Set **CSSClass** = `'pws-file-upload-success'`
      - Set **IsSuccess** = `true`**
                        10. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
                        11. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Create **EcoATM_RMA.RMAId** (Result: **$NewRMAId**)
      - Set **RMAId_BuyerCode** = `$BuyerCode`
      - Set **MaxRMAId** = `1`**
                        2. **Update Variable **$RMARequestId** = `'RMA' + $BuyerCode/Code + formatDateTime([%CurrentDateTime%], 'yy') + '001'`**
                        3. **Call Microflow **EcoATM_RMA.SUB_CalculateRMARequestSummary****
                        4. **DB Retrieve **EcoATM_RMA.RMAStatus** Filter: `[ ( SystemStatus = 'Pending Approval' ) ]` (Result: **$RMAStatus_PendingApproval**)**
                        5. **Update **$RMA** (and Save to DB)
      - Set **Number** = `$RMARequestId`
      - Set **SubmittedDate** = `[%CurrentDateTime%]`
      - Set **RMA_RMAStatus** = `$RMAStatus_PendingApproval`**
                        6. **Commit/Save **$RMAItemList** to Database**
                        7. **Commit/Save **$RMAFile** to Database**
                        8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        9. **Create **EcoATM_PWS.UserMessage** (Result: **$UserMessage**)
      - Set **Title** = `'Your return has been submitted!'`
      - Set **Message** = `'Please do not ship any devices yet. ecoATM will review your request and notify you upon approval.'`
      - Set **CSSClass** = `'pws-file-upload-success'`
      - Set **IsSuccess** = `true`**
                        10. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
                        11. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Delete**
                  2. **Delete**
                  3. **List Operation: **Filter** on **$undefined** where `empty` (Result: **$RMAItemList_InValidIMEI_List**)**
                  4. **Update **$RMAFile** (and Save to DB)
      - Set **InvalidReason** = `length($RMAItemList_InValidIMEI_List) + ' items have invalid IMEIs or serial numbers'`**
                  5. **Create **EcoATM_PWS.UserMessage** (Result: **$NewUserMessage**)
      - Set **Title** = `'Your file is incomplete'`
      - Set **Message** = `length($RMAItemList_InValidIMEI_List) + ' items have invalid IMEIs or serial numbers Please reach out to ecoATM support if you need assistance.'`
      - Set **CSSClass** = `'rma-file-upload-error'`
      - Set **IsSuccess** = `false`**
                  6. **Maps to Page: **EcoATM_RMA.RMA_Message_View****
                  7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.