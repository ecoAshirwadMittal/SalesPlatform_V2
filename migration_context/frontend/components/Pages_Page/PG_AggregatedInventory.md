# Page: PG_AggregatedInventory

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.SalesOps

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 🧩 **Active Menu Selector** (ID: `mendix.activemenuselector.ActiveMenuSelector`)
    - menuWidgetName: navigationTree3
    - menuItemTitle: Inventory
- 📦 **DataView** [Context]
  - 🧩 **Combo box** [DP: {Spacing left: Outer small, Spacing bottom: Outer none}] (ID: `com.mendix.widget.web.combobox.Combobox`)
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
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_PreCreateAuction`
    ↳ [acti] → **Nanoflow**: `AuctionUI.ACT_GetAggregateInventoryforWeek`
  - 📦 **DataView** [Context]
  - 📦 **DataView** [MF: AuctionUI.DS_GetOrCreateAggregatedInventoryTotals]
- 🧩 **Data grid 2** [Class: `inventory-grid`] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: checkbox
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.AggregatedInventory.EcoId]
        - header: Product ID
        ➤ **filter** (Widgets)
          - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
              - defaultFilter: equal
              - delay: 500
        - hidable: yes
        - width: autoFit
        - size: 1
        - alignment: left
        - visible: `true`
        - minWidth: manual
        - minWidthLimit: 125
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.AggregatedInventory.MergedGrade]
        - header: Grades
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
        - hidable: yes
        - width: autoFill
        - size: 1
        - alignment: left
        - visible: `true`
        - minWidth: auto
        - minWidthLimit: 100
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.AggregatedInventory.Brand]
        - header: Brand
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
        - hidable: yes
        - width: autoFill
        - size: 1
        - alignment: left
        - visible: `true`
        - minWidth: auto
        - minWidthLimit: 100
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.AggregatedInventory.Model]
        - header: Model
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
        - hidable: yes
        - width: autoFill
        - size: 1
        - alignment: left
        - visible: `true`
        - minWidth: auto
        - minWidthLimit: 100
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.AggregatedInventory.Name]
        - header: Model Name
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
        - hidable: yes
        - width: autoFit
        - size: 1
        - alignment: left
        - visible: `true`
        - minWidth: manual
        - minWidthLimit: 280
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.AggregatedInventory.Carrier]
        - header: Carrier
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
        - hidable: yes
        - width: autoFill
        - size: 1
        - alignment: left
        - visible: `true`
        - minWidth: auto
        - minWidthLimit: 100
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.AggregatedInventory.DWTotalQuantity]
        - header: DW Qty
        ➤ **filter** (Widgets)
          - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
              - defaultFilter: equal
              - delay: 500
        - hidable: yes
        - width: autoFill
        - size: 1
        - alignment: left
        - visible: `true`
        - minWidth: auto
        - minWidthLimit: 100
        - filterCaptionType: expression
        - showContentAs: dynamicText
        - attribute: [Attr: AuctionUI.AggregatedInventory.DWAvgTargetPrice]
        - dynamicText: {1}
        - header: DW Target Price
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
        - attribute: [Attr: AuctionUI.AggregatedInventory.TotalQuantity]
        - header: Total Qty
        ➤ **filter** (Widgets)
          - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
              - defaultFilter: equal
              - delay: 500
        - hidable: yes
        - width: autoFill
        - size: 1
        - alignment: left
        - visible: `true`
        - minWidth: auto
        - minWidthLimit: 100
        - filterCaptionType: expression
        - showContentAs: dynamicText
        - attribute: [Attr: AuctionUI.AggregatedInventory.AvgTargetPrice]
        - dynamicText: {1}
        - header: Target Price
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
        - showContentAs: customContent
        - attribute: [Attr: AuctionUI.AggregatedInventory.EcoId]
        - visible: `true`
        - hidable: no
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 0
        - alignment: right
        - columnClass: `'ColumnPickerCell'`
        - filterCaptionType: expression
    - pageSize: 20
    - pagination: buttons
    - pagingPosition: bottom
    - showEmptyPlaceholder: custom
    ➤ **emptyPlaceholder** (Widgets)
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - showPagingButtons: always
    - onClickTrigger: single
    - selectRowLabel: Select row
    - itemSelectionMode: clear
    - loadMoreButtonCaption: Load More
    - configurationStorageType: attribute
    - loadingType: spinner
- 📦 **DataView** [Context]
    ↳ [acti] → **Nanoflow**: `AuctionUI.ACT_Download_Inventory_To_Excel`
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
