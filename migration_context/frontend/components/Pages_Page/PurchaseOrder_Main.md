# Page: PurchaseOrder_Main

**Allowed Roles:** EcoATM_PO.Administrator, EcoATM_PO.SalesOps

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📦 **DataView** [Context]
  - 🧩 **Combo box** [Class: `purchase-order-dropdown` | DP: {Spacing bottom: Outer none, Spacing left: Inner none, Spacing right: Inner none}] (ID: `com.mendix.widget.web.combobox.Combobox`)
      - source: context
      - optionsSourceType: association
      - optionsSourceDatabaseCaptionType: attribute
      - optionsSourceAssociationCaptionType: expression
      - optionsSourceAssociationCaptionExpression: `$currentObject/EcoATM_PO.PurchaseOrder_Week_From/EcoATM_MDM.Week/Year + '/' +$currentObject/EcoATM_PO.PurchaseOrder_Week_From/EcoATM_MDM.Week/WeekNumber+ '-'+
$currentObject/EcoATM_PO.PurchaseOrder_Week_To/EcoATM_MDM.Week/Year + '-' +$currentObject/EcoATM_PO.PurchaseOrder_Week_To/EcoATM_MDM.Week/WeekNumber


`
      - filterType: contains
      - optionsSourceAssociationCustomContentType: no
      - optionsSourceDatabaseCustomContentType: no
      - selectionMethod: checkbox
      - selectedItemsStyle: text
      - selectAllButtonCaption: Select all
      - ariaRequired: `false`
      - clearButtonAriaLabel: Clear selection
      - removeValueAriaLabel: Remove value
      - a11ySelectedValue: Selected value:
      - a11yOptionsAvailable: Number of options available:
      - a11yInstructions: Use up and down arrow keys to navigate. Press Enter or Space Bar keys to select.
      - staticDataSourceCustomContentType: no
      - readOnlyStyle: text
      - loadingType: spinner
      - selectedItemsSorting: none
    ↳ [acti] → **Microflow**: `EcoATM_PO.ACT_CreateNewPO`
    ↳ [acti] → **Nanoflow**: `EcoATM_PO.ACT_ExportPOtoExcel`
    ↳ [acti] → **Microflow**: `EcoATM_PO.ACT_OnDemandSync`
    ↳ [acti] → **Microflow**: `EcoATM_PO.ACT_UpdatePO`
  - 🧩 **Data grid 2** [Class: `datagridfilter` | DP: {Borders: Both}] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: checkbox
      ➤ **columns**
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PO.PODetail.ProductID]
          - header: ProductID
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
          - attribute: [Attr: EcoATM_PO.PODetail.Grade]
          - header: Grade
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
          - attribute: [Attr: EcoATM_PO.PODetail.ModelName]
          - header: ModelName
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
          - attribute: [Attr: EcoATM_BuyerManagement.BuyerCode.Code]
          - header: BuyerCode
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
          - attribute: [Attr: EcoATM_PO.PODetail.Price]
          - header: Price
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
          - attribute: [Attr: EcoATM_PO.PODetail.QtyCap]
          - header: QtyCap
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
          - attribute: [Attr: EcoATM_PO.PODetail.PriceFulfilled]
          - header: PriceFulfilled
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PO.PODetail.QtyFullfiled]
          - header: QtyFullfiled
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
      - showEmptyPlaceholder: none
      - onClickTrigger: single
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
