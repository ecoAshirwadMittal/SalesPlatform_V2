# Nanoflow: SUB_OfferBuyer_IsExcelDataSuccess_2

## 📥 Inputs

- **$OfferDataExcelImporterList** (EcoATM_PWS.OfferDataExcelImporter)
- **$BuyerCode** (AuctionUI.BuyerCode)

## ⚙️ Execution Flow

1. 🔀 **DECISION:** `$OfferDataExcelImporterList!=empty`
   ➔ **If [true]:**
      1. **List Operation: **FilterByExpression** on **$OfferDataExcelImporterList** where `$currentObject/Quantity=empty or $currentObject/Quantity=0` (Result: **$EmptyOrZeoQuantityList**)**
      2. 🔀 **DECISION:** `$EmptyOrZeoQuantityList=empty`
         ➔ **If [true]:**
            1. **Create List **$UnresolvedSKUOfferDataExcelImporterList****
            2. **Create List **$DuplicateOfferDataExcelImporterList****
            3. **Create List **$CorrespondingDeviceList****
            4. 🔄 **LOOP:** For each **$Iterator** in **$OfferDataExcelImporterList**
               │ 1. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[SKU=$Iterator/SKU]` (Result: **$Device**)**
               │ 2. 🔀 **DECISION:** `$Device!=empty`
               │    ➔ **If [true]:**
               │       1. **List Operation: **Find** on **$CorrespondingDeviceList** where `$Device/SKU` (Result: **$AlreadyExist**)**
               │       2. 🔀 **DECISION:** `$AlreadyExist!=empty`
               │          ➔ **If [true]:**
               │             1. **Add **$$Iterator
** to/from list **$DuplicateOfferDataExcelImporterList****
               │             2. **Call Microflow **Custom_Logging.SUB_Log_Warning****
               │             3. ⏭️ **CONTINUE** (next iteration)
               │          ➔ **If [false]:**
               │             1. **Add **$$Device
** to/from list **$CorrespondingDeviceList****
               │             2. ⏭️ **CONTINUE** (next iteration)
               │    ➔ **If [false]:**
               │       1. **Add **$$Iterator
** to/from list **$UnresolvedSKUOfferDataExcelImporterList****
               │       2. **Call Microflow **Custom_Logging.SUB_Log_Warning****
               │       3. ⏭️ **CONTINUE** (next iteration)
               └─ **End Loop**
            5. 🔀 **DECISION:** `$DuplicateOfferDataExcelImporterList=empty and $UnresolvedSKUOfferDataExcelImporterList=empty`
               ➔ **If [true]:**
                  1. **Create List **$OfferItemList****
                  2. **Create **EcoATM_PWS.Offer** (Result: **$NewOffer**)
      - Set **OfferStatus** = `EcoATM_PWS.ENUM_PWSOrderStatus.InProgress`
      - Set **Offer_BuyerCode** = `$BuyerCode`**
                  3. **Create Variable **$TotalAmount** = `0`**
                  4. 🔄 **LOOP:** For each **$IteratorOfferDataExcelImporter** in **$OfferDataExcelImporterList**
                     │ 1. **List Operation: **Find** on **$CorrespondingDeviceList** where `$IteratorOfferDataExcelImporter/SKU` (Result: **$TargetDevice**)**
                     │ 2. **Create **EcoATM_PWS.OfferItem** (Result: **$NewOfferItem**)
      - Set **Quantity** = `$IteratorOfferDataExcelImporter/Quantity`
      - Set **OfferPrice** = `$IteratorOfferDataExcelImporter/OfferPrice`
      - Set **TotalPrice** = `$IteratorOfferDataExcelImporter/TotalPrice`
      - Set **OfferItem_Offer** = `$NewOffer`
      - Set **OfferItem_Device** = `$TargetDevice`**
                     │ 3. **Update Variable **$TotalAmount** = `$TotalAmount+$IteratorOfferDataExcelImporter/TotalPrice`**
                     │ 4. **Add **$$NewOfferItem
** to/from list **$OfferItemList****
                     │ 5. ⏭️ **CONTINUE** (next iteration)
                     └─ **End Loop**
                  5. **Update **$NewOffer**
      - Set **OfferTotal** = `$TotalAmount`**
                  6. **Commit/Save **$NewOffer** to Database**
                  7. **Commit/Save **$OfferItemList** to Database**
                  8. **Delete **$OfferDataExcelImporterList** from Database**
                  9. **Call Microflow **Custom_Logging.SUB_Log_Info****
                  10. 🏁 **END:** Return `true`
               ➔ **If [false]:**
                  1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
                  2. 🏁 **END:** Return `false`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Info****
            2. 🏁 **END:** Return `false`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. 🏁 **END:** Return `false`

## 🏁 Returns
`Boolean`
