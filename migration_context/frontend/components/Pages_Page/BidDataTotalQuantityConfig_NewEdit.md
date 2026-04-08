# Page: BidDataTotalQuantityConfig_NewEdit

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.SalesOps, AuctionUI.SalesRep

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context]
    ↳ [Enter] → **Nanoflow**: `AuctionUI.NF_RestrictToNumbers`
  - 📝 **DropDown**: dropDown1
    ↳ [Enter] → **Nanoflow**: `AuctionUI.NF_RestrictToNumbers`
    ↳ [Enter] → **Nanoflow**: `AuctionUI.NF_RestrictToNumbers`
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_BidDataTotalQuantityConfig_Save`
    ↳ [acti] → **Cancel Changes**
