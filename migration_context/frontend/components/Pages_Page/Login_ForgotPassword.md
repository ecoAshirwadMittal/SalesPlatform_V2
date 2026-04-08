# Page: Login_ForgotPassword

**Allowed Roles:** EcoATM_UserManagement.Anonymous, EcoATM_UserManagement.Administrator

**Layout:** `AuctionUI.Layout_NewLogin`

## Widget Tree

- 🧩 **Image** [DP: {Image fit: Contain}] (ID: `com.mendix.widget.web.image.Image`)
    - datasource: image
    - onClickType: action
    - widthUnit: auto
    - width: 100
    - heightUnit: auto
    - height: 100
    - iconSize: 14
    - displayAs: fullImage
- 🖼️ **Image**: _2024_ecoATM_Logo [Class: `loginlogoimage` | DP: {Center: [object Object], Spacing top: Outer large}]
- 📦 **DataView** [Context]
    ↳ [Change] → **Microflow**: `EcoATM_UserManagement.OCH_ForgotPassword_Email`
    ↳ [acti] → **Microflow**: `EcoATM_UserManagement.ACT_SendPasswordResetEmail`
    ↳ [acti] → **Page**: `EcoATM_UserManagement.Login_New`
