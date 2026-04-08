# Page: Buyer_Edit

**Allowed Roles:** EcoATM_MDM.Administrator

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context]
  - 🖼️ **Image**: Hotel
  - 🔤 **Text**: "Added" [Class: `control-label-standalone` | DP: {Spacing top: Inner small, Spacing bottom: Outer small}]
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_ToggleBuyerStatus`
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_ToggleBuyerStatus`
  - 📝 **DropDown**: dropDown1 [DP: {Spacing bottom: Outer none}]
  - 🔤 **Text**: "Apply special buyer treatment" [Class: `widget-switch-label` | DP: {Spacing bottom: Outer none}]
  - 🧩 **Switch** [DP: {Spacing bottom: Outer none}] (ID: `com.mendix.widget.custom.switch.Switch`)
      - booleanAttribute: [Attr: EcoATM_BuyerManagement.Buyer.isSpecialBuyer]
  - 🧩 **Switch** [DP: {Spacing bottom: Outer none}] (ID: `com.mendix.widget.custom.switch.Switch`)
      - booleanAttribute: [Attr: EcoATM_BuyerManagement.Buyer.isSpecialBuyer]
  - 🧩 **Combo box** [DP: {Spacing top: Outer medium}] (ID: `com.mendix.widget.web.combobox.Combobox`)
      - source: context
      - optionsSourceType: association
      - optionsSourceDatabaseCaptionType: attribute
      - optionsSourceAssociationCaptionType: expression
      - optionsSourceAssociationCaptionExpression: `$currentObject/SalesRepFirstName +' ' + $currentObject/SalesRepLastName`
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
  - 🧩 **Data grid 2** [Class: `buyer-codes-grid`] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: checkbox
      - itemSelectionMode: clear
      ➤ **columns**
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_BuyerManagement.BuyerCode.Code]
          ➤ **content** (Widgets)
          - header: Buyer Codes
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - columnClass: `if $currentObject/codeUniqueValid = false then 'not-unique-buyer-code' else ''
`
          - filterCaptionType: expression
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_BuyerManagement.BuyerCode.BuyerCodeType]
          ➤ **content** (Widgets)
            - 📝 **DropDown**: dropDown2 ✏️ (Editable if: `isNew($currentObject)`) 👁️ (If softDelete is true/false)
            - 📝 **DropDown**: dropDown3
          - header: Type
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - filterCaptionType: expression
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_BuyerManagement.BuyerCode.Budget]
          ➤ **content** (Widgets)
              ↳ [Enter] → **Nanoflow**: `EcoATM_MDM.NF_ValidateBuyerCodeBudget`
          - header: Budget ($)
          - visible: `true`
          - hidable: yes
          - width: autoFill
          - minWidth: auto
          - minWidthLimit: 100
          - size: 1
          - alignment: left
          - filterCaptionType: expression
          - showContentAs: customContent
          ➤ **content** (Widgets)
              ↳ [acti] → **Microflow**: `EcoATM_MDM.ACT_SoftDelete_BuyerCode`
          - visible: `true`
          - hidable: yes
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
      - onClickTrigger: single
      - configurationStorageType: attribute
      - exportDialogLabel: Export progress
      - cancelExportLabel: Cancel data export
      - selectRowLabel: Select row
      - loadingType: spinner
    ↳ [acti] → **Microflow**: `EcoATM_MDM.ACT_BuyerCode_Create`
    ↳ [acti] → **Cancel Changes**
    ↳ [acti] → **Microflow**: `EcoATM_MDM.ACT_Buyer_EditSave_Admin`
    ↳ [acti] → **Microflow**: `EcoATM_MDM.ACT_Buyer_EditSave_Compliance`
