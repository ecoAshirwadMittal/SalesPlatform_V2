# Microflow Detailed Specification: SUB_OrderDetails_Export_BySKU

### 📥 Inputs (Parameters)
- **$OfferAndOrdersView** (Type: EcoATM_PWS.OfferAndOrdersView)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'PWSOrderHistoryDownloadBySKU'`**
2. **Create Variable **$Description** = `'Order history download BySKU for [' + $OfferAndOrdersView/OfferOrderType + 'ID=' + $OfferAndOrdersView/OrderNumber + ']'`**
3. **Create **EcoATM_PWS.OrderDetailsExport** (Result: **$NewOrderDetailsExport**)
      - Set **DeleteAfterDownload** = `true`**
4. **Call Microflow **EcoATM_PWS.DS_OrderHistoryDetails** (Result: **$OfferItemList**)**
5. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name = 'PWSOrderDetails']` (Result: **$MxTemplate**)**
6. **Create Variable **$FileName** = `'PWSOrderDetails_' + $OfferAndOrdersView/OrderNumber + '_' + formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`**
7. 🔄 **LOOP:** For each **$IteratorOrderedItems** in **$OfferItemList**
   │ 1. **Update **$IteratorOrderedItems**
      - Set **OfferItem_OrderDetailsExport** = `$NewOrderDetailsExport`**
   └─ **End Loop**
8. **Commit/Save **$OfferItemList** to Database**
9. **Create Variable **$FileId** = `toString($NewOrderDetailsExport/FileID)`**
10. **Call Microflow **EcoATM_PWS.SUB_OrderDetails_GenerateReport****
11. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
12. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.