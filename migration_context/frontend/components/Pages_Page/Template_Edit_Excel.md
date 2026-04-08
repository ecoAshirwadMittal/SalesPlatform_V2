# Page: Template_Edit_Excel

**Allowed Roles:** XLSReport.Configurator

**Layout:** `Atlas_Core.Atlas_TopBar`

## Widget Tree

- 📦 **DataView** [Context]
  - 🔤 **Text**: "Template ID"
  - 🔤 **Text**: "Document type"
  - 🧩 **Combo box** (ID: `com.mendix.widget.web.combobox.Combobox`)
      - source: context
      - optionsSourceType: enumeration
      - attributeEnumeration: [Attr: XLSReport.MxTemplate.DocumentType]
      - optionsSourceAssociationCaptionType: attribute
      - optionsSourceDatabaseCaptionType: attribute
      - filterType: contains
      - optionsSourceAssociationCustomContentType: no
      - optionsSourceDatabaseCustomContentType: no
      - staticDataSourceCustomContentType: no
      - selectionMethod: checkbox
      - selectedItemsStyle: text
      - selectAllButtonCaption: Select all
      - readOnlyStyle: text
      - ariaRequired: `false`
      - clearButtonAriaLabel: Clear selection
      - removeValueAriaLabel: Remove value
      - a11ySelectedValue: Selected value:
      - a11yOptionsAvailable: Number of options available:
      - a11yInstructions: Use up and down arrow keys to navigate. Press Enter or Space Bar keys to select.
      - loadingType: spinner
      - selectedItemsSorting: none
  - 🔤 **Text**: "Filename"
  - 🔤 **Text**: "Input object"
  - 🧩 **Combo box** (ID: `com.mendix.widget.web.combobox.Combobox`)
      - source: context
      - optionsSourceType: association
      - optionsSourceAssociationCaptionType: attribute
      - optionsSourceDatabaseCaptionType: attribute
      - optionsSourceAssociationCaptionAttribute: [Attr: MxModelReflection.MxObjectType.CompleteName]
      - filterType: contains
      - optionsSourceAssociationCustomContentType: no
      - optionsSourceDatabaseCustomContentType: no
      - staticDataSourceCustomContentType: no
      - selectionMethod: rowclick
      - selectedItemsStyle: text
      - selectAllButtonCaption: Select all
      - readOnlyStyle: text
      - ariaRequired: `false`
      - clearButtonAriaLabel: Clear selection
      - removeValueAriaLabel: Remove value
      - a11ySelectedValue: Selected value:
      - a11yOptionsAvailable: Number of options available:
      - a11yInstructions: Use up and down arrow keys to navigate. Press Enter or Space Bar keys to select.
      - loadingType: spinner
      - selectedItemsSorting: none
  - 🔤 **Text**: "Desciption"
  - 🔤 **Text**: "Date time export format"
  - 🧩 **Combo box** (ID: `com.mendix.widget.web.combobox.Combobox`)
      - source: context
      - optionsSourceType: enumeration
      - attributeEnumeration: [Attr: XLSReport.MxTemplate.DateTimePresentation]
      - optionsSourceAssociationCaptionType: attribute
      - optionsSourceDatabaseCaptionType: attribute
      - filterType: contains
      - optionsSourceAssociationCustomContentType: no
      - optionsSourceDatabaseCustomContentType: no
      - staticDataSourceCustomContentType: no
      - selectionMethod: checkbox
      - selectedItemsStyle: text
      - selectAllButtonCaption: Select all
      - readOnlyStyle: text
      - ariaRequired: `false`
      - clearButtonAriaLabel: Clear selection
      - removeValueAriaLabel: Remove value
      - a11ySelectedValue: Selected value:
      - a11yOptionsAvailable: Number of options available:
      - a11yInstructions: Use up and down arrow keys to navigate. Press Enter or Space Bar keys to select.
      - loadingType: spinner
      - selectedItemsSorting: none
  - 🔤 **Text**: "Upload existing excel file"
  - ⚡ **Button**: microflowButton [Style: Default]
    ↳ [acti] → **Microflow**: `XLSReport.IVK_UploadExcistFile`
  - ⚡ **Button**: microflowButton2 [Style: Default]
    ↳ [acti] → **Microflow**: `XLSReport.IVK_DeleteExcistFile`
  - 🔤 **Text**: "Custom date format"
  - 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: rowClick
      - itemSelectionMode: clear
      - loadingType: spinner
      ➤ **columns**
          - showContentAs: customContent
          - attribute: [Attr: XLSReport.MxSheet.Status]
          ➤ **content** (Widgets)
            - 🖼️ **Image**: Completed 👁️ (If Status is Yes/No/(empty))
            - 🖼️ **Image**: Cancel 👁️ (If Status is Yes/No/(empty))
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
          - attribute: [Attr: XLSReport.MxSheet.Sequence]
          - header: Sequence
          - visible: `true`
          - hidable: yes
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - filterCaptionType: expression
          - showContentAs: attribute
          - attribute: [Attr: XLSReport.MxSheet.Name]
          - header: Name
          - visible: `true`
          - hidable: yes
          - width: autoFit
          - minWidth: minContent
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - filterCaptionType: expression
      - pageSize: 10
      - pagination: buttons
      - pagingPosition: bottom
      - showPagingButtons: always
      - loadMoreButtonCaption: Load More
      - showEmptyPlaceholder: none
      - onClickTrigger: double
      - configurationStorageType: attribute
      ➤ **filtersPlaceholder** (Widgets)
        - ⚡ **Button**: New [Style: Default]
          ↳ [acti] → **Microflow**: `XLSReport.IVK_CreateNewSheet`
          ↳ [acti] → **Page**: `XLSReport.MxSheet_NewEdit`
          ↳ [acti] → **Delete**
      - exportDialogLabel: Export progress
      - cancelExportLabel: Cancel data export
      - selectRowLabel: Select row
  - 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: rowClick
      - itemSelectionMode: clear
      - loadingType: spinner
      ➤ **columns**
          - showContentAs: attribute
          - attribute: [Attr: XLSReport.MxCellStyle.Name]
          - header: Style name
          - visible: `true`
          - hidable: yes
          - width: manual
          - minWidth: auto
          - minWidthLimit: 100
          - size: 100
          - alignment: left
          - filterCaptionType: expression
      - pageSize: 10
      - pagination: buttons
      - pagingPosition: bottom
      - showPagingButtons: always
      - loadMoreButtonCaption: Load More
      - showEmptyPlaceholder: none
      - onClickTrigger: double
      - configurationStorageType: attribute
      ➤ **filtersPlaceholder** (Widgets)
          ↳ [acti] → **Page**: `XLSReport.CellStyle_NewEdit`
          ↳ [acti] → **Page**: `XLSReport.CellStyle_NewEdit`
          ↳ [acti] → **Delete**
      - exportDialogLabel: Export progress
      - cancelExportLabel: Cancel data export
      - selectRowLabel: Select row
    ↳ [acti] → **Microflow**: `XLSReport.ACT_Template_Edit_Excel`
    ↳ [acti] → **Cancel Changes**
