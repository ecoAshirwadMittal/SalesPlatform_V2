# Microflow Detailed Specification: SUB_InvalidRMA_GenerateReport

### 📥 Inputs (Parameters)
- **$FileName** (Type: Variable)
- **$Template** (Type: XLSReport.MxTemplate)
- **$InvalidRMAFileExport** (Type: EcoATM_RMA.InvalidRMAFileExport)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'RMARwquestsDataGenerateReport'`**
2. **Create Variable **$Description** = `'RMA Data Generate Report.'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **JavaCallAction**
5. **Update **$Document**
      - Set **Name** = `$FileName`**
6. **DownloadFile**
7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.