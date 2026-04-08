# Microflow Detailed Specification: DS_RetrieveAvailableSAMLAuthnContexts

### 📥 Inputs (Parameters)
- **$SSOConfiguration** (Type: SAML20.SSOConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **SAML20.SAMLAuthnContext** Filter: `[not(SAML20.ConfiguredSAMLAuthnContext_SAMLAuthnContext/SAML20.ConfiguredSAMLAuthnContext/SAML20.ConfiguredSAMLAuthnContext_SSOConfiguration = $SSOConfiguration)]` (Result: **$AvailableSAMLAuthnContextList**)**
2. 🏁 **END:** Return `$AvailableSAMLAuthnContextList`

**Final Result:** This process concludes by returning a [List] value.