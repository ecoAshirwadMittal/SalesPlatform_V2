# Nanoflow: NAV_Device_Wizard_Page3

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

## 📥 Inputs

- **$MDMFuturePriceHelper** (EcoATM_PWS.MDMFuturePriceHelper)

## ⚙️ Execution Flow

1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
2. 🔀 **DECISION:** `$MDMFuturePriceHelper/FuturePWSPriceDate != empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$MDMFuturePriceHelper/FuturePWSPriceDate >= [%BeginOfCurrentDay%]`
         ➔ **If [true]:**
            1. **Retrieve related **PricingUpdateFile_MDMFuturePriceHelper** via Association from **$MDMFuturePriceHelper** (Result: **$PricingUpdateFile**)**
            2. 🔀 **DECISION:** `$PricingUpdateFile != empty and $PricingUpdateFile/HasContents and $PricingUpdateFile/Size > 0`
               ➔ **If [true]:**
                  1. **Create **EcoATM_PWS.FileUploadProcessHelper** (Result: **$NewFileUploadProcessHelper**)
      - Set **CurrentPercentage** = `0`
      - Set **FileName** = `$PricingUpdateFile/Name`
      - Set **Message** = `'Processing'`**
                  2. **Open Page: **EcoATM_PWS.Page3_LoadingBar****
                  3. **Update **$NewFileUploadProcessHelper**
      - Set **CurrentPercentage** = `25`**
                  4. **Update **$NewFileUploadProcessHelper**
      - Set **CurrentPercentage** = `60`
      - Set **Message** = `'Uploading'`**
                  5. **Call Microflow **EcoATM_PWS.ACT_UploadPWSPricingFile** (Result: **$Message**)**
                  6. 🔀 **DECISION:** `$Message = empty`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$MDMFuturePriceHelper/FuturePWSPriceDate > [%BeginOfCurrentDay%]`
                           ➔ **If [true]:**
                              1. **Commit/Save **$MDMFuturePriceHelper** to Database**
                              2. **Update **$NewFileUploadProcessHelper**
      - Set **CurrentPercentage** = `100`**
                              3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
                              4. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **Rollback **$MDMFuturePriceHelper****
                              2. **Update **$NewFileUploadProcessHelper**
      - Set **CurrentPercentage** = `100`**
                              3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
                              4. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Rollback **$MDMFuturePriceHelper****
                        2. **Delete **$PricingUpdateFile** from Database**
                        3. **Update **$NewFileUploadProcessHelper**
      - Set **CurrentPercentage** = `-1`
      - Set **Message** = `$Message`**
                        4. **Call Microflow **Custom_Logging.SUB_Log_Error** (Result: **$Log**)**
                        5. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
                  2. **Show Message (Warning): `Please select a file to upload`**
                  3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
            2. **ValidationFeedback on **FuturePWSPriceDate**: `Please enter a date that is not in the past.`**
            3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
      2. **ValidationFeedback on **FuturePWSPriceDate**: `Please select a date.`**
      3. 🏁 **END:** Return empty

## ⚠️ Error Handling

- On error in **Call Microflow **EcoATM_PWS.ACT_UploadPWSPricingFile** (Result: **$Message**)** → Rollback **$MDMFuturePriceHelper**

## 🏁 Returns
`Void`
