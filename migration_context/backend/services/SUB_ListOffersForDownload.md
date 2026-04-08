# Microflow Detailed Specification: SUB_ListOffersForDownload

### 📥 Inputs (Parameters)
- **$OfferMasterHelper** (Type: EcoATM_PWS.OfferMasterHelper)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$OfferMasterHelper/StatusSelected = EcoATM_PWS.ENUM_PWSOrderStatus.Ordered`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_PWS.Offer** Filter: `[OfferStatus='Ordered' or OfferStatus='Pending_Order']` (Result: **$OfferList_OrderedPending**)**
      2. 🏁 **END:** Return `$OfferList_OrderedPending`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$OfferMasterHelper/StatusSelected = empty`
         ➔ **If [true]:**
            1. **DB Retrieve **EcoATM_PWS.Offer**  (Result: **$OfferList_Total**)**
            2. 🏁 **END:** Return `$OfferList_Total`
         ➔ **If [false]:**
            1. **DB Retrieve **EcoATM_PWS.Offer** Filter: `[OfferStatus= $OfferMasterHelper/StatusSelected]` (Result: **$OfferList**)**
            2. 🏁 **END:** Return `$OfferList`

**Final Result:** This process concludes by returning a [List] value.