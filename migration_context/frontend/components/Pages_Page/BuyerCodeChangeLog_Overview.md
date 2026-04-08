# Page: BuyerCodeChangeLog_Overview

**Allowed Roles:** EcoATM_BuyerManagement.Administrator

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context]
  - 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: rowClick
      - itemSelectionMode: clear
      - loadingType: spinner
      ➤ **columns**
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_BuyerManagement.BuyerCodeChangeLog.OldBuyerCodeType]
          - header: Old Value
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
          - attribute: [Attr: EcoATM_BuyerManagement.BuyerCodeChangeLog.NewBuyerCodeType]
          - header: New Value
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
          - attribute: [Attr: EcoATM_BuyerManagement.BuyerCodeChangeLog.EditedBy]
          - header: Changed By
          ➤ **filter** (Widgets)
            - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                - defaultFilter: contains
                - delay: 500
                - screenReaderInputCaption: Search
          - visible: `true`
          - filterCaptionType: expression
          - hidable: no
          - width: autoFit
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_BuyerManagement.BuyerCodeChangeLog.EditedOn]
          - header: Change Date
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
      - pageSize: 20
      - pagination: buttons
      - showPagingButtons: always
      - pagingPosition: bottom
      - loadMoreButtonCaption: Load More
      - showEmptyPlaceholder: none
      - onClickTrigger: double
      - configurationStorageType: attribute
      - exportDialogLabel: Export progress
      - cancelExportLabel: Cancel data export
      - selectRowLabel: Select row
