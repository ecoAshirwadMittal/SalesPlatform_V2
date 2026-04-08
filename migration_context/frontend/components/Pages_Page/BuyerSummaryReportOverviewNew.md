# Page: BuyerSummaryReportOverviewNew

**Allowed Roles:** EcoATM_Reports.Administrator, EcoATM_Reports.SalesLeader, EcoATM_Reports.SalesOps, EcoATM_Reports.SalesRep

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 🧩 **Active Menu Selector** (ID: `mendix.activemenuselector.ActiveMenuSelector`)
    - menuWidgetName: navigationTree3
    - menuItemTitle: Auction Bid Report
- 🧩 **Data grid 2** [Class: `datagridfilter` | DP: {Borders: Both, Spacing top: Outer none}] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: checkbox
    ➤ **columns**
        - showContentAs: customContent
        - attribute: [Attr: EcoATM_Reports.BuyerBidSummaryReport_NP.BuyerCode]
        ➤ **content** (Widgets)
            ↳ [acti] → **Microflow**: `EcoATM_Reports.ACT_GetBuyerBidDetailReport_fromSummary`
        - header: BuyerCode
        - visible: `true`
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_Reports.BuyerBidSummaryReport_NP.BuyerName]
        - header: Buyer Name
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: dynamicText
        - attribute: [Attr: EcoATM_Reports.BuyerBidSummaryReport_NP.LotsBid1]
        - dynamicText: {1}
        - header: Lots Bid
        ➤ **filter** (Widgets)
          - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
              - defaultFilter: equal
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: right
        - columnClass: `if($currentObject/LotsBid1 != empty and $currentObject/LotsBid1 != 0)
then if $currentObject/UpOrDownLots= 'Up'
then 'bidReportGreenUpArrow'
else if $currentObject/UpOrDownLots= 'Down'
then 'bidReportRedDownArrow'
else 'bidReportNoArrow'
else 'bidReportNoArrow'
`
        - filterCaptionType: expression
        - showContentAs: dynamicText
        - attribute: [Attr: EcoATM_Reports.BuyerBidSummaryReport_NP.UnitsBid1]
        - dynamicText: {1}
        - header: Units Bid
        ➤ **filter** (Widgets)
          - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
              - defaultFilter: equal
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: right
        - columnClass: `if($currentObject/UnitsBid1!=empty and $currentObject/UnitsBid1!=0)
then if $currentObject/UpOrDownUnits= 'Up' 
then 'bidReportGreenUpArrow'
else if $currentObject/UpOrDownUnits= 'Down'
then 'bidReportRedDownArrow'
else 'bidReportNoArrow'
else 'bidReportNoArrow'`
        - filterCaptionType: expression
        - showContentAs: dynamicText
        - attribute: [Attr: EcoATM_Reports.BuyerBidSummaryReport_NP.LotsBid2]
        - dynamicText: {1}
        - header: Lots Bid
        ➤ **filter** (Widgets)
          - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
              - defaultFilter: equal
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: right
        - filterCaptionType: expression
        - showContentAs: dynamicText
        - attribute: [Attr: EcoATM_Reports.BuyerBidSummaryReport_NP.UnitsBid2]
        - dynamicText: {1}
        - header: Units Bid
        ➤ **filter** (Widgets)
          - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
              - defaultFilter: equal
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: right
        - filterCaptionType: expression
    - pageSize: 20
    - pagination: buttons
    - pagingPosition: bottom
    - showPagingButtons: always
    - showEmptyPlaceholder: custom
    - onClickTrigger: single
    ➤ **filtersPlaceholder** (Widgets)
      - 🔤 **Text**: "Current Auction" [Style: `font-size: 28px;
font-style: normal;
font-weight: 450;
line-height: 27px; ` | DP: {Spacing bottom: Outer none, Spacing top: Outer large}]
      - 🔤 **Text**: "Previous Auction" [Style: `font-size: 28px;
font-style: normal;
font-weight: 450;
line-height: 27px; ` | DP: {Spacing bottom: Outer none, Spacing top: Outer large}]
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
    - itemSelectionMode: clear
    - loadMoreButtonCaption: Load More
    - configurationStorageType: attribute
    - loadingType: spinner
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
