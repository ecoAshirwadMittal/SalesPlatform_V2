# Page: PWSOffer_OfferItems_ReadOnly

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

**Layout:** `EcoATM_Direct_Theme.EcoATM_PWS_Sales`

## Widget Tree

- 📦 **DataView** [Context]
    ↳ [acti] → **Microflow**: `EcoATM_PWS.SUB_ShowPWSOffersPage`
  - 📦 **DataView** [Context]
    ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_RevertOffersToSalesReview`
    ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_Offer_Download`
- 🧩 **Data grid 2** [Class: `pws-datagrid column-selector-no-styling pws-counteroffers-datagrid pws-counteroffersbuyer-datagrid` | DP: {Spacing top: Outer large, Spacing left: Outer large, Spacing right: Outer small}] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: checkbox
    - itemSelectionMode: clear
    ➤ **columns**
        - showContentAs: customContent
        - attribute: [Attr: EcoATM_PWS.OfferItem.OfferQuantity]
        ➤ **content** (Widgets)
          - 🧩 **Tooltip** [Class: `SameSKUTooltip pws-sku-tags`] 👁️ (If SameSKUOfferAvailable is true/false) (ID: `com.mendix.widget.web.tooltip.Tooltip`)
              ➤ **trigger** (Widgets)
                - 🖼️ **Image**: Tag_SameSKU_1_
              - renderMethod: text
              - textMessage: Pending Offer For Same SKU
              - tooltipPosition: top
              - arrowPosition: start
              - openOn: hover
        - header: Tag
        - visible: `true`
        - filterCaptionType: expression
        - hidable: no
        - width: autoFit
        - minWidth: manual
        - minWidthLimit: 71
        - size: 1
        - alignment: left
        - columnClass: `'pws-datagrid-background-color'
`
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
        - attribute: [Attr: EcoATM_PWSMDM.Brand.DisplayName]
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
        - attribute: [Attr: EcoATM_PWSMDM.Model.DisplayName]
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
        - attribute: [Attr: EcoATM_PWSMDM.Capacity.DisplayName]
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
        - attribute: [Attr: EcoATM_PWSMDM.Color.DisplayName]
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
        - attribute: [Attr: EcoATM_PWSMDM.Grade.DisplayName]
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
        - attribute: [Attr: EcoATM_PWS.OfferItem.MinPercentage]
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
        - attribute: [Attr: EcoATM_PWS.OfferItem.ListPercentage]
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
        - attribute: [Attr: EcoATM_PWS.OfferItem.OfferPrice]
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
        - attribute: [Attr: EcoATM_PWS.OfferItem.MinPercentage]
        ➤ **content** (Widgets)
        - header: vs Min%
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
        - attribute: [Attr: EcoATM_PWS.OfferItem.ListPercentage]
        ➤ **content** (Widgets)
        - header: vs List%
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
        - attribute: [Attr: EcoATM_PWSMDM.Device.AvailableQty]
        - dynamicText: {1}
        - header: In Stock Qty
        - visible: `true`
        - filterCaptionType: expression
        - hidable: no
        - width: autoFit
        - minWidth: minContent
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - columnClass: `'pws-datagrid-background-color'`
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_PWSMDM.Device.ATPQty]
        - header: Avl. Qty
        - visible: `true`
        - filterCaptionType: expression
        - hidable: no
        - width: autoFit
        - minWidth: minContent
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - columnClass: `'pws-datagrid-background-color'`
        - showContentAs: customContent
        - attribute: [Attr: EcoATM_PWS.OfferItem.OfferQuantity]
        ➤ **content** (Widgets)
          - 📦 **DataView** [Context]
        - header: Offer Qty
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
        - attribute: [Attr: EcoATM_PWS.OfferItem.OfferTotalPrice]
        - dynamicText: ${1}
        - header: Offer Total
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
          - 📝 **DropDown**: dropDown3 🔒 [Read-Only] [Class: `pws-offeritem-datagrid-background-color no-border-dropdown`] 👁️ (If: `1=0`)
        - header: Actions
        - visible: `true`
        - hidable: yes
        - width: autoFit
        - minWidth: manual
        - minWidthLimit: 152
        - size: 1
        - alignment: left
        - columnClass: `'pws-offeritem-datagrid-background-color pws-offer-actions-column'
`
        - filterCaptionType: expression
        - showContentAs: customContent
        ➤ **content** (Widgets)
        - header: Price
        - visible: `true`
        - hidable: yes
        - width: autoFit
        - minWidth: manual
        - minWidthLimit: 85
        - size: 1
        - alignment: left
        - columnClass: `if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept or $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Decline
then 'pws-user-cells-accept text-bold pws-offer-price-column'
else
'pws-user-cells-counter-offeritem text-bold pws-offer-price-column'
`
        - filterCaptionType: expression
        - showContentAs: customContent
        ➤ **content** (Widgets)
        - header: Qty
        - visible: `true`
        - hidable: yes
        - width: autoFit
        - minWidth: manual
        - minWidthLimit: 85
        - size: 1
        - alignment: left
        - columnClass: `if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept or $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Decline 
then 'pws-user-cells-accept text-bold pws-offer-qty-column'
else
'pws-user-cells-counter-offeritem text-bold pws-offer-qty-column'
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
        - alignment: left
        - columnClass: `if $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept or $currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Decline
then 'pws-user-cells-accept text-bold pws-offer-total-column'
else
'pws-user-cells-counter-offeritem text-bold pws-offer-total-column'
`
        - filterCaptionType: expression
    - pageSize: 50
    - pagination: virtualScrolling
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
- 📦 **DataView** [NF: EcoATM_Direct_Theme.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
