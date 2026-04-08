# Page: PWS_Pricing

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.SalesLeader

**Layout:** `EcoATM_Direct_Theme.EcoATM_PWS_Sales`

## Widget Tree

- 📦 **DataView** [MF: EcoATM_PWS.DS_GetOrCreateMDMFuturePriceHelper]
  - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOrCreatePWSPricingMDMHelper]
    - 📦 **DataView** [MF: EcoATM_PWS.SUB_FetchPWSSearchObject]
      - 🧩 **Data grid 2** [Class: `pws-datagrid column-selector-no-styling pws-pricing-datagrid` | DP: {Spacing top: Outer large, Spacing left: Outer large, Spacing right: Outer small}] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
          - refreshInterval: 0
          - itemSelectionMethod: checkbox
          - itemSelectionMode: clear
          ➤ **columns**
              - showContentAs: attribute
              - attribute: [Attr: EcoATM_PWSMDM.Device.SKU]
              - header: SKU
              ➤ **filter** (Widgets)
                - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                    - defaultFilter: contains
                    - delay: 500
              - visible: `true`
              - hidable: yes
              - width: autoFit
              - minWidth: minContent
              - minWidthLimit: 100
              - size: 1
              - alignment: left
              - columnClass: `'pws-datagrid-background-color'
`
              - filterCaptionType: expression
              - showContentAs: attribute
              - attribute: [Attr: EcoATM_PWSMDM.Category.DisplayName]
              - header: Category
              ➤ **filter** (Widgets)
                - 🧩 **Drop-down filter** [Class: `pws-dropdownfilter-fitwidth`] (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
                    - selectedItemsStyle: text
                    - selectionMethod: checkbox
              - visible: `true`
              - hidable: yes
              - width: autoFit
              - minWidth: manual
              - minWidthLimit: 70
              - size: 1
              - alignment: left
              - columnClass: `'pws-datagrid-background-color'
`
              - filterCaptionType: attribute
              - filterAssociationOptionLabelAttr: [Attr: EcoATM_PWSMDM.Category.DisplayName]
              - showContentAs: attribute
              - attribute: [Attr: EcoATM_PWSMDM.Brand.DisplayName]
              - header: Brand
              ➤ **filter** (Widgets)
                - 🧩 **Drop-down filter** [Class: `pws-dropdownfilter-fitwidth`] (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
                    - selectedItemsStyle: text
                    - selectionMethod: checkbox
              - visible: `true`
              - hidable: yes
              - width: autoFit
              - minWidth: manual
              - minWidthLimit: 70
              - size: 1
              - alignment: left
              - columnClass: `'pws-datagrid-background-color'
`
              - filterCaptionType: attribute
              - filterAssociationOptionLabelAttr: [Attr: EcoATM_PWSMDM.Brand.DisplayName]
              - showContentAs: attribute
              - attribute: [Attr: EcoATM_PWSMDM.Model.DisplayName]
              - header: Model Family
              ➤ **filter** (Widgets)
                - 🧩 **Drop-down filter** [Class: `pws-dropdownfilter-fitwidth`] (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
                    - selectedItemsStyle: text
                    - selectionMethod: checkbox
              - visible: `true`
              - hidable: yes
              - width: autoFit
              - minWidth: manual
              - minWidthLimit: 150
              - size: 1
              - alignment: left
              - columnClass: `'pws-datagrid-background-color'
`
              - filterCaptionType: attribute
              - filterAssociationOptionLabelAttr: [Attr: EcoATM_PWSMDM.Model.DisplayName]
              - showContentAs: attribute
              - attribute: [Attr: EcoATM_PWSMDM.Carrier.DisplayName]
              - header: Carrier
              ➤ **filter** (Widgets)
                - 🧩 **Drop-down filter** [Class: `pws-dropdownfilter-fitwidth`] (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
                    - selectedItemsStyle: text
                    - selectionMethod: checkbox
              - visible: `true`
              - hidable: yes
              - width: autoFit
              - minWidth: manual
              - minWidthLimit: 70
              - size: 1
              - alignment: left
              - columnClass: `'pws-datagrid-background-color'
`
              - filterCaptionType: attribute
              - filterAssociationOptionLabelAttr: [Attr: EcoATM_PWSMDM.Carrier.DisplayName]
              - showContentAs: attribute
              - attribute: [Attr: EcoATM_PWSMDM.Capacity.DisplayName]
              - header: Capacity
              ➤ **filter** (Widgets)
                - 🧩 **Drop-down filter** [Class: `pws-dropdownfilter-fitwidth`] (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
                    - selectedItemsStyle: text
                    - selectionMethod: checkbox
              - visible: `true`
              - hidable: yes
              - width: autoFit
              - minWidth: manual
              - minWidthLimit: 70
              - size: 1
              - alignment: left
              - columnClass: `'pws-datagrid-background-color'
`
              - filterCaptionType: attribute
              - filterAssociationOptionLabelAttr: [Attr: EcoATM_PWSMDM.Capacity.DisplayName]
              - showContentAs: attribute
              - attribute: [Attr: EcoATM_PWSMDM.Color.DisplayName]
              - header: Color
              ➤ **filter** (Widgets)
                - 🧩 **Drop-down filter** [Class: `pws-dropdownfilter-fitwidth`] (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
                    - selectedItemsStyle: text
                    - selectionMethod: checkbox
              - visible: `true`
              - hidable: yes
              - width: autoFit
              - minWidth: manual
              - minWidthLimit: 70
              - size: 1
              - alignment: left
              - columnClass: `'pws-datagrid-background-color'
`
              - filterCaptionType: attribute
              - filterAssociationOptionLabelAttr: [Attr: EcoATM_PWSMDM.Color.DisplayName]
              - showContentAs: attribute
              - attribute: [Attr: EcoATM_PWSMDM.Grade.DisplayName]
              - header: Grade
              ➤ **filter** (Widgets)
                - 🧩 **Drop-down filter** [Class: `pws-dropdownfilter-fitwidth`] (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
                    - selectedItemsStyle: text
                    - selectionMethod: checkbox
              - visible: `true`
              - hidable: yes
              - width: autoFit
              - minWidth: manual
              - minWidthLimit: 70
              - size: 1
              - alignment: left
              - columnClass: `'pws-datagrid-background-color'
`
              - filterCaptionType: attribute
              - filterAssociationOptionLabelAttr: [Attr: EcoATM_PWSMDM.Grade.DisplayName]
              - showContentAs: dynamicText
              - attribute: [Attr: EcoATM_PWSMDM.Device.CurrentListPrice]
              - dynamicText: {1}{2}
              - header: Current List Price
              ➤ **filter** (Widgets)
                - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                    - defaultFilter: equal
                    - delay: 500
              - visible: `true`
              - hidable: yes
              - width: autoFit
              - minWidth: manual
              - minWidthLimit: 70
              - size: 1
              - alignment: left
              - columnClass: `'pws-datagrid-background-color'
`
              - filterCaptionType: expression
              - showContentAs: dynamicText
              - attribute: [Attr: EcoATM_PWSMDM.Device.FutureListPrice]
              - dynamicText: {1}{2}
              - header: New List Price
              ➤ **filter** (Widgets)
                - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                    - defaultFilter: equal
                    - delay: 500
              - visible: `true`
              - hidable: yes
              - width: autoFit
              - minWidth: manual
              - minWidthLimit: 80
              - size: 1
              - alignment: left
              - columnClass: `'pws-input-cells'
`
              - filterCaptionType: expression
              - showContentAs: dynamicText
              - attribute: [Attr: EcoATM_PWSMDM.Device.CurrentMinPrice]
              - dynamicText: {1}{2}
              - header: Current Min Price
              ➤ **filter** (Widgets)
                - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                    - defaultFilter: equal
                    - delay: 500
              - visible: `true`
              - hidable: yes
              - width: autoFit
              - minWidth: manual
              - minWidthLimit: 70
              - size: 1
              - alignment: left
              - columnClass: `'pws-datagrid-background-color'
`
              - filterCaptionType: expression
              - showContentAs: dynamicText
              - attribute: [Attr: EcoATM_PWSMDM.Device.FutureMinPrice]
              - dynamicText: {1}{2}
              - header: New Min Price
              ➤ **filter** (Widgets)
                - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                    - defaultFilter: equal
                    - delay: 500
              - visible: `true`
              - hidable: yes
              - width: autoFit
              - minWidth: manual
              - minWidthLimit: 80
              - size: 1
              - alignment: left
              - columnClass: `'pws-input-cells'
`
              - filterCaptionType: expression
              - showContentAs: attribute
              - attribute: [Attr: EcoATM_PWSMDM.Note.Notes]
              - header: SKU Status
              ➤ **filter** (Widgets)
                - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                    - defaultFilter: contains
                    - delay: 500
              - visible: `true`
              - hidable: yes
              - width: autoFit
              - minWidth: manual
              - minWidthLimit: 80
              - size: 1
              - alignment: left
              - columnClass: `'pws-datagrid-background-color pws-font13'
`
              - filterCaptionType: expression
          - pageSize: 20
          - pagination: buttons
          - pagingPosition: bottom
          - showPagingButtons: always
          - loadMoreButtonCaption: Load More
          - showEmptyPlaceholder: none
          - onClickTrigger: single
          - configurationStorageType: attribute
          - configurationAttribute: [Attr: EcoATM_PWS.PWSUserPersonalization.DataGrid2Personalization]
          ➤ **filterList**
              - filter: [Attr: EcoATM_PWSMDM.Model.DisplayName]
              - filter: [Attr: EcoATM_PWSMDM.Color.DisplayName]
          ➤ **filtersPlaceholder** (Widgets)
            - 🖼️ **Image**: pws_search [DP: {Spacing left: Outer small}]
              ↳ [EnterKeyPress] → **Microflow**: `EcoATM_PWS.OCE_TriggerGridSearch`
              ↳ [Click] → **Page**: `EcoATM_PWS.Page2_UploadData`
              - 🖼️ **Image**: calendar_days
            - 🔤 **Text**: "More Actions" [Class: `pws-usericon_settings_title`]
              ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_PricingDataExport_ExportExcel`
              ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_LoadPWSInventoryData`
          - exportDialogLabel: Export progress
          - cancelExportLabel: Cancel data export
          - selectRowLabel: Select row
          - loadingType: skeleton
- 📦 **DataView** [NF: EcoATM_Direct_Theme.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
