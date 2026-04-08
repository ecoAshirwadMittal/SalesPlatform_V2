# Snippet: Attribute_Consuming_Service

## Widget Tree

- 📦 **DataView** [MF: SAML20.DS_GetSPAttributeConsumingService_InitialLogin] [DP: {Spacing bottom: Outer large}]
  - 📝 **CheckBox**: checkBox1
  - 📦 **DataGrid** [Association: undefined]
    - ⚡ **Button**: New [Style: Default]
      ↳ [acti] → **Page**: `SAML20.SPRequestedAttribute_NewEdit`
      ↳ [acti] → **Microflow**: `SAML20.ACT_SPRequestedAttribute_Delete`
    - 📊 **Column**: Name of the Attribute [Width: 25]
    - 📊 **Column**: Attribute Value [Width: 25]
    - 📊 **Column**: Mandatory [Width: 50]
- 📦 **DataView** [MF: SAML20.DS_GetSPAttributeConsumingService_InSessionLogin]
  - 📝 **CheckBox**: checkBox2 [DP: {Spacing top: Outer large}]
  - 📦 **DataGrid** [Association: undefined]
    - ⚡ **Button**: New [Style: Default]
      ↳ [acti] → **Page**: `SAML20.SPRequestedAttribute_NewEdit`
      ↳ [acti] → **Microflow**: `SAML20.ACT_SPRequestedAttribute_Delete`
    - 📊 **Column**: Name of the Attribute [Width: 25]
    - 📊 **Column**: Attribute Value [Width: 25]
    - 📊 **Column**: Mandatory [Width: 50]
