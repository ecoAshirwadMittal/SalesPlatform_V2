# Page: Inventory_Overview

**Allowed Roles:** EcoATM_PO.Administrator, EcoATM_PO.SalesOps

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 🧩 **Active Menu Selector** (ID: `mendix.activemenuselector.ActiveMenuSelector`)
    - menuWidgetName: navigationTree3
    - menuItemTitle: Inventory
- 📦 **DataView** [Context]
  - 📝 **ReferenceSelector**: referenceSelector1
  - 🧩 **Gallery** [DP: {Borders: Both, Grid spacing: None}] (ID: `com.mendix.widget.web.gallery.Gallery`)
      ➤ **content** (Widgets)
        - 🖼️ **Image**: calendar_days [DP: {Spacing right: Outer small}]
      - desktopItems: 1
      - tabletItems: 1
      - phoneItems: 1
      - pageSize: 5
      - pagination: buttons
      - pagingPosition: below
      - showEmptyPlaceholder: none
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_OpenAuctionPicker`
  - 🧩 **Data grid 2** [Class: `datagridfilter` | DP: {Borders: Both}] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: checkbox
      ➤ **columns**
          - showContentAs: attribute
          - attribute: [Attr: AuctionUI.AggregatedInventory.ecoID]
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
          - showContentAs: attribute
          - attribute: [Attr: AuctionUI.AggregatedInventory.Merged_Grade]
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
          - showContentAs: attribute
          - attribute: [Attr: AuctionUI.AggregatedInventory.Model_Name]
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
          - showContentAs: attribute
          - attribute: [Attr: AuctionUI.AggregatedInventory.Data_Wipe_Quantity]
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
          - showContentAs: dynamicText
          - attribute: [Attr: AuctionUI.AggregatedInventory.Data_Wipe_Target_Price]
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
          - showContentAs: attribute
          - attribute: [Attr: AuctionUI.AggregatedInventory.Total_Quantity]
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
          - showContentAs: dynamicText
          - attribute: [Attr: AuctionUI.AggregatedInventory.TargetPrice]
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
          - showContentAs: customContent
          - attribute: [Attr: AuctionUI.AggregatedInventory.ecoID]
          - visible: `true`
          - hidable: no
          - width: manual
          - minWidth: auto
          - minWidthLimit: 100
          - size: 0
          - alignment: right
          - columnClass: `'ColumnPickerCell'`
      - pageSize: 20
      - pagination: buttons
      - pagingPosition: bottom
      - showEmptyPlaceholder: custom
      - exportDialogLabel: Export progress
      - cancelExportLabel: Cancel data export
      - showPagingButtons: always
      - onClickTrigger: single
      - selectRowLabel: Select row
- 📦 **DataView** [Context]
    ↳ [acti] → **Nanoflow**: `AuctionUI.ACT_Download_Inventory_To_Excel`
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_StartWeekDelete`
