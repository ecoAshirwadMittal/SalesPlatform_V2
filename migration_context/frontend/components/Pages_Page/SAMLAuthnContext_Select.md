# Page: SAMLAuthnContext_Select

**Allowed Roles:** SAML20.Administrator

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 📦 **DataGrid** [MF: SAML20.DS_RetrieveAvailableSAMLAuthnContexts]
    - ⚡ **Button**: Select all [Style: Default]
    - ⚡ **Button**: Deselect all [Style: Default]
    - ⚡ **Button**: Select [Style: Default]
      ↳ [acti] → **Microflow**: `SAML20.MB_Select_SAMLAuthnContexts`
    - ⚡ **Button**: New [Style: Default]
    - ⚡ **Button**: Edit [Style: Default]
      ↳ [acti] → **Page**: `SAML20.SAMLAuthnContext_NewEdit`
    - 📊 **Column**: Default Priority [Width: 15]
    - 📊 **Column**: Description [Width: 25]
    - 📊 **Column**: URI [Width: 40]
    - 📊 **Column**: Default [Width: 20]
