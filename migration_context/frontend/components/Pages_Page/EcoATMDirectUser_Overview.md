# Page: EcoATMDirectUser_Overview

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 🧩 **Active Menu Selector** (ID: `mendix.activemenuselector.ActiveMenuSelector`)
    - menuWidgetName: navigationTree3
    - menuItemTitle: Users
  ↳ [acti] → **Microflow**: `AuctionUI.ACT_CreateNewUser`
- 🧩 **Data grid 2** [Class: `datagridfilter user-management-grid`] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: checkbox
    ➤ **columns**
        - showContentAs: customContent
        - attribute: [Attr: Administration.Account.FullName]
        ➤ **content** (Widgets)
        - header: Name
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** [DP: {Spacing left: Outer none, Align self: Left}] (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
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
        - attribute: [Attr: EcoATM_UserManagement.EcoATMDirectUser.UserBuyerDisplay]
        - header: Buyer
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** [DP: {Spacing left: Outer none, Align self: Left}] (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
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
        - attribute: [Attr: EcoATM_UserManagement.EcoATMDirectUser.UserRolesDisplay]
        - header: Roles
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** [DP: {Align self: Left, Spacing left: Outer none}] (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
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
        - attribute: [Attr: Administration.Account.Email]
        - header: Email
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** [DP: {Align self: Left, Spacing left: Outer none}] (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
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
        - attribute: [Attr: EcoATM_UserManagement.EcoATMDirectUser.OverallUserStatus]
        ➤ **content** (Widgets)
          - 🖼️ **Image**: active_rollover 👁️ (If OverallUserStatus is Active/Inactive/Disabled/(empty))
          - 🖼️ **Image**: Inactive 👁️ (If OverallUserStatus is Active/Inactive/Disabled/(empty))
          - 🖼️ **Image**: Disabled 👁️ (If OverallUserStatus is Active/Inactive/Disabled/(empty))
            ↳ [acti] → **Microflow**: `AuctionUI.ACT_ShowUserEditPage`
        - header: Status
        ➤ **filter** (Widgets)
          - 🧩 **Drop-down filter** [DP: {Align self: Left, Spacing left: Outer none}] (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
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
        - showContentAs: customContent
        - attribute: [Attr: EcoATM_UserManagement.EcoATMDirectUser.SubmissionID]
        - visible: `true`
        - hidable: no
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 0
        - alignment: left
        - columnClass: `'ColumnPickerCell'`
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
