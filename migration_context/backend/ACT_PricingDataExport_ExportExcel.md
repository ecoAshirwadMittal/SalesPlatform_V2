# Microflow Detailed Specification: ACT_PricingDataExport_ExportExcel

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'PWSPricingDataExcelExport'`**
2. **Create Variable **$Description** = `'Pricing Data Excel Export.'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[IsActive]` (Result: **$DeviceList**)**
5. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name = 'PWSPricing']` (Result: **$MxTemplate**)**
6. **Create **EcoATM_PWS.PricingExcelDocument** (Result: **$NewPricingExcelDocument**)
      - Set **DeleteAfterDownload** = `true`**
7. **CreateList**
8. **Create Variable **$FileName** = `'PWS_Pricing_'+ formatDateTime([%CurrentDateTime%], 'yyyyMMdd') +'.xlsx'`**
9. 🔄 **LOOP:** For each **$IteratorDevice** in **$DeviceList**
   │ 1. **Create **EcoATM_PWS.PricingDataExport** (Result: **$NewPricingDataExport**)
      - Set **PricingDataExport_PricingExcelDocument** = `$NewPricingExcelDocument`
      - Set **SKU** = `$IteratorDevice/SKU`
      - Set **Category** = `$IteratorDevice/EcoATM_PWSMDM.Device_Category/EcoATM_PWSMDM.Category/DisplayName`
      - Set **Brand** = `$IteratorDevice/EcoATM_PWSMDM.Device_Brand/EcoATM_PWSMDM.Brand/DisplayName`
      - Set **Model** = `$IteratorDevice/EcoATM_PWSMDM.Device_Model/EcoATM_PWSMDM.Model/DisplayName`
      - Set **Carrier** = `$IteratorDevice/EcoATM_PWSMDM.Device_Carrier/EcoATM_PWSMDM.Carrier/DisplayName`
      - Set **Capacity** = `$IteratorDevice/EcoATM_PWSMDM.Device_Capacity/EcoATM_PWSMDM.Capacity/DisplayName`
      - Set **Color** = `$IteratorDevice/EcoATM_PWSMDM.Device_Color/EcoATM_PWSMDM.Color/DisplayName`
      - Set **Grade** = `$IteratorDevice/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/DisplayName`
      - Set **CurrentListPrice** = `if $IteratorDevice/CurrentListPrice != empty then '$' + $IteratorDevice/CurrentListPrice else ''`
      - Set **NewListPrice** = `if $IteratorDevice/FutureListPrice != empty then '$' + $IteratorDevice/FutureListPrice else ''`
      - Set **CurrentMinPrice** = `if $IteratorDevice/CurrentMinPrice != empty then '$' + $IteratorDevice/CurrentMinPrice else ''`
      - Set **NewMinPrice** = `if $IteratorDevice/FutureMinPrice != empty then '$' + $IteratorDevice/FutureMinPrice else ''`
      - Set **SKUStatus** = `$IteratorDevice/EcoATM_PWSMDM.Device_Note/EcoATM_PWSMDM.Note/Notes`**
   │ 2. **Update **$NewPricingDataExport**
      - Set **Lookup** = `$NewPricingDataExport/Model + ' | ' + $NewPricingDataExport/Capacity + ' | ' + $NewPricingDataExport/Grade`**
   │ 3. **Add **$$NewPricingDataExport
** to/from list **$PricingDataExportList****
   └─ **End Loop**
10. **Commit/Save **$PricingDataExportList** to Database**
11. **Create Variable **$FileId** = `toString($NewPricingExcelDocument/FileID)`**
12. **Call Microflow **EcoATM_PWS.SUB_PricingData_GenerateReport****
13. **Delete**
14. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
15. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.