# Microflow Detailed Specification: SUB_ChangeOfferStatus_GetOrderList

### 📥 Inputs (Parameters)
- **$ChangeOfferStatusHelper** (Type: EcoATM_PWS.ChangeOfferStatusHelper)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/AllPeriod`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/EcoATM_PWS.ChangeOfferStatusHelper_Order!=empty`
         ➔ **If [true]:**
            1. **Retrieve related **ChangeOfferStatusHelper_Order** via Association from **$ChangeOfferStatusHelper** (Result: **$OrderList**)**
            2. 🏁 **END:** Return `$OrderList`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `empty`
   ➔ **If [false]:**
      1. **DB Retrieve **EcoATM_PWS.Order** Filter: `[OrderDate!=empty]` (Result: **$OrderList_2**)**
      2. **List Operation: **FilterByExpression** on **$undefined** where `trimToDays($currentObject/OrderDate)>=$ChangeOfferStatusHelper/StartingDate and trimToDays($currentObject/OrderDate)<=$ChangeOfferStatusHelper/EndingDate` (Result: **$OrderList_filtered**)**
      3. 🏁 **END:** Return `$OrderList_filtered`

**Final Result:** This process concludes by returning a [List] value.