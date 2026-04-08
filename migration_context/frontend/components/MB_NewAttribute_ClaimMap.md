# Microflow Detailed Specification: MB_NewAttribute_ClaimMap

### 📥 Inputs (Parameters)
- **$ClaimMap** (Type: SAML20.ClaimMap)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **ClaimMap_SSOConfiguration** via Association from **$ClaimMap** (Result: **$SSOConfiguration**)**
2. **Create **SAML20.Attribute** (Result: **$NewAttribute**)
      - Set **Attribute_IdPMetadata** = `$SSOConfiguration/SAML20.SSOConfiguration_IdPMetadata`
      - Set **ManuallyCreated** = `true`**
3. **Update **$ClaimMap**
      - Set **ClaimMap_Attribute** = `$NewAttribute`**
4. **Maps to Page: **SAML20.Attribute_NewEdit****
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.