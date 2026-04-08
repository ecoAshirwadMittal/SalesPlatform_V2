# Page: AttributesForClaimMap

**Layout:** `Atlas_Core.PopupLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 📦 **DataGrid** [MF: SAML20.MF_AvailableAttributesForSSOConfigByClaimMap]
    - ⚡ **Button**: Select [Style: Default]
      ↳ [acti] → **Microflow**: `SAML20.MB_SelectAttribute_ClaimMap`
    - ⚡ **Button**: New [Style: Default]
      ↳ [acti] → **Microflow**: `SAML20.MB_NewAttribute_ClaimMap`
    - ⚡ **Button**: Edit [Style: Default]
      ↳ [acti] → **Page**: `SAML20.Attribute_NewEdit`
    - ⚡ **Button**: Delete [Style: Default]
      ↳ [acti] → **Microflow**: `SAML20.MB_DeleteAttribute`
    - 📊 **Column**: Name [Width: 24]
    - 📊 **Column**: Friendly name [Width: 29]
    - 📊 **Column**: Name Format [Width: 25]
    - 📊 **Column**: is required [Width: 12]
    - 📊 **Column**: Manually created [Width: 10]
