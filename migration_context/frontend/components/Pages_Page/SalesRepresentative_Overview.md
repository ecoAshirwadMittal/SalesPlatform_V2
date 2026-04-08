# Page: SalesRepresentative_Overview

**Allowed Roles:** EcoATM_BuyerManagement.Administrator, EcoATM_BuyerManagement.SalesLeader, EcoATM_BuyerManagement.SalesOps, EcoATM_BuyerManagement.SalesRep

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: rowClick
    - itemSelectionMode: clear
    - loadingType: spinner
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_BuyerManagement.SalesRepresentative.SalesRepFirstName]
        - header: Sales rep first name
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
              - screenReaderInputCaption: Search
        - visible: `true`
        - filterCaptionType: expression
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_BuyerManagement.SalesRepresentative.SalesRepLastName]
        - header: Sales rep last name
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
              - screenReaderInputCaption: Search
        - visible: `true`
        - filterCaptionType: expression
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_BuyerManagement.SalesRepresentative.Active]
        - header: Active
        ➤ **filter** (Widgets)
          - 🧩 **Drop-down filter** (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
              - selectedItemsStyle: text
              - selectionMethod: checkbox
        - visible: `true`
        - filterCaptionType: expression
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: customContent
        - attribute: [Attr: EcoATM_BuyerManagement.SalesRepresentative.SalesRepFirstName]
        ➤ **content** (Widgets)
            ↳ [acti] → **Page**: `EcoATM_BuyerManagement.SalesRepresentative_NewEdit`
            ↳ [acti] → **Microflow**: `EcoATM_BuyerManagement.ACT_DeleteSalesRep`
        - visible: `true`
        - filterCaptionType: expression
        - hidable: no
        - width: autoFit
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
    - pageSize: 20
    - pagination: buttons
    - showPagingButtons: always
    - pagingPosition: bottom
    - loadMoreButtonCaption: Load More
    - showEmptyPlaceholder: none
    - onClickTrigger: double
    - configurationStorageType: attribute
    ➤ **filtersPlaceholder** (Widgets)
        ↳ [acti] → **Page**: `EcoATM_BuyerManagement.SalesRepresentative_NewEdit`
        ↳ [acti] → **Microflow**: `EcoATM_BuyerManagement.ACT_FixAllSalesRepNames`
        ↳ [acti] → **Nanoflow**: `EcoATM_BuyerManagement.NAN_SalesRepresentative_SynchronizeToSnowflake`
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
