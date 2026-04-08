# Page: PG_Bidder_Dashboard_DG2

**Allowed Roles:** EcoATM_BuyerManagement.Administrator, EcoATM_BuyerManagement.Bidder, EcoATM_BuyerManagement.SalesRep

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 🔤 **Text**: "Switch Buyer Code"
- 🧩 **Gallery** [DP: {Borders: Both}] (ID: `com.mendix.widget.web.gallery.Gallery`)
    ➤ **content** (Widgets)
      - 🖼️ **Image**: Hotel
    - desktopItems: 1
    - tabletItems: 1
    - phoneItems: 1
    - pageSize: 1
    - pagination: buttons
    - pagingPosition: below
    - showEmptyPlaceholder: none
    - itemSelectionMode: clear
    - onClickTrigger: single
- 📦 **DataView** [Context]
    ↳ [acti] → **Microflow**: `EcoATM_BuyerManagement.ACT_BidDataDoc_ExportExcel`
    ↳ [acti] → **Nanoflow**: `EcoATM_BidData.ACT_BidData_SelectImportSheet`
  - 📦 **DataView** [Context]
    - 📦 **DataView** [Context]
  - ⚡ **Button**: Submit Bids [Style: Primary] [Class: `buttonshape_nowidth` | DP: {Align self: Right, Align icon: Right, Spacing left: Inner large}]
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_OpenBidSubmitConfirmation`
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_OpenBidSubmitConfirmation`
  - 📦 **DataView** [Context]
    - 📦 **DataView** [MF: EcoATM_BuyerManagement.DS_MinimumBidConfg]
  - 📦 **DataView** [Context]
      ↳ [acti] → **Microflow**: `EcoATM_BidData.ACT_Start_CarryoverBids`
  - 🧩 **HTMLSnippet** (ID: `HTMLSnippet.widget.HTMLSnippet`)
      - contenttype: js
      - contents: document.addEventListener("focusin", function (e) { if (e.target && e.target.closest(".textbox-select-all")) { e.target.select(); } });
- 📦 **DataView** [Context]
  - 📦 **DataView** [NF: EcoATM_BuyerManagement.DS_CreteDG2UiHelper]
    - 🧩 **Data grid 2** [Class: `auctions-datagrid `] 👁️ (If: `$currentObject/IsBidDataLoaded=true
and
$currentObject/DisplayBidRankColumn=false`) (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: checkbox
        - itemSelectionMode: clear
        - loadingType: spinner
        ➤ **columns**
            - showContentAs: attribute
            - attribute: [Attr: AuctionUI.BidData.EcoID]
            - header: Product Id
            ➤ **filter** (Widgets)
              - 🧩 **Number filter** [Class: `auctions-filter`] (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                  - defaultFilter: equal
                  - delay: 500
                  - screenReaderInputCaption: Search
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: right
            - columnClass: `'auctions-datagrid-display-cell'`
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_MDM.Brand.DisplayName]
            - header: Brand
            - tooltip: {1}
            ➤ **filter** (Widgets)
              - 🧩 **Drop-down filter** [Class: `auctions-dropdownfilter-fitwidth`] (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
                  - selectedItemsStyle: text
                  - selectionMethod: checkbox
            - visible: `true`
            - filterCaptionType: attribute
            - filterAssociationOptionLabelAttr: [Attr: EcoATM_MDM.Brand.DisplayName]
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `'auctions-datagrid-display-cell'`
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_MDM.Model.DisplayName]
            - header: Model
            - tooltip: {1}
            ➤ **filter** (Widgets)
              - 🧩 **Drop-down filter** [Class: `auctions-dropdownfilter-fitwidth`] (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
                  - selectedItemsStyle: text
                  - selectionMethod: checkbox
            - visible: `true`
            - filterCaptionType: attribute
            - filterAssociationOptionLabelAttr: [Attr: EcoATM_MDM.Model.DisplayName]
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `'auctions-datagrid-display-cell'`
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_MDM.ModelName.DisplayName]
            - header: Model Name
            - tooltip: {1}
            ➤ **filter** (Widgets)
              - 🧩 **Drop-down filter** [Class: `auctions-dropdownfilter-fitwidth`] (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
                  - selectedItemsStyle: text
                  - selectionMethod: checkbox
            - visible: `true`
            - filterCaptionType: attribute
            - filterAssociationOptionLabelAttr: [Attr: EcoATM_MDM.ModelName.DisplayName]
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `'auctions-datagrid-display-cell'`
            - showContentAs: attribute
            - attribute: [Attr: AuctionUI.BidData.Merged_Grade]
            - header: Grade
            ➤ **filter** (Widgets)
              - 🧩 **Text filter** [Class: `auctions-filter`] (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                  - defaultFilter: contains
                  - delay: 500
                  - screenReaderInputCaption: Search
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `'auctions-datagrid-display-cell auction-grade'`
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_MDM.Carrier.DisplayName]
            - header: Carrier
            ➤ **filter** (Widgets)
              - 🧩 **Drop-down filter** [Class: `auctions-dropdownfilter-fitwidth`] (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
                  - selectedItemsStyle: text
                  - selectionMethod: checkbox
            - visible: `true`
            - filterCaptionType: attribute
            - filterAssociationOptionLabelAttr: [Attr: EcoATM_MDM.Carrier.DisplayName]
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 114
            - size: 1
            - alignment: left
            - columnClass: `'auctions-datagrid-display-cell auction-carrier'`
            - showContentAs: attribute
            - attribute: [Attr: AuctionUI.AggregatedInventory.CreatedAt]
            - header: Added
            ➤ **filter** (Widgets)
              - 🧩 **Date filter** [Class: `auctions-filter`] (ID: `com.mendix.widget.web.datagriddatefilter.DatagridDateFilter`)
                  - defaultFilter: greaterEqual
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 102
            - size: 1
            - alignment: left
            - columnClass: `'auctions-datagrid-display-cell auction-added'`
            - showContentAs: attribute
            - attribute: [Attr: AuctionUI.BidData.Data_Wipe_Quantity]
            - header: Avail. Qty
            - tooltip: Data Wipe Available Quantity
            ➤ **filter** (Widgets)
              - 🧩 **Number filter** [Class: `auctions-filter`] (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                  - defaultFilter: equal
                  - delay: 500
                  - screenReaderInputCaption: Search
            - visible: `if $dataView3/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe
then true
else false
`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 105
            - size: 1
            - alignment: center
            - columnClass: `'auctions-datagrid-display-cell auction-availqty'`
            - showContentAs: attribute
            - attribute: [Attr: AuctionUI.BidData.MaximumQuantity]
            - header: Avail. Qty
            - tooltip: Wholesale Available Quantity
            ➤ **filter** (Widgets)
              - 🧩 **Number filter** [Class: `auctions-filter`] (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                  - defaultFilter: equal
                  - delay: 500
                  - screenReaderInputCaption: Search
            - visible: `if $dataView3/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Wholesale
then true
else false`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 105
            - size: 1
            - alignment: center
            - columnClass: `'auctions-datagrid-display-cell auction-availqty'`
            - showContentAs: dynamicText
            - attribute: [Attr: AuctionUI.BidData.TargetPrice]
            - dynamicText: ${1}
            - header: Target Price
            ➤ **filter** (Widgets)
              - 🧩 **Number filter** [Class: `auctions-filter`] (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                  - defaultFilter: equal
                  - delay: 500
                  - screenReaderInputCaption: Search
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 114
            - size: 1
            - alignment: center
            - columnClass: `'auctions-datagrid-display-cell auction-targetprice'`
            - showContentAs: customContent
            - attribute: [Attr: AuctionUI.BidData.BidAmount]
            ➤ **content** (Widgets)
                ↳ [Change] → **Nanoflow**: `EcoATM_BuyerManagement.OCH_ValidateAndSaveBidData`
            - header: Price
            ➤ **filter** (Widgets)
              - 🧩 **Number filter** [Class: `auctions-filter`] (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                  - defaultFilter: equal
                  - delay: 500
                  - screenReaderInputCaption: Search
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 105
            - size: 1
            - alignment: center
            - columnClass: `'auction-price'`
            - showContentAs: customContent
            - attribute: [Attr: AuctionUI.BidData.BidQuantity]
            ➤ **content** (Widgets)
                ↳ [Change] → **Nanoflow**: `EcoATM_BuyerManagement.OCH_ValidateAndSaveBidData`
            - header: Qty. Cap
            ➤ **filter** (Widgets)
              - 🧩 **Number filter** [Class: `auctions-filter`] (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                  - defaultFilter: equal
                  - delay: 500
                  - screenReaderInputCaption: Search
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 105
            - size: 1
            - alignment: center
            - columnClass: `'auction-qtycap'`
        - pageSize: 20
        - pagination: virtualScrolling
        - showPagingButtons: always
        - pagingPosition: bottom
        - loadMoreButtonCaption: Load More
        - showEmptyPlaceholder: none
        - onClickTrigger: single
        - configurationStorageType: attribute
        ➤ **filtersPlaceholder** (Widgets)
        - exportDialogLabel: Export progress
        - cancelExportLabel: Cancel data export
        - selectRowLabel: Select row
    - 🧩 **Data grid 2** [Class: `auctions-datagrid-bidrank`] 👁️ (If: `$currentObject/IsBidDataLoaded=true
and
$currentObject/DisplayBidRankColumn=true`) (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: checkbox
        - itemSelectionMode: clear
        - loadingType: spinner
        ➤ **columns**
            - showContentAs: attribute
            - attribute: [Attr: AuctionUI.BidData.EcoID]
            - header: Product Id
            ➤ **filter** (Widgets)
              - 🧩 **Number filter** [Class: `auctions-filter`] (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                  - defaultFilter: equal
                  - delay: 500
                  - screenReaderInputCaption: Search
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: right
            - columnClass: `'auctions-datagrid-bidrank-display-cell'`
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_MDM.Brand.DisplayName]
            - header: Brand
            - tooltip: {1}
            ➤ **filter** (Widgets)
              - 🧩 **Drop-down filter** [Class: `auctions-dropdownfilter-fitwidth`] (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
                  - selectedItemsStyle: text
                  - selectionMethod: checkbox
            - visible: `true`
            - filterCaptionType: attribute
            - filterAssociationOptionLabelAttr: [Attr: EcoATM_MDM.Brand.DisplayName]
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `'auctions-datagrid-bidrank-display-cell'`
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_MDM.Model.DisplayName]
            - header: Model
            - tooltip: {1}
            ➤ **filter** (Widgets)
              - 🧩 **Drop-down filter** [Class: `auctions-dropdownfilter-fitwidth`] (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
                  - selectedItemsStyle: text
                  - selectionMethod: checkbox
            - visible: `true`
            - filterCaptionType: attribute
            - filterAssociationOptionLabelAttr: [Attr: EcoATM_MDM.Model.DisplayName]
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `'auctions-datagrid-bidrank-display-cell'`
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_MDM.ModelName.DisplayName]
            - header: Model Name
            - tooltip: {1}
            ➤ **filter** (Widgets)
              - 🧩 **Drop-down filter** [Class: `auctions-dropdownfilter-fitwidth`] (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
                  - selectedItemsStyle: text
                  - selectionMethod: checkbox
            - visible: `true`
            - filterCaptionType: attribute
            - filterAssociationOptionLabelAttr: [Attr: EcoATM_MDM.ModelName.DisplayName]
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `'auctions-datagrid-bidrank-display-cell'`
            - showContentAs: attribute
            - attribute: [Attr: AuctionUI.BidData.Merged_Grade]
            - header: Grade
            ➤ **filter** (Widgets)
              - 🧩 **Text filter** [Class: `auctions-filter`] (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                  - defaultFilter: contains
                  - delay: 500
                  - screenReaderInputCaption: Search
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - columnClass: `'auctions-datagrid-bidrank-display-cell auction-grade'`
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_MDM.Carrier.DisplayName]
            - header: Carrier
            ➤ **filter** (Widgets)
              - 🧩 **Drop-down filter** [Class: `auctions-dropdownfilter-fitwidth`] (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
                  - selectedItemsStyle: text
                  - selectionMethod: checkbox
            - visible: `true`
            - filterCaptionType: attribute
            - filterAssociationOptionLabelAttr: [Attr: EcoATM_MDM.Carrier.DisplayName]
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 114
            - size: 1
            - alignment: left
            - columnClass: `'auctions-datagrid-bidrank-display-cell auction-carrier'`
            - showContentAs: attribute
            - attribute: [Attr: AuctionUI.AggregatedInventory.CreatedAt]
            - header: Added
            ➤ **filter** (Widgets)
              - 🧩 **Date filter** [Class: `auctions-filter`] (ID: `com.mendix.widget.web.datagriddatefilter.DatagridDateFilter`)
                  - defaultFilter: greaterEqual
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 102
            - size: 1
            - alignment: left
            - columnClass: `'auctions-datagrid-bidrank-display-cell auction-added'`
            - showContentAs: attribute
            - attribute: [Attr: AuctionUI.BidData.Data_Wipe_Quantity]
            - header: Avail. Qty
            - tooltip: Data Wipe Available Quantity
            ➤ **filter** (Widgets)
              - 🧩 **Number filter** [Class: `auctions-filter`] (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                  - defaultFilter: equal
                  - delay: 500
                  - screenReaderInputCaption: Search
            - visible: `if $dataView3/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe
then true
else false
`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 105
            - size: 1
            - alignment: center
            - columnClass: `'auctions-datagrid-bidrank-display-cell auction-availqty'`
            - showContentAs: attribute
            - attribute: [Attr: AuctionUI.BidData.MaximumQuantity]
            - header: Avail. Qty
            ➤ **filter** (Widgets)
              - 🧩 **Number filter** [Class: `auctions-filter`] (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                  - defaultFilter: equal
                  - delay: 500
                  - screenReaderInputCaption: Search
            - visible: `if $dataView3/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Wholesale
then true
else false`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 105
            - size: 1
            - alignment: center
            - columnClass: `'auctions-datagrid-bidrank-display-cell auction-availqty'`
            - showContentAs: dynamicText
            - attribute: [Attr: AuctionUI.BidData.TargetPrice]
            - dynamicText: ${1}
            - header: Target Price
            ➤ **filter** (Widgets)
              - 🧩 **Number filter** [Class: `auctions-filter`] (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                  - defaultFilter: equal
                  - delay: 500
                  - screenReaderInputCaption: Search
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 114
            - size: 1
            - alignment: center
            - columnClass: `'auctions-datagrid-bidrank-display-cell auction-targetprice'`
            - showContentAs: customContent
            - attribute: [Attr: AuctionUI.BidData.BidAmount]
            ➤ **content** (Widgets)
                ↳ [Change] → **Nanoflow**: `EcoATM_BuyerManagement.OCH_ValidateAndSaveBidData`
            - header: Price
            ➤ **filter** (Widgets)
              - 🧩 **Number filter** [Class: `auctions-filter`] (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                  - defaultFilter: equal
                  - delay: 500
                  - screenReaderInputCaption: Search
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 105
            - size: 1
            - alignment: center
            - columnClass: `'auction-price'`
            - showContentAs: customContent
            - attribute: [Attr: AuctionUI.BidData.BidQuantity]
            ➤ **content** (Widgets)
                ↳ [Change] → **Nanoflow**: `EcoATM_BuyerManagement.OCH_ValidateAndSaveBidData`
            - header: Qty. Cap
            ➤ **filter** (Widgets)
              - 🧩 **Number filter** [Class: `auctions-filter`] (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                  - defaultFilter: equal
                  - delay: 500
                  - screenReaderInputCaption: Search
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 105
            - size: 1
            - alignment: center
            - columnClass: `'auction-qtycap'`
            - showContentAs: dynamicText
            - attribute: [Attr: AuctionUI.BidData.DisplayRound2BidRank]
            - dynamicText: {1}
            - header: Bid Rank
            ➤ **filter** (Widgets)
              - 🧩 **Text filter** [Class: `auctions-filter`] (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                  - defaultFilter: contains
                  - delay: 500
                  - screenReaderInputCaption: Search
            - visible: `true`
            - filterCaptionType: expression
            - hidable: yes
            - width: autoFit
            - minWidth: manual
            - minWidthLimit: 100
            - size: 1
            - alignment: center
            - columnClass: `'auctions-datagrid-bidrank-display-cell auction-bidrank'`
        - pageSize: 20
        - pagination: virtualScrolling
        - showPagingButtons: always
        - pagingPosition: bottom
        - loadMoreButtonCaption: Load More
        - showEmptyPlaceholder: none
        - onClickTrigger: single
        - configurationStorageType: attribute
        ➤ **filtersPlaceholder** (Widgets)
        - exportDialogLabel: Export progress
        - cancelExportLabel: Cancel data export
        - selectRowLabel: Select row
    - 📦 **DataView** [NF: EcoATM_BuyerManagement.Nanoflow] 👁️ (If IsBidDataLoaded is true/false)
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
