# Microflow Detailed Specification: ACT_OrderHistory_ExportExcel

### 📥 Inputs (Parameters)
- **$OrderHistoryHelper** (Type: EcoATM_PWS.OrderHistoryHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'PWSOrderHistoryDownload'`**
2. **Create Variable **$Description** = `'Order History Download from ' + getCaption($OrderHistoryHelper/CurrentTab) + ' tab.'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **CreateList**
5. 🔀 **DECISION:** `$OrderHistoryHelper/CurrentTab`
   ➔ **If [All]:**
      1. **DB Retrieve **EcoATM_PWS.OfferAndOrdersView**  (Result: **$OfferAndOrdersViewList_ALL**)**
      2. **Add **$$OfferAndOrdersViewList_ALL** to/from list **$OfferAndOrdersViewList****
      3. 🔀 **DECISION:** `$OfferAndOrdersViewList != empty`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name = 'PWSOrderHistory']` (Result: **$MxTemplate**)**
            2. **Create **EcoATM_PWS.OrderHistoryExport** (Result: **$NewOrderHistoryExport**)
      - Set **DeleteAfterDownload** = `true`**
            3. **CreateList**
            4. **Create Variable **$FileName** = `'PWS OrderHistory_' + getKey($OrderHistoryHelper/CurrentTab) + '_' + formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`**
            5. 🔄 **LOOP:** For each **$IteratorOfferAndOrdersView** in **$OfferAndOrdersViewList**
               │ 1. **Create **EcoATM_PWS.OrderHistoyDownload** (Result: **$NewOrderHistoryDownload**)
      - Set **OrderNumber** = `$IteratorOfferAndOrdersView/OrderNumber`
      - Set **OfferDate** = `$IteratorOfferAndOrdersView/OfferDate`
      - Set **OrderDate** = `$IteratorOfferAndOrdersView/OrderDate`
      - Set **OrderStatus** = `$IteratorOfferAndOrdersView/OrderStatus`
      - Set **SKUcount** = `$IteratorOfferAndOrdersView/SKUCount`
      - Set **TotalQuantity** = `$IteratorOfferAndOrdersView/TotalQuantity`
      - Set **TotalPrice** = `$IteratorOfferAndOrdersView/TotalPrice`
      - Set **Buyer** = `$IteratorOfferAndOrdersView/Buyer`
      - Set **Company** = `$IteratorOfferAndOrdersView/Company`
      - Set **LastUpdateDate** = `$IteratorOfferAndOrdersView/LastUpdateDate`
      - Set **OfferOrderType** = `$IteratorOfferAndOrdersView/OfferOrderType`
      - Set **OrderHistoyDownload_OrderHistoryExport** = `$NewOrderHistoryExport`
      - Set **ShipDate** = `$IteratorOfferAndOrdersView/ShipDate`
      - Set **ShipMethod** = `$IteratorOfferAndOrdersView/ShipMethod`**
               │ 2. **Add **$$NewOrderHistoryDownload** to/from list **$OrderHistoryList****
               └─ **End Loop**
            6. **Commit/Save **$OrderHistoryList** to Database**
            7. **Create Variable **$FileId** = `toString($NewOrderHistoryExport/FileID)`**
            8. **Call Microflow **EcoATM_PWS.SUB_OrderHistory_GenerateReport****
            9. **Delete**
            10. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            11. 🏁 **END:** Return empty
   ➔ **If [Recent]:**
      1. **DB Retrieve **EcoATM_PWS.OfferAndOrdersView** Filter: `[LastUpdateDate >= $OrderHistoryHelper/RecentDateCutoff]` (Result: **$OfferAndOrdersViewList_Recent**)**
      2. **Add **$$OfferAndOrdersViewList_Recent** to/from list **$OfferAndOrdersViewList****
      3. 🔀 **DECISION:** `$OfferAndOrdersViewList != empty`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name = 'PWSOrderHistory']` (Result: **$MxTemplate**)**
            2. **Create **EcoATM_PWS.OrderHistoryExport** (Result: **$NewOrderHistoryExport**)
      - Set **DeleteAfterDownload** = `true`**
            3. **CreateList**
            4. **Create Variable **$FileName** = `'PWS OrderHistory_' + getKey($OrderHistoryHelper/CurrentTab) + '_' + formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`**
            5. 🔄 **LOOP:** For each **$IteratorOfferAndOrdersView** in **$OfferAndOrdersViewList**
               │ 1. **Create **EcoATM_PWS.OrderHistoyDownload** (Result: **$NewOrderHistoryDownload**)
      - Set **OrderNumber** = `$IteratorOfferAndOrdersView/OrderNumber`
      - Set **OfferDate** = `$IteratorOfferAndOrdersView/OfferDate`
      - Set **OrderDate** = `$IteratorOfferAndOrdersView/OrderDate`
      - Set **OrderStatus** = `$IteratorOfferAndOrdersView/OrderStatus`
      - Set **SKUcount** = `$IteratorOfferAndOrdersView/SKUCount`
      - Set **TotalQuantity** = `$IteratorOfferAndOrdersView/TotalQuantity`
      - Set **TotalPrice** = `$IteratorOfferAndOrdersView/TotalPrice`
      - Set **Buyer** = `$IteratorOfferAndOrdersView/Buyer`
      - Set **Company** = `$IteratorOfferAndOrdersView/Company`
      - Set **LastUpdateDate** = `$IteratorOfferAndOrdersView/LastUpdateDate`
      - Set **OfferOrderType** = `$IteratorOfferAndOrdersView/OfferOrderType`
      - Set **OrderHistoyDownload_OrderHistoryExport** = `$NewOrderHistoryExport`
      - Set **ShipDate** = `$IteratorOfferAndOrdersView/ShipDate`
      - Set **ShipMethod** = `$IteratorOfferAndOrdersView/ShipMethod`**
               │ 2. **Add **$$NewOrderHistoryDownload** to/from list **$OrderHistoryList****
               └─ **End Loop**
            6. **Commit/Save **$OrderHistoryList** to Database**
            7. **Create Variable **$FileId** = `toString($NewOrderHistoryExport/FileID)`**
            8. **Call Microflow **EcoATM_PWS.SUB_OrderHistory_GenerateReport****
            9. **Delete**
            10. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            11. 🏁 **END:** Return empty
   ➔ **If [InProcess]:**
      1. **DB Retrieve **EcoATM_PWS.OfferAndOrdersView** Filter: `[ OrderStatus = 'In Process' or OrderStatus = 'Offer Pending' ]` (Result: **$OfferAndOrdersViewList_InProcess**)**
      2. **Add **$$OfferAndOrdersViewList_InProcess** to/from list **$OfferAndOrdersViewList****
      3. 🔀 **DECISION:** `$OfferAndOrdersViewList != empty`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name = 'PWSOrderHistory']` (Result: **$MxTemplate**)**
            2. **Create **EcoATM_PWS.OrderHistoryExport** (Result: **$NewOrderHistoryExport**)
      - Set **DeleteAfterDownload** = `true`**
            3. **CreateList**
            4. **Create Variable **$FileName** = `'PWS OrderHistory_' + getKey($OrderHistoryHelper/CurrentTab) + '_' + formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`**
            5. 🔄 **LOOP:** For each **$IteratorOfferAndOrdersView** in **$OfferAndOrdersViewList**
               │ 1. **Create **EcoATM_PWS.OrderHistoyDownload** (Result: **$NewOrderHistoryDownload**)
      - Set **OrderNumber** = `$IteratorOfferAndOrdersView/OrderNumber`
      - Set **OfferDate** = `$IteratorOfferAndOrdersView/OfferDate`
      - Set **OrderDate** = `$IteratorOfferAndOrdersView/OrderDate`
      - Set **OrderStatus** = `$IteratorOfferAndOrdersView/OrderStatus`
      - Set **SKUcount** = `$IteratorOfferAndOrdersView/SKUCount`
      - Set **TotalQuantity** = `$IteratorOfferAndOrdersView/TotalQuantity`
      - Set **TotalPrice** = `$IteratorOfferAndOrdersView/TotalPrice`
      - Set **Buyer** = `$IteratorOfferAndOrdersView/Buyer`
      - Set **Company** = `$IteratorOfferAndOrdersView/Company`
      - Set **LastUpdateDate** = `$IteratorOfferAndOrdersView/LastUpdateDate`
      - Set **OfferOrderType** = `$IteratorOfferAndOrdersView/OfferOrderType`
      - Set **OrderHistoyDownload_OrderHistoryExport** = `$NewOrderHistoryExport`
      - Set **ShipDate** = `$IteratorOfferAndOrdersView/ShipDate`
      - Set **ShipMethod** = `$IteratorOfferAndOrdersView/ShipMethod`**
               │ 2. **Add **$$NewOrderHistoryDownload** to/from list **$OrderHistoryList****
               └─ **End Loop**
            6. **Commit/Save **$OrderHistoryList** to Database**
            7. **Create Variable **$FileId** = `toString($NewOrderHistoryExport/FileID)`**
            8. **Call Microflow **EcoATM_PWS.SUB_OrderHistory_GenerateReport****
            9. **Delete**
            10. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            11. 🏁 **END:** Return empty
   ➔ **If [Complete]:**
      1. **DB Retrieve **EcoATM_PWS.OfferAndOrdersView** Filter: `[ OrderStatus!='In Process' and OrderStatus!='Offer Pending' ]` (Result: **$OfferAndOrdersViewList_Complete**)**
      2. **Add **$$OfferAndOrdersViewList_Complete** to/from list **$OfferAndOrdersViewList****
      3. 🔀 **DECISION:** `$OfferAndOrdersViewList != empty`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name = 'PWSOrderHistory']` (Result: **$MxTemplate**)**
            2. **Create **EcoATM_PWS.OrderHistoryExport** (Result: **$NewOrderHistoryExport**)
      - Set **DeleteAfterDownload** = `true`**
            3. **CreateList**
            4. **Create Variable **$FileName** = `'PWS OrderHistory_' + getKey($OrderHistoryHelper/CurrentTab) + '_' + formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`**
            5. 🔄 **LOOP:** For each **$IteratorOfferAndOrdersView** in **$OfferAndOrdersViewList**
               │ 1. **Create **EcoATM_PWS.OrderHistoyDownload** (Result: **$NewOrderHistoryDownload**)
      - Set **OrderNumber** = `$IteratorOfferAndOrdersView/OrderNumber`
      - Set **OfferDate** = `$IteratorOfferAndOrdersView/OfferDate`
      - Set **OrderDate** = `$IteratorOfferAndOrdersView/OrderDate`
      - Set **OrderStatus** = `$IteratorOfferAndOrdersView/OrderStatus`
      - Set **SKUcount** = `$IteratorOfferAndOrdersView/SKUCount`
      - Set **TotalQuantity** = `$IteratorOfferAndOrdersView/TotalQuantity`
      - Set **TotalPrice** = `$IteratorOfferAndOrdersView/TotalPrice`
      - Set **Buyer** = `$IteratorOfferAndOrdersView/Buyer`
      - Set **Company** = `$IteratorOfferAndOrdersView/Company`
      - Set **LastUpdateDate** = `$IteratorOfferAndOrdersView/LastUpdateDate`
      - Set **OfferOrderType** = `$IteratorOfferAndOrdersView/OfferOrderType`
      - Set **OrderHistoyDownload_OrderHistoryExport** = `$NewOrderHistoryExport`
      - Set **ShipDate** = `$IteratorOfferAndOrdersView/ShipDate`
      - Set **ShipMethod** = `$IteratorOfferAndOrdersView/ShipMethod`**
               │ 2. **Add **$$NewOrderHistoryDownload** to/from list **$OrderHistoryList****
               └─ **End Loop**
            6. **Commit/Save **$OrderHistoryList** to Database**
            7. **Create Variable **$FileId** = `toString($NewOrderHistoryExport/FileID)`**
            8. **Call Microflow **EcoATM_PWS.SUB_OrderHistory_GenerateReport****
            9. **Delete**
            10. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            11. 🏁 **END:** Return empty
   ➔ **If [(empty)]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.