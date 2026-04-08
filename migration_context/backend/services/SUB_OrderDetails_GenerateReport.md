# Microflow Detailed Specification: SUB_OrderDetails_GenerateReport

### 📥 Inputs (Parameters)
- **$Template** (Type: XLSReport.MxTemplate)
- **$FileName** (Type: Variable)
- **$OrderDetailsExport** (Type: EcoATM_PWS.OrderDetailsExport)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'PWSOrderHistoryGenerateReport'`**
2. **Create Variable **$Description** = `'Ordered History Generate Report.'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **JavaCallAction**
5. **Update **$Document** (and Save to DB)
      - Set **Name** = `$FileName`**
6. **DownloadFile**
7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.