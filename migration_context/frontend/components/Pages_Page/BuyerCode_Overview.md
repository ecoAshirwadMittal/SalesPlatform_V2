# Page: BuyerCode_Overview

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

  ↳ [acti] → **Page**: `AuctionUI.BuyerCode_New`
- 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: checkbox
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_BuyerManagement.Buyer.CompanyName]
        - header: Company Name
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
        - hidable: no
        - width: autoFill
        - size: 1
        - alignment: left
        - visible: `true`
        - minWidth: auto
        - minWidthLimit: 100
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_BuyerManagement.BuyerCode.Code]
        - header: Buyer Code
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
        - hidable: yes
        - width: autoFill
        - size: 1
        - alignment: left
        - visible: `true`
        - minWidth: auto
        - minWidthLimit: 100
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_BuyerManagement.BuyerCode.BuyerCodeType]
        - header: Type
        ➤ **filter** (Widgets)
          - 🧩 **Drop-down filter** (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
              - selectedItemsStyle: text
              - selectionMethod: checkbox
        - hidable: yes
        - width: autoFill
        - size: 1
        - alignment: left
        - visible: `true`
        - minWidth: auto
        - minWidthLimit: 100
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_BuyerManagement.BuyerCode.ShowSubmitOfferBtn]
        - header: Show Submit Offer
        ➤ **filter** (Widgets)
          - 🧩 **Drop-down filter** (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
              - selectedItemsStyle: text
              - selectionMethod: checkbox
        - visible: `true`
        - filterCaptionType: expression
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: customContent
        - attribute: [Attr: EcoATM_BuyerManagement.BuyerCode.SubmissionID]
        ➤ **content** (Widgets)
            ↳ [acti] → **Page**: `AuctionUI.BuyerCode_Edit`
            ↳ [acti] → **Delete**
            ↳ [acti] → **Page**: `EcoATM_BuyerManagement.BuyerCodeChangeLog_Overview`
        - hidable: no
        - width: autoFit
        - size: 1
        - alignment: left
        - visible: `true`
        - minWidth: auto
        - minWidthLimit: 100
        - filterCaptionType: expression
    - pageSize: 20
    - pagination: virtualScrolling
    - pagingPosition: bottom
    - showEmptyPlaceholder: none
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - showPagingButtons: always
    - onClickTrigger: single
    - selectRowLabel: Select row
    - itemSelectionMode: clear
    - loadMoreButtonCaption: Load More
    - configurationStorageType: attribute
    - loadingType: spinner
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
