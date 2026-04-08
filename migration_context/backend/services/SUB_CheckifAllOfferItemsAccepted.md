# Microflow Detailed Specification: SUB_CheckifAllOfferItemsAccepted

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/SalesOfferItemStatus = EcoATM_PWS.ENUM_OfferItemStatus.Finalize or $currentObject/SalesOfferItemStatus = EcoATM_PWS.ENUM_OfferItemStatus.Counter` (Result: **$OfferItemList_CounterOrFinalize**)**
3. 🔀 **DECISION:** `$OfferItemList_CounterOrFinalize=empty`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_PWS.SUB_CalculateCounterOfferSummary****
      2. **Update **$Offer** (and Save to DB)
      - Set **isValidOffer** = `true`**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.