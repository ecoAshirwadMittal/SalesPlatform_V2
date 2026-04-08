# Microflow Detailed Specification: ACT_OpenPWSOfferItems

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Offer/OfferStatus = EcoATM_PWS.ENUM_PWSOrderStatus.Sales_Review`
   ➔ **If [true]:**
      1. **JavaCallAction**
      2. 🔀 **DECISION:** `$ObjectInfo/IsCurrentUserAllowed`
         ➔ **If [true]:**
            1. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
            2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/Grade != 'A_YYY' and $currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'PWS'` (Result: **$BuyerOfferItem_FunctionalDevice**)**
            3. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/Grade = 'A_YYY' and $currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'PWS'` (Result: **$BuyerOfferItem_UntestedDevice**)**
            4. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'SPB'` (Result: **$BuyerOfferItem_CaseLot**)**
            5. **Update **$Offer** (and Save to DB)
      - Set **IsFunctionalDeviceExist** = `$BuyerOfferItem_FunctionalDevice != empty`
      - Set **IsCaseLotsExist** = `$BuyerOfferItem_CaseLot != empty`
      - Set **IsUntestedDeviceExist** = `$BuyerOfferItem_UntestedDevice != empty`**
            6. **Call Microflow **Custom_Logging.SUB_Log_Info****
            7. **Call Microflow **EcoATM_PWS.SUB_CheckifAllOfferItemsAccepted****
            8. **Maps to Page: **EcoATM_PWS.PWSOffer_OfferItems****
            9. **Call Microflow **Custom_Logging.SUB_Log_Info****
            10. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Maps to Page: **EcoATM_PWS.PWS_OfferAndCounterOffer_Review****
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$Offer/OfferStatus = EcoATM_PWS.ENUM_PWSOrderStatus.Buyer_Acceptance`
         ➔ **If [true]:**
            1. **JavaCallAction**
            2. 🔀 **DECISION:** `$ObjectInfo_1/IsCurrentUserAllowed`
               ➔ **If [false]:**
                  1. **Maps to Page: **EcoATM_PWS.PWS_OfferAndCounterOffer_Review****
                  2. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Call Microflow **Custom_Logging.SUB_Log_Info****
                  2. **Maps to Page: **EcoATM_PWS.PWSOffer_OfferItems_ReadOnly****
                  3. **Call Microflow **Custom_Logging.SUB_Log_Info****
                  4. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Info****
            2. **Maps to Page: **EcoATM_PWS.PWSOffer_OfferItems_ReadOnly****
            3. **Call Microflow **Custom_Logging.SUB_Log_Info****
            4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.