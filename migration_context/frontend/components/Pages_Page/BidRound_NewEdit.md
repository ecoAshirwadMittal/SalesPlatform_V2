# Page: BidRound_NewEdit

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context]
  - ⚡ **Button**: radioButtons1
  - 📝 **DatePicker**: datePicker1
  - ⚡ **Button**: radioButtons2
  - 📝 **DatePicker**: datePicker2
  - 📝 **ReferenceSelector**: referenceSelector1
  - 📝 **ReferenceSelector**: referenceSelector2
  - 📝 **ReferenceSelector**: referenceSelector3
  - 📝 **ReferenceSelector**: referenceSelector4
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_BidRound_UpdateByAdmin`
    ↳ [acti] → **Cancel Changes**
