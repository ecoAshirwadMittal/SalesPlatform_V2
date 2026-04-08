# Microflow Detailed Specification: SUB_CheckIfQtyAdjusted

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/FinalOfferQuantity<$currentObject/OfferQuantity` (Result: **$OfferItemWithAdjustedQty**)**
3. 🔀 **DECISION:** `$OfferItemWithAdjustedQty!=empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.