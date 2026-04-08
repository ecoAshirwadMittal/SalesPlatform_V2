# Page: Business_Auctions_ControlCenter

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

  ↳ [Click] → **Page**: `AuctionUI.SchedulingAuction_Overview`
  ↳ [Click] → **Page**: `AuctionUI.BidData_Overview_QA_Testing`
  ↳ [Click] → **Page**: `AuctionUI.Round2DefaultCriteria`
  ↳ [Click] → **Page**: `AuctionUI.PG_Round3Criteria`
  ↳ [Click] → **Page**: `EcoATM_BuyerManagement.PG_BuyerSubmitConfig`
  ↳ [Click] → **Page**: `EcoATM_BuyerManagement.QualifiedBuyerCodes_Overview`
  ↳ [Click] → **Page**: `AuctionUI.BidDataTotalQuantityConfig_Overview`
  ↳ [Click] → **Page**: `AuctionUI.PG_BidRankingConfiguration`
  ↳ [Click] → **Page**: `AuctionUI.PG_SharepointMethodConfiguration`
  ↳ [Click] → **Page**: `AuctionUI.PG_UserHelperGuide`
  ↳ [Click] → **Page**: `AuctionUI.AuctionControl_Tech_Overview`
  ↳ [Click] → **Page**: `AuctionUI.Auction_Overview_QA_Testing`
  ↳ [Click] → **Page**: `AuctionUI.PG_AggInventory_TargetPriceView`
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
