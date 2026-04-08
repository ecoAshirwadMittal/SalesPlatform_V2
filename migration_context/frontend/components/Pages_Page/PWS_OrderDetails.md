# Page: PWS_OrderDetails

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

**Layout:** `EcoATM_Direct_Theme.EcoATM_PWS_Bidder`

## Widget Tree

- 📦 **DataView** [Context]
    ↳ [Click] → **Page**: `EcoATM_PWS.PWS_OrderHistory`
    - 🧩 **Image** [Class: `pws-orderdetails-navfont`] (ID: `com.mendix.widget.web.image.Image`)
        - datasource: icon
        - onClickType: action
        - widthUnit: auto
        - width: 100
        - heightUnit: auto
        - height: 100
        - iconSize: 14
        - displayAs: fullImage
  - 📦 **DataView** [MF: EcoATM_PWS.DS_OrderStatusObjectByOrderStatus] [DP: {Spacing top: Outer large}]
  - 📦 **DataView** [MF: EcoATM_PWS.DS_Create_OrderDetailsHelper]
    - 📦 **DataView** [MF: EcoATM_PWS.DS_OrderStatusObjectByOrderStatus] [DP: {Spacing top: Outer large}]
        ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_OrderDetails_UpdateView`
        ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_OrderDetails_UpdateView`
        ↳ [acti] → **Nanoflow**: `EcoATM_PWS.NAV_BuyerCounterOffer`
        ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_OrderDetails_Export`
        ↳ [acti] → **Page**: `EcoATM_PWS.PWS_TrackOrder`
    - 🧩 **Data grid 2** [Class: `pws-orderdetails-datagrid`] 👁️ (If OrderDetailDataGridSource is BySKU/ByDevice/(empty)) (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: checkbox
        - itemSelectionMode: clear
        - loadingType: spinner
        ➤ **columns**
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_PWSMDM.Device.SKU]
            - header: SKU
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `'pws-orderhistory-table-font'`
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_PWSMDM.Device.DeviceDescription]
            - header: Description
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `'pws-oh-table-boldtext'`
            - showContentAs: customContent
            ➤ **content** (Widgets)
            - header: Ordered Qty
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: center
            - columnClass: `if $dataView1/OrderStatus='Offer Declined' or $dataView1/OrderStatus='Order Cancelled'
then
'pws-orderhistory-table-font text-line-through'
else
'pws-orderhistory-table-font'`
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_PWS.OfferItem.ShippedQty]
            ➤ **content** (Widgets)
            - header: Shipped Qty
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: center
            - columnClass: `if $dataView1/OrderStatus='Offer Declined' or $dataView1/OrderStatus='Order Cancelled'
then
'pws-orderhistory-table-font text-line-through'
else
'pws-orderhistory-table-font'
`
            - showContentAs: customContent
            ➤ **content** (Widgets)
            - header: Unit Price
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: center
            - columnClass: `if $dataView1/OrderStatus='Offer Declined' or $dataView1/OrderStatus='Order Cancelled'
then
'pws-orderhistory-table-font text-line-through'
else
'pws-orderhistory-table-font'
`
            - showContentAs: customContent
            ➤ **content** (Widgets)
            - header: Total Price
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: center
            - columnClass: `if $dataView1/OrderStatus='Offer Declined' or $dataView1/OrderStatus='Order Cancelled'
then
'pws-orderhistory-table-font text-line-through'
else
'pws-orderhistory-table-font'
`
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
    - 🧩 **Data grid 2** [Class: `pws-orderdetails-datagrid`] 👁️ (If OrderDetailDataGridSource is BySKU/ByDevice/(empty)) (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: checkbox
        - itemSelectionMode: clear
        - loadingType: spinner
        ➤ **columns**
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_PWS.IMEIDetail.IMEINumber]
            - header: IMEI
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_PWSMDM.Device.SKU]
            - header: SKU
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `'pws-orderhistory-table-font'`
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_PWSMDM.Device.DeviceDescription]
            - header: Description
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `'pws-oh-table-boldtext'`
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_PWS.OfferItem.FinalOfferPrice]
            ➤ **content** (Widgets)
            - header: Unit Price
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: center
            - columnClass: `if $dataView1/OrderStatus='Offer Declined' or $dataView1/OrderStatus='Order Cancelled'
then
'pws-orderhistory-table-font text-line-through'
else
'pws-orderhistory-table-font'
`
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_PWS.IMEIDetail.SerialNumber]
            - header: Serial Number
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_PWS.IMEIDetail.BoxLPNNumber]
            - header: Box Number
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_PWS.ShipmentDetail.TrackingNumber]
            ➤ **content** (Widgets)
                ↳ [acti] → **Nanoflow**: `EcoATM_PWS.ACT_OpenLinkInNewTab`
            - header: Tracking Number
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
- 📦 **DataView** [NF: EcoATM_Direct_Theme.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
