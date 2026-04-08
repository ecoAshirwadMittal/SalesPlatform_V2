# Page: DeposcoConfig_Overview

**Allowed Roles:** EcoATM_PWSIntegration.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

  ↳ [acti] → **Microflow**: `EcoATM_PWS.SUB_LoadPWSInventory_Deposco`
  ↳ [acti] → **Microflow**: `EcoATM_PWSIntegration.ACT_FullInventorySync`
- 📦 **DataView** [MF: EcoATM_PWSIntegration.DS_FetchDespocoConfiguration]
  - 📝 **DatePicker**: datePicker1
    ↳ [acti] → **Microflow**: `EcoATM_PWSIntegration.ACT_TestDeposcoAPI`
  - 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: checkbox
      - itemSelectionMode: clear
      - loadingType: spinner
      ➤ **columns**
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSIntegration.DesposcoAPIs.ServiceUrl]
          - header: Service url
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
          - attribute: [Attr: EcoATM_PWSIntegration.DesposcoAPIs.ServiceName]
          - header: Service name
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
          - attribute: [Attr: EcoATM_PWSIntegration.DesposcoAPIs.ServiceUrl]
          ➤ **content** (Widgets)
              ↳ [acti] → **Page**: `EcoATM_PWSIntegration.DesposcoAPIs_View`
              ↳ [acti] → **Delete**
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
          ↳ [acti] → **Nanoflow**: `EcoATM_PWSIntegration.NF_NewDeposcoAPI`
      - exportDialogLabel: Export progress
      - cancelExportLabel: Cancel data export
      - selectRowLabel: Select row
    ↳ [acti] → **Save Changes**
    ↳ [acti] → **Cancel Changes**
