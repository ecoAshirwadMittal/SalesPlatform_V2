# Microflow Detailed Specification: SUB_ShowPWSOffersPage

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Offer/OfferStatus = EcoATM_PWS.ENUM_PWSOrderStatus.Sales_Review or $Offer/OfferStatus = EcoATM_PWS.ENUM_PWSOrderStatus.Buyer_Acceptance`
   ➔ **If [true]:**
      1. **JavaCallAction**
      2. **DB Retrieve **EcoATM_Lock.Lock** Filter: `[ObjectID = $OfferObjectId and LockedBy = $currentUser/Name]` (Result: **$Lock**)**
      3. **Update **$Lock** (and Save to DB)
      - Set **Active** = `false`**
      4. **Call Microflow **EcoATM_PWS.SUB_SetAndResetTags****
      5. **Maps to Page: **EcoATM_PWS.PWSOffers****
      6. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **EcoATM_PWS.SUB_SetAndResetTags****
      2. **Maps to Page: **EcoATM_PWS.PWSOffers****
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.