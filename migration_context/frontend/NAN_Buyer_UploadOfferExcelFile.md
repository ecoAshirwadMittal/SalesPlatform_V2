# Nanoflow: NAN_Buyer_UploadOfferExcelFile

> SPKB-291 - PWS 1.2 - Upload Order

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

## 📥 Inputs

- **$OfferExcelImportDocument** (EcoATM_PWS.ManageFileDocument)
- **$BuyerCode** (EcoATM_BuyerManagement.BuyerCode)

## ⚙️ Execution Flow

1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Update **$OfferExcelImportDocument**
      - Set **Message** = `empty`
      - Set **ProcessPercentage** = `0`
      - Set **HasProcessFailed** = `false`**
3. **Call Microflow **EcoATM_PWS.VAL_OfferBuyerExcelFile_IsValid** (Result: **$IsValid**)**
4. 🔀 **DECISION:** `$IsValid`
   ➔ **If [true]:**
      1. **Close current page/popup**
      2. **Call Microflow **EcoATM_PWS.SUB_BuyerOffer_GetOrCreate** (Result: **$BuyerOffer**)**
      3. 🔀 **DECISION:** `$BuyerOffer!=empty`
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_PWS.SUB_Buyer_UploadExcelFileContent** (Result: **$IsSuccess**)**
            2. 🔀 **DECISION:** `$IsSuccess`
               ➔ **If [true]:**
                  1. **Update **$OfferExcelImportDocument**
      - Set **Message** = `'Successfully imported'`
      - Set **ProcessPercentage** = `100`
      - Set **HasProcessFailed** = `false`**
                  2. **Open Page: **EcoATM_PWS.BuyerOffer_Step2_LoadExcelFile****
                  3. **Call Microflow **Custom_Logging.SUB_Log_Info****
                  4. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Update **$OfferExcelImportDocument**
      - Set **ProcessPercentage** = `100`
      - Set **HasProcessFailed** = `true`**
                  2. **Open Page: **EcoATM_PWS.BuyerOffer_Step2_LoadExcelFile****
                  3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Update **$OfferExcelImportDocument**
      - Set **Message** = `'Unable to prepare the data'`
      - Set **HasProcessFailed** = `true`**
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$OfferExcelImportDocument**
      - Set **Message** = `'Selected file is not valid'`
      - Set **HasProcessFailed** = `true`**
      2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
