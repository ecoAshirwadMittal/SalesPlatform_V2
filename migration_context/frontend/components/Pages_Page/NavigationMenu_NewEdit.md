# Page: NavigationMenu_NewEdit

**Allowed Roles:** EcoATM_PWS.Administrator

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context]
  - ⚡ **Button**: radioButtons1
  - 🧩 **Combo box** (ID: `com.mendix.widget.web.combobox.Combobox`)
      - source: context
      - optionsSourceType: enumeration
      - attributeEnumeration: [Attr: EcoATM_PWS.NavigationMenu.UserGroup]
      - optionsSourceAssociationCaptionType: attribute
      - optionsSourceDatabaseCaptionType: attribute
      - optionsSourceAssociationCustomContentType: no
      - optionsSourceDatabaseCustomContentType: no
      - staticDataSourceCustomContentType: no
      - selectionMethod: checkbox
      - selectedItemsStyle: text
      - selectAllButtonCaption: Select all
      - readOnlyStyle: bordered
      - ariaRequired: `false`
      - clearButtonAriaLabel: Clear selection
      - removeValueAriaLabel: Remove value
      - a11ySelectedValue: Selected value:
      - a11yOptionsAvailable: Number of options available:
      - a11yInstructions: Use up and down arrow keys to navigate. Press Enter or Space Bar keys to select.
      - loadingType: spinner
      - selectedItemsSorting: none
      - filterType: contains
    ↳ [acti] → **Save Changes**
    ↳ [acti] → **Cancel Changes**
