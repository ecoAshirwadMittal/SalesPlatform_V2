# Snippet: UserProvisioning_IdPAttributes

## Widget Tree

- ⚡ **Button**: radioButtons1
  ↳ [Change] → **Microflow**: `SAML20.OC_RefreshAttributes`
- 🔤 **Text**: "Select attribute of which the value should be used while executing a user look-up. If custom, use the formal name (URN) of the attribute." [Class: `control-label`]
- 📝 **ReferenceSelector**: referenceSelector1
- ⚡ **Button**: Search [Style: Default]
  ↳ [acti] → **Microflow**: `SAML20.MB_ShowAttributesForConfig`
- 🔤 **Text**: "Not applicable" 👁️ (If IdentifyingAssertionType is IdP_Provided/InCommon_Federation/Use_Name_ID/Custom/(empty))
