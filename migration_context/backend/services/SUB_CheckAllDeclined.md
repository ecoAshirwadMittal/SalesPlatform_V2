# Microflow Detailed Specification: SUB_CheckAllDeclined

### 📥 Inputs (Parameters)
- **$OfferItemList** (Type: EcoATM_PWS.OfferItem)

### ⚙️ Execution Flow (Logic Steps)
1. **AggregateList**
2. **List Operation: **Filter** on **$undefined** where `EcoATM_PWS.ENUM_OfferItemStatus.Decline` (Result: **$DeclineItemList**)**
3. **AggregateList**
4. 🔀 **DECISION:** `$DeclineCount = $OfferItemCount`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.