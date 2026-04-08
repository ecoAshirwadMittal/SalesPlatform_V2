# Page: PWSInventorySyncReport_Overview

**Allowed Roles:** EcoATM_PWS.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: rowClick
    - itemSelectionMode: clear
    - loadingType: spinner
    ➤ **columns**
        - showContentAs: dynamicText
        - attribute: [Attr: EcoATM_PWS.PWSInventorySyncReport.ReportDate]
        - dynamicText: {1}
        - header: Report date
        - visible: `true`
        - filterCaptionType: expression
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: customContent
        - attribute: [Attr: EcoATM_PWS.PWSInventorySyncReport.ReportDate]
        ➤ **content** (Widgets)
            ↳ [acti] → **Page**: `EcoATM_PWS.SKUSyncDetail_Overview`
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
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
