# Microflow Detailed Specification: SUB_CheckForFinalizeOfferItems

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
2. **List Operation: **Filter** on **$undefined** where `EcoATM_PWS.ENUM_OfferItemStatus.Finalize` (Result: **$FinalizeOfferList**)**
3. 🏁 **END:** Return `$FinalizeOfferList!=empty`

**Final Result:** This process concludes by returning a [Boolean] value.