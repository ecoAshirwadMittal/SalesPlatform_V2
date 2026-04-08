# Page: RMA_RequestsOverview_Buyer

**Allowed Roles:** EcoATM_RMA.Administrator, EcoATM_RMA.Bidder, EcoATM_RMA.SalesOps

**Layout:** `EcoATM_Direct_Theme.EcoATM_PWS_Bidder`

## Widget Tree

- 📦 **DataView** [NF: EcoATM_Direct_Theme.DS_GetBuyerCode_SessionAndTabHelper]
  - 📦 **DataView** [MF: EcoATM_PWS.DS_BuyerCodeBySession]
    - 🧩 **Data grid 2** [Class: `pws-rma-returns-datagrid`] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: checkbox
        - itemSelectionMode: clear
        - loadingType: spinner
        ➤ **columns**
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_RMA.RMA.Number]
            - header: RMA Number
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_RMA.RMA.SubmittedDate]
            - header: Submit Date
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: attribute
            - attribute: [Attr: Administration.Account.FullName]
            - header: Buyer
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_BuyerManagement.Buyer.CompanyName]
            - header: Company
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_RMA.RMAStatus.ExternalStatusText]
            ➤ **content** (Widgets)
              - 📦 **DataView** [Context]
            - header: RMA Status
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_RMA.RMA.RequestSKUs]
            - header: SKUs
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: center
            - showContentAs: dynamicText
            - attribute: [Attr: EcoATM_RMA.RMA.RequestQty]
            - dynamicText: {1}
            - header: Qty
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: center
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_RMA.RMA.RequestSalesTotal]
            ➤ **content** (Widgets)
            - header: Original Total
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: right
        - pageSize: 20
        - pagination: buttons
        - showPagingButtons: always
        - pagingPosition: bottom
        - loadMoreButtonCaption: Load More
        - showEmptyPlaceholder: custom
        ➤ **emptyPlaceholder** (Widgets)
          - 🧩 **HTML Element** [DP: {Spacing top: Outer large}] 👁️ (If: `$currentObject != empty`) (ID: `com.mendix.widget.web.htmlelement.HTMLElement`)
              - tagName: span
              - tagNameCustom: div
              - tagContentMode: innerHTML
              - tagContentHTML: <body class="body-container"> <div class="message-container"> <span>There are currently no RMA requests</span> </div> </body>
        - rowClass: `'pws-orderhistory-datagridhover'`
        - onClickTrigger: single
        - configurationStorageType: attribute
        ➤ **filterList**
            - filter: [Attr: EcoATM_RMA.RMA.Number]
            - filter: [Attr: EcoATM_RMA.RMA.RequestSKUs]
            - filter: [Attr: EcoATM_RMA.RMAStatus.SystemStatus]
        ➤ **filtersPlaceholder** (Widgets)
          - 🧩 **Text filter** [Class: `rma-search `] (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
              - screenReaderInputCaption: Search
          - 🖼️ **Image**: pws_search [DP: {Spacing left: Outer small}]
            ↳ [acti] → **Microflow**: `EcoATM_RMA.ACT_RequestRMA`
            ↳ [acti] → **Microflow**: `EcoATM_RMA.ACT_ExportRMAExcelFile`
            ↳ [acti] → **Page**: `EcoATM_RMA.RMA_Instructions`
            ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_DownloadPWSReturnPolicy`
        - exportDialogLabel: Export progress
        - cancelExportLabel: Cancel data export
        - selectRowLabel: Select row
  - 📦 **DataView** [MF: EcoATM_PWS.DS_BuyerCodeBySession]
    - 🧩 **Data grid 2** [Class: `pws-rma-returns-datagrid`] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: checkbox
        - itemSelectionMode: clear
        - loadingType: spinner
        ➤ **columns**
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_RMA.RMA.Number]
            - header: RMA Number
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_RMA.RMA.SubmittedDate]
            - header: Submit Date
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: attribute
            - attribute: [Attr: Administration.Account.FullName]
            - header: Buyer
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_BuyerManagement.Buyer.CompanyName]
            - header: Company
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_RMA.RMAStatus.ExternalStatusText]
            ➤ **content** (Widgets)
              - 📦 **DataView** [Context]
            - header: RMA Status
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: dynamicText
            - attribute: [Attr: EcoATM_RMA.RMA.RequestSKUs]
            - dynamicText: {1}
            - header: SKUs
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: center
            - showContentAs: dynamicText
            - attribute: [Attr: EcoATM_RMA.RMA.RequestQty]
            - dynamicText: {1}
            - header: Qty
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: center
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_RMA.RMA.RequestSalesTotal]
            ➤ **content** (Widgets)
            - header: Original Total
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: right
        - pageSize: 20
        - pagination: buttons
        - showPagingButtons: always
        - pagingPosition: bottom
        - loadMoreButtonCaption: Load More
        - showEmptyPlaceholder: custom
        ➤ **emptyPlaceholder** (Widgets)
          - 🧩 **HTML Element** [DP: {Spacing top: Outer large}] (ID: `com.mendix.widget.web.htmlelement.HTMLElement`)
              - tagName: span
              - tagNameCustom: div
              - tagContentMode: innerHTML
              - tagContentHTML: <body class="body-container"> <div class="message-container"> <span>There are currently no RMA requests</span> </div> </body>
        - rowClass: `'pws-orderhistory-datagridhover'`
        - onClickTrigger: single
        - configurationStorageType: attribute
        ➤ **filterList**
            - filter: [Attr: EcoATM_RMA.RMA.Number]
            - filter: [Attr: EcoATM_RMA.RMA.RequestSKUs]
            - filter: [Attr: EcoATM_RMA.RMAStatus.SystemStatus]
        ➤ **filtersPlaceholder** (Widgets)
          - 🧩 **Text filter** [Class: `rma-search `] (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
              - screenReaderInputCaption: Search
          - 🖼️ **Image**: pws_search [DP: {Spacing left: Outer small}]
            ↳ [acti] → **Microflow**: `EcoATM_RMA.ACT_RequestRMA`
            ↳ [acti] → **Microflow**: `EcoATM_RMA.ACT_ExportRMAExcelFile`
            ↳ [acti] → **Page**: `EcoATM_RMA.RMA_Instructions`
            ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_DownloadPWSReturnPolicy`
        - exportDialogLabel: Export progress
        - cancelExportLabel: Cancel data export
        - selectRowLabel: Select row
- 📦 **DataView** [NF: EcoATM_Direct_Theme.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
