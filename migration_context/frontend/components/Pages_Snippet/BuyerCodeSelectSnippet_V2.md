# Snippet: BuyerCodeSelectSnippet_V2

## Widget Tree

- 📦 **DataView** [MF: AuctionUI.ACT_GetCurrentUser]
- 📦 **DataView** [NF: AuctionUI.DS_BuyerCodeSelectUiHelper]
  - 🧩 **Image** [Class: `bcs-card-top-image`] (ID: `com.mendix.widget.web.image.Image`)
      - datasource: image
      - onClickType: action
      - widthUnit: auto
      - width: 100
      - heightUnit: auto
      - height: 100
      - iconSize: 14
      - displayAs: fullImage
  - 📦 **ListView** [NF: AuctionUI.DS_GetBuyerCodesByType] [Class: `bcs-buyercode-list` | DP: {Style: No Styling}]
    ↳ [click] → **Nanoflow**: `AuctionUI.ACT_NavigateToBidderDashboard_WithTabId`
    - 🧩 **Image** [Class: `bcs-buyer-right-arrow`] (ID: `com.mendix.widget.web.image.Image`)
        - datasource: icon
        - onClickType: action
        - widthUnit: auto
        - width: 100
        - heightUnit: auto
        - height: 100
        - iconSize: 14
        - displayAs: fullImage
  - 🧩 **Image** [Class: `bcs-card-top-image`] (ID: `com.mendix.widget.web.image.Image`)
      - datasource: image
      - onClickType: action
      - widthUnit: auto
      - width: 100
      - heightUnit: auto
      - height: 100
      - iconSize: 14
      - displayAs: fullImage
  - 📦 **ListView** [NF: AuctionUI.DS_GetBuyerCodesByType] [Class: `bcs-buyercode-list` | DP: {Style: No Styling}]
    ↳ [click] → **Microflow**: `EcoATM_PWS.ACT_NavigateToBidderDashboard`
    - 🧩 **Image** [Class: `bcs-buyer-right-arrow`] (ID: `com.mendix.widget.web.image.Image`)
        - datasource: icon
        - onClickType: action
        - widthUnit: auto
        - width: 100
        - heightUnit: auto
        - height: 100
        - iconSize: 14
        - displayAs: fullImage
