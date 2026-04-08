# Page: Group_View

**Allowed Roles:** MicrosoftGraph.Administrator

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
    ↳ [acti] → **OpenLink**
    ↳ [acti] → **Microflow**: `MicrosoftGraph.ACT_Group_Delete`
  - ⚡ **Button**: radioButtons1
  - 📝 **DatePicker**: datePicker2
  - ⚡ **Button**: radioButtons2
  - 📑 **TabContainer**
    - 📑 **Tab**: "General"
      - 📦 **DataView** [Context]
        - 🧩 **Gallery** (ID: `com.mendix.widget.web.gallery.Gallery`)
            ➤ **content** (Widgets)
                ↳ [acti] → **OpenLink**
            - desktopItems: 1
            - tabletItems: 1
            - phoneItems: 1
            - pageSize: 20
            - pagination: virtualScrolling
            - pagingPosition: below
            - showEmptyPlaceholder: none
            - itemSelectionMode: clear
            - onClickTrigger: single
      - 📝 **DatePicker**: datePicker1
    - 📑 **Tab**: "Membership"
      - 📑 **TabContainer**
        - 📑 **Tab**: "Owners"
          - 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
              - refreshInterval: 0
              - itemSelectionMethod: rowClick
              ➤ **columns**
                  - showContentAs: attribute
                  - attribute: [Attr: MicrosoftGraph.User.DisplayName]
                  - header: Display Name
                  - visible: `true`
                  - hidable: yes
                  - width: autoFill
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - filterCaptionType: expression
                  - showContentAs: attribute
                  - attribute: [Attr: MicrosoftGraph.User.Mail]
                  - header: Mail
                  - visible: `true`
                  - hidable: yes
                  - width: autoFill
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - filterCaptionType: expression
              - pageSize: 20
              - pagination: virtualScrolling
              - pagingPosition: bottom
              - showPagingButtons: always
              - showEmptyPlaceholder: none
              - onClickTrigger: single
              ➤ **filterList**
                  - filter: [Attr: MicrosoftGraph.User.DisplayName]
              ➤ **filtersPlaceholder** (Widgets)
                  ↳ [acti] → **Page**: `MicrosoftGraph.User_View`
                - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                    - defaultFilter: contains
                    - placeholder: Search
                    - delay: 500
              - exportDialogLabel: Export progress
              - cancelExportLabel: Cancel data export
              - selectRowLabel: Select row
              - itemSelectionMode: clear
              - loadMoreButtonCaption: Load More
              - configurationStorageType: attribute
              - loadingType: spinner
        - 📑 **Tab**: "Members"
          - 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
              - refreshInterval: 0
              - itemSelectionMethod: rowClick
              ➤ **columns**
                  - showContentAs: attribute
                  - attribute: [Attr: MicrosoftGraph.User.DisplayName]
                  - header: Display Name
                  - visible: `true`
                  - hidable: yes
                  - width: autoFill
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - filterCaptionType: expression
                  - showContentAs: attribute
                  - attribute: [Attr: MicrosoftGraph.User.Mail]
                  - header: Mail
                  - visible: `true`
                  - hidable: yes
                  - width: autoFill
                  - minWidth: auto
                  - minWidthLimit: 100
                  - size: 1
                  - alignment: left
                  - filterCaptionType: expression
              - pageSize: 20
              - pagination: virtualScrolling
              - pagingPosition: bottom
              - showPagingButtons: always
              - showEmptyPlaceholder: none
              - onClickTrigger: single
              ➤ **filterList**
                  - filter: [Attr: MicrosoftGraph.User.DisplayName]
              ➤ **filtersPlaceholder** (Widgets)
                  ↳ [acti] → **Page**: `MicrosoftGraph.User_View`
                - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
                    - defaultFilter: contains
                    - placeholder: Search
                    - delay: 500
              - exportDialogLabel: Export progress
              - cancelExportLabel: Cancel data export
              - selectRowLabel: Select row
              - itemSelectionMode: clear
              - loadMoreButtonCaption: Load More
              - configurationStorageType: attribute
              - loadingType: spinner
        - 📑 **Tab**: "About membership and permissions"
            ↳ [acti] → **OpenLink**
    - 📑 **Tab**: "Settings"
      - 📝 **CheckBox**: checkBox2
      - ⚡ **Button**: radioButtons3
      - 📝 **CheckBox**: checkBox1
