# Page: PWSBuyerCounterOffer_Overview

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

**Layout:** `EcoATM_Direct_Theme.EcoATM_PWS_Bidder`

## Widget Tree

- 📦 **DataView** [NF: EcoATM_Direct_Theme.DS_GetBuyerCode_SessionAndTabHelper]
  - 📦 **DataView** [MF: EcoATM_PWS.DS_BuyerCodeBySession]
    - 📦 **DataView** [NF: EcoATM_PWS.SUB_GetCurrentUser]
    - 🧩 **Data grid 2** [Class: `pws-datagrid column-selector-no-styling pws-offers-datagrid pws-offer-footer ` | DP: {Spacing top: Outer large, Spacing left: Outer large, Spacing right: Outer small}] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: checkbox
        - itemSelectionMode: clear
        ➤ **columns**
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_PWS.Offer.OfferID]
            ➤ **content** (Widgets)
                ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_Offer_EditCounterOfferByBuyer`
            - header: Offer ID
            - visible: `true`
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `'pws-datagrid-background-color'
`
            - filterCaptionType: expression
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_PWS.OrderStatus.InternalStatusText]
            - header: Offer Status
            - visible: `true`
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `'pws-datagrid-background-color'`
            - filterCaptionType: expression
            - showContentAs: dynamicText
            - attribute: [Attr: EcoATM_PWS.Offer.FinalOfferTotalSKU]
            - dynamicText: {1}
            - header: SKUs
            - visible: `true`
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `'pws-datagrid-background-color'
`
            - filterCaptionType: expression
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_PWS.Offer.FinalOfferTotalQty]
            - header: Qty
            - visible: `true`
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `'pws-datagrid-background-color'
`
            - filterCaptionType: expression
            - showContentAs: dynamicText
            - attribute: [Attr: EcoATM_PWS.Offer.FinalOfferTotalPrice]
            - dynamicText: {1}
            - header: Offer Price
            - visible: `true`
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `if($currentObject/FinalOfferTotalPrice!=empty) then 
'pws-datagrid-background-color text-dollar' 
else 
'pws-datagrid-background-color' 

`
            - filterCaptionType: expression
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_PWS.Offer.OfferSubmissionDate]
            - header: Offer Date
            - visible: `true`
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `'pws-datagrid-background-color'
`
            - filterCaptionType: expression
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_PWS.Offer.UpdateDate]
            - header: Last Updated
            - visible: `true`
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `'pws-datagrid-background-color'`
            - filterCaptionType: expression
        - pageSize: 20
        - pagination: virtualScrolling
        - pagingPosition: bottom
        - showPagingButtons: always
        - loadMoreButtonCaption: Load More
        - showEmptyPlaceholder: custom
        ➤ **emptyPlaceholder** (Widgets)
          - 🧩 **HTML Element** (ID: `com.mendix.widget.web.htmlelement.HTMLElement`)
              - tagName: span
              - tagNameCustom: div
              - tagContentMode: innerHTML
              - tagContentHTML: <body class="body-container"> <div class="message-container"> <h1>There are currently no counter offers</h1> </div> </body>
        - onClickTrigger: single
        - configurationStorageType: attribute
        - exportDialogLabel: Export progress
        - cancelExportLabel: Cancel data export
        - selectRowLabel: Select row
        - loadingType: spinner
- 📦 **DataView** [NF: EcoATM_Direct_Theme.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
