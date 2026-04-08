# Page: PG_BuyerAwardSummaryReportReview

**Allowed Roles:** EcoATM_Reports.Administrator, EcoATM_Reports.SalesLeader, EcoATM_Reports.SalesOps

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📦 **DataView** [Context] [Style: `max-width: 1520px;
min-width: 1000px;
padding: 24px;`]
    ↳ [acti] → **Nanoflow**: `EcoATM_Reports.NAV_BuyerAwardsSummaryReportsLandingPage`
  - 🔤 **Text**: "Weekly Budget:" [Style: `color: rgb(102, 103, 102);
font-size: 18px;
font-weight: 400;
`]
  - 🧩 **Data grid 2** [Class: `buyerDetails-grid`] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: checkbox
      - itemSelectionMode: clear
      ➤ **columns**
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_DA.BuyerDetail.ProductId]
          - header: Product Id
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
          - alignment: left
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_DA.BuyerDetail.Grade]
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
          - alignment: left
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_DA.BuyerDetail.Brand]
          - header: Brand
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
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_DA.BuyerDetail.Model]
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
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_DA.BuyerDetail.AvgSalesPrice]
          - dynamicText: {1}
          - header: ASP
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
          - alignment: left
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_DA.BuyerDetail.SalesQty]
          - dynamicText: {1}
          - header: Sales Qty
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
          - alignment: left
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_DA.BuyerDetail.Amount]
          - dynamicText: {1}
          - header: Amount
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
          - alignment: left
          - filterCaptionType: expression
      - pageSize: 20
      - pagination: buttons
      - pagingPosition: bottom
      - showPagingButtons: always
      - loadMoreButtonCaption: Load More
      - showEmptyPlaceholder: none
      - onClickTrigger: single
      - configurationStorageType: attribute
      - exportDialogLabel: Export progress
      - cancelExportLabel: Cancel data export
      - selectRowLabel: Select row
      - loadingType: spinner
  - 🧩 **Data grid 2** [Class: `buyerDetailsTotals-grid`] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: checkbox
      - itemSelectionMode: clear
      ➤ **columns**
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_DA.BuyerDetailTotals.SalesQty]
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - filterCaptionType: expression
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_DA.BuyerDetailTotals.SalesQty]
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - filterCaptionType: expression
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_DA.BuyerDetailTotals.SalesQty]
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - filterCaptionType: expression
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_DA.BuyerDetailTotals.SalesQty]
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - filterCaptionType: expression
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_DA.BuyerDetailTotals.SalesQty]
          ➤ **content** (Widgets)
          - header: Total
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_DA.BuyerDetailTotals.SalesQty]
          - dynamicText: {1}
          - header: Sales Qty
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_DA.BuyerDetailTotals.Amount]
          - dynamicText: {1}
          - header: Amount
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
          - alignment: left
          - filterCaptionType: expression
      - pageSize: 20
      - pagination: buttons
      - pagingPosition: bottom
      - showPagingButtons: always
      - loadMoreButtonCaption: Load More
      - showEmptyPlaceholder: none
      - onClickTrigger: single
      - configurationStorageType: attribute
      - exportDialogLabel: Export progress
      - cancelExportLabel: Cancel data export
      - selectRowLabel: Select row
      - loadingType: spinner
