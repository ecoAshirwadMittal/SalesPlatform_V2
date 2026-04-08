# Page: PG_DeviceAllocation

**Allowed Roles:** EcoATM_DA.Administrator, EcoATM_DA.SalesLeader, EcoATM_DA.SalesOps

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📦 **DataView** [Context] [DP: {Spacing top: Inner large, Spacing bottom: Inner large, Spacing left: Inner large, Spacing right: Inner large}]
  - 🧩 **Combo box** [DP: {Spacing bottom: Outer none}] (ID: `com.mendix.widget.web.combobox.Combobox`)
      - source: context
      - optionsSourceType: association
      - optionsSourceDatabaseCaptionType: attribute
      - optionsSourceAssociationCaptionType: attribute
      - optionsSourceAssociationCaptionAttribute: [Attr: AuctionUI.Auction.AuctionTitle]
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
  - 📦 **DataView** [Context]
      ↳ [acti] → **Nanoflow**: `EcoATM_DA.NA_ACT_GetDADataByWeek`
      ↳ [acti] → **Nanoflow**: `EcoATM_DA.NA_ACT_ConfirmReviewChanges`
      ↳ [acti] → **Nanoflow**: `EcoATM_DA.NA_ACT_FinalizeChanges`
  - 📦 **DataView** [Context]
    - 📦 **DataView** [MF: EcoATM_DA.SUB_AggregateRevenueTotal_GetOrCreate]
    - 🧩 **Data grid 2** [Class: `device-allocation-grid`] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: checkbox
        - itemSelectionMode: clear
        ➤ **columns**
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_DA.DeviceAllocation.ProductID]
            - header: Product ID
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
            - attribute: [Attr: EcoATM_DA.DeviceAllocation.Brand]
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
            - attribute: [Attr: EcoATM_DA.DeviceAllocation.ModelName]
            - header: Model Name
            - tooltip: {1}
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
            - attribute: [Attr: EcoATM_DA.DeviceAllocation.Grade]
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
            - showContentAs: dynamicText
            - attribute: [Attr: EcoATM_DA.DeviceAllocation.AvailableQty]
            - dynamicText: {1}
            - header: Aval. Qty
            - tooltip: Available Quantity
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
            - attribute: [Attr: EcoATM_DA.DeviceAllocation.Payout]
            - dynamicText: {1}
            - header: APP
            - tooltip: Average Payout Price
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
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_DA.DeviceAllocation.EB]
            - header: EB
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
            - attribute: [Attr: EcoATM_DA.DeviceAllocation.TargetPrice]
            - dynamicText: {1}
            - header: ATP
            - tooltip: Average Target Price
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
            - attribute: [Attr: EcoATM_DA.DeviceAllocation.SalesQty]
            - dynamicText: {1}
            - header: Sales Qty
            - tooltip: Sales Quantity
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
            - attribute: [Attr: EcoATM_DA.DeviceAllocation.AvgSalesPrice]
            - dynamicText: {1}
            - header: ASP
            - tooltip: Average Sales Price
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
            - attribute: [Attr: EcoATM_DA.DeviceAllocation.Revenue]
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
            - alignment: left
            - columnClass: `'price-col'
`
            - filterCaptionType: expression
            - showContentAs: dynamicText
            - attribute: [Attr: EcoATM_DA.DeviceAllocation.Margin]
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
            - alignment: left
            - columnClass: `'price-col'
`
            - filterCaptionType: expression
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_DA.DeviceAllocation.MarginPercentage]
            - header: Margin %
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
            - columnClass: `'percentage-col'
`
            - filterCaptionType: expression
            - showContentAs: dynamicText
            - attribute: [Attr: EcoATM_DA.DeviceAllocation.Review]
            - dynamicText: {1}
            - header: Review
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
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_DA.DeviceAllocation.ProductID]
            ➤ **content** (Widgets)
                ↳ [acti] → **Microflow**: `EcoATM_DA.ACT_DeviceAllocation_SeeBids`
            ➤ **filter** (Widgets)
                ↳ [acti] → **Microflow**: `EcoATM_DA.ACT_DeviceAllocation_ReviewAll`
            - visible: `true`
            - hidable: yes
            - width: autoFit
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - filterCaptionType: expression
        - pageSize: 50
        - pagination: buttons
        - pagingPosition: both
        - showPagingButtons: always
        - loadMoreButtonCaption: Load More
        - showEmptyPlaceholder: none
        - rowClass: `if $currentObject/Review='NS' then 'DA-review-row-NS' else if $currentObject/Review='US' then 'DA-review-row-US' else if $currentObject/Review='SU' then 'DA-review-row-SU' else ''
`
        - onClickTrigger: single
        - configurationStorageType: attribute
        - configurationAttribute: [Attr: EcoATM_DA.DAHelper.DAGridPersonlization]
        - exportDialogLabel: Export progress
        - cancelExportLabel: Cancel data export
        - selectRowLabel: Select row
        - loadingType: spinner
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
