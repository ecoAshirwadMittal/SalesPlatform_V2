# Page: FeatureFlag_Overview

**Allowed Roles:** Eco_Core.Admin

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

  ↳ [acti] → **Page**: `AuctionUI.Business_PWS_ControlCenter`
- 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: rowClick
    - itemSelectionMode: clear
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: Eco_Core.PWSFeatureFlag.Name]
        - header: Name
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
        - showContentAs: customContent
        - attribute: [Attr: Eco_Core.PWSFeatureFlag.Active]
        ➤ **content** (Widgets)
          - 🧩 **Switch** (ID: `com.mendix.widget.custom.switch.Switch`)
              - booleanAttribute: [Attr: Eco_Core.PWSFeatureFlag.Active]
        - header: Active
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
        - showContentAs: dynamicText
        - attribute: [Attr: Eco_Core.PWSFeatureFlag.Description]
        - dynamicText: {1}
        - header: Description
        - visible: `true`
        - filterCaptionType: expression
        - hidable: yes
        - width: autoFit
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: customContent
        - attribute: [Attr: Eco_Core.PWSFeatureFlag.Name]
        ➤ **content** (Widgets)
            ↳ [acti] → **Page**: `Eco_Core.FeatureFlag_NewEdit`
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
        ↳ [acti] → **Page**: `Eco_Core.FeatureFlag_NewEdit`
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
    - loadingType: spinner
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
