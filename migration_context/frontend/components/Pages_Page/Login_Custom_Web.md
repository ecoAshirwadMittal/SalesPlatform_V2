# Page: Login_Custom_Web

**Allowed Roles:** AuctionUI.Anonymous, AuctionUI.Administrator

**Layout:** `AuctionUI.Layout_Login`

## Widget Tree

- 📦 **DataView** [NF: AuctionUI.ACT_Create_LoginCredentials_2] [Style: `width:100%`]
  - 🖼️ **Image**: EcoAtm_Logo [Style: `margin-bottom:33px;`]
    ↳ [EnterKeyPress] → **Nanoflow**: `AuctionUI.ACT_Set_ShowLoginPassword`
    ↳ [EnterKeyPress] → **Nanoflow**: `AuctionUI.ACT_Login_Client`
  - 📝 **CheckBox**: checkBox1 👁️ (If ShowPasssword is true/false)
    ↳ [acti] → **Nanoflow**: `AuctionUI.ACT_Set_ShowLogin`
    ↳ [acti] → **Microflow**: `ForgotPassword.Step1_ShowForgotPasswordPage`
    ↳ [acti] → **Nanoflow**: `AuctionUI.ACT_Login_Client`
    ↳ [acti] → **Nanoflow**: `AuctionUI.ACT_Set_ShowLoginPassword`
