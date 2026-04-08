# Page: PG_RoundThreeBidsReportByBuyer

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📦 **DataView** [Context] 👁️ (If: `$AuctionsFeature/LegacyRoundThree`)
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
            - attribute: [Attr: AuctionUI.RoundThreeBuyersDataReport.CompanyName]
            - header: Company name
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
            - attribute: [Attr: AuctionUI.RoundThreeBuyersDataReport.BuyerCodes]
            - header: Buyer codes
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
            - attribute: [Attr: AuctionUI.RoundThreeBuyersDataReport.SubmittedBy]
            - header: Submitted by
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
            - showContentAs: dynamicText
            - attribute: [Attr: AuctionUI.RoundThreeBuyersDataReport.SubmittedOn]
            - dynamicText: {1}
            - header: Submitted on
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
            - attribute: [Attr: AuctionUI.RoundThreeBuyersDataReport.CompanyName]
            ➤ **content** (Widgets)
                ↳ [acti] → **Page**: `AuctionUI.PG_RoundThreeBidRoundDataByBuyer`
                ↳ [acti] → **Microflow**: `AuctionUI.ACT_DownloadRound3ValidBidsForBuyer`
                ↳ [acti] → **Microflow**: `AuctionUI.ACT_OpenRound3BidUploadPage`
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
        - pagingPosition: bottom
        - showPagingButtons: always
        - loadMoreButtonCaption: Load More
        - showEmptyPlaceholder: none
        - onClickTrigger: single
        - configurationStorageType: attribute
        ➤ **filtersPlaceholder** (Widgets)
            ↳ [acti] → **Nanoflow**: `AuctionUI.NF_RoundAdminExportToExcel`
            ↳ [acti] → **Microflow**: `AuctionUI.ACT_Generate_RoundThreeQualifiedBuyersReport`
            ↳ [acti] → **Microflow**: `AuctionUI.SUB_CalculateRound3TargetPrice_Admin`
        - exportDialogLabel: Export progress
        - cancelExportLabel: Cancel data export
        - selectRowLabel: Select row
        - loadingType: spinner
- 📦 **DataView** [NF: AuctionUI.DS_CurrentPageName]
  - 🧩 **Microflow Timer** (ID: `MicroflowTimer.widget.MicroflowTimer`)
      - interval: 100
      - callEvent: callNanoflow
