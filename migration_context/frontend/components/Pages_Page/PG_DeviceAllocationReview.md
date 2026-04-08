# Page: PG_DeviceAllocationReview

**Allowed Roles:** EcoATM_DA.Administrator, EcoATM_DA.SalesLeader, EcoATM_DA.SalesOps

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📦 **DataView** [Context] [Style: `max-width: 1520px;
min-width: 1000px;
padding: 24px;`]
    ↳ [acti] → **Nanoflow**: `EcoATM_DA.NA_NAV_DALandingPage`
  - 🔤 **Text**: "Total Qty"
  - 🔤 **Text**: "DW Qty"
  - 🔤 **Text**: "Average Payout"
  - 🔤 **Text**: "NonDW-Average Target Price"
  - 🔤 **Text**: "DW Average Target Price"
  - 🔤 **Text**: "EB"
  - 🔤 **Text**: "Sales Qty"
  - 🔤 **Text**: "est. ASP"
  - 🔤 **Text**: "Revenue"
  - 🔤 **Text**: "Margin"
  - 🔤 **Text**: "Margin %"
  - 📦 **DataView** [MF: EcoATM_DA.DS_DeviceAllocation_CalculateClearingBid] [Class: `DAReview-ClearingBid`]
    - 🧩 **Data grid 2** [Class: `DAReview-grid`] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: checkbox
        - itemSelectionMode: clear
        ➤ **columns**
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_DA.DeviceBuyer.BuyerCode]
            - header: Buyer code
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
            - columnClass: `'buyer-code-col'
`
            - filterCaptionType: expression
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_DA.DeviceBuyer.BuyerName]
            - header: Buyer Name
            - visible: `true`
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - filterCaptionType: expression
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_DA.DeviceBuyer.Bid]
            - header: Bid
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
            - columnClass: `'price-col'
`
            - filterCaptionType: expression
            - showContentAs: attribute
            - attribute: [Attr: EcoATM_DA.DeviceBuyer.QtyCap]
            - header: Qty Cap
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
            - attribute: [Attr: EcoATM_DA.DeviceBuyer.AwardedQty]
            - header: Awarded Qty
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
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_DA.DeviceBuyer.Reject]
            ➤ **content** (Widgets)
            - header: Actions
            - visible: `true`
            - hidable: yes
            - width: autoFill
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - filterCaptionType: expression
            - showContentAs: customContent
            - attribute: [Attr: EcoATM_DA.DeviceBuyer.RejectReason]
            ➤ **content** (Widgets)
            - header: Notes:
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
        - rowClass: `if $currentObject/ClearingBid = true then 'DA-review-ClearingRow' else ''
`
        - onClickTrigger: single
        - configurationStorageType: attribute
        - exportDialogLabel: Export progress
        - cancelExportLabel: Cancel data export
        - selectRowLabel: Select row
        - loadingType: spinner
