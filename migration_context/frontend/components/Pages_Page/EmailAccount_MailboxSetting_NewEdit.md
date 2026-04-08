# Page: EmailAccount_MailboxSetting_NewEdit

**Allowed Roles:** Email_Connector.EmailConnectorAdmin

**Layout:** `Atlas_Core.Atlas_TopBar`

## Widget Tree

- 📦 **DataView** [Context]
  - 📝 **CheckBox**: checkBox1 [DP: {Spacing top: Outer medium}]
  - 📝 **CheckBox**: checkBox3 [DP: {Spacing top: Outer medium}]
    ↳ [acti] → **Cancel Changes**
    ↳ [acti] → **Microflow**: `Email_Connector.ACT_SelectedConfiguration_FetchEmailServerConfigAndShowConfigPage`
