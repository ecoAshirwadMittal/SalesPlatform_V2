# Page: PG_Bidder_Dashboard_HOT

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
- 🧩 **Hands On Table** [Class: `bidder-dashboard-table` | Style: `z-index:-1;`] (ID: `lfts.handsontable.HandsOnTable`)
    - EcoID: [Attr: AuctionUI.BidData.EcoID]
    - Brand: [Attr: AuctionUI.AggregatedInventory.Brand]
    - Model: [Attr: AuctionUI.AggregatedInventory.Model]
    - Model_Name: [Attr: AuctionUI.AggregatedInventory.Name]
    - ecoGrade: [Attr: AuctionUI.AggregatedInventory.MergedGrade]
    - Carrier: [Attr: AuctionUI.AggregatedInventory.Carrier]
    - MaximumQuantity: [Attr: AuctionUI.BidData.MaximumQuantity]
    - TargetPrice: [Attr: AuctionUI.BidData.TargetPrice]
    - BidQuantity: [Attr: AuctionUI.BidData.BidQuantity]
    - BidAmount: [Attr: AuctionUI.BidData.BidAmount]
    - User: [Attr: AuctionUI.BidData.User]
    - Code: [Attr: AuctionUI.BidData.Code]
    - CompanyName: [Attr: AuctionUI.BidData.CompanyName]
    - BuyerCodeType: [Attr: AuctionUI.BidData.BuyerCodeType]
    - Data_Wipe_Quantity: [Attr: AuctionUI.BidData.Data_Wipe_Quantity]
    - BidRound: [Attr: AuctionUI.BidData.BidRound]
    - previousRoundBidAmount: [Attr: AuctionUI.BidData.PreviousRoundBidAmount]
    - previousRoundBidQuantity: [Attr: AuctionUI.BidData.PreviousRoundBidQuantity]
    - Added: [Attr: AuctionUI.AggregatedInventory.CreatedAt]
    - licenseKey: `@EcoATM_BuyerManagement.const_HandsOnTable_LicenseKey`
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
