# Page: AggregatedInventory_NewEdit

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context]
  - 📝 **DatePicker**: datePicker1
  - ⚡ **Button**: radioButtons1
  - ⚡ **Button**: radioButtons2
  - ⚡ **Button**: radioButtons3
  - 📝 **ReferenceSelector**: referenceSelector1
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_AggregateInventory_UpdateByAdmin`
    ↳ [acti] → **Cancel Changes**
