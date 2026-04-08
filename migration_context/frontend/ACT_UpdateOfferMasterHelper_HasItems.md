# Nanoflow: ACT_UpdateOfferMasterHelper_HasItems

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

## 📥 Inputs

- **$OfferMasterHelper** (EcoATM_PWS.OfferMasterHelper)

## ⚙️ Execution Flow

1. **Retrieve related **OffersUiHelper_OfferMasterHelper** via Association from **$OfferMasterHelper** (Result: **$OffersUiHelperList**)**
2. **List Operation: **Find** on **$OffersUiHelperList** where `$OfferMasterHelper/StatusSelected` (Result: **$OffersUiHelper_selected**)**
3. **Create Variable **$Count** = `$OffersUiHelper_selected/OfferCount`**
4. 🔀 **DECISION:** `$OfferMasterHelper/StatusSelected= EcoATM_PWS.ENUM_PWSOrderStatus.Ordered or $OfferMasterHelper/StatusSelected = EcoATM_PWS.ENUM_PWSOrderStatus.Pending_Order`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$OfferMasterHelper/StatusSelected = empty`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$OfferMasterHelper/StatusSelected = EcoATM_PWS.ENUM_PWSOrderStatus.Sales_Review`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$OfferMasterHelper/StatusSelected = EcoATM_PWS.ENUM_PWSOrderStatus.Declined`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$OfferMasterHelper/StatusSelected = EcoATM_PWS.ENUM_PWSOrderStatus.Buyer_Acceptance`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$OfferMasterHelper`
                           ➔ **If [true]:**
                              1. **Update Variable **$Count** = `0`**
                              2. **Update **$OfferMasterHelper** (and Save to DB)
      - Set **HasBuyerAcceptanceItems** = `if $Count>0 then true else false`**
                              3. 🏁 **END:** Return `$OfferMasterHelper`
                     ➔ **If [true]:**
                        1. **Update Variable **$Count** = `0`**
                        2. **Update **$OfferMasterHelper** (and Save to DB)
      - Set **HasDeclinedItems** = `if $Count>0 then true else false`**
                        3. 🏁 **END:** Return `$OfferMasterHelper`
               ➔ **If [true]:**
                  1. **Update Variable **$Count** = `0`**
                  2. **Update **$OfferMasterHelper** (and Save to DB)
      - Set **HasSalesReviewItems** = `if $Count>0 then true else false`**
                  3. 🏁 **END:** Return `$OfferMasterHelper`
         ➔ **If [true]:**
            1. **Update Variable **$Count** = `0`**
            2. **Update **$OfferMasterHelper** (and Save to DB)
      - Set **HasTotalItems** = `if $Count>0 then true else false`**
            3. 🏁 **END:** Return `$OfferMasterHelper`
   ➔ **If [true]:**
      1. **Update Variable **$Count** = `0`**
      2. **Update **$OfferMasterHelper** (and Save to DB)
      - Set **HasOrderedItems** = `if $Count>0 then true else false`**
      3. 🏁 **END:** Return `$OfferMasterHelper`

## 🏁 Returns
`Object`
