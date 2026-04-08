# Snippet: SAMLConfiguration_Overview

## Widget Tree

- 📑 **TabContainer**
  - 📑 **Tab**: "IdP Configuration"
    - 📦 **DataGrid** [Context]
      - ⚡ **Button**: New [Style: Primary]
        ↳ [acti] → **Microflow**: `SAML20.MB_NewConfiguration`
      - ⚡ **Button**: Edit [Style: Primary]
        ↳ [acti] → **Page**: `SAML20.SSOConfiguration_Edit`
      - ⚡ **Button**: Delete [Style: Danger]
        ↳ [acti] → **Delete**
      - ⚡ **Button**: Toggle Active [Style: Default]
        ↳ [acti] → **Microflow**: `SAML20.MB_ToggleActive`
      - 📊 **Column**: Alias [Width: 33]
      - 📊 **Column**: IdP URL [Width: 57]
      - 📊 **Column**: Active [Width: 10]
  - 📑 **Tab**: "SP Configuration"
    - 📦 **DataView** [MF: SAML20.DS_GetSPMetadata]
      - ⚡ **Button**: radioButtons1
      - ⚡ **Button**: Refresh [Style: Default]
        ↳ [acti] → **Microflow**: `SAML20.RefreshApplicationRootURL`
      - 🔤 **Text**: "Uniquely identifies this application at the Identity Provider"
      - ⚡ **Button**: Save [Style: Primary]
        ↳ [acti] → **Microflow**: `SAML20.MB_SaveSPMetadata`
  - 📑 **Tab**: "Log"
    - 📦 **DataGrid** [Context]
      - ⚡ **Button**: View [Style: Primary]
        ↳ [acti] → **Page**: `SAML20.SSOLog_View`
      - ⚡ **Button**: Select all [Style: Default]
      - ⚡ **Button**: Deselect all [Style: Default]
      - ⚡ **Button**: Delete [Style: Danger]
        ↳ [acti] → **Delete**
      - 📊 **Column**: column4 [Width: 3]
      - 📊 **Column**: Date [Width: 14]
      - 📊 **Column**: Message [Width: 83]
  - 📑 **Tab**: "SAML Requests"
    - 📦 **DataGrid** [Context]
      - ⚡ **Button**: View [Style: Primary]
        ↳ [acti] → **Page**: `SAML20.SAMLRequest_Details`
      - ⚡ **Button**: Select all [Style: Default]
      - ⚡ **Button**: Deselect all [Style: Default]
      - ⚡ **Button**: Delete [Style: Danger]
        ↳ [acti] → **Delete**
      - ⚡ **Button**: Export to CSV [Style: Default]
      - 📊 **Column**: IdP [Width: 16]
      - 📊 **Column**: Request [Width: 7]
      - 📊 **Column**: column9 [Width: 14]
      - 📊 **Column**: Response [Width: 7]
      - 📊 **Column**: column11 [Width: 17]
      - 📊 **Column**: Request Id [Width: 22]
      - 📊 **Column**: Principal [Width: 17]
