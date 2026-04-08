# Page: RoundTwoSelectedBuyers

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.SalesOps

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📦 **DataView** [MF: AuctionUI.DS_GetOrCreateAggregatedInventoryHelper]
  - 🧩 **Combo box** [DP: {Spacing left: Outer small, Spacing bottom: Outer none}] (ID: `com.mendix.widget.web.combobox.Combobox`)
      - source: context
      - optionsSourceType: association
      - optionsSourceDatabaseCaptionType: attribute
      - optionsSourceAssociationCaptionType: expression
      - optionsSourceAssociationCaptionExpression: `$currentObject/WeekDisplay
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
  - 📦 **DataView** [Context] 👁️ (If: `$currentObject/AuctionUI.AggInventoryHelper_Week!=empty`)
    - 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: checkbox
        - itemSelectionMode: clear
        ➤ **columns**
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_BuyerManagement.Buyer.CompanyName]
            - header: Company Name
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
            - attribute: [Attr: EcoATM_BuyerManagement.Buyer.isSpecialBuyer]
            - header: Special Buyer
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
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_BuyerManagement.BuyerCode.Code]
            ➤ **content** (Widgets)
                ↳ [acti] → **Microflow**: `AuctionUI.ACT_RemoveRound2BuyerCode`
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
        ➤ **filtersPlaceholder** (Widgets)
            ↳ [acti] → **Microflow**: `AuctionUI.SUB_AssignRoundTwoBuyers`
        - exportDialogLabel: Export progress
        - cancelExportLabel: Cancel data export
        - selectRowLabel: Select row
        - loadingType: spinner
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
