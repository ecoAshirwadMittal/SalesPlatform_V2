# Microflow Detailed Specification: ACT_RMADetailSales_Export

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'PWSOrderHistoryDownload'`**
2. **Create Variable **$Description** = `'RMA details download for ['+'RMA with RMA Number =' + $RMA/Number+ ']'`**
3. **Create **EcoATM_RMA.RMADetailsExport** (Result: **$NewRMADetailsExport**)
      - Set **DeleteAfterDownload** = `true`**
4. **Retrieve related **RMAItem_RMA** via Association from **$RMA** (Result: **$RMAItemList**)**
5. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name = 'PWSRMADetailsSales']` (Result: **$MxTemplate**)**
6. **Create Variable **$FileName** = `$RMA/Number + '_'+$RMA/EcoATM_RMA.RMA_RMAStatus/EcoATM_RMA.RMAStatus/InternalStatusText+'_' + formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`**
7. 🔄 **LOOP:** For each **$IteratorRMAItem** in **$RMAItemList**
   │ 1. **Update **$IteratorRMAItem**
      - Set **RMAItem_RMADetailsExport** = `$NewRMADetailsExport`**
   └─ **End Loop**
8. **Commit/Save **$RMAItemList** to Database**
9. **Create Variable **$FileId** = `toString($NewRMADetailsExport/FileID)`**
10. **Call Microflow **EcoATM_RMA.SUB_RMADetails_GenerateReport****
11. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
12. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.