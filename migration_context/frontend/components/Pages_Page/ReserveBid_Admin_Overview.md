# Page: ReserveBid_Admin_Overview

**Allowed Roles:** EcoATM_EB.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: checkbox
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_EB.ReserveBid.ProductId]
        - header: Product id
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
        - attribute: [Attr: EcoATM_EB.ReserveBid.Grade]
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
        - attribute: [Attr: EcoATM_EB.ReserveBid.Bid]
        - header: Bid
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
        - attribute: [Attr: EcoATM_EB.ReserveBid.LastUpdateDateTime]
        - header: Last update date time
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
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_EB.ReserveBid.LastAwardedMinPrice]
        - header: Last awarded min price
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
        - attribute: [Attr: EcoATM_EB.ReserveBid.ProductId]
        ➤ **content** (Widgets)
            ↳ [acti] → **Page**: `EcoATM_EB.ReserveBid_Admin_NewEdit`
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
    - pagingPosition: bottom
    - showPagingButtons: always
    - showEmptyPlaceholder: none
    - onClickTrigger: double
    ➤ **filtersPlaceholder** (Widgets)
        ↳ [acti] → **Delete**
        ↳ [acti] → **Microflow**: `EcoATM_EB.ACT_DeleteAll`
        ↳ [acti] → **Page**: `EcoATM_EB.ReserveBid_Admin_NewEdit`
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
