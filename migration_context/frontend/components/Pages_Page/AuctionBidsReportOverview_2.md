# Page: AuctionBidsReportOverview_2

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.SalesOps

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

  ↳ [acti] → **Microflow**: `AuctionUI.ACT_AuctionBidsReportPicker`
- 📦 **DataView** [Context] [DP: {Spacing bottom: Outer none}]
- 🧩 **Data grid 2** [Class: `datagridfilter` | DP: {Borders: Both, Spacing top: Outer none}] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: checkbox
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.AuctionsBidReportTuple.ProductID]
        - header: Product ID
        ➤ **filter** (Widgets)
          - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
              - defaultFilter: equal
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: autoFit
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.AuctionsBidReportTuple.Model]
        - header: Model
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
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.AuctionsBidReportTuple.ModelName]
        - header: Model Name
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
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.AuctionsBidReportTuple.Grade]
        - header: Grade
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
        - alignment: center
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.AuctionsBidReportTuple.RoundOneQuantity]
        - header: Qty
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
        - alignment: center
        - showContentAs: dynamicText
        - attribute: [Attr: AuctionUI.AuctionsBidReportTuple.RoundOneAverageBid]
        - dynamicText: {1}
        - header: Avg. Bid
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
        - alignment: center
        - showContentAs: dynamicText
        - attribute: [Attr: AuctionUI.AuctionsBidReportTuple.RoundTwoQuantity]
        - dynamicText: {1}
        - header: Qty
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
        - alignment: center
        - showContentAs: dynamicText
        - attribute: [Attr: AuctionUI.AuctionsBidReportTuple.RoundTwoAverageBid]
        - dynamicText: {1}
        - header: Avg. Bid
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
        - alignment: center
        - showContentAs: dynamicText
        - attribute: [Attr: AuctionUI.AuctionsBidReportTuple.UpsellRoundQuantity]
        - dynamicText: {1}
        - header: Qty
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
        - alignment: center
        - showContentAs: dynamicText
        - attribute: [Attr: AuctionUI.AuctionsBidReportTuple.UpsellRoundAverageBid]
        - dynamicText: {1}
        - header: Avg. Bid
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
        - alignment: center
        - showContentAs: dynamicText
        - attribute: [Attr: AuctionUI.AuctionsBidReportTuple.Revenue]
        - dynamicText: {1}
        - header: Revenue
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
        - alignment: center
        - showContentAs: dynamicText
        - attribute: [Attr: AuctionUI.AuctionsBidReportTuple.Margin]
        - dynamicText: {1}
        - header: Margin
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
        - alignment: center
    - pageSize: 20
    - pagination: buttons
    - pagingPosition: bottom
    - showPagingButtons: always
    - showEmptyPlaceholder: custom
    - onClickTrigger: single
    ➤ **filtersPlaceholder** (Widgets)
      - 🔤 **Text**: "Round 1" [Style: `font-size: 28px;
font-style: normal;
font-weight: 450;
line-height: 27px; ` | DP: {Spacing bottom: Outer none, Spacing top: Outer large}]
      - 🔤 **Text**: "Round 2" [Style: `font-size: 28px;
font-style: normal;
font-weight: 450;
line-height: 27px; ` | DP: {Spacing bottom: Outer none, Spacing top: Outer large}]
      - 🔤 **Text**: "Upsell Round" [Style: `font-size: 28px;
font-style: normal;
font-weight: 450;
line-height: 27px; ` | DP: {Spacing top: Outer large, Spacing bottom: Outer none}]
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
