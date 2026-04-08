# Page: SchedulingAuction_SubmittedBuyerCodes

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: checkbox
    - itemSelectionMode: clear
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_BuyerManagement.BuyerCode.Code]
        - header: BuyerCode
        - visible: `true`
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: customContent
        - attribute: [Attr: AuctionUI.BidRound.BidRoundId]
        ➤ **content** (Widgets)
            ↳ [acti] → **Microflow**: `AuctionUI.ACT_SendBidstoSharepoint_perBuyerCode_Admin`
            ↳ [acti] → **Microflow**: `AuctionUI.ACT_SchedulingAuction_SendBidsToSnowflake_perBuyerCode_Admin`
        - visible: `true`
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
    - pageSize: 20
    - pagination: buttons
    - pagingPosition: bottom
    - showPagingButtons: always
    - loadMoreButtonCaption: Load More
    - showEmptyPlaceholder: none
    - onClickTrigger: single
    - configurationStorageType: attribute
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
    - loadingType: spinner
