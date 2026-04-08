# Page: BidDetailReport_BuyerCodeSelect_SwitchBuyerCode

**Allowed Roles:** EcoATM_Reports.Administrator, EcoATM_Reports.SalesLeader, EcoATM_Reports.SalesRep

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 🧩 **Active Menu Selector** (ID: `mendix.activemenuselector.ActiveMenuSelector`)
    - menuWidgetName: navigationTree3
    - menuItemTitle: Bid as Bidder
- 📦 **DataView** [MF: AuctionUI.SUB_BuyerCodeSelectSearchHelper_Create]
  - 🧩 **Combo box** [Style: `
`] (ID: `com.mendix.widget.web.combobox.Combobox`)
      - source: context
      - optionsSourceType: association
      - optionsSourceDatabaseCaptionType: attribute
      - optionsSourceAssociationCaptionType: expression
      - optionsSourceAssociationCaptionExpression: `$currentObject/Code+' '+$currentObject/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/CompanyName

`
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
