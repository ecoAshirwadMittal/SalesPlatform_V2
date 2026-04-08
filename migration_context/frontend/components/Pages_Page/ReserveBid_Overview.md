# Page: ReserveBid_Overview

**Allowed Roles:** EcoATM_EB.Administrator, EcoATM_EB.SalesLeader, EcoATM_EB.SalesOps

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📦 **DataView** [MF: EcoATM_EB.ACT_GetMostRecentReserveBidRefresh] 👁️ (If: `1=2`)
- 🧩 **Data grid 2** [Class: `datagridfilter` | DP: {Borders: Both}] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: rowClick
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_EB.ReserveBid.ProductId]
        - header: Product ID
        ➤ **filter** (Widgets)
          - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
              - defaultFilter: equal
              - placeholder: Product ID
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
        - attribute: [Attr: EcoATM_EB.ReserveBid.Grade]
        - header: Grade
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - placeholder: Grade
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
        - attribute: [Attr: EcoATM_EB.ReserveBid.Brand]
        - header: Brand
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - placeholder: Brand
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
        - attribute: [Attr: EcoATM_EB.ReserveBid.Model]
        - header: Model Name
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - placeholder: Model Name
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
        - attribute: [Attr: EcoATM_EB.ReserveBid.Bid]
        - dynamicText: ${1}
        - header: Bid
        ➤ **filter** (Widgets)
          - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
              - defaultFilter: equal
              - placeholder: Bid
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
        - attribute: [Attr: EcoATM_EB.ReserveBid.LastUpdateDateTime]
        - dynamicText: {1}
        - header: Last Updated
        ➤ **filter** (Widgets)
          - 🧩 **Date filter** (ID: `com.mendix.widget.web.datagriddatefilter.DatagridDateFilter`)
              - defaultFilter: equal
        - visible: `true`
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: customContent
        - attribute: [Attr: EcoATM_EB.ReserveBid.ProductId]
        ➤ **content** (Widgets)
            ↳ [acti] → **Page**: `EcoATM_EB.ReserveBid_Audit_View`
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
    - pagingPosition: bottom
    - showPagingButtons: always
    - showEmptyPlaceholder: none
    - onClickTrigger: double
    ➤ **filtersPlaceholder** (Widgets)
        ↳ [acti] → **Microflow**: `EcoATM_EB.ACT_DownloadReserveBids`
        ↳ [acti] → **Page**: `EcoATM_EB.ReserveBid_File_Upload`
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
