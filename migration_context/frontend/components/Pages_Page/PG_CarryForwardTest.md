# Page: PG_CarryForwardTest

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📦 **DataView** [MF: AuctionUI.ACT_CreateNewCarryForward]
  - ⚡ **Button**: radioButtons1
  - ⚡ **Button**: radioButtons2
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_CarryForwardBids`
