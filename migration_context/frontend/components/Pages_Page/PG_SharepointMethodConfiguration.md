# Page: PG_SharepointMethodConfiguration

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

  ↳ [acti] → **Page**: `AuctionUI.Business_Auctions_ControlCenter`
- 📦 **DataView** [MF: AuctionUI.DS_GetOrCreateSharepointMethod]
  - ⚡ **Button**: radioButtons1
    ↳ [Change] → **Save Changes**
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
