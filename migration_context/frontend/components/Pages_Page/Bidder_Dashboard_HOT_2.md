# Page: Bidder_Dashboard_HOT_2

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.Bidder, AuctionUI.SalesRep

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 🔤 **Text**: "Switch Buyer Code"
- 🧩 **Gallery** [DP: {Borders: Both}] (ID: `com.mendix.widget.web.gallery.Gallery`)
    ➤ **content** (Widgets)
      - 🖼️ **Image**: Hotel
    - desktopItems: 1
    - tabletItems: 1
    - phoneItems: 1
    - pageSize: 20
    - pagination: buttons
    - pagingPosition: below
    - showEmptyPlaceholder: none
- 📦 **DataView** [Context]
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_BidDataDoc_ExportExcel_OnBuyerCodeSelectR1`
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_BidDataDoc_ExportExcel_OnBuyerCodeSelectR2`
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_BidDataDoc_ExportExcel_OnBuyerCodeSelectR3`
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_OpenBidSubmitConfirmation`
- 🧩 **Hands On Table** [Class: `bidder-dashboard-table` | Style: `z-index:-1;`] (ID: `lfts.handsontable.HandsOnTable`)
    - EcoID: [Attr: AuctionUI.BidData_Helper.EcoID]
    - Brand: [Attr: AuctionUI.AggregatedInventory.Brand]
    - Model: [Attr: AuctionUI.AggregatedInventory.Model]
    - Model_Name: [Attr: AuctionUI.AggregatedInventory.Model_Name]
    - ecoGrade: [Attr: AuctionUI.AggregatedInventory.Merged_Grade]
    - Carrier: [Attr: AuctionUI.AggregatedInventory.Carrier]
    - MaximumQuantity: [Attr: AuctionUI.BidData_Helper.MaximumQuantity]
    - TargetPrice: [Attr: AuctionUI.BidData_Helper.TargetPrice]
    - BidQuantity: [Attr: AuctionUI.BidData_Helper.BidQuantity]
    - BidAmount: [Attr: AuctionUI.BidData_Helper.BidAmount]
    - User: [Attr: AuctionUI.BidData_Helper.User]
    - Code: [Attr: AuctionUI.BidData_Helper.Code]
    - CompanyName: [Attr: AuctionUI.BidData_Helper.CompanyName]
    - BuyerCodeType: [Attr: AuctionUI.BidData_Helper.BuyerCodeType]
    - Data_Wipe_Quantity: [Attr: AuctionUI.AggregatedInventory.Data_Wipe_Quantity]
