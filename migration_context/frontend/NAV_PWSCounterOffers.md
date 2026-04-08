# Microflow Detailed Specification: NAV_PWSCounterOffers

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_PWS.DS_BuyerCodeBySession** (Result: **$BuyerCode**)**
2. **DB Retrieve **EcoATM_PWS.Offer** Filter: `[EcoATM_PWS.Offer_BuyerCode=$BuyerCode] [OfferStatus='Buyer_Acceptance']` (Result: **$OfferList**)**
3. **AggregateList**
4. 🔀 **DECISION:** `$Count=1`
   ➔ **If [true]:**
      1. **List Operation: **Head** on **$undefined** (Result: **$UniqueOffer**)**
      2. **JavaCallAction**
      3. 🔀 **DECISION:** `$ObjectInfo/IsCurrentUserAllowed`
         ➔ **If [false]:**
            1. **Maps to Page: **EcoATM_PWS.PWS_OfferAndCounterOffer_Review_Buyer****
            2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Retrieve related **OfferItem_Offer** via Association from **$UniqueOffer** (Result: **$OfferItemList**)**
            2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/Grade != 'A_YYY' and $currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'PWS'` (Result: **$BuyerOfferItem_FunctionalDevice**)**
            3. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/Grade = 'A_YYY' and $currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'PWS'` (Result: **$BuyerOfferItem_UntestedDevice**)**
            4. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'SPB'` (Result: **$BuyerOfferItem_CaseLot**)**
            5. **Update **$UniqueOffer** (and Save to DB)
      - Set **IsFunctionalDeviceExist** = `$BuyerOfferItem_FunctionalDevice != empty`
      - Set **IsCaseLotsExist** = `$BuyerOfferItem_CaseLot != empty`
      - Set **IsUntestedDeviceExist** = `$BuyerOfferItem_UntestedDevice != empty`**
            6. **Maps to Page: **EcoATM_PWS.PWSBuyerCounterOffers****
            7. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Maps to Page: **EcoATM_PWS.PWSBuyerCounterOffer_Overview****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.