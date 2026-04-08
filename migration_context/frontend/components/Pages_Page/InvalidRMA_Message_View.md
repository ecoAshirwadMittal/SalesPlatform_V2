# Page: InvalidRMA_Message_View

**Allowed Roles:** EcoATM_RMA.Administrator, EcoATM_RMA.Bidder, EcoATM_RMA.SalesOps

**Layout:** `AuctionUI.ecoATM_Popup_Layout_NoTitle`

## Widget Tree

  ↳ [Click] → **Cancel Changes**
- 🧩 **Image** [Class: `ram-icon-color` | DP: {Spacing right: Outer small}] (ID: `com.mendix.widget.web.image.Image`)
    - datasource: icon
    - onClickType: action
    - widthUnit: auto
    - width: 100
    - heightUnit: auto
    - height: 100
    - iconSize: 25
    - displayAs: fullImage
  ↳ [acti] → **Microflow**: `EcoATM_RMA.ACT_RequestRMA`
  ↳ [acti] → **Microflow**: `EcoATM_RMA.ACT_DownloadInvalidIMEIs`
