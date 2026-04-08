# Page: PWS_OfferItemView

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesRep, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps

**Layout:** `AuctionUI.ecoATM_Popup_Layout_NoTitle`

## Widget Tree

  ↳ [Click] → **Cancel Changes**
- 📦 **DataView** [Context]
    ↳ [acti] → **Cancel Changes**
  - 🧩 **Image** (ID: `com.mendix.widget.web.image.Image`)
      - datasource: icon
      - onClickType: action
      - widthUnit: auto
      - width: 100
      - heightUnit: auto
      - height: 100
      - iconSize: 14
      - displayAs: fullImage
  - 🧩 **Image** [DP: {Image fit: Fill}] (ID: `com.mendix.widget.web.image.Image`)
      - datasource: icon
      - onClickType: action
      - widthUnit: auto
      - width: 100
      - heightUnit: auto
      - height: 100
      - iconSize: 14
      - displayAs: fullImage
- 📦 **DataView** [Context]
  - 🧩 **Image** [DP: {Spacing right: Inner small, Spacing left: Outer small}] 👁️ (If: `$currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept`) (ID: `com.mendix.widget.web.image.Image`)
      - datasource: image
      - onClickType: action
      - widthUnit: auto
      - width: 100
      - heightUnit: auto
      - height: 100
      - iconSize: 14
      - displayAs: fullImage
  - 🧩 **Image** [DP: {Spacing right: Inner small, Spacing left: Outer small}] 👁️ (If: `$currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Decline`) (ID: `com.mendix.widget.web.image.Image`)
      - datasource: image
      - onClickType: action
      - widthUnit: auto
      - width: 100
      - heightUnit: auto
      - height: 100
      - iconSize: 14
      - displayAs: fullImage
  - 🧩 **Image** [DP: {Spacing right: Inner small, Spacing left: Outer small}] 👁️ (If: `$currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Finalize`) (ID: `com.mendix.widget.web.image.Image`)
      - datasource: image
      - onClickType: action
      - widthUnit: auto
      - width: 100
      - heightUnit: auto
      - height: 100
      - iconSize: 14
      - displayAs: fullImage
  - 🧩 **Image** [DP: {Spacing right: Inner small, Spacing left: Outer small}] 👁️ (If: `$currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Counter`) (ID: `com.mendix.widget.web.image.Image`)
      - datasource: image
      - onClickType: action
      - widthUnit: auto
      - width: 100
      - heightUnit: auto
      - height: 100
      - iconSize: 14
      - displayAs: fullImage
  - 📝 **DropDown**: dropDown4 [Class: `no-border-dropdown`]
    ↳ [Change] → **Microflow**: `EcoATM_PWS.OCH_OfferItemDrawer_Action`
    ↳ [Change] → **Microflow**: `EcoATM_PWS.OCH_OfferItem_CalculateCountertotal`
    ↳ [Change] → **Microflow**: `EcoATM_PWS.OCH_OfferItem_CalculateCountertotal`
    ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_SaveCounterOffer_Drawer`
- 📦 **DataView** [NF: EcoATM_PWS.DS_CreateOfferDrawerHelper]
    ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_OfferDrawer_ThisSKU`
    ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_OfferDrawer_SimilarSKU`
    ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_Offers_Download_SameSKU`
    ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_Offers_Download_SimilarSKU`
  - 🧩 **Data grid 2** [Class: `pws-sidedrawer-datagrid` | DP: {Spacing top: Outer large}] 👁️ (If DataGridSource is ThisSKU/SimilarSKUs/(empty)) (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: checkbox
      - itemSelectionMode: clear
      - loadingType: skeleton
      ➤ **columns**
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_PWS.Offer.OfferID]
          ➤ **content** (Widgets)
            - 📦 **DataView** [Context]
                ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_OpenOfferDetailsPage`
          - header: Offer ID
          - visible: `true`
          - filterCaptionType: expression
          - hidable: yes
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWS.Offer.OfferSubmissionDate]
          - header: Offer Date
          - tooltip: {1}
          - visible: `true`
          - filterCaptionType: expression
          - hidable: yes
          - width: autoFit
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWS.OfferItem.OfferDrawerStatus]
          - header: Status
          - visible: `true`
          - filterCaptionType: expression
          - hidable: yes
          - width: autoFit
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `if $currentObject/OfferDrawerStatus= EcoATM_PWS.ENUM_OfferDrawerStatus.Sales_Review or $currentObject/OfferDrawerStatus= EcoATM_PWS.ENUM_OfferDrawerStatus.Accepted
or$currentObject/OfferDrawerStatus= EcoATM_PWS.ENUM_OfferDrawerStatus.Countered
then
'pws-offer-drawer-status-pending pws-datagrid-background-color'
else
'pws-datagrid-background-color'
`
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_BuyerManagement.Buyer.CompanyName]
          - header: Customer
          - visible: `true`
          - filterCaptionType: expression
          - hidable: yes
          - width: autoFit
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWS.OfferItem.OfferQuantity]
          - header: Qty
          - visible: `true`
          - filterCaptionType: expression
          - hidable: yes
          - width: autoFit
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_PWSMDM.Device.CurrentListPrice]
          - dynamicText: ${1}
          - header: List Price
          - visible: `true`
          - filterCaptionType: expression
          - hidable: yes
          - width: autoFit
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_PWS.OfferItem.OfferPrice]
          - dynamicText: ${1}
          - header: Offer Price
          - visible: `true`
          - filterCaptionType: expression
          - hidable: yes
          - width: autoFit
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
      - pageSize: 20
      - pagination: buttons
      - showPagingButtons: always
      - pagingPosition: bottom
      - loadMoreButtonCaption: Load More
      - showEmptyPlaceholder: none
      - onClickTrigger: single
      - configurationStorageType: attribute
      - exportDialogLabel: Export progress
      - cancelExportLabel: Cancel data export
      - selectRowLabel: Select row
  - 🧩 **Data grid 2** [Class: `pws-sidedrawer-datagrid` | DP: {Spacing top: Outer large}] 👁️ (If DataGridSource is ThisSKU/SimilarSKUs/(empty)) (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: checkbox
      - itemSelectionMode: clear
      - loadingType: skeleton
      ➤ **columns**
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_PWS.Offer.OfferID]
          ➤ **content** (Widgets)
            - 📦 **DataView** [Context]
                ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_OpenOfferDetailsPage`
          - header: Offer ID
          - visible: `true`
          - filterCaptionType: expression
          - hidable: yes
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWS.Offer.OfferSubmissionDate]
          - header: Offer Date
          - tooltip: {1}
          - visible: `true`
          - filterCaptionType: expression
          - hidable: yes
          - width: autoFit
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWS.OfferItem.OfferDrawerStatus]
          - header: Status
          - visible: `true`
          - filterCaptionType: expression
          - hidable: yes
          - width: autoFit
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `if $currentObject/OfferDrawerStatus= EcoATM_PWS.ENUM_OfferDrawerStatus.Sales_Review or $currentObject/OfferDrawerStatus= EcoATM_PWS.ENUM_OfferDrawerStatus.Accepted
or$currentObject/OfferDrawerStatus= EcoATM_PWS.ENUM_OfferDrawerStatus.Countered
then
'pws-offer-drawer-status-pending pws-datagrid-background-color'
else
'pws-datagrid-background-color'

`
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_BuyerManagement.Buyer.CompanyName]
          - header: Customer
          - visible: `true`
          - filterCaptionType: expression
          - hidable: yes
          - width: autoFit
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWS.OfferItem.OfferQuantity]
          - header: Qty
          - visible: `true`
          - filterCaptionType: expression
          - hidable: yes
          - width: autoFit
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_PWSMDM.Device.CurrentListPrice]
          - dynamicText: ${1}
          - header: List Price
          - visible: `true`
          - filterCaptionType: expression
          - hidable: yes
          - width: autoFit
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_PWS.OfferItem.OfferPrice]
          - dynamicText: ${1}
          - header: Offer Price
          - visible: `true`
          - filterCaptionType: expression
          - hidable: yes
          - width: autoFit
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.Color.DisplayName]
          - header: Color
          - visible: `true`
          - filterCaptionType: expression
          - hidable: yes
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
      - exportDialogLabel: Export progress
      - cancelExportLabel: Cancel data export
      - selectRowLabel: Select row
