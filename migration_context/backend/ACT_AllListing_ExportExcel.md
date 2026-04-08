# Microflow Detailed Specification: ACT_AllListing_ExportExcel

### 📥 Inputs (Parameters)
- **$BuyerOffer** (Type: EcoATM_PWS.BuyerOffer)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'PWSOrderDataExport'`**
2. **Create Variable **$Description** = `'PWS Order Data Export BuyerOffer [BuyerOfferID:'+$BuyerOffer/OfferID+'] [BuyerCode:'+$BuyerCode/Code+']'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[ATPQty!= empty and ATPQty> 0 and IsActive]` (Result: **$DeviceList**)**
5. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name = 'PWSOrder']` (Result: **$MxTemplate**)**
6. **Create **EcoATM_PWS.OrderExcelDocument** (Result: **$NewOrderDataDoc**)
      - Set **DeleteAfterDownload** = `true`**
7. **CreateList**
8. **Create Variable **$FileName** = `$BuyerOffer/EcoATM_PWS.BuyerOffer_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code + '_'+ formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`**
9. 🔄 **LOOP:** For each **$IteratorDevice** in **$DeviceList**
   │ 1. 🔀 **DECISION:** `$IteratorDevice/ItemType='SPB'`
   │    ➔ **If [false]:**
   │       1. **Call Microflow **EcoATM_PWS.DS_GetOrCreateOrderItem** (Result: **$BuyerOfferItem**)**
   │       2. **Create Variable **$AvailableQty** = `if($BuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ItemType='SPB') then toString($BuyerOfferItem/EcoATM_PWS.BuyerOfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/CaseLotATPQty) else if $BuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ATPQty= empty then '0' else if $BuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ATPQty> 100 then '100+' else toString($BuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ATPQty)`**
   │       3. **Create **EcoATM_PWS.OrderDataExport** (Result: **$NewOrderDataExcel**)
      - Set **OrderDataExport_Devices** = `$NewOrderDataDoc`
      - Set **DeviceCode** = `$BuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/SKU`
      - Set **Category** = `$BuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Category/EcoATM_PWSMDM.Category/DisplayName`
      - Set **Brand** = `$BuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Brand/EcoATM_PWSMDM.Brand/DisplayName`
      - Set **Model** = `$BuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Model/EcoATM_PWSMDM.Model/DisplayName`
      - Set **Carrier** = `$BuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Carrier/EcoATM_PWSMDM.Carrier/DisplayName`
      - Set **Capacity** = `$BuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Capacity/EcoATM_PWSMDM.Capacity/DisplayName`
      - Set **Color** = `$BuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Color/EcoATM_PWSMDM.Color/DisplayName`
      - Set **Grade** = `$BuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/DisplayName`
      - Set **ListPrice** = `if $BuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/CurrentListPrice != empty then $BuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/CurrentListPrice else empty`
      - Set **AvailableQty** = `$AvailableQty`
      - Set **OfferPrice** = `if $BuyerOfferItem/OfferPrice != empty then $BuyerOfferItem/OfferPrice else empty`
      - Set **Quantity** = `$BuyerOfferItem/Quantity`
      - Set **TotalPrice** = `if $BuyerOfferItem/TotalPrice != empty then $BuyerOfferItem/TotalPrice else empty`
      - Set **SheetName** = `if($BuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ItemType='SPB') then 'Case Lots' else if ($BuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/DisplayName='A_YYY') then 'A_YYY' else 'PWS'`
      - Set **CasePrice** = `$BuyerOfferItem/EcoATM_PWS.BuyerOfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/CaseLotPrice`
      - Set **CasePackQty** = `$BuyerOfferItem/EcoATM_PWS.BuyerOfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/CaseLotSize`
      - Set **CaseOffer** = `$BuyerOfferItem/TotalPrice`**
   │       4. **Add **$$NewOrderDataExcel
** to/from list **$OrderDataExcelList****
   │    ➔ **If [true]:**
   │       1. **Retrieve related **CaseLot_Device** via Association from **$IteratorDevice** (Result: **$CaseLotList**)**
   │       2. 🔄 **LOOP:** For each **$IteratorCaseLot** in **$CaseLotList**
   │          │ 1. **Call Microflow **EcoATM_PWS.DS_GetOrCreateOrderItem_CaseLot** (Result: **$BuyerOfferItem_1**)**
   │          │ 2. **Create Variable **$AvailableQty_1** = `if($BuyerOfferItem_1/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ItemType='SPB') then toString($BuyerOfferItem_1/EcoATM_PWS.BuyerOfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/CaseLotATPQty) else if $BuyerOfferItem_1/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ATPQty= empty then '0' else if $BuyerOfferItem_1/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ATPQty> 100 then '100+' else toString($BuyerOfferItem_1/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ATPQty)`**
   │          │ 3. **Create **EcoATM_PWS.OrderDataExport** (Result: **$NewOrderDataExcel_1**)
      - Set **OrderDataExport_Devices** = `$NewOrderDataDoc`
      - Set **DeviceCode** = `$BuyerOfferItem_1/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/SKU`
      - Set **Category** = `$BuyerOfferItem_1/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Category/EcoATM_PWSMDM.Category/DisplayName`
      - Set **Brand** = `$BuyerOfferItem_1/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Brand/EcoATM_PWSMDM.Brand/DisplayName`
      - Set **Model** = `$BuyerOfferItem_1/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Model/EcoATM_PWSMDM.Model/DisplayName`
      - Set **Carrier** = `$BuyerOfferItem_1/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Carrier/EcoATM_PWSMDM.Carrier/DisplayName`
      - Set **Capacity** = `$BuyerOfferItem_1/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Capacity/EcoATM_PWSMDM.Capacity/DisplayName`
      - Set **Color** = `$BuyerOfferItem_1/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Color/EcoATM_PWSMDM.Color/DisplayName`
      - Set **Grade** = `$BuyerOfferItem_1/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/DisplayName`
      - Set **ListPrice** = `if $BuyerOfferItem_1/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/CurrentListPrice != empty then $BuyerOfferItem_1/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/CurrentListPrice else empty`
      - Set **AvailableQty** = `$AvailableQty_1`
      - Set **OfferPrice** = `if $BuyerOfferItem_1/OfferPrice != empty then $BuyerOfferItem_1/OfferPrice else empty`
      - Set **Quantity** = `$BuyerOfferItem_1/Quantity`
      - Set **TotalPrice** = `if $BuyerOfferItem_1/TotalPrice != empty then $BuyerOfferItem_1/TotalPrice else empty`
      - Set **SheetName** = `if($BuyerOfferItem_1/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ItemType='SPB') then 'Case Lots' else if ($BuyerOfferItem_1/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/DisplayName='A_YYY') then 'A_YYY' else 'PWS'`
      - Set **CasePrice** = `$BuyerOfferItem_1/EcoATM_PWS.BuyerOfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/CaseLotPrice`
      - Set **CasePackQty** = `$BuyerOfferItem_1/EcoATM_PWS.BuyerOfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/CaseLotSize`
      - Set **CaseOffer** = `$BuyerOfferItem_1/TotalPrice * $BuyerOfferItem_1/Quantity`**
   │          │ 4. **Add **$$NewOrderDataExcel_1
** to/from list **$OrderDataExcelList****
   │          └─ **End Loop**
   └─ **End Loop**
10. **Commit/Save **$OrderDataExcelList** to Database**
11. **Create Variable **$FileId** = `toString($NewOrderDataDoc/FileID)`**
12. **Call Microflow **EcoATM_PWS.SUB_OrderData_GenerateReport****
13. **Delete**
14. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
15. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.