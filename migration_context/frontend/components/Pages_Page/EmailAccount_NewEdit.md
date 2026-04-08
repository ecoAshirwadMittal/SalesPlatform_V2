# Page: EmailAccount_NewEdit

**Allowed Roles:** Email_Connector.EmailConnectorAdmin

**Layout:** `Atlas_Core.Atlas_TopBar`

## Widget Tree

- 📦 **DataView** [MF: Email_Connector.DS_EmailAccount_CreateDummyAccount] [DP: {Spacing top: Outer large}]
  - 🧩 **Attribute Radiobutton List** (ID: `RadioButtonList.widget.AttrRadioButtonList`)
      - entity: [Attr: Email_Connector.EmailAccount.isOAuthUsed]
      - formOrientation: vertical
      - direction: horizontal
      - captiontrue: Use Microsoft Entra ID
      - captionfalse: Use Basic Credentials
      - labelWidth: 3
      - onchangeAction: [MF: Email_Connector.OCH_OAuthProvider_GetOrCreateOAuthProvider]
  - 📝 **ReferenceSelector**: referenceSelector2
    ↳ [Change] → **Microflow**: `Email_Connector.OCH_OAuthProvider_CreateNew`
  - 📦 **DataView** [Context]
    ↳ [acti] → **Close Page**
    ↳ [acti] → **Page**: `Email_Connector.EmailAccount_MailboxSetting_NewEdit`
