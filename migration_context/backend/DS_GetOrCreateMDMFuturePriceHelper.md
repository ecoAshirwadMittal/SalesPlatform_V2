# Microflow Detailed Specification: DS_GetOrCreateMDMFuturePriceHelper

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWS.MDMFuturePriceHelper**  (Result: **$MDMFuturePriceHelperList**)**
2. 🔀 **DECISION:** `$MDMFuturePriceHelperList != empty`
   ➔ **If [true]:**
      1. **List Operation: **Head** on **$undefined** (Result: **$MDMFuturePriceHelper**)**
      2. 🏁 **END:** Return `$MDMFuturePriceHelper`
   ➔ **If [false]:**
      1. **Create **EcoATM_PWS.MDMFuturePriceHelper** (Result: **$NewMDMFuturePriceHelper**)**
      2. 🏁 **END:** Return `$NewMDMFuturePriceHelper`

**Final Result:** This process concludes by returning a [Object] value.