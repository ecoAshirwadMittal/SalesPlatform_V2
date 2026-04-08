# Snippet: MxObject

## Widget Tree

- 🔤 **Text**: "Mendix object"
- 🔤 **Text**: "Inherits from"
- 📦 **ListView** [Context]
- 🔤 **Text**: "Readable name"
- 🔤 **Text**: "Persistence"
- 🧩 **Combo box** (ID: `com.mendix.widget.web.combobox.Combobox`)
    - source: context
    - optionsSourceType: enumeration
    - attributeEnumeration: [Attr: MxModelReflection.MxObjectType.PersistenceType]
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
- 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: rowClick
    - itemSelectionMode: clear
    ➤ **columns**
        - showContentAs: attribute
        - attribute: [Attr: MxModelReflection.MxObjectMember.AttributeName]
        - header: Member name
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 51
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: MxModelReflection.MxObjectMember.AttributeTypeEnum]
        - header: Type
        ➤ **filter** (Widgets)
          - 🧩 **Drop-down filter** (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
              - selectedItemsStyle: text
              - selectionMethod: checkbox
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 49
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
        ↳ [acti] → **Microflow**: `MxModelReflection.ACT_ShowMemberPage`
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
    - loadingType: spinner
- 🧩 **Data grid 2** (ID: `com.mendix.widget.web.datagrid.Datagrid`)
    - refreshInterval: 0
    - itemSelectionMethod: rowClick
    - itemSelectionMode: clear
    ➤ **columns**
        - showContentAs: customContent
        - attribute: [Attr: MxModelReflection.MxObjectReference.ReadableName]
        ➤ **content** (Widgets)
        - header: Readable name
        ➤ **filter** (Widgets)
          - 🧩 **Text filter** (ID: `com.mendix.widget.web.datagridtextfilter.DatagridTextFilter`)
              - defaultFilter: contains
              - delay: 500
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 24
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: attribute
        - attribute: [Attr: MxModelReflection.MxObjectReference.CompleteName]
        - header: Reference name
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 29
        - alignment: left
        - filterCaptionType: expression
        - showContentAs: customContent
        - attribute: [Attr: MxModelReflection.MxObjectReference.CompleteName]
        ➤ **content** (Widgets)
          - 📦 **DataView** [MF: MxModelReflection.ReferenceObjects]
        - header: Mendix object
        ➤ **filter** (Widgets)
          - 🧩 **Drop-down filter** (ID: `com.mendix.widget.web.datagriddropdownfilter.DatagridDropdownFilter`)
              - selectedItemsStyle: text
              - selectionMethod: checkbox
        - filterAssociationOptionLabel: `$currentObject/CompleteName`
        - visible: `true`
        - hidable: yes
        - width: manual
        - minWidth: auto
        - minWidthLimit: 100
        - size: 47
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
        ↳ [acti] → **Page**: `MxModelReflection.MxObjectReference_View`
        ↳ [acti] → **Microflow**: `MxModelReflection.IVK_OpenReferencedMendixObject`
    - exportDialogLabel: Export progress
    - cancelExportLabel: Cancel data export
    - selectRowLabel: Select row
    - loadingType: spinner
