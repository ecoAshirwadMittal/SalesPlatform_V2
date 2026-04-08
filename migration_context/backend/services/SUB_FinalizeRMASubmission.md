# Microflow Detailed Specification: SUB_FinalizeRMASubmission

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)
- **$RMAFile** (Type: EcoATM_RMA.RMAFile)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$RMAItemList** (Type: EcoATM_RMA.RMAItem)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$RMARequestId** = `''`**
2. **Retrieve related **RMAId_BuyerCode** via Association from **$BuyerCode** (Result: **$RMAId**)**
3. 🔀 **DECISION:** `$RMAId != empty`
   ➔ **If [true]:**
      1. **Update **$RMAId** (and Save to DB)
      - Set **MaxRMAId** = `$RMAId/MaxRMAId + 1`**
      2. **Update Variable **$RMARequestId** = `'RMA' + $BuyerCode/Code + formatDateTime([%CurrentDateTime%], 'yy') + (if $RMAId/MaxRMAId < 10 then '00' else if $RMAId/MaxRMAId < 100 then '0' else '') + $RMAId/MaxRMAId`**
      3. **Call Microflow **EcoATM_RMA.SUB_CalculateRMARequestSummary****
      4. **Update **$RMA** (and Save to DB)
      - Set **Number** = `$RMARequestId`
      - Set **SubmittedDate** = `[%CurrentDateTime%]`**
      5. **Commit/Save **$RMAItemList** to Database**
      6. **Commit/Save **$RMAFile** to Database**
      7. **Call Microflow **EcoATM_RMA.SUB_SendEmail_RMASubmitted****
      8. **Call Microflow **EcoATM_RMA.SUB_SendRMADetailsToSnowflake****
      9. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Create **EcoATM_RMA.RMAId** (Result: **$NewRMAId**)
      - Set **RMAId_BuyerCode** = `$BuyerCode`
      - Set **MaxRMAId** = `1`**
      2. **Update Variable **$RMARequestId** = `'RMA' + $BuyerCode/Code + formatDateTime([%CurrentDateTime%], 'yy') + '001'`**
      3. **Call Microflow **EcoATM_RMA.SUB_CalculateRMARequestSummary****
      4. **Update **$RMA** (and Save to DB)
      - Set **Number** = `$RMARequestId`
      - Set **SubmittedDate** = `[%CurrentDateTime%]`**
      5. **Commit/Save **$RMAItemList** to Database**
      6. **Commit/Save **$RMAFile** to Database**
      7. **Call Microflow **EcoATM_RMA.SUB_SendEmail_RMASubmitted****
      8. **Call Microflow **EcoATM_RMA.SUB_SendRMADetailsToSnowflake****
      9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.