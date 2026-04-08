# Page: EmailAccount_Manage_View

**Allowed Roles:** Email_Connector.EmailConnectorAdmin

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **ListView** [Context] [DP: {Row Size: Small}]
  ↳ [click] → **Nanoflow**: `Email_Connector.ACT_EmailAccount_SetOpen`
    ↳ [acti] → **Microflow**: `Email_Connector.SUB_EmailAccount_Delete`
  ↳ [acti] → **Page**: `Email_Connector.EmailAccount_NewEdit`
