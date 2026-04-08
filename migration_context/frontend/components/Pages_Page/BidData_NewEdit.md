# Page: BidData_NewEdit

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context]
  - 📝 **DatePicker**: datePicker1
  - 📝 **DatePicker**: datePicker2
  - ⚡ **Button**: radioButtons3
  - ⚡ **Button**: radioButtons4
  - 📝 **ReferenceSelector**: referenceSelector1
  - 📝 **ReferenceSelector**: referenceSelector2
  - 📝 **ReferenceSelector**: referenceSelector3
  - 📝 **ReferenceSelector**: referenceSelector5
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_BidData_UpdateByAdmin`
    ↳ [acti] → **Cancel Changes**
