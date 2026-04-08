# Microflow Detailed Specification: MB_MigrateToConfiguredSAMLAuthnContexts

### 📥 Inputs (Parameters)
- **$SSOConfiguration** (Type: SAML20.SSOConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SSOConfiguration_SAMLAuthnContext** via Association from **$SSOConfiguration** (Result: **$SAMLAuthnContextList**)**
2. **Call Microflow **SAML20.MF_CreateConfiguredSAMLAuthnContext_FromList** (Result: **$Variable**)**
3. **Update **$SSOConfiguration** (and Save to DB)
      - Set **MigratedToPrioritizedSAMLAuthnContexts** = `true`
      - Set **SSOConfiguration_SAMLAuthnContext** = `empty`**
4. **Show Message (Information): `The authentication context classes were successfully migrated. Please verify that the assigned priorities are suitable for your configuration.`**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.