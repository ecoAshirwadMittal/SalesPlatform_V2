# Snippet: SNIP_Metadata

## Widget Tree

- 📂 **GroupBox**: "Metadata" [DP: {Callout style: [object Object]}] 👁️ (If: `$Authentication/Tenant_Id != empty`)
  - ⚡ **Button**: radioButtons2
  - ⚡ **Button**: radioButtons3
  - ⚡ **Button**: radioButtons4
  - 📦 **DataView** [Context]
    - 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: checkbox
        ➤ **columns**
            - showContentAs: attribute
            - attribute: [Attr: MicrosoftGraph.StringArrayWrapper.Value]
            - header: Value
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
        - showPagingButtons: auto
        - showEmptyPlaceholder: none
        - onClickTrigger: single
        ➤ **filtersPlaceholder** (Widgets)
            ↳ [acti] → **Nanoflow**: `MicrosoftGraph.ACT_StringArrayWrapper_Create`
        - exportDialogLabel: Export progress
        - cancelExportLabel: Cancel data export
        - selectRowLabel: Select row
        - itemSelectionMode: clear
        - loadMoreButtonCaption: Load More
        - configurationStorageType: attribute
        - loadingType: spinner
  - 📦 **DataView** [Context]
    - 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: checkbox
        ➤ **columns**
            - showContentAs: attribute
            - attribute: [Attr: MicrosoftGraph.StringArrayWrapper.Value]
            - header: Value
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
        - showPagingButtons: auto
        - showEmptyPlaceholder: none
        - onClickTrigger: single
        ➤ **filtersPlaceholder** (Widgets)
            ↳ [acti] → **Nanoflow**: `MicrosoftGraph.ACT_StringArrayWrapper_Create`
        - exportDialogLabel: Export progress
        - cancelExportLabel: Cancel data export
        - selectRowLabel: Select row
        - itemSelectionMode: clear
        - loadMoreButtonCaption: Load More
        - configurationStorageType: attribute
        - loadingType: spinner
  - 📦 **DataView** [Context]
    - 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: checkbox
        ➤ **columns**
            - showContentAs: attribute
            - attribute: [Attr: MicrosoftGraph.StringArrayWrapper.Value]
            - header: Value
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
        - showPagingButtons: auto
        - showEmptyPlaceholder: none
        - onClickTrigger: single
        ➤ **filtersPlaceholder** (Widgets)
            ↳ [acti] → **Nanoflow**: `MicrosoftGraph.ACT_StringArrayWrapper_Create`
        - exportDialogLabel: Export progress
        - cancelExportLabel: Cancel data export
        - selectRowLabel: Select row
        - itemSelectionMode: clear
        - loadMoreButtonCaption: Load More
        - configurationStorageType: attribute
        - loadingType: spinner
  - 📦 **DataView** [Context]
    - 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: checkbox
        ➤ **columns**
            - showContentAs: attribute
            - attribute: [Attr: MicrosoftGraph.StringArrayWrapper.Value]
            - header: Value
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
        - showPagingButtons: auto
        - showEmptyPlaceholder: none
        - onClickTrigger: single
        ➤ **filtersPlaceholder** (Widgets)
            ↳ [acti] → **Nanoflow**: `MicrosoftGraph.ACT_StringArrayWrapper_Create`
        - exportDialogLabel: Export progress
        - cancelExportLabel: Cancel data export
        - selectRowLabel: Select row
        - itemSelectionMode: clear
        - loadMoreButtonCaption: Load More
        - configurationStorageType: attribute
        - loadingType: spinner
  - 📦 **DataView** [Context]
    - 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: checkbox
        ➤ **columns**
            - showContentAs: attribute
            - attribute: [Attr: MicrosoftGraph.StringArrayWrapper.Value]
            - header: Value
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
        - showPagingButtons: auto
        - showEmptyPlaceholder: none
        - onClickTrigger: single
        ➤ **filtersPlaceholder** (Widgets)
            ↳ [acti] → **Nanoflow**: `MicrosoftGraph.ACT_StringArrayWrapper_Create`
        - exportDialogLabel: Export progress
        - cancelExportLabel: Cancel data export
        - selectRowLabel: Select row
        - itemSelectionMode: clear
        - loadMoreButtonCaption: Load More
        - configurationStorageType: attribute
        - loadingType: spinner
  - 📦 **DataView** [Context]
    - 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: checkbox
        ➤ **columns**
            - showContentAs: attribute
            - attribute: [Attr: MicrosoftGraph.StringArrayWrapper.Value]
            - header: Value
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
        - showPagingButtons: auto
        - showEmptyPlaceholder: none
        - onClickTrigger: single
        ➤ **filtersPlaceholder** (Widgets)
            ↳ [acti] → **Nanoflow**: `MicrosoftGraph.ACT_StringArrayWrapper_Create`
        - exportDialogLabel: Export progress
        - cancelExportLabel: Cancel data export
        - selectRowLabel: Select row
        - itemSelectionMode: clear
        - loadMoreButtonCaption: Load More
        - configurationStorageType: attribute
        - loadingType: spinner
  - 📦 **DataView** [Context]
    - 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: checkbox
        ➤ **columns**
            - showContentAs: attribute
            - attribute: [Attr: MicrosoftGraph.StringArrayWrapper.Value]
            - header: Value
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
        - showPagingButtons: auto
        - showEmptyPlaceholder: none
        - onClickTrigger: single
        ➤ **filtersPlaceholder** (Widgets)
            ↳ [acti] → **Nanoflow**: `MicrosoftGraph.ACT_StringArrayWrapper_Create`
        - exportDialogLabel: Export progress
        - cancelExportLabel: Cancel data export
        - selectRowLabel: Select row
        - itemSelectionMode: clear
        - loadMoreButtonCaption: Load More
        - configurationStorageType: attribute
        - loadingType: spinner
