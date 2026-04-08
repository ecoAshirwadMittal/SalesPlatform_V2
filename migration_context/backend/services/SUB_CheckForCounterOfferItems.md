# Microflow Detailed Specification: SUB_CheckForCounterOfferItems

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
2. **List Operation: **Filter** on **$undefined** where `EcoATM_PWS.ENUM_OfferItemStatus.Counter` (Result: **$CounterOfferList**)**
3. 🔀 **DECISION:** `$CounterOfferList!=empty`
   ➔ **If [true]:**
      1. **Update **$Offer**
      - Set **CounterItemsExist** = `true`
      - Set **UpdateDate** = `[%CurrentDateTime%]`**
      2. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. **Update **$Offer**
      - Set **CounterItemsExist** = `false`
      - Set **UpdateDate** = `[%CurrentDateTime%]`**
      2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.