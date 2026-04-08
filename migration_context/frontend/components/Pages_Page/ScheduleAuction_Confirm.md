# Page: ScheduleAuction_Confirm

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.SalesOps

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context] [Class: `scheduleconfirm`]
  - 📝 **DatePicker**: datePicker1 🔒 [Read-Only]
  - 📝 **DatePicker**: datePicker2 🔒 [Read-Only]
  - 📝 **DatePicker**: datePicker3 🔒 [Read-Only]
  - 📝 **DatePicker**: datePicker4 🔒 [Read-Only]
  - 📝 **DatePicker**: datePicker5 🔒 [Read-Only]
  - 📝 **DatePicker**: datePicker6 🔒 [Read-Only]
    ↳ [acti] → **Cancel Changes**
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_SaveScheduleAuction`
