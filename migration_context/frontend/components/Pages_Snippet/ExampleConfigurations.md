# Snippet: ExampleConfigurations

## Widget Tree

- 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: rowClick
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: Encryption.ExampleConfiguration.Title]
        - header: Title
        - visible: `true`
        - hidable: yes
        - width: autoFill
        - minWidth: auto
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: Encryption.ExampleConfiguration.Password]
        - header: Password
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
    ➤ **filtersPlaceholder** (Widgets)
        ↳ [acti] → **Page**: `Encryption.ChangePasswordExample_NewEdit`
        ↳ [acti] → **Page**: `Encryption.ChangePasswordExample_NewEdit`
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
    - itemSelectionMode: clear
    - loadingType: spinner
    - loadMoreButtonCaption: Load More
    - configurationStorageType: attribute
