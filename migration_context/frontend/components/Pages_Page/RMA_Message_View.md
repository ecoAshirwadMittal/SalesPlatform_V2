# Page: RMA_Message_View

**Allowed Roles:** EcoATM_RMA.Administrator, EcoATM_RMA.Bidder, EcoATM_RMA.SalesLeader, EcoATM_RMA.SalesOps, EcoATM_RMA.SalesRep

**Layout:** `AuctionUI.ecoATM_Popup_Layout_NoTitle`

## Widget Tree

  ↳ [Click] → **Cancel Changes**
- 📦 **DataView** [Context]
  - 🧩 **Image** [Class: `pws-fa-image`] 👁️ (If IsSuccess is true/false) (ID: `com.mendix.widget.web.image.Image`)
      - datasource: icon
      - onClickType: action
      - widthUnit: auto
      - width: 100
      - heightUnit: auto
      - height: 100
      - iconSize: 14
      - displayAs: fullImage
  - 🧩 **Image** [Class: `ram-icon-color`] 👁️ (If IsSuccess is true/false) (ID: `com.mendix.widget.web.image.Image`)
      - datasource: icon
      - onClickType: action
      - widthUnit: auto
      - width: 100
      - heightUnit: auto
      - height: 100
      - iconSize: 25
      - displayAs: fullImage
    ↳ [acti] → **Microflow**: `EcoATM_RMA.ACT_RequestRMA`
