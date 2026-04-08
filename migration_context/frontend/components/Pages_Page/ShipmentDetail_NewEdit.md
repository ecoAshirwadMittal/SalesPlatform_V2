# Page: ShipmentDetail_NewEdit

**Allowed Roles:** EcoATM_PWS.Administrator

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context]
  - 🧩 **Combo box** (ID: `com.mendix.widget.web.combobox.Combobox`)
      - source: context
      - optionsSourceType: association
      - optionsSourceAssociationCaptionType: attribute
      - optionsSourceDatabaseCaptionType: attribute
      - optionsSourceAssociationCaptionAttribute: [Attr: EcoATM_PWS.Order.OrderNumber]
      - optionsSourceAssociationCustomContentType: no
      - optionsSourceDatabaseCustomContentType: no
      - staticDataSourceCustomContentType: no
      - selectionMethod: checkbox
      - selectedItemsStyle: text
      - selectAllButtonCaption: Select all
      - readOnlyStyle: bordered
      - ariaRequired: `false`
      - clearButtonAriaLabel: Clear selection
      - removeValueAriaLabel: Remove value
      - a11ySelectedValue: Selected value:
      - a11yOptionsAvailable: Number of options available:
      - a11yInstructions: Use up and down arrow keys to navigate. Press Enter or Space Bar keys to select.
      - loadingType: spinner
      - selectedItemsSorting: none
      - filterType: contains
  - 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: checkbox
      - itemSelectionMode: clear
      - loadingType: spinner
      ➤ **columns**
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWS.IMEIDetail.IMEINumber]
          - header: IMEI serial number
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
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWS.IMEIDetail.SerialNumber]
          - header: LPN number
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
          - showContentAs: attribute
          - attribute: [Attr: EcoATM_PWSMDM.Device.SKU]
          - header: SKU
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
    ↳ [acti] → **Save Changes**
    ↳ [acti] → **Cancel Changes**
