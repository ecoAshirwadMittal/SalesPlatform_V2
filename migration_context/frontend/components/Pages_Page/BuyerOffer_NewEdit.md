# Page: BuyerOffer_NewEdit

**Allowed Roles:** EcoATM_PWS.Administrator

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context]
  - ⚡ **Button**: radioButtons1
  - 📝 **ReferenceSelector**: referenceSelector1
    ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_BuyerOffer_UpdateByAdmin`
    ↳ [acti] → **Cancel Changes**
