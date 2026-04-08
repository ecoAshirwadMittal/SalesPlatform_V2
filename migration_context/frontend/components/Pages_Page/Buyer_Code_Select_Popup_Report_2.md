# Page: Buyer_Code_Select_Popup_Report_2

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.SalesLeader, AuctionUI.SalesRep

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 🧩 **Active Menu Selector** (ID: `mendix.activemenuselector.ActiveMenuSelector`)
    - menuWidgetName: navigationTree3
    - menuItemTitle: Bid as Bidder
- 🔤 **Text**: "Choose" [Class: `h3` | Style: `color:#14AC36 !important;`]
- 🔤 **Text**: "Buyer Code" [Class: `h3` | DP: {Spacing left: Outer small}]
- 🧩 **Gallery** [Style: `overflow:hidden;` | DP: {Borders: Both}] (ID: `com.mendix.widget.web.gallery.Gallery`)
    ➤ **content** (Widgets)
      - 🖼️ **Image**: Hotel
    - desktopItems: 1
    - tabletItems: 1
    - phoneItems: 1
    - pageSize: 200
    - pagination: virtualScrolling
    - pagingPosition: below
    - showEmptyPlaceholder: none
- 📦 **DataView** [Context]
  - 🧩 **Combo box** [Class: `border`] (ID: `com.mendix.widget.web.combobox.Combobox`)
      - source: context
      - optionsSourceType: association
      - optionsSourceDatabaseCaptionType: attribute
      - optionsSourceAssociationCaptionType: attribute
      - optionsSourceAssociationCaptionAttribute: [Attr: AuctionUI.BuyerCode.Code]
      - filterType: contains
      - optionsSourceAssociationCustomContentType: yes
      ➤ **optionsSourceAssociationCustomContent** (Widgets)
        - 🧩 **HTML Element** (ID: `com.mendix.widget.web.htmlelement.HTMLElement`)
            - tagName: div
            - tagNameCustom: div
            - tagContentMode: innerHTML
            - tagContentHTML: <span class="buyercodeselect_code">{1}</span><span class="buyercodeselect_buyer">{2}</span>
      - optionsSourceDatabaseCustomContentType: no
      - selectionMethod: rowclick
      - selectedItemsStyle: text
      - selectAllButtonCaption: Select all
      - clearButtonAriaLabel: Clear selection
      - removeValueAriaLabel: Remove value
      - a11ySelectedValue: Selected value:
      - a11yOptionsAvailable: Number of options available:
      - a11yInstructions: Use up and down arrow keys to navigate. Press Enter or Space Bar keys to select.
