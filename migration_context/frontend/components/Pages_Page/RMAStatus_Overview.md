# Page: RMAStatus_Overview

**Allowed Roles:** EcoATM_RMA.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: checkbox
    - itemSelectionMode: clear
    - loadingType: spinner
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_RMA.RMAStatus.SortOrder]
        - header: SortOrder
        - visible: `true`
        - filterCaptionType: expression
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_RMA.RMAStatus.SystemStatus]
        - header: System status
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
        - attribute: [Attr: EcoATM_RMA.RMAStatus.InternalStatusText]
        - header: Internal status text
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
        - attribute: [Attr: EcoATM_RMA.RMAStatus.ExternalStatusText]
        - header: External status text
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
        - attribute: [Attr: EcoATM_RMA.RMAStatus.StatusGroupedTo]
        - header: Status Group
        - visible: `true`
        - filterCaptionType: expression
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_RMA.RMAStatus.StatusVerbiageBidder]
        - header: Bidder Message
        - visible: `true`
        - filterCaptionType: expression
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_RMA.RMAStatus.Desciption]
        - header: Desciption
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
        - attribute: [Attr: EcoATM_RMA.RMAStatus.IsDefaultStatus]
        - header: Is Default
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
        - attribute: [Attr: EcoATM_RMA.RMAStatus.SystemStatus]
        ➤ **content** (Widgets)
            ↳ [acti] → **Page**: `EcoATM_RMA.RMAStatus_Edit`
            ↳ [acti] → **Delete**
        - header: Action
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
    - onClickTrigger: single
    - configurationStorageType: attribute
    ➤ **filtersPlaceholder** (Widgets)
        ↳ [acti] → **Page**: `EcoATM_RMA.RMAStatus_New`
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
