# Page: PWS_MyOffer

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep, EcoATM_PWS.SalesLeader

**Layout:** `EcoATM_Direct_Theme.EcoATM_PWS_Bidder`

## Widget Tree

- 📦 **DataView** [NF: EcoATM_Direct_Theme.DS_GetBuyerCode_SessionAndTabHelper]
  - 📦 **DataView** [MF: EcoATM_PWS.DS_BuyerCodeBySession]
      ↳ [acti] → **Page**: `EcoATM_PWS.PWSOrder_PE`
    - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOrCreatePWSOrderMDMHelper]
      - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOffer_InProgress]
        - 📦 **DataView** [MF: EcoATM_PWS.SUB_FetchPWSSearchObject] [Class: `pws-offer`]
          - 🖼️ **Image**: icon [DP: {Spacing right: Outer medium}]
            ↳ [Click] → **Nanoflow**: `EcoATM_PWS.ACT_SubmitCart`
          - 🔤 **Text**: "More Actions" [Class: `pws-usericon_settings_title`]
            ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_Offer_SubmitOrder`
            ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_OrderDataExport_ExportExcel`
            ↳ [acti] → **Page**: `EcoATM_PWS.PWS_ResetConfirmation`
          - 🧩 **Data grid 2** [Class: `pws-datagrid pws-my-offer-datagrid pws-myoffer-devices` | Dynamic: `if($dataView2/OfferQuantity=0)
then 'pws-my-offer-footer'
else ''
` | DP: {Spacing top: Inner large, Spacing left: Inner large, Spacing right: Inner large, Spacing bottom: Inner large}] 👁️ (If IsFunctionalDevicesExist is true/false) (ID: `com.mendix.widget.web.datagrid.Datagrid`)
              - refreshInterval: 0
              - itemSelectionMethod: checkbox
              - itemSelectionMode: clear
              ➤ **columns**
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.Device.SKU]
                  - header: SKU
                  - visible: `true`
                  - hidable: yes
                  - width: autoFit
                  - minWidth: manual
                  - minWidthLimit: 160
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'
`
                  - filterCaptionType: expression
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.Brand.DisplayName]
                  - header: Brand
                  - visible: `true`
                  - hidable: yes
                  - width: manual
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.Model.DisplayName]
                  - header: Model
                  - tooltip: {1}
                  - visible: `true`
                  - hidable: yes
                  - width: manual
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.Carrier.DisplayName]
                  - header: Carrier
                  - visible: `true`
                  - hidable: yes
                  - width: manual
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.Capacity.DisplayName]
                  - header: Capacity
                  - visible: `true`
                  - hidable: yes
                  - width: autoFit
                  - minWidth: manual
                  - minWidthLimit: 90
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.Color.DisplayName]
                  - header: Color
                  - tooltip: {1}
                  - visible: `true`
                  - hidable: yes
                  - width: manual
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.Grade.DisplayName]
                  - header: Grade
                  - visible: `true`
                  - hidable: yes
                  - width: manual
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 2
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: dynamicText
                  - attribute: [Attr: EcoATM_PWSMDM.Device.ATPQty]
                  - dynamicText: {1}
                  - header: Avl. Qty
                  - visible: `true`
                  - hidable: yes
                  - width: autoFit
                  - minWidth: manual
                  - minWidthLimit: 90
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: dynamicText
                  - attribute: [Attr: EcoATM_PWSMDM.Device.CurrentListPrice]
                  - dynamicText: ${1}
                  - header: Unit Price
                  - visible: `true`
                  - hidable: yes
                  - width: manual
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 2
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: customContent
                  - attribute: [Attr: EcoATM_PWS.BuyerOfferItem.OfferPrice]
                  ➤ **content** (Widgets)
                    - 📦 **DataView** [Context] [Style: `width:75px !important;`]
                        ↳ [Change] → **Nanoflow**: `EcoATM_PWS.OCH_Cart_OfferItem`
                  - header: Offer Price
                  - visible: `true`
                  - hidable: yes
                  - width: autoFit
                  - minWidth: manual
                  - minWidthLimit: 95
                  - size: 1
                  - alignment: left
                  - columnClass: `'user-data-cells pws-no-bg-color'
`
                  - filterCaptionType: expression
                  - showContentAs: customContent
                  - attribute: [Attr: EcoATM_PWS.BuyerOfferItem.Quantity]
                  ➤ **content** (Widgets)
                    - 📦 **DataView** [Context] [Style: `width:75px !important;`]
                        ↳ [Change] → **Nanoflow**: `EcoATM_PWS.OCH_Cart_OfferItem`
                  - header: Qty
                  - visible: `true`
                  - hidable: yes
                  - width: autoFit
                  - minWidth: manual
                  - minWidthLimit: 95
                  - size: 1
                  - alignment: left
                  - columnClass: `'user-data-cells pws-no-bg-color'`
                  - filterCaptionType: expression
                  - showContentAs: customContent
                  - attribute: [Attr: EcoATM_PWS.BuyerOfferItem.TotalPrice]
                  ➤ **content** (Widgets)
                  - header: Total
                  - visible: `true`
                  - hidable: yes
                  - width: autoFit
                  - minWidth: manual
                  - minWidthLimit: 95
                  - size: 1
                  - alignment: left
                  - columnClass: `'user-data-cells pws-no-bg-color'
`
                  - filterCaptionType: expression
                  - showContentAs: customContent
                  - attribute: [Attr: EcoATM_PWS.BuyerOfferItem.Quantity]
                  ➤ **content** (Widgets)
                      ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_RemoveBuyerOfferItem`
                  - visible: `true`
                  - filterCaptionType: expression
                  - hidable: yes
                  - width: autoFit
                  - minWidth: minContent
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-my-offer-removeofferItem'`
              - pageSize: 20
              - pagination: virtualScrolling
              - pagingPosition: bottom
              - showPagingButtons: always
              - loadMoreButtonCaption: Load More
              - showEmptyPlaceholder: none
              - onClickTrigger: single
              - configurationStorageType: attribute
              - configurationAttribute: [Attr: EcoATM_PWS.PWSUserPersonalization.DataGrid2Personalization]
              ➤ **filtersPlaceholder** (Widgets)
              - exportDialogLabel: Export progress
              - cancelExportLabel: Cancel data export
              - selectRowLabel: Select row
              - loadingType: spinner
          - 🧩 **Data grid 2** [Class: `pws-datagrid pws-my-offer-datagrid pws-myoffer-case-lots` | Dynamic: `if $dataView2/OfferQuantity = 0
then
'pws-cart-visibility'
else
''` | DP: {Spacing top: Inner large, Spacing left: Inner large, Spacing right: Inner large, Spacing bottom: Inner large}] 👁️ (If IsCaseLotsExist is true/false) (ID: `com.mendix.widget.web.datagrid.Datagrid`)
              - refreshInterval: 0
              - itemSelectionMethod: checkbox
              - itemSelectionMode: clear
              ➤ **columns**
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.Device.SKU]
                  - header: SKU
                  - visible: `true`
                  - hidable: yes
                  - width: autoFit
                  - minWidth: manual
                  - minWidthLimit: 160
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'
`
                  - filterCaptionType: expression
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.Brand.DisplayName]
                  - header: Brand
                  - visible: `true`
                  - hidable: yes
                  - width: manual
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.Model.DisplayName]
                  - header: Model
                  - tooltip: {1}
                  - visible: `true`
                  - hidable: yes
                  - width: manual
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.Carrier.DisplayName]
                  - header: Carrier
                  - visible: `true`
                  - hidable: yes
                  - width: manual
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.Capacity.DisplayName]
                  - header: Capacity
                  - visible: `true`
                  - hidable: yes
                  - width: autoFit
                  - minWidth: manual
                  - minWidthLimit: 90
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.Color.DisplayName]
                  - header: Color
                  - tooltip: {1}
                  - visible: `true`
                  - hidable: yes
                  - width: manual
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.Grade.DisplayName]
                  - header: Grade
                  - visible: `true`
                  - hidable: yes
                  - width: manual
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.CaseLot.CaseLotSize]
                  - header: Case Pack Qty
                  - visible: `true`
                  - filterCaptionType: expression
                  - hidable: yes
                  - width: manual
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.CaseLot.CaseLotATPQty]
                  - header: Avl. Qty
                  - visible: `true`
                  - hidable: yes
                  - width: autoFit
                  - minWidth: manual
                  - minWidthLimit: 90
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: dynamicText
                  - attribute: [Attr: EcoATM_PWSMDM.Device.CurrentListPrice]
                  - dynamicText: ${1}
                  - header: Unit Price
                  - visible: `true`
                  - filterCaptionType: expression
                  - hidable: yes
                  - width: manual
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - showContentAs: dynamicText
                  - attribute: [Attr: EcoATM_PWSMDM.CaseLot.CaseLotPrice]
                  - dynamicText: ${1}
                  - header: Case Price
                  - tooltip: ${1}
                  - visible: `true`
                  - hidable: yes
                  - width: manual
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: customContent
                  - attribute: [Attr: EcoATM_PWS.BuyerOfferItem.OfferPrice]
                  ➤ **content** (Widgets)
                    - 📦 **DataView** [Context] [Style: `width:75px !important;`]
                      - 📦 **DataView** [Context]
                          ↳ [Change] → **Nanoflow**: `EcoATM_PWS.OCH_Cart_OfferItem`
                  - header: Offer Price
                  - visible: `true`
                  - hidable: yes
                  - width: autoFit
                  - minWidth: manual
                  - minWidthLimit: 95
                  - size: 1
                  - alignment: left
                  - columnClass: `'user-data-cells pws-no-bg-color'
`
                  - filterCaptionType: expression
                  - showContentAs: customContent
                  - attribute: [Attr: EcoATM_PWS.BuyerOfferItem.Quantity]
                  ➤ **content** (Widgets)
                    - 📦 **DataView** [Context] [Style: `width:75px !important;`]
                      - 📦 **DataView** [Context]
                          ↳ [Change] → **Nanoflow**: `EcoATM_PWS.OCH_Cart_OfferItem`
                  - header: Qty
                  - visible: `true`
                  - hidable: yes
                  - width: autoFit
                  - minWidth: manual
                  - minWidthLimit: 95
                  - size: 1
                  - alignment: left
                  - columnClass: `'user-data-cells pws-no-bg-color'`
                  - filterCaptionType: expression
                  - showContentAs: customContent
                  - attribute: [Attr: EcoATM_PWS.BuyerOfferItem.TotalPrice]
                  ➤ **content** (Widgets)
                  - header: Total
                  - visible: `true`
                  - hidable: yes
                  - width: autoFit
                  - minWidth: manual
                  - minWidthLimit: 95
                  - size: 1
                  - alignment: left
                  - columnClass: `'user-data-cells pws-no-bg-color'
`
                  - filterCaptionType: expression
                  - showContentAs: customContent
                  - attribute: [Attr: EcoATM_PWS.BuyerOfferItem.Quantity]
                  ➤ **content** (Widgets)
                      ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_RemoveBuyerOfferItem`
                  - visible: `true`
                  - filterCaptionType: expression
                  - hidable: yes
                  - width: autoFit
                  - minWidth: minContent
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-my-offer-removeofferItem'`
              - pageSize: 20
              - pagination: virtualScrolling
              - pagingPosition: bottom
              - showPagingButtons: always
              - loadMoreButtonCaption: Load More
              - showEmptyPlaceholder: none
              - rowClass: `if $currentObject/Quantity = empty
then 
'pws-cart-visibility'
else
''
`
              - onClickTrigger: single
              - configurationStorageType: attribute
              - configurationAttribute: [Attr: EcoATM_PWS.PWSUserPersonalization.DataGrid2Personalization]
              ➤ **filtersPlaceholder** (Widgets)
              - exportDialogLabel: Export progress
              - cancelExportLabel: Cancel data export
              - selectRowLabel: Select row
              - loadingType: spinner
          - 🧩 **Data grid 2** [Class: `pws-datagrid pws-my-offer-datagrid pws-myoffer-devices` | Dynamic: `if($dataView2/OfferQuantity=0)
then 'pws-my-offer-footer'
else ''
` | DP: {Spacing top: Inner large, Spacing left: Inner large, Spacing right: Inner large, Spacing bottom: Inner large}] 👁️ (If IsUntestedDeviceExist is true/false) (ID: `com.mendix.widget.web.datagrid.Datagrid`)
              - refreshInterval: 0
              - itemSelectionMethod: checkbox
              - itemSelectionMode: clear
              ➤ **columns**
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.Device.SKU]
                  - header: SKU
                  - visible: `true`
                  - hidable: yes
                  - width: autoFit
                  - minWidth: manual
                  - minWidthLimit: 160
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'
`
                  - filterCaptionType: expression
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.Brand.DisplayName]
                  - header: Brand
                  - visible: `true`
                  - hidable: yes
                  - width: manual
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.Model.DisplayName]
                  - header: Model
                  - tooltip: {1}
                  - visible: `true`
                  - hidable: yes
                  - width: manual
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.Carrier.DisplayName]
                  - header: Carrier
                  - visible: `true`
                  - hidable: yes
                  - width: manual
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.Capacity.DisplayName]
                  - header: Capacity
                  - visible: `true`
                  - hidable: yes
                  - width: autoFit
                  - minWidth: manual
                  - minWidthLimit: 90
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.Color.DisplayName]
                  - header: Color
                  - tooltip: {1}
                  - visible: `true`
                  - hidable: yes
                  - width: manual
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: attribute
                  - attribute: [Attr: EcoATM_PWSMDM.Grade.DisplayName]
                  - header: Grade
                  - visible: `true`
                  - hidable: yes
                  - width: manual
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 2
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: dynamicText
                  - attribute: [Attr: EcoATM_PWSMDM.Device.ATPQty]
                  - dynamicText: {1}
                  - header: Avl. Qty
                  - visible: `true`
                  - hidable: yes
                  - width: autoFit
                  - minWidth: manual
                  - minWidthLimit: 90
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: dynamicText
                  - attribute: [Attr: EcoATM_PWSMDM.Device.CurrentListPrice]
                  - dynamicText: ${1}
                  - header: Unit Price
                  - visible: `true`
                  - hidable: yes
                  - width: manual
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 2
                  - alignment: left
                  - columnClass: `'pws-myoffer-datagrid-background-color'`
                  - filterCaptionType: expression
                  - showContentAs: customContent
                  - attribute: [Attr: EcoATM_PWS.BuyerOfferItem.OfferPrice]
                  ➤ **content** (Widgets)
                    - 📦 **DataView** [Context] [Style: `width:75px !important;`]
                        ↳ [Change] → **Nanoflow**: `EcoATM_PWS.OCH_Cart_OfferItem`
                  - header: Offer Price
                  - visible: `true`
                  - hidable: yes
                  - width: autoFit
                  - minWidth: manual
                  - minWidthLimit: 95
                  - size: 1
                  - alignment: left
                  - columnClass: `'user-data-cells pws-no-bg-color'
`
                  - filterCaptionType: expression
                  - showContentAs: customContent
                  - attribute: [Attr: EcoATM_PWS.BuyerOfferItem.Quantity]
                  ➤ **content** (Widgets)
                    - 📦 **DataView** [Context] [Style: `width:75px !important;`]
                        ↳ [Change] → **Nanoflow**: `EcoATM_PWS.OCH_Cart_OfferItem`
                  - header: Qty
                  - visible: `true`
                  - hidable: yes
                  - width: autoFit
                  - minWidth: manual
                  - minWidthLimit: 95
                  - size: 1
                  - alignment: left
                  - columnClass: `'user-data-cells pws-no-bg-color'`
                  - filterCaptionType: expression
                  - showContentAs: customContent
                  - attribute: [Attr: EcoATM_PWS.BuyerOfferItem.TotalPrice]
                  ➤ **content** (Widgets)
                  - header: Total
                  - visible: `true`
                  - hidable: yes
                  - width: autoFit
                  - minWidth: manual
                  - minWidthLimit: 95
                  - size: 1
                  - alignment: left
                  - columnClass: `'user-data-cells pws-no-bg-color'
`
                  - filterCaptionType: expression
                  - showContentAs: customContent
                  - attribute: [Attr: EcoATM_PWS.BuyerOfferItem.Quantity]
                  ➤ **content** (Widgets)
                      ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_RemoveBuyerOfferItem`
                  - visible: `true`
                  - filterCaptionType: expression
                  - hidable: yes
                  - width: autoFit
                  - minWidth: minContent
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - columnClass: `'pws-my-offer-removeofferItem'`
              - pageSize: 20
              - pagination: virtualScrolling
              - pagingPosition: bottom
              - showPagingButtons: always
              - loadMoreButtonCaption: Load More
              - showEmptyPlaceholder: none
              - onClickTrigger: single
              - configurationStorageType: attribute
              - configurationAttribute: [Attr: EcoATM_PWS.PWSUserPersonalization.DataGrid2Personalization]
              ➤ **filtersPlaceholder** (Widgets)
              - exportDialogLabel: Export progress
              - cancelExportLabel: Cancel data export
              - selectRowLabel: Select row
              - loadingType: spinner
          - 🧩 **HTML Element** [Class: `pws-emptygrid-message`] (ID: `com.mendix.widget.web.htmlelement.HTMLElement`)
              - tagName: span
              - tagNameCustom: div
              - tagContentMode: innerHTML
              - tagContentHTML: <body class="body-container"> <div class="message-container"> <h1>There are no items in your cart</h1> <p>Return to the Inventory page and enter a quantity to add items to your offer.</p> </div> </body>
- 📦 **DataView** [NF: EcoATM_Direct_Theme.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
