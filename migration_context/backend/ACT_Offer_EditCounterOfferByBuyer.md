# Microflow Detailed Specification: ACT_Offer_EditCounterOfferByBuyer

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. 🔀 **DECISION:** `$ObjectInfo/IsCurrentUserAllowed`
   ➔ **If [false]:**
      1. **Maps to Page: **EcoATM_PWS.PWS_OfferAndCounterOffer_Review_Buyer****
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
      2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/Grade != 'A_YYY' and $currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'PWS'` (Result: **$BuyerOfferItem_FunctionalDevice**)**
      3. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/Grade = 'A_YYY' and $currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'PWS'` (Result: **$BuyerOfferItem_UntestedDevice**)**
      4. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'SPB'` (Result: **$BuyerOfferItem_CaseLot**)**
      5. **Update **$Offer** (and Save to DB)
      - Set **IsFunctionalDeviceExist** = `$BuyerOfferItem_FunctionalDevice != empty`
      - Set **IsCaseLotsExist** = `$BuyerOfferItem_CaseLot != empty`
      - Set **IsUntestedDeviceExist** = `$BuyerOfferItem_UntestedDevice != empty`**
      6. **Maps to Page: **EcoATM_PWS.PWSBuyerCounterOffers****
      7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.