# Page: Login_Account_Activation

**Allowed Roles:** EcoATM_UserManagement.Anonymous, EcoATM_UserManagement.Administrator

**Layout:** `AuctionUI.Layout_Login`

## Widget Tree

- 📦 **DataView** [Context] [Style: `width:100%`]
  - 🖼️ **Image**: EcoAtm_Logo [Style: `margin-bottom:33px;`]
    ↳ [EnterKeyPress] → **Nanoflow**: `AuctionUI.ACT_Set_ShowLoginPassword`
    ↳ [EnterKeyPress] → **Nanoflow**: `AuctionUI.ACT_Login_Client`
  - 📝 **CheckBox**: checkBox1 👁️ (If ShowPasssword is true/false)
    ↳ [acti] → **Microflow**: `ForgotPassword.Step1_ShowForgotPasswordPage`
    ↳ [acti] → **Nanoflow**: `AuctionUI.ACT_Login_Client`
