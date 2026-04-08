# Page: PriceHistory_NewEdit

**Allowed Roles:** EcoATM_PWSMDM.Administrator

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context]
  - 📝 **DatePicker**: datePicker1
  - 📝 **ReferenceSelector**: referenceSelector1
    ↳ [acti] → **Microflow**: `EcoATM_PWSMDM.ACT_PriceHistory_UpdateByAdmin`
    ↳ [acti] → **Cancel Changes**
