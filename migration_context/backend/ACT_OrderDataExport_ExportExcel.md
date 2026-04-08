# Microflow Detailed Specification: ACT_OrderDataExport_ExportExcel

### 📥 Inputs (Parameters)
- **$BuyerOffer** (Type: EcoATM_PWS.BuyerOffer)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **BuyerOffer_BuyerCode** via Association from **$BuyerOffer** (Result: **$BuyerCode**)**
2. **Create Variable **$TimerName** = `'PWSOrderDataExport'`**
3. **Create Variable **$Description** = `'PWS Order Data Export. [BuyerCode:'+$BuyerCode/Code+'] [OfferId:'+$BuyerOffer/OfferID+']'`**
4. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
5. **Retrieve related **BuyerOfferItem_BuyerOffer** via Association from **$BuyerOffer** (Result: **$OrderItemList**)**
6. 🔀 **DECISION:** `$OrderItemList != empty`
   ➔ **If [true]:**
      1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/Quantity != empty and $currentObject/Quantity > 0` (Result: **$OrderItemList_Valid**)**
      2. 🔀 **DECISION:** `$OrderItemList_Valid != empty`
         ➔ **If [true]:**
            1. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name = 'PWSOrder']` (Result: **$MxTemplate**)**
            2. **Create **EcoATM_PWS.OrderExcelDocument** (Result: **$NewOrderDataDoc**)
      - Set **DeleteAfterDownload** = `true`**
            3. **CreateList**
            4. **Create Variable **$FileName** = `$BuyerOffer/EcoATM_PWS.BuyerOffer_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code + '_'+ formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`**
            5. 🔄 **LOOP:** For each **$IteratorOrderItem** in **$OrderItemList_Valid**
               │ 1. **Create Variable **$AvailableQty** = `if($IteratorOrderItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ItemType='SPB') then toString($IteratorOrderItem/EcoATM_PWS.BuyerOfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/CaseLotATPQty) else if $IteratorOrderItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ATPQty= empty then '0' else if $IteratorOrderItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ATPQty> 100 then '100+' else toString($IteratorOrderItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ATPQty)`**
               │ 2. **Create **EcoATM_PWS.OrderDataExport** (Result: **$NewOrderDataExcel**)
      - Set **OrderDataExport_Devices** = `$NewOrderDataDoc`
      - Set **DeviceCode** = `$IteratorOrderItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/SKU`
      - Set **Category** = `$IteratorOrderItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Category/EcoATM_PWSMDM.Category/DisplayName`
      - Set **Brand** = `$IteratorOrderItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Brand/EcoATM_PWSMDM.Brand/DisplayName`
      - Set **Model** = `$IteratorOrderItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Model/EcoATM_PWSMDM.Model/DisplayName`
      - Set **Carrier** = `$IteratorOrderItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Carrier/EcoATM_PWSMDM.Carrier/DisplayName`
      - Set **Capacity** = `$IteratorOrderItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Capacity/EcoATM_PWSMDM.Capacity/DisplayName`
      - Set **Color** = `$IteratorOrderItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Color/EcoATM_PWSMDM.Color/DisplayName`
      - Set **Grade** = `$IteratorOrderItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/DisplayName`
      - Set **ListPrice** = `if $IteratorOrderItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/CurrentListPrice != empty then $IteratorOrderItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/CurrentListPrice else empty`
      - Set **AvailableQty** = `$AvailableQty`
      - Set **OfferPrice** = `if $IteratorOrderItem/OfferPrice != empty then $IteratorOrderItem/OfferPrice else empty`
      - Set **Quantity** = `$IteratorOrderItem/Quantity`
      - Set **TotalPrice** = `if $IteratorOrderItem/TotalPrice != empty then $IteratorOrderItem/TotalPrice else empty`
      - Set **SheetName** = `if($IteratorOrderItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ItemType='SPB') then 'Case Lots' else if ($IteratorOrderItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/DisplayName='A_YYY') then 'A_YYY' else 'PWS'`
      - Set **CasePrice** = `$IteratorOrderItem/EcoATM_PWS.BuyerOfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/CaseLotPrice`
      - Set **CasePackQty** = `$IteratorOrderItem/EcoATM_PWS.BuyerOfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/CaseLotSize`
      - Set **CaseOffer** = `$IteratorOrderItem/TotalPrice * $IteratorOrderItem/Quantity`**
               │ 3. **Add **$$NewOrderDataExcel
** to/from list **$OrderDataExcelList****
               └─ **End Loop**
            6. **Commit/Save **$OrderDataExcelList** to Database**
            7. **Create Variable **$FileId** = `toString($NewOrderDataDoc/FileID)`**
            8. **Call Microflow **EcoATM_PWS.SUB_OrderData_GenerateReport****
            9. **Delete**
            10. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            11. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.