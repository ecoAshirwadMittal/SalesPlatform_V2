# Page: PriceHistory_Overview

**Allowed Roles:** EcoATM_PWSMDM.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

  ↳ [acti] → **Nanoflow**: `EcoATM_PWSMDM.NAN_MoveToPreivousPage`
- 📦 **DataView** [Context]
  - 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: rowClick
      - itemSelectionMode: clear
      - loadingType: spinner
      ➤ **columns**
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.PriceHistory.ListPrice]
          - header: List price
          ➤ **filter** (Widgets)
            - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                - defaultFilter: equal
                - delay: 500
                - screenReaderInputCaption: Search
          - visible: `true`
          - filterCaptionType: expression
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: right
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.PriceHistory.MinPrice]
          - header: Min price
          ➤ **filter** (Widgets)
            - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                - defaultFilter: equal
                - delay: 500
                - screenReaderInputCaption: Search
          - visible: `true`
          - filterCaptionType: expression
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: right
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.PriceHistory.ExpirationDate]
          - header: Expiration date
          ➤ **filter** (Widgets)
            - 🧩 **Date filter** (ID: `com.mendix.widget.web.datagriddatefilter.DatagridDateFilter`)
                - defaultFilter: equal
          - visible: `true`
          - filterCaptionType: expression
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_PWSMDM.PriceHistory.ListPrice]
          ➤ **content** (Widgets)
              ↳ [acti] → **Page**: `EcoATM_PWSMDM.PriceHistory_NewEdit`
              ↳ [acti] → **Microflow**: `EcoATM_PWSMDM.ACT_PriceHistory_DeleteByAdmin`
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
          ↳ [acti] → **Page**: `EcoATM_PWSMDM.PriceHistory_NewEdit`
      - exportDialogLabel: Export progress
      - cancelExportLabel: Cancel data export
      - selectRowLabel: Select row
