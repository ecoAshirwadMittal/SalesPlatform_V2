# Snippet: SNIP_Authentication

> Add this snippet to a page that is accessible to an administrator that should set up the Authentication with your app registration(s).

## Widget Tree

- 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: rowClick
    ➤ **columns**
        - showContentAs: customContent
        - attribute: [Attr: MicrosoftGraph.Authentication.DisplayName]
        ➤ **content** (Widgets)
            ↳ [acti] → **Page**: `MicrosoftGraph.Authentication_NewEdit`
        - header: Name
        - visible: `true`
        - hidable: yes
        - width: autoFill
        - size: 1
        - alignment: left
        - minWidth: auto
        - minWidthLimit: 100
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: MicrosoftGraph.Authentication.AppId]
        - header: Application (client) ID
        - visible: `true`
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: MicrosoftGraph.Authentication.AuthorizedUsers]
        - header: Authorized Users
        - visible: `true`
        - hidable: yes
        - width: autoFit
        - minWidth: minContent
        - minWidthLimit: 100
        - size: 1
        - alignment: right
        - filterCaptionType: expression
        - showContentAs: customContent
        - attribute: [Attr: MicrosoftGraph.Authentication.IsActive]
        ➤ **content** (Widgets)
          - 🧩 **Badge** [Dynamic: `if	$currentObject/IsActive
then	'label-success'
else	'label-default'`] (ID: `com.mendix.widget.custom.badge.Badge`)
              - type: badge
              - value: {1}
        - header: Status
        - visible: `true`
        - hidable: yes
        - width: autoFit
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: MicrosoftGraph.Authentication.IsDefault]
        - header: Default
        - visible: `true`
        - hidable: yes
        - width: autoFit
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: System.User.Name]
        - header: Created By
        - visible: `true`
        - hidable: yes
        - width: autoFit
        - minWidth: minContent
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: customContent
        - attribute: [Attr: MicrosoftGraph.Authentication.DisplayName]
        ➤ **content** (Widgets)
            ↳ [acti] → **Page**: `MicrosoftGraph.Authentication_NewEdit`
            ↳ [acti] → **Delete**
        - visible: `true`
        - hidable: yes
        - width: autoFit
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: right
        - filterCaptionType: expression
    - pageSize: 20
    - pagination: buttons
    - pagingPosition: bottom
    - showEmptyPlaceholder: custom
    ➤ **emptyPlaceholder** (Widgets)
        ↳ [acti] → **Page**: `MicrosoftGraph.Authentication_NewEdit`
    ➤ **filtersPlaceholder** (Widgets)
        ↳ [acti] → **Page**: `MicrosoftGraph.Authentication_NewEdit`
        ↳ [acti] → **Nanoflow**: `MicrosoftGraph.ACT_Authorization_Explore`
        ↳ [acti] → **Nanoflow**: `MicrosoftGraph.ACT_Authentication_RequestAuthorization`
        ↳ [acti] → **Nanoflow**: `MicrosoftGraph.ACT_Authentication_RequestAdminAuthorization`
        ↳ [acti] → **Nanoflow**: `MicrosoftGraph.ACT_Authentication_ToggleActive`
        ↳ [acti] → **Nanoflow**: `MicrosoftGraph.ACT_Authentication_SetDefault`
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
    - showPagingButtons: auto
    - onClickTrigger: single
    - itemSelectionMode: clear
    - loadMoreButtonCaption: Load More
    - configurationStorageType: attribute
    - loadingType: spinner
