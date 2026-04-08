# Page: SSOConfig_Step10

**Layout:** `MxModelReflection.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 📦 **DataView** [MF: SAML20.DS_GetSSOMetadataLink]
      ↳ [acti] → **OpenLink**
    - ⚡ **Button**: Download SP Metadata [Style: Default]
      ↳ [acti] → **Microflow**: `SAML20.SSOConfiguration_ExportMetadata`
  - ⚡ **Button**: Previous [Style: Default]
    ↳ [acti] → **Microflow**: `SAML20.SSOConfig_ToStepBack`
  - ⚡ **Button**: Save [Style: Primary]
    ↳ [acti] → **Microflow**: `SAML20.SSOConfig_Finished`
    ↳ [acti] → **Cancel Changes**
