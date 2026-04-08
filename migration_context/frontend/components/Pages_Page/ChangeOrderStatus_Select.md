# Page: ChangeOrderStatus_Select

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesRep, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context] [DP: {Spacing bottom: Outer large}]
  - 📝 **CheckBox**: checkBox1
  - 🔤 **Text**: "Period"
  - 📝 **DatePicker**: datePicker1
  - 📝 **DatePicker**: datePicker2
    ↳ [acti] → **Nanoflow**: `EcoATM_PWS.NAN_ChangeOrderStatus_SearchOrder`
  - 📦 **ListView** [Association: undefined]
      ↳ [acti] → **Nanoflow**: `EcoATM_PWS.NAN_ChangeOfferStatusHelper_RemoveOrder`
  - 📝 **CheckBox**: checkBox4
  - 📝 **DropDown**: dropDown1 ✏️ (Editable if AllPeriod)
  - 🧩 **Combo box** (ID: `com.mendix.widget.web.combobox.Combobox`)
      - source: context
      - optionsSourceType: association
      - optionsSourceAssociationCaptionType: attribute
      - optionsSourceDatabaseCaptionType: attribute
      - optionsSourceAssociationCaptionAttribute: [Attr: EcoATM_PWS.OrderStatus.SystemStatus]
      - emptyOptionText: Select a status
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
  - ⚡ **Button**: radioButtons1
  - ⚡ **Button**: radioButtons2
    ↳ [acti] → **Microflow**: `EcoATM_PWS.ACT_ChangeOfferStatus_Proceed`
    ↳ [acti] → **Cancel Changes**
