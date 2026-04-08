# Page: SSOConfiguration_Edit

**Allowed Roles:** SAML20.Administrator

**Layout:** `Atlas_Core.Atlas_Default`

## Widget Tree

- 📦 **DataView** [Context]
  - ⚡ **Button**: (Re-)Start Wizard [Style: Default]
    ↳ [acti] → **Microflow**: `SAML20.ResetConfiguration`
  - ⚡ **Button**: Download SP Metadata [Style: Default]
    ↳ [acti] → **Microflow**: `SAML20.SSOConfiguration_ExportMetadata`
  - 📝 **CheckBox**: checkBox1
  - 📑 **TabContainer**
    - 📑 **Tab**: "Mapping"
      - 📑 **TabContainer**
        - 📑 **Tab**: "IdP Attributes"
        - 📑 **Tab**: "Application Attributes"
    - 📑 **Tab**: "Provisioning"
    - 📑 **Tab**: "Request Authn Context"
    - 📑 **Tab**: "Identity Provider Metadata"
      - ⚡ **Button**: Refresh metadata [Style: Default]
        ↳ [acti] → **Microflow**: `SAML20.IdPMetadata_Refresh`
    - 📑 **Tab**: "Attribute Consuming Service"
    - 📑 **Tab**: "Encryption Settings"
  - ⚡ **Button**: Save [Style: Primary]
    ↳ [acti] → **Microflow**: `SAML20.MB_SSOConfiguration_Save`
    ↳ [acti] → **Cancel Changes**
