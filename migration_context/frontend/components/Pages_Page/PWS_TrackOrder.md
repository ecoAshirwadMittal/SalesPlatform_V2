# Page: PWS_TrackOrder

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesRep, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps

**Layout:** `AuctionUI.ecoATM_Popup_Layout_NoTitle`

## Widget Tree

  ↳ [Click] → **Cancel Changes**
  ↳ [acti] → **Cancel Changes**
- 📦 **DataView** [Context]
- 🧩 **Data grid 2** [Class: `pws-trackorder-datagrid`] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: checkbox
    - itemSelectionMode: clear
    - loadingType: spinner
    ➤ **columns**
        - showContentAs: customContent
        - attribute: [Attr: EcoATM_PWS.ShipmentDetail.TrackingNumber]
        ➤ **content** (Widgets)
            ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_OpenLinkInNewTab`
        - header: Tracking Number
        - visible: `true`
        - filterCaptionType: expression
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_PWS.ShipmentDetail.SKUCount]
        - header: SKUs
        - visible: `true`
        - filterCaptionType: expression
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_PWS.ShipmentDetail.Quantity]
        - header: Qty
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
    - showEmptyPlaceholder: none
    - onClickTrigger: single
    - configurationStorageType: attribute
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
