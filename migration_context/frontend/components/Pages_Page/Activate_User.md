# Page: Activate_User

**Allowed Roles:** EcoATM_UserManagement.Anonymous, EcoATM_UserManagement.Administrator

**Layout:** `AuctionUI.Layout_Login`

## Widget Tree

- 🖼️ **Image**: EcoAtm_Logo [DP: {Spacing top: Outer large, Spacing bottom: Outer large}]
- 📦 **DataView** [Context] [Style: `width:100%`]
  - 🖼️ **Image**: thumbs_up_red 👁️ (If IsLengthValid is true/false)
  - 🖼️ **Image**: thumbs_up_black 👁️ (If IsLengthValid is true/false)
  - 🖼️ **Image**: thumbs_up_red 👁️ (If HasUpperCaseLetter is true/false)
  - 🖼️ **Image**: thumbs_up_black 👁️ (If HasUpperCaseLetter is true/false)
  - 🖼️ **Image**: thumbs_up_red 👁️ (If HasSpecialCharacter is true/false)
  - 🖼️ **Image**: thumbs_up_black 👁️ (If HasSpecialCharacter is true/false)
    ↳ [acti] → **Microflow**: `AuctionUI.ACT_ActivateNewAccount`
