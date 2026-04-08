# Page: Carrier_NewEdit

**Allowed Roles:** EcoATM_PWSMDM.Administrator

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context]
  - ⚡ **Button**: radioButtons1
  - 📦 **DataGrid** [Context]
    - ⚡ **Button**: Search [Style: Default]
    - 📊 **Column**: SKU [Width: 100]
    ↳ [acti] → **Microflow**: `EcoATM_PWSMDM.ACT_Carrier_UpdateByAdmin`
    ↳ [acti] → **Cancel Changes**
