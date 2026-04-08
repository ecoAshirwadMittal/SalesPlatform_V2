# Page: BuyerOfferItem_NewEdit

**Allowed Roles:** EcoATM_PWS.Administrator

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context]
  - 📝 **ReferenceSelector**: referenceSelector1
  - 📝 **ReferenceSelector**: referenceSelector2
    ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_BuyerOfferItem_UpdateByAdmin`
    ↳ [acti] → **Cancel Changes**
