# Page: MxObjectReference_Select

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: rowClick
    - itemSelectionMode: clear
    - loadingType: spinner
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: MxModelReflection.MxObjectReference.CompleteName]
        - header: MxObject reference
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: autoFit
        - minWidth: minContent
        - minWidthLimit: 100
        - size: 1
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: MxModelReflection.MxObjectReference.ReferenceType]
        - header: Reference type
        - visible: `true`
        - hidable: yes
        - width: autoFit
        - minWidth: minContent
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
    - onClickTrigger: double
    - configurationStorageType: attribute
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
