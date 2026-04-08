# Page: BidRound_Overview

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

  ↳ [acti] → **Nanoflow**: `AuctionUI.NF_RoundAdminExportToExcel`
- 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: checkbox
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.Auction.AuctionTitle]
        - header: Auction Title
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.SchedulingAuction.Round]
        - header: Round
        ➤ **filter** (Widgets)
          - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
              - defaultFilter: equal
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_BuyerManagement.BuyerCode.Code]
        - header: Buyer Code
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.BidRound.Submitted]
        - header: Submitted
        ➤ **filter** (Widgets)
          - 🧩 **Drop-down filter** (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
              - selectedItemsStyle: text
              - selectionMethod: checkbox
        - visible: `true`
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: dynamicText
        - attribute: [Attr: AuctionUI.BidRound.SubmittedDatatime]
        - dynamicText: {1}
        - header: Submitted On
        ➤ **filter** (Widgets)
          - 🧩 **Date filter** (ID: `com.mendix.widget.web.datagriddatefilter.DatagridDateFilter`)
              - defaultFilter: equal
        - visible: `true`
        - filterCaptionType: expression
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: attribute
        - attribute: [Attr: AuctionUI.BidRound.UploadedToSharepoint]
        - header: Uploaded
        - tooltip: Uploaded To Sharepoint
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
        - showContentAs: dynamicText
        - attribute: [Attr: AuctionUI.BidRound.UploadToSharepointDateTime]
        - dynamicText: {1}
        - header: Upload On
        ➤ **filter** (Widgets)
          - 🧩 **Date filter** (ID: `com.mendix.widget.web.datagriddatefilter.DatagridDateFilter`)
              - defaultFilter: equal
        - visible: `true`
        - filterCaptionType: expression
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: customContent
        - attribute: [Attr: AuctionUI.BidRound.BidRoundId]
        ➤ **content** (Widgets)
            ↳ [acti] → **Microflow**: `AuctionUI.ACT_DeleteBidDataByBuyer`
            ↳ [acti] → **Microflow**: `AuctionUI.ACT_SendBidstoSharepoint_perBuyerCode_Admin`
            ↳ [acti] → **Page**: `AuctionUI.BidSubmitLog_Overview`
        - visible: `true`
        - hidable: no
        - width: autoFit
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
    - pageSize: 20
    - pagination: buttons
    - pagingPosition: bottom
    - showPagingButtons: always
    - showEmptyPlaceholder: none
    - onClickTrigger: double
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
    - itemSelectionMode: clear
    - loadMoreButtonCaption: Load More
    - configurationStorageType: attribute
    - loadingType: spinner
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
