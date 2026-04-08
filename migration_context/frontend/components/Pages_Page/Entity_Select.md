# Page: Entity_Select

**Layout:** `DeepLink.ModalPopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: checkbox
      ➤ **columns**
          - showContentAs: attribute
          - attribute: [Attr: DeepLink.Entity.Name]
          - header: Name
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
      - pageSize: 20
      - pagination: buttons
      - pagingPosition: bottom
      - showEmptyPlaceholder: none
      - exportDialogLabel: Export progress
      - cancelExportLabel: Cancel data export
      - showPagingButtons: always
      - onClickTrigger: single
      - selectRowLabel: Select row
      - itemSelectionMode: clear
      - loadMoreButtonCaption: Load More
      - configurationStorageType: attribute
      - loadingType: spinner
