# Microflow Detailed Specification: MB_NewAttribute

### 📥 Inputs (Parameters)
- **$SSOConfiguration** (Type: SAML20.SSOConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **SAML20.Attribute** (Result: **$NewAttribute**)
      - Set **Attribute_IdPMetadata** = `$SSOConfiguration/SAML20.SSOConfiguration_IdPMetadata`
      - Set **ManuallyCreated** = `true`**
2. **Maps to Page: **SAML20.Attribute_NewEdit****
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.