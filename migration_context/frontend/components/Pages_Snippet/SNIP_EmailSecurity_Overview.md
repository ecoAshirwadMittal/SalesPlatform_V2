# Snippet: SNIP_EmailSecurity_Overview

## Widget Tree

- 📦 **DataView** [MF: Email_Connector.DS_GetPKCS12Certificate]
  - 📝 **CheckBox**: checkBox1 [DP: {Spacing top: Outer small}]
    ↳ [Change] → **Microflow**: `Email_Connector.OCH_Pk12Certificate_DeleteCertificate`
- 📦 **DataView** [MF: Email_Connector.DS_GetLDAPConfiguration]
  - 📝 **CheckBox**: checkBox2 [DP: {Spacing top: Outer small}]
    ↳ [Change] → **Microflow**: `Email_Connector.OCH_LDAPConfiguration_DeleteConfiguration`
  - 📝 **DropDown**: dropDown1
  - ⚡ **Button**: radioButtons1 👁️ (If: `$currentObject/AuthType!=empty and $currentObject/LDAPHost!=empty and $currentObject/LDAPPort!=empty`)
    ↳ [acti] → **Page**: `Email_Connector.BaseName_Select`
