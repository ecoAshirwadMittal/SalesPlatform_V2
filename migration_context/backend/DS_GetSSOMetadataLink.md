# Microflow Detailed Specification: DS_GetSSOMetadataLink

### 📥 Inputs (Parameters)
- **$SSOConfiguration** (Type: SAML20.SSOConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **SAML20.DS_GetSPMetadata** (Result: **$SPMetadata**)**
2. **Create **SAML20.SSOMetadataLink** (Result: **$NewSSOMetaDataLink**)
      - Set **ApplicationURL** = `$SPMetadata/ApplicationURL`
      - Set **MetadataLink** = `'/SSO/metadata/'+$SSOConfiguration/Alias`
      - Set **Alias** = `$SSOConfiguration/Alias`**
3. 🏁 **END:** Return `$NewSSOMetaDataLink`

**Final Result:** This process concludes by returning a [Object] value.