# Microflow Detailed Specification: SUB_FutureDateChange

### 📥 Inputs (Parameters)
- **$MDMFuturePriceHelper** (Type: EcoATM_PWS.MDMFuturePriceHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. 🔀 **DECISION:** `$MDMFuturePriceHelper != empty and $MDMFuturePriceHelper/FuturePWSPriceDate != empty and $MDMFuturePriceHelper/FuturePWSPriceDate > [%BeginOfCurrentDay%]`
   ➔ **If [true]:**
      1. **Retrieve related **MDMFuturePriceHelper_Device** via Association from **$MDMFuturePriceHelper** (Result: **$DeviceList_FuturePrice**)**
      2. **CreateList**
      3. 🔄 **LOOP:** For each **$IteratorDevice_FuturePrice** in **$DeviceList_FuturePrice**
         │ 1. **Retrieve related **Price_Device_Future** via Association from **$IteratorDevice_FuturePrice** (Result: **$Price_Future**)**
         │ 2. **Update **$Price_Future**
      - Set **ExpirationDate** = `$MDMFuturePriceHelper/FuturePWSPriceDate`**
         │ 3. **Add **$$Price_Future
** to/from list **$NewPriceList_FuturePrice****
         └─ **End Loop**
      4. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[not(EcoATM_PWSMDM.Price_Device_Future/EcoATM_PWSMDM.PriceHistory)]` (Result: **$DeviceList_NoFuturePrice**)**
      5. 🔄 **LOOP:** For each **$IteratorDevice** in **$DeviceList_NoFuturePrice**
         │ 1. **Update **$MDMFuturePriceHelper**
      - Set **MDMFuturePriceHelper_Device** = `$IteratorDevice`**
         │ 2. **Create **EcoATM_PWSMDM.PriceHistory** (Result: **$NewPrice**)
      - Set **ExpirationDate** = `$MDMFuturePriceHelper/FuturePWSPriceDate`
      - Set **FutureDate** = `true`
      - Set **Price_Device_Future** = `$IteratorDevice`
      - Set **PriceHistory_DeviceList** = `$IteratorDevice`**
         │ 3. **Add **$$NewPrice
** to/from list **$NewPriceList_FuturePrice****
         └─ **End Loop**
      6. **Commit/Save **$NewPriceList_FuturePrice** to Database**
      7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      8. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.