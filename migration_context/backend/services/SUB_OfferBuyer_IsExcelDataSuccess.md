# Microflow Detailed Specification: SUB_OfferBuyer_IsExcelDataSuccess

### 📥 Inputs (Parameters)
- **$OfferDataExcelImporterList** (Type: EcoATM_PWS.OfferDataExcelImporter)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$ManageFileDocument** (Type: EcoATM_PWS.ManageFileDocument)
- **$BuyerOffer** (Type: EcoATM_PWS.BuyerOffer)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. 🔀 **DECISION:** `$OfferDataExcelImporterList!=empty`
   ➔ **If [true]:**
      1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/Quantity=empty or $currentObject/Quantity=0` (Result: **$EmptyOrZeoQuantityList**)**
      2. 🔀 **DECISION:** `$EmptyOrZeoQuantityList=empty`
         ➔ **If [true]:**
            1. **CreateList**
            2. **CreateList**
            3. **CreateList**
            4. 🔄 **LOOP:** For each **$IteratorTempBuyerOffer** in **$OfferDataExcelImporterList**
               │ 1. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[SKU=$IteratorTempBuyerOffer/SKU] [IsActive]` (Result: **$Device**)**
               │ 2. 🔀 **DECISION:** `$Device!=empty`
               │    ➔ **If [true]:**
               │       1. **List Operation: **Find** on **$undefined** where `$Device/SKU` (Result: **$AlreadyExist**)**
               │       2. 🔀 **DECISION:** `$AlreadyExist!=empty`
               │          ➔ **If [true]:**
               │             1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
               │             2. **Add **$$IteratorTempBuyerOffer
** to/from list **$DuplicateOfferDataExcelImporterList****
               │          ➔ **If [false]:**
               │             1. **Add **$$Device
** to/from list **$CorrespondingDeviceList****
               │    ➔ **If [false]:**
               │       1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
               │       2. **Add **$$IteratorTempBuyerOffer
** to/from list **$UnresolvedSKUOfferDataExcelImporterList****
               └─ **End Loop**
            5. 🔀 **DECISION:** `$DuplicateOfferDataExcelImporterList=empty and $UnresolvedSKUOfferDataExcelImporterList=empty`
               ➔ **If [true]:**
                  1. **Create Variable **$BigTotal** = `0`**
                  2. **CreateList**
                  3. 🔄 **LOOP:** For each **$IteratorOfferDataExcelImporter** in **$OfferDataExcelImporterList**
                     │ 1. **List Operation: **Find** on **$undefined** where `$IteratorOfferDataExcelImporter/SKU` (Result: **$TargetDevice**)**
                     │ 2. **Create **EcoATM_PWS.BuyerOfferItem** (Result: **$NewBuyerOfferItem**)
      - Set **Quantity** = `floor($IteratorOfferDataExcelImporter/Quantity)`
      - Set **OfferPrice** = `floor($IteratorOfferDataExcelImporter/OfferPrice)`
      - Set **TotalPrice** = `floor($IteratorOfferDataExcelImporter/OfferPrice * $IteratorOfferDataExcelImporter/Quantity)`
      - Set **BuyerOfferItem_BuyerCode** = `$BuyerCode`
      - Set **BuyerOfferItem_Device** = `$TargetDevice`
      - Set **BuyerOfferItem_BuyerOffer** = `$BuyerOffer`
      - Set **IsExceedQty** = `$TargetDevice != empty and $TargetDevice/ATPQty != empty and floor($IteratorOfferDataExcelImporter/Quantity) > $TargetDevice/ATPQty and $TargetDevice/ATPQty <= 100`**
                     │ 3. **Call Microflow **EcoATM_PWS.CAL_BuyerOfferItem_CSSStyle** (Result: **$CSSClass**)**
                     │ 4. **Update **$NewBuyerOfferItem**
      - Set **CSSClass** = `$CSSClass`**
                     │ 5. **Update Variable **$BigTotal** = `$BigTotal+($NewBuyerOfferItem/OfferPrice*$NewBuyerOfferItem/Quantity)`**
                     │ 6. **Add **$$NewBuyerOfferItem
** to/from list **$BuyerOfferItemList****
                     └─ **End Loop**
                  4. **Call Microflow **EcoATM_PWS.SUB_CalculateTotals****
                  5. **Commit/Save **$BuyerOfferItemList** to Database**
                  6. **Delete**
                  7. **Call Microflow **Custom_Logging.SUB_Log_Info****
                  8. 🏁 **END:** Return `true`
               ➔ **If [false]:**
                  1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
                  2. **Call Microflow **EcoATM_PWS.SUB_BuyerOfferItem_BuildErrorMessage** (Result: **$Message**)**
                  3. **Update **$ManageFileDocument**
      - Set **Message** = `'Import failed - Data is not compliant'`
      - Set **ProcessPercentage** = `100`
      - Set **DetailledMessage** = `$Message`**
                  4. 🏁 **END:** Return `false`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Info****
            2. 🏁 **END:** Return `false`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.