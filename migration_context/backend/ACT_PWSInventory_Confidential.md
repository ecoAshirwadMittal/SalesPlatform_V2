# Microflow Detailed Specification: ACT_PWSInventory_Confidential

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'PWSInventory'`**
2. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
3. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[ATPQty!= empty and ATPQty> 0 and IsActive] [ItemType='PWS']` (Result: **$DeviceList**)**
4. **DB Retrieve **EcoATM_PWSMDM.CaseLot** Filter: `[CaseLotATPQty!= empty and CaseLotATPQty> 0] [EcoATM_PWSMDM.CaseLot_Device/EcoATM_PWSMDM.Device/ItemType='SPB'] [EcoATM_PWSMDM.CaseLot_Device/EcoATM_PWSMDM.Device/IsActive]` (Result: **$CaseLotList**)**
5. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name = 'PWSInventory']` (Result: **$MxTemplate**)**
6. **Create **EcoATM_PWS.OrderExcelDocument** (Result: **$NewOrderDataDoc**)
      - Set **DeleteAfterDownload** = `true`**
7. **CreateList**
8. **Create Variable **$FileName** = `'CONFIDENTIAL_PWS Inventory_'+ formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`**
9. 🔄 **LOOP:** For each **$IteratorDevice** in **$DeviceList**
   │ 1. **Create **EcoATM_PWS.OrderDataExport** (Result: **$NewOrderDataExcel**)
      - Set **OrderDataExport_Devices** = `$NewOrderDataDoc`
      - Set **DeviceCode** = `$IteratorDevice/SKU`
      - Set **Category** = `$IteratorDevice/EcoATM_PWSMDM.Device_Category/EcoATM_PWSMDM.Category/DisplayName`
      - Set **Brand** = `$IteratorDevice/EcoATM_PWSMDM.Device_Brand/EcoATM_PWSMDM.Brand/DisplayName`
      - Set **Model** = `$IteratorDevice/EcoATM_PWSMDM.Device_Model/EcoATM_PWSMDM.Model/DisplayName`
      - Set **Carrier** = `$IteratorDevice/EcoATM_PWSMDM.Device_Carrier/EcoATM_PWSMDM.Carrier/DisplayName`
      - Set **Capacity** = `$IteratorDevice/EcoATM_PWSMDM.Device_Capacity/EcoATM_PWSMDM.Capacity/DisplayName`
      - Set **Color** = `$IteratorDevice/EcoATM_PWSMDM.Device_Color/EcoATM_PWSMDM.Color/DisplayName`
      - Set **Grade** = `$IteratorDevice/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/DisplayName`
      - Set **ListPrice** = `if $IteratorDevice/CurrentListPrice != empty then $IteratorDevice/CurrentListPrice else empty`
      - Set **AvailableQty** = `if $IteratorDevice/ATPQty= empty then '0' else toString($IteratorDevice/ATPQty)`
      - Set **SheetName** = `if($IteratorDevice/ItemType='SPB') then 'Case Lots' else if ($IteratorDevice/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/DisplayName='A_YYY') then 'A_YYY' else 'PWS'`**
   │ 2. **Add **$$NewOrderDataExcel
** to/from list **$OrderDataExcelList****
   └─ **End Loop**
10. 🔄 **LOOP:** For each **$IteratorCaseLot** in **$CaseLotList**
   │ 1. **Retrieve related **CaseLot_Device** via Association from **$IteratorCaseLot** (Result: **$IteratorDevice_Caselot**)**
   │ 2. **Create **EcoATM_PWS.OrderDataExport** (Result: **$NewOrderDataExcel_1**)
      - Set **DeviceCode** = `$IteratorDevice_Caselot/SKU`
      - Set **Category** = `$IteratorDevice_Caselot/EcoATM_PWSMDM.Device_Category/EcoATM_PWSMDM.Category/DisplayName`
      - Set **Brand** = `$IteratorDevice_Caselot/EcoATM_PWSMDM.Device_Brand/EcoATM_PWSMDM.Brand/DisplayName`
      - Set **Model** = `$IteratorDevice_Caselot/EcoATM_PWSMDM.Device_Model/EcoATM_PWSMDM.Model/DisplayName`
      - Set **Carrier** = `$IteratorDevice_Caselot/EcoATM_PWSMDM.Device_Carrier/EcoATM_PWSMDM.Carrier/DisplayName`
      - Set **Capacity** = `$IteratorDevice_Caselot/EcoATM_PWSMDM.Device_Capacity/EcoATM_PWSMDM.Capacity/DisplayName`
      - Set **Color** = `$IteratorDevice_Caselot/EcoATM_PWSMDM.Device_Color/EcoATM_PWSMDM.Color/DisplayName`
      - Set **Grade** = `$IteratorDevice_Caselot/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/DisplayName`
      - Set **ListPrice** = `if $IteratorDevice_Caselot/CurrentListPrice != empty then $IteratorDevice_Caselot/CurrentListPrice else empty`
      - Set **AvailableQty** = `if $IteratorDevice_Caselot/ATPQty= empty then '0' else toString($IteratorCaseLot/CaseLotATPQty)`
      - Set **SheetName** = `if($IteratorDevice_Caselot/ItemType='SPB') then 'Case Lots' else if ($IteratorDevice_Caselot/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/DisplayName='A_YYY') then 'A_YYY' else 'PWS'`
      - Set **CasePrice** = `$IteratorCaseLot/CaseLotPrice`
      - Set **CasePackQty** = `$IteratorCaseLot/CaseLotSize`
      - Set **OrderDataExport_CaseLots** = `$NewOrderDataDoc`**
   │ 3. **Add **$$NewOrderDataExcel_1
** to/from list **$OrderDataExcelList****
   └─ **End Loop**
11. **Commit/Save **$OrderDataExcelList** to Database**
12. **Create Variable **$FileId** = `toString($NewOrderDataDoc/FileID)`**
13. **Call Microflow **EcoATM_PWS.SUB_OrderData_GenerateReport****
14. **Delete**
15. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
16. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.