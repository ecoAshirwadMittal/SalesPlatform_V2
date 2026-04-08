# Microflow Detailed Specification: DS_GetOfferItem_CaseLot

### 📥 Inputs (Parameters)
- **$Device** (Type: EcoATM_PWSMDM.Device)
- **$Order** (Type: EcoATM_PWS.BuyerOffer)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$CaseLot** (Type: EcoATM_PWSMDM.CaseLot)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWS.BuyerOfferItem** Filter: `[ ( EcoATM_PWS.BuyerOfferItem_Device = $Device and EcoATM_PWS.BuyerOfferItem_BuyerOffer = $Order and EcoATM_PWS.BuyerOfferItem_BuyerCode = $BuyerCode and EcoATM_PWS.BuyerOfferItem_CaseLot =$CaseLot ) ]` (Result: **$OfferItem**)**
2. 🏁 **END:** Return `$OfferItem`

**Final Result:** This process concludes by returning a [Object] value.