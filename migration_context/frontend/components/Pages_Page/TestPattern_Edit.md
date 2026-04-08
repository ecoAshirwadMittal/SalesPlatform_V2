# Page: TestPattern_Edit

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 🔤 **Text**: "* Don't forget to copy your pattern before closing this popup. This page is just for testing the potential outcome of the pattern you try to use." [Style: `font-size: 80%; font-style: italic;`]
  - 🧩 **Combo box** (ID: `com.mendix.widget.web.combobox.Combobox`)
      - source: context
      - optionsSourceType: enumeration
      - attributeEnumeration: [Attr: MxModelReflection.TestPattern.AttributeTypeEnum]
      - optionsSourceDatabaseCaptionType: attribute
      - optionsSourceAssociationCaptionType: attribute
      - filterType: contains
      - optionsSourceAssociationCustomContentType: no
      - optionsSourceDatabaseCustomContentType: no
      - selectionMethod: checkbox
      - selectedItemsStyle: text
      - selectAllButtonCaption: Select all
      - ariaRequired: `false`
      - clearButtonAriaLabel: Clear selection
      - removeValueAriaLabel: Remove value
      - a11ySelectedValue: Selected value:
      - a11yOptionsAvailable: Number of options available:
      - a11yInstructions: Use up and down arrow keys to navigate. Press Enter or Space Bar keys to select.
      - staticDataSourceCustomContentType: no
      - readOnlyStyle: text
      - loadingType: spinner
      - selectedItemsSorting: none
  - 📝 **CheckBox**: checkBox1 👁️ (If AttributeTypeEnum is AutoNumber/BooleanType/DateTime/EnumType/HashString/IntegerType/LongType/StringType/Decimal/(empty))
  - 📝 **DatePicker**: datePicker2 👁️ (If AttributeTypeEnum is AutoNumber/BooleanType/DateTime/EnumType/HashString/IntegerType/LongType/StringType/Decimal/(empty))
    ↳ [acti] → **Microflow**: `MxModelReflection.MB_TestThePattern`
    ↳ [acti] → **Cancel Changes**
