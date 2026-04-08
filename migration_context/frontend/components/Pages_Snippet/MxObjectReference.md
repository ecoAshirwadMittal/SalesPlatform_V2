# Snippet: MxObjectReference

## Widget Tree

- 🔤 **Text**: "Name"
- 🔤 **Text**: "Type"
- 🧩 **Combo box** (ID: `com.mendix.widget.web.combobox.Combobox`)
    - source: context
    - optionsSourceType: enumeration
    - attributeEnumeration: [Attr: MxModelReflection.MxObjectReference.ReferenceType]
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
- 🔤 **Text**: "Owner"
- 🧩 **Combo box** (ID: `com.mendix.widget.web.combobox.Combobox`)
    - source: context
    - optionsSourceType: enumeration
    - attributeEnumeration: [Attr: MxModelReflection.MxObjectReference.AssociationOwner]
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
- 🔤 **Text**: "Parent objects"
- 📦 **ListView** [Context]
  ↳ [click] → **Page**: `MxModelReflection.MxObject_Details`
- 🔤 **Text**: "Child objects"
- 📦 **ListView** [Context]
  ↳ [click] → **Page**: `MxModelReflection.MxObject_Details`
