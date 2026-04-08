# Page: Attribute_Select

**Allowed Roles:** DeepLink.Admin

**Layout:** `DeepLink.ModalPopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 🔤 **Text**: "The attribute that will be used by the deeplink to uniquely identify the object that needs to be passed to the microflow."
  - 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: checkbox
      ➤ **columns**
          - showContentAs: attribute
          - attribute: [Attr: DeepLink.Attribute.Name]
          - header: Name
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
