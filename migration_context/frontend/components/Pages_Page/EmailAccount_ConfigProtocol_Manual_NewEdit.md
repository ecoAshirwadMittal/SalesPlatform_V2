# Page: EmailAccount_ConfigProtocol_Manual_NewEdit

**Allowed Roles:** Email_Connector.EmailConnectorAdmin

**Layout:** `Atlas_Core.Atlas_TopBar`

## Widget Tree

- 📦 **DataView** [Context] [DP: {Spacing top: Outer large}]
  - 📦 **DataView** [Context]
      ↳ [acti] → **Microflow**: `Email_Connector.ACT_ShowAccountLoginPage`
      ↳ [acti] → **Microflow**: `Email_Connector.ACT_EmailAccount_SaveManualConfig`
      ↳ [acti] → **Microflow**: `Email_Connector.ACT_EmailAccount_ClientCredentialsGrant_SaveManualConfig`
      ↳ [acti] → **Microflow**: `Email_Connector.ACT_EmailAccount_SaveManualConfig`
