# Page: Buyer_Code_Select_Search_Popup_Report

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.SalesRep

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [MF: AuctionUI.ACT_GetBuyerCodeSelectSearch_Helper]
  - 🧩 **Combo box** [Class: `border`] (ID: `com.mendix.widget.web.combobox.Combobox`)
      - source: context
      - optionsSourceType: association
      - optionsSourceDatabaseCaptionType: attribute
      - optionsSourceAssociationCaptionType: attribute
      - optionsSourceAssociationCaptionAttribute: [Attr: EcoATM_Caching.NP_BuyerCodeSelect_Helper.Code]
      - filterType: contains
      - optionsSourceAssociationCustomContentType: yes
      ➤ **optionsSourceAssociationCustomContent** (Widgets)
        - 🧩 **HTML Element** (ID: `com.mendix.widget.web.htmlelement.HTMLElement`)
            - tagName: div
            - tagNameCustom: div
            - tagContentMode: innerHTML
            - tagContentHTML: <span class="buyercodeselect_code">{1}</span><span class="buyercodeselect_buyer">{2}</span>
      - optionsSourceDatabaseCustomContentType: no
      - selectionMethod: checkbox
      - selectedItemsStyle: text
      - selectAllButtonCaption: Select all
      - clearButtonAriaLabel: Clear selection
      - removeValueAriaLabel: Remove value
      - a11ySelectedValue: Selected value:
      - a11yOptionsAvailable: Number of options available:
      - a11yInstructions: Use up and down arrow keys to navigate. Press Enter or Space Bar keys to select.
