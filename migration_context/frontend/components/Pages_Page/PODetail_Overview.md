# Page: PODetail_Overview

**Allowed Roles:** EcoATM_PO.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

  ↳ [acti] → **Microflow**: `EcoATM_PO.DeleteAll_PO`
- 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: rowClick
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_PO.PODetail.ProductID]
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
        - alignment: right
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_PO.PODetail.Grade]
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
        - attribute: [Attr: EcoATM_PO.PODetail.ModelName]
        - header: Model name
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
        - attribute: [Attr: EcoATM_BuyerManagement.BuyerCode.Code]
        - header: Buyer code
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
        - attribute: [Attr: EcoATM_PO.PODetail.Price]
        - header: Price
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
        - attribute: [Attr: EcoATM_PO.PODetail.QtyCap]
        - header: Qty cap
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
        - attribute: [Attr: EcoATM_PO.PODetail.PriceFulfilled]
        - header: Price fulfilled
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
        - attribute: [Attr: EcoATM_PO.PODetail.QtyFullfiled]
        - header: Qty fullfiled
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
        - attribute: [Attr: EcoATM_PO.PODetail.ProductID]
        ➤ **content** (Widgets)
            ↳ [acti] → **Page**: `EcoATM_PO.PODetail_NewEdit`
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
        ↳ [acti] → **Page**: `EcoATM_PO.PODetail_NewEdit`
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
