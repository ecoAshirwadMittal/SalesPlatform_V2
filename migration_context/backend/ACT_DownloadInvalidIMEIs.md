# Microflow Detailed Specification: ACT_DownloadInvalidIMEIs

### 📥 Inputs (Parameters)
- **$InvalidRMAItems** (Type: EcoATM_RMA.InvalidRMAItem_UiHelper)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'RMARequestsExport'`**
2. **Create Variable **$Description** = `'RMA Requests Export. [BuyerCode:'+$BuyerCode/Code+']'`**
3. **Create **EcoATM_RMA.InvalidRMAFileExport** (Result: **$NewInvalidRMAFile**)
      - Set **DeleteAfterDownload** = `true`**
4. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name = 'InvalidIMEIs']` (Result: **$MxTemplate**)**
5. **Create Variable **$FileName** = `'InvalidRMA'+ '_'+ formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`**
6. **Retrieve related **InvalidRMAItem_UiHelper_InvalidIMEI_ExportHelper** via Association from **$InvalidRMAItems** (Result: **$InvalidIMEI_ExportHelperList**)**
7. 🔄 **LOOP:** For each **$IteratorInvalidIMEI_ExportHelper** in **$InvalidIMEI_ExportHelperList**
   │ 1. **Update **$IteratorInvalidIMEI_ExportHelper**
      - Set **InvalidIMEI_ExportHelper_InvalidRMAFileExport** = `$NewInvalidRMAFile`**
   └─ **End Loop**
8. **Commit/Save **$InvalidIMEI_ExportHelperList** to Database**
9. **Call Microflow **EcoATM_RMA.SUB_InvalidRMA_GenerateReport****
10. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
11. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.