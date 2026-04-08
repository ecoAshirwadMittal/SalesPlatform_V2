# Page: PWSResponseConfig_Overview

**Allowed Roles:** EcoATM_PWSIntegration.Administrator, EcoATM_PWSIntegration.SalesLeader, EcoATM_PWSIntegration.SalesOps, EcoATM_PWSIntegration.SalesRep

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

  ↳ [acti] → **Page**: `AuctionUI.Business_PWS_ControlCenter`
- 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: checkbox
    - itemSelectionMode: clear
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_PWSIntegration.PWSResponseConfig.SourceSystem]
        - header: Source system
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
        - attribute: [Attr: EcoATM_PWSIntegration.PWSResponseConfig.SourceErrorCode]
        - header: Source error code
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
        - attribute: [Attr: EcoATM_PWSIntegration.PWSResponseConfig.SourceErrorType]
        - header: Source error type
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
        - attribute: [Attr: EcoATM_PWSIntegration.PWSResponseConfig.SourceErrorMessage]
        - header: Source error message
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
        - attribute: [Attr: EcoATM_PWSIntegration.PWSResponseConfig.UserErrorCode]
        - header: User error code
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
        - attribute: [Attr: EcoATM_PWSIntegration.PWSResponseConfig.UserErrorMessage]
        - header: User error message
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
        - attribute: [Attr: EcoATM_PWSIntegration.PWSResponseConfig.ByPassForUser]
        - header: By pass for user
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
        - attribute: [Attr: EcoATM_PWSIntegration.PWSResponseConfig.SourceSystem]
        ➤ **content** (Widgets)
            ↳ [acti] → **Page**: `EcoATM_PWSIntegration.PWSResponseConfig_Edit`
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
    - onClickTrigger: single
    - configurationStorageType: attribute
    ➤ **filtersPlaceholder** (Widgets)
        ↳ [acti] → **Microflow**: `EcoATM_PWSIntegration.ACT_PWSErrorConfig_Edit`
        ↳ [acti] → **Microflow**: `EcoATM_PWSIntegration.ACT_PWSErrorConfig_Export`
        ↳ [acti] → **Microflow**: `EcoATM_PWSIntegration.ACT_PWSErrotConfig_ImportFile`
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
    - loadingType: spinner
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
