# Snippet: AuthenticationConfig

## Widget Tree

- 📝 **ReferenceSelector**: referenceSelector
- ⚡ **Button**: Search [Style: Default]
  ↳ [acti] → **Microflow**: `SAML20.MB_ShowEntityDescriptors`
- ⚡ **Button**: Details [Style: Default]
  ↳ [acti] → **Microflow**: `SAML20.MB_ShowSelectedEntityDescriptor`
- 🔤 **Text**: "Allow Idp Intiated Authentication" [Class: `control-label`]
- 📝 **CheckBox**: checkBox1
- 📝 **CheckBox**: checkBox7 [Style: `    display: inline-block;`]
- 📝 **CheckBox**: checkBox8 [Style: `    display: inline-block;`]
- 📝 **CheckBox**: checkBox6 [Style: `    display: inline-block;`]
- 📝 **ReferenceSelector**: referenceSelector9
- 📝 **ReferenceSelector**: referenceSelector8
- 🔤 **Text**: "Disable NameID policy" [Class: `control-label`]
- 📝 **CheckBox**: checkBox3
- 🔤 **Text**: "Preferred name id (optional)" [Class: `control-label`]
- 📝 **ReferenceSelector**: referenceSelector3
- 📝 **DropDown**: dropDown2
- 📦 **DataGrid** [Context]
  - ⚡ **Button**: Add [Style: Default]
    ↳ [acti] → **Microflow**: `SAML20.MB_OpenSAMLAuthnContext_Select`
  - ⚡ **Button**: Edit [Style: Default]
    ↳ [acti] → **Page**: `SAML20.ConfiguredSAMLAuthnContext_NewEdit`
  - ⚡ **Button**: Remove [Style: Default]
    ↳ [acti] → **Delete**
  - 📊 **Column**: Priority [Width: 15]
  - 📊 **Column**: Default Priority [Width: 15]
  - 📊 **Column**: Description [Width: 35]
  - 📊 **Column**: URI [Width: 35]
- 📝 **ReferenceSetSelector**: referenceSetSelector1
  - ⚡ **Button**: Add [Style: Default]
  - ⚡ **Button**: Remove [Style: Default]
  - ⚡ **Button**: Migrate [Style: Danger]
    ↳ [acti] → **Microflow**: `SAML20.MB_MigrateToConfiguredSAMLAuthnContexts`
  - 📊 **Column**: Description [Width: 30]
  - 📊 **Column**: Value [Width: 70]
