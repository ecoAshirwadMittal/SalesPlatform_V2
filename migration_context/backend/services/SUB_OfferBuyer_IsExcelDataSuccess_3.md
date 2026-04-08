# Microflow Detailed Specification: SUB_OfferBuyer_IsExcelDataSuccess_3

### 📥 Inputs (Parameters)
- **$OfferDataExcelImporterList** (Type: EcoATM_PWS.OfferDataExcelImporter)
- **$BuyerCode** (Type: AuctionUI.BuyerCode)
- **$ManageFileDocument** (Type: EcoATM_PWS.ManageFileDocument)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$OfferDataExcelImporterList!=empty`
   ➔ **If [true]:**
      1. **List Operation: **FilterByExpression** on **$undefined** where `trim($currentObject/Quantity)='' or trim($currentObject/Quantity)='0'` (Result: **$EmptyOrZeoQuantityList**)**
      2. 🔀 **DECISION:** `$EmptyOrZeoQuantityList=empty`
         ➔ **If [true]:**
            1. **CreateList**
            2. **CreateList**
            3. **CreateList**
            4. 🔄 **LOOP:** For each **$Iterator** in **$OfferDataExcelImporterList**
               │ 1. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[SKU=$Iterator/SKU]` (Result: **$Device**)**
               │ 2. 🔀 **DECISION:** `$Device!=empty`
               │    ➔ **If [true]:**
               │       1. **List Operation: **Find** on **$undefined** where `$Device/SKU` (Result: **$AlreadyExist**)**
               │       2. 🔀 **DECISION:** `$AlreadyExist!=empty`
               │          ➔ **If [true]:**
               │             1. **Add **$$Iterator
** to/from list **$DuplicateOfferDataExcelImporterList****
               │          ➔ **If [false]:**
               │             1. **Add **$$Device
** to/from list **$CorrespondingDeviceList****
               │    ➔ **If [false]:**
               │       1. **Add **$$Iterator
** to/from list **$UnresolvedSKUOfferDataExcelImporterList****
               └─ **End Loop**
            5. 🔀 **DECISION:** `$DuplicateOfferDataExcelImporterList=empty and $UnresolvedSKUOfferDataExcelImporterList=empty`
               ➔ **If [true]:**
                  1. **CreateList**
                  2. 🔄 **LOOP:** For each **$IteratorOfferDataExcelImporter** in **$OfferDataExcelImporterList**
                     │ 1. **List Operation: **Find** on **$undefined** where `$IteratorOfferDataExcelImporter/SKU` (Result: **$TargetDevice**)**
                     │ 2. **Create **EcoATM_PWS.BuyerOffer** (Result: **$NewOfferItem**)
      - Set **Quantity** = `parseInteger(replaceAll($IteratorOfferDataExcelImporter/Quantity,'\D',''))`
      - Set **OfferPrice** = `parseInteger(replaceAll($IteratorOfferDataExcelImporter/OfferPrice,'\D',''))`
      - Set **TotalPrice** = `parseInteger(replaceAll($IteratorOfferDataExcelImporter/TotalPrice,'\D',''))`
      - Set **BuyerOffer_BuyerCode** = `$BuyerCode`
      - Set **BuyerOffer_Device** = `$TargetDevice`**
                     │ 3. **Add **$$NewOfferItem
** to/from list **$BuyerOfferList****
                     └─ **End Loop**
                  3. **Commit/Save **$BuyerOfferList** to Database**
                  4. **Delete**
                  5. 🏁 **END:** Return `true`
               ➔ **If [false]:**
                  1. **Update **$ManageFileDocument**
      - Set **Message** = `'Import failed - Data is not compliant'`
      - Set **ProcessPercentage** = `25`**
                  2. 🏁 **END:** Return `false`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Info****
            2. 🏁 **END:** Return `false`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.