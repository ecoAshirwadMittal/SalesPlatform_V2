# Page: PWSOrder_PE

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep, EcoATM_PWS.SalesLeader

**Layout:** `EcoATM_Direct_Theme.EcoATM_PWS_Bidder`

## Widget Tree

- 📦 **DataView** [NF: EcoATM_Direct_Theme.DS_GetBuyerCode_SessionAndTabHelper]
  - 📦 **DataView** [MF: EcoATM_PWS.DS_BuyerCodeBySession]
    - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOrCreatePWSOrderMDMHelper]
      - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOffer_InProgress]
        - 📦 **DataView** [MF: EcoATM_PWS.SUB_FetchPWSSearchObject]
            ↳ [EnterKeyPress] → **Microflow**: `EcoATM_PWS.OCE_TriggerGridSearch`
          - 🖼️ **Image**: pws_search [DP: {Spacing left: Outer small}]
            ↳ [Click] → **Page**: `EcoATM_PWS.PWS_MyOffer`
            - 🧩 **Image** (ID: `com.mendix.widget.web.image.Image`)
                - datasource: icon
                - onClickType: action
                - widthUnit: auto
                - width: 100
                - heightUnit: auto
                - height: 100
                - iconSize: 14
                - displayAs: fullImage
          - 🔤 **Text**: "More Actions" [Class: `pws-usericon_settings_title`]
            ↳ [acti] → **Page**: `EcoATM_PWS.PWS_ResetConfirmation`
            ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_Buyer_ChooseUploadOfferExcelFile`
            ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_OrderDataExport_ExportExcel`
            ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_Listing_Download`
            ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_AllListing_ExportExcel`
          - 📑 **TabContainer** [Class: `inventory-container` | DP: {Style: Lined}]
            - 📑 **Tab**: "Functional Devices"
              - 🧩 **Data grid 2** [Class: `pws-datagrid column-selector-no-styling pws-order-datagrid` | DP: {Spacing top: Outer medium, Spacing left: Outer large}] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
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
                      - attribute: [Attr: EcoATM_PWSMDM.Device.ATPQty]
                      - dynamicText: {1}
                      - header: Avl. Qty
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
                      - attribute: [Attr: EcoATM_PWSMDM.Device.CurrentListPrice]
                      - dynamicText: ${1}
                      - header: Price
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
                      - showContentAs: customContent
                      - attribute: [Attr: EcoATM_PWSMDM.Device.AvailableQty]
                      ➤ **content** (Widgets)
                        - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOrCreateOrderItem] 👁️ (If: `$dataView9 != empty`)
                            ↳ [Change] → **Nanoflow**: `EcoATM_PWS.OCH_OfferItem`
                      - header: Offer Price
                      - visible: `true`
                      - hidable: yes
                      - width: autoFit
                      - minWidth: manual
                      - minWidthLimit: 120
                      - size: 1
                      - alignment: left
                      - columnClass: `'user-data-cells text-bold  pws-no-bg-color'`
                      - filterCaptionType: expression
                      - showContentAs: customContent
                      - attribute: [Attr: EcoATM_PWSMDM.Device.AvailableQty]
                      ➤ **content** (Widgets)
                        - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOfferItem] 👁️ (If: `$dataView9 != empty`)
                            ↳ [Change] → **Nanoflow**: `EcoATM_PWS.OCH_OfferItem`
                      - header: Cart Qty
                      - visible: `true`
                      - hidable: yes
                      - width: autoFit
                      - minWidth: manual
                      - minWidthLimit: 75
                      - size: 1
                      - alignment: left
                      - columnClass: `'user-data-cells text-bold pws-no-bg-color'
`
                      - filterCaptionType: expression
                      - showContentAs: customContent
                      - attribute: [Attr: EcoATM_PWSMDM.Device.AvailableQty]
                      ➤ **content** (Widgets)
                        - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOfferItem] [DP: {Spacing right: Outer small}]
                      - header: Total
                      - visible: `true`
                      - hidable: yes
                      - width: autoFit
                      - minWidth: manual
                      - minWidthLimit: 90
                      - size: 1
                      - alignment: center
                      - columnClass: `'user-data-cells text-bold pws-no-bg-color'
`
                      - filterCaptionType: expression
                  - pageSize: 50
                  - pagination: buttons
                  - pagingPosition: bottom
                  - showPagingButtons: always
                  - loadMoreButtonCaption: Load More
                  - showEmptyPlaceholder: none
                  - onClickTrigger: single
                  - configurationStorageType: attribute
                  - configurationAttribute: [Attr: EcoATM_PWS.PWSUserPersonalization.DataGrid2Personalization]
                  ➤ **filterList**
                      - filter: [Attr: EcoATM_PWSMDM.Device.DeviceDescription]
                  - exportDialogLabel: Export progress
                  - cancelExportLabel: Cancel data export
                  - selectRowLabel: Select row
                  - loadingType: skeleton
            - 📑 **Tab**: "Functional Case Lots" 👁️ (If: `$dataView8/EnableCaseLots`)
              - 🧩 **Image** (ID: `com.mendix.widget.web.image.Image`)
                  - datasource: icon
                  - onClickType: action
                  - widthUnit: auto
                  - width: 100
                  - heightUnit: auto
                  - height: 100
                  - iconSize: 16
                  - displayAs: fullImage
              - 🧩 **Data grid 2** [Class: `pws-datagrid column-selector-no-styling pws-order-datagrid` | Dynamic: `'pws-caselots-footer'` | DP: {Spacing top: Outer medium, Spacing left: Outer large}] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
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
                      - showContentAs: attribute
                      - attribute: [Attr: EcoATM_PWSMDM.CaseLot.CaseLotSize]
                      - header: Case Pack Qty
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
                      - attribute: [Attr: EcoATM_PWSMDM.CaseLot.CaseLotATPQty]
                      - dynamicText: {1}
                      - header: Avl. Cases
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
                      - attribute: [Attr: EcoATM_PWSMDM.Device.CurrentListPrice]
                      - dynamicText: ${1}
                      - header: Unit Price
                      ➤ **filter** (Widgets)
                        - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                            - defaultFilter: equal
                            - delay: 500
                      - visible: `true`
                      - filterCaptionType: expression
                      - hidable: yes
                      - width: autoFit
                      - minWidth: minContent
                      - minWidthLimit: 100
                      - size: 1
                      - alignment: left
                      - columnClass: `'pws-datagrid-background-color'`
                      - showContentAs: dynamicText
                      - attribute: [Attr: EcoATM_PWSMDM.CaseLot.CaseLotPrice]
                      - dynamicText: ${1}
                      - header: Case Price
                      ➤ **filter** (Widgets)
                        - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                            - defaultFilter: equal
                            - delay: 500
                      - visible: `true`
                      - filterCaptionType: expression
                      - hidable: yes
                      - width: autoFit
                      - minWidth: minContent
                      - minWidthLimit: 100
                      - size: 1
                      - alignment: left
                      - columnClass: `'pws-datagrid-background-color'`
                      - showContentAs: customContent
                      - attribute: [Attr: EcoATM_PWSMDM.CaseLot.CaseLotSize]
                      ➤ **content** (Widgets)
                        - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOfferItem_CaseLot] 👁️ (If: `$dataView9 != empty`)
                            ↳ [Change] → **Nanoflow**: `EcoATM_PWS.OCH_OfferItem`
                      - header: No. Cases
                      - visible: `true`
                      - hidable: yes
                      - width: autoFit
                      - minWidth: manual
                      - minWidthLimit: 75
                      - size: 1
                      - alignment: left
                      - columnClass: `'user-data-cells text-bold pws-no-bg-color'
`
                      - filterCaptionType: expression
                      - showContentAs: customContent
                      - attribute: [Attr: EcoATM_PWSMDM.CaseLot.CaseLotSize]
                      ➤ **content** (Widgets)
                        - 📦 **DataView** [Context]
                          - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOrCreateOrderItem_CaseLot] 👁️ (If: `$dataView9 != empty`)
                              ↳ [Change] → **Nanoflow**: `EcoATM_PWS.OCH_OfferItem`
                      - header: Unit Offer
                      - visible: `true`
                      - filterCaptionType: expression
                      - hidable: yes
                      - width: autoFit
                      - minWidth: minContent
                      - minWidthLimit: 100
                      - size: 1
                      - alignment: left
                      - columnClass: `'user-data-cells text-bold pws-no-bg-color'`
                      - showContentAs: customContent
                      - attribute: [Attr: EcoATM_PWSMDM.CaseLot.CaseLotSize]
                      ➤ **content** (Widgets)
                        - 📦 **DataView** [Context]
                          - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOfferItem_CaseLot]
                      - header: Case Offer
                      - visible: `true`
                      - filterCaptionType: expression
                      - hidable: yes
                      - width: autoFit
                      - minWidth: minContent
                      - minWidthLimit: 100
                      - size: 1
                      - alignment: left
                      - columnClass: `'user-data-cells text-bold pws-no-bg-color'`
                      - showContentAs: customContent
                      - attribute: [Attr: EcoATM_PWSMDM.CaseLot.CaseLotSize]
                      ➤ **content** (Widgets)
                        - 📦 **DataView** [Context]
                          - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOfferItem_CaseLot] [DP: {Spacing right: Outer small}]
                      - header: Total
                      - visible: `true`
                      - hidable: yes
                      - width: autoFit
                      - minWidth: manual
                      - minWidthLimit: 90
                      - size: 1
                      - alignment: center
                      - columnClass: `'user-data-cells text-bold pws-no-bg-color'
`
                      - filterCaptionType: expression
                  - pageSize: 50
                  - pagination: buttons
                  - pagingPosition: bottom
                  - showPagingButtons: always
                  - loadMoreButtonCaption: Load More
                  - showEmptyPlaceholder: custom
                  ➤ **emptyPlaceholder** (Widgets)
                    - 🧩 **HTML Element** (ID: `com.mendix.widget.web.htmlelement.HTMLElement`)
                        - tagName: span
                        - tagNameCustom: div
                        - tagContentMode: innerHTML
                        - tagContentHTML: <body class="body-container"> <div class="message-container"> <h1 style="font-weight: 500; padding-top:35px;" >There are currently no case lots to purchase</h1> </div> </body>
                  - onClickTrigger: single
                  - configurationStorageType: attribute
                  - configurationAttribute: [Attr: EcoATM_PWS.PWSUserPersonalization.DataGrid2Personalization]
                  - exportDialogLabel: Export progress
                  - cancelExportLabel: Cancel data export
                  - selectRowLabel: Select row
                  - loadingType: skeleton
            - 📑 **Tab**: "Untested Devices" 👁️ (If: `$dataView8/EnableAYYY`)
              - 🧩 **Data grid 2** [Class: `pws-datagrid column-selector-no-styling pws-order-datagrid` | DP: {Spacing top: Outer medium, Spacing left: Outer large}] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
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
                        - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                            - defaultFilter: contains
                            - delay: 500
                            - screenReaderInputCaption: Search
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
                      - attribute: [Attr: EcoATM_PWSMDM.Device.ATPQty]
                      - dynamicText: {1}
                      - header: Avl. Qty
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
                      - attribute: [Attr: EcoATM_PWSMDM.Device.CurrentListPrice]
                      - dynamicText: ${1}
                      - header: Price
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
                      - showContentAs: customContent
                      - attribute: [Attr: EcoATM_PWSMDM.Device.AvailableQty]
                      ➤ **content** (Widgets)
                        - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOrCreateOrderItem] 👁️ (If: `$dataView9 != empty`)
                            ↳ [Change] → **Nanoflow**: `EcoATM_PWS.OCH_OfferItem`
                      - header: Offer Price
                      - visible: `true`
                      - hidable: yes
                      - width: autoFit
                      - minWidth: manual
                      - minWidthLimit: 120
                      - size: 1
                      - alignment: left
                      - columnClass: `'user-data-cells text-bold  pws-no-bg-color'`
                      - filterCaptionType: expression
                      - showContentAs: customContent
                      - attribute: [Attr: EcoATM_PWSMDM.Device.AvailableQty]
                      ➤ **content** (Widgets)
                        - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOfferItem] 👁️ (If: `$dataView9 != empty`)
                            ↳ [Change] → **Nanoflow**: `EcoATM_PWS.OCH_OfferItem`
                      - header: Cart Qty
                      - visible: `true`
                      - hidable: yes
                      - width: autoFit
                      - minWidth: manual
                      - minWidthLimit: 75
                      - size: 1
                      - alignment: left
                      - columnClass: `'user-data-cells text-bold pws-no-bg-color'
`
                      - filterCaptionType: expression
                      - showContentAs: customContent
                      - attribute: [Attr: EcoATM_PWSMDM.Device.AvailableQty]
                      ➤ **content** (Widgets)
                        - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOfferItem] [DP: {Spacing right: Outer small}]
                      - header: Total
                      - visible: `true`
                      - hidable: yes
                      - width: autoFit
                      - minWidth: manual
                      - minWidthLimit: 90
                      - size: 1
                      - alignment: center
                      - columnClass: `'user-data-cells text-bold pws-no-bg-color'
`
                      - filterCaptionType: expression
                  - pageSize: 50
                  - pagination: buttons
                  - pagingPosition: bottom
                  - showPagingButtons: always
                  - loadMoreButtonCaption: Load More
                  - showEmptyPlaceholder: none
                  - onClickTrigger: single
                  - configurationStorageType: attribute
                  - configurationAttribute: [Attr: EcoATM_PWS.PWSUserPersonalization.DataGrid2Personalization]
                  ➤ **filterList**
                      - filter: [Attr: EcoATM_PWSMDM.Device.DeviceDescription]
                  - exportDialogLabel: Export progress
                  - cancelExportLabel: Cancel data export
                  - selectRowLabel: Select row
                  - loadingType: skeleton
- 📦 **DataView** [NF: EcoATM_Direct_Theme.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
