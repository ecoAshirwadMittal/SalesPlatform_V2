# Page: Tile_NewEdit

**Allowed Roles:** Eco_Core.Admin, Eco_Core.System

**Layout:** `AuctionUI.ecoATM_Popup_Layout`

## Widget Tree

- 📦 **DataView** [Context] [DP: {Spacing bottom: Outer large}]
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
    ↳ [acti] → **Microflow**: `Eco_Core.ACT_Tile_Create`
    ↳ [acti] → **Cancel Changes**
