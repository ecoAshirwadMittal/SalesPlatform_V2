# Page: Buyer_Select

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataGrid** [Context] [DP: {Style: Bordered}]
  - ⚡ **Button**: Toggle Search [Style: Default]
  - ⚡ **Button**: Select [Style: Primary]
  - 📊 **Column**: Buyer Name [Width: 100] [Style: `font-size:18px;`]
  ↳ [acti] → **Page**: `AuctionUI.Buyer_New_Old`
