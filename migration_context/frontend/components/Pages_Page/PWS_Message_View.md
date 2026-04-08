# Page: PWS_Message_View

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

**Layout:** `AuctionUI.ecoATM_Popup_Layout_NoTitle`

## Widget Tree

- 📦 **DataView** [Context]
    ↳ [acti] → **Close Page**
  - 🧩 **Image** [Class: `pws-fa-image`] 👁️ (If IsSuccess is true/false) (ID: `com.mendix.widget.web.image.Image`)
      - datasource: icon
      - onClickType: action
      - widthUnit: auto
      - width: 100
      - heightUnit: auto
      - height: 100
      - iconSize: 14
      - displayAs: fullImage
