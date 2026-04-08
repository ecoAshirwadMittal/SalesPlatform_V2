# Page: EcoATMDirectUser_New

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context]
  - 🖼️ **Image**: user_plus
    ↳ [Change] → **Microflow**: `AuctionUI.ACT_HandleUserEmailChange`
  - 📝 **DropDown**: dropDown1 [Class: `formgroup_smallmargin`]
  - 📑 **TabContainer** [Style: `font-size:22px;`]
    - 📑 **Tab**: "Internal User Roles" 👁️ (If: `(not($currentObject/IsLocalUser) and ($currentObject/Email!=empty)) or ($currentObject/Email=empty)`)
      - 🧩 **Checkbox Set Selector** (ID: `CheckboxSelector.widget.checkboxselector`)
          - sortAttr: [Attr: System.UserRole.Name]
          - sortOrder: Asc
          - limit: 0
          ➤ **displayAttrs**
              - displayAttr: [Attr: System.UserRole.Name]
              - displayWidth: 95
              - decimalPrecision: 2
              - currency: None
    - 📑 **Tab**: "External User Roles" 👁️ (If: `$currentObject/IsLocalUser and ($currentObject/Email!=empty)`)
      - 📝 **InputReferenceSetSelector**: referenceSetSelector2 🔒 [Read-Only]
      - 🧩 **Combo box** [Class: `choose-buyer`] (ID: `com.mendix.widget.web.combobox.Combobox`)
          - source: context
          - optionsSourceType: association
          - optionsSourceDatabaseCaptionType: attribute
          - optionsSourceAssociationCaptionType: attribute
          - optionsSourceAssociationCaptionAttribute: [Attr: EcoATM_BuyerManagement.Buyer.CompanyName]
          - filterType: startsWith
          - optionsSourceAssociationCustomContentType: no
          - optionsSourceDatabaseCustomContentType: no
          ➤ **menuFooterContent** (Widgets)
              ↳ [acti] → **Microflow**: `EcoATM_MDM.ACT_Buyer_CreateNewFromUser`
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
    ↳ [acti] → **Cancel Changes**
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_SaveNewUser`
