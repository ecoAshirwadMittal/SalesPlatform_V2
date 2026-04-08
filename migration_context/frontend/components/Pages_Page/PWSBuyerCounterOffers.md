# Page: PWSBuyerCounterOffers

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

**Layout:** `EcoATM_Direct_Theme.EcoATM_PWS_Bidder`

## Widget Tree

- 📦 **DataView** [Context]
  - 🧩 **HTML Element** (ID: `com.mendix.widget.web.htmlelement.HTMLElement`)
      - tagName: span
      - tagNameCustom: div
      - tagContentMode: innerHTML
      - tagContentHTML: <body class="body-container"> <div class="message-container"> <h1>There are no counter offers available for the Offer ID you have selected. Please choose another Offer and try again.</h1> </div> </body>
  - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOriginalOfferForCounterOffers]
      ↳ [Click] → **Nanoflow**: `EcoATM_PWS.ACT_ChangeOfferStatus`
  - 📦 **DataView** [MF: EcoATM_PWS.DS_GetEcoATMCounterOffers]
      ↳ [Click] → **Nanoflow**: `EcoATM_PWS.ACT_ChangeOfferStatus`
  - 📦 **DataView** [MF: EcoATM_PWS.DS_GetFinalOfferForCounterOffers]
      ↳ [Click] → **Nanoflow**: `EcoATM_PWS.ACT_ChangeOfferStatus`
    ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_Offer_BuyerSubmitCounterResponse`
  - 🔤 **Text**: "More Actions" [Class: `pws-usericon_settings_title`]
    ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_Offer_BuyerAcceptAllCounters`
    ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_Offer_BuyerCancelOffer`
  - 🧩 **Data grid 2** [Class: `pws-datagrid column-selector-no-styling pws-counteroffers-datagrid pws-counteroffersbuyer-datagrid`] 👁️ (If: `$Offer/IsFunctionalDeviceExist = true`) (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: checkbox
      - itemSelectionMode: clear
      ➤ **columns**
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.Device.SKU]
          - header: SKU
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.Brand.DisplayName]
          - header: Brand
          - visible: `true`
          - hidable: no
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
          - header: Model
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.Carrier.DisplayName]
          - header: Carrier
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.Capacity.DisplayName]
          - header: Capacity
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.Color.DisplayName]
          - header: Color
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.Grade.DisplayName]
          - header: Grade
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: center
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - dynamicText: ${1}
          - header: List Price
          - visible: `true`
          - filterCaptionType: expression
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - showContentAs: dynamicText
          - dynamicText: {1}
          - header: Offer Qty
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: center
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - dynamicText: ${1}
          - header: Offer Price
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: manual
          - minWidthLimit: 95
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - dynamicText: ${1}
          - header: Offer Total
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: manual
          - minWidthLimit: 95
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWS.OfferItem.SalesOfferItemStatus]
          - header: Status
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: customContent
          ➤ **content** (Widgets)
            - 🧩 **Image** [Class: `pws-banner-iconcolor`] 👁️ (If BuyerCounterStatus is Accept/Decline/(empty)) (ID: `com.mendix.widget.web.image.Image`)
                - datasource: icon
                - onClickType: action
                - widthUnit: auto
                - width: 100
                - heightUnit: auto
                - height: 100
                - iconSize: 16
                - displayAs: fullImage
            - 🧩 **Image** [Class: `pws-acceptoffer-icon`] 👁️ (If BuyerCounterStatus is Accept/Decline/(empty)) (ID: `com.mendix.widget.web.image.Image`)
                - datasource: icon
                - onClickType: action
                - widthUnit: auto
                - width: 100
                - heightUnit: auto
                - height: 100
                - iconSize: 16
                - displayAs: fullImage
            - 📝 **DropDown**: dropDown1 [Class: `no-border-dropdown` | Dynamic: `if toString($currentObject/BuyerCounterStatus) = 'Accept' then
'fa fa-thumbs-up text-success'
else 'fa fa-times-circle text-danger'


`]
              ↳ [Change] → **Microflow**: `EcoATM_PWS.OCh_OfferItem_RecalculateFInalOffer`
          - header: Action
          - visible: `true`
          - hidable: yes
          - width: autoFit
          - minWidth: manual
          - minWidthLimit: 152
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWS.OfferItem.CounterQuantity]
          - header: Qty
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: manual
          - minWidthLimit: 55
          - size: 1
          - alignment: center
          - columnClass: `if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept
then 'pws-user-cells-accept text-bold'
else
'pws-user-cells-counter text-bold'

`
          - filterCaptionType: expression
          - showContentAs: customContent
          ➤ **content** (Widgets)
          - header: Price
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: manual
          - minWidthLimit: 200
          - size: 1
          - alignment: center
          - columnClass: `if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept
then 'pws-user-cells-accept text-bold'
else if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Counter then
'pws-user-cells-counter text-bold'
else
'pws-user-cells-counter text-bold'
 
`
          - filterCaptionType: expression
          - showContentAs: customContent
          ➤ **content** (Widgets)
          - header: Total
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: manual
          - minWidthLimit: 85
          - size: 1
          - alignment: center
          - columnClass: `if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept
then 'pws-user-cells-accept text-bold'
else if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Counter then
'pws-user-cells-counter text-bold'
else
'pws-user-cells-counter text-bold'
 

`
          - filterCaptionType: expression
      - pageSize: 20
      - pagination: virtualScrolling
      - pagingPosition: bottom
      - showPagingButtons: always
      - loadMoreButtonCaption: Load More
      - showEmptyPlaceholder: none
      - onClickTrigger: single
      - configurationStorageType: attribute
      ➤ **filtersPlaceholder** (Widgets)
      - exportDialogLabel: Export progress
      - cancelExportLabel: Cancel data export
      - selectRowLabel: Select row
      - loadingType: spinner
  - 🧩 **Data grid 2** [Class: `pws-datagrid column-selector-no-styling pws-counteroffers-datagrid pws-counteroffersbuyer-datagrid`] 👁️ (If: `$Offer/IsCaseLotsExist = true`) (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: checkbox
      - itemSelectionMode: clear
      ➤ **columns**
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.Device.SKU]
          - header: SKU
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.Brand.DisplayName]
          - header: Brand
          - visible: `true`
          - hidable: no
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
          - header: Model
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.Carrier.DisplayName]
          - header: Carrier
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.Capacity.DisplayName]
          - header: Capacity
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.Color.DisplayName]
          - header: Color
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.CaseLot.CaseLotSize]
          - header: Case Pack Qty
          - visible: `true`
          - filterCaptionType: expression
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: center
          - columnClass: `'pws-datagrid-background-color'`
          - showContentAs: dynamicText
          - dynamicText: ${1}
          - header: Unit Price
          - visible: `true`
          - filterCaptionType: expression
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - showContentAs: dynamicText
          - dynamicText: ${1}
          - header: Case Price
          - visible: `true`
          - filterCaptionType: expression
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - showContentAs: dynamicText
          - dynamicText: {1}
          - header: Offer Qty
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: center
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - dynamicText: ${1}
          - header: Unit Offer
          - visible: `true`
          - filterCaptionType: expression
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - showContentAs: dynamicText
          - dynamicText: ${1}
          - header: Offer Total
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: manual
          - minWidthLimit: 95
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWS.OfferItem.SalesOfferItemStatus]
          - header: Status
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: customContent
          ➤ **content** (Widgets)
            - 🧩 **Image** [Class: `pws-banner-iconcolor`] 👁️ (If BuyerCounterStatus is Accept/Decline/(empty)) (ID: `com.mendix.widget.web.image.Image`)
                - datasource: icon
                - onClickType: action
                - widthUnit: auto
                - width: 100
                - heightUnit: auto
                - height: 100
                - iconSize: 16
                - displayAs: fullImage
            - 🧩 **Image** [Class: `pws-acceptoffer-icon`] 👁️ (If BuyerCounterStatus is Accept/Decline/(empty)) (ID: `com.mendix.widget.web.image.Image`)
                - datasource: icon
                - onClickType: action
                - widthUnit: auto
                - width: 100
                - heightUnit: auto
                - height: 100
                - iconSize: 16
                - displayAs: fullImage
            - 📝 **DropDown**: dropDown3 [Class: `no-border-dropdown` | Dynamic: `if toString($currentObject/BuyerCounterStatus) = 'Accept' then
'fa fa-thumbs-up text-success'
else 'fa fa-times-circle text-danger'


`]
              ↳ [Change] → **Microflow**: `EcoATM_PWS.OCh_OfferItem_RecalculateFInalOffer`
          - header: Action
          - visible: `true`
          - hidable: yes
          - width: autoFit
          - minWidth: manual
          - minWidthLimit: 152
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWS.OfferItem.CounterQuantity]
          - header: Qty
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: manual
          - minWidthLimit: 55
          - size: 1
          - alignment: center
          - columnClass: `if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept
then 'pws-user-cells-accept text-bold'
else
'pws-user-cells-counter text-bold'

`
          - filterCaptionType: expression
          - showContentAs: customContent
          ➤ **content** (Widgets)
          - header: Unit Price
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: manual
          - minWidthLimit: 100
          - size: 1
          - alignment: center
          - columnClass: `if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept
then 'pws-user-cells-accept text-bold'
else if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Counter then
'pws-user-cells-counter text-bold'
else
'pws-user-cells-counter text-bold'
 
`
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - dynamicText: ${1}
          - header: Case Price
          - visible: `true`
          - filterCaptionType: expression
          - hidable: yes
          - width: autoFit
          - minWidth: manual
          - minWidthLimit: 100
          - size: 1
          - alignment: center
          - columnClass: `if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept
then 'pws-user-cells-accept text-bold'
else if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Counter then
'pws-user-cells-counter text-bold'
else
'pws-user-cells-counter text-bold'
 
`
          - showContentAs: customContent
          ➤ **content** (Widgets)
          - header: Total
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: manual
          - minWidthLimit: 85
          - size: 1
          - alignment: center
          - columnClass: `if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept
then 'pws-user-cells-accept text-bold'
else if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Counter then
'pws-user-cells-counter text-bold'
else
'pws-user-cells-counter text-bold'
 

`
          - filterCaptionType: expression
      - pageSize: 20
      - pagination: virtualScrolling
      - pagingPosition: bottom
      - showPagingButtons: always
      - loadMoreButtonCaption: Load More
      - showEmptyPlaceholder: none
      - onClickTrigger: single
      - configurationStorageType: attribute
      ➤ **filtersPlaceholder** (Widgets)
      - exportDialogLabel: Export progress
      - cancelExportLabel: Cancel data export
      - selectRowLabel: Select row
      - loadingType: spinner
  - 🧩 **Data grid 2** [Class: `pws-datagrid column-selector-no-styling pws-counteroffers-datagrid pws-counteroffersbuyer-datagrid`] 👁️ (If: `$Offer/IsUntestedDeviceExist = true`) (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: checkbox
      - itemSelectionMode: clear
      ➤ **columns**
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.Device.SKU]
          - header: SKU
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.Brand.DisplayName]
          - header: Brand
          - visible: `true`
          - hidable: no
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
          - header: Model
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.Carrier.DisplayName]
          - header: Carrier
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.Capacity.DisplayName]
          - header: Capacity
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.Color.DisplayName]
          - header: Color
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.Grade.DisplayName]
          - header: Grade
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: center
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - dynamicText: ${1}
          - header: List Price
          - visible: `true`
          - filterCaptionType: expression
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - showContentAs: dynamicText
          - dynamicText: {1}
          - header: Offer Qty
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: center
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - dynamicText: ${1}
          - header: Offer Price
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: manual
          - minWidthLimit: 95
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - dynamicText: ${1}
          - header: Offer Total
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: manual
          - minWidthLimit: 95
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWS.OfferItem.SalesOfferItemStatus]
          - header: Status
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'
`
          - filterCaptionType: expression
          - showContentAs: customContent
          ➤ **content** (Widgets)
            - 🧩 **Image** [Class: `pws-banner-iconcolor`] 👁️ (If BuyerCounterStatus is Accept/Decline/(empty)) (ID: `com.mendix.widget.web.image.Image`)
                - datasource: icon
                - onClickType: action
                - widthUnit: auto
                - width: 100
                - heightUnit: auto
                - height: 100
                - iconSize: 16
                - displayAs: fullImage
            - 🧩 **Image** [Class: `pws-acceptoffer-icon`] 👁️ (If BuyerCounterStatus is Accept/Decline/(empty)) (ID: `com.mendix.widget.web.image.Image`)
                - datasource: icon
                - onClickType: action
                - widthUnit: auto
                - width: 100
                - heightUnit: auto
                - height: 100
                - iconSize: 16
                - displayAs: fullImage
            - 📝 **DropDown**: dropDown2 [Class: `no-border-dropdown` | Dynamic: `if toString($currentObject/BuyerCounterStatus) = 'Accept' then
'fa fa-thumbs-up text-success'
else 'fa fa-times-circle text-danger'


`]
              ↳ [Change] → **Microflow**: `EcoATM_PWS.OCh_OfferItem_RecalculateFInalOffer`
          - header: Action
          - visible: `true`
          - hidable: yes
          - width: autoFit
          - minWidth: manual
          - minWidthLimit: 152
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWS.OfferItem.CounterQuantity]
          - header: Qty
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: manual
          - minWidthLimit: 55
          - size: 1
          - alignment: center
          - columnClass: `if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept
then 'pws-user-cells-accept text-bold'
else
'pws-user-cells-counter text-bold'

`
          - filterCaptionType: expression
          - showContentAs: customContent
          ➤ **content** (Widgets)
          - header: Price
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: manual
          - minWidthLimit: 200
          - size: 1
          - alignment: center
          - columnClass: `if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept
then 'pws-user-cells-accept text-bold'
else if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Counter then
'pws-user-cells-counter text-bold'
else
'pws-user-cells-counter text-bold'
 
`
          - filterCaptionType: expression
          - showContentAs: customContent
          ➤ **content** (Widgets)
          - header: Total
          - visible: `true`
          - hidable: no
          - width: autoFit
          - minWidth: manual
          - minWidthLimit: 85
          - size: 1
          - alignment: center
          - columnClass: `if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept
then 'pws-user-cells-accept text-bold'
else if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Counter then
'pws-user-cells-counter text-bold'
else
'pws-user-cells-counter text-bold'
 

`
          - filterCaptionType: expression
      - pageSize: 20
      - pagination: virtualScrolling
      - pagingPosition: bottom
      - showPagingButtons: always
      - loadMoreButtonCaption: Load More
      - showEmptyPlaceholder: none
      - onClickTrigger: single
      - configurationStorageType: attribute
      ➤ **filtersPlaceholder** (Widgets)
      - exportDialogLabel: Export progress
      - cancelExportLabel: Cancel data export
      - selectRowLabel: Select row
      - loadingType: spinner
- 📦 **DataView** [NF: EcoATM_Direct_Theme.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
