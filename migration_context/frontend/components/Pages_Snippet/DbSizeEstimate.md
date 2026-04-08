# Snippet: DbSizeEstimate

## Widget Tree

- 🔤 **Text**: "Nr of records"
- 🔤 **Text**: "Mx object"
- 🧩 **Combo box** (ID: `com.mendix.widget.web.combobox.Combobox`)
    - source: context
    - optionsSourceType: association
    - optionsSourceDatabaseCaptionType: attribute
    - optionsSourceAssociationCaptionType: attribute
    - optionsSourceAssociationCaptionAttribute: [Attr: MxModelReflection.MxObjectType.CompleteName]
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
- 🔤 **Text**: "Calculated size in bytes"
- 🔤 **Text**: "Calculated size in kilo bytes"
