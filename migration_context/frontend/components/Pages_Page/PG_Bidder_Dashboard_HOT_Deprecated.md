# Page: PG_Bidder_Dashboard_HOT_Deprecated

**Allowed Roles:** EcoATM_BuyerManagement.Administrator, EcoATM_BuyerManagement.Bidder, EcoATM_BuyerManagement.SalesRep

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 🔤 **Text**: "Switch Buyer Code"
- 🧩 **Gallery** [DP: {Borders: Both}] (ID: `com.mendix.widget.web.gallery.Gallery`)
    ➤ **content** (Widgets)
      - 🖼️ **Image**: Hotel
    - desktopItems: 1
    - tabletItems: 1
    - phoneItems: 1
    - pageSize: 1
    - pagination: buttons
    - pagingPosition: below
    - showEmptyPlaceholder: none
    - itemSelectionMode: clear
    - onClickTrigger: single
- 📦 **DataView** [Context]
    ↳ [acti] → **Microflow**: `EcoATM_BuyerManagement.ACT_BidDataDoc_ExportExcel`
    ↳ [acti] → **Nanoflow**: `EcoATM_BidData.ACT_BidData_SelectImportSheet`
  - 📦 **DataView** [Context]
  - ⚡ **Button**: Submit Bids [Style: Primary] [Class: `buttonshape_nowidth` | DP: {Align self: Right, Align icon: Right, Spacing left: Inner large}]
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_OpenBidSubmitConfirmation`
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_OpenBidSubmitConfirmation`
- 📦 **DataView** [Context]
    ↳ [acti] → **Microflow**: `EcoATM_BidData.ACT_Start_CarryoverBids`
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
