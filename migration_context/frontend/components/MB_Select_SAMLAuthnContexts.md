# Microflow Detailed Specification: MB_Select_SAMLAuthnContexts

### 📥 Inputs (Parameters)
- **$SSOConfiguration** (Type: SAML20.SSOConfiguration)
- **$SAMLAuthnContextList** (Type: SAML20.SAMLAuthnContext)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **SAML20.MF_CreateConfiguredSAMLAuthnContext_FromList** (Result: **$Variable**)**
2. **Close current page/popup**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.