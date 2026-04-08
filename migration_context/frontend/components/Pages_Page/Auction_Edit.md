# Page: Auction_Edit

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - ⚡ **Button**: radioButtons1
  - 📝 **ReferenceSelector**: referenceSelector1
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_Auction_UpdateByAdmin`
    ↳ [acti] → **Cancel Changes**
