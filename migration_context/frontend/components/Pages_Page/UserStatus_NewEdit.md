# Page: UserStatus_NewEdit

**Allowed Roles:** AuctionUI.Administrator

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context]
  - 🧩 **Image** (ID: `com.mendix.widget.web.image.Image`)
      - datasource: image
      - onClickType: action
      - widthUnit: auto
      - width: 100
      - heightUnit: auto
      - height: 100
      - iconSize: 14
      - displayAs: fullImage
  - ⚡ **Button**: radioButtons1
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_UserStatus_UpdateByAdmin`
    ↳ [acti] → **Cancel Changes**
