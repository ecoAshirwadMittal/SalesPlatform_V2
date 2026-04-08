# Microflow Detailed Specification: ACT_UploadPWSPricingFile

### 📥 Inputs (Parameters)
- **$PricingUpdateFile** (Type: EcoATM_PWS.PricingUpdateFile)
- **$MDMFuturePriceHelper** (Type: EcoATM_PWS.MDMFuturePriceHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'PWSUploadPricingFile'`**
2. **Create Variable **$Description** = `'uploading PWS pricing file'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **ImportExcelData**
5. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/New_List_Price != empty and $currentObject/New_Min_Price != empty and $currentObject/New_List_Price != '' and $currentObject/New_List_Price != '$' and $currentObject/New_List_Price != '$0' and $currentObject/New_List_Price != '0' and $currentObject/New_Min_Price != '' and $currentObject/New_Min_Price != '$' and $currentObject/New_Min_Price != '$0' and $currentObject/New_Min_Price != '0'` (Result: **$PricingDataExcelImporterList_Filtered**)**
6. **Create Variable **$IsFutureDate** = `$MDMFuturePriceHelper != empty and $MDMFuturePriceHelper/FuturePWSPriceDate > [%BeginOfCurrentDayUTC%]`**
7. **Create Variable **$UniqueSKUs** = `true`**
8. **Create Variable **$DuplicateSKUList** = `'Duplicate SKUs: '`**
9. **Create Variable **$ValidSKUs** = `true`**
10. **Create Variable **$InvalidSKUList** = `'Invalid SKUs: '`**
11. **CreateList**
12. **CreateList**
13. 🔄 **LOOP:** For each **$IteratorPricingDataExcelHelper** in **$PricingDataExcelImporterList_Filtered**
   │ 1. **List Operation: **Find** on **$undefined** where `$IteratorPricingDataExcelHelper/SKU` (Result: **$PricingDataImportHelper_Existing**)**
   │ 2. 🔀 **DECISION:** `$PricingDataImportHelper_Existing = empty`
   │    ➔ **If [false]:**
   │       1. **Update Variable **$DuplicateSKUList** = `$DuplicateSKUList + $IteratorPricingDataExcelHelper/SKU + ', '`**
   │       2. **Update Variable **$UniqueSKUs** = `false`**
   │    ➔ **If [true]:**
   │       1. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[SKU = $IteratorPricingDataExcelHelper/SKU]` (Result: **$Device_ImportPricingData**)**
   │       2. 🔀 **DECISION:** `$Device_ImportPricingData != empty`
   │          ➔ **If [false]:**
   │             1. **Update Variable **$InvalidSKUList** = `$InvalidSKUList + $IteratorPricingDataExcelHelper/SKU + ', '`**
   │             2. **Update Variable **$ValidSKUs** = `false`**
   │          ➔ **If [true]:**
   │             1. **Add **$$Device_ImportPricingData** to/from list **$DeviceList_ToBeUpdated****
   │             2. **Create **EcoATM_PWS.PricingDataImportHelper** (Result: **$NewPricingDataImportHelper**)
      - Set **SKU** = `$IteratorPricingDataExcelHelper/SKU`
      - Set **CurrentListPrice** = `if $IsFutureDate then $Device_ImportPricingData/CurrentListPrice else if $IteratorPricingDataExcelHelper/New_List_Price = empty or trim($IteratorPricingDataExcelHelper/New_List_Price) = '' or trim($IteratorPricingDataExcelHelper/New_List_Price) = '$' then empty else if startsWith($IteratorPricingDataExcelHelper/New_List_Price, '$') then parseInteger(substring($IteratorPricingDataExcelHelper/New_List_Price, 1)) else parseInteger($IteratorPricingDataExcelHelper/New_List_Price)`
      - Set **NewListPrice** = `if $IsFutureDate = false then $Device_ImportPricingData/FutureListPrice else if $IteratorPricingDataExcelHelper/New_List_Price = empty or trim($IteratorPricingDataExcelHelper/New_List_Price) = '' or trim($IteratorPricingDataExcelHelper/New_List_Price) = '$' then empty else if startsWith($IteratorPricingDataExcelHelper/New_List_Price, '$') then parseInteger(substring($IteratorPricingDataExcelHelper/New_List_Price, 1)) else parseInteger($IteratorPricingDataExcelHelper/New_List_Price)`
      - Set **CurrentMinPrice** = `if $IsFutureDate then $Device_ImportPricingData/CurrentMinPrice else if $IteratorPricingDataExcelHelper/New_Min_Price = empty or trim($IteratorPricingDataExcelHelper/New_Min_Price) = '' or trim($IteratorPricingDataExcelHelper/New_Min_Price) = '$' then empty else if startsWith($IteratorPricingDataExcelHelper/New_Min_Price, '$') then parseInteger(substring($IteratorPricingDataExcelHelper/New_Min_Price, 1)) else parseInteger($IteratorPricingDataExcelHelper/New_Min_Price)`
      - Set **NewMinPrice** = `if $IsFutureDate = false then $Device_ImportPricingData/FutureMinPrice else if $IteratorPricingDataExcelHelper/New_Min_Price = empty or trim($IteratorPricingDataExcelHelper/New_Min_Price) = '' or trim($IteratorPricingDataExcelHelper/New_Min_Price) = '$' then empty else if startsWith($IteratorPricingDataExcelHelper/New_Min_Price, '$') then parseInteger(substring($IteratorPricingDataExcelHelper/New_Min_Price, 1)) else parseInteger($IteratorPricingDataExcelHelper/New_Min_Price)`**
   │             3. **Add **$$NewPricingDataImportHelper
** to/from list **$PricingDataImportHelperList****
   │             4. **Update **$Device_ImportPricingData**
      - Set **CurrentListPrice** = `if $NewPricingDataImportHelper/CurrentListPrice != empty and $NewPricingDataImportHelper/CurrentListPrice > 0 then $NewPricingDataImportHelper/CurrentListPrice else $Device_ImportPricingData/CurrentListPrice`
      - Set **CurrentMinPrice** = `if $NewPricingDataImportHelper/CurrentMinPrice != empty and $NewPricingDataImportHelper/CurrentMinPrice > 0 then $NewPricingDataImportHelper/CurrentMinPrice else $Device_ImportPricingData/CurrentMinPrice`
      - Set **FutureListPrice** = `if $NewPricingDataImportHelper/NewListPrice != empty and $NewPricingDataImportHelper/NewListPrice > 0 then $NewPricingDataImportHelper/NewListPrice else $Device_ImportPricingData/FutureListPrice`
      - Set **FutureMinPrice** = `if $NewPricingDataImportHelper/NewMinPrice != empty and $NewPricingDataImportHelper/NewMinPrice > 0 then $NewPricingDataImportHelper/NewMinPrice else $Device_ImportPricingData/FutureMinPrice`**
   │             5. **Call Microflow **EcoATM_PWS.VAL_Device****
   └─ **End Loop**
14. 🔀 **DECISION:** `$UniqueSKUs and $ValidSKUs`
   ➔ **If [true]:**
      1. **Commit/Save **$DeviceList_ToBeUpdated** to Database**
      2. **Call Microflow **EcoATM_PWS.ACT_SendPricingDataToSnowflake****
      3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      4. 🏁 **END:** Return `empty`
   ➔ **If [false]:**
      1. **Update Variable **$DuplicateSKUList** = `substring($DuplicateSKUList, 0, length($DuplicateSKUList) - 2)`**
      2. **Update Variable **$InvalidSKUList** = `substring($InvalidSKUList, 0, length($InvalidSKUList) - 2)`**
      3. **Create Variable **$ErrorMessage** = `if $UniqueSKUs = false then $DuplicateSKUList + ' ' else ''`**
      4. **Update Variable **$ErrorMessage** = `if $ValidSKUs = false then $ErrorMessage + $InvalidSKUList else $ErrorMessage`**
      5. **Call Microflow **Custom_Logging.SUB_Log_Error****
      6. 🏁 **END:** Return `$ErrorMessage`

**Final Result:** This process concludes by returning a [String] value.