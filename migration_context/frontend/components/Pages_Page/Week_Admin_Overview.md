# Page: Week_Admin_Overview

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: checkbox
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_MDM.Week.WeekID]
        - header: Week ID
        ➤ **filter** (Widgets)
          - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
              - defaultFilter: equal
              - delay: 500
        - hidable: yes
        - width: autoFill
        - size: 1
        - alignment: right
        - visible: `true`
        - minWidth: auto
        - minWidthLimit: 100
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_MDM.Week.WeekNumber]
        - header: Week number
        ➤ **filter** (Widgets)
          - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
              - defaultFilter: equal
              - delay: 500
        - hidable: yes
        - width: autoFill
        - size: 1
        - alignment: right
        - visible: `true`
        - minWidth: auto
        - minWidthLimit: 100
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_MDM.Week.WeekStartDateTime]
        - header: Week start date time
        ➤ **filter** (Widgets)
          - 🧩 **Date filter** (ID: `com.mendix.widget.web.datagriddatefilter.DatagridDateFilter`)
              - defaultFilter: equal
        - hidable: yes
        - width: autoFill
        - size: 1
        - alignment: left
        - visible: `true`
        - minWidth: auto
        - minWidthLimit: 100
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_MDM.Week.WeekEndDateTime]
        - header: Week end date time
        ➤ **filter** (Widgets)
          - 🧩 **Date filter** (ID: `com.mendix.widget.web.datagriddatefilter.DatagridDateFilter`)
              - defaultFilter: equal
        - hidable: yes
        - width: autoFill
        - size: 1
        - alignment: left
        - visible: `true`
        - minWidth: auto
        - minWidthLimit: 100
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_MDM.Week.WeekDisplay]
        - header: Week display
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
        - attribute: [Attr: EcoATM_MDM.Week.Year]
        - header: Year
        ➤ **filter** (Widgets)
          - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
              - defaultFilter: equal
              - delay: 500
        - hidable: yes
        - width: autoFill
        - size: 1
        - alignment: right
        - visible: `true`
        - minWidth: auto
        - minWidthLimit: 100
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_MDM.Week.createdDate]
        - header: createdDate
        - visible: `true`
        - filterCaptionType: expression
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: attribute
        - attribute: [Attr: System.User.Name]
        - header: Name
        - visible: `true`
        - filterCaptionType: expression
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_MDM.Week.changedDate]
        - header: changedDate
        - visible: `true`
        - filterCaptionType: expression
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: attribute
        - attribute: [Attr: System.User.Name]
        - header: Name
        - visible: `true`
        - filterCaptionType: expression
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: attribute
        - attribute: [Attr: EcoATM_MDM.Week.AuctionDataPurged]
        - header: Auctions Data Purged
        ➤ **filter** (Widgets)
          - 🧩 **Drop-down filter** (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
              - selectedItemsStyle: text
              - selectionMethod: checkbox
        - visible: `true`
        - filterCaptionType: expression
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - showContentAs: customContent
        - attribute: [Attr: EcoATM_MDM.Week.WeekID]
        ➤ **content** (Widgets)
            ↳ [acti] → **Page**: `AuctionUI.Week_Admin_NewEdit`
            ↳ [acti] → **Microflow**: `AuctionUI.ACT_CleanupDataForAWeek`
        - hidable: no
        - width: autoFit
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
    ➤ **filtersPlaceholder** (Widgets)
        ↳ [acti] → **Page**: `AuctionUI.Week_Admin_NewEdit`
        ↳ [acti] → **Microflow**: `AuctionUI.ACT_SendWeekDataToSnowflake`
        ↳ [acti] → **Nanoflow**: `AuctionUI.NAN_Generic_ExportToExcel`
        ↳ [acti] → **Microflow**: `AuctionUI.SCE_WeeklyPurgeJobForAuctions`
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - showPagingButtons: always
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
