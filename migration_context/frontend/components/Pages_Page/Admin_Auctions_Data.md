# Page: Admin_Auctions_Data

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

  ↳ [Click] → **Page**: `AuctionUI.Auction_Overview`
  ↳ [Click] → **Page**: `AuctionUI.EcoATMDirectUser_Admin_Overview`
  ↳ [Click] → **Page**: `AuctionUI.AggreegatedInventoryTotals_Overview`
  ↳ [Click] → **Page**: `AuctionUI.AggregatedInventory_Overview`
  ↳ [Click] → **Page**: `AuctionUI.BidData_Overview`
  ↳ [Click] → **Page**: `AuctionUI.BidRound_Admin_Overview`
  ↳ [Click] → **Page**: `AuctionUI.BidDataDoc_Overview`
  ↳ [Click] → **Page**: `AuctionUI.SchedulingAuction_Overview_AllBuyers`
  ↳ [Click] → **Page**: `AuctionUI.SchedulingAuction_Overview`
  ↳ [Click] → **Page**: `EcoATM_EB.ReserveBid_Admin_Overview`
  ↳ [Click] → **Page**: `AuctionUI.UserStatus_Overview`
  ↳ [Click] → **Page**: `AuctionUI.BuyerCode_Overview`
  ↳ [Click] → **Page**: `EcoATM_DA.DAWeek_Overview`
  ↳ [Click] → **Page**: `EcoATM_PO.PODetail_Overview`
  ↳ [Click] → **Page**: `EcoATM_PO.PurchaseOrder_Overview`
  ↳ [Click] → **Page**: `AuctionUI.Week_Admin_Overview`
  ↳ [Click] → **Page**: `EcoATM_BuyerManagement.SalesRepresentative_Overview`
  ↳ [Click] → **Page**: `EcoATM_MDM.CompanyHoliday_Overview`
  ↳ [Click] → **Page**: `EcoATM_Reports.PG_CohortMapping`
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
