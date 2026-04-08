# Page: Buyer_Overview

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.Compliance

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 🧩 **Active Menu Selector** (ID: `mendix.activemenuselector.ActiveMenuSelector`)
    - menuWidgetName: navigationTree3
    - menuItemTitle: Buyers
  ↳ [acti] → **Microflow**: `EcoATM_MDM.ACT_SendAllBuyerstoSnowflake`
  ↳ [acti] → **Microflow**: `EcoATM_MDM.ACT_Buyer_CreateNewFromBuyer`
- 🧩 **Data grid 2** [Class: `datagridfilter` | DP: {Borders: Both}] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: checkbox
    ➤ **columns**
        - showContentAs: customContent
        - attribute: [Attr: EcoATM_BuyerManagement.Buyer.CompanyName]
        ➤ **content** (Widgets)
          - 🖼️ **Image**: Hotel
        - header: Buyer Name
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
        - attribute: [Attr: EcoATM_BuyerManagement.Buyer.BuyerCodesDisplay]
        - header: Buyer Codes
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
        - showContentAs: customContent
        - attribute: [Attr: EcoATM_BuyerManagement.Buyer.Status]
        ➤ **content** (Widgets)
          - 🧩 **HTML Element** (ID: `com.mendix.widget.web.htmlelement.HTMLElement`)
              - tagName: div
              - tagNameCustom: div
              - tagContentMode: container
              ➤ **tagContentContainer** (Widgets)
                - 🖼️ **Image**: active_rollover 👁️ (If Status is Active/Disabled/(empty))
                - 🖼️ **Image**: Disabled_Rollover 👁️ (If Status is Active/Disabled/(empty))
            ↳ [acti] → **Microflow**: `AuctionUI.ACT_OpenBuyerEditPage`
        - header: Status
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
    - pageSize: 20
    - pagination: buttons
    - pagingPosition: bottom
    - showEmptyPlaceholder: none
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - showPagingButtons: auto
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
