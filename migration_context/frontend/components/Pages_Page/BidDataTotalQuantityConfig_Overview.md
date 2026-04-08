# Page: BidDataTotalQuantityConfig_Overview

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.SalesOps, AuctionUI.SalesRep

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

  ↳ [acti] → **Page**: `AuctionUI.Business_Auctions_ControlCenter`
- 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: rowClick
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.BidDataTotalQuantityConfig.EcoID]
        - header: Eco ID
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
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.BidDataTotalQuantityConfig.Grade]
        - header: Grade
        ➤ **filter** (Widgets)
          - 🧩 **Drop-down filter** (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
              - selectedItemsStyle: text
              - selectionMethod: checkbox
        - visible: `true`
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.BidDataTotalQuantityConfig.DataWipeQuantity]
        - header: Data Wipe Quantity
        ➤ **filter** (Widgets)
          - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
              - defaultFilter: equal
              - delay: 500
        - visible: `true`
        - filterCaptionType: expression
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.BidDataTotalQuantityConfig.NonDWQuantity]
        - header: Additional non DW Quantity
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
        - showContentAs: customContent
        - attribute: [Attr: AuctionUI.BidDataTotalQuantityConfig.EcoID]
        ➤ **content** (Widgets)
            ↳ [acti] → **Page**: `AuctionUI.BidDataTotalQuantityConfig_NewEdit`
            ↳ [acti] → **Delete**
        - visible: `true`
        - hidable: no
        - width: autoFit
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
    - pageSize: 20
    - pagination: buttons
    - pagingPosition: top
    - showPagingButtons: always
    - showEmptyPlaceholder: none
    - onClickTrigger: double
    ➤ **filtersPlaceholder** (Widgets)
        ↳ [acti] → **Microflow**: `AuctionUI.ACT_BidDataTotalQuantityConfig_Create`
        ↳ [acti] → **Nanoflow**: `AuctionUI.NF_BidDataTotalQuantity_Download`
        ↳ [acti] → **Page**: `EcoATM_BuyerManagement.PG_BidDataTotalQuantityConfig_Upload`
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
