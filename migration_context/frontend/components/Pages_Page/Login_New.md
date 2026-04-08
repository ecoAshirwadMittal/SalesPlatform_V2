# Page: Login_New

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
- 🧩 **Events** (ID: `com.mendix.widget.web.events.Events`)
    - componentLoadDelayParameterType: number
    - componentLoadDelay: 0
    - componentLoadRepeatIntervalParameterType: number
    - componentLoadRepeatInterval: 30000
    - onEventChangeDelayParameterType: number
    - onEventChangeDelay: 0
- 🖼️ **Image**: _2024_ecoATM_Logo [Class: `loginlogoimage` | DP: {Center: [object Object]}]
- 📦 **DataView** [NF: AuctionUI.ACT_Create_LoginCredentials_2]
    ↳ [Change] → **Microflow**: `EcoATM_UserManagement.OCH_Login_Email`
  - 🧩 **Toggle Show Password** (ID: `incentro.toggleshowpassword.ToggleShowPassword`)
  - 🧩 **Toggle Show Password** (ID: `incentro.toggleshowpassword.ToggleShowPassword`)
  - 📝 **CheckBox**: checkBox2 [DP: {Align self: Left}]
    ↳ [acti] → **Microflow**: `EcoATM_UserManagement.ACT_ForgotPassword`
    ↳ [acti] → **Nanoflow**: `EcoATM_UserManagement.ACT_Login_ExternalUser`
    ↳ [acti] → **Nanoflow**: `EcoATM_UserManagement.ACT_Login_InternalUser`
  ↳ [acti] → **OpenLink**
