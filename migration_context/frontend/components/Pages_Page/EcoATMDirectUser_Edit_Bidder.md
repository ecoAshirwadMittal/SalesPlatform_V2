# Page: EcoATMDirectUser_Edit_Bidder

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 🔤 **Text**: "Last invite sent" [Class: `control-label-standalone`]
  - 🔤 **Text**: "Activation Date" [Class: `control-label-standalone`]
  - 🔤 **Text**: "Last Login" [Class: `control-label-standalone`]
  - 📝 **DropDown**: dropDown2 [DP: {Spacing left: Outer small}] 👁️ (If Inactive is true/false)
  - 📝 **DropDown**: dropDown1
  - 🔤 **Text**: "Role" [Class: `control-label-standalone`]
  - 🧩 **Checkbox Set Selector** [Class: `border`] (ID: `CheckboxSelector.widget.checkboxselector`)
      - sortAttr: [Attr: System.UserRole.Name]
      - sortOrder: Asc
      - limit: 0
      ➤ **displayAttrs**
          - header: Role
          - displayAttr: [Attr: System.UserRole.Name]
          - displayWidth: 90
          - decimalPrecision: 2
          - currency: None
      - onChangeMf: [MF: AuctionUI.ACT_SetBuyerVisibility]
  - 📝 **InputReferenceSetSelector**: referenceSetSelector1 [DP: {Spacing top: Outer small}] 👁️ (If: `1=0`)
  - 🧩 **Combo box** (ID: `com.mendix.widget.web.combobox.Combobox`)
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
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_SaveUserChanges`
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_ResendInvite`
