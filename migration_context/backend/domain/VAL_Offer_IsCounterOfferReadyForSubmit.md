# Microflow Detailed Specification: VAL_Offer_IsCounterOfferReadyForSubmit

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
2. **List Operation: **Filter** on **$undefined** where `EcoATM_PWS.ENUM_OfferItemStatus.Counter` (Result: **$CounterOfferItemList**)**
3. 🔀 **DECISION:** `$CounterOfferItemList!=empty`
   ➔ **If [true]:**
      1. **List Operation: **Filter** on **$undefined** where `empty` (Result: **$MissingBuyerCounterStatusOfferItemList**)**
      2. 🏁 **END:** Return `$MissingBuyerCounterStatusOfferItemList=empty`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.