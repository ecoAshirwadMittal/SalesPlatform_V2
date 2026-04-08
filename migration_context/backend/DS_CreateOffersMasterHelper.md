# Microflow Detailed Specification: DS_CreateOffersMasterHelper

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **OfferMasterHelper_Session** via Association from **$currentSession** (Result: **$OfferMasterHelperList**)**
2. 🔀 **DECISION:** `$OfferMasterHelperList!=empty`
   ➔ **If [true]:**
      1. **List Operation: **Head** on **$undefined** (Result: **$OfferMasterHelper**)**
      2. 🏁 **END:** Return `$OfferMasterHelper`
   ➔ **If [false]:**
      1. **DB Retrieve **EcoATM_PWS.Offer** Filter: `[OfferStatus = 'Sales_Review']` (Result: **$Offer**)**
      2. **Create Variable **$HasItems** = `false`**
      3. 🔀 **DECISION:** `$Offer != empty`
         ➔ **If [true]:**
            1. **Update Variable **$HasItems** = `true`**
            2. **Create **EcoATM_PWS.OfferMasterHelper** (Result: **$NewOffersMasterHelper**)
      - Set **StatusSelected** = `EcoATM_PWS.ENUM_PWSOrderStatus.Sales_Review`
      - Set **OfferMasterHelper_Session** = `$currentSession`
      - Set **HasSalesReviewItems** = `$HasItems`**
            3. 🏁 **END:** Return `$NewOffersMasterHelper`
         ➔ **If [false]:**
            1. **Create **EcoATM_PWS.OfferMasterHelper** (Result: **$NewOffersMasterHelper**)
      - Set **StatusSelected** = `EcoATM_PWS.ENUM_PWSOrderStatus.Sales_Review`
      - Set **OfferMasterHelper_Session** = `$currentSession`
      - Set **HasSalesReviewItems** = `$HasItems`**
            2. 🏁 **END:** Return `$NewOffersMasterHelper`

**Final Result:** This process concludes by returning a [Object] value.