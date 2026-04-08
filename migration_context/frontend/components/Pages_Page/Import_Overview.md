# Page: Import_Overview

**Allowed Roles:** ExcelImporter.Configurator

**Layout:** `Atlas_Core.Atlas_Default`

## Widget Tree

- 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: rowClick
    - itemSelectionMode: clear
    - loadingType: spinner
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: System.FileDocument.Name]
        - header: Filename
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 39
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: ExcelImporter.Template.Nr]
        - header: Template number
        ➤ **filter** (Widgets)
          - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
              - defaultFilter: equal
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 11
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: ExcelImporter.Template.Title]
        - header: Template name
        ➤ **filter** (Widgets)
          - 🧩 **Drop-down filter** (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
              - multiSelect: False
              - selectedItemsStyle: text
              - selectionMethod: checkbox
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 50
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: customContent
        - attribute: [Attr: System.FileDocument.Name]
        ➤ **content** (Widgets)
          - ⚡ **Button**: Edit [Style: Default]
            ↳ [acti] → **Page**: `ExcelImporter.TemplateDocument_NewEdit`
          - ⚡ **Button**: Delete [Style: Danger]
            ↳ [acti] → **Delete**
        - visible: `true`
        - hidable: no
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 20
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
    ➤ **filtersPlaceholder** (Widgets)
        ↳ [acti] → **Page**: `ExcelImporter.TemplateDocument_NewEdit`
      - ⚡ **Button**: Import file [Style: Default]
        ↳ [acti] → **Microflow**: `ExcelImporter.IVK_ImportTemplateDocument`
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
