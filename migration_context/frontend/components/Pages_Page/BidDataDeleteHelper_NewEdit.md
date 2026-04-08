# Page: BidDataDeleteHelper_NewEdit

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📦 **DataView** [Context] [DP: {Spacing bottom: Outer large}]
  - 📝 **DatePicker**: datePicker1
  - 📝 **DatePicker**: datePicker2
    ↳ [acti] → **Microflow**: `AuctionUI.MF_CleanupUsingStoredProcedure`
    ↳ [acti] → **Cancel Changes**
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_ExecuteAdhocQuery`
