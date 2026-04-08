# Page: Settings_View

**Allowed Roles:** Eco_Core.Admin, Eco_Core.System

**Layout:** `AuctionUI.ecoAtm_Atlas_Default`

## Widget Tree

- 📦 **ListView** [Context] [Class: `lv-col-md-4` | DP: {Style: No Styling}]
  - 🧩 **Image** [DP: {Center image: [object Object], Image style: Circle, Spacing top: Outer medium}] (ID: `com.mendix.widget.web.image.Image`)
      - datasource: image
      - onClickType: action
      - widthUnit: pixels
      - width: 250
      - heightUnit: pixels
      - height: 250
      - iconSize: 14
      - displayAs: fullImage
  - 🖼️ **Image**: Cancel
    ↳ [click] → **Delete**
  - 🖼️ **Image**: Edit_button
    ↳ [click] → **Page**: `Eco_Core.Tile_NewEdit`
  ↳ [acti] → **Page**: `Eco_Core.Tile_NewEdit`
