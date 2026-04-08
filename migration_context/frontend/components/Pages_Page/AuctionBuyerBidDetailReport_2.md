# Page: AuctionBuyerBidDetailReport_2

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.Bidder, AuctionUI.SalesOps

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 🧩 **Active Menu Selector** (ID: `mendix.activemenuselector.ActiveMenuSelector`)
    - menuWidgetName: navigationTree3
    - menuItemTitle: Auction Bid Report
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
- 🔤 **Text**: "Current Auction" [Style: `font-size: 28px;
font-style: normal;
font-weight: 450;
line-height: 27px; ` | DP: {Spacing bottom: Outer small, Spacing top: Outer small}]
- 🔤 **Text**: "Previous Auction" [Style: `font-size: 28px;
font-style: normal;
font-weight: 450;
line-height: 27px; ` | DP: {Spacing bottom: Outer small, Spacing top: Outer small}]
- 🧩 **Data grid 2** [Class: `datagridfilternospace` | DP: {Borders: Both, Spacing top: Outer small}] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: checkbox
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.BuyerBidDetailReport.ProductID]
        - header: Product ID
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.BuyerBidDetailReport.Model]
        - header: Model
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.BuyerBidDetailReport.ModelName]
        - header: Model name
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 4
        - alignment: left
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.BuyerBidDetailReport.Grade]
        - header: Grade
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 2
        - alignment: left
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.BuyerBidDetailReport.Quantity1]
        - header: Qty
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: center
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.BuyerBidDetailReport.AverageBid1]
        - header: Avg. Bid
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: center
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.BuyerBidDetailReport.Quantity2]
        - header: Qty
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: center
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.BuyerBidDetailReport.AverageBid2]
        - header: Avg. Bid
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: center
    - pageSize: 20
    - pagination: virtualScrolling
    - pagingPosition: bottom
    - showPagingButtons: always
    - showEmptyPlaceholder: none
    - onClickTrigger: single
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
