# Page: PG_BidRankingConfiguration

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

  ↳ [acti] → **Page**: `AuctionUI.Business_Auctions_ControlCenter`
- 📦 **DataView** [MF: AuctionUI.DS_GetorCreateBidRanking]
  - 🧩 **Switch** (ID: `com.mendix.widget.custom.switch.Switch`)
      - booleanAttribute: [Attr: AuctionUI.BidRanking.DisplayRank]
  - 🧩 **Switch** (ID: `com.mendix.widget.custom.switch.Switch`)
      - booleanAttribute: [Attr: AuctionUI.BidRanking.IncludeEBForRanking]
    ↳ [Change] → **Save Changes**
    ↳ [Change] → **Save Changes**
    ↳ [Change] → **Save Changes**
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
