# Page: Buyer_New

**Allowed Roles:** EcoATM_MDM.Administrator

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context]
  - 🖼️ **Image**: user_plus
  - 🔤 **Text**: "Apply special buyer treatment" [Class: `widget-switch-label` | DP: {Spacing bottom: Outer none}]
  - 🧩 **Switch** [DP: {Spacing bottom: Outer none}] (ID: `com.mendix.widget.custom.switch.Switch`)
      - booleanAttribute: [Attr: EcoATM_BuyerManagement.Buyer.isSpecialBuyer]
  - 🧩 **Combo box** (ID: `com.mendix.widget.web.combobox.Combobox`)
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
  - 🔤 **Text**: "Buyer Codes" [Class: `h5`]
  - 🧩 **Data grid 2** [Class: `buyer-codes-grid`] (ID: `com.mendix.widget.web.datagrid.Datagrid`)
      - refreshInterval: 0
      - itemSelectionMethod: checkbox
      - itemSelectionMode: clear
      ➤ **columns**
          - showContentAs: customContent
          - attribute: [Attr: EcoATM_BuyerManagement.BuyerCode.Code]
          ➤ **content** (Widgets)
          - header: Code
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
            - 📝 **DropDown**: dropDown1
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
              ↳ [acti] → **Delete**
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
    ↳ [acti] → **Microflow**: `EcoATM_MDM.ACT_Buyer_SaveBuyerFromUser`
    ↳ [acti] → **Microflow**: `EcoATM_MDM.ACT_Buyer_NewSave`
