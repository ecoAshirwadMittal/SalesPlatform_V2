# Page: PG_UserAgreement

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.Bidder, AuctionUI.SalesOps, AuctionUI.SalesRep

**Layout:** `AuctionUI.ecoATM_Popup_Layout_NoTitle_CloseButton`

## Widget Tree

- 📦 **DataView** [MF: EcoATM_UserManagement.DS_GetCurrentEcoATMDirectUser]
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_DowloadAgreement`
  - 📝 **CheckBox**: checkBox1
    ↳ [acti] → **Nanoflow**: `AuctionUI.ACT_AcknowledgeClicked`
