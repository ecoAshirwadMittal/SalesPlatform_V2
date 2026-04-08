# Page: AuctionBuyerBidDetailReport

**Allowed Roles:** EcoATM_Reports.Administrator, EcoATM_Reports.SalesOps

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
    - itemSelectionMode: clear
    - onClickTrigger: single
- 🔤 **Text**: "Current Auction" [Style: `font-size: 20px;
font-style: normal;
font-weight: 450;
line-height: 27px; ` | DP: {Spacing bottom: Outer none, Spacing top: Outer large}]
- 🔤 **Text**: "Previous Auction" [Style: `font-size: 20px;
font-style: normal;
font-weight: 450;
line-height: 27px; ` | DP: {Spacing bottom: Outer none, Spacing top: Outer large}]
- 🧩 **Data grid 2** [Class: `datagridfilternospace` | DP: {Borders: Both, Spacing top: Outer small}] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: checkbox
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_Reports.BuyerBidDetailReport_NP.ProductId]
        - header: Product ID
        ➤ **filter** (Widgets)
          - 🧩 **Number filter** [Class: `dataGridFilterSpacing`] (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
              - defaultFilter: equal
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_Reports.BuyerBidDetailReport_NP.Model]
        - header: Model
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_Reports.BuyerBidDetailReport_NP.ModelName]
        - header: Model name
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 4
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_Reports.BuyerBidDetailReport_NP.Grade]
        - header: Grade
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 2
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: dynamicText
        - attribute: [Attr: EcoATM_Reports.BuyerBidDetailReport_NP.Quantity1]
        - dynamicText: {1}
        - header: Qty
        ➤ **filter** (Widgets)
          - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
              - defaultFilter: equal
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: center
        - filterCaptionType: expression
        - showContentAs: dynamicText
        - attribute: [Attr: EcoATM_Reports.BuyerBidDetailReport_NP.Bid1]
        - dynamicText: {1}
        - header: Bid
        ➤ **filter** (Widgets)
          - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
              - defaultFilter: equal
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: center
        - filterCaptionType: expression
        - showContentAs: dynamicText
        - attribute: [Attr: EcoATM_Reports.BuyerBidDetailReport_NP.Quantity2]
        - dynamicText: {1}
        - header: Qty
        ➤ **filter** (Widgets)
          - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
              - defaultFilter: equal
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: center
        - filterCaptionType: expression
        - showContentAs: dynamicText
        - attribute: [Attr: EcoATM_Reports.BuyerBidDetailReport_NP.Bid2]
        - dynamicText: {1}
        - header: Bid
        ➤ **filter** (Widgets)
          - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
              - defaultFilter: equal
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: center
        - filterCaptionType: expression
    - pageSize: 20
    - pagination: virtualScrolling
    - pagingPosition: bottom
    - showPagingButtons: always
    - showEmptyPlaceholder: custom
    ➤ **emptyPlaceholder** (Widgets)
    - onClickTrigger: single
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
    - itemSelectionMode: clear
    - loadMoreButtonCaption: Load More
    - configurationStorageType: attribute
    - loadingType: spinner
- 📦 **DataView** [Context]
