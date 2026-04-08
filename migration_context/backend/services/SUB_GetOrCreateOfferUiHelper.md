# Microflow Detailed Specification: SUB_GetOrCreateOfferUiHelper

### 📥 Inputs (Parameters)
- **$ENUM_PWSOrderStatus** (Type: Variable)
- **$OfferMasterHelper** (Type: EcoATM_PWS.OfferMasterHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **OffersUiHelper_OfferMasterHelper** via Association from **$OfferMasterHelper** (Result: **$OffersUiHelperList**)**
2. 🔀 **DECISION:** `$OffersUiHelperList!=empty`
   ➔ **If [true]:**
      1. **List Operation: **Find** on **$undefined** where `$ENUM_PWSOrderStatus` (Result: **$OffersUiHelperFound**)**
      2. 🔀 **DECISION:** `$OffersUiHelperFound!=empty`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `$OffersUiHelperFound`
         ➔ **If [false]:**
            1. **Create **EcoATM_PWS.OffersUiHelper** (Result: **$NewOffersUiHelper**)
      - Set **OffersUiHelper_OfferMasterHelper** = `$OfferMasterHelper`**
            2. 🏁 **END:** Return `$NewOffersUiHelper`
   ➔ **If [false]:**
      1. **Create **EcoATM_PWS.OffersUiHelper** (Result: **$NewOffersUiHelper**)
      - Set **OffersUiHelper_OfferMasterHelper** = `$OfferMasterHelper`**
      2. 🏁 **END:** Return `$NewOffersUiHelper`

**Final Result:** This process concludes by returning a [Object] value.