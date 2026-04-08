# Page: PWSOffer_OfferItems_2

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

**Layout:** `EcoATM_Direct_Theme.EcoATM_PWS`

## Widget Tree

  ↳ [acti] → **Page**: `EcoATM_PWS.PWSOffers`
- 📦 **DataView** [Context]
    ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_Offer_CompleteReview`
  - 🔤 **Text**: "More Actions" [Class: `pws-usericon_settings_title`]
    ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_Offer_SalesAcceptAll`
    ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_Offer_SalesFinalizeAll`
- 🧩 **Data grid 2** [Class: `pws-datagrid column-selector-no-styling pws-offeritems-datagrid` | DP: {Spacing top: Outer large, Spacing left: Outer large, Spacing right: Outer small}] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
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
        - minWidth: minContent
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - columnClass: `'pws-datagrid-background-color'
`
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_PWSMDM.Category.Category]
        - header: Category
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
        - attribute: [Attr: EcoATM_PWSMDM.Brand.Brand]
        - header: Brand
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
        - attribute: [Attr: EcoATM_PWSMDM.Model.Model]
        - header: Model
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
        - attribute: [Attr: EcoATM_PWSMDM.Carrier.Carrier]
        - header: Carrier
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
        - attribute: [Attr: EcoATM_PWSMDM.Capacity.Capacity]
        - header: Capacity
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
        - attribute: [Attr: EcoATM_PWSMDM.Color.Color]
        - header: Color
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
        - attribute: [Attr: EcoATM_PWSMDM.Grade.Grade]
        - header: Grade
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
        - showContentAs: dynamicText
        - dynamicText: ${1}
        - header: Min Price
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
        - showContentAs: dynamicText
        - dynamicText: ${1}
        - header: List Price
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
        - showContentAs: dynamicText
        - dynamicText: ${1}
        - header: Offer Price
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
        - showContentAs: customContent
        ➤ **content** (Widgets)
        - header: vs Min%
        - visible: `true`
        - hidable: yes
        - width: autoFit
        - minWidth: minContent
        - minWidthLimit: 100
        - size: 1
        - alignment: center
        - columnClass: `'pws-datagrid-background-color'
`
        - filterCaptionType: expression
        - showContentAs: customContent
        ➤ **content** (Widgets)
        - header: vs List%
        - visible: `true`
        - hidable: yes
        - width: autoFit
        - minWidth: minContent
        - minWidthLimit: 100
        - size: 1
        - alignment: center
        - columnClass: `'pws-datagrid-background-color'
`
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_PWS.OfferItem.OfferQuantity]
        - header: Offer Qty
        - visible: `true`
        - hidable: yes
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
        - header: Offer Total
        - visible: `true`
        - hidable: yes
        - width: autoFit
        - minWidth: minContent
        - minWidthLimit: 100
        - size: 1
        - alignment: center
        - columnClass: `'pws-datagrid-background-color'
`
        - filterCaptionType: expression
        - showContentAs: customContent
        ➤ **content** (Widgets)
          - 🧩 **Image** 👁️ (If: `$currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept`) (ID: `com.mendix.widget.web.image.Image`)
              - datasource: image
              - onClickType: action
              - widthUnit: auto
              - width: 100
              - heightUnit: auto
              - height: 100
              - iconSize: 14
              - displayAs: fullImage
          - 🧩 **Image** 👁️ (If: `$currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Counter`) (ID: `com.mendix.widget.web.image.Image`)
              - datasource: image
              - onClickType: action
              - widthUnit: auto
              - width: 100
              - heightUnit: auto
              - height: 100
              - iconSize: 14
              - displayAs: fullImage
          - 🧩 **Image** 👁️ (If: `$currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Decline`) (ID: `com.mendix.widget.web.image.Image`)
              - datasource: image
              - onClickType: action
              - widthUnit: auto
              - width: 100
              - heightUnit: auto
              - height: 100
              - iconSize: 14
              - displayAs: fullImage
          - 🧩 **Image** 👁️ (If: `$currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Finalize`) (ID: `com.mendix.widget.web.image.Image`)
              - datasource: image
              - onClickType: action
              - widthUnit: auto
              - width: 100
              - heightUnit: auto
              - height: 100
              - iconSize: 14
              - displayAs: fullImage
          - 📝 **DropDown**: dropDown3 [Class: `no-border-dropdown`]
            ↳ [Change] → **Microflow**: `EcoATM_PWS.OCH_OfferItem_ActionPriceQty`
        - header: Actions
        - visible: `true`
        - hidable: yes
        - width: autoFit
        - minWidth: manual
        - minWidthLimit: 152
        - size: 1
        - alignment: center
        - columnClass: `'pws-datagrid-background-color'
`
        - filterCaptionType: expression
        - showContentAs: customContent
        ➤ **content** (Widgets)
            ↳ [Change] → **Microflow**: `EcoATM_PWS.OCH_OfferItem_ActionPriceQty`
        - header: Price
        - visible: `true`
        - hidable: yes
        - width: autoFit
        - minWidth: manual
        - minWidthLimit: 60
        - size: 1
        - alignment: center
        - columnClass: `if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept or $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Decline
then 'pws-user-cells-accept'
else
'pws-user-cells-counter'
`
        - filterCaptionType: expression
        - showContentAs: customContent
        ➤ **content** (Widgets)
            ↳ [Change] → **Microflow**: `EcoATM_PWS.OCH_OfferItem_ActionPriceQty`
        - header: Qty
        - visible: `true`
        - hidable: yes
        - width: autoFit
        - minWidth: manual
        - minWidthLimit: 60
        - size: 1
        - alignment: center
        - columnClass: `if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept or $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Decline
then 'pws-user-cells-accept'
else
'pws-user-cells-counter'
`
        - filterCaptionType: expression
        - showContentAs: customContent
        ➤ **content** (Widgets)
        - header: Total
        - visible: `true`
        - hidable: yes
        - width: autoFit
        - minWidth: manual
        - minWidthLimit: 90
        - size: 1
        - alignment: center
        - columnClass: `if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept or $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Decline
then 'pws-user-cells-accept'
else
'pws-user-cells-counter'
`
        - filterCaptionType: expression
    - pageSize: 20
    - pagination: buttons
    - pagingPosition: bottom
    - showPagingButtons: always
    - loadMoreButtonCaption: Load More
    - showEmptyPlaceholder: none
    - onClickTrigger: single
    - configurationStorageType: localStorage
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
    - loadingType: spinner
