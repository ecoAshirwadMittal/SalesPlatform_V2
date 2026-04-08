# Page: PWSOffers

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

**Layout:** `EcoATM_Direct_Theme.EcoATM_PWS_Sales`

## Widget Tree

- 📦 **DataView** [MF: EcoATM_PWS.DS_CreateOffersMasterHelper]
  - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOfferSummaryByStatus]
      ↳ [Click] → **Nanoflow**: `EcoATM_PWS.ACT_ChangeOfferStatus`
  - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOfferSummaryByStatus]
      ↳ [Click] → **Nanoflow**: `EcoATM_PWS.ACT_ChangeOfferStatus`
  - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOfferSummaryOrdered]
      ↳ [Click] → **Nanoflow**: `EcoATM_PWS.ACT_ChangeOfferStatus`
  - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOfferSummaryByStatus_Decline]
      ↳ [Click] → **Nanoflow**: `EcoATM_PWS.ACT_ChangeOfferStatus`
  - 📦 **DataView** [MF: EcoATM_PWS.DS_GetOfferSummaryTotal]
      ↳ [Click] → **Nanoflow**: `EcoATM_PWS.ACT_ChangeOfferStatus`
  - 🧩 **Data grid 2** [Class: `pws-datagrid column-selector-no-styling pws-offers-datagrid` | Dynamic: `if $currentObject/StatusSelected = empty
then
'pws-selected-offerstatus-total'
else 'pws-selected-offerstatus-' + toLowerCase(getKey($currentObject/StatusSelected))` | DP: {Spacing left: Outer large, Spacing right: Outer small}] 👁️ (If StatusSelected is Sales_Review/Buyer_Acceptance/Ordered/Pending_Order/Declined/(empty)) (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: checkbox
      - itemSelectionMode: clear
      ➤ **columns**
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_PWS.Offer.OfferID]
          ➤ **content** (Widgets)
              ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_OpenPWSOfferItems`
          - header: Offer ID
          ➤ **filter** (Widgets)
            - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                - defaultFilter: contains
                - delay: 500
                - screenReaderInputCaption: Search
          - visible: `true`
          - hidable: yes
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color pws-small-text pws-text-overflowhidden'`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWS.Order.OrderNumber]
          - header: Order Number
          ➤ **filter** (Widgets)
            - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                - defaultFilter: contains
                - delay: 500
                - screenReaderInputCaption: Search
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
          ➤ **filter** (Widgets)
            - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                - defaultFilter: contains
                - delay: 500
                - screenReaderInputCaption: Search
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
          - attribute: [Attr: EcoATM_BuyerManagement.Buyer.CompanyName]
          - header: Buyer Name
          ➤ **filter** (Widgets)
            - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                - defaultFilter: contains
                - delay: 500
                - screenReaderInputCaption: Search
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_BuyerManagement.BuyerCode.Code]
          - header: Buyer Code
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
          - columnClass: `'pws-datagrid-background-color'`
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_BuyerManagement.SalesRepresentative.SalesRepFirstName]
          - dynamicText: {1} {2}
          - header: Sales Rep
          ➤ **filter** (Widgets)
            - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                - defaultFilter: contains
                - delay: 500
                - screenReaderInputCaption: Search
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
          - attribute: [Attr: EcoATM_PWS.Offer.OfferSKUCount]
          - dynamicText: {1}
          - header: SKUs
          ➤ **filter** (Widgets)
            - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                - defaultFilter: equal
                - delay: 500
                - screenReaderInputCaption: Search
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
          - attribute: [Attr: EcoATM_PWS.Offer.OfferTotalQuantity]
          - dynamicText: {1}
          - header: Qty
          ➤ **filter** (Widgets)
            - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                - defaultFilter: equal
                - delay: 500
                - screenReaderInputCaption: Search
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
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_PWS.Offer.OfferTotalPrice]
          ➤ **content** (Widgets)
          - exportValue: ${1}
          - header: Offer Price
          ➤ **filter** (Widgets)
            - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                - defaultFilter: equal
                - delay: 500
                - screenReaderInputCaption: Search
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `if(($currentObject/CounterOfferTotalPrice!=empty)
or ($currentObject/FinalOfferTotalPrice!=empty)
) then 
'pws-datagrid-background-color' 
else 
'pws-datagrid-background-color' `
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_PWS.Offer.OfferSubmissionDate]
          - dynamicText: {1}
          - header: Offer Date
          - tooltip: {1}
          ➤ **filter** (Widgets)
            - 🧩 **Date filter** (ID: `com.mendix.widget.web.datagriddatefilter.DatagridDateFilter`)
                - defaultFilter: equal
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
          - attribute: [Attr: EcoATM_PWS.Offer.UpdateDate]
          - dynamicText: {1}
          - header: Last Updated
          - tooltip: {1}
          ➤ **filter** (Widgets)
            - 🧩 **Date filter** (ID: `com.mendix.widget.web.datagriddatefilter.DatagridDateFilter`)
                - defaultFilter: equal
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
      - pageSize: 50
      - pagination: virtualScrolling
      - pagingPosition: bottom
      - showPagingButtons: always
      - loadMoreButtonCaption: Load More
      - showEmptyPlaceholder: custom
      ➤ **emptyPlaceholder** (Widgets)
      - rowClass: `'pws-offers-datagrid-hover-declined'`
      - onClickTrigger: single
      - configurationStorageType: attribute
      ➤ **filtersPlaceholder** (Widgets)
          ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_DownloadOffers`
      - exportDialogLabel: Export progress
      - cancelExportLabel: Cancel data export
      - selectRowLabel: Select row
      - loadingType: spinner
  - 📦 **DataView** [NF: EcoATM_PWS.ACT_CreateSelectionHelper]
    - 🧩 **Data grid 2** [Class: `pws-datagrid column-selector-no-styling pws-offers-datagrid` | Dynamic: `if $dataView6/StatusSelected = empty
then
'pws-selected-offerstatus-total'
else 'pws-selected-offerstatus-' + toLowerCase(getKey($dataView6/StatusSelected))
` | DP: {Spacing top: Outer large, Spacing left: Outer large, Spacing right: Outer small}] 👁️ (If StatusSelected is Sales_Review/Buyer_Acceptance/Ordered/Pending_Order/Declined/(empty)) (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: checkbox
        - itemSelectionMode: clear
        ➤ **columns**
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_PWS.Offer.OfferID]
            ➤ **content** (Widgets)
                ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_OpenPWSOfferItems`
            - header: Offer ID
            ➤ **filter** (Widgets)
              - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                  - defaultFilter: contains
                  - delay: 500
                  - screenReaderInputCaption: Search
            - visible: `true`
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `'pws-datagrid-background-color pws-small-text pws-text-overflowhidden'`
            - filterCaptionType: expression
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_PWS.Order.OrderNumber]
            - header: Order Number
            ➤ **filter** (Widgets)
              - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                  - defaultFilter: contains
                  - delay: 500
                  - screenReaderInputCaption: Search
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
            ➤ **filter** (Widgets)
              - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                  - defaultFilter: contains
                  - delay: 500
                  - screenReaderInputCaption: Search
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
            - attribute: [Attr: EcoATM_BuyerManagement.Buyer.CompanyName]
            - header: Buyer Name
            - tooltip: {1}
            ➤ **filter** (Widgets)
              - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                  - defaultFilter: contains
                  - delay: 500
                  - screenReaderInputCaption: Search
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
            - attribute: [Attr: EcoATM_BuyerManagement.BuyerCode.Code]
            - header: Buyer Code
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
            - columnClass: `'pws-datagrid-background-color'`
            - showContentAs: dynamicText
            - attribute: [Attr: EcoATM_BuyerManagement.SalesRepresentative.SalesRepFirstName]
            - dynamicText: {1} {2}
            - header: Sales Rep
            ➤ **filter** (Widgets)
              - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                  - defaultFilter: contains
                  - delay: 500
                  - screenReaderInputCaption: Search
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
            - attribute: [Attr: EcoATM_PWS.Offer.FinalOfferTotalSKU]
            - header: SKUs
            ➤ **filter** (Widgets)
              - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                  - defaultFilter: equal
                  - delay: 500
                  - screenReaderInputCaption: Search
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
            ➤ **filter** (Widgets)
              - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                  - defaultFilter: equal
                  - delay: 500
                  - screenReaderInputCaption: Search
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
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_PWS.Offer.FinalOfferTotalPrice]
            ➤ **content** (Widgets)
            - exportValue: {1}
            - header: Offer Price
            ➤ **filter** (Widgets)
              - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                  - defaultFilter: equal
                  - delay: 500
                  - screenReaderInputCaption: Search
            - visible: `true`
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `if($currentObject/FinalOfferTotalPrice!=empty) then 
'pws-datagrid-background-color' 
else 
'pws-datagrid-background-color' `
            - filterCaptionType: expression
            - showContentAs: dynamicText
            - attribute: [Attr: EcoATM_PWS.Offer.OfferSubmissionDate]
            - dynamicText: {1}
            - header: Offer Date
            ➤ **filter** (Widgets)
              - 🧩 **Date filter** (ID: `com.mendix.widget.web.datagriddatefilter.DatagridDateFilter`)
                  - defaultFilter: equal
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
            - attribute: [Attr: EcoATM_PWS.Offer.UpdateDate]
            - dynamicText: {1}
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
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_PWS.Offer.OfferBeyondSLA]
            ➤ **content** (Widgets)
              - 🧩 **Tooltip** [Class: `SameSKUTooltip pws-sku-tags`] (ID: `com.mendix.widget.web.tooltip.Tooltip`)
                  ➤ **trigger** (Widgets)
                    - 🖼️ **Image**: Tag_SameSKU_1_
                  - renderMethod: text
                  - textMessage: Pending Offer For Same SKU
                  - tooltipPosition: top
                  - arrowPosition: start
                  - openOn: hover
              - 🧩 **Tooltip** [Class: `SLATooltip pws-sku-tags`] (ID: `com.mendix.widget.web.tooltip.Tooltip`)
                  ➤ **trigger** (Widgets)
                    - 🖼️ **Image**: Tag_BeyondSLA_1_
                  - renderMethod: text
                  - textMessage: Offer In Stage Beyond SLA
                  - tooltipPosition: top
                  - arrowPosition: start
                  - openOn: hover
            - header: Tags
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
        - pageSize: 50
        - pagination: virtualScrolling
        - pagingPosition: bottom
        - showPagingButtons: always
        - loadMoreButtonCaption: Load More
        - showEmptyPlaceholder: custom
        ➤ **emptyPlaceholder** (Widgets)
        - rowClass: `'pws-offers-datagrid-hover-buyer_acceptance'`
        - onClickTrigger: single
        - configurationStorageType: attribute
        ➤ **filtersPlaceholder** (Widgets)
            ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_DownloadOffers`
        - exportDialogLabel: Export progress
        - cancelExportLabel: Cancel data export
        - selectRowLabel: Select row
        - loadingType: spinner
  - 🧩 **Data grid 2** [Class: `pws-datagrid column-selector-no-styling pws-offers-datagrid` | Dynamic: `if $currentObject/StatusSelected = empty
then
'pws-selected-offerstatus-total'
else 'pws-selected-offerstatus-' + toLowerCase(getKey($currentObject/StatusSelected))` | DP: {Spacing top: Outer large, Spacing left: Outer large, Spacing right: Outer small}] 👁️ (If StatusSelected is Sales_Review/Buyer_Acceptance/Ordered/Pending_Order/Declined/(empty)) (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: checkbox
      - itemSelectionMode: clear
      ➤ **columns**
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_PWS.Offer.OfferID]
          ➤ **content** (Widgets)
              ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_OpenPWSOfferItems`
          - header: Offer ID
          ➤ **filter** (Widgets)
            - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                - defaultFilter: contains
                - delay: 500
                - screenReaderInputCaption: Search
          - visible: `true`
          - hidable: yes
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color pws-small-text pws-text-overflowhidden'`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWS.Order.OrderNumber]
          - header: Order Number
          ➤ **filter** (Widgets)
            - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                - defaultFilter: contains
                - delay: 500
                - screenReaderInputCaption: Search
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
          ➤ **filter** (Widgets)
            - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                - defaultFilter: contains
                - delay: 500
                - screenReaderInputCaption: Search
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
          - attribute: [Attr: EcoATM_BuyerManagement.Buyer.CompanyName]
          - header: Buyer Name
          - tooltip: {1}
          ➤ **filter** (Widgets)
            - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                - defaultFilter: contains
                - delay: 500
                - screenReaderInputCaption: Search
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
          - attribute: [Attr: EcoATM_BuyerManagement.BuyerCode.Code]
          - header: Buyer Code
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
          - columnClass: `'pws-datagrid-background-color'`
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_BuyerManagement.SalesRepresentative.SalesRepFirstName]
          - dynamicText: {1} {2}
          - header: Sales Rep
          ➤ **filter** (Widgets)
            - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                - defaultFilter: contains
                - delay: 500
                - screenReaderInputCaption: Search
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
          - attribute: [Attr: EcoATM_PWS.Offer.OfferSKUCount]
          - header: SKUs
          ➤ **filter** (Widgets)
            - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                - defaultFilter: equal
                - delay: 500
                - screenReaderInputCaption: Search
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
          - attribute: [Attr: EcoATM_PWS.Offer.OfferTotalQuantity]
          - header: Qty
          ➤ **filter** (Widgets)
            - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                - defaultFilter: equal
                - delay: 500
                - screenReaderInputCaption: Search
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
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_PWS.Offer.OfferTotalPrice]
          ➤ **content** (Widgets)
          - exportValue: {1}
          - header: Offer Price
          ➤ **filter** (Widgets)
            - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                - defaultFilter: equal
                - delay: 500
                - screenReaderInputCaption: Search
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `if($currentObject/FinalOfferTotalPrice!=empty) then 
'pws-datagrid-background-color' 
else 
'pws-datagrid-background-color' `
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_PWS.Offer.OfferSubmissionDate]
          - dynamicText: {1}
          - header: Offer Date
          - tooltip: {1}
          ➤ **filter** (Widgets)
            - 🧩 **Date filter** (ID: `com.mendix.widget.web.datagriddatefilter.DatagridDateFilter`)
                - defaultFilter: equal
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
          - attribute: [Attr: EcoATM_PWS.Offer.UpdateDate]
          - dynamicText: {1}
          - header: Last Updated
          - tooltip: {1}
          ➤ **filter** (Widgets)
            - 🧩 **Date filter** (ID: `com.mendix.widget.web.datagriddatefilter.DatagridDateFilter`)
                - defaultFilter: equal
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - filterCaptionType: expression
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_PWS.Offer.OfferID]
          ➤ **content** (Widgets)
            - 🧩 **Tooltip** [Class: `SameSKUTooltip pws-sku-tags`] (ID: `com.mendix.widget.web.tooltip.Tooltip`)
                ➤ **trigger** (Widgets)
                  - 🖼️ **Image**: Tag_SameSKU_1_
                - renderMethod: text
                - textMessage: Pending Offer For Same SKU
                - tooltipPosition: top
                - arrowPosition: start
                - openOn: hover
            - 🧩 **Tooltip** [Class: `SLATooltip pws-sku-tags`] (ID: `com.mendix.widget.web.tooltip.Tooltip`)
                ➤ **trigger** (Widgets)
                  - 🖼️ **Image**: Tag_BeyondSLA_1_
                - renderMethod: text
                - textMessage: Offer In Stage Beyond SLA
                - tooltipPosition: top
                - arrowPosition: start
                - openOn: hover
          - header: Tags
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
      - pageSize: 50
      - pagination: virtualScrolling
      - pagingPosition: bottom
      - showPagingButtons: always
      - loadMoreButtonCaption: Load More
      - showEmptyPlaceholder: custom
      ➤ **emptyPlaceholder** (Widgets)
      - rowClass: `'pws-offers-datagrid-hover-sales_review'`
      - onClickTrigger: single
      - configurationStorageType: attribute
      ➤ **filtersPlaceholder** (Widgets)
          ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_DownloadOffers`
      - exportDialogLabel: Export progress
      - cancelExportLabel: Cancel data export
      - selectRowLabel: Select row
      - loadingType: spinner
  - 🧩 **Data grid 2** [Class: `pws-datagrid column-selector-no-styling pws-offers-datagrid` | Dynamic: `if $currentObject/StatusSelected = empty
then
'pws-selected-offerstatus-total'
else 'pws-selected-offerstatus-' + toLowerCase(getKey($currentObject/StatusSelected))` | DP: {Spacing top: Outer large, Spacing left: Outer large, Spacing right: Outer small}] 👁️ (If StatusSelected is Sales_Review/Buyer_Acceptance/Ordered/Pending_Order/Declined/(empty)) (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: -1
      - itemSelectionMethod: checkbox
      - itemSelectionMode: clear
      ➤ **columns**
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_PWS.Offer.OfferID]
          ➤ **content** (Widgets)
              ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_OpenPWSOfferItems`
          - header: Offer ID
          ➤ **filter** (Widgets)
            - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                - defaultFilter: contains
                - delay: 500
                - screenReaderInputCaption: Search
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color pws-small-text pws-text-overflowhidden'`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWS.Order.OrderNumber]
          - header: Order Number
          ➤ **filter** (Widgets)
            - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                - defaultFilter: contains
                - delay: 500
                - screenReaderInputCaption: Search
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
          - tooltip: {1} {2}
          ➤ **filter** (Widgets)
            - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                - defaultFilter: contains
                - delay: 500
                - screenReaderInputCaption: Search
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
          - attribute: [Attr: EcoATM_BuyerManagement.Buyer.CompanyName]
          - header: Buyer Name
          - tooltip: {1}
          ➤ **filter** (Widgets)
            - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                - defaultFilter: contains
                - delay: 500
                - screenReaderInputCaption: Search
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
          - attribute: [Attr: EcoATM_BuyerManagement.BuyerCode.Code]
          - header: Buyer Code
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
          - columnClass: `'pws-datagrid-background-color'`
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_BuyerManagement.SalesRepresentative.SalesRepFirstName]
          - dynamicText: {1} {2}
          - header: Sales Rep
          ➤ **filter** (Widgets)
            - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                - defaultFilter: contains
                - delay: 500
                - screenReaderInputCaption: Search
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
          - attribute: [Attr: EcoATM_PWS.Offer.FinalOfferTotalSKU]
          - header: SKUs
          ➤ **filter** (Widgets)
            - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                - defaultFilter: equal
                - delay: 500
                - screenReaderInputCaption: Search
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
          ➤ **filter** (Widgets)
            - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                - defaultFilter: equal
                - delay: 500
                - screenReaderInputCaption: Search
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
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_PWS.Offer.FinalOfferTotalPrice]
          ➤ **content** (Widgets)
          - exportValue: {1}
          - header: Offer Price
          ➤ **filter** (Widgets)
            - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                - defaultFilter: equal
                - delay: 500
                - screenReaderInputCaption: Search
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
          - attribute: [Attr: EcoATM_PWS.Offer.OfferSubmissionDate]
          - dynamicText: {1}
          - header: Offer Date
          - tooltip: {1}
          ➤ **filter** (Widgets)
            - 🧩 **Date filter** (ID: `com.mendix.widget.web.datagriddatefilter.DatagridDateFilter`)
                - defaultFilter: equal
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
          - attribute: [Attr: EcoATM_PWS.Offer.UpdateDate]
          - dynamicText: {1}
          - header: Last Updated
          - tooltip: {1}
          ➤ **filter** (Widgets)
            - 🧩 **Date filter** (ID: `com.mendix.widget.web.datagriddatefilter.DatagridDateFilter`)
                - defaultFilter: equal
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - filterCaptionType: expression
      - pageSize: 50
      - pagination: virtualScrolling
      - pagingPosition: bottom
      - showPagingButtons: always
      - loadMoreButtonCaption: Load More
      - showEmptyPlaceholder: custom
      ➤ **emptyPlaceholder** (Widgets)
      - rowClass: `'pws-offers-datagrid-hover-ordered'`
      - onClickTrigger: single
      - configurationStorageType: attribute
      ➤ **filtersPlaceholder** (Widgets)
          ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_DownloadOffers`
      - exportDialogLabel: Export progress
      - cancelExportLabel: Cancel data export
      - selectRowLabel: Select row
      - loadingType: spinner
  - 🧩 **Data grid 2** [Class: `pws-datagrid column-selector-no-styling pws-offers-datagrid pws-selected-offerstatus-total` | DP: {Spacing top: Outer large, Spacing left: Outer large, Spacing right: Outer small}] 👁️ (If StatusSelected is Sales_Review/Buyer_Acceptance/Ordered/Pending_Order/Declined/(empty)) (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: checkbox
      - itemSelectionMode: clear
      ➤ **columns**
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_PWS.Offer.OfferID]
          ➤ **content** (Widgets)
              ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_OpenPWSOfferItems`
          - header: Offer ID
          ➤ **filter** (Widgets)
            - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                - defaultFilter: contains
                - delay: 500
                - screenReaderInputCaption: Search
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color pws-text-overflowhidden'
`
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWS.Order.OrderNumber]
          - header: Order Number
          ➤ **filter** (Widgets)
            - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                - defaultFilter: contains
                - delay: 500
                - screenReaderInputCaption: Search
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
          ➤ **filter** (Widgets)
            - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                - defaultFilter: contains
                - delay: 500
                - screenReaderInputCaption: Search
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
          - attribute: [Attr: EcoATM_BuyerManagement.Buyer.CompanyName]
          - header: Buyer Name
          - tooltip: {1}
          ➤ **filter** (Widgets)
            - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                - defaultFilter: contains
                - delay: 500
                - screenReaderInputCaption: Search
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
          - attribute: [Attr: EcoATM_BuyerManagement.BuyerCode.Code]
          - header: Buyer Code
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
          - columnClass: `'pws-datagrid-background-color'`
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_BuyerManagement.SalesRepresentative.SalesRepFirstName]
          - dynamicText: {1} {2}
          - header: Sales Rep
          ➤ **filter** (Widgets)
            - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                - defaultFilter: contains
                - delay: 500
                - screenReaderInputCaption: Search
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
          - attribute: [Attr: EcoATM_PWS.Offer.FinalOfferTotalSKU]
          - header: SKUs
          ➤ **filter** (Widgets)
            - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                - defaultFilter: equal
                - delay: 500
                - screenReaderInputCaption: Search
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
          ➤ **filter** (Widgets)
            - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                - defaultFilter: equal
                - delay: 500
                - screenReaderInputCaption: Search
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
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_PWS.Offer.FinalOfferTotalPrice]
          ➤ **content** (Widgets)
          - exportValue: {1}
          - header: Offer Price
          ➤ **filter** (Widgets)
            - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                - defaultFilter: equal
                - delay: 500
                - screenReaderInputCaption: Search
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color' `
          - filterCaptionType: expression
          - showContentAs: dynamicText
          - attribute: [Attr: EcoATM_PWS.Offer.OfferSubmissionDate]
          - dynamicText: {1}
          - header: Offer Date
          - tooltip: {1}
          ➤ **filter** (Widgets)
            - 🧩 **Date filter** (ID: `com.mendix.widget.web.datagriddatefilter.DatagridDateFilter`)
                - defaultFilter: equal
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
          - attribute: [Attr: EcoATM_PWS.Offer.UpdateDate]
          - dynamicText: {1}
          - header: Last Updated
          - tooltip: {1}
          ➤ **filter** (Widgets)
            - 🧩 **Date filter** (ID: `com.mendix.widget.web.datagriddatefilter.DatagridDateFilter`)
                - defaultFilter: equal
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
          - filterCaptionType: expression
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_PWS.Offer.OfferID]
          ➤ **content** (Widgets)
            - 🧩 **Tooltip** [Class: `SameSKUTooltip pws-sku-tags`] (ID: `com.mendix.widget.web.tooltip.Tooltip`)
                ➤ **trigger** (Widgets)
                  - 🖼️ **Image**: Tag_SameSKU_1_
                - renderMethod: text
                - textMessage: Pending Offer For Same SKU
                - tooltipPosition: top
                - arrowPosition: start
                - openOn: hover
            - 🧩 **Tooltip** [Class: `SLATooltip pws-sku-tags`] (ID: `com.mendix.widget.web.tooltip.Tooltip`)
                ➤ **trigger** (Widgets)
                  - 🖼️ **Image**: Tag_BeyondSLA_1_
                - renderMethod: text
                - textMessage: Offer In Stage Beyond SLA
                - tooltipPosition: top
                - arrowPosition: start
                - openOn: hover
          - header: Tags
          - visible: `true`
          - filterCaptionType: expression
          - hidable: yes
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `'pws-datagrid-background-color'`
      - pageSize: 50
      - pagination: virtualScrolling
      - pagingPosition: bottom
      - showPagingButtons: always
      - loadMoreButtonCaption: Load More
      - showEmptyPlaceholder: none
      - rowClass: `'pws-offers-datagrid-hover-sales_review'`
      - onClickTrigger: single
      - configurationStorageType: attribute
      ➤ **filtersPlaceholder** (Widgets)
          ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_DownloadOffers`
      - exportDialogLabel: Export progress
      - cancelExportLabel: Cancel data export
      - selectRowLabel: Select row
      - loadingType: spinner
- 📦 **DataView** [NF: EcoATM_Direct_Theme.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
