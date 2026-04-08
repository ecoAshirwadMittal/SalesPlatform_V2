# Microflow Detailed Specification: ACT_DownloadRMAFile

### 📥 Inputs (Parameters)
- **$RMAUiHelper** (Type: EcoATM_RMA.RMAUiHelper)
- **$RMAMasterHelper** (Type: EcoATM_RMA.RMAMasterHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'RMARequestsExport'`**
2. **Create Variable **$Description** = `'RMA Requests Export for Status:' +$RMAUiHelper/RMASystemStatus`**
3. **Create **EcoATM_RMA.RMAExcelDocument** (Result: **$NewRMAExcelDocument**)
      - Set **DeleteAfterDownload** = `true`**
4. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name = 'RMAReturns']` (Result: **$MxTemplate**)**
5. **Create Variable **$FileName** = `'RMA_Returns'+ '_' + (if $RMAUiHelper/HeaderLabel = empty then 'Total' else getKey($RMAUiHelper/HeaderLabel)) + '_'+ formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`**
6. **Retrieve related **RMAUiHelper_RMAStatus** via Association from **$RMAUiHelper** (Result: **$RMAStatusList**)**
7. **Call Microflow **EcoATM_RMA.DS_GetRMAsByStatus** (Result: **$RMAList**)**
8. 🔀 **DECISION:** `$RMAList != empty`
   ➔ **If [true]:**
      1. 🔄 **LOOP:** For each **$IteratorRMA** in **$RMAList**
         │ 1. **Update **$IteratorRMA**
      - Set **RMA_RMAExcelDocument** = `$NewRMAExcelDocument`**
         └─ **End Loop**
      2. **Commit/Save **$RMAList** to Database**
      3. **Call Microflow **EcoATM_RMA.SUB_RMARequests_GenerateReport****
      4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      5. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.