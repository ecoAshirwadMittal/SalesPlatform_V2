# Microflow Detailed Specification: SUB_OrderDetails_Export_ByDevice

### 📥 Inputs (Parameters)
- **$OfferAndOrdersView** (Type: EcoATM_PWS.OfferAndOrdersView)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'PWSOrderHistoryDownloadByDevice'`**
2. **Create Variable **$Description** = `'Order history download By Device for [' + $OfferAndOrdersView/OfferOrderType + 'ID=' + $OfferAndOrdersView/OrderNumber + ']'`**
3. **Create **EcoATM_PWS.OrderDetailsExportByDevice** (Result: **$NewOrderDetailsExportByDevice**)
      - Set **DeleteAfterDownload** = `true`**
4. **DB Retrieve **EcoATM_PWS.IMEIDetail** Filter: `[EcoATM_PWS.IMEIDetail_OfferItem/EcoATM_PWS.OfferItem/EcoATM_PWS.OfferItem_Order/EcoATM_PWS.Order/OrderNumber=$OfferAndOrdersView/OrderNumber]` (Result: **$IMEIDetailsList**)**
5. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name = 'PWSOrderDetailsByDevice']` (Result: **$MxTemplate**)**
6. **Create Variable **$FileName** = `'PWSOrderDetails_' + $OfferAndOrdersView/OrderNumber + '_' + formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`**
7. 🔄 **LOOP:** For each **$IteratorOrderedItems** in **$IMEIDetailsList**
   │ 1. **Update **$IteratorOrderedItems**
      - Set **IMEIDetail_OrderDetailsExportByDevice** = `$NewOrderDetailsExportByDevice`**
   └─ **End Loop**
8. **Commit/Save **$IMEIDetailsList** to Database**
9. **Create Variable **$FileId** = `toString($NewOrderDetailsExportByDevice/FileID)`**
10. **Call Microflow **EcoATM_PWS.SUB_OrderDetailsByDevice_GenerateReport****
11. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
12. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.