# Page: ReferenceHandling_NewEdit

**Allowed Roles:** ExcelImporter.Configurator

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 🧩 **Combo box** (ID: `com.mendix.widget.web.combobox.Combobox`)
      - source: context
      - optionsSourceType: association
      - optionsSourceAssociationCaptionType: attribute
      - optionsSourceDatabaseCaptionType: attribute
      - optionsSourceAssociationCaptionAttribute: [Attr: MxModelReflection.MxObjectReference.CompleteName]
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
  - 🧩 **Combo box** (ID: `com.mendix.widget.web.combobox.Combobox`)
      - source: context
      - optionsSourceType: enumeration
      - attributeEnumeration: [Attr: ExcelImporter.ReferenceHandling.Handling]
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
  - 🧩 **Combo box** (ID: `com.mendix.widget.web.combobox.Combobox`)
      - source: context
      - optionsSourceType: enumeration
      - attributeEnumeration: [Attr: ExcelImporter.ReferenceHandling.DataHandling]
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
  - 📝 **CheckBox**: checkBox2
  - 📝 **CheckBox**: checkBox1
  - 📝 **CheckBox**: checkBox
    ↳ [acti] → **Save Changes**
    ↳ [acti] → **Cancel Changes**
