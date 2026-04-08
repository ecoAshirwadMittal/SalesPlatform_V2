# Page: DAHelper_Overview

**Allowed Roles:** EcoATM_DA.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: rowClick
    - itemSelectionMode: clear
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_DA.DAHelper.DisplayDA_DataGrid]
        - header: Display DA Data grid
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
        - showContentAs: customContent
        - attribute: [Attr: EcoATM_DA.DAHelper.DisplayDA_DataGrid]
        ➤ **content** (Widgets)
            ↳ [acti] → **Page**: `EcoATM_DA.DAHelper_NewEdit`
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
    - loadMoreButtonCaption: Load More
    - showEmptyPlaceholder: none
    - onClickTrigger: double
    - configurationStorageType: attribute
    ➤ **filtersPlaceholder** (Widgets)
        ↳ [acti] → **Page**: `EcoATM_DA.DAHelper_NewEdit`
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
    - loadingType: spinner
