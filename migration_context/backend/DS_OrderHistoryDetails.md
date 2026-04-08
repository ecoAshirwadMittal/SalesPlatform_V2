# Microflow Detailed Specification: DS_OrderHistoryDetails

### 📥 Inputs (Parameters)
- **$OfferAndOrdersView** (Type: EcoATM_PWS.OfferAndOrdersView)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$OfferAndOrdersView/OrderNumber != empty and trim($OfferAndOrdersView/OrderNumber) != ''`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$OfferAndOrdersView/OfferOrderType = 'Offer'`
         ➔ **If [true]:**
            1. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[ ( EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferID = $OfferAndOrdersView/OrderNumber ) ]` (Result: **$OfferItemList**)**
            2. 🏁 **END:** Return `$OfferItemList`
         ➔ **If [false]:**
            1. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[ ( EcoATM_PWS.OfferItem_Order/EcoATM_PWS.Order/OrderNumber = $OfferAndOrdersView/OrderNumber ) ]` (Result: **$OfferItemList_1**)**
            2. 🏁 **END:** Return `$OfferItemList_1`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [List] value.