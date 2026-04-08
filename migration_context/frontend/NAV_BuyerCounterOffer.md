# Nanoflow: NAV_BuyerCounterOffer

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

## 📥 Inputs

- **$OfferAndOrdersView** (EcoATM_PWS.OfferAndOrdersView)

## ⚙️ Execution Flow

1. **DB Retrieve **EcoATM_PWS.Offer** Filter: `[ ( OfferID = $OfferAndOrdersView/OrderNumber ) ]` (Result: **$CounterOffer**)**
2. **Retrieve related **OfferItem_Offer** via Association from **$CounterOffer** (Result: **$OfferItemList**)**
3. **List Operation: **FindByExpression** on **$OfferItemList** where `$currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/Grade != 'A_YYY' and $currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'PWS'` (Result: **$BuyerOfferItem_FunctionalDevice**)**
4. **List Operation: **FindByExpression** on **$OfferItemList** where `$currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/Grade = 'A_YYY' and $currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'PWS'` (Result: **$BuyerOfferItem_UntestedDevice**)**
5. **List Operation: **FindByExpression** on **$OfferItemList** where `$currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'SPB'` (Result: **$BuyerOfferItem_CaseLot**)**
6. **Update **$CounterOffer** (and Save to DB)
      - Set **IsFunctionalDeviceExist** = `$BuyerOfferItem_FunctionalDevice != empty`
      - Set **IsCaseLotsExist** = `$BuyerOfferItem_CaseLot != empty`
      - Set **IsUntestedDeviceExist** = `$BuyerOfferItem_UntestedDevice != empty`**
7. **Open Page: **EcoATM_PWS.PWSBuyerCounterOffers****
8. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
