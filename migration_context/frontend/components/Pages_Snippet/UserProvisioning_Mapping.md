# Snippet: UserProvisioning_Mapping

## Widget Tree

- ⚡ **Button**: radioButtons4
- 🔤 **Text**: "Userrole to associate to a newly created user" [Class: `control-label`]
- 📝 **ReferenceSelector**: referenceSelector4
- 📝 **CheckBox**: checkBox5 [Style: `display: inline-block; margin-right: 3em;`]
- 📝 **ReferenceSelector**: referenceSelector5
- 📝 **CheckBox**: checkBox7 [Style: `display: inline-block; margin-right: 3em;`]
- 📝 **ReferenceSelector**: referenceSelector7
- 🔤 **Text**: "Specify which Mendix object attributes have to be overwritten by IdP assertion data."
- 📦 **DataGrid** [Context]
  - ⚡ **Button**: New [Style: Primary]
  - ⚡ **Button**: Edit [Style: Default]
    ↳ [acti] → **Page**: `SAML20.ClaimMap_NewEdit`
  - ⚡ **Button**: Delete [Style: Danger]
    ↳ [acti] → **Delete**
  - 📊 **Column**: Claim [Width: 50]
  - 📊 **Column**: Mx User attribute [Width: 50]
