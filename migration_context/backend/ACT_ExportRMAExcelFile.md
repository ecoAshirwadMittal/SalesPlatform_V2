# Microflow Detailed Specification: ACT_ExportRMAExcelFile

### 📥 Inputs (Parameters)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'RMARequestsExport'`**
2. **Create Variable **$Description** = `'RMA Requests Export. [BuyerCode:'+$BuyerCode/Code+']'`**
3. **Create **EcoATM_RMA.RMAExcelDocument** (Result: **$NewRMAExcelDocument**)
      - Set **DeleteAfterDownload** = `true`**
4. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name = 'RMAExport']` (Result: **$MxTemplate**)**
5. **Create Variable **$FileName** = `'PWS Returns'+ '_'+ formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`**
6. **Retrieve related **RMA_BuyerCode** via Association from **$BuyerCode** (Result: **$RMAList**)**
7. 🔀 **DECISION:** `$RMAList != empty`
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