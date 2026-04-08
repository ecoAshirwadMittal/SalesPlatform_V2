# Page: PG_BuyerAwardsSummaryReport

**Allowed Roles:** EcoATM_Reports.Administrator, EcoATM_Reports.SalesOps, EcoATM_Reports.SalesLeader

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 🧩 **Active Menu Selector** (ID: `mendix.activemenuselector.ActiveMenuSelector`)
    - menuWidgetName: navigationTree3
    - menuItemTitle: Buyer Summary Report
- 📦 **DataView** [Context]
  - 🧩 **Combo box** [DP: {Spacing bottom: Outer none}] (ID: `com.mendix.widget.web.combobox.Combobox`)
      - source: context
      - optionsSourceType: association
      - optionsSourceDatabaseCaptionType: attribute
      - optionsSourceAssociationCaptionType: expression
      - optionsSourceAssociationCaptionExpression: `$currentObject/WeekDisplay
`
      - filterType: contains
      - optionsSourceAssociationCustomContentType: no
      - optionsSourceDatabaseCustomContentType: no
      - selectionMethod: checkbox
      - selectedItemsStyle: text
      - selectAllButtonCaption: Select all
      - ariaRequired: `false`
      - clearButtonAriaLabel: Clear selection
      - removeValueAriaLabel: Remove value
      - a11ySelectedValue: Selected value:
      - a11yOptionsAvailable: Number of options available:
      - a11yInstructions: Use up and down arrow keys to navigate. Press Enter or Space Bar keys to select.
      - staticDataSourceCustomContentType: no
      - readOnlyStyle: text
      - loadingType: spinner
      - selectedItemsSorting: none
  - 🧩 **Data grid 2** [Class: `buyerSummary-grid`] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: checkbox
      ➤ **columns**
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_DA.BuyerSummary.BuyerCode]
          ➤ **content** (Widgets)
              ↳ [acti] → **Microflow**: `EcoATM_Reports.ACT_BuyerAwardSummaryReport_ShowPage`
          - header: Buyer Code
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
          - attribute: [Attr: EcoATM_DA.BuyerSummary.BuyerName]
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
          - attribute: [Attr: EcoATM_DA.BuyerSummary.SalesQty]
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
          - attribute: [Attr: EcoATM_DA.BuyerSummary.Amount]
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
          - columnClass: `'price-col'
`
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_DA.BuyerSummary.WeeklyBudget]
          - dynamicText: {1}
          - header: Weekly Budget
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
          - columnClass: `'price-col'
`
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_DA.BuyerSummary.PreviousWeekSalesQty]
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
          - attribute: [Attr: EcoATM_DA.BuyerSummary.PreviousWeekAmount]
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
          - columnClass: `'price-col'
`
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_DA.BuyerSummary.PreviousWeekWeeklyBudget]
          - dynamicText: {1}
          - header: Weekly Budget
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
          - columnClass: `'price-col'
`
          - filterCaptionType: expression
      - pageSize: 20
      - pagination: buttons
      - pagingPosition: bottom
      - showEmptyPlaceholder: custom
      - exportDialogLabel: Export progress
      - cancelExportLabel: Cancel data export
      - showPagingButtons: always
      - onClickTrigger: single
      - selectRowLabel: Select row
      - itemSelectionMode: clear
      - loadMoreButtonCaption: Load More
      - configurationStorageType: attribute
      - loadingType: spinner
  - 🧩 **Data grid 2** [Class: `buyerSummaryTotal-grid`] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: checkbox
      - itemSelectionMode: clear
      ➤ **columns**
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_Reports.BuyerAwardSummaryTotals.SalesQty]
          - header: Buyer Code
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - filterCaptionType: expression
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_Reports.BuyerAwardSummaryTotals.SalesQty]
          ➤ **content** (Widgets)
          - header: Buyer Name
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: right
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_Reports.BuyerAwardSummaryTotals.SalesQty]
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
          - attribute: [Attr: EcoATM_Reports.BuyerAwardSummaryTotals.Amount]
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
          - columnClass: `'price-col'
`
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_Reports.BuyerAwardSummaryTotals.WeeklyBudget]
          - dynamicText: {1}
          - header: Weekly Budget
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
          - columnClass: `'price-col'
`
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_Reports.BuyerAwardSummaryTotals.PreviousWeekSalesQty]
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
          - attribute: [Attr: EcoATM_Reports.BuyerAwardSummaryTotals.PreviousWeekAmount]
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
          - columnClass: `'price-col'
`
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_Reports.BuyerAwardSummaryTotals.PreviousWeekWeeklyBudget]
          - dynamicText: {1}
          - header: Weekly Budget
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
          - columnClass: `'price-col'
`
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
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
