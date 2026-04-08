# Page: PWS_RMADetails_Sales

**Allowed Roles:** EcoATM_RMA.Administrator, EcoATM_RMA.SalesLeader, EcoATM_RMA.SalesOps, EcoATM_RMA.SalesRep

**Layout:** `EcoATM_Direct_Theme.EcoATM_PWS_Sales`

## Widget Tree

- 📦 **DataView** [Context]
  - 📦 **DataView** [Context]
      ↳ [Click] → **Page**: `EcoATM_RMA.RMA_RequestsOverview_Sales`
      - 🧩 **Image** [Class: `pws-orderdetails-navfont`] (ID: `com.mendix.widget.web.image.Image`)
          - datasource: icon
          - onClickType: action
          - widthUnit: auto
          - width: 100
          - heightUnit: auto
          - height: 100
          - iconSize: 14
          - displayAs: fullImage
      ↳ [acti] → **Microflow**: `EcoATM_RMA.ACT_RMADetailSales_Export`
      ↳ [acti] → **Microflow**: `EcoATM_RMA.ACT_RMADetails_CompleteReview`
      ↳ [acti] → **Microflow**: `EcoATM_RMA.ACT_RMADetailSales_Export`
    - 🔤 **Text**: "More Actions" [Class: `pws-usericon_settings_title`]
      ↳ [acti] → **Microflow**: `EcoATM_RMA.ACT_RMADetailSales_Export_PendingApproval`
      ↳ [acti] → **Microflow**: `EcoATM_RMA.ACT_RMAItem_SalesApproveAll`
      ↳ [acti] → **Microflow**: `EcoATM_RMA.ACT_RMAItem_SalesDeclineAll`
    - 🧩 **Data grid 2** [Class: `pws-rmadetails-datagrid-withborder`] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: checkbox
        - itemSelectionMode: clear
        - loadingType: spinner
        ➤ **columns**
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_RMA.RMAItem.IMEI]
            - header: IMEI/Serial
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_RMA.RMAItem.OrderNumber]
            - header: Order Number
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: dynamicText
            - attribute: [Attr: EcoATM_RMA.RMAItem.ShipDate]
            - dynamicText: {1}
            - header: Ship Date
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
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
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `if $currentObject/EcoATM_RMA.RMAItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/Grade = 'A_YYY' 
or
$currentObject/EcoATM_RMA.RMAItem_Device/EcoATM_PWSMDM.Device/ItemType = 'SPB'
then 
'rmaitem-sku-bgcolor'
else 
''`
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_PWSMDM.Device.DeviceDescription]
            ➤ **content** (Widgets)
            - header: Description
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_PWSMDM.Grade.Grade]
            - header: Grade
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: dynamicText
            - attribute: [Attr: EcoATM_RMA.RMAItem.SalePrice]
            - dynamicText: ${1}
            - header: Original Price
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_RMA.RMAItem.ReturnReason]
            - header: Return Reason
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_RMA.RMAItem.Status]
            ➤ **content** (Widgets)
              - 🖼️ **Image**: Group_2057 👁️ (If Status is Approve/Decline/(empty))
              - 🖼️ **Image**: Group_2058 👁️ (If Status is Approve/Decline/(empty))
              - 📝 **DropDown**: dropDown1 [Class: `no-border-dropdown` | Dynamic: `if toString($currentObject/Status) = 'Approve' then
'fa fa-thumbs-up text-success'
else 'fa fa-times-circle text-danger'


`]
                ↳ [Change] → **Microflow**: `EcoATM_RMA.OCH_RMAItem_Action`
            - header: Action
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 152
            - size: 1
            - alignment: left
            - columnClass: `'pws-offeritem-datagrid-background-color pws-offer-actions-column'`
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
    - 🧩 **Data grid 2** [Class: `pws-rmadetails-datagrid`] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: checkbox
        - itemSelectionMode: clear
        - loadingType: spinner
        ➤ **columns**
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_RMA.RMAItem.IMEI]
            - header: IMEI/Serial
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_RMA.RMAItem.OrderNumber]
            - header: Order Number
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: dynamicText
            - attribute: [Attr: EcoATM_RMA.RMAItem.ShipDate]
            - dynamicText: {1}
            - header: Ship Date
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
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
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `if $currentObject/EcoATM_RMA.RMAItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/Grade = 'A_YYY' 
or
$currentObject/EcoATM_RMA.RMAItem_Device/EcoATM_PWSMDM.Device/ItemType = 'SPB'
then 
'rmaitem-sku-bgcolor'
else 
''`
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_PWSMDM.Device.DeviceDescription]
            ➤ **content** (Widgets)
            - header: Description
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_PWSMDM.Grade.Grade]
            - header: Grade
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: dynamicText
            - attribute: [Attr: EcoATM_RMA.RMAItem.SalePrice]
            - dynamicText: ${1}
            - header: Original Price
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_RMA.RMAItem.ReturnReason]
            - header: Return Reason
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_RMA.RMAItem.Status]
            ➤ **content** (Widgets)
            - header: Decision
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
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
