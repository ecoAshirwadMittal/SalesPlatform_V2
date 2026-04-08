# Page: BaseName_Select

**Allowed Roles:** Email_Connector.EmailConnectorAdmin

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 📦 **DataGrid** [MF: Email_Connector.DS_LDAPBaseDNList]
    - ⚡ **Button**: Select [Style: Primary]
      ↳ [acti] → **Microflow**: `Email_Connector.ACT_LDAPConfiguration_ChangeBaseName`
    - 📊 **Column**: Base Name [Width: 100]
    ↳ [acti] → **Close Page**
