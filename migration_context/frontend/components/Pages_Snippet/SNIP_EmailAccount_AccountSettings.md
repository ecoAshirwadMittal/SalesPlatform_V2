# Snippet: SNIP_EmailAccount_AccountSettings

## Widget Tree

  ↳ [acti] → **Page**: `Email_Connector.EmailConnector_Overview`
  ↳ [acti] → **Microflow**: `Email_Connector.SUB_EmailAccount_Delete`
- 📑 **TabContainer** [DP: {Style: Lined, Spacing top: Outer large}]
  - 📑 **Tab**: "Email Settings"
    - 📝 **CheckBox**: checkBox2
    - 📦 **DataView** [Context] [DP: {Spacing top: Outer medium}] 👁️ (If isIncomingEmailConfigured is true/false)
    - 📦 **DataView** [Context] [DP: {Spacing top: Outer medium, Spacing left: Outer large}] 👁️ (If isOutgoingEmailConfigured is true/false)
      ↳ [acti] → **Cancel Changes**
    - ⚡ **Button**: Save [Style: Primary] [DP: {Align self: Right, Spacing top: Outer large}]
      ↳ [acti] → **Microflow**: `Email_Connector.SUB_EmailAccount_Save`
  - 📑 **Tab**: "Server Settings"
    - 📦 **DataView** [Context]
        ↳ [acti] → **Microflow**: `Email_Connector.ACT_OAuthProvider_ShowOAuthProviderPage`
        ↳ [acti] → **Nanoflow**: `Email_Connector.ACT_EmailAccount_StartOAuthFlow`
      ↳ [acti] → **Cancel Changes**
      ↳ [acti] → **Microflow**: `Email_Connector.ACT_SaveEmailAccountSettingAndClosePage`
  - 📑 **Tab**: "Email Security" 👁️ (If isOutgoingEmailConfigured is true/false)
      ↳ [acti] → **Cancel Changes**
      ↳ [acti] → **Microflow**: `Email_Connector.ACT_EmailAccount_SaveEmailSecurityConfiguration`
  - 📑 **Tab**: "Error Logs"
