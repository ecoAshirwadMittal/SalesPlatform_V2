# Nanoflow: ACT_SubmitRMAFile

**Allowed Roles:** EcoATM_RMA.Administrator, EcoATM_RMA.Bidder, EcoATM_RMA.SalesOps, EcoATM_RMA.SalesRep

## 📥 Inputs

- **$RMA** (EcoATM_RMA.RMA)
- **$RMAFile** (EcoATM_RMA.RMAFile)
- **$BuyerCode** (EcoATM_BuyerManagement.BuyerCode)

## ⚙️ Execution Flow

1. **Create Variable **$TimerName** = `'RMAUploadNewRequest'`**
2. **Create Variable **$Description** = `'importing New RMA Request excel document for [BuyerCode:'+ $BuyerCode/Code + ']'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. 🔀 **DECISION:** `$RMAFile/Size > 0`
   ➔ **If [true]:**
      1. **Create **EcoATM_RMA.FileUploadProcessHelper** (Result: **$NewFileUploadProcessHelper**)
      - Set **CurrentPercentage** = `0`
      - Set **Message** = `'Uploading file'`
      - Set **FileName** = `$RMAFile/Name`**
      2. **Open Page: **EcoATM_RMA.FileUpload_LoadingBar****
      3. **Call Microflow **EcoATM_RMA.SUB_ProcessFileUpload** (Result: **$RMARequest_ImportHelper**)**
      4. 🔀 **DECISION:** `$RMARequest_ImportHelper != empty`
         ➔ **If [true]:**
            1. **Update **$NewFileUploadProcessHelper**
      - Set **CurrentPercentage** = `30`
      - Set **FileName** = `$RMAFile/Name`
      - Set **Message** = `'Processing file'`**
            2. **Call Microflow **EcoATM_RMA.SUB_CheckEmptyRecords** (Result: **$EmptyRecordCount**)**
            3. 🔀 **DECISION:** `$EmptyRecordCount = 0`
               ➔ **If [false]:**
                  1. **Call Microflow **EcoATM_RMA.SUB_ProcessFailureCleanup****
                  2. **Close current page/popup**
                  3. **Create **EcoATM_PWS.UserMessage** (Result: **$UserMessage_EmptyRecordCheck**)
      - Set **Title** = `'Your file is incomplete'`
      - Set **Message** = `$EmptyRecordCount + ' items are missing IMEIs, serial numbers or return reasons Please reach out to ecoATM support if you need assistance.'`
      - Set **CSSClass** = `'rma-file-upload-error'`
      - Set **IsSuccess** = `false`**
                  4. **Open Page: **EcoATM_RMA.RMA_Message_View****
                  5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  6. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Update **$NewFileUploadProcessHelper**
      - Set **CurrentPercentage** = `70`
      - Set **FileName** = `$RMAFile/Name`
      - Set **Message** = `'Validating records'`**
                  2. **Call Microflow **EcoATM_RMA.SUB_ValidateRecords** (Result: **$RMAItemList**)**
                  3. 🔀 **DECISION:** `$RMAFile/IsValid`
                     ➔ **If [true]:**
                        1. **Update **$NewFileUploadProcessHelper**
      - Set **CurrentPercentage** = `90`
      - Set **FileName** = `$RMAFile/Name`
      - Set **Message** = `'Finalizing RMA submission'`**
                        2. **Call Microflow **EcoATM_RMA.SUB_FinalizeRMASubmission****
                        3. **Update **$NewFileUploadProcessHelper**
      - Set **CurrentPercentage** = `100`
      - Set **FileName** = `$RMAFile/Name`
      - Set **Message** = `'RMA submitted'`**
                        4. **Close current page/popup**
                        5. **Create **EcoATM_PWS.UserMessage** (Result: **$UserMessage_Submit**)
      - Set **Title** = `'RMA Request Submitted!'`
      - Set **PrimaryMessage** = `empty`
      - Set **Message** = `'Do not ship devices. ecoATM will review your request and confirm which devices are approved for return.'`
      - Set **CSSClass** = `'pws-file-upload-success'`
      - Set **IsSuccess** = `true`**
                        6. **Open Page: **EcoATM_RMA.RMA_Message_View****
                        7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        8. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Retrieve related **InvalidRMAItem_UiHelper_RMA** via Association from **$RMA** (Result: **$InvalidRMAItemsList**)**
                        2. **List Operation: **Sort** on **$InvalidRMAItemsList** sorted by: changedDate (Descending) (Result: **$InvalidRMAItemsList_Sorted**)**
                        3. **Call Microflow **EcoATM_RMA.SUB_ProcessFailureCleanup****
                        4. **Close current page/popup**
                        5. **List Operation: **Head** on **$InvalidRMAItemsList_Sorted** (Result: **$InvalidRMAItems**)**
                        6. **Open Page: **EcoATM_RMA.InvalidRMA_Message_View****
                        7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        8. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **EcoATM_RMA.SUB_ProcessFailureCleanup****
            2. **Close current page/popup**
            3. **Create **EcoATM_PWS.UserMessage** (Result: **$UserMessage_FileUpload**)
      - Set **Title** = `'Your file is incomplete'`
      - Set **Message** = `'Invalid file. Zero IMEIs or serial numbers found. Please reach out to ecoATM support if you need assistance.'`
      - Set **CSSClass** = `'rma-file-upload-error'`
      - Set **IsSuccess** = `false`**
            4. **Open Page: **EcoATM_RMA.RMA_Message_View****
            5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            6. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return empty

## ⚠️ Error Handling

- On error in **Call Microflow **EcoATM_RMA.SUB_FinalizeRMASubmission**** → ExclusiveMerge
- On error in **Call Microflow **EcoATM_RMA.SUB_ProcessFileUpload** (Result: **$RMARequest_ImportHelper**)** → Call Microflow **EcoATM_RMA.SUB_ProcessFailureCleanup**

## 🏁 Returns
`Void`
