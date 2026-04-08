# Page: Templates_Overview

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
        - attribute: [Attr: ExcelImporter.Template.Status]
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 5
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: ExcelImporter.Template.Nr]
        - header: Nr
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 7
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: ExcelImporter.Template.Title]
        - header: Title
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 38
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: ExcelImporter.Template.Description]
        - header: Description
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 50
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: customContent
        - attribute: [Attr: ExcelImporter.Template.Status]
        ➤ **content** (Widgets)
          - ⚡ **Button**: Edit [Style: Default]
            ↳ [acti] → **Page**: `ExcelImporter.Template_Edit`
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
        ↳ [acti] → **Page**: `ExcelImporter.Template_Edit`
      - ⚡ **Button**: New template by Excel file [Style: Success]
        ↳ [acti] → **Microflow**: `ExcelImporter.IVK_Template_NewFromFile`
      - ⚡ **Button**: Duplicate [Style: Default]
        ↳ [acti] → **Microflow**: `ExcelImporter.IVK_DuplicateTemplate`
      - ⚡ **Button**: Export template [Style: Default]
        ↳ [acti] → **Microflow**: `ExcelImporter.ExcelTemplate_ExportToXML`
      - ⚡ **Button**: Import template [Style: Default]
        ↳ [acti] → **Microflow**: `ExcelImporter.IVK_ImportXML_Upload`
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
