# Page: ExcelImportOverview

**Allowed Roles:** ExcelImporter.Configurator

**Layout:** `Atlas_Core.Atlas_Default`

## Widget Tree

- 📑 **TabContainer**
  - 📑 **Tab**: "Normal templates"
    - 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
        - refreshInterval: 0
        - itemSelectionMethod: rowClick
        - itemSelectionMode: clear
        - loadingType: spinner
        ➤ **columns**
            - showContentAs: customContent
            - attribute: [Attr: ExcelImporter.Template.Status]
            ➤ **content** (Widgets)
              - 🧩 **Image** [DP: {Image style: Rounded}] 👁️ (If Status is INFO/VALID/INVALID/(empty)) (ID: `com.mendix.widget.web.image.Image`)
                  - datasource: icon
                  - onClickType: action
                  - widthUnit: auto
                  - width: 100
                  - heightUnit: auto
                  - height: 100
                  - iconSize: 14
                  - displayAs: fullImage
              - 🧩 **Image** 👁️ (If Status is INFO/VALID/INVALID/(empty)) (ID: `com.mendix.widget.web.image.Image`)
                  - datasource: icon
                  - onClickType: action
                  - widthUnit: auto
                  - width: 100
                  - heightUnit: auto
                  - height: 100
                  - iconSize: 14
                  - displayAs: fullImage
            - visible: `true`
            - hidable: no
            - width: autoFit
            - minWidth: auto
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - filterCaptionType: expression
            - showContentAs: attribute
            - attribute: [Attr: ExcelImporter.Template.Nr]
            - header: Nr
            - visible: `true`
            - hidable: yes
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
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
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
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
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - filterCaptionType: expression
            - showContentAs: customContent
            - attribute: [Attr: ExcelImporter.Template.Status]
            ➤ **content** (Widgets)
              - ⚡ **Button**: Edit [Style: Default]
                ↳ [acti] → **Page**: `ExcelImporter.Template_Edit`
              - ⚡ **Button**: Delete [Style: Danger]
                ↳ [acti] → **Delete**
            - header: Actions
            - visible: `true`
            - hidable: no
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
        ➤ **filtersPlaceholder** (Widgets)
            ↳ [acti] → **Page**: `ExcelImporter.Template_New`
          - ⚡ **Button**: New template from Excel file [Style: Success]
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
  - 📑 **Tab**: "Import files"
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
            - width: autoFit
            - minWidth: minContent
            - minWidthLimit: 100
            - size: 1
            - alignment: left
            - filterCaptionType: expression
            - showContentAs: attribute
            - attribute: [Attr: ExcelImporter.Template.Nr]
            - header: Template nr
            ➤ **filter** (Widgets)
              - 🧩 **Number filter** (ID: `com.mendix.widget.web.datagridnumberfilter.DatagridNumberFilter`)
                  - defaultFilter: equal
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
            - attribute: [Attr: ExcelImporter.Template.Title]
            - header: Template name
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
            - showContentAs: customContent
            - attribute: [Attr: System.FileDocument.Name]
            ➤ **content** (Widgets)
              - ⚡ **Button**: Edit [Style: Default]
                ↳ [acti] → **Page**: `ExcelImporter.TemplateDocument_NewEdit`
              - ⚡ **Button**: Delete [Style: Danger]
                ↳ [acti] → **Delete**
            - header: Actions
            - visible: `true`
            - hidable: no
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
        ➤ **filtersPlaceholder** (Widgets)
            ↳ [acti] → **Page**: `ExcelImporter.TemplateDocument_NewEdit`
          - ⚡ **Button**: Import file [Style: Default]
            ↳ [acti] → **Microflow**: `ExcelImporter.IVK_ImportTemplateDocument`
        - exportDialogLabel: Export progress
        - cancelExportLabel: Cancel data export
        - selectRowLabel: Select row
