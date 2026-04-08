# Page: Model_Overview

**Allowed Roles:** EcoATM_PWSMDM.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: rowClick
    - itemSelectionMode: clear
    - loadingType: spinner
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_PWSMDM.Model.Model]
        - header: Model
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
        - attribute: [Attr: EcoATM_PWSMDM.Model.DisplayName]
        - header: DisplayName
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
        - attribute: [Attr: EcoATM_PWSMDM.Model.IsEnabledForFilter]
        - header: Is enabled for filter
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
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_PWSMDM.Model.Rank]
        - header: Rank
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
        - showContentAs: customContent
        - attribute: [Attr: EcoATM_PWSMDM.Model.Model]
        ➤ **content** (Widgets)
            ↳ [acti] → **Page**: `EcoATM_PWSMDM.Model_NewEdit`
            ↳ [acti] → **Microflow**: `EcoATM_PWSMDM.ACT_Model_DeleteByAdmin`
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
        ↳ [acti] → **Page**: `EcoATM_PWSMDM.Model_NewEdit`
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
